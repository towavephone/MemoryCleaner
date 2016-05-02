package edu.wkd.towave.memorycleaner.ui.adapter;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import edu.wkd.towave.memorycleaner.R;

/**
 * Created by Administrator on 2016/5/2.
 */
public class MenuItemViewHolder extends RecyclerView.ViewHolder {
    private final ImageView mImageView;
    private final TextView mTextView;


    public MenuItemViewHolder(View parent) {
        super(parent);
        mImageView = (ImageView) parent.findViewById(R.id.icon);
        mTextView = (TextView) parent.findViewById(R.id.content);
    }


    public void setIcon(Drawable icon) {
        mImageView.setImageDrawable(icon);
    }


    public void setContent(String content) {
        mTextView.setText(content);
    }
}
