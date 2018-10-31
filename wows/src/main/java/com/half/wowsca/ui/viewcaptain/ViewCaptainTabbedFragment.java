package com.half.wowsca.ui.viewcaptain;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.half.wowsca.CAApp;
import com.half.wowsca.R;
import com.half.wowsca.ui.adapter.pager.ViewCaptainPager;
import com.utilities.views.SlidingTabLayout;

/**
 * A placeholder fragment containing a simple view.
 */
public class ViewCaptainTabbedFragment extends Fragment {

    private ViewPager mViewPager;
    private SlidingTabLayout pagerTabs;
    private ViewCaptainPager pager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_captain, container, false);
        bindView(view);
        return view;
    }

    private void bindView(View view) {
        //set up pager and info
        mViewPager = (ViewPager) view.findViewById(R.id.fragment_view_captain_tabbed_pager);
        pagerTabs = (SlidingTabLayout) view.findViewById(R.id.fragment_view_captain_pager_tab);
        Integer[] iconResourceArray = {R.drawable.ic_captain,
                R.drawable.ic_stats, R.drawable.ic_trophy, R.drawable.ic_star, R.drawable.ic_medal,
                R.drawable.ic_ship};
        pagerTabs.setIconResourceArray(iconResourceArray);
        pagerTabs.setIconTintColor(0);

        int indicatorColor = R.color.selected_tab_color;
        if(!CAApp.isOceanTheme(view.getContext()))
            indicatorColor = R.color.top_background;

        pagerTabs.setSelectedIndicatorColors(ContextCompat.getColor(getContext(), indicatorColor));
        pagerTabs.setDistributeEvenly(true);
        pagerTabs.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        pager = new ViewCaptainPager(getChildFragmentManager());
        mViewPager.setAdapter(pager);
        pagerTabs.setViewPager(mViewPager);
    }

    public void fix(){
        if(mViewPager != null){
            int item = mViewPager.getCurrentItem();
            pager = new ViewCaptainPager(getChildFragmentManager());
            mViewPager.setAdapter(pager);
            mViewPager.setCurrentItem(item);
        }
    }
}
