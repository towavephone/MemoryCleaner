package edu.wkd.towave.memorycleaner.mvp.presenters.impl.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.PopupMenu;
import edu.wkd.towave.memorycleaner.R;
import edu.wkd.towave.memorycleaner.injector.ContextLifeCycle;
import edu.wkd.towave.memorycleaner.mvp.presenters.Presenter;
import edu.wkd.towave.memorycleaner.mvp.views.View;
import edu.wkd.towave.memorycleaner.mvp.views.impl.activity.MainView;
import edu.wkd.towave.memorycleaner.tools.PreferenceUtils;
import edu.wkd.towave.memorycleaner.ui.activity.AppManage;
import javax.inject.Inject;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by Administrator on 2016/5/4.
 */
public class MainPresenter
        implements Presenter, PopupMenu.OnMenuItemClickListener {
    MainView mMainView;
    final Context mContext;
    private PreferenceUtils mPreferenceUtils;


    @Override public boolean onMenuItemClick(MenuItem menuItem) {
        return false;
    }


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
        mMainView.initViewPager();
        //EventBus.getDefault().register(this);
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
