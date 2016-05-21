package edu.wkd.towave.memorycleaner.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
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
import butterknife.OnClick;
import com.john.waveview.WaveView;
import com.pluscubed.recyclerfastscroll.RecyclerFastScroller;
import edu.wkd.towave.memorycleaner.R;
import edu.wkd.towave.memorycleaner.adapter.AppsListAdapter;
import edu.wkd.towave.memorycleaner.adapter.AutoStartAdapter;
import edu.wkd.towave.memorycleaner.mvp.presenters.Presenter;
import edu.wkd.towave.memorycleaner.mvp.presenters.impl.fragment.AppsPresenter;
import edu.wkd.towave.memorycleaner.mvp.presenters.impl.fragment.AutoStartPresenter;
import edu.wkd.towave.memorycleaner.mvp.views.impl.fragment.AppsView;
import edu.wkd.towave.memorycleaner.mvp.views.impl.fragment.AutoStartView;
import edu.wkd.towave.memorycleaner.tools.SnackbarUtils;
import edu.wkd.towave.memorycleaner.tools.StorageUtil;
import edu.wkd.towave.memorycleaner.ui.fragment.base.BaseFragment;
import javax.inject.Inject;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

/**
 * Created by Administrator on 2016/4/28.
 */
public class AutoStartFragment extends BaseFragment implements AutoStartView {

    @Bind(R.id.recyclerView) RecyclerView recyclerView;
    @Bind(R.id.scanProgress) MaterialProgressBar mProgressBar;
    @Bind(R.id.processName) TextView mTextView;
    @Bind(R.id.wave_view) WaveView mWaveView;
    @Bind(R.id.recyclerfastscroll) RecyclerFastScroller mRecyclerFastScroller;
    @Bind(R.id.toolbar_layout) CollapsingToolbarLayout mCollapsingToolbarLayout;
    @Bind(R.id.refresher) SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.coordinatorLayout) CoordinatorLayout mCoordinatorLayout;
    @Bind(R.id.disableApps) FloatingActionButton mFloatingActionButton;
    @Inject AutoStartPresenter mAutoStartPresenter;

    public static final String ARG_POSITION = "position";


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        if (getArguments().containsKey(ARG_POSITION)) {
            int position = getArguments().getInt(ARG_POSITION);
            mAutoStartPresenter.onActivityCreated(position);
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
        return mAutoStartPresenter;
    }


    @Override protected void initializeDependencyInjector() {
        super.initializeDependencyInjector();
        mBuilder.inject(this);
    }


    @Override public void onResume() {
        super.onResume();
        mAutoStartPresenter.onResume();
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
    public void initViews(AutoStartAdapter recyclerAdapter, Context context) {
        //TypedArray actionbarSizeTypedArray = context.obtainStyledAttributes(
        //        new int[] { android.R.attr.actionBarSize });
        //float h = actionbarSizeTypedArray.getDimension(0, 0);
        //mCoordinatorLayout.setPadding(0, (int) h * 2, 0, 0);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(context, LinearLayoutManager.VERTICAL,
                        false));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(recyclerAdapter);
        mSwipeRefreshLayout.setOnRefreshListener(mAutoStartPresenter);
        TypedValue typedValue = new TypedValue();
        context.getTheme()
               .resolveAttribute(R.attr.colorPrimary, typedValue, true);
        mSwipeRefreshLayout.setColorSchemeColors(typedValue.data);
        mRecyclerFastScroller.attachRecyclerView(recyclerView);
    }


    @Override public void showSnackbar(String message) {
        SnackbarUtils.show(mFloatingActionButton, message);
    }


    @Override public void setFabVisible(boolean visible) {
        mFloatingActionButton.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }


    @Override
    public void onProgressUpdate(int current, int max, String appName) {
        mCollapsingToolbarLayout.setTitle(current + "个应用");
        mTextView.setText("正在扫描:" + current + "/" + max + " 应用名:" +
                appName);
        float percent = (int) (1.0 * current / max * 100);
        mProgressBar.setProgress((int) percent);
    }


    @Override public void onPreExecute() {
        mCollapsingToolbarLayout.setTitle("0个应用");
        mWaveView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        mProgressBar.setProgress(0);
        mTextView.setVisibility(View.VISIBLE);
        mTextView.setText("开始扫描");
    }


    @Override public void onDestroy() {
        super.onDestroy();
        mAutoStartPresenter.onDestroy();
    }


    @Override public void onPostExecute(AutoStartAdapter autoStartAdapter) {
        mCollapsingToolbarLayout.setTitle(
                autoStartAdapter.getList().size() + "个应用");
        mProgressBar.setVisibility(View.GONE);
        mTextView.setVisibility(View.GONE);
    }


    @Override public void onDestroyView() {
        mAutoStartPresenter.onDestroy();
        super.onDestroyView();
    }


    @OnClick(R.id.disableApps) public void disableApps() {
        mAutoStartPresenter.disableApps();
    }
}
