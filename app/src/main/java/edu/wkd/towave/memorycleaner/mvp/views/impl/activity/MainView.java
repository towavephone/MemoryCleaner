package edu.wkd.towave.memorycleaner.mvp.views.impl.activity;

import android.support.v4.app.Fragment;
import edu.wkd.towave.memorycleaner.mvp.views.View;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/5/4.
 */
public interface MainView extends View {
    void initToolbar();

    void initDrawerView();

    void initViewPager(ArrayList<Fragment> items);

    void showSnackbar();

    void reCreate();
}
