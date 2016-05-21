package edu.wkd.towave.memorycleaner.mvp.presenters.impl.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.squareup.leakcanary.RefWatcher;
import edu.wkd.towave.memorycleaner.App;
import edu.wkd.towave.memorycleaner.injector.ContextLifeCycle;
import edu.wkd.towave.memorycleaner.mvp.presenters.Presenter;
import edu.wkd.towave.memorycleaner.mvp.views.View;
import edu.wkd.towave.memorycleaner.mvp.views.impl.activity.AppManageView;
import edu.wkd.towave.memorycleaner.ui.fragment.AppsFragment;
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

        for (int i = 0; i < 2; i++) {
            AppsFragment appsFragment = new AppsFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(AppsFragment.ARG_POSITION, i);
            appsFragment.setArguments(bundle);
            items.add(appsFragment);
        }
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
        RefWatcher refWatcher = App.getRefWatcher(mContext);
        refWatcher.watch(this);
    }


    @Override public void attachView(View v) {
        mAppManageView = (AppManageView) v;
    }
}
