package edu.wkd.towave.memorycleaner.mvp.presenters.impl.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import edu.wkd.towave.memorycleaner.R;
import edu.wkd.towave.memorycleaner.adapter.ProcessListAdapter;
import edu.wkd.towave.memorycleaner.adapter.base.BaseRecyclerViewAdapter;
import edu.wkd.towave.memorycleaner.injector.ContextLifeCycle;
import edu.wkd.towave.memorycleaner.model.AppProcessInfo;
import edu.wkd.towave.memorycleaner.model.Ignore;
import edu.wkd.towave.memorycleaner.mvp.presenters.Presenter;
import edu.wkd.towave.memorycleaner.mvp.views.View;
import edu.wkd.towave.memorycleaner.mvp.views.impl.activity.MemoryCleanView;
import edu.wkd.towave.memorycleaner.service.CoreService;
import edu.wkd.towave.memorycleaner.tools.TextFormater;
import edu.wkd.towave.memorycleaner.ui.activity.IgnoreSetting;
import edu.wkd.towave.memorycleaner.ui.activity.MemoryClean;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import net.tsz.afinal.FinalDb;

/**
 * Created by Administrator on 2016/5/4.
 */
public class MemoryCleanPresenter implements Presenter,
        CoreService.OnProcessActionListener,
        SwipeRefreshLayout.OnRefreshListener {

    MemoryCleanView mMemoryClean;
    final Context mContext;
    List<AppProcessInfo> mAppProcessInfos = new ArrayList<>();
    ProcessListAdapter recyclerAdapter;
    boolean isdesc = true;
    FinalDb mFinalDb;


    @Inject
    public MemoryCleanPresenter(
            @ContextLifeCycle("Activity") Context context, FinalDb finalDb) {
        this.mContext = context;
        this.mFinalDb = finalDb;
    }


    private CoreService mCoreService;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mCoreService
                    = ((CoreService.ProcessServiceBinder) service).getService();
            mCoreService.setOnActionListener(MemoryCleanPresenter.this);
            mCoreService.scanRunProcess();
            //  updateStorageUsage();

        }


        @Override public void onServiceDisconnected(ComponentName name) {
            mCoreService.setOnActionListener(null);
            mCoreService = null;
        }
    };


    @Override public void onCreate(Bundle savedInstanceState) {
        initViews();
    }


    public void initViews() {
        recyclerAdapter = new ProcessListAdapter(mAppProcessInfos, mContext);
        recyclerAdapter.setOnInViewClickListener(R.id.card_item_root,
                new BaseRecyclerViewAdapter.onInternalClickListenerImpl<AppProcessInfo>() {
                    @Override
                    public void OnClickListener(android.view.View parentV, android.view.View v, Integer position, AppProcessInfo values) {
                        super.OnClickListener(parentV, v, position, values);
                        String[] memory = TextFormater.dataSizeFormat(
                                values.memory).split(" ");
                        RelativeLayout relativeLayout
                                = mMemoryClean.setDialogValues(memory);
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                mContext).setTitle(values.appName)
                                         .setIcon(values.icon)
                                         .setNegativeButton("取消",
                                                 (dialogInterface, i) -> {
                                                     dialogInterface.dismiss();
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
                                                                     values.packName));
                                                     mContext.startActivity(
                                                             intent);
                                                 })
                                         .setView(relativeLayout);
                        List<Ignore> ignores = mFinalDb.findAllByWhere(
                                Ignore.class,
                                "packName='" + values.packName + "'");
                        if (ignores.size() == 0) {
                            builder.setPositiveButton("添加至忽略列表",
                                    (dialogInterface, i) -> {
                                        Ignore ignore = new Ignore(
                                                values.packName);
                                        List<Ignore> mIgnore
                                                = mFinalDb.findAllByWhere(
                                                Ignore.class, "packName='" +
                                                        values.packName +
                                                        "'");
                                        if (mIgnore.size() == 0) {
                                            if (mFinalDb.saveBindId(ignore)) {
                                                recyclerAdapter.remove(values);
                                                updateMemoryCount();
                                                mMemoryClean.showSnackBar(
                                                        values.appName + "已添加");
                                            }
                                            else {
                                                mMemoryClean.showSnackBar(
                                                        values.appName +
                                                                "添加失败");
                                            }
                                        }
                                        else {
                                            mMemoryClean.showSnackBar(
                                                    values.appName + "已在白名单中");
                                        }
                                    });
                        }
                        builder.create().show();
                    }
                });
        recyclerAdapter.setOnInViewClickListener(R.id.is_clean,
                new BaseRecyclerViewAdapter.onInternalClickListenerImpl<AppProcessInfo>() {
                    @Override
                    public void OnClickListener(android.view.View parentV, android.view.View v, Integer position, AppProcessInfo values) {
                        super.OnClickListener(parentV, v, position, values);
                        if (values.checked) {
                            values.checked = false;
                        }
                        else {
                            values.checked = true;
                        }
                        updateMemoryCount();
                        recyclerAdapter.update(values);
                    }
                });
        recyclerAdapter.setFirstOnly(false);
        recyclerAdapter.setDuration(300);
        //0则不执行拖动或者滑动
        ItemTouchHelper.Callback mCallback = new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }


            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                mMemoryClean.showSnackBar("清理" + TextFormater.dataSizeFormat(
                        mAppProcessInfos.get(position).memory) + "内存");
                mCoreService.killBackgroundProcesses(
                        mAppProcessInfos.get(position).processName);
                recyclerAdapter.remove(position);
                updateMemoryCount();
            }


            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY,
                        actionState, isCurrentlyActive);
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    //滑动时改变Item的透明度
                    final float alpha = 1 - Math.abs(dX) /
                            (float) viewHolder.itemView.getWidth();
                    viewHolder.itemView.setAlpha(alpha);
                    viewHolder.itemView.setTranslationX(dX);
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(mCallback);
        mContext.bindService(new Intent(mContext, CoreService.class),
                mServiceConnection, Context.BIND_AUTO_CREATE);
        mMemoryClean.initViews(recyclerAdapter, mContext, itemTouchHelper);
    }


    void updateMemoryCount() {
        int check_count = 0;
        long memory_count = 0;
        for (AppProcessInfo appProcessInfo : mAppProcessInfos) {
            if (appProcessInfo.checked) {
                check_count++;
                memory_count += appProcessInfo.memory;
            }
        }
        mMemoryClean.updateTitle(mContext, memory_count);
        mMemoryClean.updateBadge(check_count);
    }


    @Override public void onResume() {

    }


    @Override public void onStart() {

    }


    @Override public void onPause() {

    }


    @Override public void onStop() {

    }


    public boolean onOptionsItemSelected(MenuItem item) {
        if (mMemoryClean.isRefreshing()) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.action_settings:
                mContext.startActivity(
                        new Intent(mContext, IgnoreSetting.class));
                return true;
            case R.id.refresh:
                mMemoryClean.startRefresh();
                onRefresh();
                return true;
            case R.id.allcheck:
                boolean flag = true;
                for (AppProcessInfo appProcessInfo : mAppProcessInfos) {
                    if (appProcessInfo.checked) {
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    for (AppProcessInfo appProcessInfo : mAppProcessInfos) {
                        appProcessInfo.checked = true;
                    }
                }
                else {
                    for (AppProcessInfo appProcessInfo : mAppProcessInfos) {
                        appProcessInfo.checked = false;
                    }
                }
                updateMemoryCount();
                recyclerAdapter.notifyDataSetChanged();
                break;
            case MemoryClean.BASE_ID + 1:
                if (item.isChecked()) return true;
                if (isdesc) {
                    Collections.sort(mAppProcessInfos,
                            (v2, v1) -> v1.appName.compareTo(v2.appName));
                }
                else {
                    Collections.sort(mAppProcessInfos,
                            (v1, v2) -> v1.appName.compareTo(v2.appName));
                }
                recyclerAdapter.notifyDataSetChanged();
                item.setChecked(true);
                break;
            case MemoryClean.BASE_ID + 2:
                if (item.isChecked()) return true;
                if (isdesc) {
                    Collections.sort(mAppProcessInfos,
                            (v2, v1) -> (int) (v1.memory - v2.memory));
                }
                else {
                    Collections.sort(mAppProcessInfos,
                            (v1, v2) -> (int) (v1.memory - v2.memory));
                }
                recyclerAdapter.notifyDataSetChanged();
                item.setChecked(true);
                break;
            case MemoryClean.BASE_ID + 3:
                if (item.isChecked()) return true;
                if (isdesc) {
                    Collections.sort(mAppProcessInfos, (v2, v1) -> {
                        if (v1.checked == v2.checked) {
                            return 0;
                        }
                        else if (v1.checked) {
                            return 1;
                        }
                        else {
                            return -1;
                        }
                    });
                }
                else {
                    Collections.sort(mAppProcessInfos, (v1, v2) -> {
                        if (v1.checked == v2.checked) {
                            return 0;
                        }
                        else if (v1.checked) {
                            return 1;
                        }
                        else {
                            return -1;
                        }
                    });
                }
                recyclerAdapter.notifyDataSetChanged();
                item.setChecked(true);
                break;
            case MemoryClean.BASE_ID + 4:
                isdesc = !item.isChecked();
                item.setChecked(isdesc);
        }
        return false;
    }


    @Override public void onDestroy() {
        mContext.unbindService(mServiceConnection);
        //RefWatcher refWatcher = App.getRefWatcher(mContext);
        //refWatcher.watch(this);
    }


    @Override public void attachView(View v) {
        mMemoryClean = (MemoryClean) v;
    }


    @Override public void onScanStarted(Context context) {
        mMemoryClean.enableSwipeRefreshLayout(false);
        mMemoryClean.onScanStarted(context);
        mMemoryClean.startRefresh();
        //mProgressBarText.setText(R.string.scanning);
        //showProgressBar(true);
    }


    @Override
    public void onScanProgressUpdated(Context context, int current, int max, long memory, String processName) {
        mMemoryClean.onScanProgressUpdated(context, current, max, memory,
                processName);
    }


    @Override
    public void onScanCompleted(Context context, List<AppProcessInfo> apps) {
        mAppProcessInfos.clear();

        //Allmemory = 0;
        for (AppProcessInfo appInfo : apps) {
            //if (!appInfo.isSystem) {
            mAppProcessInfos.add(appInfo);
            //Allmemory += appInfo.memory;
            //}
        }

        recyclerAdapter.notifyDataSetChanged();
        mMemoryClean.stopRefresh();
        mMemoryClean.onScanCompleted();
        mMemoryClean.enableSwipeRefreshLayout(true);
    }


    @Override public void onCleanStarted(Context context) {

    }


    public void cleanMemory() {
        long killAppmemory = 0;
        long count = 0;
        for (int i = mAppProcessInfos.size() - 1; i >= 0; i--) {
            long memory = mAppProcessInfos.get(i).memory;
            if (mAppProcessInfos.get(i).checked) {
                count++;
                killAppmemory += memory;
                mCoreService.killBackgroundProcesses(
                        mAppProcessInfos.get(i).processName);
                //mAppProcessInfos.remove(mAppProcessInfos.get(i));
                //mClearMemoryAdapter.notifyDataSetChanged();
                recyclerAdapter.remove(mAppProcessInfos.get(i));
            }
        }
        mMemoryClean.updateBadge(0);
        mMemoryClean.updateTitle(mContext, 0);
        mMemoryClean.showSnackBar(count > 0 ? "共清理" + count + "个进程,共占内存" +
                TextFormater.dataSizeFormat(killAppmemory) +
                "内存" : "未选中要清理的进程");
    }


    @Override public void onCleanCompleted(Context context, long cacheSize) {

    }


    @Override public void onRefresh() {
        mContext.unbindService(mServiceConnection);
        mContext.bindService(new Intent(mContext, CoreService.class),
                mServiceConnection, Context.BIND_AUTO_CREATE);
    }
}
