package com.half.wowsca.ui.adapter.pager;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.half.wowsca.ui.encyclopedia.tabs.CaptainSkillsFragment;
import com.half.wowsca.ui.encyclopedia.tabs.EncyclopediaShipsFragment;
import com.half.wowsca.ui.encyclopedia.tabs.FlagsFragment;
import com.half.wowsca.ui.encyclopedia.tabs.GraphsStatsFragment;
import com.half.wowsca.ui.encyclopedia.tabs.UpgradesFragment;

/**
 * Created by slai4 on 12/1/2015.
 */
public class ShipopediaPager extends FragmentPagerAdapter {

    public ShipopediaPager(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment f = null;
        switch (position) {
            case 0:
                f = new EncyclopediaShipsFragment();
                break;
            case 1:
                f = new GraphsStatsFragment();
                break;
            case 2:
                f = new CaptainSkillsFragment();
                break;
            case 3:
                f = new FlagsFragment();
                break;
            case 4:
                f = new UpgradesFragment();
                break;
        }
        return f;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        switch (position) {
            case 0:
                title = "Encyclopedia";
                break;
            case 1:
                title = "Ship Stats";
                break;
            case 2:
                title = "Captain Skills";
                break;
            case 3:
                title = "Flags";
                break;
            case 4:
                title = "Upgrades";
                break;
        }
        return title;
    }

    @Override
    public int getCount() {
        return 5;
    }
}