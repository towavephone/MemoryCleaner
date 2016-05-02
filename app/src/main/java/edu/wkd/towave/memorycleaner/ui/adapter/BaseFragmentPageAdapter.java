package edu.wkd.towave.memorycleaner.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/4/21.
 */
public class BaseFragmentPageAdapter extends FragmentPagerAdapter {
    ArrayList<Fragment> items;
    FragmentManager fm;
    ArrayList<String> titleList;


    public BaseFragmentPageAdapter(FragmentManager fm, ArrayList<Fragment> items) {
        super(fm);
        // TODO Auto-generated constructor stub
        this.items = items;
        this.fm = fm;
    }


    public BaseFragmentPageAdapter(FragmentManager fm, ArrayList<Fragment> items, ArrayList<String> titleList) {
        super(fm);
        // TODO Auto-generated constructor stub
        this.items = items;
        this.fm = fm;
        this.titleList = titleList;
    }


    @Override public Fragment getItem(int arg0) {
        // TODO Auto-generated method stub
        return items.get(arg0);
    }


    @Override public int getCount() {
        // TODO Auto-generated method stub
        return items.size();
    }


    @Override public int getItemPosition(Object object) {
        // TODO Auto-generated method stub
        return super.getItemPosition(object);
    }


    @Override public CharSequence getPageTitle(int position) {
        if (titleList != null && titleList.size() > 0) {
            return titleList.get(position % titleList.size());
        }
        return "";
    }
}
