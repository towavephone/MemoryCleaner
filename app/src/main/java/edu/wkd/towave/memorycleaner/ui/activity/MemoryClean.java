package edu.wkd.towave.memorycleaner.ui.activity;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.john.waveview.WaveView;
import com.pluscubed.recyclerfastscroll.RecyclerFastScroller;
import edu.wkd.towave.memorycleaner.App;
import edu.wkd.towave.memorycleaner.R;
import edu.wkd.towave.memorycleaner.adapter.ProcessListAdapter;
import edu.wkd.towave.memorycleaner.injector.component.DaggerActivityComponent;
import edu.wkd.towave.memorycleaner.injector.module.ActivityModule;
import edu.wkd.towave.memorycleaner.mvp.presenters.impl.activity.MemoryCleanPresenter;
import edu.wkd.towave.memorycleaner.mvp.views.impl.activity.MemoryCleanView;
import edu.wkd.towave.memorycleaner.tools.AppUtils;
import edu.wkd.towave.memorycleaner.tools.SnackbarUtils;
import edu.wkd.towave.memorycleaner.tools.TextFormater;
import edu.wkd.towave.memorycleaner.ui.activity.base.BaseActivity;
import java.math.BigDecimal;
import javax.inject.Inject;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class MemoryClean extends BaseActivity implements MemoryCleanView {
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.recyclerView) RecyclerView recyclerView;
    @Bind(R.id.scanProgress) MaterialProgressBar mProgressBar;
    @Bind(R.id.processName) TextView mTextView;
    @Bind(R.id.wave_view) WaveView mWaveView;
    @Bind(R.id.recyclerfastscroll) RecyclerFastScroller mRecyclerFastScroller;
    @Bind(R.id.toolbar_layout) CollapsingToolbarLayout mCollapsingToolbarLayout;
    @Bind(R.id.clean_memory) FloatingActionButton mFloatingActionButton;
    @Bind(R.id.refresher) SwipeRefreshLayout mSwipeRefreshLayout;
    @Inject MemoryCleanPresenter mMemoryCleanPresenter;


    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializePresenter();
        mMemoryCleanPresenter.onCreate(savedInstanceState);
    }


    @Override public void onDestroy() {
        super.onDestroy();
        mMemoryCleanPresenter.onDestroy();
    }


    private void initializePresenter() {
        mMemoryCleanPresenter.attachView(this);
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


    @Override public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_memory_clean, menu);
        return true;
    }


    @Override
    public void initViews(ProcessListAdapter recyclerAdapter, Context context, ItemTouchHelper itemTouchHelper) {
        recyclerView.setLayoutManager(
                new LinearLayoutManager(context, LinearLayoutManager.VERTICAL,
                        false));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(recyclerAdapter);
        mSwipeRefreshLayout.setOnRefreshListener(mMemoryCleanPresenter);
        mSwipeRefreshLayout.setColorSchemeColors(getColorPrimary());
        mRecyclerFastScroller.attachRecyclerView(recyclerView);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }


    @Override public void onScanStarted(Context context) {
        mFloatingActionButton.setVisibility(View.GONE);
        mCollapsingToolbarLayout.setTitle(
                "0M 0%-->" + AppUtils.getPercent(context) + "%");
        mWaveView.setProgress(0);
        mProgressBar.setVisibility(View.VISIBLE);
        mProgressBar.setProgress(0);
        mTextView.setVisibility(View.VISIBLE);
        mTextView.setText("开始扫描");
    }


    @Override
    public void onScanProgressUpdated(Context context, int current, int max, long memory, String processName) {

        float scanMemoryPercent = AppUtils.getPercent(memory);
        mCollapsingToolbarLayout.setTitle(
                TextFormater.dataSizeFormat(memory) + " " +
                        scanMemoryPercent + "%-->" +
                        new BigDecimal(AppUtils.getPercent(context) -
                                scanMemoryPercent).setScale(2,
                                BigDecimal.ROUND_HALF_UP).floatValue() +
                        "%");
        mWaveView.setProgress((int) scanMemoryPercent);
        mTextView.setText("正在扫描:" + current + "/" + max + " 进程名:" +
                processName);
        float percent = (int) (1.0 * current / max * 100);
        mProgressBar.setProgress((int) percent);
    }


    @Override public void onScanCompleted() {
        mFloatingActionButton.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        mTextView.setVisibility(View.GONE);
    }


    @Override public RelativeLayout setDialogValues(String[] memory) {
        RelativeLayout dialog_process_detail
                = (RelativeLayout) getLayoutInflater().inflate(
                R.layout.dialog_process_detail, null);
        if (memory == null || memory.length == 0) return dialog_process_detail;
        TextView mTextView2 = (TextView) dialog_process_detail.findViewById(
                R.id.memory);
        TextView mTextView3 = (TextView) dialog_process_detail.findViewById(
                R.id.unit);
        mTextView2.setText(memory[0]);
        mTextView3.setText(memory[1]);
        return dialog_process_detail;
    }


    @OnClick(R.id.clean_memory) public void cleanMemory() {
        mMemoryCleanPresenter.cleanMemory();
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


    @Override public void showSnackBar(String message) {
        SnackbarUtils.show(mFloatingActionButton, message);
    }


    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (mMemoryCleanPresenter.onOptionsItemSelected(item.getItemId())) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
