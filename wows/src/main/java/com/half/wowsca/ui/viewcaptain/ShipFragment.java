package com.half.wowsca.ui.viewcaptain;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.half.wowsca.CAApp;
import com.half.wowsca.R;
import com.half.wowsca.interfaces.ICaptain;
import com.half.wowsca.managers.CaptainManager;
import com.half.wowsca.managers.StorageManager;
import com.half.wowsca.model.BatteryStats;
import com.half.wowsca.model.Captain;
import com.half.wowsca.model.Ship;
import com.half.wowsca.model.Statistics;
import com.half.wowsca.model.encyclopedia.items.ShipInfo;
import com.half.wowsca.model.encyclopedia.items.ShipStat;
import com.half.wowsca.model.enums.AverageType;
import com.half.wowsca.model.listModels.ListAverages;
import com.half.wowsca.model.ranked.SeasonInfo;
import com.half.wowsca.model.ranked.SeasonStats;
import com.half.wowsca.model.saveobjects.SavedShips;
import com.half.wowsca.ui.SettingActivity;
import com.half.wowsca.ui.UIUtils;
import com.half.wowsca.ui.adapter.AveragesAdapter;
import com.half.wowsca.ui.viewcaptain.tabs.CaptainRankedFragment;
import com.half.wowsca.ui.views.NonScrollableGridView;
import com.half.wowsca.ui.views.RadarMarkerView;
import com.utilities.Utils;
import com.utilities.logging.Dlog;
import com.utilities.preferences.Prefs;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by slai4 on 12/12/2015.
 */
public class ShipFragment extends Fragment{

    public static final String TAG_AVERAGE_DAMAGE = "AverageDamage";
    public static final String TAG_BATTLES = "Battles";
    public static final String TAG_WIN_RATE = "WinRate";
    public static final String TAG_KILL_DEATH = "KillDeath";
    public static final String TAG_AVERAGE_EXP = "AverageExp";
    public static final String SELECTED_GRAPH = "selected_graph";
    public static final String SHIP_ID = "shipId";
    private long id;

    private ImageView ivShip;
    private TextView tvName;
    private TextView tvNationTier;
    private TextView tvBattles;
    private TextView tvWinRate;
    private TextView tvAvgExp;
    private TextView tvAvgFrag;
    private TextView tvAvgDamage;
    private TextView tvTopCARating;

    private TextView tvBatteryMain;
    private TextView tvBatteryTorps;
    private TextView tvBatteryAircraft;
    private TextView tvBatteryOther;

    private View aCompare;
    private RadarChart chartAverages;
    private NonScrollableGridView gridView;
    private TextView tvCARating;
    private TextView tvCADiff;
    private TextView tvAverageTitle;

    private TextView tvMaxKills;
    private TextView tvMaxDamage;
    private TextView tvMaxPlanes;
    private TextView tvMaxXp;
    private TextView tvTraveled;
    private TextView tvAvgPlanes;
    private TextView tvSurvivalRate;
    private TextView tvAvgCaps;
    private TextView tvAvgDropped;
    private TextView tvTotalXp;
    private TextView tvSurvivedWins;

    private TextView tvWins;
    private TextView tvLosses;
    private TextView tvDraws;

    private TextView tvTotalPlanes;
    private TextView tvTotalCaptures;
    private TextView tvTotalDefReset;

    private TextView tvBatteryAccMain;
    private TextView tvBatteryAccTorps;

    private TextView tvSpottingDamage;
    private TextView tvArgoDamage;
    private TextView tvBuildingDamage;
    private TextView tvArgoTorpDamage;
    private TextView tvSuppressionCount;
    private TextView tvSpottingCount;
    private TextView tvMaxSpotting;

    private View aRanked;
    private LinearLayout llContainer;

    private View aChartArea;
    private LineChart chartSavedArea;
    private TextView tvChartSaved;

    private View aSavedChartBattles;
    private View aSavedChartDamage;
    private View aSavedChartWinRate;
    private View aSavedChartExp;
    private View aSavedChartKills;

    private List<Ship> savedShipsInfo;

    private MyFormatter formatter;
    private MyYFormatter yFormatter;

    private LinearLayout aOtherStats;

    private PieChart chartGameModes;

    private TextView tvGameModeTitle;

    private View aCARatingArea;

    private View aTopCARatingArea;

