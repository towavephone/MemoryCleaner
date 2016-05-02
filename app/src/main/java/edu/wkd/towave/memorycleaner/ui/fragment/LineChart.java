package edu.wkd.towave.memorycleaner.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import edu.wkd.towave.memorycleaner.R;
import edu.wkd.towave.memorycleaner.tools.MemoryUsedMessage;
import edu.wkd.towave.memorycleaner.tools.TimeUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * Created by Administrator on 2016/4/21.
 */
public class LineChart extends Fragment {

    @Bind(R.id.linechartview) LineChartView mLineChartView;
    @Bind(R.id.percent) TextView mTextView;

    private List<PointValue> mPointValues;
    private List<AxisValue> mAxisValues;

    final static int MAX_COUNT = 60;
    //final static int REAL_MAX_COUNT = MAX_COUNT + MAX_COUNT / 15;
    View view;
    Context context;
    int count;
    long sum, available;
    float percent;
    public static final int IS_NORMAL = 101;
    private Handler mHandler = new Handler() {
        @SuppressLint("HandlerLeak") public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int msgId = msg.what;
            switch (msgId) {
                case IS_NORMAL:
                    updateTextViews();
                    //chartsTools.updateChartsData(count++, percent);
                    break;
                default:
                    Toast.makeText(context, msg.obj.toString(),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    @Nullable @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_linechartview, null);
        context = getActivity();
        ButterKnife.bind(this, view);
        initViews();
        setTimeTask();
        //addListener();
        return view;
    }


    public void updateTextViews() {
        // TODO Auto-generated method stub
        mTextView.setText(percent + "%");
        //LineChartData updatedData = new LineChartData();
        mPointValues.add(new PointValue(count, percent));
        mAxisValues.add(
                new AxisValue(count).setLabel(TimeUtils.getSystemDate()));

        if (count > MAX_COUNT) {
            mPointValues.remove(0);
            mAxisValues.remove(0);
        }
        int color = getResources().getColor(R.color.colorPrimary);
        Line line = new Line(mPointValues).setColor(color)
                                          .setCubic(true)
                                          .setFilled(true)
                                          .setHasPoints(false);
        List<Line> lines = new ArrayList<>();
        lines.add(line);
        LineChartData updatedData = new LineChartData(
                mLineChartView.getLineChartData());
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
        mLineChartView.setLineChartData(updatedData);
        //mLineChartView.setVisibility(View.VISIBLE);
        count++;
    }


    private void setTimeTask() {
        new Timer().schedule(new TimerTask() {
            @Override public void run() {
                Message msg = Message.obtain();
                try {
                    sum = MemoryUsedMessage.getTotalMemory();
                    available = MemoryUsedMessage.getAvailMemory(context);
                    percent = MemoryUsedMessage.getPercent(context);
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


    private void initViews() {
        count = 0;

        mPointValues = new ArrayList<>();
        mAxisValues = new ArrayList<>();
        //set chart data to initialize viewport, otherwise it will be[0,0;0,0]
        //get initialized viewport and change if ranges according to your needs.

        mLineChartView.setInteractive(false);
    }


    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
