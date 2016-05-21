package edu.wkd.towave.memorycleaner.mvp.presenters.impl.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.widget.PopupMenu;
import com.squareup.leakcanary.RefWatcher;
import edu.wkd.towave.memorycleaner.App;
import edu.wkd.towave.memorycleaner.R;
import edu.wkd.towave.memorycleaner.adapter.base.BaseFragmentPageAdapter;
import edu.wkd.towave.memorycleaner.injector.ContextLifeCycle;
import edu.wkd.towave.memorycleaner.mvp.presenters.Presenter;
import edu.wkd.towave.memorycleaner.mvp.views.View;
import edu.wkd.towave.memorycleaner.mvp.views.impl.activity.MainView;
import edu.wkd.towave.memorycleaner.tools.PreferenceUtils;
import edu.wkd.towave.memorycleaner.ui.activity.AppManage;
import edu.wkd.towave.memorycleaner.ui.fragment.CircularLoader;
import edu.wkd.towave.memorycleaner.ui.fragment.LineChart;
import java.util.ArrayList;
import javax.inject.Inject;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by Administrator on 2016/5/4.
 */
public class MainPresenter implements Presenter {
    MainView mMainView;
    final Context mContext;
    private PreferenceUtils mPreferenceUtils;
    ArrayList<Fragment> items;


    @Inject
    public MainPresenter(@ContextLifeCycle("Activity")
                         Context context, PreferenceUtils preferenceUtils) {
        this.mContext = context;
        this.mPreferenceUtils = preferenceUtils;
    }


    @Override public void attachView(View view) {
        this.mMainView = (MainView) view;
    }


    @Override public void onCreate(Bundle savedInstanceState) {
        mMainView.initToolbar();
        mMainView.initDrawerView();
        initViewPager();
        //EventBus.getDefault().register(this);
    }


    public void initViewPager() {
        items = new ArrayList<>();
        items.add(new CircularLoader());
        items.add(new LineChart());
        mMainView.initViewPager(items);
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
        //EventBus.getDefault().unregister(this);
        RefWatcher refWatcher = App.getRefWatcher(mContext);
        refWatcher.watch(this);
    }


    public boolean onNavigationItemSelected(int id) {
        // Handle navigation view item clicks here.
        switch (id) {
            case R.id.main_content:
                mContext.startActivity(new Intent(mContext, AppManage.class));
                break;
        }
        return true;
    }
}
