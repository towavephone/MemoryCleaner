package edu.wkd.towave.memorycleaner.mvp.presenters.impl.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import edu.wkd.towave.memorycleaner.R;
import edu.wkd.towave.memorycleaner.adapter.ProcessListAdapter;
import edu.wkd.towave.memorycleaner.adapter.base.BaseRecyclerViewAdapter;
import edu.wkd.towave.memorycleaner.injector.ContextLifeCycle;
import edu.wkd.towave.memorycleaner.model.AppProcessInfo;
import edu.wkd.towave.memorycleaner.mvp.presenters.Presenter;
import edu.wkd.towave.memorycleaner.mvp.views.View;
import edu.wkd.towave.memorycleaner.mvp.views.impl.activity.MainView;
import edu.wkd.towave.memorycleaner.service.CoreService;
import edu.wkd.towave.memorycleaner.tools.PreferenceUtils;
import edu.wkd.towave.memorycleaner.ui.activity.MemoryClean;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

/**
 * Created by Administrator on 2016/5/4.
 */
public class MemoryCleanPresenter
        implements Presenter, CoreService.OnProcessActionListener {

    MemoryClean mMemoryClean;
    final Context mContext;
    List<AppProcessInfo> mAppProcessInfos = new ArrayList<>();
    ProcessListAdapter recyclerAdapter;
    public long Allmemory;

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
                        //onRecyclerViewItemClick(position, values);
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
        mContext.bindService(new Intent(mContext, CoreService.class),
                mServiceConnection, Context.BIND_AUTO_CREATE);
        mMemoryClean.initViews(recyclerAdapter,mContext);
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

    }


    @Override public void attachView(View v) {
        mMemoryClean = (MemoryClean) v;
    }


    @Override public void onScanStarted(Context context) {

    }


    @Override
    public void onScanProgressUpdated(Context context, int current, int max) {

    }


    @Override
    public void onScanCompleted(Context context, List<AppProcessInfo> apps) {
        mAppProcessInfos.clear();

        Allmemory = 0;
        for (AppProcessInfo appInfo : apps) {
            if (!appInfo.isSystem) {
                mAppProcessInfos.add(appInfo);
                Allmemory += appInfo.memory;
            }
        }

        recyclerAdapter.notifyDataSetChanged();

    }


    @Override public void onCleanStarted(Context context) {

    }


    @Override public void onCleanCompleted(Context context, long cacheSize) {

    }
}
