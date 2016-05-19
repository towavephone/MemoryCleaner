package edu.wkd.towave.memorycleaner.mvp.views.impl.activity;

import android.content.Context;
import android.support.v7.widget.helper.ItemTouchHelper;
import edu.wkd.towave.memorycleaner.adapter.CacheListAdapter;
import edu.wkd.towave.memorycleaner.mvp.views.View;

/**
 * Created by Administrator on 2016/5/4.
 */
public interface RubbishCleanView extends View {
    void initViews(CacheListAdapter recyclerAdapter, Context context,ItemTouchHelper itemTouchHelper);

    void onScanStarted(Context context);

    void onScanProgressUpdated(Context context, int current, int max,
                                      long cacheSize, String packageName);
    void onScanCompleted();

    void stopRefresh();

    void startRefresh();

    boolean isRefreshing();

    void enableSwipeRefreshLayout(boolean enable);

}
