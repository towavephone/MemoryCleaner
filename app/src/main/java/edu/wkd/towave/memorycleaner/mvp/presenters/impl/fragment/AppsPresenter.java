package edu.wkd.towave.memorycleaner.mvp.presenters.impl.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
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
import edu.wkd.towave.memorycleaner.adapter.CacheListAdapter;
import edu.wkd.towave.memorycleaner.adapter.base.BaseRecyclerViewAdapter;
import edu.wkd.towave.memorycleaner.injector.ContextLifeCycle;
import edu.wkd.towave.memorycleaner.model.AppInfo;
import edu.wkd.towave.memorycleaner.mvp.presenters.Presenter;
import edu.wkd.towave.memorycleaner.mvp.views.View;
import edu.wkd.towave.memorycleaner.mvp.views.impl.fragment.AppsView;
import edu.wkd.towave.memorycleaner.tools.L;
import edu.wkd.towave.memorycleaner.tools.TextFormater;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import javax.inject.Inject;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * Created by Administrator on 2016/5/5.
 */
public class AppsPresenter
        implements Presenter, SwipeRefreshLayout.OnRefreshListener {

    private AppsView mAppsView;
    private final Context mContext;
    private int position = 0; // 0:应用软件，2 系统软件
    AppsListAdapter recyclerAdapter;
    List<AppInfo> userAppInfos = new ArrayList<>();

    List<AppInfo> systemAppInfos = new ArrayList<>();

    private Method mGetPackageSizeInfoMethod;

    private TaskScanApps mTaskScanApps;


    @Inject
    public AppsPresenter(@ContextLifeCycle("Activity") Context context) {
        this.mContext = context;
    }


    public void getPosition(int position) {
        this.position = position;
    }


    @Override public void onCreate(Bundle savedInstanceState) {
        //position = mContext.getArguments().getInt(ARG_POSITION);
        try {
            mGetPackageSizeInfoMethod = mContext.getPackageManager()
                                                .getClass()
                                                .getMethod("getPackageSizeInfo",
                                                        String.class,
                                                        IPackageStatsObserver.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        mTaskScanApps = new TaskScanApps();
        //mAppsView.initViews(mAppsListAdapter,mContext,);
    }


    public void onActivityCreated(int position) {
        this.position = position;
        initView();
        loadData();
    }
    //public boolean onOptionsItemSelected(int id) {
    //    switch (id) {
    //        //case R.id.setting:
    //        //    startSettingActivity();
    //        //    return true;
    //        case R.id.refresh:
    //            if (mAppsView.isRefreshing()) {
    //                return true;
    //            }
    //            mAppsView.startRefresh();
    //            onRefresh();
    //            return true;
    //        //case R.id.about:
    //        //    startAboutActivity();
    //        //    return true;
    //    }
    //    return false;
    //}


    public void initView() {
        if (position == 0) {
            recyclerAdapter = new AppsListAdapter(userAppInfos, mContext);
        }
        else {
            recyclerAdapter = new AppsListAdapter(systemAppInfos, mContext);
        }

        recyclerAdapter.setOnInViewClickListener(R.id.card_item_root,
                new BaseRecyclerViewAdapter.onInternalClickListenerImpl<AppInfo>() {
                    @Override
                    public void OnClickListener(android.view.View parentV, android.view.View v, Integer position, AppInfo values) {
                        super.OnClickListener(parentV, v, position, values);
                        String[] memory = TextFormater.dataSizeFormat(
                                values.getPkgSize()).split(" ");
                        RelativeLayout relativeLayout
                                = mAppsView.setDialogValues(memory);
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                mContext).setTitle(values.getAppName())
                                         .setIcon(values.getAppIcon())
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
                                                                     values.getPackname()));
                                                     mContext.startActivity(
                                                             intent);
                                                 })
                                         .setView(relativeLayout);
                        builder.create().show();
                    }
                });
        recyclerAdapter.setFirstOnly(false);
        recyclerAdapter.setDuration(300);

        mAppsView.initViews(recyclerAdapter, mContext);
    }


    @Override public void onResume() {
        //    loadData();
    }


    @Override public void onStart() {

    }


    @Override public void onRefresh() {
        loadData();
    }


    public class TaskScanApps extends AsyncTask<Void, Object, List<AppInfo>> {

        private int mAppCount = 0;

        private long allSize = 0;


        @Override protected List<AppInfo> doInBackground(Void... params) {
            PackageManager pm = mContext.getPackageManager();
            List<PackageInfo> packInfos = pm.getInstalledPackages(0);
            publishProgress(0, packInfos.size(), 0, "开始扫描");
            List<AppInfo> appinfos = new ArrayList<>();
            allSize = 0;
            final CountDownLatch countDownLatch = new CountDownLatch(
                    packInfos.size());
            for (PackageInfo packInfo : packInfos) {
                final AppInfo appInfo = new AppInfo();
                Drawable appIcon = packInfo.applicationInfo.loadIcon(pm);
                appInfo.setAppIcon(appIcon);

                int flags = packInfo.applicationInfo.flags;

                int uid = packInfo.applicationInfo.uid;

                appInfo.setUid(uid);

                if ((flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                    appInfo.setUserApp(false);//系统应用
                }
                else {
                    appInfo.setUserApp(true);//用户应用
                }
                if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0) {
                    appInfo.setInRom(false);
                }
                else {
                    appInfo.setInRom(true);
                }
                String appName = packInfo.applicationInfo.loadLabel(pm)
                                                         .toString();
                appInfo.setAppName(appName);
                String packname = packInfo.packageName;
                appInfo.setPackname(packname);
                String version = packInfo.versionName;
                appInfo.setVersion(version);
                try {
                    mGetPackageSizeInfoMethod.invoke(
                            mContext.getPackageManager(),
                            new Object[] { packname,
                                    new IPackageStatsObserver.Stub() {
                                        @Override
                                        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
                                                throws RemoteException {
                                            synchronized (appInfo) {
                                                appInfo.setPkgSize(
                                                        pStats.cacheSize +
                                                                pStats.codeSize +
                                                                pStats.dataSize);
                                                allSize += appInfo.getPkgSize();
                                                publishProgress(++mAppCount,
                                                        packInfos.size(),
                                                        allSize,
                                                        appInfo.getAppName());
                                            }
                                            synchronized (countDownLatch) {
                                                countDownLatch.countDown();
                                            }
                                        }
                                    } });
                } catch (Exception e) {
                }
                if (isCancelled()) {
                    return null;
                }
                appinfos.add(appInfo);
            }
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return appinfos;
        }


        @Override protected void onProgressUpdate(Object... values) {
            try {
                mAppsView.onProgressUpdate(Integer.parseInt(values[0] + ""),
                        Integer.parseInt(values[1] + ""),
                        Long.parseLong(values[2] + ""), values[3] + "");
                //mProgressBarText.setText(
                //        getString(R.string.scanning_m_of_n, values[0],
                //                values[1]));
            } catch (Exception e) {

            }
        }


        @Override protected void onPreExecute() {
            try {
                mAppsView.enableSwipeRefreshLayout(false);
                mAppsView.startRefresh();
                mAppsView.onPreExecute();
                //showProgressBar(true);
                //mProgressBarText.setText(R.string.scanning);
            } catch (Exception e) {

            }
            //    loading.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }


        @Override protected void onPostExecute(List<AppInfo> result) {

            super.onPostExecute(result);

            try {
                long userAppSize = 0;
                long systemAppSize = 0;
                //showProgressBar(false);
                //userAppInfos = new ArrayList<>();
                //systemAppInfos = new ArrayList<>();
                userAppInfos.clear();
                systemAppInfos.clear();
                for (AppInfo a : result) {
                    if (a.isUserApp()) {
                        userAppSize += a.getPkgSize();
                        userAppInfos.add(a);
                    }
                    else {
                        systemAppSize += a.getPkgSize();
                        systemAppInfos.add(a);
                    }
                }
                recyclerAdapter.notifyDataSetChanged();
                if (position == 0) {
                    mAppsView.onPostExecute(recyclerAdapter, userAppSize);
                }
                else {
                    mAppsView.onPostExecute(recyclerAdapter, systemAppSize);
                }
                mAppsView.stopRefresh();
                mAppsView.enableSwipeRefreshLayout(true);
            } catch (Exception e) {

            }
        }
    }


    @Override public void onPause() {

    }


    @Override public void onStop() {

    }


    @Override public void onDestroy() {
        mTaskScanApps.cancel(true);
    }


    @Override public void attachView(View v) {
        mAppsView = (AppsView) v;
    }


    public void loadData() {
        mTaskScanApps.execute();
    }
}
