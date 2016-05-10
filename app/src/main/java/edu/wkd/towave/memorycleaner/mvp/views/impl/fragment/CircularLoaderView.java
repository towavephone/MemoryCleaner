package edu.wkd.towave.memorycleaner.mvp.views.impl.fragment;

import android.content.Context;
import edu.wkd.towave.memorycleaner.model.Menu;
import edu.wkd.towave.memorycleaner.mvp.views.View;
import java.util.ArrayList;

/**
 * Created by towave on 2016/5/10.
 */
public interface CircularLoaderView extends View{
    //private long sum, available;
    //private float percent;
    void initViews(ArrayList<Menu> menus, Context context);
    void updateViews(long sum,long available,float percent);
}
