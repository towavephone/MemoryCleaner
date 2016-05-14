package edu.wkd.towave.memorycleaner.mvp.presenters.impl.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import edu.wkd.towave.memorycleaner.R;
import edu.wkd.towave.memorycleaner.injector.ContextLifeCycle;
import edu.wkd.towave.memorycleaner.model.Menu;
import edu.wkd.towave.memorycleaner.mvp.presenters.Presenter;
import edu.wkd.towave.memorycleaner.mvp.views.View;
import edu.wkd.towave.memorycleaner.tools.MemoryUsedMessage;
import edu.wkd.towave.memorycleaner.tools.TimeUtils;
import edu.wkd.towave.memorycleaner.ui.fragment.CircularLoader;
import edu.wkd.towave.memorycleaner.ui.fragment.LineChart;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.inject.Inject;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;

/**
 * Created by towave on 2016/5/14.
 */
public class LineChartPresenter implements Presenter {

    private LineChart mLineChart;
    private final Context mContext;
    private static final int IS_NORMAL = 101;
    private List<PointValue> mPointValues;
    private List<AxisValue> mAxisValues;

    public final static int MAX_COUNT = 60;

    int count;


    @Inject
    public LineChartPresenter(@ContextLifeCycle("Activity") Context context) {
        this.mContext = context;
    }


    private Handler mHandler = new Handler() {
        @SuppressLint("HandlerLeak") public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case IS_NORMAL:
                    updateViews(count++, (float) msg.obj);
                    break;
                default:
                    Toast.makeText(mContext, msg.obj.toString(),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    @Override public void onCreate(Bundle savedInstanceState) {
        initViews();
        setTimeTask();
    }


    private void updateViews(int count, float percent) {
        //LineChartData updatedData = new LineChartData();
        mPointValues.add(new PointValue(count, percent));
        mAxisValues.add(
                new AxisValue(count).setLabel(TimeUtils.getSystemDate()));

        if (count > MAX_COUNT) {
            mPointValues.remove(0);
            mAxisValues.remove(0);
        }
        int color = mContext.getResources().getColor(R.color.colorPrimary);
        Line line = new Line(mPointValues).setColor(color)
                                          .setCubic(true)
                                          .setFilled(true)
                                          .setHasPoints(false);
        List<Line> lines = new ArrayList<>();
        lines.add(line);
        LineChartData updatedData = new LineChartData();
        updatedData.setLines(lines);
        // 坐标轴
        Axis axisX = new Axis(mAxisValues).setTextColor(color)
                                          .setHasLines(true)
                                          .setLineColor(color)
                                          .setTextSize(9)
                                          .setName("时间(HH:mm:ss)")
                                          .setMaxLabelChars(5); // X轴
        updatedData.setAxisXBottom(axisX);
        Axis axisY = new Axis().setHasLines(true)
                               .setTextColor(color)
                               .setLineColor(color)
                               .setMaxLabelChars(5)
                               .setName("内存占用率(%)"); // Y轴

        // 默认是3，只能看最后三个数字
        updatedData.setAxisYLeft(axisY);
        mLineChart.updateViews(percent,updatedData);
        // 设置行为属性，支持缩放、滑动以及平移
        //final Viewport v = new Viewport(mLineChartView.getMaximumViewport());
        ////v.top =v.top+10; //example max value
        ////v.bottom = v.bottom-10;  //example min value
        ////mLineChartView.setMaximumViewport(v);
        //v.left = v.left + 1; //current viewport will take only part of max viewport horizontally
        //v.right = v.right - 1;
        //mLineChartView.setCurrentViewport(v);
        ////Optional step: disable viewport recalculations, thanks to this animations will not change viewport automatically.
        //mLineChartView.setViewportCalculationEnabled(false);
    }


    private void initViews() {
        count = 0;
        mPointValues = new ArrayList<>();
        mAxisValues = new ArrayList<>();
        mLineChart.initViews();
        //set chart data to initialize viewport, otherwise it will be[0,0;0,0]
        //get initialized viewport and change if ranges according to your needs.
    }


    private void setTimeTask() {
        new Timer().schedule(new TimerTask() {
            @Override public void run() {
                Message msg = Message.obtain();
                try {
                    msg.obj = MemoryUsedMessage.getPercent(mContext);
                    msg.what = IS_NORMAL;
                    mHandler.sendMessage(msg);
                } catch (Exception e) {
                    msg.what = 3;
                    msg.obj = e.toString();
                    mHandler.sendMessage(msg);
                }
            }
        }, 0, 1000);
    }


    @Override public void onResume() {

    }


    @Override public void onStart() {

    }


    @Override public void onPause() {

    }


    @Override public void onStop() {

    }


    @Override public void onDestroy() {

    }


    @Override public void attachView(View v) {
        this.mLineChart = (LineChart) v;
    }
}
