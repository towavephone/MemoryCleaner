package edu.wkd.towave.memorycleaner.mvp.presenters.impl.fragment;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import edu.wkd.towave.memorycleaner.R;
import edu.wkd.towave.memorycleaner.adapter.MenuListAdapter;
import edu.wkd.towave.memorycleaner.adapter.base.BaseRecyclerViewAdapter;
import edu.wkd.towave.memorycleaner.injector.ContextLifeCycle;
import edu.wkd.towave.memorycleaner.model.AppProcessInfo;
import edu.wkd.towave.memorycleaner.model.Menu;
import edu.wkd.towave.memorycleaner.mvp.presenters.Presenter;
import edu.wkd.towave.memorycleaner.mvp.views.View;
import edu.wkd.towave.memorycleaner.mvp.views.impl.fragment.CircularLoaderView;
import edu.wkd.towave.memorycleaner.service.CoreService;
import edu.wkd.towave.memorycleaner.tools.AppUtils;
import edu.wkd.towave.memorycleaner.tools.T;
import edu.wkd.towave.memorycleaner.tools.TextFormater;
import edu.wkd.towave.memorycleaner.ui.activity.AppManage;
import edu.wkd.towave.memorycleaner.ui.activity.AutoStartManage;
import edu.wkd.towave.memorycleaner.ui.activity.MemoryClean;
import edu.wkd.towave.memorycleaner.ui.activity.RubbishClean;
import edu.wkd.towave.memorycleaner.ui.fragment.CircularLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.inject.Inject;

/**
 * Created by towave on 2016/5/10.
 */
public class CircularLoaderPresenter
        implements Presenter, CoreService.OnProcessActionListener {

    private CircularLoaderView mCircularLoaderView;
    private final Context mContext;
    private boolean isCardLayout = false;
    private long sum, available;
    private float percent;
    private static final int IS_NORMAL = 101;
    private MenuListAdapter recyclerAdapter;
    private Timer mTimer;

    private CoreService mCoreService;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mCoreService
                    = ((CoreService.ProcessServiceBinder) service).getService();
            mCoreService.setOnActionListener(CircularLoaderPresenter.this);
            mCoreService.cleanAllProcess();
            //  updateStorageUsage();

        }


        @Override public void onServiceDisconnected(ComponentName name) {
            mCoreService.setOnActionListener(null);
            mCoreService = null;
        }
    };


    @Inject
    public CircularLoaderPresenter(
            @ContextLifeCycle("Activity") Context context) {
        this.mContext = context;
        //this.mPreferenceUtils = preferenceUtils;
    }


    @Override public void attachView(View v) {
        this.mCircularLoaderView = (CircularLoader) v;
    }


    @Override public void onCreate(Bundle savedInstanceState) {
        initViews();
        setTimeTask();
    }


    private void initViews() {
        ArrayList<Menu> menus = new ArrayList<>();
        menus.add(new Menu.Builder(mContext).content("内存加速")
                                            .icon(R.drawable.card_icon_speedup)
                                            .build());
        menus.add(new Menu.Builder(mContext).content("垃圾清理")
                                            .icon(R.drawable.card_icon_trash)
                                            .build());
        menus.add(new Menu.Builder(mContext).content("自启管理")
                                            .icon(R.drawable.card_icon_autorun)
                                            .build());
        menus.add(new Menu.Builder(mContext).content("软件管理")
                                            .icon(R.drawable.card_icon_media)
                                            .build());
        recyclerAdapter = new MenuListAdapter(menus, mContext);
        recyclerAdapter.setOnInViewClickListener(R.id.card_item_root,
                new BaseRecyclerViewAdapter.onInternalClickListenerImpl<Menu>() {
                    @Override
                    public void OnClickListener(android.view.View parentV, android.view.View v, Integer position, Menu values) {
                        super.OnClickListener(parentV, v, position, values);
                        onRecyclerViewItemClick(position, values);
                    }
                });
        //recyclerAdapter.setOnInViewClickListener(R.id.note_more,
        //        new BaseRecyclerViewAdapter.onInternalClickListenerImpl<SNote>() {
        //            @Override
        //            public void OnClickListener(View parentV, View v, Integer position, SNote values) {
        //                super.OnClickListener(parentV, v, position, values);
        //                mainPresenter.showPopMenu(v, position, values);
        //            }
        //        });
        recyclerAdapter.setFirstOnly(false);
        recyclerAdapter.setDuration(300);
        mCircularLoaderView.initViews(recyclerAdapter);
    }


    public void onRecyclerViewItemClick(int position, Menu value) {
        switch (position) {
            case 0:
                mContext.startActivity(new Intent(mContext, MemoryClean.class));
                break;
            case 1:
                mContext.startActivity(
                        new Intent(mContext, RubbishClean.class));
                break;
            case 2:
                mContext.startActivity(
                        new Intent(mContext, AutoStartManage.class));
                break;
            case 3:
                mContext.startActivity(new Intent(mContext, AppManage.class));
                break;
            default:
                break;
        }
    }


    public void cleanMemory() {
        mContext.bindService(new Intent(mContext, CoreService.class),
                mServiceConnection, Context.BIND_AUTO_CREATE);
    }


    private Handler mHandler = new Handler() {
        @SuppressLint("HandlerLeak") public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case IS_NORMAL:
                    mCircularLoaderView.updateViews(sum, available, percent);
                    break;
                default:
                    T.showShort(mContext, msg.obj.toString());
                    break;
            }
        }
    };


    public void setTimeTask() {
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override public void run() {
                Message msg = Message.obtain();
                try {
                    sum = AppUtils.getTotalMemory();
                    available = AppUtils.getAvailMemory(mContext);
                    percent = AppUtils.getPercent(mContext);
                    msg.what = IS_NORMAL;
                    mHandler.sendMessage(msg);
                } catch (Exception e) {
                    msg.what = 3;
                    msg.obj = e.toString();
                    mHandler.sendMessage(msg);
                }
            }
        }, 0, 1000);
    }


    @Override public void onResume() {

    }


    @Override public void onStart() {

    }


    @Override public void onPause() {

    }


    @Override public void onStop() {

    }


    @Override public void onDestroy() {
        mTimer.cancel();
    }


    @Override public void onScanStarted(Context context) {

    }


    @Override
    public void onScanProgressUpdated(Context context, int current, int max, long memory, String processName) {

    }


    @Override
    public void onScanCompleted(Context context, List<AppProcessInfo> apps) {

    }


    @Override public void onCleanStarted(Context context) {
        mCircularLoaderView.onCleanStarted(context);
    }


    @Override public void onCleanCompleted(Context context, long cacheSize) {
        T.showLong(context, "已清理内存" + TextFormater.dataSizeFormat(cacheSize));
        mCircularLoaderView.onCleanCompleted(context, cacheSize);
        mContext.unbindService(mServiceConnection);
    }
}
