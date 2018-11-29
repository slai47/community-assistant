package com.half.wowsca.ui.adapter.pager;

import android.os.Parcelable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.half.wowsca.ui.viewcaptain.tabs.CaptainAchievementsFragment;
import com.half.wowsca.ui.viewcaptain.tabs.CaptainFragment;
import com.half.wowsca.ui.viewcaptain.tabs.CaptainGraphsFragment;
import com.half.wowsca.ui.viewcaptain.tabs.CaptainRankedFragment;
import com.half.wowsca.ui.viewcaptain.tabs.CaptainShipsFragment;
import com.half.wowsca.ui.viewcaptain.tabs.CaptainTopShipInfoFragment;

/**
 * Created by slai4 on 9/15/2015.
 */
public class ViewCaptainPager extends FragmentStatePagerAdapter {

    public ViewCaptainPager(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment f = null;
        switch (position) {
            case 0:
                f = new CaptainFragment();
                break;
            case 1:
                f = new CaptainGraphsFragment();
                break;
            case 2:
                f = new CaptainTopShipInfoFragment();
                break;
            case 3:
                f = new CaptainRankedFragment();
                break;
            case 4:
                f = new CaptainAchievementsFragment();
                break;
            case 5:
                f = new CaptainShipsFragment();
                break;
        }
        return f;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        switch (position) {
            case 0:
                title = "Overall";
                break;
            case 1:
                title = "Top Ship Info";
                break;
            case 2:
                title = "Details";
                break;
            case 3:
                title = "Ranked & Leaderboards";
                break;
            case 4:
                title = "Medals";
                break;
            case 5:
                title = "Ships";
                break;
        }
        return title;
    }

    @Override
    public int getCount() {
        return 6;
    }

    @Override
    public Parcelable saveState() {
        return null;
    }
}
