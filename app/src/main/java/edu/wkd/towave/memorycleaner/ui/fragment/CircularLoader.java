package edu.wkd.towave.memorycleaner.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import com.mikhaellopez.circularfillableloaders.CircularFillableLoaders;
import edu.wkd.towave.memorycleaner.R;
import edu.wkd.towave.memorycleaner.adapter.base.BaseRecyclerViewAdapter;
import edu.wkd.towave.memorycleaner.model.Menu;
import edu.wkd.towave.memorycleaner.mvp.presenters.Presenter;
import edu.wkd.towave.memorycleaner.mvp.presenters.impl.fragment.CircularLoaderPresenter;
import edu.wkd.towave.memorycleaner.adapter.MenuListAdapter;
import edu.wkd.towave.memorycleaner.mvp.views.impl.fragment.CircularLoaderView;
import edu.wkd.towave.memorycleaner.ui.fragment.base.BaseFragment;
import java.util.ArrayList;
import javax.inject.Inject;

/**
 * Created by Administrator on 2016/4/21.
 */
public class CircularLoader extends BaseFragment implements CircularLoaderView {

    @Bind(R.id.circularFillableLoaders) CircularFillableLoaders
            mCircularFillableLoaders;
    @Bind(R.id.percent) TextView mTextView;
    @Bind(R.id.number) TextView mTextView2;
    @Bind(R.id.recyclerView) RecyclerView recyclerView;

    @Inject CircularLoaderPresenter mCircularLoaderPresenter;

    private MenuListAdapter recyclerAdapter;


    @Nullable @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override protected int getLayoutView() {
        return R.layout.fragment_circular_loader;
    }


    @Override protected Presenter getPresenter() {
        return mCircularLoaderPresenter;
    }


    @Override protected void initializeDependencyInjector() {
        super.initializeDependencyInjector();
        mBuilder.inject(this);
    }


    @Override public void initViews(ArrayList<Menu> menus, Context context) {
        recyclerAdapter = new MenuListAdapter(menus, context);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,
                LinearLayoutManager.VERTICAL));
        recyclerView.setHasFixedSize(true);
        recyclerAdapter.setOnInViewClickListener(R.id.card_item_root,
                new BaseRecyclerViewAdapter.onInternalClickListenerImpl<Menu>() {
                    @Override
                    public void OnClickListener(View parentV, View v, Integer
                            position, Menu values) {
                        super.OnClickListener(parentV, v, position, values);
                        mCircularLoaderPresenter.onRecyclerViewItemClick(position, values);
                    }
                });
        //recyclerAdapter.setOnInViewClickListener(R.id.note_more,
        //        new BaseRecyclerViewAdapter.onInternalClickListenerImpl<SNote>() {
        //            @Override
        //            public void OnClickListener(View parentV, View v, Integer position, SNote values) {
        //                super.OnClickListener(parentV, v, position, values);
        //                mainPresenter.showPopMenu(v, position, values);
        //            }
        //        });
        recyclerAdapter.setFirstOnly(false);
        recyclerAdapter.setDuration(300);
        recyclerView.setAdapter(recyclerAdapter);
    }


    @Override public void updateViews(long sum, long available, float percent) {
        mTextView.setText(percent + "%");
        mTextView2.setText("已用:" + (sum - available) + "M/" + sum + "M");
        mCircularFillableLoaders.setProgress((int) (100 - percent));
    }
}
