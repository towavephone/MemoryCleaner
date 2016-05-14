package edu.wkd.towave.memorycleaner.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import butterknife.Bind;
import edu.wkd.towave.memorycleaner.App;
import edu.wkd.towave.memorycleaner.R;
import edu.wkd.towave.memorycleaner.injector.component.DaggerActivityComponent;
import edu.wkd.towave.memorycleaner.injector.module.ActivityModule;
import edu.wkd.towave.memorycleaner.mvp.presenters.impl.activity.MemoryCleanPresenter;
import edu.wkd.towave.memorycleaner.mvp.views.impl.activity.MemoryCleanView;
import edu.wkd.towave.memorycleaner.ui.activity.base.BaseActivity;
import javax.inject.Inject;

public class MemoryClean extends BaseActivity implements MemoryCleanView {
    @Bind(R.id.toolbar) Toolbar toolbar;
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
}
