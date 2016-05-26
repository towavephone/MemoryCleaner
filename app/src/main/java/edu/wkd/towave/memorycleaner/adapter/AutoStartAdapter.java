package edu.wkd.towave.memorycleaner.adapter;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import edu.wkd.towave.memorycleaner.R;
import edu.wkd.towave.memorycleaner.adapter.base.BaseRecyclerViewAdapter;
import edu.wkd.towave.memorycleaner.adapter.viewholder.ProcessItemViewHolder;
import edu.wkd.towave.memorycleaner.model.AutoStartInfo;
import java.util.List;

/**
 * Created by towave on 2016/5/21.
 */
public class AutoStartAdapter extends BaseRecyclerViewAdapter<AutoStartInfo> {
    private Context mContext;


    public AutoStartAdapter(List<AutoStartInfo> list) {
        super(list);
    }


    public AutoStartAdapter(List<AutoStartInfo> list, Context context) {
        super(list, context);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        final View view = LayoutInflater.from(mContext)
                                        .inflate(R.layout.item_list_view,
                                                parent, false);
        return new ProcessItemViewHolder(view);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        super.onBindViewHolder(viewHolder, position);
        ProcessItemViewHolder holder = (ProcessItemViewHolder) viewHolder;
        AutoStartInfo autoStartInfo = list.get(position);
        if (autoStartInfo == null) return;
        holder.setIcon(autoStartInfo.getIcon());
        holder.setName(autoStartInfo.getLabel());
        holder.setChecked(autoStartInfo.isEnable());
        holder.setMemoryVisible(false);
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
