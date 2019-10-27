package com.example.anany.restaurantleftoverfood.ui.main;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.anany.restaurantleftoverfood.FragmentAccountInfo;
import com.example.anany.restaurantleftoverfood.FragmentAppSettings;
import com.example.anany.restaurantleftoverfood.FragmentDonations;
import com.example.anany.restaurantleftoverfood.FragmentHome;
import com.example.anany.restaurantleftoverfood.FragmentList;
import com.example.anany.restaurantleftoverfood.FragmentMap;
import com.example.anany.restaurantleftoverfood.FragmentSettings;
import com.example.anany.restaurantleftoverfood.R;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2, R.string.tab_text_3, R.string.tab_text_5, R.string.tab_text_4};
    private final Context mContext;
    TabLayout tab;

    public SectionsPagerAdapter(Context context, FragmentManager fm, TabLayout tabs) {
        super(fm);
        mContext = context;
        tab = tabs;
    }

    static int clicks = 0;

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        //DONE Create new Fragment Classes with individual layouts.
        //DONE Use switch statement to check which tab is on. Then, return that fragment.
        Fragment frag;
        switch (position) {
            case 0:
                frag = FragmentHome.newInstance("hi", "Hi", mContext, tab);
                break;
            case 1:
                frag = FragmentList.newInstance("hi", "Hi", mContext);
                break;
            case 2:
                frag = FragmentMap.newInstance("ho", "ho", mContext);
                break;
            case 4:
                frag = FragmentSettings.newInstance("hi", "Hi", mContext, tab);
                break;
            case 5:
                frag = FragmentAppSettings.newInstance("hi", "Hi", mContext, tab);
                break;
            case 6:
                frag = FragmentAccountInfo.newInstance("hi", "Hi", mContext, tab);
                break;
            case 3:
                frag = FragmentDonations.newInstance("hi", "Hi", mContext);
                break;
            default:
                frag = PlaceholderFragment.newInstance(position + 1);
                break;
        }
        return frag;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        //INFO Show 5 total pages.
        return 5;
    }
}