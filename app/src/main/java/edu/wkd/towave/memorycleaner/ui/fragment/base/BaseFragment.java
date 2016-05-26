package edu.wkd.towave.memorycleaner.ui.fragment.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import edu.wkd.towave.memorycleaner.injector.component.DaggerFragmentComponent;
import edu.wkd.towave.memorycleaner.injector.component.FragmentComponent;
import edu.wkd.towave.memorycleaner.injector.module.FragmentModule;
import edu.wkd.towave.memorycleaner.mvp.presenters.Presenter;
import edu.wkd.towave.memorycleaner.ui.activity.base.BaseActivity;

/**
 * Created by towave on 2016/5/14.
 */
public abstract class BaseFragment extends Fragment
        implements edu.wkd.towave.memorycleaner.mvp.views.View {
    private BaseActivity activity;
    protected FragmentComponent mBuilder;


    @Nullable @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutView(), null);
        initializeDependencyInjector();
        initializePresenter();
        ButterKnife.bind(this, view);
        getPresenter().onCreate(savedInstanceState);
        return view;
    }


    public int getColorPrimary() {
        return activity.getColorPrimary();
    }


    protected void initializeDependencyInjector() {
        mBuilder = DaggerFragmentComponent.builder()
                                          .fragmentModule(new FragmentModule())
                                          .activityComponent(
                                                  activity.getActivityComponent())
                                          .build();
    }


    //在Fragment绑定中，对Fragment中的Activity成员变量进行初始化，防止每次调用getActivity造成性能损失。
    @Override public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (getActivity() != null) {
            this.activity = (BaseActivity) getActivity();
        }
    }


    protected abstract @LayoutRes int getLayoutView();

    protected abstract Presenter getPresenter();


    private void initializePresenter() {
        getPresenter().attachView(this);
    }


    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
