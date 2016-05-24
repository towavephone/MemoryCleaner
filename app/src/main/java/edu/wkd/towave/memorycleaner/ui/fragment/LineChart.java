package edu.wkd.towave.memorycleaner.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import edu.wkd.towave.memorycleaner.R;
import edu.wkd.towave.memorycleaner.mvp.presenters.Presenter;
import edu.wkd.towave.memorycleaner.mvp.presenters.impl.fragment.LineChartPresenter;
import edu.wkd.towave.memorycleaner.mvp.views.impl.fragment.LineChartView;
import edu.wkd.towave.memorycleaner.ui.fragment.base.BaseFragment;
import javax.inject.Inject;
import lecho.lib.hellocharts.model.LineChartData;

/**
 * Created by Administrator on 2016/4/21.
 */
public class LineChart extends BaseFragment implements LineChartView {

    @Bind(R.id.linechartview) lecho.lib.hellocharts.view.LineChartView
            mLineChartView;
    @Bind(R.id.percent) TextView mTextView;

    @Inject LineChartPresenter mLineChartPresenter;


    @Nullable @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override protected int getLayoutView() {
        return R.layout.fragment_linechartview;
    }


    @Override protected Presenter getPresenter() {
        return mLineChartPresenter;
    }


    @Override protected void initializeDependencyInjector() {
        super.initializeDependencyInjector();
        mBuilder.inject(this);
    }


    @Override public int initViews() {
        mTextView.setTextColor(getColorPrimary());
        mLineChartView.setInteractive(false);
        return getColorPrimary();
    }


    @Override
    public void updateViews(float percent, LineChartData updatedData) {
        mTextView.setText(percent + "%");
        mLineChartView.setLineChartData(updatedData);
    }


    @Override public void onDestroy() {
        mLineChartPresenter.onDestroy();
        super.onDestroy();
    }
}
