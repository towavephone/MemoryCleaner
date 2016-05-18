package edu.wkd.towave.memorycleaner.mvp.views.impl.activity;

import android.content.Context;
import android.support.v7.widget.helper.ItemTouchHelper;
import edu.wkd.towave.memorycleaner.adapter.ProcessListAdapter;
import edu.wkd.towave.memorycleaner.mvp.views.View;

/**
 * Created by Administrator on 2016/5/4.
 */
public interface MemoryCleanView extends View {
    void initViews(ProcessListAdapter recyclerAdapter, Context context, ItemTouchHelper itemTouchHelper);

    void updateViews(long sum, long available, float percent);

    void onScanStarted(Context context);

    void onScanProgressUpdated(Context context, int current, int max, long memory, String processName);

    void onScanCompleted();

    void stopRefresh();

    void startRefresh();

    boolean isRefreshing();

    void enableSwipeRefreshLayout(boolean enable);

}
