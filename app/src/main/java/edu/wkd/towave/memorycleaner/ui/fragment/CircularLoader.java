package edu.wkd.towave.memorycleaner.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.mikhaellopez.circularfillableloaders.CircularFillableLoaders;
import edu.wkd.towave.memorycleaner.R;
import edu.wkd.towave.memorycleaner.model.Menu;
import edu.wkd.towave.memorycleaner.tools.MemoryUsedMessage;
import edu.wkd.towave.memorycleaner.ui.activity.MemoryClean;
import edu.wkd.towave.memorycleaner.ui.adapter.MenuListAdapter;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2016/4/21.
 */
public class CircularLoader extends Fragment {
    private View view;
    private Context context;
    @Bind(R.id.circularFillableLoaders) CircularFillableLoaders
            mCircularFillableLoaders;
    @Bind(R.id.percent) TextView mTextView;
    @Bind(R.id.number) TextView mTextView2;
    @Bind(R.id.recyclerView) RecyclerView recyclerView;

    private MenuListAdapter recyclerAdapter;
    private int status = IS_NORMAL;
    private static final int IS_NORMAL = 101;
    private long sum, available;
    private float percent;

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


    private void updateViews() {
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
        ArrayList<Menu> menus = new ArrayList<>();
        menus.add(new Menu.Builder(context).content("内存加速")
                                           .icon(R.drawable.card_icon_speedup)
                                           .build());
        menus.add(new Menu.Builder(context).content("垃圾清理")
                                           .icon(R.drawable.card_icon_trash)
                                           .build());
        menus.add(new Menu.Builder(context).content("自启管理")
                                           .icon(R.drawable.card_icon_autorun)
                                           .build());
        menus.add(new Menu.Builder(context).content("软件管理")
                                           .icon(R.drawable.card_icon_media)
                                           .build());
        recyclerAdapter = new MenuListAdapter(menus, context);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        recyclerView.setHasFixedSize(true);
        //recyclerAdapter.setOnInViewClickListener(R.id.notes_item_root,
        //        new BaseRecyclerViewAdapter.onInternalClickListenerImpl<SNote>() {
        //            @Override
        //            public void OnClickListener(View parentV, View v, Integer position, SNote values) {
        //                super.OnClickListener(parentV, v, position, values);
        //                mainPresenter.onRecyclerViewItemClick(position, values);
        //            }
        //        });
        //recyclerAdapter.setOnInViewClickListener(R.id.note_more,
        //        new BaseRecyclerViewAdapter.onInternalClickListenerImpl<SNote>() {
        //            @Override
        //            public void OnClickListener(View parentV, View v, Integer position, SNote values) {
        //                super.OnClickListener(parentV, v, position, values);
        //                mainPresenter.showPopMenu(v, position, values);
        //            }
        //        });
        recyclerAdapter.setFirstOnly(false);
        recyclerAdapter.setDuration(300);
        recyclerView.setAdapter(recyclerAdapter);
    }


    //@OnClick(R.id.card1) void speedUp() {
    //    startActivity(new Intent(context, MemoryClean.class));
    //}
    //
    //
    //@OnClick(R.id.card2) void rubbishClean() {
    //    //startActivity(RubbishCleanActivity.class);
    //}
    //
    //
    //@OnClick(R.id.card3) void AutoStartManage() {
    //    //startActivity(AutoStartManageActivity.class);
    //}
    //
    //
    //@OnClick(R.id.card4) void SoftwareManage() {
    //    //startActivity(SoftwareManageActivity.class);
    //}


    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
