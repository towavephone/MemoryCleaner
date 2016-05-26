package edu.wkd.towave.memorycleaner.mvp.views.impl.fragment;

import android.content.Context;
import edu.wkd.towave.memorycleaner.adapter.MenuListAdapter;
import edu.wkd.towave.memorycleaner.mvp.views.View;

/**
 * Created by towave on 2016/5/10.
 */
public interface CircularLoaderView extends View {

    void initViews(MenuListAdapter recyclerAdapter);

    void updateViews(long sum, long available, float percent);

    void onCleanStarted(Context context);

    void onCleanCompleted(Context context, long memory);
}
