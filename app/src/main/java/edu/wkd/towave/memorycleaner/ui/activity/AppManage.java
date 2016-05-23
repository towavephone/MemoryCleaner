package edu.wkd.towave.memorycleaner.ui.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import butterknife.Bind;
import edu.wkd.towave.memorycleaner.App;
import edu.wkd.towave.memorycleaner.R;
import edu.wkd.towave.memorycleaner.adapter.base.BaseFragmentPageAdapter;
import edu.wkd.towave.memorycleaner.injector.component.DaggerActivityComponent;
import edu.wkd.towave.memorycleaner.injector.module.ActivityModule;
import edu.wkd.towave.memorycleaner.mvp.presenters.impl.activity.AppManagePresenter;
import edu.wkd.towave.memorycleaner.mvp.views.impl.activity.AppManageView;
import edu.wkd.towave.memorycleaner.tools.ToolbarUtils;
import edu.wkd.towave.memorycleaner.ui.activity.base.BaseActivity;
import java.util.ArrayList;
import javax.inject.Inject;

public class AppManage extends BaseActivity implements AppManageView {

    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.tabs) TabLayout mTabs;
    @Bind(R.id.container) ViewPager mContainer;

    @Inject AppManagePresenter mAppManagePresenter;

    BaseFragmentPageAdapter mBaseFragmentPageAdapter;


    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializePresenter();
        mAppManagePresenter.onCreate(savedInstanceState);
    }


    private void initializePresenter() {
        mAppManagePresenter.attachView(this);
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


    @Override public void initToolbar() {
        ToolbarUtils.initToolbar(mToolbar, this);
    }


    @Override
    public void initViews(ArrayList<Fragment> items, ArrayList<String> titles) {
        mBaseFragmentPageAdapter = new BaseFragmentPageAdapter(
                getSupportFragmentManager(), items, titles);
        mContainer.setAdapter(mBaseFragmentPageAdapter);

        for (int i = 0; i < mBaseFragmentPageAdapter.getCount(); i++) {
            mTabs.addTab(mTabs.newTab().setText(titles.get(i)));
        }
        mTabs.setupWithViewPager(mContainer);
    }


    @Override protected int getLayoutView() {
        return R.layout.activity_app_manage;
    }
}
