package com.half.wowsca.ui.viewcaptain.tabs;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;



import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.half.wowsca.CAApp;
import com.half.wowsca.R;
import com.half.wowsca.backend.GetCaptainTask;
import com.half.wowsca.interfaces.ICaptain;
import com.half.wowsca.managers.CARatingManager;
import com.half.wowsca.managers.CaptainManager;
import com.half.wowsca.managers.StorageManager;
import com.half.wowsca.model.Captain;
import com.half.wowsca.model.CaptainDetails;
import com.half.wowsca.model.CaptainPrivateInformation;
import com.half.wowsca.model.Ship;
import com.half.wowsca.model.Statistics;
import com.half.wowsca.model.encyclopedia.holders.ShipsHolder;
import com.half.wowsca.model.encyclopedia.items.ShipInfo;
import com.half.wowsca.model.enums.AverageType;
import com.half.wowsca.model.events.CaptainReceivedEvent;
import com.half.wowsca.model.events.CaptainSavedEvent;
import com.half.wowsca.model.events.ProgressEvent;
import com.half.wowsca.model.events.RefreshEvent;
import com.half.wowsca.model.listModels.ListAverages;
import com.half.wowsca.model.ranked.RankedInfo;
import com.half.wowsca.model.saveobjects.SavedDetails;
import com.half.wowsca.ui.CAFragment;
import com.half.wowsca.ui.InformationActivity;
import com.half.wowsca.ui.SettingActivity;
import com.half.wowsca.ui.UIUtils;
import com.half.wowsca.ui.adapter.AveragesAdapter;
import com.half.wowsca.ui.views.NonScrollableGridView;
import com.half.wowsca.ui.views.RadarMarkerView;
import org.greenrobot.eventbus.Subscribe;
import com.utilities.Utils;
import com.utilities.logging.Dlog;
import com.utilities.preferences.Prefs;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by slai4 on 9/15/2015.
 */
public class CaptainFragment extends CAFragment {

    public static final String TAG_AVERAGE_DAMAGE = "AverageDamage";
    public static final String TAG_BATTLES = "Battles";
    public static final String TAG_WIN_RATE = "WinRate";
    public static final String TAG_KILL_DEATH = "KillDeath";
    public static final String TAG_AVERAGE_EXP = "AverageExp";
    public static final String SELECTED_GRAPH = "selected_graph";
    public static final String SEEN_TOPICAL_GRAPH_DESCRIPTION = "seen_topical_graph_descriptions";

    private View aBattles;
    private View aWinRate;
    private View aAverageExp;
    private View aAverageDamage;
    private View aKillDeath;

    private TextView tvBattles;
    private TextView tvWinRate;
    private TextView tvAverageExp;
    private TextView tvAverageDamage;
    private TextView tvKillDeath;

    private TextView tvGenXP;
    private TextView tvGenDamage;
    private TextView tvGenPlanesKilled;
    private TextView tvGenCapture;
    private TextView tvGenDropped;
    private TextView tvGenProfileLevel;

    private View topicalArea;
    private TextView topicalText;
    private LineChart topicalChart;
    private BarChart tiersChart;
    private TextView tvTierAverage;
    private View chartProgress;
    private TextView tvTopicalDescription;

    private HorizontalBarChart chartGamePerType;
    private PieChart chartGamePerNation;

    private ProgressBar pbDistanceTraveled;
    private TextView tvDistanceTraveled;
    private TextView tvDistanceTotal;

//    private View captainAddView;
//    private CheckBox captainCheckBox;

    private TextView tvTotalPlanes;
    private TextView tvTotalCaptures;
    private TextView tvTotalDefReset;

    private TextView tvWins;
    private TextView tvLosses;
    private TextView tvDraws;
    private TextView tvSurvivalRate;
    private TextView tvSurvivedWins;

    private TextView tvMainBatteryAcc;
    private TextView tvTorpAcc;

    private TextView lastBattleTime;
    private TextView createdOnTime;

    private MyFormatter formatter;
    private MyYFormatter yFormatter;

    private TextView tvCARating;
    private TextView tvCADiff;

    private TextView tvSpottingDamage;
    private TextView tvArgoDamage;
    private TextView tvBuildingDamage;
    private TextView tvArgoTorpDamage;
    private TextView tvSuppressionCount;
    private TextView tvSpottingCount;

    private View aPrivateArea;
    private TextView tvPrivateGold;
    private TextView tvPrivateCredits;
    private TextView tvPrivateFreeExp;
    private TextView tvPrivateSlots;
    private TextView tvPrivateBattleTime;
    private TextView tvPrivatePremiumExpiresOn;

    private View aAverage;

    private RadarChart chartAverages;
    private NonScrollableGridView gAverages;
    private AveragesAdapter averagesAdapter;

    private LinearLayout aOtherStats;

    private PieChart chartGamemodes;

    private BarChart chartWRModes;

    private BarChart chartSurvivalRate;

    private HorizontalBarChart chartAvgDmg;

    private View tvGameModeTitle;

    private View aCARatingArea;

