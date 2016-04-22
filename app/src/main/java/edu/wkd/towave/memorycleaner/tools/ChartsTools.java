//package edu.wkd.towave.memorycleaner.tools;
//
//import butterknife.Bind;
//import butterknife.ButterKnife;
//import com.db.chart.view.LineChartView;
//import com.mikhaellopez.circularfillableloaders.CircularFillableLoaders;
//import edu.wkd.towave.memorycleaner.R;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//import android.content.Context;
//import android.graphics.Color;
//import android.view.View;
//import android.view.animation.Animation;
//import android.view.animation.AnimationUtils;
//
//public class ChartsTools {
//
//    @Bind(R.id.linechart) LineChartView mLineChartView;
//
//    private List<PointValue> mPointValues;
//    private List<AxisValue> mAxisValues;
//    private List<Line> lines;
//
//    final static int MAX_COUNT = 2 * 60;
//    final static int REAL_MAX_COUNT = MAX_COUNT + MAX_COUNT / 15;
//    //private Animation animation;
//
//    public ChartsTools(Context context,View view) {
//        ButterKnife.bind(this,view);
//        //mLineChartView = (LineChartView) view.findViewById(R.id
//        // .linechartview);
//        //mpieChartView = (PieChartView) view.findViewById(R.id.piechartview);
//        mPointValues = new ArrayList<PointValue>();
//        mAxisValues = new ArrayList<AxisValue>();
//        //sliceValues = new ArrayList<SliceValue>();
//        //animation = AnimationUtils.loadAnimation(context, R.anim.my_rotate);
//    }
//
//    public void updateChartsData(int count, float percent) {
//        Date date = new Date();
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
//        String time = simpleDateFormat.format(date);
//
//        mCircularFillableLoaders.setProgress((int) (100 - percent));
//
//        mPointValues.add(new PointValue(count, percent));
//        mAxisValues.add(new AxisValue(count).setLabel(time));
//        if (count > REAL_MAX_COUNT) {
//            mPointValues.remove(0);
//            mAxisValues.remove(0);
//        }
//        Line line = new Line(mPointValues)
//                .setColor(Color.parseColor("#3f51b5")).setCubic(true)
//                .setFilled(true).setHasPoints(false);
//        lines = new ArrayList<Line>();
//        lines.add(line);
//        LineChartData data = new LineChartData();
//        data.setLines(lines);
//        // 坐标轴
//        Axis axisX = new Axis(mAxisValues).setTextColor(Color.BLACK)
//                                          .setHasLines(true).setLineColor(Color.BLACK)
//                                          .setMaxLabelChars(8); // X轴
//        data.setAxisXBottom(axisX);
//        Axis axisY = new Axis().setHasLines(true).setTextColor(Color.BLACK)
//                               .setLineColor(Color.BLACK).setMaxLabelChars(6); // Y轴
//
//        // 默认是3，只能看最后三个数字
//        data.setAxisYLeft(axisY);
//
//        // 设置行为属性，支持缩放、滑动以及平移
//        //mLineChartView.setInteractive(false);
//        //mLineChartView.setLineChartData(data);
//        //mLineChartView.setVisibility(View.VISIBLE);
//    }
//
//}
