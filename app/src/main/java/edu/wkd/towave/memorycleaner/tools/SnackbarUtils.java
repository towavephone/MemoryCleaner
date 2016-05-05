package edu.wkd.towave.memorycleaner.tools;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * Created by towave on 2016/5/5.
 */
public class SnackbarUtils {
    public static final int DURATION = Snackbar.LENGTH_LONG / 2;


    public static void show(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
    }


    public static void show(Activity activity, String message) {
        View view = activity.getWindow().getDecorView();
        show(view, message);
    }


    public static void showAction(View view, String message, String action, View
            .OnClickListener listener) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
                .setAction(action, listener)
                .show();
    }


    public static void showAction(Activity activity, String message, String action, View.OnClickListener listener) {
        View view = activity.getWindow().getDecorView();
        showAction(view, message, action, listener);
    }
}
