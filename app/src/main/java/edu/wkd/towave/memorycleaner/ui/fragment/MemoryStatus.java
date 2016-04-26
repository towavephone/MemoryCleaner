//package edu.wkd.towave.memorycleaner.ui.fragment;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.support.v4.app.Fragment;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import android.widget.Button;
//import android.widget.TextView;
//import android.widget.Toast;
//import butterknife.Bind;
//import butterknife.ButterKnife;
//import edu.wkd.towave.memorycleaner.R;
//import edu.wkd.towave.memorycleaner.tools.ChartsTools;
//import edu.wkd.towave.memorycleaner.tools.MemoryUsedMessage;
//import java.util.Timer;
//import java.util.TimerTask;
//
///**
// * A simple {@link Fragment} subclass.
// * Activities that contain this fragment must implement the
// * {@link MemoryStatus.OnFragmentInteractionListener} interface
// * to handle interaction events.
// * Use the {@link MemoryStatus#newInstance} factory method to
// * create an instance of this fragment.
// */
//public class MemoryStatus extends Fragment {
//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//
//    //@Bind(R.id.textview1) TextView textView;
//    //@Bind(R.id.textview2) TextView textView2;
//    //@Bind(R.id.textview3) TextView textView3;
//    @Bind(R.id.clear_memory_button) Button button;
//
//    View view;
//    Context context;
//    int count;
//    long sum, available;
//    float percent;
//    int status = IS_NORMAL;
//    public static final int IS_CLEANING = 100;
//    public static final int IS_NORMAL = 101;
//    public static final int IS_CLEAN_FINISH = 102;
//    private ChartsTools chartsTools;
//
//    private Handler mh = new Handler() {
//        @SuppressLint("HandlerLeak") public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            int msgId = msg.what;
//            switch (msgId) {
//                case IS_NORMAL:
//                    updateViews();
//                    //chartsTools.updateChartsData(count++, percent);
//                    break;
//                default:
//                    Toast.makeText(context, msg.obj.toString(),
//                            Toast.LENGTH_SHORT).show();
//            }
//        }
//
//
//        ;
//    };
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        //view = inflater.inflate(R.layout.activity_memory_status, null);
//        context = getActivity();
//        ButterKnife.bind(this,view);
//        initViews();
//        setTimeTask();
//        addListener();
//        return view;
//    }
//
//    public void addListener() {
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                switchStatusByClick();
//            }
//        });
//    }
//
//    private void switchStatusByClick() {
//        if (status == IS_NORMAL) {
//            status = IS_CLEANING;
//            switchStatus();
//        } else if (status == IS_CLEAN_FINISH) {
//            status = IS_NORMAL;
//        }
//    }
//
//    private void switchStatus() {
//        // TODO Auto-generated method stub
//        switch (status) {
//            case IS_CLEANING:
//                button.setText("正在清理");
//                //chartsTools.startAnimation();
//                button.setClickable(false);
//                //clearMemory();
//                break;
//            case IS_NORMAL:
//                button.setText(percent + "%\n一键清理");// 已用内存
//                break;
//            case IS_CLEAN_FINISH:
//                button.setText("清理完成");
//                //chartsTools.clearAnimation();
//                button.performClick();
//                // Main a = (Main) getActivity();
//                // a.getAdapter().reLoad();
//                break;
//            default:
//                break;
//        }
//    }
//
//    //private void clearMemory() {
//    //    // TODO Auto-generated method stub
//    //    ThreadPool.getInstance().AddThread(new Runnable() {
//    //
//    //        @Override
//    //        public void run() {
//    //            ArrayList<MPrograme> list = ProgramManager.getInstance()
//    //                                                      .iskill(AutoClean.getInstance().onkeyclean());
//    //            if (list.size() != 0)
//    //                LogWriter.getInstance().writeLog("一键清理", list);
//    //            Message m = Message.obtain();
//    //            m.what = IS_CLEAN_FINISH;
//    //            mh.sendMessage(m);
//    //        }
//    //    });
//    //
//    //}
//
//    public void updateTextViews() {
//        // TODO Auto-generated method stub
//        //textView.setText("已用内存:\n" + (sum - available) + "MB");
//        //textView2.setText("可用内存:\n" + available + "MB");
//        //textView3.setText("总内存:\n" + sum + "MB");
//        if (status == IS_NORMAL) {
//            switchStatus();
//        }
//    }
//
//    private void setTimeTask() {
//        new Timer().schedule(new TimerTask() {
//            @Override
//            public void run() {
//                Message message = new Message();
//                try {
//                    sum = MemoryUsedMessage.getTotalMemory();
//                    available = MemoryUsedMessage.getAvailMemory(context);
//                    percent = MemoryUsedMessage.getPercent(context);
//                    message.what = IS_NORMAL;
//                    mh.sendMessage(message);
//                } catch (Exception e) {
//                    message.what = 3;
//                    mh.sendMessage(message);
//                }
//            }
//        }, 0, 1000);
//    }
//
//    private void initViews() {
//        count = 0;
//        chartsTools=new ChartsTools(context,view);
//    }
//
//    @Override public void onDestroyView() {
//        super.onDestroyView();
//        ButterKnife.unbind(this);
//    }
//}
