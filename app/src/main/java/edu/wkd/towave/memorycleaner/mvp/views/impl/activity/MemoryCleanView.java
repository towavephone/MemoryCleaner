package edu.wkd.towave.memorycleaner.mvp.views.impl.activity;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.widget.RelativeLayout;
import edu.wkd.towave.memorycleaner.adapter.ProcessListAdapter;
import edu.wkd.towave.memorycleaner.mvp.views.View;

/**
 * Created by Administrator on 2016/5/4.
 */
public interface MemoryCleanView extends View {
    void initViews(ProcessListAdapter recyclerAdapter, Context context, ItemTouchHelper itemTouchHelper);

    void onScanStarted(Context context);

    void onScanProgressUpdated(Context context, int current, int max, long memory, String processName);

    void onScanCompleted();

    void stopRefresh();

    void startRefresh();

    boolean isRefreshing();

    RelativeLayout setDialogValues(String[] memory);

    void enableSwipeRefreshLayout(boolean enable);
}
