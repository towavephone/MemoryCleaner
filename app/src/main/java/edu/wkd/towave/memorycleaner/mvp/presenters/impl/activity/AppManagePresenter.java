package edu.wkd.towave.memorycleaner.mvp.presenters.impl.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import edu.wkd.towave.memorycleaner.adapter.base.BaseFragmentPageAdapter;
import edu.wkd.towave.memorycleaner.injector.ContextLifeCycle;
import edu.wkd.towave.memorycleaner.mvp.presenters.Presenter;
import edu.wkd.towave.memorycleaner.mvp.views.View;
import edu.wkd.towave.memorycleaner.mvp.views.impl.activity.AppManageView;
import edu.wkd.towave.memorycleaner.ui.activity.AppManage;
import edu.wkd.towave.memorycleaner.ui.fragment.SystemApps;
import edu.wkd.towave.memorycleaner.ui.fragment.UserApps;
import java.util.ArrayList;
import javax.inject.Inject;

/**
 * Created by Administrator on 2016/5/4.
 */
public class AppManagePresenter implements Presenter {
    AppManageView mAppManageView;
    final Context mContext;
    ArrayList<Fragment> items;
    ArrayList<String> titles;


    @Inject
    public AppManagePresenter(@ContextLifeCycle("Activity") Context context) {
        this.mContext = context;
    }


    @Override public void onCreate(Bundle savedInstanceState) {
        initViews();
    }


    public void initViews() {
        items = new ArrayList<>();
        items.add(new UserApps());
        items.add(new SystemApps());

        titles = new ArrayList<>();
        titles.add("用户软件");
        titles.add("系统软件");
        mAppManageView.initViews(items, titles);
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

    }


    @Override public void attachView(View v) {
        mAppManageView = (AppManageView) v;
    }
}
