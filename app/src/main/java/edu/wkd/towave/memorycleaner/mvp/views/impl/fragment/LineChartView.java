package edu.wkd.towave.memorycleaner.mvp.views.impl.fragment;

import android.content.Context;
import edu.wkd.towave.memorycleaner.model.Menu;
import edu.wkd.towave.memorycleaner.mvp.views.View;
import java.util.ArrayList;
import lecho.lib.hellocharts.model.LineChartData;

/**
 * Created by towave on 2016/5/14.
 */
public interface LineChartView extends View {
    void initViews();

    void updateViews(float percent, LineChartData updatedData);
}
