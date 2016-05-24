package edu.wkd.towave.memorycleaner.mvp.views.impl.fragment;

import edu.wkd.towave.memorycleaner.mvp.views.View;

/**
 * Created by towave on 2016/5/24.
 */
public interface SettingView extends View {
    void findPreference();

    void initPreferenceListView(android.view.View view);

    void showSnackbar(String message);

    void showThemeChooseDialog();

    boolean isResume();

    void reload();

    void setShowStartActivityChecked(boolean checked);
}
