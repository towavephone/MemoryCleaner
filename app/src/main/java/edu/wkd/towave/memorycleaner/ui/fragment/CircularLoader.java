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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.andexert.library.RippleView;
import com.mikhaellopez.circularfillableloaders.CircularFillableLoaders;
import edu.wkd.towave.memorycleaner.R;
import edu.wkd.towave.memorycleaner.tools.MemoryUsedMessage;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2016/4/21.
 */
public class CircularLoader extends Fragment {
    View view;
    Context context;
    @Bind(R.id.circularFillableLoaders) CircularFillableLoaders
            mCircularFillableLoaders;
    @Bind(R.id.percent) TextView mTextView;
    @Bind(R.id.number) TextView mTextView2;

    int status = IS_NORMAL;
    public static final int IS_NORMAL = 101;
    long sum, available;
    float percent;

    private Handler mHandler = new Handler() {
        @SuppressLint("HandlerLeak") public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int msgId = msg.what;
            switch (msgId) {
                case IS_NORMAL:
                    updateViews();
                    //chartsTools.updateChartsData(count++, percent);
                    break;
                default:
                    Toast.makeText(context, msg.obj.toString(),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }


        ;
    };


    @Nullable @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_circular_loader, null);
        context = getActivity();
        ButterKnife.bind(this, view);
        initViews();
        setTimeTask();
        //addListener();
        return view;
    }


    public void updateViews() {
        // TODO Auto-generated method stub
        //textView.setText("已用内存:\n" + (sum - available) + "MB");
        //textView2.setText("可用内存:\n" + available + "MB");
        //textView3.setText("总内存:\n" + sum + "MB");
        mTextView.setText(percent + "%");
        mTextView2.setText("已用:" + (sum - available) + "M/" + sum + "M");
        mCircularFillableLoaders.setProgress((int) (100 - percent));
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

    }


    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
