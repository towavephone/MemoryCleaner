package edu.wkd.towave.memorycleaner.mvp.views.impl.activity;

import android.content.Context;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.widget.RelativeLayout;
import edu.wkd.towave.memorycleaner.adapter.IgnoreListAdapter;
import edu.wkd.towave.memorycleaner.adapter.ProcessListAdapter;
import edu.wkd.towave.memorycleaner.mvp.views.View;

/**
 * Created by Administrator on 2016/5/4.
 */
public interface IgnoreSettingView extends View {
    void initViews(IgnoreListAdapter recyclerAdapter, Context context, ItemTouchHelper
            itemTouchHelper);

    void stopRefresh();

    void startRefresh();

    boolean isRefreshing();

    void enableSwipeRefreshLayout(boolean enable);

    void showSnackBar(String message);

    void updateBadge(int count);

    void updateTitle(Context context, long size);
}
