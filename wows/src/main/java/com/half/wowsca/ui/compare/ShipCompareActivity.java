package com.half.wowsca.ui.compare;

import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.half.wowsca.CAApp;
import com.half.wowsca.R;
import com.half.wowsca.managers.CompareManager;
import com.half.wowsca.model.events.ProgressEvent;
import com.half.wowsca.model.result.ShipResult;
import com.half.wowsca.ui.CABaseActivity;
import com.half.wowsca.ui.adapter.pager.ShipComparePager;
import org.greenrobot.eventbus.Subscribe;
import com.utilities.logging.Dlog;
import com.utilities.views.SlidingTabLayout;

/**
 * Created by slai47 on 3/5/2017.
 */

public class ShipCompareActivity extends CABaseActivity {

    /**
     * this holds a pager of a ShipCompareDifFragment and the ship profiles
     */

    private SlidingTabLayout pagerTabs;
    private ViewPager mViewPager;
    private ShipComparePager pager;

    private View progress;

    private ShipResult lastUpdated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare_ships);
        bindView();

    }

    private void bindView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(R.string.ship_compare_title);

        progress = findViewById(R.id.activity_compare_ships_progress);
        pagerTabs = (SlidingTabLayout) findViewById(R.id.ship_compare_pager_tab);
        mViewPager = (ViewPager) findViewById(R.id.ship_compare_tabbed_pager);
    }

    @Override
    protected void onResume() {
        super.onResume();
        CAApp.getEventBus().register(this);
        initView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        CAApp.getEventBus().unregister(this);
    }

    private void initView() {
        if(CompareManager.getSHIPS().size() == 0){
            finish();
        } else {
            Dlog.d("initView", "getSHIPS = " + CompareManager.getSHIPS().size() + " si = " + CompareManager.getShipInformation().size());
            if(CompareManager.getSHIPS().size() != CompareManager.getShipInformation().size()){
                if(!CompareManager.GRABBING_INFO){
                    grabInfo();
                } else {
                }
            } else {
                CompareManager.GRABBING_INFO = false;
                progress.setVisibility(View.GONE);
                setupView();
            }
        }
    }

    private void setupView() {
        if(pager == null){
            int indicatorColor = R.color.selected_tab_color;
            if (!CAApp.isOceanTheme(getApplicationContext()))
                indicatorColor = R.color.top_background;

            Integer[] iconResourceArray = {R.drawable.ic_ship,
                    R.drawable.ic_stats};
            pagerTabs.setIconResourceArray(iconResourceArray);
            pagerTabs.setIconTintColor(0);

            pagerTabs.setSelectedIndicatorColors(ContextCompat.getColor(getApplicationContext(), indicatorColor));
            pagerTabs.setDistributeEvenly(true);
            pagerTabs.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            pager = new ShipComparePager(getSupportFragmentManager());
            mViewPager.setAdapter(pager);
            pagerTabs.setViewPager(mViewPager);
        }
        if(lastUpdated != null) {
            CAApp.getEventBus().post(lastUpdated.getShipId());
            lastUpdated = null;
        }
    }

    private void grabInfo(){
        progress.setVisibility(View.VISIBLE);
        CompareManager.searchShips(getApplicationContext());
    }

    @Subscribe
    public void onShipRecieveInfo(ShipResult result) {
        Dlog.d("onShipReceiveInfo", "result = " + result.toString());
        if (result.getShipInfo() != null && CompareManager.getSHIPS().contains(result.getShipId())) {
            CompareManager.addShipInfo(result.getShipId(), result.getShipInfo());
            CompareManager.checkForDone();
            lastUpdated = result;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    initView();
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_refresh, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        } else if (item.getItemId() == R.id.action_refresh){
            //refresh all fragments
            //send out calls again
        }
        return super.onOptionsItemSelected(item);
    }

    @Subscribe
    public void onRefresh(ProgressEvent event) {
        progress.setVisibility(event.isRefreshing() ? View.VISIBLE : View.GONE);
    }
}