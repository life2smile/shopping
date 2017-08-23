package com.shopping.hanxiao.shopping.tablayout.toptab;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * Created by wenzhi on 17/6/19.
 */

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> mFragments;
    private List<String> mTabTitles;

    public ViewPagerAdapter(FragmentManager fragmentManager, Context context,
                            List<Fragment> fragmentList, List<String> tabTitles) {
        super(fragmentManager);
        this.mFragments = fragmentList;
        this.mTabTitles = tabTitles;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position % mFragments.size());
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabTitles.get(position % mTabTitles.size());
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }
}
