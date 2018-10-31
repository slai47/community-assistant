package com.half.wowsca.ui.adapter.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.half.wowsca.ui.compare.ShipCompareDifFragment;
import com.half.wowsca.ui.compare.ShipCompareGraphFragment;
import com.half.wowsca.ui.compare.ShipModuleListFragment;

/**
 * Created by slai47 on 3/12/2017.
 */

public class ShipComparePager extends FragmentPagerAdapter {

    public ShipComparePager(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment frag = null;
        if (position == 0){
            frag = new ShipModuleListFragment();
        } else if(position == 1){
            frag = new ShipCompareGraphFragment();
        } else {
            frag = new ShipCompareDifFragment();
        }
        return frag;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return null;
    }
}