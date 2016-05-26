package edu.wkd.towave.memorycleaner.mvp.views.impl.fragment;

import edu.wkd.towave.memorycleaner.mvp.views.View;
import lecho.lib.hellocharts.model.LineChartData;

/**
 * Created by towave on 2016/5/14.
 */
public interface LineChartView extends View {
    int initViews();

    void updateViews(float percent, LineChartData updatedData);
}
