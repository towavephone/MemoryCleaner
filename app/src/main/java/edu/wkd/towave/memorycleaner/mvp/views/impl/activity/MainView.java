package edu.wkd.towave.memorycleaner.mvp.views.impl.activity;

import edu.wkd.towave.memorycleaner.mvp.views.View;

/**
 * Created by Administrator on 2016/5/4.
 */
public interface MainView extends View{
    void initToolbar();
    void initDrawerView();
    void initViewPager();
    void showSnackbar();
}
