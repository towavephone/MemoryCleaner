package edu.wkd.towave.memorycleaner.mvp.views.impl.fragment;

import android.content.Context;
import android.widget.RelativeLayout;
import edu.wkd.towave.memorycleaner.adapter.AppsListAdapter;
import edu.wkd.towave.memorycleaner.mvp.views.View;

/**
 * Created by Administrator on 2016/5/5.
 */
public interface AppsView extends View {
    void onProgressUpdate(int current, int max, long memory, String appName);

    void onPreExecute();

    void onPostExecute(AppsListAdapter appsListAdapter,long memory);

    void stopRefresh();

    void startRefresh();

    boolean isRefreshing();

    void enableSwipeRefreshLayout(boolean enable);

    void initViews(AppsListAdapter recyclerAdapter, Context context);

    RelativeLayout setDialogValues(String[] memory);

    void showSnackBar(String message);
}
