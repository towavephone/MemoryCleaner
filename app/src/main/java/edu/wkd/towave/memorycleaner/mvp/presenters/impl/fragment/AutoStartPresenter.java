package edu.wkd.towave.memorycleaner.mvp.presenters.impl.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.widget.RelativeLayout;
import edu.wkd.towave.memorycleaner.R;
import edu.wkd.towave.memorycleaner.adapter.AppsListAdapter;
import edu.wkd.towave.memorycleaner.adapter.AutoStartAdapter;
import edu.wkd.towave.memorycleaner.adapter.base.BaseRecyclerViewAdapter;
import edu.wkd.towave.memorycleaner.injector.ContextLifeCycle;
import edu.wkd.towave.memorycleaner.model.AppInfo;
import edu.wkd.towave.memorycleaner.model.AppProcessInfo;
import edu.wkd.towave.memorycleaner.model.AutoStartInfo;
import edu.wkd.towave.memorycleaner.mvp.presenters.Presenter;
import edu.wkd.towave.memorycleaner.mvp.views.View;
import edu.wkd.towave.memorycleaner.mvp.views.impl.fragment.AppsView;
import edu.wkd.towave.memorycleaner.mvp.views.impl.fragment.AutoStartView;
import edu.wkd.towave.memorycleaner.tools.ObservableUtils;
import edu.wkd.towave.memorycleaner.tools.RootUtil;
import edu.wkd.towave.memorycleaner.tools.TextFormater;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import javax.inject.Inject;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/5/5.
 */
