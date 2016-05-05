package edu.wkd.towave.memorycleaner.tools;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import edu.wkd.towave.memorycleaner.R;

/**
 * Created by towave on 2016/5/5.
 */
public class DialogUtils {
    public static AlertDialog.Builder makeDialogBuilderByTheme(Context context, ThemeUtils.Theme theme){
        AlertDialog.Builder builder;
        int style = R.style.RedDialogTheme;
        switch (theme){
            case BROWN:
                style = R.style.BrownDialogTheme;
                break;
            case BLUE:
                style = R.style.BlueDialogTheme;
                break;
            case BLUE_GREY:
                style = R.style.BlueGreyDialogTheme;
                break;
            case YELLOW:
                style = R.style.YellowDialogTheme;
                break;
            case DEEP_PURPLE:
                style = R.style.DeepPurpleDialogTheme;
                break;
            case PINK:
                style = R.style.PinkDialogTheme;
                break;
            case GREEN:
                style = R.style.GreenDialogTheme;
                break;
            default:
                break;
        }
        builder = new AlertDialog.Builder(context, style);
        return builder;
    }

    public static AlertDialog.Builder makeDialogBuilder(Context context){
        ThemeUtils.Theme theme = ThemeUtils.getCurrentTheme(context);
        return makeDialogBuilderByTheme(context, theme);
    }
}
