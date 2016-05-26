package edu.wkd.towave.memorycleaner.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import butterknife.Bind;
import edu.wkd.towave.memorycleaner.App;
import edu.wkd.towave.memorycleaner.R;
import edu.wkd.towave.memorycleaner.injector.component.DaggerActivityComponent;
import edu.wkd.towave.memorycleaner.injector.module.ActivityModule;
import edu.wkd.towave.memorycleaner.ui.activity.base.BaseActivity;
import edu.wkd.towave.memorycleaner.ui.fragment.SettingFragment;

public class SettingActivity extends BaseActivity {
    @Bind(R.id.toolbar) Toolbar toolbar;


    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
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


    @Override protected int getLayoutView() {
        return R.layout.activity_setting;
    }


    private void init() {
        SettingFragment settingFragment = SettingFragment.newInstance();
        getFragmentManager().beginTransaction()
                            .replace(R.id.fragment_content, settingFragment)
                            .commit();
    }


    @Override protected void initToolbar() {
        super.initToolbar(toolbar);
        //toolbar.setTitle("设置");
    }

    //@Override public boolean onOptionsItemSelected(MenuItem item) {
    //    if(item.getItemId()==R.id)
    //    return super.onOptionsItemSelected(item);
    //}
}
