package edu.wkd.towave.memorycleaner.tools;

import android.app.Activity;
import android.content.Context;
import edu.wkd.towave.memorycleaner.R;

/**
 * Created by Administrator on 2016/5/5.
 */
public class ThemeUtils {
    public static void changeTheme(Activity activity, Theme theme) {
        if (activity == null) return;
        int style = R.style.DeepPurpleTheme;
        switch (theme) {
            case BROWN:
                style = R.style.BrownTheme;
                break;
            case BLUE:
                style = R.style.BlueTheme;
                break;
            case BLUE_GREY:
                style = R.style.BlueGreyTheme;
                break;
            case YELLOW:
                style = R.style.YellowTheme;
                break;
            case RED:
                style = R.style.RedTheme;
                break;
            case PINK:
                style = R.style.PinkTheme;
                break;
            case GREEN:
                style = R.style.GreenTheme;
                break;
            default:
                break;
        }
        activity.setTheme(style);
    }


    public static Theme getCurrentTheme(Context context) {
        int value = PreferenceUtils.getInstance(context)
                                   .getIntParam(context.getString(
                                           R.string.change_theme_key), 0);
        return ThemeUtils.Theme.mapValueToTheme(value);
    }


    public enum Theme {
        DEEP_PURPLE(0x00),
        BROWN(0x01),
        BLUE(0x02),
        BLUE_GREY(0x03),
        YELLOW(0x04),
        RED(0x05),
        PINK(0x06),
        GREEN(0x07);

        private int mValue;


        Theme(int value) {
            this.mValue = value;
        }


        public static Theme mapValueToTheme(final int value) {
            for (Theme theme : Theme.values()) {
                if (value == theme.getIntValue()) {
                    return theme;
                }
            }
            // If run here, return default
            return DEEP_PURPLE;
        }


        static Theme getDefault() {
            return DEEP_PURPLE;
        }


        public int getIntValue() {
            return mValue;
        }
    }
}
