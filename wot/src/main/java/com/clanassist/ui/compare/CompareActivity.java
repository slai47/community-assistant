package com.clanassist.ui.compare;

import android.content.res.Configuration;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.clanassist.CAApp;
import com.clanassist.model.infoobj.VehicleWN8;
import com.clanassist.model.infoobj.Tank;
import com.clanassist.model.player.Player;
import com.clanassist.model.player.PlayerVehicleInfo;
import com.clanassist.model.statistics.Statistics;
import com.clanassist.tools.CompareManager;
import com.cp.assist.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.squareup.otto.Subscribe;
import com.utilities.Utils;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CompareActivity extends AppCompatActivity {

    public static final int POS_WRONG = -1;
    private LinearLayout container;
    private View progressBar;
    private LinearLayout topDragContainer;

    private TextView tvErrorText;

    private Toolbar mToolbar;
    private ScrollView mScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set language
        String current = CAApp.getAppLanguage(getApplicationContext());
        Locale myLocale = new Locale(current);
        Locale.setDefault(myLocale);
        Configuration config = new Configuration();
        config.locale = myLocale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

        CAApp.setTheme(this);
        setTitle("");
        setContentView(R.layout.activity_compare);
        bindView();
        if(CAApp.isLightTheme(getApplicationContext())){
            findViewById(R.id.compare_background).setBackgroundResource(R.color.background_material_light);
        } else {
            findViewById(R.id.compare_background).setBackgroundResource(R.color.background_material_dark);
        }
    }

    private void bindView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mToolbar.setBackgroundResource(R.color.transparent);
        container = (LinearLayout) findViewById(R.id.compare_container);
        progressBar = findViewById(R.id.compare_progress);
        tvErrorText = (TextView) findViewById(R.id.compare_middle_text);
        topDragContainer = (LinearLayout) findViewById(R.id.compare_top_title_bar);
        mScrollView = (ScrollView) findViewById(R.id.compare_scroll);
    }

    @Override
    protected void onResume() {
        super.onResume();
        CAApp.getEventBus().register(this);
        initView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        CAApp.getEventBus().unregister(this);
    }

    private void initView() {
        if(CompareManager.size() > 1){
            if(!CompareManager.playersHaveInfo()){
                container.removeAllViews();
                topDragContainer.removeAllViews();
                CompareManager.search(getApplicationContext());
                progressBar.setVisibility(View.VISIBLE);
            } else {
                progressBar.setVisibility(View.GONE);
                buildViews();
            }
        } else {
            progressBar.setVisibility(View.GONE);
            topDragContainer.removeAllViews();
            container.removeAllViews();
        }

        mScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int scrollY = mScrollView.getScrollY(); //for verticalScrollView
                //DO SOMETHING WITH THE SCROLL COORDINATES
                if (scrollY > 80) {
                    topDragContainer.setVisibility(View.VISIBLE);
                } else {
                    topDragContainer.setVisibility(View.GONE);
                }
            }
        });
    }

    @Subscribe
    public void onReceiver(Player result){
        if(result != null){
                Player c = result;
                CompareManager.overridePlayer(c);
                if(CompareManager.playersHaveInfo()){
                    progressBar.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            buildViews();
                        }
                    });
                }
        }
    }

    private void buildViews(){
        int size = CompareManager.size();
        if(size > 1){
            boolean larger = false;
            int layoutId = R.layout.list_compare_two;
            if(size > 2){
                layoutId = R.layout.list_compare_three;
                larger = true;
            }
            container.removeAllViews();

            Player c1 = CompareManager.getPlayers().get(0);
            Player c2 = CompareManager.getPlayers().get(1);
            Player c3 = null;
            if(larger)
                c3 = CompareManager.getPlayers().get(2);

            build(layoutId, larger, "", c1.getName(), c2.getName(), (c3 != null ? c3.getName() : ""), POS_WRONG);
            buildTitleDrag(layoutId, larger, "", c1.getName(), c2.getName(), (c3 != null ? c3.getName() : ""), POS_WRONG);

            Statistics details1 = c1.getOverallStats();
            Statistics details2 = c2.getOverallStats();
            Statistics details3 = null;
            if(larger)
                details3 = c3.getOverallStats();

            showBattles(larger, layoutId, details1, details2, details3);

            showWN8(larger, layoutId, c1, c2, c3);
            Statistics cwDetails3 = null;
            if(larger)
                cwDetails3 = c3.getClanStats();
            showCWBattles(larger, layoutId, c1.getClanStats(), c2.getClanStats(), cwDetails3);
            showCWWN8(larger, layoutId, c1, c2, c3);
            Statistics shDetails3 = null;
            if(larger)
                shDetails3 = c3.getStrongholdStats();
            showSHBattles(larger, layoutId, c1.getStrongholdStats(), c2.getStrongholdStats(), shDetails3);
            showSHWN8(larger, layoutId, c1, c2, c3);

            showRecentDayWN8(larger, layoutId, c1, c2, c3);
            showRecent7WN8(larger, layoutId, c1, c2, c3);
            showRecent30DayWN8(larger, layoutId, c1, c2, c3);
            showRecent60DayWN8(larger, layoutId, c1, c2, c3);

            buildTitle(getString(R.string.details_cap));

            showAverageExperience(larger, layoutId, details1, details2, details3);
            showAverageDamage(larger, layoutId, details1, details2, details3);
            showWinRate(larger, layoutId, details1, details2, details3);
            showKD(larger, layoutId, details1, details2, details3);

            buildGraphs(layoutId, larger, c1.getName(), c2.getName(), (c3 != null ? c3.getName() : null), c1.getPlayerVehicleInfoList(), c2.getPlayerVehicleInfoList(), (c3 != null ? c3.getPlayerVehicleInfoList() : null));

            showCapturesPerGame(larger, layoutId, details1, details2, details3);
            showDefenderPointsPerGame(larger, layoutId, details1, details2, details3);
            showSurvivalRate(larger, layoutId, details1, details2, details3);
            showDamageFactor(larger, layoutId, details1, details2, details3);
            showHitRatio(larger, layoutId, details1, details2, details3);
        } else {
        }
    }

    private void buildTitle(String title){
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.list_compare_title, container, false);

        TextView tvTitle = (TextView) view.findViewById(R.id.list_compare_title);

        tvTitle.setText(title);

        boolean isLightTheme = CAApp.isLightTheme(tvTitle.getContext());
        tvTitle.setTextColor(isLightTheme ? ContextCompat.getColor(tvTitle.getContext(), R.color.black) : ContextCompat.getColor(tvTitle.getContext(), R.color.white));

        container.addView(view);
    }

    private void buildTitleDrag(int layoutId, boolean larger, String titleStr, String oneStr, String twoStr, String threeStr, int highest) {
        View view = LayoutInflater.from(getApplicationContext()).inflate(layoutId, container, false);
        boolean isLightTheme = CAApp.isLightTheme(getApplicationContext());

        view.setBackgroundResource(isLightTheme ? R.color.white : R.color.black);


        TextView title = (TextView) view.findViewById(R.id.compare_title);
        TextView one = (TextView) view.findViewById(R.id.compare_one);
        TextView two = (TextView) view.findViewById(R.id.compare_two);

        title.setVisibility(View.INVISIBLE);
        one.setText(oneStr);
        one.setTextColor(isLightTheme ? ContextCompat.getColor(one.getContext(), R.color.black) : ContextCompat.getColor(one.getContext(), R.color.white));

        two.setText(twoStr);
        two.setTextColor(isLightTheme ? ContextCompat.getColor(one.getContext(), R.color.black) : ContextCompat.getColor(one.getContext(), R.color.white));


        title.setBackgroundResource(isLightTheme ? R.drawable.compare_normal_grid_light : R.drawable.compare_normal_grid);
        one.setBackgroundResource(isLightTheme ? R.drawable.compare_normal_grid_light : R.drawable.compare_normal_grid);
        two.setBackgroundResource(isLightTheme ? R.drawable.compare_normal_grid_light : R.drawable.compare_normal_grid);

        TextView three = null;
        if (larger) {
            three = (TextView) view.findViewById(R.id.compare_three);
            three.setText(threeStr);
            three.setTextColor(isLightTheme ? ContextCompat.getColor(one.getContext(), R.color.black) : ContextCompat.getColor(one.getContext(), R.color.white));
            three.setBackgroundResource(isLightTheme ? R.drawable.compare_normal_grid_light : R.drawable.compare_normal_grid);
        }

        topDragContainer.addView(view);
    }

    private void showBattles(boolean larger, int layoutId, Statistics details1, Statistics details2, Statistics details3) {
        int battles1 = details1.getBattles();
        int battles2 = details2.getBattles();
        int battles3 = POS_WRONG;
        if(larger)
            battles3 = details3.getBattles();
        int highestPos = highest(battles1, battles2, battles3);
        if(battles3 == POS_WRONG)
            battles3 = 0;
        build(layoutId, larger, getResources().getString(R.string.battles), battles1 + "", battles2 + "", (larger ? (battles3 + "") : ""), highestPos);
    }

    private void showCWBattles(boolean larger, int layoutId, Statistics details1, Statistics details2, Statistics details3) {
        int battles1 = details1.getBattles();
        int battles2 = details2.getBattles();
        int battles3 = POS_WRONG;
        if(larger)
            battles3 = details3.getBattles();
        int highestPos = highest(battles1, battles2, battles3);
        if(battles3 == POS_WRONG)
            battles3 = 0;
        build(layoutId, larger,getString(R.string.clan_wars_cap) + " " + getResources().getString(R.string.battles), battles1 + "", battles2 + "", (larger ? (battles3 + "") : ""), highestPos);
    }

    private void showSHBattles(boolean larger, int layoutId, Statistics details1, Statistics details2, Statistics details3) {
        int battles1 = details1.getBattles();
        int battles2 = details2.getBattles();
        int battles3 = POS_WRONG;
        if(larger)
            battles3 = details3.getBattles();
        int highestPos = highest(battles1, battles2, battles3);
        if(battles3 == POS_WRONG)
            battles3 = 0;
        build(layoutId, larger,getString(R.string.stronghold_cap) + " " + getResources().getString(R.string.battles), battles1 + "", battles2 + "", (larger ? (battles3 + "") : ""), highestPos);
    }

    private void showWN8(boolean larger, int layoutId, Player details1, Player details2, Player details3) {
        float battles1 = details1.getWN8();
        float battles2 = details2.getWN8();
        float battles3 = POS_WRONG;
        if(larger)
            battles3 = details3.getWN8();
        int highestPos = highest(battles1, battles2, battles3);
        if(battles3 == POS_WRONG)
            battles3 = 0;
        DecimalFormat formatter = Utils.getDefaultDecimalFormatter();
        build(layoutId, larger, getResources().getString(R.string.wn8_cap), formatter.format(battles1) + "", formatter.format(battles2) + "", (larger ? (formatter.format(battles3) + ""):""), highestPos);
    }

    private void showRecent7WN8(boolean larger, int layoutId, Player details1, Player details2, Player details3) {
        float battles1 = details1.getWn8StatsInfo().getPastWeek();
        float battles2 = details2.getWn8StatsInfo().getPastWeek();
        float battles3 = POS_WRONG;
        if(larger)
            battles3 = details3.getWn8StatsInfo().getPastWeek();
        int highestPos = highest(battles1, battles2, battles3);
        if(battles3 == POS_WRONG)
            battles3 = 0;
        DecimalFormat formatter = Utils.getDefaultDecimalFormatter();
        build(layoutId, larger, getResources().getString(R.string.last_7_wn8), formatter.format(battles1) + "", formatter.format(battles2) + "", (larger ? (formatter.format(battles3) + ""):""), highestPos);
    }

    private void showRecentDayWN8(boolean larger, int layoutId, Player details1, Player details2, Player details3) {
        float battles1 = details1.getWn8StatsInfo().getPastDay();
        float battles2 = details2.getWn8StatsInfo().getPastDay();
        float battles3 = POS_WRONG;
        if(larger)
            battles3 = details3.getWn8StatsInfo().getPastDay();
        int highestPos = highest(battles1, battles2, battles3);
        if(battles3 == POS_WRONG)
            battles3 = 0;
        DecimalFormat formatter = Utils.getDefaultDecimalFormatter();
        build(layoutId, larger, getResources().getString(R.string.last_24_wn8), formatter.format(battles1) + "", formatter.format(battles2) + "", (larger ? (formatter.format(battles3) + ""):""), highestPos);
    }

    private void showRecent30DayWN8(boolean larger, int layoutId, Player details1, Player details2, Player details3) {
        float battles1 = details1.getWn8StatsInfo().getPastMonth();
        float battles2 = details2.getWn8StatsInfo().getPastMonth();
        float battles3 = POS_WRONG;
        if(larger)
            battles3 = details3.getWn8StatsInfo().getPastMonth();
        int highestPos = highest(battles1, battles2, battles3);
        if(battles3 == POS_WRONG)
            battles3 = 0;
        DecimalFormat formatter = Utils.getDefaultDecimalFormatter();
        build(layoutId, larger, getResources().getString(R.string.last_30_wn8), formatter.format(battles1) + "", formatter.format(battles2) + "", (larger ? (formatter.format(battles3) + ""):""), highestPos);
    }

    private void showRecent60DayWN8(boolean larger, int layoutId, Player details1, Player details2, Player details3) {
        float battles1 = details1.getWn8StatsInfo().getPastTwoMonths();
        float battles2 = details2.getWn8StatsInfo().getPastTwoMonths();
        float battles3 = POS_WRONG;
        if(larger)
            battles3 = details3.getWn8StatsInfo().getPastTwoMonths();
        int highestPos = highest(battles1, battles2, battles3);
        if(battles3 == POS_WRONG)
            battles3 = 0;
        DecimalFormat formatter = Utils.getDefaultDecimalFormatter();
        build(layoutId, larger, getResources().getString(R.string.last_60_wn8), formatter.format(battles1) + "", formatter.format(battles2) + "", (larger ? (formatter.format(battles3) + ""):""), highestPos);
    }

    private void showCWWN8(boolean larger, int layoutId, Player details1, Player details2, Player details3) {
        float battles1 = details1.getClanWN8();
        float battles2 = details2.getClanWN8();
        float battles3 = POS_WRONG;
        if(larger)
            battles3 = details3.getClanWN8();
        int highestPos = highest(battles1, battles2, battles3);
        if(battles3 == POS_WRONG)
            battles3 = 0;

        DecimalFormat formatter = Utils.getDefaultDecimalFormatter();
        build(layoutId, larger, getResources().getStringArray(R.array.clan_sorting)[5], formatter.format(battles1) + "", formatter.format(battles2) + "", (larger ? (formatter.format(battles3) + "") : ""), highestPos);
    }

    private void showSHWN8(boolean larger, int layoutId, Player details1, Player details2, Player details3) {
        float battles1 = details1.getStrongholdWN8();
        float battles2 = details2.getStrongholdWN8();
        float battles3 = POS_WRONG;
        if(larger)
            battles3 = details3.getStrongholdWN8();
        int highestPos = highest(battles1, battles2, battles3);
        if(battles3 == POS_WRONG)
            battles3 = 0;
        DecimalFormat formatter = Utils.getDefaultDecimalFormatter();
        build(layoutId, larger, getString(R.string.stronghold_wn8), formatter.format(battles1) + "", formatter.format(battles2) + "", (larger ? (formatter.format(battles3) + ""):""), highestPos);
    }

    private void showAverageDamage(boolean larger, int layoutId, Statistics details1, Statistics details2, Statistics details3) {
        int battles1 = details1.getBattles();
        int battles2 = details2.getBattles();
        int battles3 = 0;
        if (larger)
            battles3 = details3.getBattles();

        int wn1 = 0, wn2 = 0, wn3 = POS_WRONG;
        if (battles1 > 0) {
            wn1 = (int) (details1.getDamageDealt() / battles1);
        }
        if (battles2 > 0) {
            wn2 = (int) (details2.getDamageDealt() / battles2);
        }
        if (battles3 > 0) {
            wn3 = (int) (details3.getDamageDealt() / battles3);
        }

        int highestPos = highest(wn1, wn2, wn3);

        DecimalFormat formatter = Utils.getDefaultDecimalFormatter();
        if(wn3 == POS_WRONG)
            wn3 = 0;
        build(layoutId, larger, getResources().getString(R.string.avg_damage), formatter.format(wn1) + "", formatter.format(wn2) + "", (larger ? (formatter.format(wn3) + "") : ""), highestPos);
    }

    private void showAverageExperience(boolean larger, int layoutId, Statistics details1, Statistics details2, Statistics details3) {
        int battles1 = details1.getBattles();
        int battles2 = details2.getBattles();
        int battles3 = 0;
        if (larger)
            battles3 = details3.getBattles();

        int wn1 = 0, wn2 = 0, wn3 = POS_WRONG;
        if (battles1 > 0) {
            wn1 = (int) (details1.getXp() / battles1);
        }
        if (battles2 > 0) {
            wn2 = (int) (details2.getXp() / battles2);
        }
        if (battles3 > 0) {
            wn3 = (int) (details3.getXp() / battles3);
        }

        int highestPos = highest(wn1, wn2, wn3);

        DecimalFormat formatter = Utils.getDefaultDecimalFormatter();
        if(wn3 == POS_WRONG)
            wn3 = 0;
        build(layoutId, larger, getResources().getString(R.string.avg_exp), formatter.format(wn1) + "", formatter.format(wn2) + "", (larger ? (formatter.format(wn3) + "") : ""), highestPos);
    }

    private void showWinRate(boolean larger, int layoutId, Statistics details1, Statistics details2, Statistics details3) {
        int battles1 = details1.getBattles();
        int battles2 = details2.getBattles();
        int battles3 = 0;
        if(larger)
            battles3 = details3.getBattles();

        float wn1 = 0, wn2 = 0, wn3 = POS_WRONG;
        if(battles1 > 0){
            wn1 = ((float) details1.getWins() / battles1) * 100;
        }
        if(battles2 > 0){
            wn2 = ((float) details2.getWins() / battles2) * 100;
        }
        if(battles3 > 0){
            wn3 = ((float) details3.getWins() / battles3) * 100;
        }

        int highestPos = highest(wn1, wn2, wn3);

        DecimalFormat formatter = Utils.getDefaultDecimalFormatter();
        if(wn3 == POS_WRONG)
            wn3 = 0;
        build(layoutId, larger, getResources().getString(R.string.win_rate), formatter.format(wn1) + "", formatter.format(wn2) + "", (larger ? (formatter.format(wn3) + ""):""), highestPos);
    }

    private void showSurvivalRate(boolean larger, int layoutId, Statistics details1, Statistics details2, Statistics details3) {
        int battles1 = details1.getBattles();
        int battles2 = details2.getBattles();
        int battles3 = 0;
        if(larger)
            battles3 = details3.getBattles();

        float wn1 = 0, wn2 = 0, wn3 = POS_WRONG;
        if(battles1 > 0){
            wn1 = ((float) details1.getSurvivedBattles() / battles1) * 100;
        }
        if(battles2 > 0){
            wn2 = ((float) details2.getSurvivedBattles() / battles2) * 100;
        }
        if(battles3 > 0){
            wn3 = ((float) details3.getSurvivedBattles() / battles3) * 100;
        }

        int highestPos = highest(wn1, wn2, wn3);

        DecimalFormat formatter = Utils.getDefaultDecimalFormatter();
        if(wn3 == POS_WRONG)
            wn3 = 0;
        build(layoutId, larger, getString(R.string.survival_rate), formatter.format(wn1) + "", formatter.format(wn2) + "", (larger ? (formatter.format(wn3) + ""):""), highestPos);
    }

    private void showHitRatio(boolean larger, int layoutId, Statistics details1, Statistics details2, Statistics details3) {
        int battles1 = details1.getHitsPercentage();
        int battles2 = details2.getHitsPercentage();
        int battles3 = 0;
        if(larger)
            battles3 = details3.getHitsPercentage();

        int highestPos = highest(battles1, battles2, battles3);

        build(layoutId, larger, getString(R.string.hit_percentage), battles1 + "", battles2 + "", (larger ? (battles3 + ""):""), highestPos);
    }

    private void showDamageFactor(boolean larger, int layoutId, Statistics details1, Statistics details2, Statistics details3) {
        float wn1 = 0, wn2 = 0, wn3 = POS_WRONG;
        wn1 = (float) details1.getDamageDealt() / (float) details1.getDamageReceived();
        wn2 = (float) details2.getDamageDealt() /(float) details2.getDamageReceived();
        if(larger)
            wn3 = (float) details3.getDamageDealt() / (float) details3.getDamageReceived();

        int highestPos = highest(wn1, wn2, wn3);

        DecimalFormat formatter = Utils.getDefaultDecimalFormatter();
        if(wn3 == POS_WRONG)
            wn3 = 0;
        build(layoutId, larger, getString(R.string.dmg_factor), formatter.format(wn1) + "", formatter.format(wn2) + "", (larger ? (formatter.format(wn3) + ""):""), highestPos);
    }

    private void showKD(boolean larger, int layoutId, Statistics details1, Statistics details2, Statistics details3) {
        float battles1 = details1.getBattles();
        float battles2 = details2.getBattles();
        float battles3 = 0;
        if(larger)
            battles3 = details3.getBattles();
        if(battles1 != details1.getSurvivedBattles())
            battles1 = battles1 - details1.getSurvivedBattles();

        if(battles2 != details2.getSurvivedBattles())
            battles2 = battles2 - details2.getSurvivedBattles();

        if(larger && battles3 != details3.getSurvivedBattles())
            battles3 = battles3 - details3.getSurvivedBattles();

        float kd1 = 0, kd2 = 0, kd3 = POS_WRONG;
        if(battles1 > 0){
            kd1 = ((float) details1.getFrags() / battles1);
        }
        if(battles2 > 0){
            kd2 = ((float) details2.getFrags() / battles2);
        }
        if(battles3 > 0 && larger){
            kd3 = ((float) details3.getFrags() / battles3);
        }

        int highestPos = highest(kd1, kd2, kd3);

        DecimalFormat formatter = Utils.getDefaultDecimalFormatter();
        if(kd3 == POS_WRONG)
            kd3 = 0;
        build(layoutId, larger, getResources().getString(R.string.k_d), formatter.format(kd1) + "", formatter.format(kd2) + "", (larger ? (formatter.format(kd3) + "") : ""), highestPos);
    }


    private void showCapturesPerGame(boolean larger, int layoutId, Statistics details1, Statistics details2, Statistics details3) {
        float battles1 = details1.getBattles();
        float battles2 = details2.getBattles();
        float battles3 = 0;
        if (larger)
            battles3 = details3.getBattles();

        float wn1 = 0, wn2 = 0, wn3 = POS_WRONG;
        if (battles1 > 0) {
            wn1 = details1.getCapturePoints() / battles1;
        }
        if (battles2 > 0) {
            wn2 = details2.getCapturePoints() / battles2;
        }
        if (battles3 > 0) {
            wn3 = details3.getCapturePoints() / battles3;
        }

        int highestPos = highest(wn1, wn2, wn3);

        DecimalFormat formatter = Utils.getOneDepthDecimalFormatter();
        if(wn3 == POS_WRONG)
            wn3 = 0;
        build(layoutId, larger, getString(R.string.caps_per_game), formatter.format(wn1) + "", formatter.format(wn2) + "", (larger ? (formatter.format(wn3) + "") : ""), highestPos);
    }

    private void showDefenderPointsPerGame(boolean larger, int layoutId, Statistics details1, Statistics details2, Statistics details3) {
        float battles1 = details1.getBattles();
        float battles2 = details2.getBattles();
        float battles3 = 0;
        if (larger)
            battles3 = details3.getBattles();

        float wn1 = 0, wn2 = 0, wn3 = POS_WRONG;
        if (battles1 > 0) {
            wn1 = details1.getDroppedCapture_points() / battles1;
        }
        if (battles2 > 0) {
            wn2 = details2.getDroppedCapture_points() / battles2;
        }
        if (battles3 > 0) {
            wn3 = details3.getDroppedCapture_points() / battles3;
        }

        int highestPos = highest(wn1, wn2, wn3);

        DecimalFormat formatter = Utils.getOneDepthDecimalFormatter();

        if(wn3 == POS_WRONG)
            wn3 = 0;

        build(layoutId, larger, getString(R.string.def_reset_per_game), formatter.format(wn1) + "", formatter.format(wn2) + "", (larger ? (formatter.format(wn3) + "") : ""), highestPos);
    }

    private void build(int layoutId, boolean larger, String titleStr, String oneStr, String twoStr, String threeStr, int highest){
        View view = LayoutInflater.from(getApplicationContext()).inflate(layoutId, container, false);

        TextView title = (TextView) view.findViewById(R.id.compare_title);
        TextView one = (TextView) view.findViewById(R.id.compare_one);
        TextView two = (TextView) view.findViewById(R.id.compare_two);

        title.setText(titleStr);
//        if(TextUtils.isEmpty(titleStr))
//            title.setText("");
        one.setText(oneStr);
        two.setText(twoStr);

        boolean isLightTheme = CAApp.isLightTheme(view.getContext());
        title.setTextColor(isLightTheme ? ContextCompat.getColor(getApplicationContext(), R.color.black) : ContextCompat.getColor(getApplicationContext(), R.color.white));
        one.setTextColor(isLightTheme ? ContextCompat.getColor(getApplicationContext(), R.color.black) : ContextCompat.getColor(getApplicationContext(), R.color.white));
        two.setTextColor(isLightTheme ? ContextCompat.getColor(getApplicationContext(), R.color.black) : ContextCompat.getColor(getApplicationContext(), R.color.white));

        title.setBackgroundResource(isLightTheme ? R.drawable.compare_normal_grid_light : R.drawable.compare_normal_grid);
        one.setBackgroundResource(isLightTheme ? R.drawable.compare_normal_grid_light : R.drawable.compare_normal_grid);
        two.setBackgroundResource(isLightTheme ? R.drawable.compare_normal_grid_light : R.drawable.compare_normal_grid);

        TextView three = null;
        if(larger) {
            three = (TextView) view.findViewById(R.id.compare_three);
            three.setText(threeStr);
            three.setTextColor(isLightTheme ? getResources().getColor(R.color.black) : getResources().getColor(R.color.white));
            three.setBackgroundResource(isLightTheme ? R.drawable.compare_normal_grid_light : R.drawable.compare_normal_grid);
        }
        if(highest != POS_WRONG)
            colorCells(highest, R.drawable.compare_top_grid , one, two, three);

        container.addView(view);
    }

    private void build(View view, boolean larger, String titleStr, String oneStr, String twoStr, String threeStr, int highest){
        TextView title = (TextView) view.findViewById(R.id.compare_title);
        TextView one = (TextView) view.findViewById(R.id.compare_one);
        TextView two = (TextView) view.findViewById(R.id.compare_two);

        title.setText(titleStr);
        one.setText(oneStr);
        two.setText(twoStr);

        boolean isLightTheme = CAApp.isLightTheme(view.getContext());
        title.setTextColor(isLightTheme ? ContextCompat.getColor(getApplicationContext(), R.color.black) : ContextCompat.getColor(getApplicationContext(), R.color.white));
        one.setTextColor(isLightTheme ? ContextCompat.getColor(getApplicationContext(), R.color.black) : ContextCompat.getColor(getApplicationContext(), R.color.white));
        two.setTextColor(isLightTheme ? ContextCompat.getColor(getApplicationContext(), R.color.black) : ContextCompat.getColor(getApplicationContext(), R.color.white));

        title.setBackgroundResource(isLightTheme ? R.drawable.compare_normal_grid_light : R.drawable.compare_normal_grid);
        one.setBackgroundResource(isLightTheme ? R.drawable.compare_normal_grid_light : R.drawable.compare_normal_grid);
        two.setBackgroundResource(isLightTheme ? R.drawable.compare_normal_grid_light : R.drawable.compare_normal_grid);

        TextView three = null;
        if(larger) {
            three = (TextView) view.findViewById(R.id.compare_three);
            three.setText(threeStr);
            three.setTextColor(isLightTheme ? ContextCompat.getColor(getApplicationContext(), R.color.black) : ContextCompat.getColor(getApplicationContext(), R.color.white));
            three.setBackgroundResource(isLightTheme ? R.drawable.compare_normal_grid_light : R.drawable.compare_normal_grid);
        }
        if(highest != POS_WRONG)
            colorCells(highest, R.drawable.compare_top_grid , one, two, three);
    }

    private void buildGraphs(int layoutId, boolean larger, String s, String s1, String s2, List<PlayerVehicleInfo> details1, List<PlayerVehicleInfo> details2, List<PlayerVehicleInfo> details3){
        View damage = LayoutInflater.from(getApplicationContext()).inflate(R.layout.list_compare_bar_graph, container, false);

        boolean isLightTheme = CAApp.isLightTheme(damage.getContext());
        TextView titledamage = (TextView) damage.findViewById(R.id.list_compare_graph_text);
        titledamage.setText(R.string.avg_dmg_per_tier);

        BarChart chartDamage = (BarChart) damage.findViewById(R.id.list_compare_graph);

        View experience = LayoutInflater.from(getApplicationContext()).inflate(R.layout.list_compare_bar_graph, container, false);

        TextView titleExperience = (TextView) experience.findViewById(R.id.list_compare_graph_text);
        titleExperience.setText(R.string.avg_exp_per_tier);

        View wn8 = LayoutInflater.from(getApplicationContext()).inflate(R.layout.list_compare_bar_graph, container, false);

        TextView titlewn8 = (TextView) wn8.findViewById(R.id.list_compare_graph_text);
        titlewn8.setText(R.string.wn8_per_tier);

        titledamage.setTextColor(isLightTheme ? ContextCompat.getColor(getApplicationContext(), R.color.black) : ContextCompat.getColor(getApplicationContext(), R.color.white));
        titleExperience.setTextColor(isLightTheme ? getResources().getColor(R.color.black) : ContextCompat.getColor(getApplicationContext(), R.color.white));
        titlewn8.setTextColor(isLightTheme ? ContextCompat.getColor(getApplicationContext(), R.color.black) : ContextCompat.getColor(getApplicationContext(), R.color.white));
        damage.findViewById(R.id.compare_background).setBackgroundResource(isLightTheme ? R.drawable.compare_normal_grid_light : R.drawable.compare_normal_grid);
        experience.findViewById(R.id.compare_background).setBackgroundResource(isLightTheme ? R.drawable.compare_normal_grid_light : R.drawable.compare_normal_grid);
        wn8.findViewById(R.id.compare_background).setBackgroundResource(isLightTheme ? R.drawable.compare_normal_grid_light : R.drawable.compare_normal_grid);

        BarChart chartExperience = (BarChart) experience.findViewById(R.id.list_compare_graph);

        View view = LayoutInflater.from(getApplicationContext()).inflate(layoutId, container, false);
        TextView title = (TextView) view.findViewById(R.id.compare_title);
        title.setText(R.string.avg_tier);

        BarChart chartWN8 = (BarChart) wn8.findViewById(R.id.list_compare_graph);

        setUpCharts(view, larger, s, s1, s2, details1, details2, details3, chartDamage, chartExperience, chartWN8);
        container.addView(view);
        container.addView(wn8);
        container.addView(damage);
        container.addView(experience);
    }

    private void setUpCharts(final View view, final boolean larger, final String s, final String s1, final String s2, final List<PlayerVehicleInfo> captain, final List<PlayerVehicleInfo> captain2, final List<PlayerVehicleInfo> captain3, final BarChart averageDamage, final BarChart averageExperience, final BarChart chartWN8) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                averageDamage.clear();
                averageExperience.clear();
                final PlayerStatsCompareObject cap1 = calculateStats(captain);
                final PlayerStatsCompareObject cap2 = calculateStats(captain2);
                PlayerStatsCompareObject cap3 = null;
                if(captain3 != null)
                    cap3 = calculateStats(captain3);
                setUpWN8Chart(cap1.avgWN8, cap2.avgWN8, (cap3 != null ? cap3.avgWN8 : null), chartWN8, s, s1, s2);
                setUpExpChart(cap1.averages, cap2.averages, (cap3 != null ? cap3.averages : null), averageExperience, s, s1, s2);
                setUpDamageChart(cap1.avgDamages, cap2.avgDamages, (cap3 != null ? cap3.avgDamages : null), averageDamage, s, s1, s2);
                final PlayerStatsCompareObject cap3Obj = cap3;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DecimalFormat formatter = Utils.getOneDepthDecimalFormatter();
                        int highest = highest(cap1.averageTier, cap2.averageTier, (cap3Obj != null ? cap3Obj.averageTier : POS_WRONG));
                        build(view, larger, getResources().getString(R.string.avg_tier), formatter.format(cap1.averageTier), formatter.format(cap2.averageTier), ((cap3Obj != null) ? formatter.format(cap3Obj.averageTier) : ""), highest);
                    }
                });
            }

            private PlayerStatsCompareObject calculateStats(List<PlayerVehicleInfo> capShips){
                SparseArray<Integer> battleCounts = new SparseArray<Integer>();
                SparseArray<Long> experience = new SparseArray<Long>();
                SparseArray<Long> damages = new SparseArray<Long>();
                SparseArray<Long> wn8s = new SparseArray<Long>();
                SparseArray<Integer> tanksPerTier = new SparseArray<Integer>();
                int averageTier = 0;
                int totalBattles = 0;
                for(PlayerVehicleInfo s : capShips){
                    Tank tankInfo = CAApp.getInfoManager().getTanks(getApplicationContext()).getTank(s.getTankId());
                    if(tankInfo != null){
                        int tier = tankInfo.getTier();
                        Integer battleCount = battleCounts.get(tier);
                        if(battleCount != null) {
                            battleCounts.put(tier, battleCount + s.getOverallStats().getBattles());
                        } else {
                            battleCounts.put(tier, s.getOverallStats().getBattles());
                        }
                        Long exp = experience.get(tier);
                        if(exp != null) {
                            experience.put(tier, exp + s.getOverallStats().getXp());
                        } else {
                            experience.put(tier, (long) s.getOverallStats().getXp());
                        }
                        Long damage = damages.get(tier);
                        if(damage != null){
                            damages.put(tier, damage + s.getOverallStats().getDamageDealt());
                        } else {
                            damages.put(tier, (long) s.getOverallStats().getDamageDealt());
                        }
                        Long wn8 = wn8s.get(tier);
                        if(wn8 != null){
                            wn8s.put(tier, (long) (wn8 + s.getWN8()));
                        } else {
                            wn8s.put(tier, (long) s.getWN8());
                        }
                        Integer tpt = tanksPerTier.get(tier);
                        if(tpt != null){
                            tanksPerTier.put(tier, tpt + 1);
                        } else {
                            tanksPerTier.put(tier, 1);
                        }
                        averageTier += tier * s.getOverallStats().getBattles();
                    }
                    totalBattles += s.getOverallStats().getBattles();
                }
                float averageTierNumber = (float) averageTier / (float) totalBattles;
                Map<Integer, Long> averages = new HashMap<Integer, Long>();
                Map<Integer, Long> avgDamages =  new HashMap<Integer, Long>();
                Map<Integer, Long> avgWN8s =  new HashMap<Integer, Long>();
                for(int i = 1; i < 11; i++){
                    Integer battleCount = battleCounts.get(i);
                    Long exp = experience.get(i);
                    if(battleCount != null && exp != null && battleCount > 0){
                        averages.put(i, exp / battleCount);
                    }else {
                        averages.put(i, 0l);
                    }
                    Long damage = damages.get(i);
                    if(damage != null && battleCount > 0){
                        avgDamages.put(i, damage / battleCount);
                    } else {
                        avgDamages.put(i, 0l);
                    }
                    Long wn8 = wn8s.get(i);
                    Integer tpt = tanksPerTier.get(i);
                    if(wn8 != null && tpt != null && tpt > 0){
                        avgWN8s.put(i, wn8 / tpt);
                    } else {
                        avgWN8s.put(i, 0l);
                    }
                }
                PlayerStatsCompareObject object = new PlayerStatsCompareObject();
                object.averages = averages;
                object.avgDamages = avgDamages;
                object.averageTier = averageTierNumber;
                object.avgWN8 = avgWN8s;
                return object;
            }

            private void setUpWN8Chart(final Map<Integer, Long> avgDamages, final Map<Integer, Long> averages2, final Map<Integer, Long> averages3, final BarChart chartAvgWN8, final String s, final String s1, final String s2) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        boolean isLightTheme = CAApp.isLightTheme(chartAvgWN8.getContext());
                        int textColor = !isLightTheme ? ContextCompat.getColor(getApplicationContext(), R.color.material_text_primary) : ContextCompat.getColor(getApplicationContext(), R.color.material_light_text_secondary);
                        int accentColor = ContextCompat.getColor(getApplicationContext(), R.color.compare_first);
                        int accentColor2 = ContextCompat.getColor(getApplicationContext(), R.color.compare_second);
                        int accentColor3 = ContextCompat.getColor(getApplicationContext(), R.color.compare_three);

                        chartAvgWN8.setDrawBarShadow(false);
                        chartAvgWN8.setDrawValueAboveBar(false);
                        chartAvgWN8.setPinchZoom(false);
                        chartAvgWN8.setDoubleTapToZoomEnabled(false);
                        chartAvgWN8.setDrawGridBackground(false);
                        chartAvgWN8.setDrawValueAboveBar(true);
                        chartAvgWN8.setTouchEnabled(false);

                        XAxis xAxis = chartAvgWN8.getXAxis();
                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                        xAxis.setTextColor(textColor);
                        xAxis.setDrawGridLines(true);

                        YAxis yAxis = chartAvgWN8.getAxisRight();
                        yAxis.setLabelCount(6, false);
                        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
                        yAxis.setTextColor(textColor);
                        yAxis.setEnabled(false);

                        YAxis yAxis2 = chartAvgWN8.getAxisLeft();
                        yAxis2.setLabelCount(6, false);
                        yAxis2.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
                        yAxis2.setTextColor(textColor);
                        yAxis2.setValueFormatter(new LargeValueFormatter());

                        Legend l = chartAvgWN8.getLegend();
                        l.setEnabled(true);
                        l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
                        l.setTextColor(textColor);

                        ArrayList<String> xVals = new ArrayList<String>();
                        for (int i = 1; i <= 10; i++)
                            xVals.add(i + "");

                        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
                        ArrayList<BarEntry> yVals2 = new ArrayList<BarEntry>();
                        ArrayList<BarEntry> yVals3 = new ArrayList<BarEntry>();

                        List<Integer> colorList = new ArrayList<Integer>();
                        for (int i = 0; i < 10; i++) {
                            double dValue = avgDamages.get(i + 1);
                            float value = (float) dValue;
                            double dValue2 = averages2.get(i + 1);
                            float value2 = (float) dValue2;
                            yVals1.add(new BarEntry(value, i));
                            yVals2.add(new BarEntry(value2, i));
                            if (averages3 != null) {
                                double dValue3 = averages3.get(i + 1);
                                float value3 = (float) dValue3;
                                yVals3.add(new BarEntry(value3, i));
                            }
                        }

                        BarDataSet set1 = new BarDataSet(yVals1, "");
                        set1.setBarSpacePercent(20f);
                        set1.setColor(accentColor);
                        set1.setLabel(s);

                        BarDataSet set2 = new BarDataSet(yVals2, "");
                        set2.setBarSpacePercent(20f);
                        set2.setColor(accentColor2);
                        set2.setLabel(s1);

                        BarDataSet set3 = null;
                        if (averages3 != null) {
                            set3 = new BarDataSet(yVals3, "");
                            set3.setBarSpacePercent(20f);
                            set3.setColor(accentColor3);
                            set3.setLabel(s2);
                        }

                        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
                        dataSets.add(set1);
                        dataSets.add(set2);
                        if (set3 != null)
                            dataSets.add(set3);

                        BarData data = new BarData(xVals, dataSets);
                        data.setValueTextSize(10f);
                        data.setValueTextColor(textColor);
                        data.setValueFormatter(new LargeValueFormatter());

                        chartAvgWN8.setDescription("");
                        chartAvgWN8.setData(data);
                        chartAvgWN8.requestLayout();
                    }
                });
            }


            private void setUpDamageChart(final Map<Integer, Long> avgDamages, final Map<Integer, Long> averages2, final Map<Integer, Long> averages3, final BarChart chartAverageDamage, final String s, final String s1, final String s2) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        boolean isLightTheme = CAApp.isLightTheme(chartAverageDamage.getContext());
                        int textColor = !isLightTheme ? ContextCompat.getColor(getApplicationContext(), R.color.material_text_primary) : ContextCompat.getColor(getApplicationContext(), R.color.material_light_text_secondary);
                        int accentColor = ContextCompat.getColor(getApplicationContext(), R.color.compare_first);
                        int accentColor2 = ContextCompat.getColor(getApplicationContext(), R.color.compare_second);
                        int accentColor3 = ContextCompat.getColor(getApplicationContext(), R.color.compare_three);

                        chartAverageDamage.setDrawBarShadow(false);
                        chartAverageDamage.setDrawValueAboveBar(false);
                        chartAverageDamage.setPinchZoom(false);
                        chartAverageDamage.setDoubleTapToZoomEnabled(false);
                        chartAverageDamage.setDrawGridBackground(false);
                        chartAverageDamage.setDrawValueAboveBar(true);
                        chartAverageDamage.setTouchEnabled(false);

                        XAxis xAxis = chartAverageDamage.getXAxis();
                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                        xAxis.setTextColor(textColor);
                        xAxis.setDrawGridLines(true);

                        YAxis yAxis = chartAverageDamage.getAxisRight();
                        yAxis.setLabelCount(6, false);
                        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
                        yAxis.setTextColor(textColor);
                        yAxis.setEnabled(false);

                        YAxis yAxis2 = chartAverageDamage.getAxisLeft();
                        yAxis2.setLabelCount(6, false);
                        yAxis2.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
                        yAxis2.setTextColor(textColor);
                        yAxis2.setValueFormatter(new LargeValueFormatter());

                        Legend l = chartAverageDamage.getLegend();
                        l.setEnabled(true);
                        l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
                        l.setTextColor(textColor);

                        ArrayList<String> xVals = new ArrayList<String>();
                        for (int i = 1; i <= 10; i++)
                            xVals.add(i + "");

                        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
                        ArrayList<BarEntry> yVals2 = new ArrayList<BarEntry>();
                        ArrayList<BarEntry> yVals3 = new ArrayList<BarEntry>();

                        List<Integer> colorList = new ArrayList<Integer>();
                        for (int i = 0; i < 10; i++) {
                            double dValue = avgDamages.get(i + 1);
                            float value = (float) dValue;
                            double dValue2 = averages2.get(i + 1);
                            float value2 = (float) dValue2;
                            yVals1.add(new BarEntry(value, i));
                            yVals2.add(new BarEntry(value2, i));
                            if (averages3 != null) {
                                double dValue3 = averages3.get(i + 1);
                                float value3 = (float) dValue3;
                                yVals3.add(new BarEntry(value3, i));
                            }
                        }

                        BarDataSet set1 = new BarDataSet(yVals1, "");
                        set1.setBarSpacePercent(20f);
                        set1.setColor(accentColor);
                        set1.setLabel(s);

                        BarDataSet set2 = new BarDataSet(yVals2, "");
                        set2.setBarSpacePercent(20f);
                        set2.setColor(accentColor2);
                        set2.setLabel(s1);

                        BarDataSet set3 = null;
                        if (averages3 != null) {
                            set3 = new BarDataSet(yVals3, "");
                            set3.setBarSpacePercent(20f);
                            set3.setColor(accentColor3);
                            set3.setLabel(s2);
                        }


                        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
                        dataSets.add(set1);
                        dataSets.add(set2);
                        if (set3 != null)
                            dataSets.add(set3);

                        BarData data = new BarData(xVals, dataSets);
                        data.setValueTextSize(10f);
                        data.setValueTextColor(textColor);
                        data.setValueFormatter(new LargeValueFormatter());

                        chartAverageDamage.setDescription("");
                        chartAverageDamage.setData(data);
                        chartAverageDamage.requestLayout();
                    }
                });
            }

            private void setUpExpChart(final Map<Integer, Long> averages, final Map<Integer, Long> averages2, final Map<Integer, Long> averages3, final BarChart chartAverageExperience, final String s, final String s1, final String s2) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        boolean isLightTheme = CAApp.isLightTheme(chartAverageExperience.getContext());
                        int textColor = !isLightTheme ? ContextCompat.getColor(getApplicationContext(), R.color.material_text_primary) : ContextCompat.getColor(getApplicationContext(), R.color.material_light_text_secondary);
                        int accentColor = ContextCompat.getColor(getApplicationContext(), R.color.compare_first);
                        int accentColor2 = ContextCompat.getColor(getApplicationContext(), R.color.compare_second);
                        int accentColor3 = ContextCompat.getColor(getApplicationContext(), R.color.compare_three);

                        chartAverageExperience.setDrawBarShadow(false);
                        chartAverageExperience.setDrawValueAboveBar(false);
                        chartAverageExperience.setPinchZoom(false);
                        chartAverageExperience.setDoubleTapToZoomEnabled(false);
                        chartAverageExperience.setDrawGridBackground(false);
                        chartAverageExperience.setDrawValueAboveBar(true);
                        chartAverageExperience.setTouchEnabled(false);

                        XAxis xAxis = chartAverageExperience.getXAxis();
                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                        xAxis.setTextColor(textColor);
                        xAxis.setDrawGridLines(true);

                        YAxis yAxis = chartAverageExperience.getAxisRight();
                        yAxis.setLabelCount(6, false);
                        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
                        yAxis.setTextColor(textColor);
                        yAxis.setEnabled(false);

                        YAxis yAxis2 = chartAverageExperience.getAxisLeft();
                        yAxis2.setLabelCount(6, false);
                        yAxis2.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
                        yAxis2.setTextColor(textColor);
                        yAxis2.setValueFormatter(new LargeValueFormatter());

                        Legend l = chartAverageExperience.getLegend();
                        l.setEnabled(true);
                        l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
                        l.setTextColor(textColor);

                        ArrayList<String> xVals = new ArrayList<String>();
                        for(int i =1; i <= 10; i++)
                            xVals.add(i + "");

                        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
                        ArrayList<BarEntry> yVals2 = new ArrayList<BarEntry>();
                        ArrayList<BarEntry> yVals3 = new ArrayList<BarEntry>();
                        List<Integer> colorList = new ArrayList<Integer>();
                        for (int i = 0; i < 10; i++) {
                            double dValue = averages.get(i + 1);
                            float value = (float) dValue;
                            double dValue2 = averages2.get(i + 1);
                            float value2 = (float) dValue2;
                            yVals1.add(new BarEntry(value, i));
                            yVals2.add(new BarEntry(value2, i));
                            if(averages3 != null){
                                double dValue3 = averages3.get(i + 1);
                                float value3 = (float) dValue3;
                                yVals3.add(new BarEntry(value3, i));
                            }
                        }


                        BarDataSet set1 = new BarDataSet(yVals1, "");
                        set1.setBarSpacePercent(20f);
                        set1.setColor(accentColor);
                        set1.setLabel(s);

                        BarDataSet set2 = new BarDataSet(yVals2, "");
                        set2.setBarSpacePercent(20f);
                        set2.setColor(accentColor2);
                        set2.setLabel(s1);

                        BarDataSet set3 = null;
                        if(averages3 != null){
                            set3 = new BarDataSet(yVals3, "");
                            set3.setBarSpacePercent(20f);
                            set3.setColor(accentColor3);
                            set3.setLabel(s2);
                        }

                        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
                        dataSets.add(set1);
                        dataSets.add(set2);
                        if(set3 != null)
                            dataSets.add(set3);

                        BarData data = new BarData(xVals, dataSets);
                        data.setValueTextSize(10f);
                        data.setValueTextColor(textColor);
                        data.setValueFormatter(new LargeValueFormatter());

                        chartAverageExperience.setDescription("");

                        chartAverageExperience.setData(data);
                        chartAverageExperience.requestLayout();
                    }
                });
            }
        };
        new Thread(runnable).start();
    }

    private class PlayerStatsCompareObject {
        public Map<Integer, Long> averages;
        public Map<Integer, Long> avgDamages;
        public Map<Integer, Long> avgWN8;
        public float averageTier;
    }


    private int highest(int one, int two, int three){
        int highestPos = POS_WRONG;
        if(one == 0 && two == 0 && (three == 0 || three == POS_WRONG)){

        } else {
            int highest = 0;
            int[] array = {one, two, three};
            for (int i = 0; i < array.length; i++) {
                int current = array[i];
                if (current > highest) {
                    highest = current;
                    highestPos = i;
                }
            }
        }
        return highestPos;
    }

    private int highest(float one, float two, float three){
        int highestPos = POS_WRONG;
        if(one == 0 && two == 0 && (three == 0 || three == POS_WRONG)){

        } else {
            float highest = 0;
            float[] array = {one, two, three};
            for (int i = 0; i < array.length; i++) {
                float current = array[i];
                if (current > highest) {
                    highest = current;
                    highestPos = i;
                }
            }
        }
        return highestPos;
    }

    private int highest(long one, long two, long three){
        int highestPos = POS_WRONG;
        if(one == 0 && two == 0 && (three == 0 || three == POS_WRONG)){

        } else {
            long highest = 0;
            long[] array = {one, two, three};
            for (int i = 0; i < array.length; i++) {
                long current = array[i];
                if (current > highest && current != 0) {
                    highest = current;
                    highestPos = i;
                }
            }
        }
        return highestPos;
    }

    private void colorCells(int number, int background, TextView one, TextView two, TextView three){
        if(number == 0){
            one.setBackgroundResource(background);
        }
        if(number == 1){
            two.setBackgroundResource(background);
        }
        if(three != null) {
            if (number == 2) {
                three.setBackgroundResource(background);
            }
        }
    }
}