public class AutoStartPresenter
        implements Presenter, SwipeRefreshLayout.OnRefreshListener {

    private AutoStartView mAutoStartView;
    private final Context mContext;
    private int position = 0; // 0:应用软件，2 系统软件
    AutoStartAdapter recyclerAdapter;

    ArrayList<AutoStartInfo> isSystemAuto = new ArrayList<>();
    ArrayList<AutoStartInfo> noSystemAuto = new ArrayList<>();

    ObservableUtils mObservableUtils;


    @Inject
    public AutoStartPresenter(@ContextLifeCycle("Activity")
                              Context context, ObservableUtils observableUtils) {
        this.mContext = context;
        this.mObservableUtils = observableUtils;
    }


    @Override public void onCreate(Bundle savedInstanceState) {
        //position = mContext.getArguments().getInt(ARG_POSITION);

        //mTaskScanApps = new TaskScanApps();
        //mAppsView.initViews(mAppsListAdapter,mContext,);
    }


    public void onActivityCreated(int position) {
        this.position = position;
        initView();
        loadData();
    }


    public void disableApps() {
        boolean flag = true;
        for (AutoStartInfo auto : noSystemAuto) {
            if (auto.isEnable()) {
                flag = false;
                break;
            }
        }
        if (flag) {
            mAutoStartView.showSnackbar("没有自启应用需要优化");
            return;
        }
        mObservableUtils.disableApps(mContext, noSystemAuto)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe((v) -> {
                            if (!v) {
                                mAutoStartView.showSnackbar(
                                        "该功能需要获取系统root权限，请允许获取root权限");
                                return;
                            }
                            int count = 0;
                            for (AutoStartInfo auto : noSystemAuto) {
                                if (auto.isEnable()) {
                                    auto.setEnable(false);
                                    count++;
                                }
                            }
                            recyclerAdapter.notifyDataSetChanged();
                            mAutoStartView.showSnackbar(count + "款应用已全部禁止");
                        }, (e) -> {
                            e.printStackTrace();
                        });
    }


    public void initView() {
        if (position == 0) {
            recyclerAdapter = new AutoStartAdapter(noSystemAuto, mContext);
        }
        else {
            recyclerAdapter = new AutoStartAdapter(isSystemAuto, mContext);
        }

        recyclerAdapter.setOnInViewClickListener(R.id.card_item_root,
                new BaseRecyclerViewAdapter.onInternalClickListenerImpl<AutoStartInfo>() {
                    @Override
                    public void OnClickListener(android.view.View parentV, android.view.View v, Integer position, AutoStartInfo values) {
                        super.OnClickListener(parentV, v, position, values);

                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                mContext).setTitle(values.getLabel())
                                         .setIcon(values.getIcon())
                                         .setNegativeButton("取消",
                                                 (dialogInterface, i) -> {
                                                     dialogInterface.dismiss();
                                                 })
                                         .setPositiveButton("添加至忽略列表",
                                                 (dialogInterface, i) -> {

                                                 })
                                         .setNeutralButton("详情",
                                                 (dialogInterface, i) -> {
                                                     Intent intent
                                                             = new Intent();
                                                     intent.setFlags(
                                                             Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                     intent.setAction(
                                                             android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                     intent.setData(Uri.parse(
                                                             "package:" +
                                                                     values.getPackageName()));
                                                     mContext.startActivity(
                                                             intent);
                                                 })
                                         .setView(null);
                        builder.create().show();
                    }
                });

        recyclerAdapter.setOnInViewClickListener(R.id.is_clean,
                new BaseRecyclerViewAdapter.onInternalClickListenerImpl<AutoStartInfo>() {
                    @Override
                    public void OnClickListener(android.view.View parentV, android.view.View v, Integer position, AutoStartInfo values) {
                        super.OnClickListener(parentV, v, position, values);
                        enableApp(values);
                    }
                });
        recyclerAdapter.setFirstOnly(false);
        recyclerAdapter.setDuration(300);

        mAutoStartView.initViews(recyclerAdapter, mContext);
    }


    public void enableApp(AutoStartInfo autoStartInfo) {
        mObservableUtils.enableApp(mContext, autoStartInfo)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe((v) -> {
                            if (!v) {
                                String msg = !autoStartInfo.isEnable()
                                             ? "开启失败"
                                             : "禁止失败";
                                mAutoStartView.showSnackbar(
                                        autoStartInfo.getLabel() + msg);
                                return;
                            }

                            autoStartInfo.setEnable(
                                    autoStartInfo.isEnable() ? false : true);
                            recyclerAdapter.update(autoStartInfo);
                            String msg = autoStartInfo.isEnable()
                                         ? "已开启"
                                         : "已禁止";
                            mAutoStartView.showSnackbar(
                                    autoStartInfo.getLabel() + msg);
                        }, (e) -> {
                            e.printStackTrace();
                        });
    }


    @Override public void onResume() {
        //    loadData();
    }


    @Override public void onStart() {

    }


    @Override public void onRefresh() {
        loadData();
    }


    public class TaskScanApps
            extends AsyncTask<Void, Object, List<AutoStartInfo>> {

        private int mAppCount = 0;


        @Override protected List<AutoStartInfo> doInBackground(Void... params) {
            PackageManager pm = mContext.getPackageManager();
            Intent intent = new Intent(Intent.ACTION_BOOT_COMPLETED);
            List<ResolveInfo> resolveInfoList = pm.queryBroadcastReceivers(
                    intent, PackageManager.GET_DISABLED_COMPONENTS);
            List<AutoStartInfo> appList = new ArrayList<>();
            String appName = null;
            String packageReceiver = null;
            Drawable icon = null;
            boolean isSystem = false;
            boolean isenable = true;
            if (resolveInfoList.size() > 0) {
                publishProgress(0, resolveInfoList.size(), 0, "开始扫描");
                appName = resolveInfoList.get(0).loadLabel(pm).toString();
                packageReceiver =
                        resolveInfoList.get(0).activityInfo.packageName +
                                "/" + resolveInfoList.get(0).activityInfo.name;
                icon = resolveInfoList.get(0).loadIcon(pm);
                ComponentName mComponentName1 = new ComponentName(
                        resolveInfoList.get(0).activityInfo.packageName,
                        resolveInfoList.get(0).activityInfo.name);

                if (pm.getComponentEnabledSetting(mComponentName1) == 2) {
                    isenable = false;
                }
                else {
                    isenable = true;
                }
                if ((resolveInfoList.get(0).activityInfo.applicationInfo.flags &
                        ApplicationInfo.FLAG_SYSTEM) != 0) {
                    isSystem = true;
                }
                else {
                    isSystem = false;
                }
                for (int i = 1; i < resolveInfoList.size(); i++) {
                    AutoStartInfo mAutoStartInfo = new AutoStartInfo();
                    if (appName.equals(
                            resolveInfoList.get(i).loadLabel(pm).toString())) {
                        publishProgress(++mAppCount, resolveInfoList.size(),
                                appName);
                        packageReceiver = packageReceiver + ";" +
                                resolveInfoList.get(
                                        i).activityInfo.packageName +
                                "/" + resolveInfoList.get(i).activityInfo.name;
                    }
                    else {
                        mAutoStartInfo.setLabel(appName);
                        mAutoStartInfo.setSystem(isSystem);
                        mAutoStartInfo.setEnable(isenable);
                        mAutoStartInfo.setIcon(icon);
                        mAutoStartInfo.setPackageReceiver(packageReceiver);
                        appList.add(mAutoStartInfo);
                        appName = resolveInfoList.get(i)
                                                 .loadLabel(pm)
                                                 .toString();
                        publishProgress(++mAppCount, resolveInfoList.size(),
                                appName);
                        packageReceiver = resolveInfoList.get(
                                i).activityInfo.packageName +
                                "/" +
                                resolveInfoList.get(i).activityInfo.name;
                        icon = resolveInfoList.get(i).loadIcon(pm);
                        ComponentName mComponentName2 = new ComponentName(
                                resolveInfoList.get(i).activityInfo.packageName,
                                resolveInfoList.get(i).activityInfo.name);
                        if (pm.getComponentEnabledSetting(mComponentName2) ==
                                2) {
                            isenable = false;
                        }
                        else {
                            isenable = true;
                        }

                        if ((resolveInfoList.get(
                                i).activityInfo.applicationInfo.flags &
                                ApplicationInfo.FLAG_SYSTEM) != 0) {
                            isSystem = true;
                        }
                        else {
                            isSystem = false;
                        }
                    }
                }
                AutoStartInfo mAutoStartInfo = new AutoStartInfo();
                mAutoStartInfo.setLabel(appName);
                mAutoStartInfo.setSystem(isSystem);
                mAutoStartInfo.setEnable(isenable);
                mAutoStartInfo.setIcon(icon);
                mAutoStartInfo.setPackageReceiver(packageReceiver);
                publishProgress(++mAppCount, resolveInfoList.size(), appName);
                appList.add(mAutoStartInfo);
            }

            return appList;
        }


        @Override protected void onProgressUpdate(Object... values) {
            try {
                mAutoStartView.onProgressUpdate(
                        Integer.parseInt(values[0] + ""),
                        Integer.parseInt(values[1] + ""), values[3] + "");
            } catch (Exception e) {

            }
        }


        @Override protected void onPreExecute() {
            try {
                mAutoStartView.enableSwipeRefreshLayout(false);
                mAutoStartView.startRefresh();
                mAutoStartView.onPreExecute();
                if (position == 0) {
                    mAutoStartView.setFabVisible(false);
                }
            } catch (Exception e) {

            }
            //    loading.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }


        @Override protected void onPostExecute(List<AutoStartInfo> result) {

            super.onPostExecute(result);

            try {
                noSystemAuto.clear();
                isSystemAuto.clear();
                for (AutoStartInfo a : result) {
                    if (a.isSystem()) {
                        isSystemAuto.add(a);
                    }
                    else {
                        noSystemAuto.add(a);
                    }
                }
                if (position == 0) {
                    mAutoStartView.setFabVisible(true);
                }
                recyclerAdapter.notifyDataSetChanged();
                mAutoStartView.onPostExecute(recyclerAdapter);
                mAutoStartView.stopRefresh();
                mAutoStartView.enableSwipeRefreshLayout(true);
                //mTaskScanApps.cancel(true);
            } catch (Exception e) {

            }
        }
    }


    @Override public void onPause() {

    }


    @Override public void onStop() {

    }


    @Override public void onDestroy() {
        //if(mAutoStartView.isRefreshing()){
        //mTaskScanApps.cancel(true);
        //}
    }


    @Override public void attachView(View v) {
        mAutoStartView = (AutoStartView) v;
    }


    public void loadData() {
        //mTaskScanApps.cancel(true);
        new TaskScanApps().execute();
    }
}
