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
import edu.wkd.towave.memorycleaner.mvp.views.impl.fragment.CircularLoaderView;
import edu.wkd.towave.memorycleaner.tools.MemoryUsedMessage;
import edu.wkd.towave.memorycleaner.tools.T;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import javax.inject.Inject;

/**
 * Created by towave on 2016/5/10.
 */
public class CircularLoaderPresenter implements Presenter {

    private CircularLoaderView mCircularLoaderView;
    private final Context mContext;
    private boolean isCardLayout = false;
    private long sum, available;
    private float percent;
    private int status = IS_NORMAL;
    private static final int IS_NORMAL = 101;


    @Inject
    public CircularLoaderPresenter(
            @ContextLifeCycle("Activity") Context context) {
        this.mContext = context;
        //this.mPreferenceUtils = preferenceUtils;
    }


    @Override public void attachView(View v) {
        this.mCircularLoaderView = (CircularLoaderView) v;
    }


    @Override public void onCreate(Bundle savedInstanceState) {
        initViews();
        setTimeTask();
    }


    private void initViews() {
        ArrayList<Menu> menus = new ArrayList<>();
        menus.add(new Menu.Builder(mContext).content("内存加速")
                                            .icon(R.drawable.card_icon_speedup)
                                            .build());
        menus.add(new Menu.Builder(mContext).content("垃圾清理")
                                            .icon(R.drawable.card_icon_trash)
                                            .build());
        menus.add(new Menu.Builder(mContext).content("自启管理")
                                            .icon(R.drawable.card_icon_autorun)
                                            .build());
        menus.add(new Menu.Builder(mContext).content("软件管理")
                                            .icon(R.drawable.card_icon_media)
                                            .build());
        mCircularLoaderView.initViews(menus, mContext);
    }


    private Handler mHandler = new Handler() {
        @SuppressLint("HandlerLeak") public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case IS_NORMAL:
                    mCircularLoaderView.updateViews(sum, available, percent);
                    break;
                default:
                    T.showShort(mContext, msg.obj.toString());
                    break;
            }
        }
    };


    public void setTimeTask() {
        new Timer().schedule(new TimerTask() {
            @Override public void run() {
                Message msg = Message.obtain();
                try {
                    sum = MemoryUsedMessage.getTotalMemory();
                    available = MemoryUsedMessage.getAvailMemory(mContext);
                    percent = MemoryUsedMessage.getPercent(mContext);
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
}
