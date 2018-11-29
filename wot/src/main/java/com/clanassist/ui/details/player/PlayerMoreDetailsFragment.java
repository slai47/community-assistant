package com.clanassist.ui.details.player;

import android.content.Context;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.clanassist.CAApp;
import com.clanassist.SVault;
import com.clanassist.model.events.details.PlayerProfileHit;
import com.clanassist.model.infoobj.CombinedInfoObject;
import com.clanassist.model.infoobj.VehicleWN8;
import com.clanassist.model.enums.ChartType;
import com.clanassist.model.events.details.PlayerClanAdvancedHit;
import com.clanassist.model.events.details.PlayerClearEvent;
import com.clanassist.model.events.details.PlayerDifferenceSavedEvent;
import com.clanassist.model.infoobj.Tank;
import com.clanassist.model.infoobj.Tanks;
import com.clanassist.model.infoobj.WN8Data;
import com.clanassist.model.interfaces.IDetails;
import com.clanassist.model.player.Player;
import com.clanassist.model.player.PlayerVehicleInfo;
import com.clanassist.model.player.WN8StatsInfo;
import com.clanassist.model.player.storage.PlayerFutureStats;
import com.clanassist.model.player.storage.PlayerSavedStats;
import com.clanassist.model.player.storage.SavedStatsObj;
import com.clanassist.model.statistics.Statistics;
import com.clanassist.tools.CPStorageManager;
import com.clanassist.ui.UIUtils;
import com.clanassist.ui.managers.ChartManager;
import com.cp.assist.R;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.squareup.otto.Subscribe;
import com.utilities.Utils;
import com.utilities.logging.Dlog;
import com.utilities.preferences.Prefs;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Harrison on 6/28/2015.
 */
public class PlayerMoreDetailsFragment extends Fragment {

    private IDetails details;

    private TextView tvTankingFactor;
    private TextView tvAvgSpot;
    private TextView tvAvgTrackDmg;
    private TextView tvAvgDamageBlocked;
    private TextView tvAvgDamageAssisted;
    private TextView tvDamagingSpots;
    private TextView tvTreesCut;

    private TextView tvGlobalRating;

    private TextView tvMasteryBadges;
    private TextView tvFirstClassBadges;
    private TextView tvSecondClassBadges;
    private TextView tvThirdClassBadges;

    private TextView tvBestDamageName;
    private TextView tvBestDamage;

    private TextView tvBestExp;
    private TextView tvBestExpName;

    private TextView tvBestFrags;
    private TextView tvBestFragsName;

    private View topWN8TankArea;
    private TextView tvTopWN8TankName;
    private TextView tvTopWN8TankWN8;

    private View progress;

    private View aChartWn8;
    private LineChart chartWn8;

    private View aChartTier10s;
    private TextView tvChartTier10s;
    private HorizontalBarChart chartTier10s;

    private View aChartTier10sGames;
    private TextView tvChartTier10sGames;
    private HorizontalBarChart chartTier10sGames;

    private View aChartTier9s;
    private TextView tvChartTier9s;
    private HorizontalBarChart chartTier9s;

    private View aChartTier9sGames;
    private TextView tvChartTier9sGames;
    private HorizontalBarChart chartTier9sGames;

    private View aChartTier8s;
    private TextView tvChartTier8s;
    private HorizontalBarChart chartTier8s;

    private View aChartTier8sGames;
    private TextView tvChartTier8sGames;
    private HorizontalBarChart chartTier8sGames;


    private View aChartTier7s;
    private TextView tvChartTier7s;
    private HorizontalBarChart chartTier7s;

    private View aChartTier7sGames;
    private TextView tvChartTier7sGames;
    private HorizontalBarChart chartTier7sGames;


    private View aChartTier6s;
    private TextView tvChartTier6s;
    private HorizontalBarChart chartTier6s;

    private View aChartTier6sGames;
    private TextView tvChartTier6sGames;
    private HorizontalBarChart chartTier6sGames;


    private View aChartTier5s;
    private TextView tvChartTier5s;
    private HorizontalBarChart chartTier5s;

