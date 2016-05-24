package edu.wkd.towave.memorycleaner.ui.fragment;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.ListView;
import com.jenzz.materialpreference.CheckBoxPreference;
import com.jenzz.materialpreference.Preference;
import edu.wkd.towave.memorycleaner.R;
import edu.wkd.towave.memorycleaner.adapter.ColorsListAdapter;
import edu.wkd.towave.memorycleaner.injector.component.DaggerFragmentComponent;
import edu.wkd.towave.memorycleaner.injector.module.FragmentModule;
import edu.wkd.towave.memorycleaner.mvp.presenters.impl.fragment.SettingPresenter;
import edu.wkd.towave.memorycleaner.mvp.views.impl.fragment.SettingView;
import edu.wkd.towave.memorycleaner.tools.DialogUtils;
import edu.wkd.towave.memorycleaner.tools.SnackbarUtils;
import edu.wkd.towave.memorycleaner.tools.ThemeUtils;
import edu.wkd.towave.memorycleaner.ui.activity.SettingActivity;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends PreferenceFragment implements SettingView {
    public static final String PREFERENCE_FILE_NAME = "memorycleaner.setting";

    private SettingActivity activity;
    private CheckBoxPreference showStartActivityPreference;
    //private Preference changeThemePreference;

    @Inject SettingPresenter mSettingPresenter;


    public SettingFragment() {
        super();
    }


    public static SettingFragment newInstance() {
        SettingFragment fragment = new SettingFragment();
        return fragment;
    }


    @Override public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (getActivity() != null && getActivity() instanceof SettingActivity) {
            this.activity = (SettingActivity) getActivity();
        }
    }


    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeDependencyInjector();
        addPreferencesFromResource(R.xml.prefs);
        getPreferenceManager().setSharedPreferencesName(PREFERENCE_FILE_NAME);
        initializePresenter();
        mSettingPresenter.onCreate(savedInstanceState);
    }


    @Override public void onStart() {
        mSettingPresenter.onStart();
        super.onStart();
    }


    @Override public void onStop() {
        mSettingPresenter.onStop();
        super.onStop();
    }


    private void initializePresenter() {
        mSettingPresenter.attachView(this);
    }


    @Override public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSettingPresenter.onViewCreated(view);
    }


    private void initializeDependencyInjector() {
        DaggerFragmentComponent.builder()
                               .fragmentModule(new FragmentModule())
                               .activityComponent(
                                       activity.getActivityComponent())
                               .build()
                               .inject(this);
    }


    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, android.preference.Preference preference) {
        mSettingPresenter.onPreferenceTreeClick(preference);
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }


    @Override public void onDestroy() {
        mSettingPresenter.onDestroy();
        super.onDestroy();
    }


    @Override public void findPreference() {
        showStartActivityPreference = (CheckBoxPreference) findPreference(
                getString(R.string.show_start_activity_key));
        //changeThemePreference = (Preference) findPreference(
        //        getString(R.string.change_theme_key));
    }


    @Override public void initPreferenceListView(View view) {
        ListView listView = (ListView) view.findViewById(android.R.id.list);
        listView.setHorizontalScrollBarEnabled(false);
        listView.setVerticalScrollBarEnabled(false);
        listView.setDivider(
                new ColorDrawable(getResources().getColor(R.color.grey)));
        listView.setDividerHeight((int) getResources().getDimension(
                R.dimen.preference_divider_height));
        listView.setFooterDividersEnabled(false);
        listView.setHeaderDividersEnabled(false);
    }


    @Override public void showThemeChooseDialog() {
        AlertDialog.Builder builder = DialogUtils.makeDialogBuilder(activity);
        builder.setTitle(R.string.change_theme);
        Integer[] res = new Integer[] { R.drawable.deep_purple_round,
                R.drawable.brown_round, R.drawable.blue_round,
                R.drawable.blue_grey_round, R.drawable.yellow_round,
                R.drawable.red_round, R.drawable.pink_round,
                R.drawable.green_round };
        List<Integer> list = Arrays.asList(res);
        ColorsListAdapter adapter = new ColorsListAdapter(getActivity(), list);
        adapter.setCheckItem(
                ThemeUtils.getCurrentTheme(activity).getIntValue());
        GridView gridView = (GridView) LayoutInflater.from(activity)
                                                     .inflate(
                                                             R.layout.colors_panel_layout,
                                                             null);
        gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        gridView.setCacheColorHint(0);
        gridView.setAdapter(adapter);
        builder.setView(gridView);
        final AlertDialog dialog = builder.show();
        gridView.setOnItemClickListener((parent, view, position, id) -> {
            dialog.dismiss();
            mSettingPresenter.onThemeChoose(position);
        });
    }


    @Override public void showSnackbar(String message) {
        if (activity != null) SnackbarUtils.show(activity, message);
    }


    @Override public boolean isResume() {
        return isResumed();
    }


    @Override public void reload() {
        if (activity != null) {
            activity.reload(false);
        }
    }


    @Override public void setShowStartActivityChecked(boolean checked) {
        showStartActivityPreference.setChecked(checked);
    }
}
