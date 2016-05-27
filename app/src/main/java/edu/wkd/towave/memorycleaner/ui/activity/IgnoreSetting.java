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
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.OnClick;
import com.john.waveview.WaveView;
import com.mikepenz.actionitembadge.library.ActionItemBadge;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.pluscubed.recyclerfastscroll.RecyclerFastScroller;
import edu.wkd.towave.memorycleaner.App;
import edu.wkd.towave.memorycleaner.R;
import edu.wkd.towave.memorycleaner.adapter.IgnoreListAdapter;
import edu.wkd.towave.memorycleaner.injector.component.DaggerActivityComponent;
import edu.wkd.towave.memorycleaner.injector.module.ActivityModule;
import edu.wkd.towave.memorycleaner.mvp.presenters.impl.activity.IgnoreSettingPresenter;
import edu.wkd.towave.memorycleaner.mvp.views.impl.activity.IgnoreSettingView;
import edu.wkd.towave.memorycleaner.tools.SnackbarUtils;
import edu.wkd.towave.memorycleaner.ui.activity.base.BaseActivity;
import javax.inject.Inject;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class IgnoreSetting extends BaseActivity implements IgnoreSettingView {
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.recyclerView) RecyclerView recyclerView;
    @Bind(R.id.scanProgress) MaterialProgressBar mProgressBar;
    @Bind(R.id.processName) TextView mTextView;
    @Bind(R.id.wave_view) WaveView mWaveView;
    @Bind(R.id.recyclerfastscroll) RecyclerFastScroller mRecyclerFastScroller;
    @Bind(R.id.toolbar_layout) CollapsingToolbarLayout mCollapsingToolbarLayout;
    @Bind(R.id.clean_memory) FloatingActionButton mFloatingActionButton;
    @Bind(R.id.refresher) SwipeRefreshLayout mSwipeRefreshLayout;
    @Inject IgnoreSettingPresenter mIgnoreSettingPresenter;
    public static final int BASE_ID = 0;
    public static final int GROUP_ID = 100;
    MenuItem mMenuItem;


    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializePresenter();
        mIgnoreSettingPresenter.onCreate(savedInstanceState);
    }


    @Override public void onDestroy() {
        super.onDestroy();
    }


    private void initializePresenter() {
        mIgnoreSettingPresenter.attachView(this);
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
        getMenuInflater().inflate(R.menu.menu_ignore_setting, menu);
        SubMenu subMenu = menu.addSubMenu(GROUP_ID, BASE_ID, 0, "排序");
        subMenu.setIcon(R.drawable.ic_sort_white_24dp);
        subMenu.add(GROUP_ID + 1, BASE_ID + 1, 0, "应用名");
        subMenu.add(GROUP_ID + 1, BASE_ID + 2, 1, "选中");
        subMenu.add(GROUP_ID + 2, BASE_ID + 3, 2, "降序")
               .setCheckable(true)
               .setChecked(true);
        subMenu.setGroupCheckable(GROUP_ID + 1, true, true);
        mMenuItem = menu.findItem(R.id.allcheck);
        ActionItemBadge.update(this, mMenuItem, FontAwesome.Icon.faw_check,
                ActionItemBadge.BadgeStyles.DARK_GREY, 0);
        return true;
    }


    @Override
    public void initViews(IgnoreListAdapter recyclerAdapter, Context context, ItemTouchHelper itemTouchHelper) {
        mWaveView.setVisibility(View.GONE);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(context, LinearLayoutManager.VERTICAL,
                        false));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(recyclerAdapter);
        //mSwipeRefreshLayout.setOnRefreshListener(mIgnoreSettingPresenter);
        mSwipeRefreshLayout.setColorSchemeColors(getColorPrimary());
        mRecyclerFastScroller.attachRecyclerView(recyclerView);
        mCollapsingToolbarLayout.setTitle(0 + "个应用");
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }


    @OnClick(R.id.clean_memory) public void cleanMemory() {
        mIgnoreSettingPresenter.cleanMemory();
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


    @Override public void updateBadge(int count) {
        ActionItemBadge.update(mMenuItem, count);
    }


    @Override public void updateTitle(Context context, long size) {
        mCollapsingToolbarLayout.setTitle(size + "个应用");
    }


    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (mIgnoreSettingPresenter.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
