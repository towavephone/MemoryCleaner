package edu.wkd.towave.memorycleaner.mvp.presenters.impl.activity;

import android.content.Context;
import android.os.Bundle;
import edu.wkd.towave.memorycleaner.injector.ContextLifeCycle;
import edu.wkd.towave.memorycleaner.mvp.presenters.Presenter;
import edu.wkd.towave.memorycleaner.mvp.views.View;
import edu.wkd.towave.memorycleaner.mvp.views.impl.activity.MainView;
import edu.wkd.towave.memorycleaner.tools.PreferenceUtils;
import edu.wkd.towave.memorycleaner.ui.activity.MemoryClean;
import javax.inject.Inject;

/**
 * Created by Administrator on 2016/5/4.
 */
public class MemoryCleanPresenter implements Presenter {

    MemoryClean mMemoryClean;
    final Context mContext;


    @Inject
    public MemoryCleanPresenter(@ContextLifeCycle("Activity") Context context) {
        this.mContext = context;
    }


    @Override public void onCreate(Bundle savedInstanceState) {

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

    }
}
