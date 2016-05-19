package edu.wkd.towave.memorycleaner.mvp.presenters.impl.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.format.Formatter;
import edu.wkd.towave.memorycleaner.R;
import edu.wkd.towave.memorycleaner.adapter.CacheListAdapter;
import edu.wkd.towave.memorycleaner.adapter.base.BaseRecyclerViewAdapter;
import edu.wkd.towave.memorycleaner.injector.ContextLifeCycle;
import edu.wkd.towave.memorycleaner.model.AppProcessInfo;
import edu.wkd.towave.memorycleaner.model.CacheListItem;
import edu.wkd.towave.memorycleaner.mvp.presenters.Presenter;
import edu.wkd.towave.memorycleaner.mvp.views.View;
import edu.wkd.towave.memorycleaner.service.CleanerService;
import edu.wkd.towave.memorycleaner.tools.T;
import edu.wkd.towave.memorycleaner.tools.TextFormater;
import edu.wkd.towave.memorycleaner.ui.activity.RubbishClean;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

/**
 * Created by Administrator on 2016/5/4.
 */
public class RubbishCleanPresenter implements Presenter,
        CleanerService.OnActionListener,
        SwipeRefreshLayout.OnRefreshListener {

    RubbishClean mRubbishClean;
    protected static final int SCANING = 5;

    protected static final int SCAN_FINIFSH = 6;
    protected static final int PROCESS_MAX = 8;
    protected static final int PROCESS_PROCESS = 9;

    private static final int INITIAL_DELAY_MILLIS = 300;
    int ptotal = 0;
    int pprocess = 0;

    private CleanerService mCleanerService;

    private boolean mAlreadyScanned = false;
    private boolean mAlreadyCleaned = false;
    final Context mContext;
    List<CacheListItem> mCacheListItems = new ArrayList<>();
    CacheListAdapter recyclerAdapter;


    @Inject
    public RubbishCleanPresenter(
            @ContextLifeCycle("Activity") Context context) {
        this.mContext = context;
    }


    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mCleanerService
                    = ((CleanerService.CleanerServiceBinder) service).getService();
            mCleanerService.setOnActionListener(RubbishCleanPresenter.this);

            //  updateStorageUsage();

            if (!mCleanerService.isScanning() && !mAlreadyScanned) {
                mCleanerService.scanCache();
            }
        }


        @Override public void onServiceDisconnected(ComponentName name) {
            mCleanerService.setOnActionListener(null);
            mCleanerService = null;
        }
    };


    @Override public void onCreate(Bundle savedInstanceState) {
        initViews();
    }


    public void initViews() {

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
                        mCacheListItems.get(position).getCacheSize()) + "缓存");
                mCleanerService.cleanCache(
                        mCacheListItems.get(position).getPackageName());
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

        recyclerAdapter = new CacheListAdapter(mCacheListItems, mContext);
        recyclerAdapter.setOnInViewClickListener(R.id.card_item_root,
                new BaseRecyclerViewAdapter.onInternalClickListenerImpl<CacheListItem>() {
                    @Override
                    public void OnClickListener(android.view.View parentV, android.view.View v, Integer position, CacheListItem values) {
                        super.OnClickListener(parentV, v, position, values);
                        //onRecyclerViewItemClick(position, values);
                        Intent intent = new Intent();
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.setAction(
                                android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse(
                                "package:" + values.getPackageName()));
                        mContext.startActivity(intent);
                    }
                });
        recyclerAdapter.setFirstOnly(false);
        recyclerAdapter.setDuration(300);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(mCallback);
        mContext.bindService(new Intent(mContext, CleanerService.class),
                mServiceConnection, Context.BIND_AUTO_CREATE);
        mRubbishClean.initViews(recyclerAdapter, mContext, itemTouchHelper);
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
                if (mRubbishClean.isRefreshing()) {
                    return true;
                }
                onRefresh();
                return true;
            //case R.id.about:
            //    startAboutActivity();
            //    return true;
        }
        return false;
    }


    @Override public void onDestroy() {
        //mContext.unbindService(mServiceConnection);
    }


    @Override public void attachView(View v) {
        mRubbishClean = (RubbishClean) v;
    }


    @Override public void onScanStarted(Context context) {
        mRubbishClean.onScanStarted(context);
        mRubbishClean.startRefresh();
        mRubbishClean.enableSwipeRefreshLayout(false);
        //mProgressBarText.setText(R.string.scanning);
        //showProgressBar(true);
    }


    @Override
    public void onScanProgressUpdated(Context context, int current, int max, long cacheSize, String packageName) {
        mRubbishClean.onScanProgressUpdated(context, current, max, cacheSize,
                packageName);
    }


    @Override
    public void onScanCompleted(Context context, List<CacheListItem> apps) {
        mCacheListItems.clear();
        mCacheListItems.addAll(apps);
        recyclerAdapter.notifyDataSetChanged();
        mRubbishClean.onScanCompleted();
        mContext.unbindService(mServiceConnection);
        mRubbishClean.stopRefresh();
        mRubbishClean.enableSwipeRefreshLayout(true);
    }


    @Override public void onCleanStarted(Context context) {
        //if (isProgressBarVisible()) {
        //    showProgressBar(false);
        //}
        //
        //if (!RubbishCleanActivity.this.isFinishing()) {
        //    showDialogLoading();
        //}
    }


    @Override public void onCleanCompleted(Context context, long cacheSize) {
        //dismissDialogLoading();
        T.showLong(mContext, context.getString(R.string.cleaned,
                Formatter.formatShortFileSize(mContext, cacheSize)));
        mCacheListItems.clear();
        recyclerAdapter.notifyDataSetChanged();
    }


    @Override public void onRefresh() {
        mContext.bindService(new Intent(mContext, CleanerService.class),
                mServiceConnection, Context.BIND_AUTO_CREATE);
    }


    public void cleanCache() {
        if (mCleanerService != null && !mCleanerService.isScanning() &&
                !mCleanerService.isCleaning() &&
                mCleanerService.getCacheSize() > 0) {
            mAlreadyCleaned = false;

            mCleanerService.cleanCache();
        }
    }
}
