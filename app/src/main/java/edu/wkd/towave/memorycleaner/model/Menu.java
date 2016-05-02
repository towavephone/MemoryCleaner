package edu.wkd.towave.memorycleaner.model;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;

/**
 * Created by Administrator on 2016/5/2.
 */
public class Menu {
    private Builder mBuilder;

    private Menu(Builder builder) {
        mBuilder = builder;
    }

    public Drawable getIcon() {
        return mBuilder.mIcon;
    }

    public String getContent() {
        return mBuilder.mContent;
    }

    public static class Builder {

        private Context mContext;
        protected Drawable mIcon;
        protected String mContent;

        public Builder(Context context) {
            mContext = context;
        }

        public Builder icon(Drawable icon) {
            this.mIcon = icon;
            return this;
        }

        public Builder icon(@DrawableRes int iconRes) {
            if (iconRes == 0)
                return this;
            return icon(ContextCompat.getDrawable(mContext, iconRes));
        }

        public Builder content(String content) {
            this.mContent = content;
            return this;
        }

        public Builder content(@StringRes int contentRes) {
            return content(mContext.getString(contentRes));
        }

        public Menu build() {
            return new Menu(this);
        }
    }

    @Override
    public String toString() {
        if (getContent() != null)
            return getContent().toString();
        else return "(no content)";
    }
}
