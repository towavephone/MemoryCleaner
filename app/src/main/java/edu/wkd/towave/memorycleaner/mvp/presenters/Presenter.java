package edu.wkd.towave.memorycleaner.mvp.presenters;

import android.os.Bundle;
import edu.wkd.towave.memorycleaner.mvp.views.View;

/**
 * Created by Administrator on 2016/5/4.
 */
public interface Presenter {
    void onCreate(Bundle savedInstanceState);

    void onResume();

    void onStart();

    void onPause();

    void onStop();

    void onDestroy();

    void attachView(View v);
}
