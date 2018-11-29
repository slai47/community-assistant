package com.half.wowsca.ui.encyclopedia;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.half.wowsca.CAApp;
import com.half.wowsca.R;
import com.half.wowsca.managers.CompareManager;
import com.half.wowsca.managers.InfoManager;
import com.half.wowsca.model.encyclopedia.holders.ShipsHolder;
import com.half.wowsca.model.events.ShipCompareEvent;
import com.half.wowsca.ui.CABaseActivity;
import com.half.wowsca.ui.adapter.pager.ShipopediaPager;
import com.half.wowsca.ui.compare.ShipCompareActivity;
import org.greenrobot.eventbus.Subscribe;
import com.utilities.views.SlidingTabLayout;
import com.utilities.views.SwipeBackLayout;


/**
 * Created by slai4 on 10/31/2015.
 */
public class EncyclopediaTabbedActivity extends CABaseActivity {

    private Toolbar mToolbar;

    private ViewPager mViewPager;
    private SlidingTabLayout pagerTabs;
    private ShipopediaPager pager;

    private View bClear;
    private View bCompare;

    private View aTabs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encyclopedia);
        bindView();
    }

    private void bindView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mViewPager = (ViewPager) findViewById(R.id.encyclopedia_pager);
        pager = new ShipopediaPager(getSupportFragmentManager());
        Integer[] iconResourceArray = {R.drawable.ic_encyclopedia,
                R.drawable.ic_stats, R.drawable.ic_captain_skills,
                R.drawable.ic_flags, R.drawable.ic_upgrade};
        pagerTabs = (SlidingTabLayout) findViewById(R.id.encyclopedia_pager_tab);
        pagerTabs.setIconResourceArray(iconResourceArray);
        pagerTabs.setIconTintColor(0);

        int indicatorColor = R.color.selected_tab_color;
        if(!CAApp.isOceanTheme(getApplicationContext()))
            indicatorColor = R.color.material_primary;
        setTitle("");

        aTabs = findViewById(R.id.encyclopedia_tabs);
        bClear = findViewById(R.id.encyclopedia_clear);
        bCompare = findViewById(R.id.encyclopedia_compare);

        pagerTabs.setSelectedIndicatorColors(ContextCompat.getColor(getApplicationContext(), indicatorColor));
        pagerTabs.setDistributeEvenly(true);
        pagerTabs.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        mViewPager.setAdapter(pager);
        pagerTabs.setViewPager(mViewPager);

        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
    }

    @Override
    protected void onResume() {
        super.onResume();
        CAApp.getEventBus().register(this);
        toggleTopArea();
        setUpButtons();
    }

    @Override
    protected void onPause() {
        super.onPause();
        CAApp.getEventBus().unregister(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!CompareManager.getSHIPS().isEmpty()) {
            clearItems();
        } else
            super.onBackPressed();
    }

    @Subscribe
    public void onShipCompare(ShipCompareEvent event){
        toggleTopArea();
    }

    private void setUpButtons(){
        bCompare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CompareManager.getSHIPS().size() > 1) {
                    Context ctx  = EncyclopediaTabbedActivity.this;
                    AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                    builder.setTitle("Compare these ships?");

                    ShipsHolder info = new InfoManager().getShipInfo(getApplicationContext());
                    StringBuilder sb = new StringBuilder();
                    for(long id : CompareManager.getSHIPS()){
                        String name = info.get(id).getName();
                        sb.append(name);
                        sb.append("\n");
                    }
                    builder.setMessage(sb.toString());

                    builder.setPositiveButton(R.string.compare, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            CompareManager.clearShips(false);
                            Intent i = new Intent(getApplicationContext(), ShipCompareActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                            dialog.dismiss();
                        }
                    });

                    builder.setNeutralButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    builder.setNegativeButton(R.string.clear_list, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            clearItems();
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.toast_not_enough_ships_to_compare, Toast.LENGTH_SHORT).show();
                }
            }
        });
        bClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearItems();
            }
        });
    }

    private void clearItems() {
        CompareManager.clearShips(true);
        ShipCompareEvent event = new ShipCompareEvent(0);
        event.setCleared(true);
        CAApp.getEventBus().post(event);
    }

    private void toggleTopArea(){
        if(!CompareManager.getSHIPS().isEmpty()){
            pagerTabs.setVisibility(View.GONE);
            bClear.setVisibility(View.VISIBLE);
            bCompare.setVisibility(View.VISIBLE);
        } else {
            pagerTabs.setVisibility(View.VISIBLE);
            bClear.setVisibility(View.GONE);
            bCompare.setVisibility(View.GONE);
        }
    }

}
