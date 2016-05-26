package edu.wkd.towave.memorycleaner.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.OnClick;
import com.john.waveview.WaveView;
import com.pluscubed.recyclerfastscroll.RecyclerFastScroller;
import edu.wkd.towave.memorycleaner.App;
import edu.wkd.towave.memorycleaner.R;
import edu.wkd.towave.memorycleaner.adapter.CacheListAdapter;
import edu.wkd.towave.memorycleaner.injector.component.DaggerActivityComponent;
import edu.wkd.towave.memorycleaner.injector.module.ActivityModule;
import edu.wkd.towave.memorycleaner.mvp.presenters.impl.activity.RubbishCleanPresenter;
import edu.wkd.towave.memorycleaner.mvp.views.impl.activity.RubbishCleanView;
import edu.wkd.towave.memorycleaner.tools.SnackbarUtils;
import edu.wkd.towave.memorycleaner.tools.TextFormater;
import edu.wkd.towave.memorycleaner.ui.activity.base.BaseActivity;
import javax.inject.Inject;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class RubbishClean extends BaseActivity implements RubbishCleanView {
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.recyclerView) RecyclerView recyclerView;
    @Bind(R.id.scanProgress) MaterialProgressBar mProgressBar;
    @Bind(R.id.processName) TextView mTextView;
    @Bind(R.id.wave_view) WaveView mWaveView;
    @Bind(R.id.recyclerfastscroll) RecyclerFastScroller mRecyclerFastScroller;
    @Bind(R.id.toolbar_layout) CollapsingToolbarLayout mCollapsingToolbarLayout;
    @Bind(R.id.clean_memory) FloatingActionButton mFloatingActionButton;
    @Bind(R.id.refresher) SwipeRefreshLayout mSwipeRefreshLayout;
    @Inject RubbishCleanPresenter mRubbishCleanPresenter;


    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializePresenter();
        mRubbishCleanPresenter.onCreate(savedInstanceState);
    }


    @Override public void onDestroy() {
        super.onDestroy();
        mRubbishCleanPresenter.onDestroy();
    }


    private void initializePresenter() {
        mRubbishCleanPresenter.attachView(this);
    }


    @Override protected void initializeDependencyInjector() {
        App app = (App) getApplication();
        mActivityComponent = DaggerActivityComponent.builder()
                                                    .activityModule(
                                                            new ActivityModule(
                                                                    this))
                                                    .appComponent(
                                                            app.getAppComponent())
                                                    .build();
        mActivityComponent.inject(this);
    }


    @Override protected void initToolbar() {
        super.initToolbar(toolbar);
    }


    @Override protected int getLayoutView() {
        return R.layout.activity_memory_clean;
    }


    @Override
    public void initViews(CacheListAdapter recyclerAdapter, Context context, ItemTouchHelper itemTouchHelper) {
        recyclerView.setLayoutManager(
                new LinearLayoutManager(context, LinearLayoutManager.VERTICAL,
                        false));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(recyclerAdapter);
        mSwipeRefreshLayout.setOnRefreshListener(mRubbishCleanPresenter);
        mSwipeRefreshLayout.setColorSchemeColors(getColorPrimary());
        itemTouchHelper.attachToRecyclerView(recyclerView);
        mRecyclerFastScroller.attachRecyclerView(recyclerView);
    }


    @Override public void onScanStarted(Context context) {
        mFloatingActionButton.setVisibility(View.GONE);
        mCollapsingToolbarLayout.setTitle("0KB 可清理");
        mWaveView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        mProgressBar.setProgress(0);
        mTextView.setVisibility(View.VISIBLE);
        mTextView.setText("开始扫描");
    }


    @Override
    public void onScanProgressUpdated(Context context, int current, int max, long cacheSize, String packageName) {
        mCollapsingToolbarLayout.setTitle(
                TextFormater.dataSizeFormat(cacheSize) + " 可清理");
        mTextView.setText("正在扫描:" + current + "/" + max + " 包名:" +
                packageName);
        float percent = (int) (1.0 * current / max * 100);
        mProgressBar.setProgress((int) percent);
    }


    @Override public void onScanCompleted() {
        mFloatingActionButton.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        mTextView.setVisibility(View.GONE);
    }


    @OnClick(R.id.clean_memory) public void cleanMemory() {
        mRubbishCleanPresenter.cleanCache();
    }


    @Override public void stopRefresh() {
        mSwipeRefreshLayout.setRefreshing(false);
    }


    @Override public void startRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
    }


    @Override public boolean isRefreshing() {
        return mSwipeRefreshLayout.isRefreshing();
    }


    @Override public void enableSwipeRefreshLayout(boolean enable) {
        mSwipeRefreshLayout.setEnabled(enable);
    }


    @Override public void showSnackbar(String message) {
        SnackbarUtils.show(mFloatingActionButton, message);
    }


    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (mRubbishCleanPresenter.onOptionsItemSelected(item.getItemId())) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
