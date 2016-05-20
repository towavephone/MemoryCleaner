package edu.wkd.towave.memorycleaner.mvp.presenters.impl.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.DialogPreference;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import edu.wkd.towave.memorycleaner.App;
import edu.wkd.towave.memorycleaner.R;
import edu.wkd.towave.memorycleaner.adapter.ProcessListAdapter;
import edu.wkd.towave.memorycleaner.adapter.base.BaseRecyclerViewAdapter;
import edu.wkd.towave.memorycleaner.injector.ContextLifeCycle;
import edu.wkd.towave.memorycleaner.model.AppProcessInfo;
import edu.wkd.towave.memorycleaner.mvp.presenters.Presenter;
import edu.wkd.towave.memorycleaner.mvp.views.View;
import edu.wkd.towave.memorycleaner.mvp.views.impl.activity.MainView;
import edu.wkd.towave.memorycleaner.mvp.views.impl.activity.MemoryCleanView;
import edu.wkd.towave.memorycleaner.service.CoreService;
import edu.wkd.towave.memorycleaner.tools.AppUtils;
import edu.wkd.towave.memorycleaner.tools.L;
import edu.wkd.towave.memorycleaner.tools.PreferenceUtils;
import edu.wkd.towave.memorycleaner.tools.T;
import edu.wkd.towave.memorycleaner.tools.TextFormater;
import edu.wkd.towave.memorycleaner.ui.activity.MemoryClean;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

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


    @Inject
    public MemoryCleanPresenter(@ContextLifeCycle("Activity") Context context) {
        this.mContext = context;
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
                                                                     mCoreService
                                                                             .getApplicationInfo(
                                                                                     values.processName).packageName));
                                                     mContext.startActivity(
                                                             intent);
                                                 })
                                         .setView(relativeLayout);
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
                            recyclerAdapter.update(values);
                        }
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
                //recyclerAdapter.remove(position);
                T.showShort(mContext, "清理" + TextFormater.dataSizeFormat(
                        mAppProcessInfos.get(position).memory) + "内存");
                mCoreService.killBackgroundProcesses(
                        mAppProcessInfos.get(position).processName);
                //mAppProcessInfos.remove(mAppProcessInfos.get(i));
                //mClearMemoryAdapter.notifyDataSetChanged();
                recyclerAdapter.remove(position);
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


    @Override public void onResume() {

    }


    @Override public void onStart() {

    }


    @Override public void onPause() {

    }


    @Override public void onStop() {

    }


    public boolean onOptionsItemSelected(int id) {
        switch (id) {
            //case R.id.setting:
            //    startSettingActivity();
            //    return true;
            case R.id.refresh:
                if (mMemoryClean.isRefreshing()) {
                    return true;
                }
                mMemoryClean.startRefresh();
                onRefresh();
                return true;
            //case R.id.about:
            //    startAboutActivity();
            //    return true;
        }
        return false;
    }


    @Override public void onDestroy() {
        mContext.unbindService(mServiceConnection);
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
        for (int i = mAppProcessInfos.size() - 1; i >= 0; i--) {
            if (mAppProcessInfos.get(i).checked) {
                killAppmemory += mAppProcessInfos.get(i).memory;
                mCoreService.killBackgroundProcesses(
                        mAppProcessInfos.get(i).processName);
                //mAppProcessInfos.remove(mAppProcessInfos.get(i));
                //mClearMemoryAdapter.notifyDataSetChanged();
                recyclerAdapter.remove(mAppProcessInfos.get(i));
            }
        }
        T.showLong(mContext,
                "共清理" + TextFormater.dataSizeFormat(killAppmemory) + "内存");
    }


    @Override public void onCleanCompleted(Context context, long cacheSize) {

    }


    @Override public void onRefresh() {
        mContext.unbindService(mServiceConnection);
        mContext.bindService(new Intent(mContext, CoreService.class),
                mServiceConnection, Context.BIND_AUTO_CREATE);
    }
}