    private View aAveragesContribution;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ship, container, false);
        bindView(view);
        if(savedInstanceState != null){
            id = savedInstanceState.getLong(SHIP_ID);
        }
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("shipId", id);
    }

    private void bindView(View view) {
        ivShip = (ImageView) view.findViewById(R.id.snippet_ship_icon);
        tvName = (TextView) view.findViewById(R.id.snippet_ship_name);
        tvNationTier = (TextView) view.findViewById(R.id.snippet_ship_nation_tier);
        tvBattles = (TextView) view.findViewById(R.id.snippet_ship_battles);
        tvWinRate = (TextView) view.findViewById(R.id.snippet_ship_win_rate);
        tvAvgExp = (TextView) view.findViewById(R.id.snippet_ship_avg_exp);
        tvAvgFrag = (TextView) view.findViewById(R.id.snippet_ship_avg_kills);
        tvAvgDamage = (TextView) view.findViewById(R.id.snippet_ship_avg_damage);
        tvTopCARating = (TextView) view.findViewById(R.id.snippet_ship_ca_rating);

        tvBatteryAircraft = (TextView) view.findViewById(R.id.fragment_ship_battery_kills_aircraft);
        tvBatteryMain = (TextView) view.findViewById(R.id.fragment_ship_battery_kills_main);
        tvBatteryTorps = (TextView) view.findViewById(R.id.fragment_ship_battery_kills_torps);
        tvBatteryOther = (TextView) view.findViewById(R.id.fragment_ship_battery_kills_other);

        aCompare = view.findViewById(R.id.averages_grid_area);
        chartAverages = (RadarChart) view.findViewById(R.id.averages_chart);
        gridView = (NonScrollableGridView) view.findViewById(R.id.averages_grid);
        tvCARating = (TextView) view.findViewById(R.id.averages_car);
        tvCADiff = (TextView) view.findViewById(R.id.averages_car_dif);

        tvMaxDamage = (TextView) view.findViewById(R.id.fragment_ship_max_damage);
        tvMaxKills = (TextView) view.findViewById(R.id.fragment_ship_max_kills);
        tvMaxPlanes = (TextView) view.findViewById(R.id.fragment_ship_max_planes_killed);
        tvMaxXp = (TextView) view.findViewById(R.id.fragment_ship_max_xp);
        tvTraveled = (TextView) view.findViewById(R.id.fragment_ship_distance_traveled);
        tvAvgPlanes = (TextView) view.findViewById(R.id.fragment_ship_planes_destroyed);
        tvSurvivalRate = (TextView) view.findViewById(R.id.fragment_ship_survived_battles);
        tvAvgCaps = (TextView) view.findViewById(R.id.fragment_ship_capture_points);
        tvAvgDropped  = (TextView) view.findViewById(R.id.fragment_ship_dropped_capture_points);
        tvTotalXp = (TextView) view.findViewById(R.id.fragment_ship_total_exp);
        tvSurvivedWins = (TextView) view.findViewById(R.id.fragment_ship_survived_wins);

        tvWins = (TextView) view.findViewById(R.id.fragment_ship_wins);
        tvLosses = (TextView) view.findViewById(R.id.fragment_ship_losses);
        tvDraws = (TextView) view.findViewById(R.id.fragment_ship_draws);

        tvTotalCaptures = (TextView) view.findViewById(R.id.fragment_ship_total_captures);
        tvTotalDefReset = (TextView) view.findViewById(R.id.fragment_ship_total_def_points);
        tvTotalPlanes = (TextView) view.findViewById(R.id.fragment_ship_total_planes);

        tvBatteryAccMain = (TextView) view.findViewById(R.id.fragment_ship_battery_accuracy_main);
        tvBatteryAccTorps = (TextView) view.findViewById(R.id.fragment_ship_battery_accuracy_torp);

        aRanked = view.findViewById(R.id.fragment_ship_ranked_area);
        llContainer = (LinearLayout) view.findViewById(R.id.fragment_ship_ranked_container);

        aChartArea = view.findViewById(R.id.fragment_ship_saved_chart_area);
        chartSavedArea = (LineChart) view.findViewById(R.id.fragment_ship_saved_chart_graph_topical_line);

        aSavedChartBattles = view.findViewById(R.id.fragment_ship_saved_chart_battles_area);
        aSavedChartWinRate = view.findViewById(R.id.fragment_ship_saved_chart_winning_area);
        aSavedChartDamage = view.findViewById(R.id.fragment_ship_saved_chart_damage_area);
        aSavedChartExp = view.findViewById(R.id.fragment_ship_saved_chart_experience_area);
        aSavedChartKills = view.findViewById(R.id.fragment_ship_saved_chart_kills_area);
        tvChartSaved = (TextView) view.findViewById(R.id.fragment_ship_saved_chart_graph_topical_text);

        tvSpottingDamage = (TextView) view.findViewById(R.id.fragment_ship_total_spotting);
        tvArgoDamage = (TextView) view.findViewById(R.id.fragment_ship_total_argo);
        tvBuildingDamage = (TextView) view.findViewById(R.id.fragment_ship_total_building);
        tvArgoTorpDamage = (TextView) view.findViewById(R.id.fragment_ship_total_torp_argo);

        tvSuppressionCount = (TextView) view.findViewById(R.id.fragment_ship_total_supressions);
        tvSpottingCount = (TextView) view.findViewById(R.id.fragment_ship_total_spots);
        tvMaxSpotting = (TextView) view.findViewById(R.id.fragment_ship_max_spots);

        aOtherStats = (LinearLayout) view.findViewById(R.id.fragment_ship_other_stats);
        tvGameModeTitle = (TextView) view.findViewById(R.id.fragment_ship_game_mode_title);

        chartGameModes = (PieChart) view.findViewById(R.id.fragment_ship_graphs_games_per_mode);

        tvAverageTitle = (TextView) view.findViewById(R.id.averages_top_title);
        tvAverageTitle.setText(R.string.community_assistant_rating);

        aCARatingArea = view.findViewById(R.id.averages_ca_rating_top_area);
        aTopCARatingArea = view.findViewById(R.id.snippet_ship_ca_rating_area);

        aAveragesContribution = view.findViewById(R.id.averages_contribution_chart_area);
        view.findViewById(R.id.ca_rating_breakdown_area).setClickable(false);
        aAveragesContribution.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
    }

    private void initView() {
        try {
            getActivity().invalidateOptionsMenu();
        } catch (Exception e) {
        }
        ShipInfo info = CAApp.getInfoManager().getShipInfo(getContext()).get(id);
        ShipStat stat = CAApp.getInfoManager().getShipStats(getContext()).get(id);
        Captain captain = null;
        try {
            captain = ((ICaptain) getActivity()).getCaptain(getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(captain != null && captain.getShips() != null){
            Ship ship = null;
            for(Ship s : captain.getShips()){
                if(s.getShipId() == id){
                    ship = s;
                    Dlog.wtf("ShipInfo", " ship - " + s);
                    break;
                }
            }

            if (info != null) {
                tvName.setText(info.getName());
                UIUtils.setShipImage(ivShip, info, true);
                String nation = UIUtils.getNationText(getContext(), info.getNation());
                tvNationTier.setText(nation + " - " + info.getTier());
                if (info.isPremium()) {
                    tvName.setTextColor(ContextCompat.getColor(getContext(), R.color.premium_shade));
                } else {
                    tvName.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                }
            } else {
                tvName.setText("" + id);
                tvNationTier.setText(R.string.unknown);
            }

            if(ship != null){
                float battles = ship.getBattles();
                Dlog.wtf("ShipFragment", "battles = " + battles);

                Prefs prefs = new Prefs(getContext());
                boolean showCompare = prefs.getBoolean(SettingActivity.SHOW_COMPARE, true);
                aCARatingArea.setVisibility(View.VISIBLE);
                aTopCARatingArea.setVisibility(View.VISIBLE);
                if(stat != null && showCompare && battles > 0) {
                    aCompare.setVisibility(View.VISIBLE);
                    List<ListAverages> averages = new ArrayList<ListAverages>();
                    averages.add(ListAverages.create(getString(R.string.damage), (float) (ship.getTotalDamage() / battles), stat.getDmg_dlt(), AverageType.LARGE_NUMBER));
                    averages.add(ListAverages.create(getString(R.string.short_kills_game), ship.getFrags() / battles, stat.getFrags(), AverageType.DEFAULT));
                    averages.add(ListAverages.create(getString(R.string.short_win_rate), (ship.getWins() / battles), stat.getWins(), AverageType.PERCENT));
                    averages.add(ListAverages.create(getString(R.string.short_planes_game), ship.getPlanesKilled() / battles, stat.getPls_kd(), AverageType.DEFAULT));

//                    averages.add(ListAverages.create(getString(R.string.survival_rate), ship.getSurvivedBattles() / battles, stat.getSr_bat(), AverageType.PERCENT));
//                    averages.add(ListAverages.create(getString(R.string.survived_wins), ship.getSurvivedWins() / battles, stat.getSr_wins(), AverageType.PERCENT));
//                    averages.add(ListAverages.create(getString(R.string.average_xp), ship.getTotalXP() / battles, stat.getAvg_xp(), AverageType.DEFAULT));

                    if (gridView.getAdapter() == null) {
                        AveragesAdapter averagesAdapter = new AveragesAdapter(getContext(), R.layout.list_averages, averages);
                        gridView.setAdapter(averagesAdapter);
                    } else {
                        AveragesAdapter adapter = (AveragesAdapter) gridView.getAdapter();
                        adapter.setObjects(averages);
                    }
                } else {
                    aCompare.setVisibility(View.GONE);
                }

                if (battles > 0) {
                    tvBattles.setText("" + ((int) battles));
                    float avgExp = ship.getTotalXP() / battles;
                    tvAvgExp.setText((int) avgExp + "");
                    float wr = (ship.getWins() / battles) * 100.0f;
                    tvWinRate.setText(Utils.getDefaultDecimalFormatter().format(wr) + "%");
                    int kdBattles = (int) battles;
                    if (kdBattles != ship.getSurvivedBattles()) {
                        kdBattles = (int) (battles - ship.getSurvivedBattles());
                    }
                    float kd = (float) ship.getFrags() / kdBattles;
                    tvAvgFrag.setText("" + Utils.getDefaultDecimalFormatter().format(kd));
                    int avgDamage = (int) (ship.getTotalDamage() / battles);
                    tvAvgDamage.setText("" + avgDamage);

                    float rating = ship.getCARating();
                    tvCARating.setText(Math.round(rating) + "");
                    tvCARating.setTag(rating + "");
                    tvTopCARating.setText(Math.round(rating) + "");

                    BatteryStats mainBatteryStats = ship.getMainBattery();
                    BatteryStats torpStats = ship.getTorpedoes();
                    BatteryStats aircraftStats = ship.getAircraft();

                    tvBatteryMain.setText("" + mainBatteryStats.getFrags());
                    if(mainBatteryStats.getShots() > 0)
                        tvBatteryAccMain.setText(Utils.getOneDepthDecimalFormatter().format(mainBatteryStats.getHits() / (float) mainBatteryStats.getShots() * 100) + "%");
                    tvBatteryTorps.setText("" + torpStats.getFrags());
                    if(torpStats.getShots() > 0)
                        tvBatteryAccTorps.setText(Utils.getOneDepthDecimalFormatter().format(torpStats.getHits() / (float) torpStats.getShots() * 100) + "%");
                    tvBatteryAircraft.setText("" + aircraftStats.getFrags());

                    int others = ship.getFrags() - mainBatteryStats.getFrags() - torpStats.getFrags() - aircraftStats.getFrags();
                    tvBatteryOther.setText("" + others);

                    tvMaxKills.setText("" + ship.getMaxFragsInBattle());
                    tvMaxDamage.setText("" + ship.getMaxDamage());
                    tvMaxPlanes.setText("" + ship.getMaxPlanesKilled());
                    tvMaxXp.setText("" + ship.getMaxXP());
                    tvAvgCaps.setText(Utils.getOneDepthDecimalFormatter().format((float) ship.getCapturePoints() / battles) + "");
                    tvSurvivedWins.setText(Utils.getOneDepthDecimalFormatter().format((float) ship.getSurvivedWins() / battles) + "%");
                    tvSurvivalRate.setText(Utils.getOneDepthDecimalFormatter().format(((float) ship.getSurvivedBattles() / battles) * 100) + "%");
                    tvAvgDropped.setText(Utils.getOneDepthDecimalFormatter().format((float) ship.getDroppedCapturePoints() / battles) + "");
                    tvTotalXp.setText("" + ship.getTotalXP());

                    tvWins.setText("" + ship.getWins());
                    tvLosses.setText("" + ship.getLosses());
                    tvDraws.setText("" + ship.getDraws());

                    tvTotalCaptures.setText("" + ship.getCapturePoints());
                    tvTotalPlanes.setText("" + ship.getPlanesKilled());
                    tvTotalDefReset.setText("" + ship.getDroppedCapturePoints());

                    tvAvgPlanes.setText("" + Utils.getOneDepthDecimalFormatter().format((float) ship.getPlanesKilled() / battles));
                    tvTraveled.setText(ship.getDistanceTraveled() + " miles");

                    String argoDamage = "" + ship.getTotalArgoDamage();
                    if(ship.getTotalArgoDamage() > 1000000){
                        argoDamage = "" + Utils.getDefaultDecimalFormatter().format(ship.getTotalArgoDamage() / 1000000) + getString(R.string.million);
                    }
                    tvArgoDamage.setText(argoDamage);

                    String argoTorpDamage = "" + ship.getTorpArgoDamage();
                    if(ship.getTotalArgoDamage() > 1000000){
                        argoTorpDamage = "" + Utils.getDefaultDecimalFormatter().format(ship.getTotalArgoDamage() / 1000000) + getString(R.string.million);
                    }
                    tvArgoTorpDamage.setText(argoTorpDamage);

                    String buildingDamage = "" + ship.getBuildingDamage();
                    if(ship.getBuildingDamage() > 1000000){
                        buildingDamage = "" + Utils.getDefaultDecimalFormatter().format(ship.getBuildingDamage() / 1000000) + getString(R.string.million);
                    }
                    tvBuildingDamage.setText(buildingDamage);

                    String scoutingDamage = "" + ship.getScoutingDamage();
                    if(ship.getScoutingDamage() > 1000000){
                        scoutingDamage = "" + Utils.getDefaultDecimalFormatter().format(ship.getScoutingDamage() / 1000000) + getString(R.string.million);
                    }
                    tvSpottingDamage.setText(scoutingDamage);

                    tvSpottingCount.setText("" + ship.getSpotted());
                    tvSuppressionCount.setText("" + ship.getSuppressionCount());
                    tvMaxSpotting.setText("" + ship.getMaxSpotted());

                    setUpAverages(ship, stat);

                    setUpOtherModeInformation(ship);

                    View.OnClickListener onClick = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String tag = (String) v.getTag();
                            Prefs prefs = new Prefs(v.getContext());
                            prefs.setString(SELECTED_GRAPH, tag);
                            //check background
                            checkBackgrounds(tag);
                            //setUpGraph
                            if(savedShipsInfo != null)
                                setUpTopicalInfo(tag, savedShipsInfo, false);
                        }
                    };
                    aSavedChartDamage.setOnClickListener(onClick);
                    aSavedChartDamage.setTag(TAG_AVERAGE_DAMAGE);
                    aSavedChartBattles.setOnClickListener(onClick);
                    aSavedChartBattles.setTag(TAG_BATTLES);
                    aSavedChartWinRate.setOnClickListener(onClick);
                    aSavedChartWinRate.setTag(TAG_WIN_RATE);
                    aSavedChartKills.setOnClickListener(onClick);
                    aSavedChartKills.setTag(TAG_KILL_DEATH);
                    aSavedChartExp.setOnClickListener(onClick);
                    aSavedChartExp.setTag(TAG_AVERAGE_EXP);

                    String id = CaptainManager.createCapIdStr(captain.getServer(), captain.getId());
                    getSavedData(id, ship.getShipId());

                    createGameModeGraphs(ship);
                }
                setUpRankedArea(ship);
            }
        }
    }

    private void createGameModeGraphs(Ship ship) {
        final List<String> modeStrings = new ArrayList<>();
        final Map<String, Integer> gamesPerMode = new HashMap<>();
        int soloBattles = ship.getBattles()
                - ship.getPve().getBattles()
                - ship.getPvpDiv2().getBattles()
                - ship.getPvpDiv3().getBattles()
                - ship.getTeamBattles().getBattles();

        if(soloBattles > 0) {
            String str = getString(R.string.solo_pvp);
            modeStrings.add(str);
            gamesPerMode.put(str, soloBattles);
        }
        if(ship.getPve().getBattles() > 0) {
            String str = getString(R.string.pve);
            modeStrings.add(str);
            gamesPerMode.put(str, ship.getPve().getBattles());
        }
        if(ship.getPvpDiv2().getBattles() > 0) {
            String str = getString(R.string.pvp_2_div);
            modeStrings.add(str);
            gamesPerMode.put(str, ship.getPvpDiv2().getBattles());
        }
        if(ship.getPvpDiv3().getBattles() > 0) {
            String str = getString(R.string.pvp_3_div);
            modeStrings.add(str);
            gamesPerMode.put(str, ship.getPvpDiv3().getBattles());
        }
        if(ship.getTeamBattles().getBattles() > 0) {
            String str = getString(R.string.team_battles);
            modeStrings.add(str);
            gamesPerMode.put(str, ship.getTeamBattles().getBattles());
        }

        int rankedBattles = 0;
        if(ship.getRankedInfo() != null){
            for(SeasonInfo info : ship.getRankedInfo()){
                try {
                    rankedBattles += info.getSolo().getBattles();
                } catch (Exception e) {
                }
            }
        }
        if(rankedBattles > 0){
            String ranked = getString(R.string.ranked);
            modeStrings.add(ranked);
            gamesPerMode.put(ranked, rankedBattles);
        }

        chartGameModes.post(new Runnable() {
            @Override
            public void run() {
                setUpGamesPerModeChart(gamesPerMode, modeStrings);
            }
        });
    }

    private void setUpGamesPerModeChart(Map<String, Integer> gamesPerMode, List<String> modeStrings) {
        if(gamesPerMode.size() > 0) {
            int textColor = CAApp.getTextColor(chartGameModes.getContext());
            boolean colorblind = CAApp.isColorblind(chartGameModes.getContext());

            chartGameModes.setRotationEnabled(false);

            chartGameModes.setDrawHoleEnabled(true);
            chartGameModes.setHoleColor(R.color.transparent);
            chartGameModes.setTransparentCircleRadius(50);
            chartGameModes.setHoleRadius(50);

            chartGameModes.setDrawSliceText(false);

            Legend l = chartGameModes.getLegend();
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
            colorList.add(Color.parseColor("#F44336"));
            colorList.add(Color.parseColor("#FF9800"));
            colorList.add(ContextCompat.getColor(chartGameModes.getContext(), R.color.average_up));
            colorList.add(Color.parseColor("#2196F3"));
            colorList.add(Color.parseColor("#FAFA00"));

            PieDataSet dataSet = new PieDataSet(yVals1, "");

            ArrayList<PieDataSet> dataSets = new ArrayList<PieDataSet>();
            dataSets.add(dataSet);

            dataSet.setColors(colorList);

            PieData data = new PieData(xVals, dataSet);
            data.setValueTextColor(ContextCompat.getColor(chartGameModes.getContext(), R.color.black));
            data.setValueTextSize(14);
            chartGameModes.setDescription("");
            data.setValueFormatter(new LargeValueFormatter());

            chartGameModes.highlightValues(null);

            chartGameModes.setData(data);
            chartGameModes.requestLayout();
            tvGameModeTitle.setVisibility(View.VISIBLE);
        } else {
            tvGameModeTitle.setVisibility(View.GONE);
        }
    }

    private void setUpOtherModeInformation(Ship ship) {
        final List<String> strStatistics = new ArrayList<>();
        final List<Statistics> statistics = new ArrayList<>();

        if(ship.getTeamBattles() != null && ship.getTeamBattles().getBattles() > 0) {
            strStatistics.add(getString(R.string.team_battles_title));
            statistics.add(ship.getTeamBattles());
        }
        if(ship.getPvpDiv2() != null && ship.getPvpDiv2().getBattles() > 0){
            strStatistics.add(getString(R.string.two_div_title));
            statistics.add(ship.getPvpDiv2());

        }
        if(ship.getPvpDiv3() != null && ship.getPvpDiv3().getBattles() > 0) {
            strStatistics.add(getString(R.string.three_div_title));
            statistics.add(ship.getPvpDiv3());
        }
        if(ship.getPve() != null && ship.getPve().getBattles() > 0) {
            strStatistics.add(getString(R.string.pve_title));
            statistics.add(ship.getPve());
        }

        aOtherStats.post(new Runnable() {
            @Override
            public void run() {
                UIUtils.createOtherStatsArea(aOtherStats, strStatistics, statistics);
            }
        });
    }

    private void setUpRankedArea(Ship ship) {
        llContainer.removeAllViews();
        if(ship.getRankedInfo() != null){
            aRanked.setVisibility(View.VISIBLE);

            Collections.sort(ship.getRankedInfo(), new Comparator<SeasonInfo>() {
                @Override
                public int compare(SeasonInfo lhs, SeasonInfo rhs) {
                    return rhs.getSeasonNum().compareToIgnoreCase(lhs.getSeasonNum());
                }
            });

            for(SeasonInfo stats : ship.getRankedInfo()){
                if(stats.getSolo() != null) {
                    View view = LayoutInflater.from(getContext()).inflate(R.layout.list_ship_ranked, llContainer, false);

                    UIUtils.setUpCard(view, R.id.list_ship_ranked_card_area);

                    TextView tvTitle = (TextView) view.findViewById(R.id.list_ship_ranked_title);

                    TextView tvBattles = (TextView) view.findViewById(R.id.list_ship_ranked_battles);
                    TextView tvAvgDamage = (TextView) view.findViewById(R.id.list_ship_ranked_avg_dmg);
                    TextView tvAvgKills = (TextView) view.findViewById(R.id.list_ship_ranked_kills);
                    TextView tvAvgCaps = (TextView) view.findViewById(R.id.list_ship_ranked_avg_caps);
                    TextView tvDrpCapPts = (TextView) view.findViewById(R.id.list_ship_ranked_drp_cap_pts);
                    TextView tvAvgPlanes = (TextView) view.findViewById(R.id.list_ship_ranked_avg_planes);
                    TextView tvAvgExp = (TextView) view.findViewById(R.id.list_ship_ranked_avg_exp);
                    TextView tvSurvivalRate = (TextView) view.findViewById(R.id.list_ship_ranked_survival_rate);
                    TextView tvWinRate = (TextView) view.findViewById(R.id.list_ship_ranked_win_rate);
                    TextView tvSurvivedWins = (TextView) view.findViewById(R.id.list_ship_ranked_survival_wins);


                    TextView tvBatteryMain = (TextView) view.findViewById(R.id.list_ship_ranked_battery_kills_main);
                    TextView tvBatteryTorps = (TextView) view.findViewById(R.id.list_ship_ranked_battery_kills_torps);
                    TextView tvBatteryPlanes = (TextView) view.findViewById(R.id.list_ship_ranked_battery_kills_aircraft);
                    TextView tvBatteryOther = (TextView) view.findViewById(R.id.list_ship_ranked_battery_kills_other);

                    tvTitle.setText(getString(R.string.ranked_season) + " " + stats.getSeasonNum());
                    SeasonStats season = stats.getSolo();
                    float bat = season.getBattles();
                    tvBattles.setText(season.getBattles() + "");
                    if(bat > 0){
                        float avgExp = season.getXp() / bat;
                        tvAvgExp.setText(Utils.getDefaultDecimalFormatter().format(avgExp) + "");
                        float wr = (season.getWins() / bat) * 100.0f;
                        tvWinRate.setText(Utils.getDefaultDecimalFormatter().format(wr) + "%");
                        float kd = (float) season.getFrags() / bat;
                        tvAvgKills.setText("" + Utils.getDefaultDecimalFormatter().format(kd));
                        int avgDamage = (int) (season.getDamage() / bat);
                        tvAvgDamage.setText("" + avgDamage);

                        BatteryStats mainBatteryStats = season.getMain();
                        BatteryStats torpStats = season.getTorps();
                        BatteryStats aircraftStats = season.getAircraft();
                        tvBatteryMain.setText("" + CaptainRankedFragment.createBatteryString(mainBatteryStats));
                        tvBatteryTorps.setText("" + CaptainRankedFragment.createBatteryString(torpStats));
                        tvBatteryPlanes.setText("" + CaptainRankedFragment.createBatteryString(aircraftStats));
                        int others = season.getFrags() - mainBatteryStats.getFrags() - torpStats.getFrags() - aircraftStats.getFrags();
                        tvBatteryOther.setText("" + others);

                        tvAvgPlanes.setText(Utils.getOneDepthDecimalFormatter().format(season.getPlanesKilled() / bat));

                        tvSurvivedWins.setText(Utils.getOneDepthDecimalFormatter().format((season.getSurWins() / bat) * 100) + "%");
                        tvAvgCaps.setText(Utils.getOneDepthDecimalFormatter().format((float) season.getCapPts() / bat) + "");
                        tvSurvivalRate.setText(Utils.getOneDepthDecimalFormatter().format(((float) season.getSurvived() / bat) * 100) + "%");
                        tvDrpCapPts.setText(Utils.getOneDepthDecimalFormatter().format((float) season.getDrpCapPts() / bat) + "");
                    }
                    llContainer.addView(view);
                }
            }

        } else {
            aRanked.setVisibility(View.GONE);

        }
    }

    private void getSavedData(final String accountId, final long shipId) {
        if(savedShipsInfo == null) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    SavedShips ships = StorageManager.getPlayerShips(getContext(), accountId);
                    if (ships != null) {
                        savedShipsInfo = ships.getSavedShips().get(shipId);
                        if (savedShipsInfo != null && savedShipsInfo.size() > 1) {
                            setupSavedInfo(savedShipsInfo, true);
                        } else {
                            aChartArea.post(new Runnable() {
                                @Override
                                public void run() {
                                    aChartArea.setVisibility(View.GONE);
                                }
                            });
                        }
                    } else {
                        aChartArea.post(new Runnable() {
                            @Override
                            public void run() {
                                aChartArea.setVisibility(View.GONE);
                            }
                        });
                    }
                    ships = null;
                }
            });
            t.start();
        } else {
            setupSavedInfo(savedShipsInfo, false);
        }
    }

    private void setupSavedInfo(final List<Ship> shipStats, final boolean firstLoad) {
        Prefs prefs = new Prefs(getContext());
        String selectedGraph = null;
        try {
            selectedGraph = prefs.getString(SELECTED_GRAPH, "");
        } catch (Exception e) {
        }
        if (TextUtils.isEmpty(selectedGraph))
            selectedGraph = TAG_AVERAGE_EXP;

        final String tag = selectedGraph;
        aChartArea.post(new Runnable() {
            @Override
            public void run() {
                checkBackgrounds(tag);
                if(shipStats != null)
                    setUpTopicalInfo(tag, shipStats, firstLoad);
            }
        });

        tvCADiff.post(new Runnable() {
            @Override
            public void run() {
                Object rating =  tvCARating.getTag();
                try {
                    if(rating != null && shipStats.size() > 0) {
                        float fRating = Float.parseFloat((String) rating);
                        Ship lastShip = shipStats.get(shipStats.size() - 2);
                        float prevRating = lastShip.getCARating();
                        if (prevRating > 0) {
                            float dif = fRating - prevRating;
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
                    } else {
                        tvCADiff.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                }
            }
        });
    }

    private void setUpTopicalInfo(final String tag, List<Ship> ships, boolean firstLoad){
        aChartArea.setVisibility(View.VISIBLE);
        final List<String> nameValues = new ArrayList<String>();
        List<Float> numbers = new ArrayList<Float>();
        if(firstLoad)
            Collections.reverse(ships);
        int strResId = R.string.captain_average_exp;

        for (Ship detail : ships) {
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
                float kd = (float) detail.getFrags() / (battles - (float) detail.getSurvivedBattles());
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
            aChartArea.post(new Runnable() {
                @Override
                public void run() {
                    tvChartSaved.setText(topicalStrRes);
                    chartSavedArea.clear();

                    boolean colorblind = CAApp.isColorblind(chartSavedArea.getContext());
                    int textColor = CAApp.getTextColor(chartSavedArea.getContext());
                    int accentColor = !colorblind ?
                            (CAApp.getTheme(chartSavedArea.getContext()).equals("ocean") ?
                                    ContextCompat.getColor(chartSavedArea.getContext(), R.color.graph_line_color) : ContextCompat.getColor(chartSavedArea.getContext(), R.color.top_background))
                            : ContextCompat.getColor(chartSavedArea.getContext(), R.color.white);
                    chartSavedArea.setDoubleTapToZoomEnabled(false);
                    chartSavedArea.setPinchZoom(false);

                    chartSavedArea.setDescription("");

                    chartSavedArea.setDragEnabled(false);
                    chartSavedArea.setScaleEnabled(false);
                    chartSavedArea.setDrawGridBackground(false);

                    chartSavedArea.getLegend().setEnabled(false);

                    XAxis xAxis = chartSavedArea.getXAxis();
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xAxis.setTextColor(textColor);
                    xAxis.setDrawGridLines(true);

                    YAxis yAxis = chartSavedArea.getAxisRight();
                    yAxis.setEnabled(false);

                    YAxis yAxis2 = chartSavedArea.getAxisLeft();
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
                    set.setCircleSize(3f);
                    set.setFillColor(accentColor);
                    set.setDrawValues(false);
                    set.setCircleColor(accentColor);

//                            if(tag.equals(TAG_AVERAGE_EXP) || tag.equals(TAG_AVERAGE_DAMAGE)){
//                                set.setValueFormatter(new DefaultValueFormatter(0));
//                            } else {
//                            }
                    List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
                    dataSets.add(set);

                    LineData data = new LineData(nameValues, dataSets);

                    if (formatter == null)
                        formatter = new MyFormatter();
                    formatter.change(tag);
                    data.setValueFormatter(formatter);

                    chartSavedArea.setData(data);
                    chartSavedArea.requestLayout();

                    chartSavedArea.animateX(750);
                }
            });
            aChartArea.setVisibility(View.VISIBLE);
        } else {
            aChartArea.setVisibility(View.GONE);
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


    private void checkBackgrounds(String tag) {
        if (tag.equals(TAG_AVERAGE_EXP)) {
            aSavedChartExp.setBackgroundResource(R.color.captain_top_background);
        } else {
            aSavedChartExp.setBackgroundResource(R.color.transparent);
        }
        if (tag.equals(TAG_KILL_DEATH)) {
            aSavedChartKills.setBackgroundResource(R.color.captain_top_background);
        } else {
            aSavedChartKills.setBackgroundResource(R.color.transparent);
        }
        if (tag.equals(TAG_WIN_RATE)) {
            aSavedChartWinRate.setBackgroundResource(R.color.captain_top_background);
        } else {
            aSavedChartWinRate.setBackgroundResource(R.color.transparent);
        }
        if (tag.equals(TAG_AVERAGE_DAMAGE)) {
            aSavedChartDamage.setBackgroundResource(R.color.captain_top_background);
        } else {
            aSavedChartDamage.setBackgroundResource(R.color.transparent);
        }
        if (tag.equals(TAG_BATTLES)) {
            aSavedChartBattles.setBackgroundResource(R.color.captain_top_background);
        } else {
            aSavedChartBattles.setBackgroundResource(R.color.transparent);
        }
    }

    private void setUpAverages(final Ship ship, final ShipStat stat) {
        Runnable runnable = new Runnable() {

            private String cleanTitleString(int id){
                String str = getString(id);
                if(str.length() > 14){
                    str = str.substring(0,15).trim() + "...";
                }
                return str;
            }

            public void run() {
                try {
                    boolean isColorblind = CAApp.isColorblind(chartAverages.getContext());
                    int color = !isColorblind ?
                            (CAApp.getTheme(chartAverages.getContext()).equals("ocean") ?
                                    ContextCompat.getColor(chartAverages.getContext(), R.color.graph_line_color) : ContextCompat.getColor(chartAverages.getContext(), R.color.top_background))
                            : ContextCompat.getColor(chartAverages.getContext(), R.color.white);
                    int textColor = CAApp.getTextColor(chartAverages.getContext());
                    int webTextColor = ContextCompat.getColor(chartAverages.getContext(), R.color.web_text_color);

                    chartAverages.setDescription(getString(R.string.baseline));
                    chartAverages.setDescriptionColor(webTextColor);

                    chartAverages.setWebLineWidth(1.5f);
                    chartAverages.setWebAlpha(200);
                    chartAverages.setWebLineWidthInner(0.75f);
                    chartAverages.setWebColor(ContextCompat.getColor(chartAverages.getContext(), R.color.transparent));
                    chartAverages.setWebColorInner(ContextCompat.getColor(chartAverages.getContext(), R.color.transparent));

                    chartAverages.setTouchEnabled(false);

                    RadarMarkerView mv = new RadarMarkerView(chartAverages.getContext(), R.layout.custom_marker_view);
                    chartAverages.setMarkerView(mv);

                    XAxis xAxis = chartAverages.getXAxis();
                    xAxis.setTextSize(9f);
                    xAxis.setTextColor(textColor);

                    YAxis yAxis = chartAverages.getYAxis();
                    yAxis.setLabelCount(5, false);
                    yAxis.setTextSize(9f);
                    yAxis.setTextColor(webTextColor);
                    yAxis.setAxisMinValue(0f);

                    Legend l = chartAverages.getLegend();
                    l.setEnabled(false);

                    List<String> titles = new ArrayList<>();

                    titles.add(cleanTitleString(R.string.damage));
                    titles.add(cleanTitleString(R.string.short_kills_game));
                    titles.add(cleanTitleString(R.string.short_win_rate));
                    titles.add(cleanTitleString(R.string.short_planes_game));
//                    titles.add(getString(R.string.short_cap_points));
//                    titles.add(getString(R.string.short_def_reset));

//                    titles.add(getString(R.string.survival_rate));
//                    titles.add(getString(R.string.survived_wins));
//                    titles.add(getString(R.string.average_xp));

                    List<Entry> yVals = new ArrayList<>();

                    float battles = ship.getBattles();
                    yVals.add(new Entry((float) (((ship.getTotalDamage() / battles) / stat.getDmg_dlt()) * 100), 0));
                    yVals.add(new Entry((float) (((ship.getFrags() / battles) / stat.getFrags()) * 100), 1)); //kills
                    yVals.add(new Entry((float) (((ship.getWins() / battles) / stat.getWins()) * 100), 2)); //winrate
                    yVals.add(new Entry(((float) (((ship.getPlanesKilled() / battles) / stat.getPls_kd()) * 100)), 5)); // planes
//                    yVals.add(new Entry((float) (((ship.getCapturePoints() / battles) / stat.getCap_pts()) * 100), 3)); // captures
//                    yVals.add(new Entry((float) (((ship.getDroppedCapturePoints() / battles) / stat.getDr_cap_pts()) * 100), 4)); // def reset

//                    yVals.add(new Entry(((float) (((ship.getSurvivedBattles() / battles) / stat.getSr_bat()) * 100)), 6)); // survival
//                    yVals.add(new Entry(((float) (((ship.getSurvivedWins() / battles) / stat.getSr_wins()) * 100)), 7)); // survived wins
//                    yVals.add(new Entry(((float) (((ship.getTotalXP() / battles) / stat.getAvg_xp()) * 100)), 8)); // xp


                    RadarDataSet set = new RadarDataSet(yVals, "");
                    set.setColor(color);
                    set.setDrawFilled(true);
                    set.setLineWidth(2f);

                    List<IRadarDataSet> sets = new ArrayList<>();
                    sets.add(set);

                    RadarData data = new RadarData(titles, sets);
                    data.setValueTextColor(textColor);
                    data.setValueTextSize(8f);


                    chartAverages.setData(data);
                    chartAverages.invalidate();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        chartAverages.post(runnable);
    }

    public void setId(long id) {
        this.id = id;
    }
}