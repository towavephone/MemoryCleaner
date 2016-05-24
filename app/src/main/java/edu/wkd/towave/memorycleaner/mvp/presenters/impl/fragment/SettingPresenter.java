package edu.wkd.towave.memorycleaner.mvp.presenters.impl.fragment;

import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import edu.wkd.towave.memorycleaner.R;
import edu.wkd.towave.memorycleaner.injector.ContextLifeCycle;
import edu.wkd.towave.memorycleaner.mvp.presenters.Presenter;
import edu.wkd.towave.memorycleaner.mvp.presenters.impl.activity.MainPresenter;
import edu.wkd.towave.memorycleaner.mvp.views.View;
import edu.wkd.towave.memorycleaner.mvp.views.impl.fragment.SettingView;
import edu.wkd.towave.memorycleaner.tools.PreferenceUtils;
import edu.wkd.towave.memorycleaner.tools.ThemeUtils;
import javax.inject.Inject;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by Administrator on 2016/5/5.
 */
public class SettingPresenter implements Presenter {

    private SettingView mSettingView;
    private final Context mContext;
    private boolean isShowStartActivity = false;
    private PreferenceUtils mPreferenceUtils;
    private MainPresenter.NotifyEvent<Void> event;

    @Inject
    public SettingPresenter(@ContextLifeCycle("Activity")
                            Context context, PreferenceUtils mPreferenceUtils) {
        this.mContext = context;
        this.mPreferenceUtils = mPreferenceUtils;
    }


    @Override public void onCreate(Bundle savedInstanceState) {
        mSettingView.findPreference();
        initPreference();
        EventBus.getDefault().register(this);
    }


    void initPreference() {
        isShowStartActivity = mPreferenceUtils.getBooleanParam(
                getString(mContext, R.string.show_start_activity_key));
        mSettingView.setShowStartActivityChecked(isShowStartActivity);
    }


    @Override public void onResume() {

    }


    public boolean onPreferenceTreeClick(Preference preference) {
        if (mSettingView.isResume() && preference == null) {
            return false;
        }
        String key = preference.getKey();
        if (TextUtils.equals(key,
                getString(mContext, R.string.show_start_activity_key))) {
            isShowStartActivity = !isShowStartActivity;
            mPreferenceUtils.saveParam(
                    getString(mContext, R.string.show_start_activity_key),
                    isShowStartActivity);
        }

        if (TextUtils.equals(key,
                getString(mContext, R.string.change_theme_key))) {
            mSettingView.showThemeChooseDialog();
        }

        return false;
    }


    public void onViewCreated(android.view.View v) {
        mSettingView.initPreferenceListView(v);
    }


    private String getString(Context context, @StringRes int string) {
        if (context != null) return context.getString(string);
        return "";
    }


    @Override public void onStart() {
        //EventBus.getDefault().register(this);
    }


    @Override public void onPause() {

    }


    @Override public void onStop() {
        //EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEventMainThread(Boolean result){
        //handleLoginResult(result);
    }

    @Override public void onDestroy() {
        if (event != null &&
                event.getType() != MainPresenter.NotifyEvent.CHANGE_THEME){
            EventBus.getDefault().post(event);
        }
        EventBus.getDefault().unregister(this);
    }


    public void onThemeChoose(int position) {
        int value = ThemeUtils.getCurrentTheme(mContext).getIntValue();
        if (value != position) {
            mPreferenceUtils.saveParam(
                    getString(mContext, R.string.change_theme_key), position);
            notifyChangeTheme();
        }
    }


    private void notifyChangeTheme() {
        if (event == null) {
            event = new MainPresenter.NotifyEvent<>();
        }
        event.setType(MainPresenter.NotifyEvent.CHANGE_THEME);
        //post change theme event immediately
        EventBus.getDefault().post(event);
        mSettingView.reload();
    }


    @Override public void attachView(View v) {
        mSettingView = (SettingView) v;
    }
}
