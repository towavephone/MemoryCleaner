package edu.wkd.towave.memorycleaner;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mikhaellopez.circularfillableloaders.CircularFillableLoaders;
import edu.wkd.towave.memorycleaner.tools.MemoryUsedMessage;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Bind(R.id.textview1) TextView textView;
    @Bind(R.id.textview2) TextView textView2;
    @Bind(R.id.textview3) TextView textView3;
    @Bind(R.id.clear_memory_button) Button button;
    @Bind(R.id.fab) FloatingActionButton fab;
    @Bind(R.id.circularFillableLoaders) CircularFillableLoaders mCircularFillableLoaders;
    @Bind(R.id.nav_view) NavigationView navigationView;
    @Bind(R.id.drawer_layout) DrawerLayout drawer;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.linechartview) LineChartView mLineChartView;

    Context context;
    private long exitTime = 0;
    private List<PointValue> mPointValues;
    private List<AxisValue> mAxisValues;
    private List<Line> lines;
    final static int MAX_COUNT = 2 * 60;
    final static int REAL_MAX_COUNT = MAX_COUNT + MAX_COUNT / 15;
    int count;
    long sum, available;
    float percent;
    int status = IS_NORMAL;
    public static final int IS_CLEANING = 100;
    public static final int IS_NORMAL = 101;
    public static final int IS_CLEAN_FINISH = 102;
    private Handler mh = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int msgId = msg.what;
            switch (msgId) {
                case IS_NORMAL:
                    updateTextViews();
                    updateChartsData(count++, percent);
                    break;
                case IS_CLEAN_FINISH:
                    button.setClickable(true);
                    status = IS_CLEAN_FINISH;
                    switchStatus();
                    break;
                default:
                    break;
            }
        }


        ;
    };


    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        context = getApplicationContext();
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action",
                        Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer,
                toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        setTimeTask();
        addListener();
    }


    public void addListener() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                // TODO Auto-generated method stub
                switchStatusByClick();
            }
        });
    }


    private void switchStatusByClick() {
        if (status == IS_NORMAL) {
            status = IS_CLEANING;
            switchStatus();
        }
        else if (status == IS_CLEAN_FINISH) {
            status = IS_NORMAL;
        }
    }


    private void initViews() {
        mPointValues = new ArrayList<PointValue>();
        mAxisValues = new ArrayList<AxisValue>();
        count = 0;
    }


    private void switchStatus() {
        // TODO Auto-generated method stub
        switch (status) {
            case IS_CLEANING:
                button.setText("正在清理");
                //startAnimation();
                button.setClickable(false);
                //clearMemory();
                break;
            case IS_NORMAL:
                button.setText(percent + "%\n一键清理");// 已用内存
                break;
            case IS_CLEAN_FINISH:
                button.setText("清理完成");
                //clearAnimation();
                button.performClick();
                // Main a = (Main) getActivity();
                // a.getAdapter().reLoad();
                break;
            default:
                break;
        }
    }


    private void setTimeTask() {
        new Timer().schedule(new TimerTask() {
            @Override public void run() {
                Message message = new Message();
                try {
                    sum = MemoryUsedMessage.getTotalMemory();
                    available = MemoryUsedMessage.getAvailMemory(context);
                    percent = MemoryUsedMessage.getPercent(context);
                    message.what = IS_NORMAL;
                    mh.sendMessage(message);
                } catch (Exception e) {
                    message.what = 3;
                    mh.sendMessage(message);
                }
            }
        }, 0, 1000);
    }


    public void updateChartsData(int count, float percent) {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        String time = simpleDateFormat.format(date);
        mCircularFillableLoaders.setProgress((int) (100 - percent));

        mPointValues.add(new PointValue(count, percent));
        mAxisValues.add(new AxisValue(count).setLabel(time));
        if (count > REAL_MAX_COUNT) {
            mPointValues.remove(0);
            mAxisValues.remove(0);
        }
        Line line = new Line(mPointValues).setColor(Color.parseColor("#3f51b5"))
                                          .setCubic(true)
                                          .setFilled(true)
                                          .setHasPoints(false);
        lines = new ArrayList<Line>();
        lines.add(line);
        LineChartData data = new LineChartData();
        data.setLines(lines);
        // 坐标轴
        Axis axisX = new Axis(mAxisValues).setTextColor(Color.BLACK)
                                          .setHasLines(true)
                                          .setLineColor(Color.BLACK)
                                          .setMaxLabelChars(8); // X轴
        data.setAxisXBottom(axisX);
        Axis axisY = new Axis().setHasLines(true)
                               .setTextColor(Color.BLACK)
                               .setLineColor(Color.BLACK)
                               .setMaxLabelChars(6); // Y轴
        // Float num[]=new Float[]{(float) 0,(float) 20,(float)
        // 40,(float) 60,(float) 80,(float) 100};
        //
        // axisY=Axis.generateAxisFromRange(0, 100, (float) 0.1);
        // axisY.setFormatter(new SimpleAxisValueFormatter(1));
        // 默认是3，只能看最后三个数字
        data.setAxisYLeft(axisY);

        // 设置行为属性，支持缩放、滑动以及平移
        mLineChartView.setInteractive(false);
        // mLineChartView.setZoomType(ZoomType.HORIZONTAL);
        // mLineChartView.setContainerScrollEnabled(true,
        // ContainerScrollType.HORIZONTAL);
        mLineChartView.setLineChartData(data);
        mLineChartView.setVisibility(View.VISIBLE);
    }


    //public void startAnimation(){
    //    mpieChartView.startAnimation(animation);
    //}
    //public void clearAnimation(){
    //    mpieChartView.clearAnimation();
    //}
    public void updateTextViews() {
        // TODO Auto-generated method stub
        textView.setText("已用内存:\n" + (sum - available) + "MB");
        textView2.setText("可用内存:\n" + available + "MB");
        textView3.setText("总内存:\n" + sum + "MB");
        if (status == IS_NORMAL) {
            switchStatus();
        }
    }


    @Override public void onBackPressed() {
        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }


    @Override public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody") @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        }
        else if (id == R.id.nav_gallery) {

        }
        else if (id == R.id.nav_slideshow) {

        }
        else if (id == R.id.nav_manage) {

        }
        else if (id == R.id.nav_share) {

        }
        else if (id == R.id.nav_send) {

        }

        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK &&
                event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序",
                        Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            }
            else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
