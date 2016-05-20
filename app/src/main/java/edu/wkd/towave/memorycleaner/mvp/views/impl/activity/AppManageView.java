package edu.wkd.towave.memorycleaner.mvp.views.impl.activity;

import android.support.v4.app.Fragment;
import edu.wkd.towave.memorycleaner.mvp.views.View;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/5/4.
 */
public interface AppManageView extends View {
    void initViews(ArrayList<Fragment> items, ArrayList<String> titles);
}