    private View aChartTier5sGames;
    private TextView tvChartTier5sGames;
    private HorizontalBarChart chartTier5sGames;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player_more, container, false);
        bindView(view);
        return view;
    }

    private void bindView(View view) {
        UIUtils.setUpCard(view, R.id.more_player_details_top_area);
        UIUtils.setUpCard(view, R.id.more_player_details_more_info);
        UIUtils.setUpCard(view, R.id.more_player_details_chart_wn8_changes_area);
        UIUtils.setUpCard(view, R.id.more_player_details_chart_tier_10s_area);
        UIUtils.setUpCard(view, R.id.more_player_details_chart_tier_10s_games_area);

        UIUtils.setUpCard(view, R.id.more_player_details_chart_tier_8s_area);
        UIUtils.setUpCard(view, R.id.more_player_details_chart_tier_8s_games_area);
        UIUtils.setUpCard(view, R.id.more_player_details_chart_tier_6s_area);
        UIUtils.setUpCard(view, R.id.more_player_details_chart_tier_6s_games_area);

        UIUtils.setUpCard(view, R.id.more_player_details_chart_tier_9s_area);
        UIUtils.setUpCard(view, R.id.more_player_details_chart_tier_9s_games_area);
        UIUtils.setUpCard(view, R.id.more_player_details_chart_tier_7s_area);
        UIUtils.setUpCard(view, R.id.more_player_details_chart_tier_7s_games_area);
        UIUtils.setUpCard(view, R.id.more_player_details_chart_tier_5s_area);
        UIUtils.setUpCard(view, R.id.more_player_details_chart_tier_5s_games_area);

        progress = view.findViewById(R.id.more_player_details_progress);

        tvGlobalRating = (TextView) view.findViewById(R.id.more_player_details_info_global_rating);
        tvTankingFactor = (TextView) view.findViewById(R.id.more_player_details_info_tanking_factor);
        tvAvgTrackDmg = (TextView) view.findViewById(R.id.more_player_details_info_tracking_damage);
        tvAvgDamageAssisted = (TextView) view.findViewById(R.id.more_player_details_info_assist_damage);

        tvTreesCut = (TextView) view.findViewById(R.id.more_player_details_info_trees_cut);
        tvAvgSpot = (TextView) view.findViewById(R.id.more_player_details_info_avg_spotting);
        tvAvgDamageBlocked = (TextView) view.findViewById(R.id.more_player_details_info_damage_blocked);
        tvDamagingSpots = (TextView) view.findViewById(R.id.more_player_details_info_penatration_factor);

        tvMasteryBadges = (TextView) view.findViewById(R.id.more_player_details_top_mastery_totals_M);
        tvFirstClassBadges = (TextView) view.findViewById(R.id.more_player_details_top_mastery_totals_1);
        tvSecondClassBadges = (TextView) view.findViewById(R.id.more_player_details_top_mastery_totals_2);
        tvThirdClassBadges = (TextView) view.findViewById(R.id.more_player_details_top_mastery_totals_3);

        tvBestDamage = (TextView) view.findViewById(R.id.more_player_details_top_best_game_damage);
        tvBestDamageName = (TextView) view.findViewById(R.id.more_player_details_top_best_game_damage_name);

        tvBestExp = (TextView) view.findViewById(R.id.more_player_details_top_best_exp);
        tvBestExpName = (TextView) view.findViewById(R.id.more_player_details_top_best_exp_name);

        tvBestFrags = (TextView) view.findViewById(R.id.more_player_details_top_best_frag_frags);
        tvBestFragsName = (TextView) view.findViewById(R.id.more_player_details_top_best_frag_name);

        tvTopWN8TankName = (TextView) view.findViewById(R.id.more_player_details_top_best_wn8_name);
        tvTopWN8TankWN8 = (TextView) view.findViewById(R.id.more_player_details_top_best_wn8);

        aChartWn8 = view.findViewById(R.id.more_player_details_chart_wn8_changes_area);
        chartWn8 = (LineChart) view.findViewById(R.id.more_player_details_chart_wn8_changes);

        aChartTier10s = view.findViewById(R.id.more_player_details_chart_tier_10s_area);
        tvChartTier10s = (TextView) view.findViewById(R.id.more_player_details_chart_tier_10s_text);
        chartTier10s = (HorizontalBarChart) view.findViewById(R.id.more_player_details_chart_tier_10s);

        aChartTier10sGames = view.findViewById(R.id.more_player_details_chart_tier_10s_games_area);
        tvChartTier10sGames = (TextView) view.findViewById(R.id.more_player_details_chart_tier_10s_games_text);
        chartTier10sGames = (HorizontalBarChart) view.findViewById(R.id.more_player_details_chart_tier_10s_games);

        aChartTier9s = view.findViewById(R.id.more_player_details_chart_tier_9s_area);
        tvChartTier9s = (TextView) view.findViewById(R.id.more_player_details_chart_tier_9s_text);
        chartTier9s = (HorizontalBarChart) view.findViewById(R.id.more_player_details_chart_tier_9s);

        aChartTier9sGames = view.findViewById(R.id.more_player_details_chart_tier_9s_games_area);
        tvChartTier9sGames = (TextView) view.findViewById(R.id.more_player_details_chart_tier_9s_games_text);
        chartTier9sGames = (HorizontalBarChart) view.findViewById(R.id.more_player_details_chart_tier_9s_games);

        aChartTier8s = view.findViewById(R.id.more_player_details_chart_tier_8s_area);
        tvChartTier8s = (TextView) view.findViewById(R.id.more_player_details_chart_tier_8s_text);
        chartTier8s = (HorizontalBarChart) view.findViewById(R.id.more_player_details_chart_tier_8s);

        aChartTier8sGames = view.findViewById(R.id.more_player_details_chart_tier_8s_games_area);
        tvChartTier8sGames = (TextView) view.findViewById(R.id.more_player_details_chart_tier_8s_games_text);
        chartTier8sGames = (HorizontalBarChart) view.findViewById(R.id.more_player_details_chart_tier_8s_games);

        aChartTier7s = view.findViewById(R.id.more_player_details_chart_tier_7s_area);
        tvChartTier7s = (TextView) view.findViewById(R.id.more_player_details_chart_tier_7s_text);
        chartTier7s = (HorizontalBarChart) view.findViewById(R.id.more_player_details_chart_tier_7s);

        aChartTier7sGames = view.findViewById(R.id.more_player_details_chart_tier_7s_games_area);
        tvChartTier7sGames = (TextView) view.findViewById(R.id.more_player_details_chart_tier_7s_games_text);
        chartTier7sGames = (HorizontalBarChart) view.findViewById(R.id.more_player_details_chart_tier_7s_games);

        aChartTier6s = view.findViewById(R.id.more_player_details_chart_tier_6s_area);
        tvChartTier6s = (TextView) view.findViewById(R.id.more_player_details_chart_tier_6s_text);
        chartTier6s = (HorizontalBarChart) view.findViewById(R.id.more_player_details_chart_tier_6s);

        aChartTier6sGames = view.findViewById(R.id.more_player_details_chart_tier_6s_games_area);
        tvChartTier6sGames = (TextView) view.findViewById(R.id.more_player_details_chart_tier_6s_games_text);
        chartTier6sGames = (HorizontalBarChart) view.findViewById(R.id.more_player_details_chart_tier_6s_games);

        aChartTier5s = view.findViewById(R.id.more_player_details_chart_tier_5s_area);
        tvChartTier5s = (TextView) view.findViewById(R.id.more_player_details_chart_tier_5s_text);
        chartTier5s = (HorizontalBarChart) view.findViewById(R.id.more_player_details_chart_tier_5s);

        aChartTier5sGames = view.findViewById(R.id.more_player_details_chart_tier_5s_games_area);
        tvChartTier5sGames = (TextView) view.findViewById(R.id.more_player_details_chart_tier_5s_games_text);
        chartTier5sGames = (HorizontalBarChart) view.findViewById(R.id.more_player_details_chart_tier_5s_games);
    }

    @Override
    public void onResume() {
        super.onResume();
        CAApp.getEventBus().register(this);
        initView();
    }

    @Override
    public void onPause() {
        super.onPause();
        CAApp.getEventBus().unregister(this);
    }

    private void initView() {
        Context ctx = getActivity();
        if(ctx == null){
            ctx = progress.getContext();
        }
        if(ctx != null) {
            if (details == null) {
                details = (IDetails) getActivity().getSupportFragmentManager().findFragmentById(R.id.container);
            }
            Player p = details.getPlayer(ctx);
            if (p != null) {
                findPlayerDifference(null);

                Prefs pref = new Prefs(ctx);
                boolean isColorBlind = pref.getBoolean(SVault.PREF_COLORBLIND, false);
                progress.setVisibility(View.GONE);
                Statistics stats = p.getOverallStats();

                if (p.getBadges() != null) {
                    tvMasteryBadges.setText(p.getBadges().getMastery() + "");
                    tvFirstClassBadges.setText(p.getBadges().getFirstClass() + "");
                    tvSecondClassBadges.setText(p.getBadges().getSecondClass() + "");
                    tvThirdClassBadges.setText(p.getBadges().getThirdClass() + "");
                }
                tvGlobalRating.setText(p.getGlobalRating() + "");

                Tanks tanks = CAApp.getInfoManager().getTanks(getContext());
                Tank maxDmgTank = tanks.getTank(p.getMaxDamageTankId());
                Tank maxXPTank = tanks.getTank(p.getMaxXpTankId());
                Tank maxFragsTank = tanks.getTank(p.getMaxFragsTankId());
                Tank maxWn8Tank = tanks.getTank(p.getBestTankIdWN8());

                tvTopWN8TankName.setText((maxWn8Tank != null ? maxWn8Tank.getName() : getString(R.string.player_details_unknown_tank)));
                tvBestDamageName.setText((maxDmgTank != null ? maxDmgTank.getName() : getString(R.string.player_details_unknown_tank)));
                tvBestExpName.setText((maxXPTank != null ? maxXPTank.getName() : getString(R.string.player_details_unknown_tank)));
                tvBestFragsName.setText((maxFragsTank != null ? maxFragsTank.getName() : getString(R.string.player_details_unknown_tank)));

                tvBestFrags.setText(p.getMaxFrags() + "");
                tvBestExp.setText(p.getMaxXp() + "");
                tvBestDamage.setText(p.getMaxDamage() + "");
                tvTreesCut.setText(p.getTreesCut() + "");

                int wn8 = p.getBestTankWN8();
                tvTopWN8TankWN8.setText(wn8 + "");

                if (stats != null) {
                    tvTankingFactor.setText(stats.getTankingFactor() + "");
                    tvAvgDamageAssisted.setText(Utils.getOneDepthDecimalFormatter().format(stats.getAvgDamageAssisted()) + "");
                    tvAvgSpot.setText(Utils.getOneDepthDecimalFormatter().format(stats.getAvgDamageAssistedRadio()) + "");
                    tvAvgDamageBlocked.setText(Utils.getOneDepthDecimalFormatter().format(stats.getAvgDamageBlocked()) + "");
                    tvAvgTrackDmg.setText(Utils.getOneDepthDecimalFormatter().format(stats.getAvgDamageAssistedTrack()) + "");
                    double pens = 0.0;
                    if (stats.getShots() != 0)
                        pens = (((double) (stats.getPiercings() + stats.getExplosionHits())) / ((double) stats.getShots())) * 100;
                    tvDamagingSpots.setText(Utils.getDefaultDecimalFormatter().format(pens) + "%");
                }
                buildTierCharts(p,
                        chartTier10s, chartTier10sGames,
                        tvChartTier10s, tvChartTier10sGames, 10,
                        R.string.wn8_per_top_tier, R.string.battles_per_top_tier, R.string.no_tier_10s_found, R.string.no_tier_10s_found_games
                );
                buildTierCharts(p,
                        chartTier9s, chartTier9sGames,
                        tvChartTier9s, tvChartTier9sGames, 9,
                        R.string.wn8_per_tier_9, R.string.battles_per_tier_9, R.string.no_tier_9s_found, R.string.no_tier_9s_found_games
                );
                buildTierCharts(p,
                        chartTier8s, chartTier8sGames,
                        tvChartTier8s, tvChartTier8sGames, 8,
                        R.string.wn8_per_tier_8, R.string.battles_per_tier_8, R.string.no_tier_8s_found, R.string.no_tier_8s_found_games
                );
                buildTierCharts(p,
                        chartTier7s, chartTier7sGames,
                        tvChartTier7s, tvChartTier7sGames, 7,
                        R.string.wn8_per_tier_7, R.string.battles_per_tier_7, R.string.no_tier_7s_found, R.string.no_tier_7s_found_games
                );
                buildTierCharts(p,
                        chartTier6s, chartTier6sGames,
                        tvChartTier6s, tvChartTier6sGames, 6,
                        R.string.wn8_per_tier_6, R.string.battles_per_tier_6, R.string.no_tier_6s_found, R.string.no_tier_6s_found_games
                );
                buildTierCharts(p,
                        chartTier5s, chartTier5sGames,
                        tvChartTier5s, tvChartTier5sGames, 5,
                        R.string.wn8_per_tier_5, R.string.battles_per_tier_5, R.string.no_tier_5s_found, R.string.no_tier_5s_found_games
                );
            } else {
                progress.setVisibility(View.VISIBLE);
            }
        }
    }

    private void buildTierCharts(final Player p,
                                 final HorizontalBarChart wn8Chart, final HorizontalBarChart gamesChart,
                                 TextView wnt8Text, TextView gamesText,
                                 final int tier, int textWn8TextRes, int textGamesTextRes, int textNoWn8FoundRes, int textNoGamesFoundRes) {
        if (p.getPlayerGraphs() != null) {
            if (p.getPlayerGraphs().getTanksPerTier() != null) {
                int numberOfTanks = p.getPlayerGraphs().getTanksPerTier().get(tier);
//                Dlog.d("NumOfTanks", "tier = " + tier + " tanks = " + numberOfTanks);
                if (numberOfTanks > 0) {
                    new Runnable() {
                        @Override
                        public void run() {
                            Tanks tanks = CAApp.getInfoManager().getTanks(wn8Chart.getContext());
                            WN8Data wn8Data = CAApp.getInfoManager().getWN8Data(wn8Chart.getContext());
                            Map<Integer, PlayerVehicleInfo> tiersMap = new HashMap<Integer, PlayerVehicleInfo>();
                            List<CombinedInfoObject> tiersList = new ArrayList<CombinedInfoObject>();
                            for (PlayerVehicleInfo info : p.getPlayerVehicleInfoList()) {
                                VehicleWN8 vWN8 = wn8Data.getWN8(info.getTankId());
                                Tank tankInfo = tanks.getTank(info.getTankId());
                                if (vWN8 != null && tankInfo != null) {
                                    if(tankInfo.getTier() == tier) {
                                        tiersList.add(new CombinedInfoObject(tankInfo, vWN8));
                                        tiersMap.put(info.getTankId(), info);
                                    }
                                }
                            }
                            Collections.sort(tiersList, new Comparator<CombinedInfoObject>() {
                                @Override
                                public int compare(CombinedInfoObject lhs, CombinedInfoObject rhs) {
                                    return rhs.getTank().getName().compareToIgnoreCase(lhs.getTank().getName());
                                }
                            });
                            List<String> names = new ArrayList<String>();
                            List<Double> tierNumbers = new ArrayList<Double>();
                            List<Double> tierGames = new ArrayList<Double>();

                            for (CombinedInfoObject v : tiersList) {
                                PlayerVehicleInfo info = tiersMap.get(v.getWn8().getId());
                                names.add(tanks.getTank(v.getWn8().getId()).getName());
                                tierNumbers.add((double) info.getWN8());
                                tierGames.add((double) info.getOverallStats().getBattles());
                            }
                            boolean isLightTheme = CAApp.isLightTheme(getActivity());
                            int height = UIUtils.getChartHeight(getActivity());
                            if (names.size() > 12) {
                                height = (int) (height * UIUtils.CHART_SIZE_PORTION);
                            } else if (names.size() > 8) {
                                height = height * 2;
                            } else if (names.size() < 5) {
                                height = height / 2;
                            }
                            if (tierNumbers.size() > 0) {
                                ChartManager.buildHorizontalBarChart(wn8Chart, names, tierNumbers, isLightTheme, height, ChartType.TIER, true, false, false);
                            }
                            if (tierGames.size() > 0){
                                ChartManager.buildHorizontalBarChart(gamesChart, names, tierGames, isLightTheme, height, ChartType.TIER, false, false, true);
                            }
                        }
                    }.run();
                    wnt8Text.setText(textWn8TextRes);
                    gamesText.setText(textGamesTextRes);
                    wn8Chart.setVisibility(View.VISIBLE);
                    gamesChart.setVisibility(View.VISIBLE);
                } else {
                    wnt8Text.setText(textNoWn8FoundRes);
                    wn8Chart.setVisibility(View.GONE);
                    gamesText.setText(textNoGamesFoundRes);
                    gamesChart.setVisibility(View.GONE);
                }
            } else {
                wnt8Text.setText(textNoWn8FoundRes);
                gamesText.setText(textNoGamesFoundRes);
                wn8Chart.setVisibility(View.GONE);
                gamesChart.setVisibility(View.GONE);
            }
        } else {
            wnt8Text.setText(textNoWn8FoundRes);
            gamesText.setText(textNoGamesFoundRes);
            wn8Chart.setVisibility(View.GONE);
            gamesChart.setVisibility(View.GONE);
        }
    }

    @Subscribe
    public void onReceived(PlayerProfileHit result) {
        initView();
        progress.setVisibility(View.GONE);
    }

    @Subscribe
    public void findPlayerDifference(PlayerDifferenceSavedEvent event) {
        new Runnable() {
            @Override
            public void run() {
                try {
                    Player p = details.getPlayer(getActivity());
                    SavedStatsObj obj = CPStorageManager.getPlayerStats(getActivity(), p.getId());
                    PlayerFutureStats stats = CPStorageManager.getPlayerFutureStats(getActivity(), p.getId());
                    if (obj != null) {
                        if (obj.getStats() != null) {
                            if (obj.getStats().size() > 3) {
                                final List<String> xList = new ArrayList<String>();
                                final List<List<Float>> lineNumbers = new ArrayList<List<Float>>();
                                List<Float> numbers = new ArrayList<Float>();
                                List<Float> recentDayNumbers = new ArrayList<Float>();
                                List<Float> recent7Numbers = new ArrayList<Float>();
                                List<Float> recent30Numbers = new ArrayList<Float>();
                                List<Float> recent60Numbers = new ArrayList<Float>();

                                for (int i = obj.getStats().size() - 1; i >= 0; i--) {
                                    PlayerSavedStats stat = obj.getStats().get(i);
                                    float wn8 = stat.getWn8();
                                    if(stat.getStats() != null) {
                                        xList.add(stat.getStats().getBattles() + "");
                                        numbers.add(wn8);
                                    }
                                }
                                lineNumbers.add(numbers);

                                if(stats != null) {
                                    for (int j = stats.getStatsInfos().size() - 1; j >= 0 && recentDayNumbers.size() < numbers.size(); j--) {
                                        WN8StatsInfo info = stats.getStatsInfos().get(j);
                                        recentDayNumbers.add((float) info.getPastDay());
                                        recent7Numbers.add((float) info.getPastWeek());
                                        recent30Numbers.add((float) info.getPastMonth());
                                        recent60Numbers.add((float) info.getPastTwoMonths());
                                    }
                                    lineNumbers.add(recentDayNumbers);
                                    lineNumbers.add(recent7Numbers);
                                    lineNumbers.add(recent30Numbers);
                                    lineNumbers.add(recent60Numbers);
                                }

//                                Dlog.d("lengths", "xList " + xList.size() + " overall = " + numbers.size() + " 24h " + recentDayNumbers.size() + " 7d " + recent7Numbers.size() + " 30d " + recent30Numbers.size() + " 60d " + recent60Numbers.size());

                                aChartWn8.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            aChartWn8.setVisibility(View.VISIBLE);
                                            ChartManager.buildLinesChart(chartWn8, xList, lineNumbers, CAApp.isLightTheme(aChartWn8.getContext()), UIUtils.getChartHeight(getActivity()));
                                        } catch (Exception e) {
                                        }
                                    }
                                });
                            } else {
                                aChartWn8.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        aChartWn8.setVisibility(View.GONE);
                                    }
                                });
                            }
                        }
                    }
                    obj = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.run();
    }


    @Subscribe
    public void onRefreshClearEvent(PlayerClearEvent event) {
        tvBestFragsName.setText("");
        tvBestFrags.setText("");
        tvBestDamageName.setText("");
        tvBestDamage.setText("");
        tvAvgDamageAssisted.setText("");
        tvAvgDamageBlocked.setText("");
        tvAvgSpot.setText("");
        tvAvgTrackDmg.setText("");
        tvBestExp.setText("");
        tvBestExpName.setText("");
        tvDamagingSpots.setText("");
        tvFirstClassBadges.setText("");
        tvGlobalRating.setText("");
        tvSecondClassBadges.setText("");
        tvTankingFactor.setText("");
        tvThirdClassBadges.setText("");
        tvTopWN8TankName.setText("");
        tvTopWN8TankWN8.setText("");
        tvTreesCut.setText("");
        tvMasteryBadges.setText("");

        chartTier10s.clear();
        chartWn8.clear();
        chartTier10sGames.clear();
    }

    public void setDetails(IDetails details) {
        this.details = details;
    }
}