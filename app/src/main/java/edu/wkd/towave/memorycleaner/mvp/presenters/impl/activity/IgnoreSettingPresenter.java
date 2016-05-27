package edu.wkd.towave.memorycleaner.mvp.presenters.impl.activity;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import edu.wkd.towave.memorycleaner.R;
import edu.wkd.towave.memorycleaner.adapter.IgnoreListAdapter;
import edu.wkd.towave.memorycleaner.adapter.base.BaseRecyclerViewAdapter;
import edu.wkd.towave.memorycleaner.injector.ContextLifeCycle;
import edu.wkd.towave.memorycleaner.model.Ignore;
import edu.wkd.towave.memorycleaner.mvp.presenters.Presenter;
import edu.wkd.towave.memorycleaner.mvp.views.View;
import edu.wkd.towave.memorycleaner.mvp.views.impl.activity.IgnoreSettingView;
import edu.wkd.towave.memorycleaner.tools.ObservableUtils;
import edu.wkd.towave.memorycleaner.ui.activity.IgnoreSetting;
import edu.wkd.towave.memorycleaner.ui.activity.MemoryClean;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import net.tsz.afinal.FinalDb;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/5/4.
 */
public class IgnoreSettingPresenter implements Presenter {

    IgnoreSettingView mIgnoreSettingView;
    final Context mContext;
    List<Ignore> mIgnores = new ArrayList<>();
    IgnoreListAdapter recyclerAdapter;
    ObservableUtils mObservableUtils;
    FinalDb mFinalDb;
    boolean isdesc = true;
    Subscription mSubscription;


    @Inject
    public IgnoreSettingPresenter(@ContextLifeCycle("Activity")
                                  Context context, FinalDb finalDb, ObservableUtils observableUtils) {
        this.mContext = context;
        this.mFinalDb = finalDb;
        this.mObservableUtils = observableUtils;
    }


    @Override public void onCreate(Bundle savedInstanceState) {
        initViews();
        loadData();
    }


    public void loadData() {
        mIgnoreSettingView.startRefresh();
        mSubscription = mObservableUtils.getIgnoreApps(mFinalDb, mContext)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(
                                                AndroidSchedulers.mainThread())
                                        .subscribe((v) -> {
                                            mIgnores.addAll(v);
                                            updateMemoryCount();
                                            recyclerAdapter.notifyDataSetChanged();
                                            mIgnoreSettingView.stopRefresh();
                                            mIgnoreSettingView.enableSwipeRefreshLayout(
                                                    false);
                                        }, (e) -> {
                                            e.printStackTrace();
                                        });
    }


    public void initViews() {
        recyclerAdapter = new IgnoreListAdapter(mIgnores, mContext);
        recyclerAdapter.setOnInViewClickListener(R.id.card_item_root,
                new BaseRecyclerViewAdapter.onInternalClickListenerImpl<Ignore>() {
                    @Override
                    public void OnClickListener(android.view.View parentV, android.view.View v, Integer position, Ignore values) {
                        super.OnClickListener(parentV, v, position, values);
                        values.setChecked(!values.getChecked());
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
                mIgnoreSettingView.showSnackBar(
                        mIgnores.get(position).getAppName() + "已移除白名单");
                //mCoreService.killBackgroundProcesses(
                //        mAppProcessInfos.get(position).processName);
                mFinalDb.delete(mIgnores.get(position));
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
        //mContext.bindService(new Intent(mContext, CoreService.class),
        //        mServiceConnection, Context.BIND_AUTO_CREATE);
        mIgnoreSettingView.initViews(recyclerAdapter, mContext,
                itemTouchHelper);
    }


    void updateMemoryCount() {
        int check_count = 0;
        for (Ignore ignore : mIgnores) {
            if (ignore.getChecked()) {
                check_count++;
            }
        }
        mIgnoreSettingView.updateTitle(mContext, mIgnores.size());
        mIgnoreSettingView.updateBadge(check_count);
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
        if (mIgnoreSettingView.isRefreshing()) {
            return true;
        }
        switch (item.getItemId()) {
            //case R.id.setting:
            //    startSettingActivity();
            //    return true;
            case R.id.add:

                return true;
            case R.id.allcheck:
                boolean flag = true;
                for (Ignore ignore : mIgnores) {
                    if (ignore.getChecked()) {
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    for (Ignore ignore : mIgnores) {
                        ignore.setChecked(true);
                    }
                }
                else {
                    for (Ignore ignore : mIgnores) {
                        ignore.setChecked(false);
                    }
                }
                updateMemoryCount();
                recyclerAdapter.notifyDataSetChanged();
                break;
            case MemoryClean.BASE_ID + 1:
                if (item.isChecked()) return true;
                if (isdesc) {
                    Collections.sort(mIgnores, (v2, v1) -> v1.getAppName()
                                                             .compareTo(
                                                                     v2.getAppName()));
                }
                else {
                    Collections.sort(mIgnores, (v1, v2) -> v1.getAppName()
                                                             .compareTo(
                                                                     v2.getAppName()));
                }
                recyclerAdapter.notifyDataSetChanged();
                item.setChecked(true);
                break;
            case MemoryClean.BASE_ID + 2:
                if (item.isChecked()) return true;
                if (isdesc) {
                    Collections.sort(mIgnores, (v2, v1) -> {
                        if (v1.getChecked() == v2.getChecked()) {
                            return 0;
                        }
                        else if (v1.getChecked()) {
                            return 1;
                        }
                        else {
                            return -1;
                        }
                    });
                }
                else {
                    Collections.sort(mIgnores, (v1, v2) -> {
                        if (v1.getChecked() == v2.getChecked()) {
                            return 0;
                        }
                        else if (v1.getChecked()) {
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
            case MemoryClean.BASE_ID + 3:
                isdesc = !item.isChecked();
                item.setChecked(isdesc);
        }
        return false;
    }


    @Override public void onDestroy() {
        //mContext.unbindService(mServiceConnection);
        //RefWatcher refWatcher = App.getRefWatcher(mContext);
        //refWatcher.watch(this);
    }


    @Override public void attachView(View v) {
        mIgnoreSettingView = (IgnoreSetting) v;
    }


    public void cleanMemory() {
        long count = 0;
        for (int i = mIgnores.size() - 1; i >= 0; i--) {
            if (mIgnores.get(i).getChecked()) {
                mFinalDb.delete(mIgnores.get(i));
                count++;
                recyclerAdapter.remove(mIgnores.get(i));
            }
        }
        updateMemoryCount();
        mIgnoreSettingView.showSnackBar(
                count > 0 ? count + "个应用从白名单中移除" : "未选中要移除的应用");
    }
}
