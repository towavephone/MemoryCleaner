package edu.wkd.towave.memorycleaner.mvp.presenters.impl.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import edu.wkd.towave.memorycleaner.injector.ContextLifeCycle;
import edu.wkd.towave.memorycleaner.mvp.presenters.Presenter;
import edu.wkd.towave.memorycleaner.mvp.views.View;
import edu.wkd.towave.memorycleaner.mvp.views.impl.activity.AutoStartManageView;
import edu.wkd.towave.memorycleaner.ui.fragment.AppsFragment;
import edu.wkd.towave.memorycleaner.ui.fragment.AutoStartFragment;
import java.util.ArrayList;
import javax.inject.Inject;

/**
 * Created by Administrator on 2016/5/4.
 */
public class AutoStartManagePresenter implements Presenter {
    AutoStartManageView mAutoStartManageView;
    final Context mContext;
    ArrayList<Fragment> items;
    ArrayList<String> titles;


    @Inject
    public AutoStartManagePresenter(
            @ContextLifeCycle("Activity") Context context) {
        this.mContext = context;
    }


    @Override public void onCreate(Bundle savedInstanceState) {
        initViews();
    }


    public void initViews() {
        items = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            AutoStartFragment autoStartFragment = new AutoStartFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(AppsFragment.ARG_POSITION, i);
            autoStartFragment.setArguments(bundle);
            items.add(autoStartFragment);
        }
        titles = new ArrayList<>();
        titles.add("用户软件");
        titles.add("系统软件");
        mAutoStartManageView.initViews(items, titles);
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
        //RefWatcher refWatcher = App.getRefWatcher(mContext);
        //refWatcher.watch(this);
    }


    @Override public void attachView(View v) {
        mAutoStartManageView = (AutoStartManageView) v;
    }
}