    private BarChart chartCAContribution;
    private BarChart chartCARatingPerTier;
    private ImageView ivBreakdown;
    private View aBreakdown;
    private View aBreakdownCharts;

    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_captain, container, false);
        bindView(view);
        return view;
    }

    private void bindView(View view) {
        aAverageDamage = view.findViewById(R.id.captain_damage_area);
        aKillDeath = view.findViewById(R.id.captain_kills_area);
        aAverageExp = view.findViewById(R.id.captain_experience_area);
        aWinRate = view.findViewById(R.id.captain_winning_area);
        aBattles = view.findViewById(R.id.captain_battles_area);

        tvBattles = (TextView) view.findViewById(R.id.captain_battles);
        tvWinRate = (TextView) view.findViewById(R.id.captain_win_rate);
        tvAverageExp = (TextView) view.findViewById(R.id.captain_avg_exp);
        tvAverageDamage = (TextView) view.findViewById(R.id.captain_avg_dmg);
        tvKillDeath = (TextView) view.findViewById(R.id.captain_k_d);

//        if(CAApp.isLightTheme(view.getContext())){
//            ((ImageView) view.findViewById(R.id.captain_battles_iv)).setColorFilter(ContextCompat.getColor(view.getContext(), R.color.top_background), PorterDuff.Mode.MULTIPLY);
//            ((ImageView) view.findViewById(R.id.captain_win_rate_iv)).setColorFilter(ContextCompat.getColor(view.getContext(), R.color.top_background), PorterDuff.Mode.MULTIPLY);
//            ((ImageView) view.findViewById(R.id.captain_avg_exp_iv)).setColorFilter(ContextCompat.getColor(view.getContext(), R.color.top_background), PorterDuff.Mode.MULTIPLY);
//            ((ImageView) view.findViewById(R.id.captain_damage_iv)).setColorFilter(ContextCompat.getColor(view.getContext(), R.color.top_background), PorterDuff.Mode.MULTIPLY);
//            ((ImageView) view.findViewById(R.id.captain_k_d_iv)).setColorFilter(ContextCompat.getColor(view.getContext(), R.color.top_background), PorterDuff.Mode.MULTIPLY);
//        }

        tvGenXP = (TextView) view.findViewById(R.id.captain_general_total_xp);
        tvGenDamage = (TextView) view.findViewById(R.id.captain_general_total_damage);
        tvGenPlanesKilled = (TextView) view.findViewById(R.id.captain_general_planes_killed);
        tvGenCapture = (TextView) view.findViewById(R.id.captain_general_capture_points);
        tvGenDropped = (TextView) view.findViewById(R.id.captain_general_defender_points);
        tvGenProfileLevel = (TextView) view.findViewById(R.id.captain_general_profile_level);

        chartProgress = view.findViewById(R.id.captain_graphs_progress);

        topicalArea = view.findViewById(R.id.captain_graph_topical_area);
        topicalText = (TextView) view.findViewById(R.id.captain_graph_topical_text);
        topicalChart = (LineChart) view.findViewById(R.id.captain_graph_topical_line);
        tvTierAverage = (TextView) view.findViewById(R.id.captain_graph_tier_average);
        tiersChart = (BarChart) view.findViewById(R.id.captain_graphs_tier);
        tvTopicalDescription = (TextView) view.findViewById(R.id.captain_graph_description);

        chartGamePerType = (HorizontalBarChart) view.findViewById(R.id.captain_graphs_games_per_type);
        chartGamePerNation = (PieChart) view.findViewById(R.id.captain_graphs_games_per_nation);

        pbDistanceTraveled = (ProgressBar) view.findViewById(R.id.captain_distance_traveled_progress);
        tvDistanceTraveled = (TextView) view.findViewById(R.id.captain_distance_traveled);
        tvDistanceTotal = (TextView) view.findViewById(R.id.captain_distance_traveled_text);

//        captainAddView = view.findViewById(R.id.captain_checkbox_area);
//        captainCheckBox = (CheckBox) view.findViewById(R.id.captain_checkbox);

        tvWins = (TextView) view.findViewById(R.id.captain_general_wins);
        tvLosses = (TextView) view.findViewById(R.id.captain_general_losses);
        tvDraws = (TextView) view.findViewById(R.id.captain_general_draws);

        tvMainBatteryAcc = (TextView) view.findViewById(R.id.captain_general_main_accuracy);
        tvTorpAcc = (TextView) view.findViewById(R.id.captain_general_torp_accuracy);

        tvTotalCaptures = (TextView) view.findViewById(R.id.captain_general_total_captures);
        tvTotalDefReset = (TextView) view.findViewById(R.id.captain_general_total_def_points);
        tvTotalPlanes = (TextView) view.findViewById(R.id.captain_general_total_planes);

        lastBattleTime = (TextView) view.findViewById(R.id.captain_general_last_battle);
        createdOnTime = (TextView) view.findViewById(R.id.captain_general_created_date);

        tvSurvivalRate = (TextView) view.findViewById(R.id.captain_general_survival_rate);
        tvSurvivedWins = (TextView) view.findViewById(R.id.captain_general_survived_wins);

        tvCARating = (TextView) view.findViewById(R.id.averages_car);
        tvCADiff = (TextView) view.findViewById(R.id.averages_car_dif);

        aAverage = view.findViewById(R.id.averages_grid_area);

        gAverages = (NonScrollableGridView) view.findViewById(R.id.averages_grid);
        chartAverages = (RadarChart) view.findViewById(R.id.averages_chart);

        aPrivateArea = view.findViewById(R.id.captain_private_area);

        chartGamemodes = (PieChart) view.findViewById(R.id.captain_graphs_games_per_mode);
        chartWRModes = (BarChart) view.findViewById(R.id.captain_graphs_win_rate_per_mode);
        chartAvgDmg = (HorizontalBarChart) view.findViewById(R.id.captain_graphs_avg_dmg_per_mode);
        chartSurvivalRate = (BarChart) view.findViewById(R.id.captain_graphs_survival_rate_per_mode);

        tvGameModeTitle = view.findViewById(R.id.captain_game_mode_title);

        tvPrivateGold = (TextView) view.findViewById(R.id.captain_private_gold);
        tvPrivateCredits = (TextView) view.findViewById(R.id.captain_private_credits);
        tvPrivateBattleTime = (TextView) view.findViewById(R.id.captain_private_battle_time);
        tvPrivateFreeExp = (TextView) view.findViewById(R.id.captain_private_free_exp);
        tvPrivatePremiumExpiresOn = (TextView) view.findViewById(R.id.captain_private_premium);
        tvPrivateSlots = (TextView) view.findViewById(R.id.captain_private_slots);

        tvSpottingDamage = (TextView) view.findViewById(R.id.captain_general_total_spotting);
        tvArgoDamage = (TextView) view.findViewById(R.id.captain_general_total_argo);
        tvBuildingDamage = (TextView) view.findViewById(R.id.captain_general_total_building);
        tvArgoTorpDamage = (TextView) view.findViewById(R.id.captain_general_total_torp_argo);

        tvSuppressionCount = (TextView) view.findViewById(R.id.captain_general_total_supressions);
        tvSpottingCount = (TextView) view.findViewById(R.id.captain_general_total_spots);

        aCARatingArea = view.findViewById(R.id.averages_ca_rating_top_area);

        aOtherStats = (LinearLayout) view.findViewById(R.id.captain_statistics_area);

        chartCAContribution = (BarChart) view.findViewById(R.id.averages_contribution_chart);
        chartCARatingPerTier = (BarChart) view.findViewById(R.id.averages_ca_per_tier_chart);
        ivBreakdown = (ImageView) view.findViewById(R.id.averages_ca_rating_breakdown);
        aBreakdown = view.findViewById(R.id.ca_rating_breakdown_area);
        aBreakdownCharts = view.findViewById(R.id.averages_contribution_chart_area);

        bindSwipe(view);
        initSwipeLayout();

        UIUtils.setUpCard(view, R.id.captain_general_area);
        UIUtils.setUpCard(view, R.id.captain_private_area);
    }

    @Override
    public void onResume() {
        super.onResume();
        CAApp.getEventBus().register(this);
        initView();
    }

    private void initView() {
        Captain captain = null;
        try {
            captain = ((ICaptain) getActivity()).getCaptain(getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (captain != null && captain.getDetails() != null) {
            refreshing(false);
            CaptainDetails details = captain.getDetails();
            float battles = details.getBattles();

            //dates
            DateFormat df = Utils.getDayMonthYearFormatter(getActivity());
            Calendar lastBattle = Calendar.getInstance();
            lastBattle.setTimeInMillis(captain.getDetails().getLastBattleTime() * 1000);
            lastBattleTime.setText(df.format(lastBattle.getTime()));

            Calendar createdOn = Calendar.getInstance();
            createdOn.setTimeInMillis(captain.getDetails().getCreatedAt() * 1000);
            createdOnTime.setText(df.format(createdOn.getTime()));

            tvGenProfileLevel.setText("" + details.getTierLevel());

            if (battles > 0) {
                DecimalFormat bigNumFormatter = new DecimalFormat("###,###,###");
                tvBattles.setText(Utils.getDefaultDecimalFormatter().format(battles) + "");

                int avgDamage = (int) (details.getTotalDamage() / battles);
                tvAverageDamage.setText("" + avgDamage);

                int avgExp = (int) (details.getTotalXP() / battles);
                tvAverageExp.setText(avgExp + "");
                float kdBattles = battles;
                if (kdBattles != details.getSurvivedBattles()) {
                    kdBattles = battles - details.getSurvivedBattles();
                }
                float kd = (float) details.getFrags() / kdBattles;
                tvKillDeath.setText("" + Utils.getDefaultDecimalFormatter().format(kd));

                float winRate = ((float) details.getWins() / battles) * 100;
                tvWinRate.setText(Utils.getDefaultDecimalFormatter().format(winRate) + "%");

                tvGenXP.setText("" + bigNumFormatter.format(details.getTotalXP()));
                double totalDamage = details.getTotalDamage();
                if (totalDamage > 1000000) {
                    totalDamage = totalDamage / 1000000;
                    tvGenDamage.setText(Utils.getDefaultDecimalFormatter().format(totalDamage) + getString(R.string.million));
                } else {
                    tvGenDamage.setText("" + totalDamage);
                }

                tvGenDropped.setText(Utils.getDefaultDecimalFormatter().format(((float) details.getDroppedCapturePoints() / battles)) + "");
                tvGenCapture.setText(Utils.getDefaultDecimalFormatter().format(((float) details.getCapturePoints() / battles)) + "");
                tvGenPlanesKilled.setText(Utils.getDefaultDecimalFormatter().format(((float) details.getPlanesKilled() / battles)) + "");

                tvTotalPlanes.setText("" + bigNumFormatter.format(details.getPlanesKilled()));
                tvTotalCaptures.setText("" + bigNumFormatter.format(details.getCapturePoints()));
                tvTotalDefReset.setText("" + bigNumFormatter.format(details.getDroppedCapturePoints()));

                tvTierAverage.setText(getString(R.string.average_tier) + ": " + Utils.getDefaultDecimalFormatter().format(details.getAverageTier()));

                if(details.getMainBattery().getShots() > 0)
                    tvMainBatteryAcc.setText(Utils.getOneDepthDecimalFormatter().format(details.getMainBattery().getHits() / (float) details.getMainBattery().getShots()* 100f) + "%");
                if(details.getTorpedoes().getShots() > 0)
                    tvTorpAcc.setText(Utils.getOneDepthDecimalFormatter().format(details.getTorpedoes().getHits() / (float) details.getTorpedoes().getShots()* 100f) + "%");

                tvWins.setText("" + details.getWins());
                tvLosses.setText("" + details.getLosses());
                tvDraws.setText("" + details.getDraws());

                tvSurvivalRate.setText(Utils.getOneDepthDecimalFormatter().format(((float) details.getSurvivedBattles() / battles) * 100) + "%");
                tvSurvivedWins.setText(Utils.getOneDepthDecimalFormatter().format(((float) details.getSurvivedWins() / battles) * 100) + "%");

                Dlog.d("CaptainFragment", "buildings = " + details.getBuildingDamage());
                String argoDamage = "" + details.getTotalArgoDamage();
                if(details.getTotalArgoDamage() > 1000000){
                    argoDamage = "" + Utils.getDefaultDecimalFormatter().format(details.getTotalArgoDamage() / 1000000) + getString(R.string.million);
                }
                tvArgoDamage.setText(argoDamage);

                String argoTorpDamage = "" + details.getTorpArgoDamage();
                if(details.getTotalArgoDamage() > 1000000){
                    argoTorpDamage = "" + Utils.getDefaultDecimalFormatter().format(details.getTotalArgoDamage() / 1000000) + getString(R.string.million);
                }
                tvArgoTorpDamage.setText(argoTorpDamage);

                String buildingDamage = "" + details.getBuildingDamage();
                if(details.getBuildingDamage() > 1000000){
                    buildingDamage = "" + Utils.getDefaultDecimalFormatter().format(details.getBuildingDamage() / 1000000) + getString(R.string.million);
                }
                tvBuildingDamage.setText(buildingDamage);

                String scoutingDamage = "" + details.getScoutingDamage();
                if(details.getScoutingDamage() > 1000000){
                    scoutingDamage = "" + Utils.getDefaultDecimalFormatter().format(details.getScoutingDamage() / 1000000) + getString(R.string.million);
                }
                tvSpottingDamage.setText(scoutingDamage);

                tvSpottingCount.setText("" + details.getSpotted());
                tvSuppressionCount.setText("" + details.getSuppressionCount());

                Prefs pref = new Prefs(getContext());
                boolean hasSeenGraphDescription = pref.getBoolean(SEEN_TOPICAL_GRAPH_DESCRIPTION, false);
                if (!hasSeenGraphDescription) {
                    tvTopicalDescription.setVisibility(View.VISIBLE);
                    pref.setBoolean(SEEN_TOPICAL_GRAPH_DESCRIPTION, true);
                }

                setUpCARatingExplantion();
                aCARatingArea.setVisibility(View.VISIBLE);

                setupPrivateInformation(captain, bigNumFormatter);

                setUpTopicalArea(captain);

                setUpDistanceArea(details);

                setUpCharts(captain);

                setUpAverages(CaptainManager.createCapIdStr(captain.getServer(), captain.getId()), captain.getDetails());

                setUpOtherStatistics(captain);

            } else {
                if(captain.getShips() == null)
                    refreshing(true);
                tvBattles.setText("0");
                tvWinRate.setText("0.0%");
                tvAverageDamage.setText("0");
                tvAverageExp.setText("0");
                tvKillDeath.setText("0");
            }
        } else {
            refreshing(true);
        }
    }

    private void setUpCARatingExplantion(){
        aAverage.setClickable(true);
        aAverage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getString(R.string.ca_rating_dialog_title));
                builder.setMessage(getString(R.string.ca_rating_tl_dr, Math.round(CARatingManager.DAMAGE_COEF * 100) + "%", Math.round(CARatingManager.KILLS_COEF * 100) + "%", Math.round(CARatingManager.WR_COEF * 100) + "%"));
                builder.setPositiveButton(getString(R.string.learn_more), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //move to information on CA Rating
                        Intent i = new Intent(getContext(), InformationActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(R.string.dismiss, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });
    }

    private void setUpDistanceArea(CaptainDetails details) {
        //distance traveled stuff
        float circumferance = 24902; // miles
        float traveled = details.getDistanceTraveled();
        float timesAround = traveled / circumferance;
        int progress = (int) (((traveled % circumferance) / circumferance) * 100f);
        pbDistanceTraveled.setProgress(progress);
        tvDistanceTraveled.setText(Utils.getDefaultDecimalFormatter().format(timesAround) + "");
        chartProgress.setVisibility(View.VISIBLE);
        StringBuilder sb = new StringBuilder();
        float kilos = details.getDistanceTraveled() * 1.60934f; //kilos
        DecimalFormat format = new DecimalFormat("###,###,###");
        sb.append(format.format(kilos) + "km / ");
        sb.append(format.format(details.getDistanceTraveled()) +  "m");
        tvDistanceTotal.setText(sb.toString());
    }

    private void setUpTopicalArea(Captain captain) {
        if (!CaptainManager.fromSearch(getContext(), captain.getServer(), captain.getId())) {
            //set up line graph with the last clicked top section
            View.OnClickListener onClick = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String tag = (String) v.getTag();
                    Prefs prefs = new Prefs(v.getContext());
                    prefs.setString(SELECTED_GRAPH, tag);
                    //check background
                    checkBackgrounds(tag);
                    //setUpGraph
                    setUpTopicalChart(tag);
                }
            };
            aAverageDamage.setOnClickListener(onClick);
            aAverageDamage.setTag(TAG_AVERAGE_DAMAGE);
            aBattles.setOnClickListener(onClick);
            aBattles.setTag(TAG_BATTLES);
            aWinRate.setOnClickListener(onClick);
            aWinRate.setTag(TAG_WIN_RATE);
            aKillDeath.setOnClickListener(onClick);
            aKillDeath.setTag(TAG_KILL_DEATH);
            aAverageExp.setOnClickListener(onClick);
            aAverageExp.setTag(TAG_AVERAGE_EXP);

            Prefs prefs = new Prefs(getContext());
            String selectedGraph = prefs.getString(SELECTED_GRAPH, "");
            if (TextUtils.isEmpty(selectedGraph))
                selectedGraph = TAG_AVERAGE_EXP;
            checkBackgrounds(selectedGraph);
            setUpTopicalChart(selectedGraph);
            topicalArea.setVisibility(View.VISIBLE);
        } else {
            topicalArea.setVisibility(View.GONE);
        }
    }

    private void setupPrivateInformation(Captain captain, DecimalFormat bigNumFormatter) {
        if(captain.getInformation() != null){
            aPrivateArea.setVisibility(View.VISIBLE);
            CaptainPrivateInformation info = captain.getInformation();
            int filledSlots = info.getSlots() - info.getEmptySlots();
            tvPrivateSlots.setText(filledSlots + " / " + info.getSlots());
            tvPrivateFreeExp.setText(bigNumFormatter.format(info.getFreeExp()));
            tvPrivateCredits.setText(bigNumFormatter.format(info.getCredits()));
            tvPrivateGold.setText(bigNumFormatter.format(info.getGold()));

            long battleTimeS = info.getBattleTime();

            int battleTimeM = (int) (battleTimeS / 60);
            int battleTimeH = battleTimeM / 60;
            int battleTimeD = battleTimeH / 24;
            int battleTimeW = battleTimeD / 7;
            int battleTimeY = battleTimeD / 365;

//            Dlog.d("CaptainFragment", "hours = " + battleTimeH + " s = " + battleTimeS);

            battleTimeW = battleTimeW % 52;
            battleTimeD = battleTimeD % 7;
            battleTimeH = battleTimeH % 24;
            battleTimeM = battleTimeM % 60;

            StringBuilder sb = new StringBuilder();
            if(battleTimeY > 0) {
                sb.append(battleTimeY + " " + (battleTimeY > 1 ? "years" : "year"));
                sb.append(" ");
            }
            if(battleTimeW > 0) {
                sb.append(battleTimeW + " " + (battleTimeW > 1 ? "weeks" : "week"));
                sb.append(" ");
            }
            if(battleTimeD > 0) {
                sb.append(battleTimeD + " " + (battleTimeD > 1 ? "days" : "day"));
                sb.append(" ");
            }
            if(battleTimeH > 0) {
                sb.append(battleTimeH + " " + (battleTimeH > 1 ? "hours" : "hour"));
                sb.append(" ");
            }
            if(battleTimeM > 0) {
                sb.append(battleTimeM + " " + (battleTimeM > 1 ? "minutes" : "minute"));
                sb.append(" ");
            }
            tvPrivateBattleTime.setText(sb.toString());

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(info.getPremiumExpiresAt());
            tvPrivatePremiumExpiresOn.setText(Utils.getDayMonthYearFormatter(tvPrivatePremiumExpiresOn.getContext()).format(calendar.getTime()));
        } else {
            aPrivateArea.setVisibility(View.GONE);
        }
    }

    private void checkBackgrounds(String tag) {
        if (tag.equals(TAG_AVERAGE_EXP)) {
            aAverageExp.setBackgroundResource(R.color.captain_top_background);
        } else {
            aAverageExp.setBackgroundResource(R.color.transparent);
        }
        if (tag.equals(TAG_KILL_DEATH)) {
            aKillDeath.setBackgroundResource(R.color.captain_top_background);
        } else {
            aKillDeath.setBackgroundResource(R.color.transparent);
        }
        if (tag.equals(TAG_WIN_RATE)) {
            aWinRate.setBackgroundResource(R.color.captain_top_background);
        } else {
            aWinRate.setBackgroundResource(R.color.transparent);
        }
        if (tag.equals(TAG_AVERAGE_DAMAGE)) {
            aAverageDamage.setBackgroundResource(R.color.captain_top_background);
        } else {
            aAverageDamage.setBackgroundResource(R.color.transparent);
        }
        if (tag.equals(TAG_BATTLES)) {
            aBattles.setBackgroundResource(R.color.captain_top_background);
        } else {
            aBattles.setBackgroundResource(R.color.transparent);
        }
    }

    private void setUpTopicalChart(final String tag) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Captain captain = null;
                    try {
                        captain = ((ICaptain) getActivity()).getCaptain(getContext());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    SavedDetails details = StorageManager.getPlayerStats(getContext(), CaptainManager.getCapIdStr(captain));
                    final List<String> nameValues = new ArrayList<String>();
                    List<Float> numbers = new ArrayList<Float>();
                    List<CaptainDetails> reversedDetails = details.getDetails();
                    Collections.reverse(reversedDetails);

                    setUpCADiff(captain, reversedDetails);

                    int strResId = R.string.captain_average_exp;
                    for (CaptainDetails detail : reversedDetails) {
                        float battles = detail.getBattles();
                        nameValues.add(((int) battles) + "");
                        if (tag.equals(TAG_AVERAGE_EXP)) {
                            float avgExp = detail.getTotalXP() / battles;
                            numbers.add(avgExp);
                        } else if (tag.equals(TAG_AVERAGE_DAMAGE)) {
                            strResId = R.string.captain_average_damage;
                            float avgDamage = (float) detail.getTotalDamage() / battles;
                            numbers.add(avgDamage);
                        } else if (tag.equals(TAG_BATTLES)) {
                            strResId = R.string.captain_battles;
                            float survivalRate = (detail.getSurvivedBattles() / battles) * 100;
                            numbers.add(survivalRate);
                        } else if (tag.equals(TAG_KILL_DEATH)) {
                            strResId = R.string.captain_kills_deaths;
                            float deaths = (battles - (float) detail.getSurvivedBattles());
                            if(deaths <= 1){
                                deaths = 1f;
                            }
                            float kd = (float) detail.getFrags() / deaths;
                            numbers.add(kd);
                        } else if (tag.equals(TAG_WIN_RATE)) {
                            strResId = R.string.captain_winrate;
                            float winRate = ((float) detail.getWins() / battles) * 100;
                            numbers.add(winRate);
                        }
                    }
                    final int topicalStrRes = strResId;
                    final List<Entry> yVals = new ArrayList<Entry>();
                    for (int i = 0; i < numbers.size(); i++) {
                        yVals.add(new Entry(numbers.get(i), i));
                    }
                    if(yVals.size() > 0) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                topicalText.setText(topicalStrRes);
                                topicalChart.clear();

                                boolean colorblind = CAApp.isColorblind(topicalChart.getContext());
                                int textColor = CAApp.getTextColor(topicalChart.getContext());
                                int accentColor = !colorblind ?
                                        (CAApp.getTheme(topicalChart.getContext()).equals("ocean") ?
                                                ContextCompat.getColor(topicalChart.getContext(), R.color.graph_line_color) : ContextCompat.getColor(topicalChart.getContext(), R.color.top_background))
                                        : ContextCompat.getColor(topicalChart.getContext(), R.color.white);

                                topicalChart.setDoubleTapToZoomEnabled(false);
                                topicalChart.setPinchZoom(false);

                                topicalChart.setDescription("");

                                topicalChart.setDragEnabled(false);
                                topicalChart.setScaleEnabled(false);
                                topicalChart.setDrawGridBackground(false);

                                topicalChart.getLegend().setEnabled(false);

                                XAxis xAxis = topicalChart.getXAxis();
                                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                                xAxis.setTextColor(textColor);
                                xAxis.setDrawGridLines(true);

                                YAxis yAxis = topicalChart.getAxisRight();
                                yAxis.setEnabled(false);

                                YAxis yAxis2 = topicalChart.getAxisLeft();
                                yAxis2.setLabelCount(6, true);
                                yAxis2.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
                                yAxis2.setTextColor(textColor);
                                if (yFormatter == null)
                                    yFormatter = new MyYFormatter();
                                yFormatter.change(tag);
                                yAxis2.setValueFormatter(yFormatter);

                                LineDataSet set = new LineDataSet(yVals, "");

                                set.setColor(accentColor);
                                set.setLineWidth(2f);
                                set.setCircleRadius(3f);
                                set.setFillColor(accentColor);
                                set.setDrawValues(false);
                                set.setCircleColor(accentColor);

                                List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
                                dataSets.add(set);

                                LineData data = new LineData(nameValues, dataSets);

                                if (formatter == null)
                                    formatter = new MyFormatter();
                                formatter.change(tag);
                                data.setValueFormatter(formatter);

                                topicalChart.setData(data);
                                topicalChart.requestLayout();
                                chartProgress.setVisibility(View.GONE);

                                topicalChart.animateX(750);
                            }
                        });
                    } else {
                        topicalArea.setVisibility(View.VISIBLE);
                        chartProgress.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(runnable).start();
        chartProgress.setVisibility(View.VISIBLE);
    }

    private void setUpCADiff(Captain captain, List<CaptainDetails> reversedDetails) {
        try {
            final CaptainDetails last = reversedDetails.get(reversedDetails.size() - 2);
            final float currentRating = captain.getDetails().getCARating();
            if(last != null){
                tvCARating.post(new Runnable() {
                    @Override
                    public void run() {
                        if (last.getCARating() != 0) {
                            Dlog.d("LastCARating", "last = " + last.getCARating() + " cur = " + currentRating);
                            float dif = currentRating - last.getCARating();
                            if (Math.abs(dif) > 0) {
                                StringBuilder sb = new StringBuilder();
                                if (dif > 0) {
                                    sb.append("+");
                                }
                                sb.append(Utils.getOneDepthDecimalFormatter().format(dif));
                                tvCADiff.setText(sb.toString());
                                int colorResId = R.color.average_down;
                                if (dif > 0) {
                                    colorResId = R.color.average_up;
                                }
                                tvCADiff.setTextColor(ContextCompat.getColor(tvCARating.getContext(), colorResId));
                                tvCADiff.setVisibility(View.VISIBLE);
                            } else {
                                tvCADiff.setVisibility(View.GONE);
                            }
                        } else {
                            tvCADiff.setVisibility(View.GONE);
                        }
                    }
                });
            }
        } catch (Exception e) {
        }
    }

    private void setUpAverages(final String captainId, final CaptainDetails captain) {
        Prefs prefs = new Prefs(chartAverages.getContext());
        boolean showCompare = prefs.getBoolean(SettingActivity.SHOW_COMPARE, true);
        if(showCompare) {
            aAverage.setVisibility(View.VISIBLE);
            ivBreakdown.setVisibility(View.VISIBLE);
            aBreakdown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(aBreakdownCharts.getVisibility() == View.GONE){
                        aBreakdownCharts.setVisibility(View.VISIBLE);
                        ivBreakdown.setImageResource(R.drawable.ic_collapse);
                    } else {
                        aBreakdownCharts.setVisibility(View.GONE);
                        ivBreakdown.setImageResource(R.drawable.ic_expand);
                    }
                }
            });
            aBreakdownCharts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    aBreakdownCharts.setVisibility(View.GONE);
                    ivBreakdown.setImageResource(R.drawable.ic_expand);
                }
            });
            if(aBreakdownCharts.getVisibility() == View.GONE){
                ivBreakdown.setImageResource(R.drawable.ic_expand);
            } else {
                ivBreakdown.setImageResource(R.drawable.ic_collapse);
            }
            Runnable runnable = new Runnable() {
                public void run() {
                    setUpAveragesRadarChart();
                }

                private String cleanTitleString(int id){
                    String str = getString(id);
                    if(str.length() > 14){
                        str = str.substring(0,15).trim() + "...";
                    }
                    return str;
                }

                private void setUpAveragesRadarChart() {
                    try {
                        boolean isColorblind = CAApp.isColorblind(chartAverages.getContext());
                        int color = !isColorblind ?
                                (CAApp.getTheme(topicalChart.getContext()).equals("ocean") ?
                                        ContextCompat.getColor(topicalChart.getContext(), R.color.graph_line_color) : ContextCompat.getColor(topicalChart.getContext(), R.color.top_background))
                                : ContextCompat.getColor(topicalChart.getContext(), R.color.white);
                        int textColor = CAApp.getTextColor(chartAverages.getContext());
                        int webTextColor = ContextCompat.getColor(chartAverages.getContext(), R.color.web_text_color);

                        chartAverages.setDescription(getString(R.string.baseline));
                        chartAverages.setDescriptionColor(webTextColor);

                        chartAverages.setWebLineWidth(1.0f);
                        chartAverages.setWebAlpha(200);
                        chartAverages.setWebLineWidthInner(0.75f);
                        chartAverages.setWebColor(ContextCompat.getColor(chartAverages.getContext(), R.color.transparent));
                        chartAverages.setWebColorInner(ContextCompat.getColor(chartAverages.getContext(), R.color.transparent));

                        chartAverages.setTouchEnabled(false);
                        chartAverages.setClickable(false);
                        chartAverages.setFocusableInTouchMode(false);

                        RadarMarkerView mv = new RadarMarkerView(chartAverages.getContext(), R.layout.custom_marker_view);
                        chartAverages.setMarkerView(mv);

                        XAxis xAxis = chartAverages.getXAxis();
                        xAxis.setTextSize(9f);
                        xAxis.setTextColor(textColor);

                        YAxis yAxis = chartAverages.getYAxis();
                        yAxis.setLabelCount(4, false);
                        yAxis.setTextSize(9f);
                        yAxis.setTextColor(webTextColor);
                        yAxis.setAxisMinValue(0f);

                        Legend l = chartAverages.getLegend();
                        l.setEnabled(false);

                        List<String> titles = new ArrayList<>();
                        titles.add(cleanTitleString(R.string.damage));
                        titles.add(cleanTitleString(R.string.short_kills_game));
                        titles.add(cleanTitleString(R.string.short_win_rate));
                        titles.add(cleanTitleString(R.string.short_cap_points));
//                        titles.add(cleanTitleString(R.string.short_def_reset));
//                        titles.add(cleanTitleString(R.string.short_planes_game));

                        List<Entry> yVals = new ArrayList<>();

                        yVals.add(new Entry((captain.getcDamage() / captain.getExpectedDamage()) * 100, 0));
                        yVals.add(new Entry((captain.getcKills() / captain.getExpectedKills()) * 100, 1));
                        yVals.add(new Entry((captain.getcWinRate() / captain.getExpectedWinRate()) * 100, 2));
                        yVals.add(new Entry((captain.getcPlanes() / captain.getExpectedPlanes()) * 100, 3));

//                        yVals.add(new Entry((captain.getcCaptures() / captain.getExpectedCaptures()) * 100, 3));
//                        yVals.add(new Entry((captain.getcDefReset() / captain.getExpectedDefReset()) * 100, 4));

                        RadarDataSet set = new RadarDataSet(yVals, "");
                        set.setColor(color);
                        set.setDrawFilled(true);
                        set.setLineWidth(2f);

                        List<IRadarDataSet> sets = new ArrayList<>();
                        sets.add(set);

                        RadarData data = new RadarData(titles, sets);
                        data.setValueTextColor(textColor);
                        data.setValueTextSize(8f);

                        chartAverages.setSkipWebLineCount(2);
                        chartAverages.setData(data);
                        chartAverages.invalidate();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            chartAverages.post(runnable);

            float caRating = captain.getCARating();
            tvCARating.setText(Utils.getOneDepthDecimalFormatter().format(caRating) + "");

            final List<ListAverages> averages = new ArrayList<ListAverages>();
            averages.add(ListAverages.create(getString(R.string.damage), captain.getcDamage(), captain.getExpectedDamage(), AverageType.LARGE_NUMBER));
            averages.add(ListAverages.create(getString(R.string.kills_game), captain.getcKills(), captain.getExpectedKills(), AverageType.DEFAULT));
            averages.add(ListAverages.create(getString(R.string.win_rate), captain.getcWinRate(), captain.getExpectedWinRate(), AverageType.PERCENT));

            averages.add(ListAverages.create(getString(R.string.planes_downed_game), captain.getcPlanes(), captain.getExpectedPlanes(), AverageType.DEFAULT));

            averagesAdapter = new AveragesAdapter(getContext(), R.layout.list_averages, averages);
            gAverages.setAdapter(averagesAdapter);

        } else {
            aAverage.setVisibility(View.GONE);
        }
    }

    private class MyFormatter implements ValueFormatter {

        private DecimalFormat mFormat;
        private boolean isPercent;

        public MyFormatter() {
            this.mFormat = new DecimalFormat("###,###,##0.0");
        }

        public void change(String tag) {
            if (tag.equals(TAG_AVERAGE_EXP) || tag.equals(TAG_AVERAGE_DAMAGE)) {
                this.mFormat = new DecimalFormat("###,###,##0");
            } else {
                this.mFormat = new DecimalFormat("###,###,##0.0");
            }
            isPercent = tag.equals(TAG_WIN_RATE) || tag.equals(TAG_BATTLES);
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return mFormat.format(value) + (isPercent ? "%" : "");
        }
    }

    private class MyYFormatter implements YAxisValueFormatter {

        private DecimalFormat mFormat;
        private boolean isPercent;

        public MyYFormatter() {
            this.mFormat = new DecimalFormat("###,###,##0.0");
        }

        public void change(String tag) {
            if (tag.equals(TAG_AVERAGE_EXP) || tag.equals(TAG_AVERAGE_DAMAGE)) {
                this.mFormat = new DecimalFormat("###,###,##0");
            } else if (tag.equals(TAG_KILL_DEATH)) {
                this.mFormat = new DecimalFormat("###,###,##0.00");
            } else {
                this.mFormat = new DecimalFormat("###,###,##0.0");
            }
            isPercent = tag.equals(TAG_WIN_RATE) || tag.equals(TAG_BATTLES);
        }

        @Override
        public String getFormattedValue(float value, YAxis yAxis) {
            return mFormat.format(value) + (isPercent ? "%" : "");
        }
    }

    private void setUpCharts(final Captain captain) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    final SparseArray<Integer> battleCounts = new SparseArray<>();
                    final Map<String, Integer> gamesPerType = new HashMap<String, Integer>();
                    final Map<String, Integer> gamesPerNation = new HashMap<String, Integer>();

                    SparseArray<Float> ratingsPerTier = new SparseArray<>();
                    SparseArray<Float> shipsPerTier = new SparseArray<>(); // this is to average the ratings
                    SparseArray<Float> battlePerTier = new SparseArray<>();

                    ShipsHolder shipsHolder = CAApp.getInfoManager().getShipInfo(getContext());
                    for (Ship s : captain.getShips()) {
                        ShipInfo info = shipsHolder.get(s.getShipId());
                        if (info != null) {
                            int tier = info.getTier();
                            Integer battleCount = battleCounts.get(tier);
                            if (battleCount != null) {
                                battleCounts.put(tier, battleCount + s.getBattles());
                            } else {
                                battleCounts.put(tier, s.getBattles());
                            }
                            Integer gameType = gamesPerType.get(info.getType());
                            if (gameType != null) {
                                gamesPerType.put(info.getType(), gameType + s.getBattles());
                            } else {
                                gamesPerType.put(info.getType(), s.getBattles());
                            }
                            Integer nationType = gamesPerNation.get(info.getNation());
                            if (nationType != null) {
                                gamesPerNation.put(info.getNation(), nationType + s.getBattles());
                            } else {
                                gamesPerNation.put(info.getNation(), s.getBattles());
                            }
                            GetCaptainTask.addTierNumber(ratingsPerTier, tier, s.getCARating());
                            GetCaptainTask.addTierNumber(shipsPerTier, tier, 1);
                            GetCaptainTask.addTierNumber(battlePerTier, tier, s.getBattles());
                        }
                    }
                    final Map<String, Float> caPerTier = new HashMap<>();
                    final Map<String, Float> caContribPerTier = new HashMap<>();
                    for( int i = 1; i <= shipsPerTier.size(); i++){
                        Float ratingTotal = ratingsPerTier.get(i);
                        Float shipsTotal = shipsPerTier.get(i);
                        Float battlesTotal = battlePerTier.get(i);

                        if(ratingTotal != null && shipsTotal != null && battlesTotal != null && battlesTotal > 0){
                            // avgRating = total rating per tier / ship per tier
                            float tierRatingAverage = ratingTotal / shipsTotal;
                            // percentageRatio = total battles per tier / total games
                            float tierRatio = battlesTotal / captain.getDetails().getBattles();
//                            Dlog.d("CARating", tierRatingAverage + " ratio = " + tierRatio + " tier = " + i + " ratio = " + (tierRatingAverage * tierRatio));
                            caPerTier.put(i + "", tierRatingAverage);

                            caContribPerTier.put(i + "", tierRatingAverage * tierRatio);
                        }
                    }

                    final List<String> modeStrings = new ArrayList<>();
                    final Map<String, Integer> gamesPerMode = new HashMap<>();
                    final Map<String, Float> winRatePerMode = new HashMap<>();
                    final Map<String, Float> avgDamageMode = new HashMap<>();
                    final Map<String, Float> survivalRateMode = new HashMap<>();
                    int soloBattles = captain.getDetails().getBattles()
                            - captain.getPveDetails().getBattles()
                            - captain.getPvpDiv2Details().getBattles()
                            - captain.getPvpDiv3Details().getBattles()
                            - captain.getTeamBattleDetails().getBattles();
                    if(soloBattles > 0) {
                        String str = getString(R.string.solo_pvp);
                        modeStrings.add(str);
                        gamesPerMode.put(str, soloBattles);
                        float winRate = (captain.getDetails().getWins() / (float) captain.getDetails().getBattles()) * 100;
                        winRatePerMode.put(str, winRate);
                        avgDamageMode.put(str, (float) (captain.getDetails().getTotalDamage() / (float) captain.getDetails().getBattles()));
                        float survivedWins = (captain.getDetails().getSurvivedWins() / (float) captain.getDetails().getBattles()) * 100;
                        survivalRateMode.put(str, survivedWins);
                    }
                    if(captain.getPveDetails().getBattles() > 0) {
                        String str = getString(R.string.pve);
                        modeStrings.add(str);
                        gamesPerMode.put(str, captain.getPveDetails().getBattles());
                        float winRate = (captain.getPveDetails().getWins() / (float) captain.getPveDetails().getBattles()) * 100;
                        winRatePerMode.put(str, winRate);
                        avgDamageMode.put(str, (float) (captain.getPveDetails().getTotalDamage() / (float) captain.getPveDetails().getBattles()));
                        float survivedWins = (captain.getPveDetails().getSurvivedWins() / (float) captain.getPveDetails().getBattles()) * 100;
                        survivalRateMode.put(str, survivedWins);
                    }
                    if(captain.getPvpDiv2Details().getBattles() > 0) {
                        String str = getString(R.string.pvp_2_div);
                        modeStrings.add(str);
                        gamesPerMode.put(str, captain.getPvpDiv2Details().getBattles());
                        float winRate = (captain.getPvpDiv2Details().getWins() / (float) captain.getPvpDiv2Details().getBattles()) * 100;
                        winRatePerMode.put(str, winRate);
                        avgDamageMode.put(str, (float) (captain.getPvpDiv2Details().getTotalDamage() / (float) captain.getPvpDiv2Details().getBattles()));
                        float survivedWins = (captain.getPvpDiv2Details().getSurvivedWins() / (float) captain.getPvpDiv2Details().getBattles()) * 100;
                        survivalRateMode.put(str, survivedWins);
                    }
                    if(captain.getPvpDiv3Details().getBattles() > 0) {
                        String str = getString(R.string.pvp_3_div);
                        modeStrings.add(str);
                        gamesPerMode.put(str, captain.getPvpDiv3Details().getBattles());
                        float winRate = (captain.getPvpDiv3Details().getWins() / (float) captain.getPvpDiv3Details().getBattles()) * 100;
                        winRatePerMode.put(str, winRate);
                        avgDamageMode.put(str, (float) (captain.getPvpDiv3Details().getTotalDamage() / (float) captain.getPvpDiv3Details().getBattles()));
                        float survivedWins = (captain.getPvpDiv3Details().getSurvivedWins() / (float) captain.getPvpDiv3Details().getBattles()) * 100;
                        survivalRateMode.put(str, survivedWins);
                    }
                    if(captain.getTeamBattleDetails().getBattles() > 0) {
                        String str = getString(R.string.team_battles);
                        modeStrings.add(str);
                        gamesPerMode.put(str, captain.getTeamBattleDetails().getBattles());
                        float winRate = (captain.getTeamBattleDetails().getWins() / (float) captain.getTeamBattleDetails().getBattles()) * 100;
                        winRatePerMode.put(str, winRate);
                        avgDamageMode.put(str, (float) (captain.getTeamBattleDetails().getTotalDamage() / (float) captain.getTeamBattleDetails().getBattles()));
                        float survivedWins = (captain.getTeamBattleDetails().getSurvivedWins() / (float) captain.getTeamBattleDetails().getBattles()) * 100;
                        survivalRateMode.put(str, survivedWins);
                    }
                    if(captain.getRankedSeasons() != null){
                        int ranked = 0;
                        float wins = 0;
                        float damage = 0;
                        float survivedBattles = 0;
                        for(RankedInfo info : captain.getRankedSeasons()){
                            try {
                                ranked += info.getSolo().getBattles();
                                wins += info.getSolo().getWins();
                                damage += info.getSolo().getDamage();
                                survivedBattles += info.getSolo().getSurvived();
                            } catch (Exception e) {
                            }
                        }
                        if(ranked > 0){
                            String rankedStr = getString(R.string.ranked);
                            modeStrings.add(rankedStr);
                            gamesPerMode.put(rankedStr, ranked);
                            float winRate = (wins / (float) ranked) * 100;
                            winRatePerMode.put(rankedStr, winRate);
                            avgDamageMode.put(rankedStr, damage / (float) ranked);
                            float survivedWins = (survivedBattles / (float) ranked) * 100;
                            survivalRateMode.put(rankedStr, survivedWins);
                        }
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                setUpTiersChart();

                                setUpGamesTypeChart();

                                setUpGamesNationChart();

                                setUpGamesPerModeChart();

                                setUpWRPerModeChart();

                                setUpAvgDmgPerModeChart();

                                setUpSurvivalRatePerModeChart();

                                setUpCAChart(chartCAContribution, caContribPerTier);

                                setUpCAChart(chartCARatingPerTier, caPerTier);

                                chartProgress.setVisibility(View.GONE);
                            } catch (Exception e) {
                            }
                        }

                        private void setUpCAChart(BarChart chart, Map<String, Float> map) {
                            if(map.size() > 0){
                                int textColor = CAApp.getTextColor(chart.getContext());
                                boolean colorblind = CAApp.isColorblind(chart.getContext());
                                int accentColor = !colorblind ?
                                        (CAApp.getTheme(topicalChart.getContext()).equals("ocean") ?
                                                ContextCompat.getColor(topicalChart.getContext(), R.color.graph_line_color) : ContextCompat.getColor(topicalChart.getContext(), R.color.top_background))
                                        : ContextCompat.getColor(topicalChart.getContext(), R.color.white);
                                chart.setDrawBarShadow(false);
                                chart.setDrawValueAboveBar(false);
                                chart.setPinchZoom(false);
                                chart.setDoubleTapToZoomEnabled(false);
                                chart.setDrawGridBackground(false);
                                chart.setDrawValueAboveBar(true);
                                chart.setTouchEnabled(false);

                                XAxis xAxis = chart.getXAxis();
                                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                                xAxis.setTextColor(textColor);
                                xAxis.setDrawGridLines(true);

                                YAxis yAxis = chart.getAxisRight();
                                yAxis.setLabelCount(6, false);
                                yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
                                yAxis.setTextColor(textColor);
                                yAxis.setEnabled(false);

                                YAxis yAxis2 = chart.getAxisLeft();
                                yAxis2.setLabelCount(6, false);
                                yAxis2.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
                                yAxis2.setTextColor(textColor);

                                Legend l = chart.getLegend();
                                l.setEnabled(false);
                                List<String> xVals = new ArrayList<String>();
                                for (int i = 1; i <= 10; i++)
                                    xVals.add(i + "");

                                List<BarEntry> yVals = new ArrayList<BarEntry>();
                                for (int i = 0; i < xVals.size(); i++) {
                                    if (map.get(xVals.get(i)) != null)
                                        yVals.add(new BarEntry(map.get(xVals.get(i)), i));
                                    else
                                        yVals.add(new BarEntry(0, i));
                                }

                                BarDataSet set1 = new BarDataSet(yVals, "");
                                set1.setColor(accentColor);
                                set1.setBarSpacePercent(20f);

                                ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
                                dataSets.add(set1);

                                BarData data = new BarData(xVals, dataSets);
                                data.setValueTextSize(10f);
                                data.setValueTextColor(textColor);
                                chart.setDescription("");
                                chart.setData(data);
                                chart.requestLayout();
                            } else {

                            }
                        }

                        private void setUpGamesPerModeChart() {
                            if(gamesPerMode.size() > 0) {
                                int textColor = CAApp.getTextColor(chartGamemodes.getContext());
                                boolean colorblind = CAApp.isColorblind(chartGamemodes.getContext());

                                chartGamemodes.setRotationEnabled(false);

                                chartGamemodes.setDrawHoleEnabled(true);
                                chartGamemodes.setHoleColor(R.color.transparent);
                                chartGamemodes.setTransparentCircleRadius(50);
                                chartGamemodes.setHoleRadius(50);

                                chartGamemodes.setDrawSliceText(false);

                                Legend l = chartGamemodes.getLegend();
                                l.setTextColor(textColor);
                                l.setPosition(Legend.LegendPosition.LEFT_OF_CHART);
                                l.setForm(Legend.LegendForm.CIRCLE);

                                ArrayList<String> xVals = new ArrayList<String>();
                                xVals.addAll(modeStrings);

                                ArrayList<Entry> yVals1 = new ArrayList<Entry>();
                                for (int i = 0; i < xVals.size(); i++) {
                                    double dValue = gamesPerMode.get(xVals.get(i));
                                    float value = (float) dValue;
                                    yVals1.add(new Entry(value, i));
                                }

                                for (int j = 0; j < xVals.size(); j++) {
                                    String name = xVals.get(j);
                                    xVals.set(j, name);
                                }

                                List<Integer> colorList = new ArrayList<Integer>();
                                colorList.add(ContextCompat.getColor(chartGamemodes.getContext(), R.color.solo_pvp_color));
                                colorList.add(ContextCompat.getColor(chartGamemodes.getContext(), R.color.pve_color));
                                colorList.add(ContextCompat.getColor(chartGamemodes.getContext(), R.color.average_up));
                                colorList.add(ContextCompat.getColor(chartGamemodes.getContext(), R.color.div_pvp_3));
                                colorList.add(ContextCompat.getColor(chartGamemodes.getContext(), R.color.div_pvp_2));
                                colorList.add(ContextCompat.getColor(chartGamemodes.getContext(), R.color.ranked_color));

                                PieDataSet dataSet = new PieDataSet(yVals1, "");

                                ArrayList<PieDataSet> dataSets = new ArrayList<PieDataSet>();
                                dataSets.add(dataSet);

                                dataSet.setColors(colorList);

                                PieData data = new PieData(xVals, dataSet);
                                data.setValueTextColor(ContextCompat.getColor(chartGamemodes.getContext(), R.color.black));
                                data.setValueTextSize(14);
                                chartGamemodes.setDescription("");
                                data.setValueFormatter(new LargeValueFormatter());

                                chartGamemodes.highlightValues(null);

                                chartGamemodes.setData(data);
                                chartGamemodes.requestLayout();
                                tvGameModeTitle.setVisibility(View.VISIBLE);
                            } else {
                                tvGameModeTitle.setVisibility(View.GONE);
                            }
                        }

                        private void setUpGamesNationChart() {
                            if(gamesPerNation.size() > 1) {
                                int textColor = CAApp.getTextColor(chartGamePerNation.getContext());
                                boolean colorblind = CAApp.isColorblind(chartGamePerNation.getContext());

                                chartGamePerNation.setRotationEnabled(false);

                                chartGamePerNation.setDrawHoleEnabled(true);
                                chartGamePerNation.setHoleColor(R.color.transparent);
                                chartGamePerNation.setTransparentCircleRadius(50);
                                chartGamePerNation.setHoleRadius(50);

                                chartGamePerNation.setDrawSliceText(false);


                                Legend l = chartGamePerNation.getLegend();
                                l.setTextColor(textColor);
                                l.setPosition(Legend.LegendPosition.LEFT_OF_CHART);
                                l.setForm(Legend.LegendForm.CIRCLE);

                                ArrayList<String> xVals = new ArrayList<String>();
                                Iterator<String> itea = gamesPerNation.keySet().iterator();
                                List<Integer> colorList = new ArrayList<Integer>();
                                while (itea.hasNext()) {
                                    String key = itea.next();
                                    xVals.add(key);
                                    if (key.equals("ussr")) {
                                        colorList.add(Color.parseColor("#F44336")); // RED
                                    } else if (key.equals("germany")) {
                                        colorList.add(Color.parseColor("#9E9E9E")); // blackish
                                    } else if (key.equals("usa")) {
                                        colorList.add(Color.parseColor("#2196F3")); // Blue
                                    } else if (key.equals("poland")) {
                                        colorList.add(Color.parseColor("#FAFA00")); // yellow
                                    } else if (key.equals("japan")) {
                                        colorList.add(Color.parseColor("#4CAF50")); // Green
                                    } else if (key.equals("uk")) {
                                        colorList.add(Color.parseColor("#E1F5FE")); // whiteish blue
                                    }
                                }
                                colorList.add(Color.parseColor("#AAE157"));
                                colorList.add(Color.parseColor("#FF9800"));
                                colorList.add(Color.parseColor("#22FFCB"));
                                colorList.add(Color.parseColor("#795548"));

                                ArrayList<Entry> yVals1 = new ArrayList<Entry>();
                                for (int i = 0; i < xVals.size(); i++) {
                                    double dValue = gamesPerNation.get(xVals.get(i));
                                    float value = (float) dValue;
                                    yVals1.add(new Entry(value, i));
                                }

                                for (int j = 0; j < xVals.size(); j++) {
                                    String name = xVals.get(j);
                                    String newStr = UIUtils.getNationText(getContext(), name);
                                    if (newStr != null)
                                        xVals.set(j, newStr);
                                }


                                PieDataSet dataSet = new PieDataSet(yVals1, "");

                                ArrayList<PieDataSet> dataSets = new ArrayList<PieDataSet>();
                                dataSets.add(dataSet);

                                dataSet.setColors(colorList);

                                PieData data = new PieData(xVals, dataSet);
                                data.setValueTextColor(ContextCompat.getColor(chartGamePerNation.getContext(), R.color.black));
                                data.setValueTextSize(14);
                                chartGamePerNation.setDescription("");
                                data.setValueFormatter(new LargeValueFormatter());

                                chartGamePerNation.highlightValues(null);

                                chartGamePerNation.setData(data);
                                chartGamePerNation.requestLayout();
                            }
                        }

                        private void setUpGamesTypeChart() {
                            if(gamesPerType.size() > 0) {
                                int textColor = CAApp.getTextColor(chartGamePerType.getContext());
                                boolean colorblind = CAApp.isColorblind(chartGamePerType.getContext());

                                chartGamePerType.setDrawBarShadow(false);
                                chartGamePerType.setDrawValueAboveBar(false);
                                chartGamePerType.setPinchZoom(false);
                                chartGamePerType.setDoubleTapToZoomEnabled(false);
                                chartGamePerType.setDrawGridBackground(false);
                                chartGamePerType.setDrawValueAboveBar(true);
                                chartGamePerType.setTouchEnabled(false);

                                XAxis xAxis = chartGamePerType.getXAxis();
                                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                                xAxis.setTextColor(textColor);
                                xAxis.setDrawGridLines(true);

                                YAxis yAxis = chartGamePerType.getAxisRight();
                                yAxis.setLabelCount(4, false);
                                yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
                                yAxis.setTextColor(textColor);
                                yAxis.setEnabled(false);

                                YAxis yAxis2 = chartGamePerType.getAxisLeft();
                                yAxis2.setLabelCount(6, false);
                                yAxis2.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
                                yAxis2.setTextColor(textColor);

                                Legend l = chartGamePerType.getLegend();
                                l.setEnabled(false);

                                ArrayList<String> xVals = new ArrayList<String>();
                                Iterator<String> itea = gamesPerType.keySet().iterator();
                                List<Integer> colorList = new ArrayList<Integer>();
                                while (itea.hasNext()) {
                                    String key = itea.next();
                                    if (key.equalsIgnoreCase("cruiser")) {
                                        colorList.add(Color.parseColor("#4CAF50"));
                                    } else if (key.equalsIgnoreCase("battleship")) {
                                        colorList.add(Color.parseColor("#F44336"));
                                    } else if (key.equalsIgnoreCase("aircarrier")) {
                                        colorList.add(Color.parseColor("#673AB7"));
                                    } else if (key.equalsIgnoreCase("destroyer")) {
                                        colorList.add(Color.parseColor("#FDD835"));
                                    }

                                    xVals.add(key);
                                }
                                colorList.add(Color.parseColor("#009688"));
                                colorList.add(Color.parseColor("#795548"));

                                ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
                                for (int i = 0; i < xVals.size(); i++) {
                                    double dValue = gamesPerType.get(xVals.get(i));
                                    float value = (float) dValue;
                                    yVals1.add(new BarEntry(value, i));
                                }

                                BarDataSet set1 = new BarDataSet(yVals1, "");
                                set1.setBarSpacePercent(20f);
                                set1.setColors(colorList);

                                ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
                                dataSets.add(set1);

                                BarData data = new BarData(xVals, dataSets);
                                data.setValueTextSize(10f);
                                data.setValueTextColor(textColor);
                                data.setValueFormatter(new LargeValueFormatter());

                                chartGamePerType.setDescription("");

                                chartGamePerType.setData(data);
                                chartGamePerType.requestLayout();
                            }
                        }

                        private void setUpTiersChart() {
                            if(battleCounts.size() > 0) {
                                int textColor = CAApp.getTextColor(tiersChart.getContext());
                                boolean colorblind = CAApp.isColorblind(tiersChart.getContext());
                                int accentColor = !colorblind ?
                                (CAApp.getTheme(topicalChart.getContext()).equals("ocean") ?
                                        ContextCompat.getColor(topicalChart.getContext(), R.color.graph_line_color) : ContextCompat.getColor(topicalChart.getContext(), R.color.top_background))
                                : ContextCompat.getColor(topicalChart.getContext(), R.color.white);
                                tiersChart.setDrawBarShadow(false);
                                tiersChart.setDrawValueAboveBar(false);
                                tiersChart.setPinchZoom(false);
                                tiersChart.setDoubleTapToZoomEnabled(false);
                                tiersChart.setDrawGridBackground(false);
                                tiersChart.setDrawValueAboveBar(true);
                                tiersChart.setTouchEnabled(false);

                                XAxis xAxis = tiersChart.getXAxis();
                                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                                xAxis.setTextColor(textColor);
                                xAxis.setDrawGridLines(true);

                                YAxis yAxis = tiersChart.getAxisRight();
                                yAxis.setLabelCount(6, false);
                                yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
                                yAxis.setTextColor(textColor);
                                yAxis.setEnabled(false);

                                YAxis yAxis2 = tiersChart.getAxisLeft();
                                yAxis2.setLabelCount(6, false);
                                yAxis2.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
                                yAxis2.setTextColor(textColor);

                                Legend l = tiersChart.getLegend();
                                l.setEnabled(false);
                                List<String> xVals = new ArrayList<String>();
                                for (int i = 1; i <= 10; i++)
                                    xVals.add(i + "");

                                List<BarEntry> yVals = new ArrayList<BarEntry>();
                                for (int i = 0; i < 10; i++) {
                                    if (battleCounts.get(i + 1) != null)
                                        yVals.add(new BarEntry(battleCounts.get(i + 1), i));
                                    else
                                        yVals.add(new BarEntry(0, i));
                                }

                                BarDataSet set1 = new BarDataSet(yVals, "");
                                set1.setColor(accentColor);
                                set1.setBarSpacePercent(20f);

                                ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
                                dataSets.add(set1);

                                BarData data = new BarData(xVals, dataSets);
                                data.setValueTextSize(10f);
                                data.setValueTextColor(textColor);
                                data.setValueFormatter(new LargeValueFormatter());
                                tiersChart.setDescription("");
                                tiersChart.setData(data);
                                tiersChart.requestLayout();
                            }
                        }

                        private void setUpWRPerModeChart() {
                            if(winRatePerMode.size() > 0) {
                                int textColor = CAApp.getTextColor(chartWRModes.getContext());

                                chartWRModes.setDrawBarShadow(false);
                                chartWRModes.setDrawValueAboveBar(false);
                                chartWRModes.setPinchZoom(false);
                                chartWRModes.setDoubleTapToZoomEnabled(false);
                                chartWRModes.setDrawGridBackground(false);
                                chartWRModes.setDrawValueAboveBar(true);
                                chartWRModes.setTouchEnabled(false);

                                XAxis xAxis = chartWRModes.getXAxis();
                                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                                xAxis.setTextColor(textColor);
                                xAxis.setDrawGridLines(true);

                                YAxis yAxis = chartWRModes.getAxisRight();
                                yAxis.setLabelCount(6, false);
                                yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
                                yAxis.setTextColor(textColor);
                                yAxis.setEnabled(false);

                                YAxis yAxis2 = chartWRModes.getAxisLeft();
                                yAxis2.setLabelCount(6, false);
                                yAxis2.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
                                yAxis2.setTextColor(textColor);

                                List<Integer> colorList = new ArrayList<Integer>();
                                colorList.add(ContextCompat.getColor(chartGamemodes.getContext(), R.color.solo_pvp_color));
                                colorList.add(ContextCompat.getColor(chartGamemodes.getContext(), R.color.pve_color));
                                colorList.add(ContextCompat.getColor(chartGamemodes.getContext(), R.color.average_up));
                                colorList.add(ContextCompat.getColor(chartGamemodes.getContext(), R.color.div_pvp_3));
                                colorList.add(ContextCompat.getColor(chartGamemodes.getContext(), R.color.div_pvp_2));
                                colorList.add(ContextCompat.getColor(chartGamemodes.getContext(), R.color.ranked_color));

                                Legend l = chartWRModes.getLegend();
                                l.setEnabled(false);
                                List<String> xVals = new ArrayList<String>();
                                xVals.addAll(modeStrings);

                                List<BarEntry> yVals = new ArrayList<BarEntry>();
                                for (int i = 0; i < xVals.size(); i++) {
                                    yVals.add(new BarEntry(winRatePerMode.get(xVals.get(i)), i));
                                }

                                BarDataSet set1 = new BarDataSet(yVals, "");
                                set1.setColors(colorList);
                                set1.setBarSpacePercent(20f);

                                ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
                                dataSets.add(set1);

                                BarData data = new BarData(xVals, dataSets);
                                data.setValueTextSize(10f);
                                data.setValueTextColor(textColor);
                                data.setValueFormatter(new PercentFormatter());
                                chartWRModes.setDescription("");
                                chartWRModes.setData(data);
                                chartWRModes.requestLayout();
                            }
                        }

                        private void setUpAvgDmgPerModeChart() {
                            if(avgDamageMode.size() > 0) {
                                int textColor = CAApp.getTextColor(chartAvgDmg.getContext());

                                chartAvgDmg.setDrawBarShadow(false);
                                chartAvgDmg.setDrawValueAboveBar(false);
                                chartAvgDmg.setPinchZoom(false);
                                chartAvgDmg.setDoubleTapToZoomEnabled(false);
                                chartAvgDmg.setDrawGridBackground(false);
                                chartAvgDmg.setDrawValueAboveBar(true);
                                chartAvgDmg.setTouchEnabled(false);

                                XAxis xAxis = chartAvgDmg.getXAxis();
                                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                                xAxis.setTextColor(textColor);
                                xAxis.setDrawGridLines(true);

                                YAxis yAxis = chartAvgDmg.getAxisRight();
                                yAxis.setLabelCount(6, false);
                                yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
                                yAxis.setTextColor(textColor);
                                yAxis.setEnabled(false);

                                YAxis yAxis2 = chartAvgDmg.getAxisLeft();
                                yAxis2.setLabelCount(6, false);
                                yAxis2.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
                                yAxis2.setTextColor(textColor);

                                List<Integer> colorList = new ArrayList<Integer>();
                                colorList.add(ContextCompat.getColor(chartGamemodes.getContext(), R.color.solo_pvp_color));
                                colorList.add(ContextCompat.getColor(chartGamemodes.getContext(), R.color.pve_color));
                                colorList.add(ContextCompat.getColor(chartGamemodes.getContext(), R.color.average_up));
                                colorList.add(ContextCompat.getColor(chartGamemodes.getContext(), R.color.div_pvp_3));
                                colorList.add(ContextCompat.getColor(chartGamemodes.getContext(), R.color.div_pvp_2));
                                colorList.add(ContextCompat.getColor(chartGamemodes.getContext(), R.color.ranked_color));

                                Legend l = chartAvgDmg.getLegend();
                                l.setEnabled(false);
                                List<String> xVals = new ArrayList<String>();
                                xVals.addAll(modeStrings);

                                List<BarEntry> yVals = new ArrayList<BarEntry>();
                                for (int i = 0; i < xVals.size(); i++) {
                                    yVals.add(new BarEntry(avgDamageMode.get(xVals.get(i)), i));
                                }

                                BarDataSet set1 = new BarDataSet(yVals, "");
                                set1.setColors(colorList);
                                set1.setBarSpacePercent(20f);

                                ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
                                dataSets.add(set1);

                                BarData data = new BarData(xVals, dataSets);
                                data.setValueTextSize(10f);
                                data.setValueTextColor(textColor);
                                data.setValueFormatter(new LargeValueFormatter());
                                chartAvgDmg.setDescription("");
                                chartAvgDmg.setData(data);
                                chartAvgDmg.requestLayout();
                            }
                        }

                        private void setUpSurvivalRatePerModeChart() {
                            if(survivalRateMode.size() > 0) {
                                int textColor = CAApp.getTextColor(chartSurvivalRate.getContext());

                                chartSurvivalRate.setDrawBarShadow(false);
                                chartSurvivalRate.setDrawValueAboveBar(false);
                                chartSurvivalRate.setPinchZoom(false);
                                chartSurvivalRate.setDoubleTapToZoomEnabled(false);
                                chartSurvivalRate.setDrawGridBackground(false);
                                chartSurvivalRate.setDrawValueAboveBar(true);
                                chartSurvivalRate.setTouchEnabled(false);

                                XAxis xAxis = chartSurvivalRate.getXAxis();
                                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                                xAxis.setTextColor(textColor);
                                xAxis.setDrawGridLines(true);

                                YAxis yAxis = chartSurvivalRate.getAxisRight();
                                yAxis.setLabelCount(6, false);
                                yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
                                yAxis.setTextColor(textColor);
                                yAxis.setEnabled(false);

                                YAxis yAxis2 = chartSurvivalRate.getAxisLeft();
                                yAxis2.setLabelCount(6, false);
                                yAxis2.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
                                yAxis2.setTextColor(textColor);

                                List<Integer> colorList = new ArrayList<Integer>();
                                colorList.add(Color.parseColor("#F44336"));
                                colorList.add(Color.parseColor("#FF9800"));
                                colorList.add(ContextCompat.getColor(chartSurvivalRate.getContext(), R.color.average_up));
                                colorList.add(Color.parseColor("#2196F3"));
                                colorList.add(Color.parseColor("#FAFA00"));

                                Legend l = chartSurvivalRate.getLegend();
                                l.setEnabled(false);
                                List<String> xVals = new ArrayList<String>();
                                xVals.addAll(modeStrings);

                                List<BarEntry> yVals = new ArrayList<BarEntry>();
                                for (int i = 0; i < xVals.size(); i++) {
                                    yVals.add(new BarEntry(survivalRateMode.get(xVals.get(i)), i));
                                }

                                BarDataSet set1 = new BarDataSet(yVals, "");
                                set1.setColors(colorList);
                                set1.setBarSpacePercent(20f);

                                ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
                                dataSets.add(set1);

                                BarData data = new BarData(xVals, dataSets);
                                data.setValueTextSize(10f);
                                data.setValueTextColor(textColor);
                                data.setValueFormatter(new PercentFormatter());
                                chartSurvivalRate.setDescription("");
                                chartSurvivalRate.setData(data);
                                chartSurvivalRate.requestLayout();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
        new Thread(runnable).start();
    }

    private void setUpOtherStatistics(Captain captain) {

        final List<String> strStatistics = new ArrayList<>();
        final List<Statistics> statistics = new ArrayList<>();

        if(captain.getTeamBattleDetails() != null && captain.getTeamBattleDetails().getBattles() > 0) {
            strStatistics.add(getString(R.string.team_battles_title));
            statistics.add(captain.getTeamBattleDetails());
        }
        if(captain.getPvpDiv2Details() != null && captain.getPvpDiv2Details().getBattles() > 0){
            strStatistics.add(getString(R.string.two_div_title));
            statistics.add(captain.getPvpDiv2Details());
        }
        if(captain.getPvpDiv3Details() != null && captain.getPvpDiv3Details().getBattles() > 0) {
            strStatistics.add(getString(R.string.three_div_title));
            statistics.add(captain.getPvpDiv3Details());
        }
        if(captain.getPveDetails() != null && captain.getPveDetails().getBattles() > 0) {
            strStatistics.add(getString(R.string.pve_title));
            statistics.add(captain.getPveDetails());
        }

        aOtherStats.post(new Runnable() {
            @Override
            public void run() {
                UIUtils.createOtherStatsArea(aOtherStats, strStatistics, statistics);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        CAApp.getEventBus().unregister(this);
    }

    @Subscribe
    public void onReceive(CaptainReceivedEvent event) {
        initView();
    }


    @Subscribe
    public void onSaveFinished(CaptainSavedEvent event) {
        chartProgress.post(new Runnable() {
            @Override
            public void run() {
                initView();
            }
        });
    }

    @Subscribe
    public void onProgressEvent(ProgressEvent event){
        Dlog.d("CaptainFragment","progressEvent");
        if(mSwipeRefreshLayout != null){
            mSwipeRefreshLayout.setRefreshing(event.isRefreshing());
        }
    }

    @Subscribe
    public void onRefresh(RefreshEvent event) {
        refreshing(true);
        tvBattles.setText("");

        tvAverageDamage.setText("");

        tvAverageExp.setText("");

        tvKillDeath.setText("");

        tvWinRate.setText("");

        tvGenXP.setText("");
        tvGenDamage.setText("");

        tvGenDropped.setText("");
        tvGenCapture.setText("");
        tvGenPlanesKilled.setText("");
        tvGenProfileLevel.setText("");

        tvCADiff.setText("");
        tvCARating.setText("");

        //dates
        lastBattleTime.setText("");

        createdOnTime.setText("");

        //checkbox area
        chartAverages.clear();
        gAverages.setAdapter(null);
        averagesAdapter = null;

        chartProgress.setVisibility(View.VISIBLE);

        topicalChart.clear();

        chartGamePerNation.clear();
        chartGamePerType.clear();

        chartGamemodes.clear();

        aOtherStats.removeAllViews();

        tiersChart.clear();
    }
}