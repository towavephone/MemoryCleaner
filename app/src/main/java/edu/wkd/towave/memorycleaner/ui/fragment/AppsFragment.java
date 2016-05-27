package edu.wkd.towave.memorycleaner.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.Bind;
import com.john.waveview.WaveView;
import com.pluscubed.recyclerfastscroll.RecyclerFastScroller;
import edu.wkd.towave.memorycleaner.R;
import edu.wkd.towave.memorycleaner.adapter.AppsListAdapter;
import edu.wkd.towave.memorycleaner.mvp.presenters.Presenter;
import edu.wkd.towave.memorycleaner.mvp.presenters.impl.fragment.AppsPresenter;
import edu.wkd.towave.memorycleaner.mvp.views.impl.fragment.AppsView;
import edu.wkd.towave.memorycleaner.tools.SnackbarUtils;
import edu.wkd.towave.memorycleaner.tools.StorageUtil;
import edu.wkd.towave.memorycleaner.ui.fragment.base.BaseFragment;
import javax.inject.Inject;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

/**
 * Created by Administrator on 2016/4/28.
 */
public class AppsFragment extends BaseFragment implements AppsView {

    @Bind(R.id.recyclerView) RecyclerView recyclerView;
    @Bind(R.id.scanProgress) MaterialProgressBar mProgressBar;
    @Bind(R.id.processName) TextView mTextView;
    @Bind(R.id.wave_view) WaveView mWaveView;
    @Bind(R.id.recyclerfastscroll) RecyclerFastScroller mRecyclerFastScroller;
    @Bind(R.id.toolbar_layout) CollapsingToolbarLayout mCollapsingToolbarLayout;
    @Bind(R.id.refresher) SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.coordinatorLayout) CoordinatorLayout mCoordinatorLayout;
    @Inject AppsPresenter mAppsPresenter;

    public static final String ARG_POSITION = "position";


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        if (getArguments().containsKey(ARG_POSITION)) {
            //L.d("sssss", getArguments().getInt(ARG_POSITION) + "");
            int position = getArguments().getInt(ARG_POSITION);
            mAppsPresenter.onActivityCreated(position);
        }
        super.onActivityCreated(savedInstanceState);
    }


    @Nullable @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override protected int getLayoutView() {
        return R.layout.include_memory_clean;
    }


    @Override protected Presenter getPresenter() {
        return mAppsPresenter;
    }


    @Override protected void initializeDependencyInjector() {
        super.initializeDependencyInjector();
        mBuilder.inject(this);
    }


    @Override public void onResume() {
        super.onResume();
        mAppsPresenter.onResume();
    }


    @Override public void stopRefresh() {
        mSwipeRefreshLayout.setRefreshing(false);
    }


    @Override public void startRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
    }


    @Override public boolean isRefreshing() {
        return mSwipeRefreshLayout.isRefreshing();
    }


    @Override public void enableSwipeRefreshLayout(boolean enable) {
        mSwipeRefreshLayout.setEnabled(enable);
    }


    @Override
    public void initViews(AppsListAdapter recyclerAdapter, Context context) {
        //TypedArray actionbarSizeTypedArray = context.obtainStyledAttributes(
        //        new int[] { android.R.attr.actionBarSize });
        //float h = actionbarSizeTypedArray.getDimension(0, 0);
        //mCoordinatorLayout.setPadding(0, (int) h * 2, 0, 0);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(context, LinearLayoutManager.VERTICAL,
                        false));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(recyclerAdapter);
        mSwipeRefreshLayout.setOnRefreshListener(mAppsPresenter);
        TypedValue typedValue = new TypedValue();
        context.getTheme()
               .resolveAttribute(R.attr.colorPrimary, typedValue, true);
        mSwipeRefreshLayout.setColorSchemeColors(typedValue.data);
        mRecyclerFastScroller.attachRecyclerView(recyclerView);
    }

    //@Override public boolean onOptionsItemSelected(MenuItem item) {
    //    if (mAppsPresenter.onOptionsItemSelected(item.getItemId())) {
    //        return true;
    //    }
    //    return super.onOptionsItemSelected(item);
    //}


    @Override public RelativeLayout setDialogValues(String[] memory) {
        RelativeLayout dialog_process_detail
                = (RelativeLayout) getActivity().getLayoutInflater()
                                                .inflate(
                                                        R.layout.dialog_process_detail,
                                                        null);
        if (memory == null || memory.length == 0) return dialog_process_detail;
        TextView mTextView2 = (TextView) dialog_process_detail.findViewById(
                R.id.memory);
        TextView mTextView3 = (TextView) dialog_process_detail.findViewById(
                R.id.unit);
        mTextView2.setText(memory[0]);
        mTextView3.setText(memory[1]);
        return dialog_process_detail;
    }


    @Override public void showSnackBar(String message) {
        SnackbarUtils.show(getActivity(), message);
    }


    @Override
    public void onProgressUpdate(int current, int max, long memory, String appName) {
        mCollapsingToolbarLayout.setTitle(StorageUtil.convertStorage(memory));
        mTextView.setText("正在扫描:" + current + "/" + max + " 应用名:" +
                appName);
        float percent = (int) (1.0 * current / max * 100);
        mProgressBar.setProgress((int) percent);
    }


    @Override public void onPreExecute() {
        mCollapsingToolbarLayout.setTitle("0M");
        mWaveView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        mProgressBar.setProgress(0);
        mTextView.setVisibility(View.VISIBLE);
        mTextView.setText("开始扫描");
    }


    @Override public void onDestroy() {
        super.onDestroy();
        mAppsPresenter.onDestroy();
    }


    @Override
    public void onPostExecute(AppsListAdapter appsListAdapter, long memory) {
        mCollapsingToolbarLayout.setTitle(
                appsListAdapter.getList().size() + "款共" +
                        StorageUtil.convertStorage(memory));
        mProgressBar.setVisibility(View.GONE);
        mTextView.setVisibility(View.GONE);
    }


    @Override public void onDestroyView() {
        mAppsPresenter.onDestroy();
        super.onDestroyView();
    }
}
