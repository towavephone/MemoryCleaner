package edu.wkd.towave.memorycleaner.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.wkd.towave.memorycleaner.R;
import edu.wkd.towave.memorycleaner.adapter.base.BaseFragmentPageAdapter;
import edu.wkd.towave.memorycleaner.ui.fragment.SystemApps;
import edu.wkd.towave.memorycleaner.ui.fragment.UserApps;
import java.util.ArrayList;

public class AppManage extends AppCompatActivity {

    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.tabs) TabLayout mTabs;
    @Bind(R.id.container) ViewPager mContainer;
    @Bind(R.id.fab) FloatingActionButton mFab;

    BaseFragmentPageAdapter mCommonFragmentPageAdapter;
    Snackbar snackbar;
    ArrayList<Fragment> items;
    Context context;
    ArrayList<String> titles;


    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_clean);
        context = getApplicationContext();
        //初始化view
        initViews();
        //loadData();
        addListener();
    }


    public void initViews() {
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        snackbar = Snackbar.make(mFab, "Replace with your own action",
                Snackbar.LENGTH_LONG).setAction("Action", null);
    }


    public void addListener() {
        items = new ArrayList<>();
        items.add(new UserApps());
        items.add(new SystemApps());

        titles = new ArrayList<>();
        titles.add("正在运行");
        titles.add("开机自启");
        mCommonFragmentPageAdapter = new BaseFragmentPageAdapter(
                getSupportFragmentManager(), items, titles);

        mContainer.setAdapter(mCommonFragmentPageAdapter);

        for (int i = 0; i < mCommonFragmentPageAdapter.getCount(); i++) {
            mTabs.addTab(mTabs.newTab().setText(titles.get(i)));
        }
        mTabs.setupWithViewPager(mContainer);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                finish();
            }
        });

        mToolbar.setOnMenuItemClickListener(
                new Toolbar.OnMenuItemClickListener() {
                    @Override public boolean onMenuItemClick(MenuItem item) {
                        return false;
                    }
                });
    }


    @Override public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_memory_clean, menu);
        return true;
    }


    @Override public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @OnClick(R.id.fab) public void show() {
        snackbar.show();
    }
}
