package edu.wkd.towave.memorycleaner.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.Bind;
import com.john.waveview.WaveView;
import edu.wkd.towave.memorycleaner.App;
import edu.wkd.towave.memorycleaner.R;
import edu.wkd.towave.memorycleaner.adapter.ProcessListAdapter;
import edu.wkd.towave.memorycleaner.injector.component.DaggerActivityComponent;
import edu.wkd.towave.memorycleaner.injector.module.ActivityModule;
import edu.wkd.towave.memorycleaner.mvp.presenters.impl.activity.MemoryCleanPresenter;
import edu.wkd.towave.memorycleaner.mvp.views.impl.activity.MemoryCleanView;
import edu.wkd.towave.memorycleaner.tools.AppUtils;
import edu.wkd.towave.memorycleaner.tools.L;
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
    @Bind(R.id.toolbar_layout) CollapsingToolbarLayout mCollapsingToolbarLayout;
    @Inject MemoryCleanPresenter mMemoryCleanPresenter;


    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializePresenter();
        mMemoryCleanPresenter.onCreate(savedInstanceState);
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
    public void initViews(ProcessListAdapter recyclerAdapter, Context context) {
        recyclerView.setLayoutManager(
                new LinearLayoutManager(context, LinearLayoutManager.VERTICAL,
                        false));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(recyclerAdapter);
    }


    @Override public void updateViews(long sum, long available, float percent) {

    }


    @Override public void onScanStarted(Context context) {
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
        mProgressBar.setVisibility(View.GONE);
        mTextView.setVisibility(View.GONE);
    }
}
