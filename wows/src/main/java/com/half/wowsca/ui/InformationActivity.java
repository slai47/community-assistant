package com.half.wowsca.ui;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.widget.TextView;

import com.half.wowsca.R;
import com.half.wowsca.managers.CARatingManager;
import com.utilities.views.SwipeBackLayout;

public class InformationActivity extends CABaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        //set up action bar

        Toolbar bar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(bar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
    }

    @Override
    protected void onResume() {
        super.onResume();
        TextView individual = (TextView) findViewById(R.id.info_per_ship);
        individual.setText(getString(R.string.ca_rating_explanation, Math.round(CARatingManager.DAMAGE_COEF * 100) + "%", Math.round(CARatingManager.KILLS_COEF * 100) + "%", Math.round(CARatingManager.WR_COEF * 100) + "%"));

        TextView overall = (TextView) findViewById(R.id.info_overall);
        overall.setText(getString(R.string.ca_rating_overall_explanation));

        TextView reasons = (TextView) findViewById(R.id.info_reasons);
        reasons.setText(getString(R.string.ca_rating_reason, Math.round(CARatingManager.DAMAGE_COEF * 100) + "%", Math.round(CARatingManager.KILLS_COEF * 100) + "%", Math.round(CARatingManager.WR_COEF * 100) + "%"));
    }
}
