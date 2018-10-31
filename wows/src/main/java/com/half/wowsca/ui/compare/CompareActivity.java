package com.half.wowsca.ui.compare;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.half.wowsca.CAApp;
import com.half.wowsca.R;
import com.half.wowsca.managers.CompareManager;
import com.half.wowsca.model.Achievement;
import com.half.wowsca.model.Captain;
import com.half.wowsca.model.CaptainDetails;
import com.half.wowsca.model.Ship;
import com.half.wowsca.model.encyclopedia.holders.AchievementsHolder;
import com.half.wowsca.model.encyclopedia.holders.ShipsHolder;
import com.half.wowsca.model.encyclopedia.items.AchievementInfo;
import com.half.wowsca.model.encyclopedia.items.ShipInfo;
import com.half.wowsca.model.result.CaptainResult;
import com.half.wowsca.ui.CABaseActivity;
import com.half.wowsca.ui.SettingActivity;
import org.greenrobot.eventbus.Subscribe;
import com.utilities.Utils;
import com.utilities.preferences.Prefs;
import com.utilities.views.SwipeBackLayout;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompareActivity extends CABaseActivity {

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
        setContentView(R.layout.activity_compare);

        bindView();
    }

    private void bindView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        container = (LinearLayout) findViewById(R.id.compare_container);
        progressBar = findViewById(R.id.compare_progress);
        tvErrorText = (TextView) findViewById(R.id.compare_middle_text);
        topDragContainer = (LinearLayout) findViewById(R.id.compare_top_title_bar);
        mScrollView = (ScrollView) findViewById(R.id.compare_scroll);

        if(CAApp.isDarkTheme(getApplicationContext())){
            topDragContainer.setBackgroundResource(R.color.material_action_bar_dark);
        }

        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
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
        if (CompareManager.size() > 1) {
            if (!CompareManager.captainsHaveInfo()) {
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
                if(scrollY > 80){
                    topDragContainer.setVisibility(View.VISIBLE);
                } else {
                    topDragContainer.setVisibility(View.GONE);
                }
            }
        });
    }

    @Subscribe
    public void onReceiver(CaptainResult result) {
        if (result != null) {
            if (result.getCaptain() != null) {
                Captain c = result.getCaptain();
                CompareManager.overrideCaptain(c);
                if (CompareManager.captainsHaveInfo()) {
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
    }

    private void buildViews() {
        int size = CompareManager.size();
        if (size > 1) {
            boolean larger = false;
            int layoutId = R.layout.list_compare_two;
            if (size > 2) {
                layoutId = R.layout.list_compare_three;
                larger = true;
            }
            container.removeAllViews();

            Captain c1 = CompareManager.getCaptains().get(0);
            Captain c2 = CompareManager.getCaptains().get(1);
            Captain c3 = null;
            if (larger)
                c3 = CompareManager.getCaptains().get(2);

            build(layoutId, larger, "", c1.getName(), c2.getName(), (c3 != null ? c3.getName() : ""), POS_WRONG);
            buildTitleDrag(layoutId, larger, "", c1.getName(), c2.getName(), (c3 != null ? c3.getName() : ""), POS_WRONG);

            CaptainDetails details1 = c1.getDetails();
            CaptainDetails details2 = c2.getDetails();
            CaptainDetails details3 = null;
            if (larger)
                details3 = c3.getDetails();

            showBattles(larger, layoutId, details1, details2, details3);
            showAverageDamage(larger, layoutId, details1, details2, details3);
            showWinRate(larger, layoutId, details1, details2, details3);
            showKD(larger, layoutId, details1, details2, details3);
            showSurvivalRate(larger, layoutId, details1, details2, details3);
            showPlanesPerGame(larger, layoutId, details1, details2, details3);

            showCARatingStats(larger, layoutId, details1, details2, details3);

            buildGraphs(layoutId, larger, c1.getName(), c2.getName(), (c3 != null ? c3.getName() : null), c1.getShips(), c2.getShips(), (c3 != null ? c3.getShips() : null));

            showCapturesPerGame(larger, layoutId, details1, details2, details3);
            showDefenderPointsPerGame(larger, layoutId, details1, details2, details3);
//            showKaram(larger, layoutId, details1, details2, details3);

            Prefs prefs = new Prefs(getApplicationContext());
            boolean showCompare = prefs.getBoolean(SettingActivity.SHOW_COMPARE, true);
            if(showCompare) {
                buildTitle(getString(R.string.compare_stats_average));
                showCEDamageStats(larger, layoutId, details1, details2, details3);
                showCEKillsStats(larger, layoutId, details1, details2, details3);
                showCEWRStats(larger, layoutId, details1, details2, details3);
                showCEPlanesStats(larger, layoutId, details1, details2, details3);
//                showCESurvivalStats(larger, layoutId, details1, details2, details3);
//                showCESurWinsStats(larger, layoutId, details1, details2, details3);
            }

            buildTitle(getString(R.string.compareArmament));

            showMainBatteryStats(larger, layoutId, details1, details2, details3);
            showTorpBatteryStats(larger, layoutId, details1, details2, details3);
            showPlaneStats(larger, layoutId, details1, details2, details3);
            showOtherKillStats(larger, layoutId, details1, details2, details3);

            showMainBatteryHitRate(larger, layoutId, details1, details2, details3);
            showTorpHitRate(larger, layoutId, details1, details2, details3);

            createAchievements(larger, layoutId, c1, c2, c3);
        } else {
        }
    }

    private void createAchievements(boolean larger, int layoutId, Captain c1, Captain c2, Captain c3) {
        buildTitle(getString(R.string.achievements));

        Map<String, Integer> captain1Achi = new HashMap<>();
        Map<String, Integer> captain2Achi = new HashMap<>();
        Map<String, Integer> captain3Achi = new HashMap<>();
        for(Achievement achi : c1.getAchievements()){
            captain1Achi.put(achi.getName(), achi.getNumber());
        }
        for(Achievement achi : c2.getAchievements()){
            captain2Achi.put(achi.getName(), achi.getNumber());
        }
        if(c3 != null)
            for(Achievement achi : c3.getAchievements()){
                captain3Achi.put(achi.getName(), achi.getNumber());
            }

        AchievementsHolder achis = CAApp.getInfoManager().getAchievements(getApplicationContext());
        for(AchievementInfo ach : achis.getItems().values()){
            Integer c1Achi = captain1Achi.get(ach.getId());
            Integer c2Achi = captain2Achi.get(ach.getId());
            Integer c3Achi = captain3Achi.get(ach.getId());
            if(c1Achi == null){
                c1Achi = 0;
            }
            if(c2Achi == null){
                c2Achi = 0;
            }
            if(c3Achi == null){
                c3Achi = 0;
            }
            if(c1Achi > 0 || c2Achi > 0 || c3Achi > 0){
                showAchievement(larger, layoutId, c1Achi, c2Achi, c3Achi, ach.getName());
            }
        }
    }

    private void buildTitle(String title){
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.list_compare_title, container, false);

        TextView tvTitle = (TextView) view.findViewById(R.id.list_compare_title);

        tvTitle.setText(title);

        container.addView(view);
    }

    private void showAchievement(boolean larger, int layoutId, Integer c1,Integer c2,Integer c3, String name){
        int highestPos = highest(c1, c2, c3);
        build(layoutId, larger, name, c1 + "", c2 + "", (larger ? (c3 + "") : ""), highestPos);
    }



    private void showBattles(boolean larger, int layoutId, CaptainDetails details1, CaptainDetails details2, CaptainDetails details3) {
        int battles1 = details1.getBattles();
        int battles2 = details2.getBattles();
        int battles3 = POS_WRONG;
        if (larger)
            battles3 = details3.getBattles();
        int highestPos = highest(battles1, battles2, battles3);
        if (battles3 == POS_WRONG)
            battles3 = 0;
        build(layoutId, larger, getString(R.string.battles), battles1 + "", battles2 + "", (larger ? (battles3 + "") : ""), highestPos);
    }

    private void showKaram(boolean larger, int layoutId, CaptainDetails details1, CaptainDetails details2, CaptainDetails details3) {
        int karma1 = details1.getKarma();
        int karma2 = details2.getKarma();
        int karma3 = POS_WRONG;
        if (larger)
            karma3 = details3.getKarma();
        int highestPos = highest(karma1, karma2, karma3);
        if (karma3 == POS_WRONG)
            karma3 = 0;
        build(layoutId, larger, getString(R.string.karma), karma1 + "", karma2 + "", (larger ? (karma3 + "") : ""), highestPos);
    }

    private void showMainBatteryStats(boolean larger, int layoutId, CaptainDetails details1, CaptainDetails details2, CaptainDetails details3) {
        int num1 = details1.getMainBattery().getFrags();
        int num2 = details2.getMainBattery().getFrags();
        int num3 = POS_WRONG;
        if (larger)
            num3 = details3.getMainBattery().getFrags();
        int highestPos = highest(num1, num2, num3);
        if (num3 == POS_WRONG)
            num3 = 0;
        build(layoutId, larger, getString(R.string.main_battery_kills), num1 + "", num2 + "", (larger ? (num3 + "") : ""), highestPos);
    }

    private void showCEDamageStats(boolean larger, int layoutId, CaptainDetails details1, CaptainDetails details2, CaptainDetails details3) {
        float num1 = details1.getcDamage() - details1.getExpectedDamage();
        float num2 = details2.getcDamage() - details2.getExpectedDamage();
        float num3 = POS_WRONG;
        if (larger)
            num3 = details3.getcDamage() - details3.getExpectedDamage();
        int highestPos = highest(num1, num2, num3);
        if (num3 == POS_WRONG)
            num3 = 0;
        DecimalFormat formatter = new DecimalFormat("#");
        build(layoutId, larger, getString(R.string.damage), formatter.format(num1) + "", formatter.format(num2) + "", (larger ? (formatter.format(num3) + "") : ""), highestPos);
    }

    private void showCARatingStats(boolean larger, int layoutId, CaptainDetails details1, CaptainDetails details2, CaptainDetails details3) {
        float num1 = details1.getCARating();
        float num2 = details2.getCARating();
        float num3 = POS_WRONG;
        if (larger)
            num3 = details3.getCARating();
        int highestPos = highest(num1, num2, num3);
        if (num3 == POS_WRONG)
            num3 = 0;
        DecimalFormat formatter = new DecimalFormat("#");
        build(layoutId, larger, getString(R.string.community_assistant_rating_shorter), formatter.format(num1) + "", formatter.format(num2) + "", (larger ? (formatter.format(num3) + "") : ""), highestPos);
    }

    private void showCEKillsStats(boolean larger, int layoutId, CaptainDetails details1, CaptainDetails details2, CaptainDetails details3) {
        float num1 = details1.getcKills() - details1.getExpectedKills();
        float num2 = details2.getcKills() - details2.getExpectedKills();
        float num3 = POS_WRONG;
        if (larger)
            num3 = details3.getcKills() - details3.getExpectedKills();
        int highestPos = highest(num1, num2, num3);
        if (num3 == POS_WRONG)
            num3 = 0;
        DecimalFormat formatter = new DecimalFormat("#.#");
        build(layoutId, larger, getString(R.string.kills), formatter.format(num1) + "", formatter.format(num2) + "", (larger ? (formatter.format(num3) + "") : ""), highestPos);
    }

    private void showCEWRStats(boolean larger, int layoutId, CaptainDetails details1, CaptainDetails details2, CaptainDetails details3) {
        float num1 = details1.getcWinRate() - details1.getExpectedWinRate();
        float num2 = details2.getcWinRate() - details2.getExpectedWinRate();
        float num3 = POS_WRONG;
        if (larger)
            num3 = details3.getcWinRate() - details3.getExpectedWinRate();
        int highestPos = highest(num1, num2, num3);
        if (num3 == POS_WRONG)
            num3 = 0;
        DecimalFormat formatter = new DecimalFormat("#.#%");
        build(layoutId, larger, getString(R.string.win_rate), formatter.format(num1) + "", formatter.format(num2) + "", (larger ? (formatter.format(num3) + "") : ""), highestPos);
    }

    private void showCEPlanesStats(boolean larger, int layoutId, CaptainDetails details1, CaptainDetails details2, CaptainDetails details3) {
        float num1 = details1.getcPlanes() - details1.getExpectedPlanes();
        float num2 = details2.getcPlanes() - details2.getExpectedPlanes();
        float num3 = POS_WRONG;
        if (larger)
            num3 = details3.getcPlanes() - details3.getExpectedPlanes();
        int highestPos = highest(num1, num2, num3);
        if (num3 == POS_WRONG)
            num3 = 0;
        DecimalFormat formatter = new DecimalFormat("#.#");
        build(layoutId, larger, getString(R.string.planes_downed), formatter.format(num1) + "", formatter.format(num2) + "", (larger ? (formatter.format(num3) + "") : ""), highestPos);
    }

//    private void showCESurvivalStats(boolean larger, int layoutId, CaptainDetails details1, CaptainDetails details2, CaptainDetails details3) {
//        float num1 = details1.getcSurvival() - details1.getExpectedSurvival();
//        float num2 = details2.getcSurvival() - details2.getExpectedSurvival();
//        float num3 = POS_WRONG;
//        if (larger)
//            num3 = details3.getcSurvival() - details3.getExpectedSurvival();
//        int highestPos = highest(num1, num2, num3);
//        if (num3 == POS_WRONG)
//            num3 = 0;
//        DecimalFormat formatter = new DecimalFormat("#.#%");
//        build(layoutId, larger, getString(R.string.survival_rate), formatter.format(num1) + "", formatter.format(num2) + "", (larger ? (formatter.format(num3) + "") : ""), highestPos);
//    }

//    private void showCESurWinsStats(boolean larger, int layoutId, CaptainDetails details1, CaptainDetails details2, CaptainDetails details3) {
//        float num1 = details1.getcSurWins() - details1.getExpectedSurWins();
//        float num2 = details2.getcSurWins() - details2.getExpectedSurWins();
//        float num3 = POS_WRONG;
//        if (larger)
//            num3 = details3.getcSurWins() - details3.getExpectedSurWins();
//        int highestPos = highest(num1, num2, num3);
//        if (num3 == POS_WRONG)
//            num3 = 0;
//        DecimalFormat formatter = new DecimalFormat("#.#%");
//        build(layoutId, larger, getString(R.string.survived_wins), formatter.format(num1) + "", formatter.format(num2) + "", (larger ? (formatter.format(num3) + "") : ""), highestPos);
//    }

    private void showTorpBatteryStats(boolean larger, int layoutId, CaptainDetails details1, CaptainDetails details2, CaptainDetails details3) {
        int num1 = details1.getTorpedoes().getFrags();
        int num2 = details2.getTorpedoes().getFrags();
        int num3 = POS_WRONG;
        if (larger)
            num3 = details3.getTorpedoes().getFrags();
        int highestPos = highest(num1, num2, num3);
        if (num3 == POS_WRONG)
            num3 = 0;
        build(layoutId, larger, getString(R.string.torpedoes_kills), num1 + "", num2 + "", (larger ? (num3 + "") : ""), highestPos);
    }

    private void showPlaneStats(boolean larger, int layoutId, CaptainDetails details1, CaptainDetails details2, CaptainDetails details3) {
        int battles1 = details1.getAircraft().getFrags();
        int battles2 = details2.getAircraft().getFrags();
        int battles3 = POS_WRONG;
        if (larger)
            battles3 = details3.getAircraft().getFrags();
        int highestPos = highest(battles1, battles2, battles3);
        if (battles3 == POS_WRONG)
            battles3 = 0;
        build(layoutId, larger, getString(R.string.aircraft_kills), battles1 + "", battles2 + "", (larger ? (battles3 + "") : ""), highestPos);
    }

    private void showOtherKillStats(boolean larger, int layoutId, CaptainDetails details1, CaptainDetails details2, CaptainDetails details3) {
        int battles1 = details1.getFrags() - details1.getAircraft().getFrags() - details1.getTorpedoes().getFrags() - details1.getMainBattery().getFrags();
        int battles2 = details2.getFrags() - details2.getAircraft().getFrags() - details2.getTorpedoes().getFrags() - details2.getMainBattery().getFrags();
        int battles3 = POS_WRONG;
        if (larger)
            battles3 = details3.getFrags() - details3.getAircraft().getFrags() - details3.getTorpedoes().getFrags() - details3.getMainBattery().getFrags();
        int highestPos = highest(battles1, battles2, battles3);
        if (battles3 == POS_WRONG)
            battles3 = 0;
        build(layoutId, larger, getString(R.string.other_kills), battles1 + "", battles2 + "", (larger ? (battles3 + "") : ""), highestPos);
    }

    private void showMainBatteryHitRate(boolean larger, int layoutId, CaptainDetails details1, CaptainDetails details2, CaptainDetails details3) {
        int shots1 = details1.getMainBattery().getShots();
        int shots2 = details2.getMainBattery().getShots();
        int shots3 = 0;
        if (larger)
            shots3 = details3.getMainBattery().getShots();

        float wn1 = 0, wn2 = 0, wn3 = POS_WRONG;
        if (shots1 > 0) {
            wn1 = ((float) details1.getMainBattery().getHits() / shots1) * 100;
        }
        if (shots2 > 0) {
            wn2 = ((float) details2.getMainBattery().getHits() / shots2) * 100;
        }
        if (shots3 > 0) {
            wn3 = ((float) details3.getMainBattery().getHits() / shots3) * 100;
        }

        int highestPos = highest(wn1, wn2, wn3);
        if (wn3 == POS_WRONG)
            wn3 = 0;
        DecimalFormat formatter = Utils.getOneDepthDecimalFormatter();

        build(layoutId, larger, getString(R.string.main_battery_hit_per), formatter.format(wn1) + "", formatter.format(wn2) + "", (larger ? (formatter.format(wn3) + "") : ""), highestPos);
    }

    private void showTorpHitRate(boolean larger, int layoutId, CaptainDetails details1, CaptainDetails details2, CaptainDetails details3) {
        int shots1 = details1.getTorpedoes().getShots();
        int shots2 = details2.getTorpedoes().getShots();
        int shots3 = 0;
        if (larger)
            shots3 = details3.getTorpedoes().getShots();

        float wn1 = 0, wn2 = 0, wn3 = POS_WRONG;
        if (shots1 > 0) {
            wn1 = ((float) details1.getTorpedoes().getHits() / shots1) * 100;
        }
        if (shots2 > 0) {
            wn2 = ((float) details2.getTorpedoes().getHits() / shots2) * 100;
        }
        if (shots3 > 0) {
            wn3 = ((float) details3.getTorpedoes().getHits() / shots3) * 100;
        }

        int highestPos = highest(wn1, wn2, wn3);

        DecimalFormat formatter = Utils.getOneDepthDecimalFormatter();
        if (wn3 == POS_WRONG)
            wn3 = 0;
        build(layoutId, larger, getString(R.string.torpedoes_hit_per), formatter.format(wn1) + "", formatter.format(wn2) + "", (larger ? (formatter.format(wn3) + "") : ""), highestPos);
    }

    private void showAverageDamage(boolean larger, int layoutId, CaptainDetails details1, CaptainDetails details2, CaptainDetails details3) {
        int battles1 = details1.getBattles();
        int battles2 = details2.getBattles();
        int battles3 = 0;
        if (larger)
            battles3 = details3.getBattles();

        int wn1 = 0, wn2 = 0, wn3 = POS_WRONG;
        if (battles1 > 0) {
            wn1 = (int) (details1.getTotalDamage() / battles1);
        }
        if (battles2 > 0) {
            wn2 = (int) (details2.getTotalDamage() / battles2);
        }
        if (battles3 > 0) {
            wn3 = (int) (details3.getTotalDamage() / battles3);
        }

        int highestPos = highest(wn1, wn2, wn3);

        DecimalFormat formatter = Utils.getDefaultDecimalFormatter();
        if (wn3 == POS_WRONG)
            wn3 = 0;
        build(layoutId, larger, getString(R.string.average_damage), formatter.format(wn1) + "", formatter.format(wn2) + "", (larger ? (formatter.format(wn3) + "") : ""), highestPos);
    }

    private void showWinRate(boolean larger, int layoutId, CaptainDetails details1, CaptainDetails details2, CaptainDetails details3) {
        int battles1 = details1.getBattles();
        int battles2 = details2.getBattles();
        int battles3 = 0;
        if (larger)
            battles3 = details3.getBattles();

        float wn1 = 0, wn2 = 0, wn3 = POS_WRONG;
        if (battles1 > 0) {
            wn1 = ((float) details1.getWins() / battles1) * 100;
        }
        if (battles2 > 0) {
            wn2 = ((float) details2.getWins() / battles2) * 100;
        }
        if (battles3 > 0) {
            wn3 = ((float) details3.getWins() / battles3) * 100;
        }

        int highestPos = highest(wn1, wn2, wn3);

        DecimalFormat formatter = Utils.getDefaultDecimalFormatter();
        if (wn3 == POS_WRONG)
            wn3 = 0;
        build(layoutId, larger, getString(R.string.win_rate), formatter.format(wn1) + "", formatter.format(wn2) + "", (larger ? (formatter.format(wn3) + "") : ""), highestPos);
    }

    private void showSurvivalRate(boolean larger, int layoutId, CaptainDetails details1, CaptainDetails details2, CaptainDetails details3) {
        int battles1 = details1.getBattles();
        int battles2 = details2.getBattles();
        int battles3 = 0;
        if (larger)
            battles3 = details3.getBattles();

        float wn1 = 0, wn2 = 0, wn3 = POS_WRONG;
        if (battles1 > 0) {
            wn1 = ((float) details1.getSurvivedBattles() / battles1) * 100;
        }
        if (battles2 > 0) {
            wn2 = ((float) details2.getSurvivedBattles() / battles2) * 100;
        }
        if (battles3 > 0) {
            wn3 = ((float) details3.getSurvivedBattles() / battles3) * 100;
        }

        int highestPos = highest(wn1, wn2, wn3);

        DecimalFormat formatter = Utils.getDefaultDecimalFormatter();
        if (wn3 == POS_WRONG)
            wn3 = 0;
        build(layoutId, larger, getString(R.string.survival_rate), formatter.format(wn1) + "", formatter.format(wn2) + "", (larger ? (formatter.format(wn3) + "") : ""), highestPos);
    }

    private void showKD(boolean larger, int layoutId, CaptainDetails details1, CaptainDetails details2, CaptainDetails details3) {
        float battles1 = details1.getBattles();
        float battles2 = details2.getBattles();
        float battles3 = 0;
        if (larger)
            battles3 = details3.getBattles();


        if (battles1 != details1.getSurvivedBattles())
            battles1 = battles1 - details1.getSurvivedBattles();

        if (battles2 != details2.getSurvivedBattles())
            battles2 = battles2 - details2.getSurvivedBattles();

        if (larger && battles3 != details3.getSurvivedBattles())
            battles3 = battles3 - details3.getSurvivedBattles();

        float kd1 = 0, kd2 = 0, kd3 = POS_WRONG;
        if (battles1 > 0) {
            kd1 = ((float) details1.getFrags() / battles1);
        }
        if (battles2 > 0) {
            kd2 = ((float) details2.getFrags() / battles2);
        }
        if (battles3 > 0 && larger) {
            kd3 = ((float) details3.getFrags() / battles3);
        }

        int highestPos = highest(kd1, kd2, kd3);

        DecimalFormat formatter = Utils.getDefaultDecimalFormatter();
        if (kd3 == POS_WRONG)
            kd3 = 0;
        build(layoutId, larger, getString(R.string.kills_game), formatter.format(kd1) + "", formatter.format(kd2) + "", (larger ? (formatter.format(kd3) + "") : ""), highestPos);
    }

    private void showPlanesPerGame(boolean larger, int layoutId, CaptainDetails details1, CaptainDetails details2, CaptainDetails details3) {
        float battles1 = details1.getBattles();
        float battles2 = details2.getBattles();
        float battles3 = 0;
        if (larger)
            battles3 = details3.getBattles();

        float wn1 = 0, wn2 = 0, wn3 = POS_WRONG;
        if (battles1 > 0) {
            wn1 = details1.getPlanesKilled() / battles1;
        }
        if (battles2 > 0) {
            wn2 = details2.getPlanesKilled() / battles2;
        }
        if (battles3 > 0) {
            wn3 = details3.getPlanesKilled() / battles3;
        }

        int highestPos = highest(wn1, wn2, wn3);

        DecimalFormat formatter = Utils.getOneDepthDecimalFormatter();
        if (wn3 == POS_WRONG)
            wn3 = 0;
        build(layoutId, larger, getString(R.string.planes_downed_game), formatter.format(wn1) + "", formatter.format(wn2) + "", (larger ? (formatter.format(wn3) + "") : ""), highestPos);
    }

    private void showCapturesPerGame(boolean larger, int layoutId, CaptainDetails details1, CaptainDetails details2, CaptainDetails details3) {
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
        if (wn3 == POS_WRONG)
            wn3 = 0;
        build(layoutId, larger, getString(R.string.caps_game), formatter.format(wn1) + "", formatter.format(wn2) + "", (larger ? (formatter.format(wn3) + "") : ""), highestPos);
    }

    private void showDefenderPointsPerGame(boolean larger, int layoutId, CaptainDetails details1, CaptainDetails details2, CaptainDetails details3) {
        float battles1 = details1.getBattles();
        float battles2 = details2.getBattles();
        float battles3 = 0;
        if (larger)
            battles3 = details3.getBattles();

        float wn1 = 0, wn2 = 0, wn3 = POS_WRONG;
        if (battles1 > 0) {
            wn1 = details1.getDroppedCapturePoints() / battles1;
        }
        if (battles2 > 0) {
            wn2 = details2.getDroppedCapturePoints() / battles2;
        }
        if (battles3 > 0) {
            wn3 = details3.getDroppedCapturePoints() / battles3;
        }

        int highestPos = highest(wn1, wn2, wn3);

        DecimalFormat formatter = Utils.getOneDepthDecimalFormatter();

        if (wn3 == POS_WRONG)
            wn3 = 0;

        build(layoutId, larger, getString(R.string.def_reset_game), formatter.format(wn1) + "", formatter.format(wn2) + "", (larger ? (formatter.format(wn3) + "") : ""), highestPos);
    }

    private void build(int layoutId, boolean larger, String titleStr, String oneStr, String twoStr, String threeStr, int highest) {
        View view = LayoutInflater.from(getApplicationContext()).inflate(layoutId, container, false);

        TextView title = (TextView) view.findViewById(R.id.compare_title);
        TextView one = (TextView) view.findViewById(R.id.compare_one);
        TextView two = (TextView) view.findViewById(R.id.compare_two);

        title.setText(titleStr);
        if (TextUtils.isEmpty(titleStr))
            title.setText(R.string.captain);
        one.setText(oneStr);
        two.setText(twoStr);
        TextView three = null;
        if (larger) {
            three = (TextView) view.findViewById(R.id.compare_three);
            three.setText(threeStr);
        }
        if (highest != POS_WRONG)
            colorCells(highest, R.drawable.compare_top_grid, one, two, three);

        container.addView(view);
    }

    private void buildTitleDrag(int layoutId, boolean larger, String titleStr, String oneStr, String twoStr, String threeStr, int highest) {
        View view = LayoutInflater.from(getApplicationContext()).inflate(layoutId, container, false);

        TextView title = (TextView) view.findViewById(R.id.compare_title);
        TextView one = (TextView) view.findViewById(R.id.compare_one);
        TextView two = (TextView) view.findViewById(R.id.compare_two);

        title.setVisibility(View.INVISIBLE);
        one.setText(oneStr);
        two.setText(twoStr);
        TextView three = null;
        if (larger) {
            three = (TextView) view.findViewById(R.id.compare_three);
            three.setText(threeStr);
        }

        topDragContainer.addView(view);
    }

    private void build(View view, boolean larger, String titleStr, String oneStr, String twoStr, String threeStr, int highest) {
        TextView title = (TextView) view.findViewById(R.id.compare_title);
        TextView one = (TextView) view.findViewById(R.id.compare_one);
        TextView two = (TextView) view.findViewById(R.id.compare_two);

        title.setText(titleStr);
        one.setText(oneStr);
        two.setText(twoStr);
        TextView three = null;
        if (larger) {
            three = (TextView) view.findViewById(R.id.compare_three);
            three.setText(threeStr);
        }
        if (highest != POS_WRONG)
            colorCells(highest, R.drawable.compare_top_grid, one, two, three);
    }

    private void buildGraphs(int layoutId, boolean larger, String s, String s1, String s2, List<Ship> details1, List<Ship> details2, List<Ship> details3) {
        View damage = LayoutInflater.from(getApplicationContext()).inflate(R.layout.list_compare_bar_graph, container, false);

        TextView titledamage = (TextView) damage.findViewById(R.id.list_compare_graph_text);
        titledamage.setText(R.string.average_damage_per_tier);

        BarChart chartDamage = (BarChart) damage.findViewById(R.id.list_compare_graph);

        View experience = LayoutInflater.from(getApplicationContext()).inflate(R.layout.list_compare_bar_graph, container, false);

        TextView titleExperience = (TextView) experience.findViewById(R.id.list_compare_graph_text);
        titleExperience.setText(R.string.average_experience_per_tier);

        BarChart chartExperience = (BarChart) experience.findViewById(R.id.list_compare_graph);

        View view = LayoutInflater.from(getApplicationContext()).inflate(layoutId, container, false);
        TextView title = (TextView) view.findViewById(R.id.compare_title);
        title.setText(R.string.average_tier);

        setUpCharts(view, larger, s, s1, s2, details1, details2, details3, chartDamage, chartExperience);
        container.addView(view);
        container.addView(damage);
        container.addView(experience);
    }

    private void setUpCharts(final View view, final boolean larger, final String s, final String s1, final String s2, final List<Ship> captain, final List<Ship> captain2, final List<Ship> captain3, final BarChart averageDamage, final BarChart averageExperience) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                averageDamage.clear();
                averageExperience.clear();
                final CaptainStatsCompareObject cap1 = calculateStats(captain);
                final CaptainStatsCompareObject cap2 = calculateStats(captain2);
                CaptainStatsCompareObject cap3 = null;
                if (captain3 != null)
                    cap3 = calculateStats(captain3);
                setUpExpChart(cap1.averages, cap2.averages, (cap3 != null ? cap3.averages : null), averageExperience, s, s1, s2);
                setUpDamageChart(cap1.avgDamages, cap2.avgDamages, (cap3 != null ? cap3.avgDamages : null), averageDamage, s, s1, s2);
                final CaptainStatsCompareObject cap3Obj = cap3;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DecimalFormat formatter = Utils.getOneDepthDecimalFormatter();
                        int highest = highest(cap1.averageTier, cap2.averageTier, (cap3Obj != null ? cap3Obj.averageTier : POS_WRONG));
                        build(view, larger, getString(R.string.average_tier), formatter.format(cap1.averageTier), formatter.format(cap2.averageTier), ((cap3Obj != null) ? formatter.format(cap3Obj.averageTier) : ""), highest);
                    }
                });
            }

            private CaptainStatsCompareObject calculateStats(List<Ship> capShips) {
                SparseArray<Integer> battleCounts = new SparseArray<>();
                SparseArray<Long> experience = new SparseArray<>();
                SparseArray<Long> damages = new SparseArray<>();
                ShipsHolder shipsHolder = CAApp.getInfoManager().getShipInfo(getApplicationContext());
                int averageTier = 0;
                int totalBattles = 0;
                for (Ship s : capShips) {
                    ShipInfo info = shipsHolder.get(s.getShipId());
                    if (info != null) {
                        int tier = info.getTier();
                        Integer battleCount = battleCounts.get(tier);
                        if (battleCount != null) {
                            battleCounts.put(tier, battleCount + s.getBattles());
                        } else {
                            battleCounts.put(tier, s.getBattles());
                        }
                        Long exp = experience.get(tier);
                        if (exp != null) {
                            experience.put(tier, exp + s.getTotalXP());
                        } else {
                            experience.put(tier, s.getTotalXP());
                        }
                        Long damage = damages.get(tier);
                        if (damage != null) {
                            damages.put(tier, (long) (damage + s.getTotalDamage()));
                        } else {
                            damages.put(tier, (long) s.getTotalDamage());
                        }
                        averageTier += tier * s.getBattles();
                    }
                    totalBattles += s.getBattles();
                }
                float averageTierNumber = (float) averageTier / (float) totalBattles;
                Map<Integer, Long> averages = new HashMap<Integer, Long>();
                Map<Integer, Long> avgDamages = new HashMap<Integer, Long>();
                for (int i = 1; i < 11; i++) {
                    Integer battleCount = battleCounts.get(i);
                    Long exp = experience.get(i);
                    if (battleCount != null && exp != null && battleCount > 0) {
                        averages.put(i, exp / battleCount);
                    } else {
                        averages.put(i, 0l);
                    }
                    Long damage = damages.get(i);
                    if (damage != null && battleCount > 0) {
                        avgDamages.put(i, damage / battleCount);
                    } else {
                        avgDamages.put(i, 0l);
                    }
                }
                CaptainStatsCompareObject object = new CaptainStatsCompareObject();
                object.averages = averages;
                object.avgDamages = avgDamages;
                object.averageTier = averageTierNumber;
                return object;
            }

            private void setUpDamageChart(final Map<Integer, Long> avgDamages, final Map<Integer, Long> averages2, final Map<Integer, Long> averages3, final BarChart chartAverageDamage, final String s, final String s1, final String s2) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int textColor = ContextCompat.getColor(chartAverageDamage.getContext(), R.color.material_text_primary);
                        int accentColor = ContextCompat.getColor(chartAverageDamage.getContext(), R.color.compare_first);
                        int accentColor2 = ContextCompat.getColor(chartAverageDamage.getContext(), R.color.compare_second);
                        int accentColor3 = ContextCompat.getColor(chartAverageDamage.getContext(), R.color.compare_three);
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
                        l.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));

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


                        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
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
                        int textColor = ContextCompat.getColor(chartAverageExperience.getContext(), R.color.material_text_primary);
                        int accentColor = ContextCompat.getColor(chartAverageExperience.getContext(), R.color.compare_first);
                        int accentColor2 = ContextCompat.getColor(chartAverageExperience.getContext(), R.color.compare_second);
                        int accentColor3 = ContextCompat.getColor(chartAverageExperience.getContext(), R.color.compare_three);

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
                        l.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));

                        ArrayList<String> xVals = new ArrayList<String>();
                        for (int i = 1; i <= 10; i++)
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

                        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
                        dataSets.add(set1);
                        dataSets.add(set2);
                        if (set3 != null)
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

    private class CaptainStatsCompareObject {
        public Map<Integer, Long> averages;
        public Map<Integer, Long> avgDamages;
        public float averageTier;
    }


    private int highest(int one, int two, int three) {
        int highestPos = POS_WRONG;
        if (one == 0 && two == 0 && (three == 0 || three == POS_WRONG)) {

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

    private int highest(float one, float two, float three) {
        int highestPos = POS_WRONG;
        if (one == 0 && two == 0 && (three == 0 || three == POS_WRONG)) {

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

    private int highest(long one, long two, long three) {
        int highestPos = POS_WRONG;
        if (one == 0 && two == 0 && (three == 0 || three == POS_WRONG)) {

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

    private void colorCells(int number, int background, TextView one, TextView two, TextView three) {
        if (number == 0) {
            one.setBackgroundResource(background);
        }
        if (number == 1) {
            two.setBackgroundResource(background);
        }
        if (three != null) {
            if (number == 2) {
                three.setBackgroundResource(background);
            }
        }
    }
}