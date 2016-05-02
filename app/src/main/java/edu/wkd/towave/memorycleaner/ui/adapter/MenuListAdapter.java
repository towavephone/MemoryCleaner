package edu.wkd.towave.memorycleaner.ui.adapter;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import edu.wkd.towave.memorycleaner.R;
import edu.wkd.towave.memorycleaner.model.Menu;
import edu.wkd.towave.memorycleaner.ui.adapter.base.BaseRecyclerViewAdapter;
import java.util.List;

/**
 * Created by Administrator on 2016/5/2.
 */
public class MenuListAdapter extends BaseRecyclerViewAdapter<Menu> {

    private Context mContext;

    public MenuListAdapter(List<Menu> list) {
        super(list);
    }

    public MenuListAdapter(List<Menu> list, Context context) {
        super(list, context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        final View view = LayoutInflater.from(mContext)
                                        .inflate(R.layout.item_card_view,
                                                parent, false);
        return new MenuItemViewHolder(view);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        super.onBindViewHolder(viewHolder, position);
        MenuItemViewHolder holder = (MenuItemViewHolder) viewHolder;
        Menu menu = list.get(position);
        if (menu == null) return;
        holder.setIcon(menu.getIcon());
        holder.setContent(menu.getContent());
        animate(viewHolder, position);
    }


    @Override protected Animator[] getAnimators(View view) {
        if (view.getMeasuredHeight() <= 0) {
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX",
                    1.05f, 1.0f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY",
                    1.05f, 1.0f);
            return new ObjectAnimator[] { scaleX, scaleY };
        }
        return new Animator[] {
                ObjectAnimator.ofFloat(view, "scaleX", 1.05f, 1.0f),
                ObjectAnimator.ofFloat(view, "scaleY", 1.05f, 1.0f), };
    }
}
