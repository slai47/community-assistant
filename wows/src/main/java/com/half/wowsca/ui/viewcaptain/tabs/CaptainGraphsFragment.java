package com.half.wowsca.ui.viewcaptain.tabs;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.half.wowsca.CAApp;
import com.half.wowsca.R;
import com.half.wowsca.interfaces.ICaptain;
import com.half.wowsca.model.BatteryStats;
import com.half.wowsca.model.Captain;
import com.half.wowsca.model.Ship;
import com.half.wowsca.model.ShipCompare;
import com.half.wowsca.model.encyclopedia.holders.ShipsHolder;
import com.half.wowsca.model.encyclopedia.items.ShipInfo;
import com.half.wowsca.model.events.CaptainReceivedEvent;
import com.half.wowsca.model.events.ProgressEvent;
import com.half.wowsca.model.events.RefreshEvent;
import com.half.wowsca.ui.CAFragment;
import org.greenrobot.eventbus.Subscribe;
import com.utilities.logging.Dlog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by slai4 on 9/15/2015.
 */
public class CaptainGraphsFragment extends CAFragment {


    private BarChart chartAverageExperience;
    private BarChart chartAverageDamage;
    private BarChart chartAverageWinRate;
    private BarChart chartAverageSurvival;
    private BarChart chartAverageAccuracy;
    private HorizontalBarChart chartAverageExperienceClass;
    private HorizontalBarChart chartAverageDamageClass;
    private HorizontalBarChart chartAverageWinRateClass;
    private HorizontalBarChart chartAverageSurvivalClass;

    private HorizontalBarChart chartTopTenPlayed;
    private HorizontalBarChart chartTopTenExp;
    private HorizontalBarChart chartTopTenDmg;
    private HorizontalBarChart chartTopTenWinRate;
    private HorizontalBarChart chartTopTenKD;
    private HorizontalBarChart chartTopTenAccuracy;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_captain_graphs, container, false);
        bindView(view);
        return view;
    }

    private void bindView(View view) {
        chartAverageExperience = (BarChart) view.findViewById(R.id.captain_details_graphs_avg_exp_per_tier);
        chartAverageDamage = (BarChart) view.findViewById(R.id.captain_details_graphs_damage_per_tier);
        chartAverageWinRate = (BarChart) view.findViewById(R.id.captain_details_graphs_win_rate_per_tier);
        chartAverageSurvival = (BarChart) view.findViewById(R.id.captain_details_graphs_survival_per_tier);
        chartAverageAccuracy = (BarChart) view.findViewById(R.id.captain_details_graphs_accuracy_per_tier);

        chartAverageDamageClass = (HorizontalBarChart) view.findViewById(R.id.captain_details_graphs_damage_per_class);
        chartAverageWinRateClass = (HorizontalBarChart) view.findViewById(R.id.captain_details_graphs_win_rate_per_class);
        chartAverageExperienceClass = (HorizontalBarChart) view.findViewById(R.id.captain_details_graphs_experience_per_class);
        chartAverageSurvivalClass = (HorizontalBarChart) view.findViewById(R.id.captain_details_graphs_survival_rate_per_class);

        chartTopTenPlayed = (HorizontalBarChart) view.findViewById(R.id.captain_details_graphs_top_ten_played);
        chartTopTenExp = (HorizontalBarChart) view.findViewById(R.id.captain_details_graphs_top_ten_exp);
        chartTopTenDmg = (HorizontalBarChart) view.findViewById(R.id.captain_details_graphs_top_ten_average_dmg);
        chartTopTenWinRate = (HorizontalBarChart) view.findViewById(R.id.captain_details_graphs_top_ten_win_rate);
        chartTopTenKD = (HorizontalBarChart) view.findViewById(R.id.captain_details_graphs_top_ten_k_d);
        chartTopTenAccuracy = (HorizontalBarChart) view.findViewById(R.id.captain_details_graphs_top_ten_accuracy);

        bindSwipe(view);
        initSwipeLayout();
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
        Captain captain = null;
        try {
            captain = ((ICaptain) getActivity()).getCaptain(getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (captain != null && captain.getDetails() != null && captain.getDetails().getBattles() > 0) {
            refreshing(false);
            setUpCharts(captain);
        } else {
        }
    }

    private void setUpCharts(final Captain captain) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                SparseArray<Integer> battleCounts = new SparseArray<>();
                Map<String, Integer> battleCountsClass = new HashMap<>();

                SparseArray<Long> experience = new SparseArray<>();
                SparseArray<Long> damages = new SparseArray<>();
                SparseArray<Long> wins = new SparseArray<>();
                SparseArray<Long> survivalRate = new SparseArray<>();
                SparseArray<Long> accuracyHits = new SparseArray<>();
                SparseArray<Long> accuracyShots = new SparseArray<>();

                Map<String, Long> damageClass = new HashMap<>();
                Map<String, Long> winsClass = new HashMap<>();
                Map<String, Long> expClass = new HashMap<>();
                Map<String, Long> survivalClass = new HashMap<>();
                ShipsHolder shipsHolder = CAApp.getInfoManager().getShipInfo(getContext());
                if(captain.getShips() != null) {
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
                            Long winRate = wins.get(tier);
                            if (winRate != null) {
                                wins.put(tier, (long) (winRate + s.getWins()));
                            } else {
                                wins.put(tier, (long) s.getWins());
                            }
                            Long survival = survivalRate.get(tier);
                            if (survival != null) {
                                survivalRate.put(tier, (long) (survival + s.getSurvivedBattles()));
                            } else {
                                survivalRate.put(tier, (long) s.getSurvivedBattles());
                            }

                            Long hits = accuracyHits.get(tier);
                            BatteryStats stats = s.getMainBattery();
                            if(stats.getShots() > 0) {
                                if (hits != null) {
                                    accuracyHits.put(tier, hits + stats.getHits());
                                } else {
                                    accuracyHits.put(tier, (long) stats.getHits());
                                }
                            }

                            Long shots = accuracyShots.get(tier);
                            BatteryStats statsShots = s.getMainBattery();
                            if(statsShots.getShots() > 0) {
                                if (shots != null) {
                                    accuracyShots.put(tier, shots + statsShots.getShots());
                                } else {
                                    accuracyShots.put(tier, (long) statsShots.getShots());
                                }
                            }

                            //class area
                            String shipType = info.getType();
                            Integer battleCountC = battleCountsClass.get(shipType);
                            if (battleCountC != null) {
                                battleCountsClass.put(shipType, battleCountC + s.getBattles());
                            } else {
                                battleCountsClass.put(shipType, s.getBattles());
                            }
                            Long damageC = damageClass.get(shipType);
                            if (damageC != null) {
                                damageClass.put(shipType, (long) (damageC + s.getTotalDamage()));
                            } else {
                                damageClass.put(shipType, (long) s.getTotalDamage());
                            }
                            Long winsC = winsClass.get(shipType);
                            if (winsC != null) {
                                winsClass.put(shipType, (long) (winsC + s.getWins()));
                            } else {
                                winsClass.put(shipType, (long) s.getWins());
                            }
                            Long expC = expClass.get(shipType);
                            if (expC != null) {
                                expClass.put(shipType, (long) expC + s.getTotalXP());
                            } else {
                                expClass.put(shipType, (long) s.getTotalXP());
                            }
                            Long survivalC = survivalClass.get(shipType);
                            if (survivalC != null) {
                                survivalClass.put(shipType, (long) survivalC + s.getSurvivedBattles());
                            } else {
                                survivalClass.put(shipType, (long) s.getSurvivedBattles());
                            }
                        }
                    }
                    Map<Integer, Long> averages = new HashMap<Integer, Long>();
                    Map<Integer, Long> avgDamages = new HashMap<Integer, Long>();
                    Map<Integer, Long> avgWinRate = new HashMap<Integer, Long>();
                    Map<Integer, Long> avgSuvival = new HashMap<Integer, Long>();
                    Map<Integer, Long> avgAccuracy = new HashMap<>();
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
                        Long win = wins.get(i);
                        if (win != null && battleCount > 0) {
                            avgWinRate.put(i, (long) ((win / (float) battleCount) * 100));
                        } else {
                            avgWinRate.put(i, 0l);
                        }
                        Long survival = survivalRate.get(i);
                        if (survival != null && battleCount > 0) {
                            avgSuvival.put(i, (long) ((survival / (float) battleCount) * 100));
                        } else {
                            avgSuvival.put(i, 0l);
                        }
                        Long hits = accuracyHits.get(i);
                        Long shots = accuracyShots.get(i);
                        if(hits != null && shots > 0){
                            avgAccuracy.put(i, (long) ((hits / (float)shots) * 100));
                        } else {
                            avgAccuracy.put(i, 0l);
                        }
                    }

                    for (String key : battleCountsClass.keySet()) {
                        Integer battles = battleCountsClass.get(key);
                        if (battles > 0) {
                            float fBattles = battles;
                            Long damage = damageClass.get(key);
                            Long winsC = winsClass.get(key);
                            Long expC = expClass.get(key);
                            Long survivalC = survivalClass.get(key);
                            if (damage != null) {
                                damageClass.put(key, (long) (damage / fBattles));
                            } else {
                                damageClass.put(key, 0l);
                            }
                            if (winsC != null) {
                                winsClass.put(key, (long) ((winsC / fBattles) * 100));
                            } else {
                                winsClass.put(key, 0l);
                            }
                            if (expC != null) {
                                expClass.put(key, (long) ((expC / fBattles)));
                            } else {
                                expClass.put(key, 0l);
                            }
                            if (survivalC != null) {
                                survivalClass.put(key, (long) ((survivalC / fBattles) * 100));
                            } else {
                                survivalClass.put(key, 0l);
                            }
                        } else {
                            damageClass.put(key, 0l);
                            winsClass.put(key, 0l);
                            expClass.put(key, 0l);
                            survivalClass.put(key, 0l);
                        }
                    }

                    ShipCompare compare = new ShipCompare();
                    compare.setShipsHolder(shipsHolder);

                    List<Ship> shipsClone = new ArrayList<>();
                    for (Ship s : captain.getShips()) {
                        if (s.getBattles() > 0)
                            shipsClone.add(s);
                    }
                    TopTenObj battlesTen = new TopTenObj();
                    Collections.sort(shipsClone, compare.battlesComparator);
                    for (int i = 0; i < 10 && i < shipsClone.size(); i++) {
                        Ship s = shipsClone.get(i);
                        ShipInfo info = shipsHolder.get(s.getShipId());
                        String name = s.getShipId() + "";
                        if (info != null)
                            name = info.getName();

                        battlesTen.names.add(name);
                        battlesTen.data.add((float) s.getBattles());
                    }
                    battlesTen.reverse();

                    TopTenObj averageExpTen = new TopTenObj();
                    Collections.sort(shipsClone, compare.averageExpComparator);
                    for (int i = 0; i < 10 && i < shipsClone.size(); i++) {
                        Ship s = shipsClone.get(i);
                        ShipInfo info = shipsHolder.get(s.getShipId());
                        String name = s.getShipId() + "";
                        if (info != null)
                            name = info.getName();

                        averageExpTen.names.add(name);
                        averageExpTen.data.add((s.getTotalXP() / (float) s.getBattles()));
                    }
                    averageExpTen.reverse();

                    TopTenObj averageDmgTen = new TopTenObj();
                    Collections.sort(shipsClone, compare.averageDamageComparator);
                    for (int i = 0; i < 10 && i < shipsClone.size(); i++) {
                        Ship s = shipsClone.get(i);
                        ShipInfo info = shipsHolder.get(s.getShipId());
                        String name = s.getShipId() + "";
                        if (info != null)
                            name = info.getName();

                        averageDmgTen.names.add(name);
                        averageDmgTen.data.add((float) (s.getTotalDamage() / (float) s.getBattles()));
                    }
                    averageDmgTen.reverse();

                    TopTenObj averageWRTen = new TopTenObj();
                    Collections.sort(shipsClone, compare.winRateComparator);
                    for (int i = 0; averageWRTen.names.size() < 10 && i < shipsClone.size(); i++) {
                        Ship s = shipsClone.get(i);
                        if (s.getBattles() > 4) {
                            ShipInfo info = shipsHolder.get(s.getShipId());
                            String name = s.getShipId() + "";
                            if (info != null)
                                name = info.getName();

                            averageWRTen.names.add(name);
                            averageWRTen.data.add((((float) s.getWins() / (float) s.getBattles()) * 100f));
                        }
                    }
                    averageWRTen.reverse();

                    TopTenObj averageKDTen = new TopTenObj();
                    Collections.sort(shipsClone, compare.killsDeathComparator);
                    for (int i = 0; i < 10 && i < shipsClone.size(); i++) {
                        Ship s = shipsClone.get(i);
                        ShipInfo info = shipsHolder.get(s.getShipId());
                        String name = s.getShipId() + "";
                        if (info != null)
                            name = info.getName();

                        averageKDTen.names.add(name);
                        float deaths = s.getBattles() - s.getSurvivedBattles();
                        if(deaths <= 1)
                            deaths = 1f;
                        float frags = s.getFrags();
                        averageKDTen.data.add(frags / deaths);
                    }
                    averageKDTen.reverse();

                    TopTenObj averageAccuracyTen = new TopTenObj();
                    Collections.sort(shipsClone, compare.accuracyComparator);
                    for (int i = 0; i < 10 && i < shipsClone.size(); i++) {
                        Ship s = shipsClone.get(i);
                        ShipInfo info = shipsHolder.get(s.getShipId());
                        String name = s.getShipId() + "";
                        if (info != null)
                            name = info.getName();

                        averageAccuracyTen.names.add(name);
                        float shots = s.getMainBattery().getShots();
                        float hits = s.getMainBattery().getHits();
                        if(shots > 0)
                            averageAccuracyTen.data.add((hits / shots) * 100f);
                    }
                    averageAccuracyTen.reverse();

                    if (avgDamages.size() > 0)
                        setUpBarChart(chartAverageDamage, avgDamages, true);
                    if (averages.size() > 0)
                        setUpBarChart(chartAverageExperience, averages, true);
                    if (avgSuvival.size() > 0)
                        setUpBarChart(chartAverageSurvival, avgSuvival, true);
                    if (avgWinRate.size() > 0)
                        setUpBarChart(chartAverageWinRate, avgWinRate, true);
                    if (avgAccuracy.size() > 0)
                        setUpBarChart(chartAverageAccuracy, avgAccuracy, false);

                    if (damageClass.size() > 0)
                        setUpClassCharts(chartAverageDamageClass, damageClass, false);
                    if (winsClass.size() > 0)
                        setUpClassCharts(chartAverageWinRateClass, winsClass, true);
                    if (expClass.size() > 0)
                        setUpClassCharts(chartAverageExperienceClass, expClass, false);
                    if (survivalClass.size() > 0)
                        setUpClassCharts(chartAverageSurvivalClass, survivalClass, true);

                    if (battlesTen.count() > 0)
                        setUpTopTenCharts(chartTopTenPlayed, battlesTen, false);
                    if (averageExpTen.count() > 0)
                        setUpTopTenCharts(chartTopTenExp, averageExpTen, false);
                    if (averageDmgTen.count() > 0)
                        setUpTopTenCharts(chartTopTenDmg, averageDmgTen, false);
                    if (averageKDTen.count() > 0)
                        setUpTopTenCharts(chartTopTenKD, averageKDTen, false);
                    if (averageWRTen.count() > 0)
                        setUpTopTenCharts(chartTopTenWinRate, averageWRTen, true);
                    if(averageAccuracyTen.count() > 0)
                        setUpTopTenCharts(chartTopTenAccuracy, averageAccuracyTen, true);

                    shipsClone = null;
                }
            }

            private Map<Long, Ship> grabTopTen(List<Ship> ships){
                Map<Long, Ship> topTen = new HashMap<>();
                for(int i = 0; i < 10 && i < ships.size(); i++){
                    topTen.put(ships.get(i).getShipId(), ships.get(i));
                }
                return topTen;
            }

            private void setUpTopTenCharts(final HorizontalBarChart chart, final TopTenObj averages, final boolean percentage) {
                chart.post(new Runnable() {
                    @Override
                    public void run() {
                        int textColor = CAApp.getTextColor(chart.getContext());
                        boolean colorblind = CAApp.isColorblind(chart.getContext());
                        int accentColor = !colorblind ?
                                (CAApp.getTheme(chart.getContext()).equals("ocean") ?
                                        ContextCompat.getColor(chart.getContext(), R.color.graph_line_color) : ContextCompat.getColor(chart.getContext(), R.color.top_background))
                                : ContextCompat.getColor(chart.getContext(), R.color.white);
                        chart.setDrawBarShadow(false);
                        chart.setDrawValueAboveBar(false);
                        chart.setPinchZoom(false);
                        chart.setDoubleTapToZoomEnabled(false);
                        chart.setDrawGridBackground(false);
                        chart.setDrawValueAboveBar(true);
                        chart.setTouchEnabled(false);

                        setupXAxis(textColor, chart);

                        setupYAxis(textColor, chart);

                        setupYAxis2(textColor, chart);

                        Legend l = chart.getLegend();
                        l.setEnabled(false);

                        ArrayList<String> xVals = averages.names;

                        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
                        for (int i = 0; i < averages.data.size(); i++) {
                            double dValue = averages.data.get(i);
                            float value = (float) dValue;
                            yVals1.add(new BarEntry(value, i));
                        }

                        BarDataSet set1 = new BarDataSet(yVals1, "");
                        set1.setBarSpacePercent(20f);
                        set1.setColor(accentColor);

                        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
                        dataSets.add(set1);

                        BarData data = new BarData(xVals, dataSets);
                        data.setValueTextSize(10f);
                        data.setValueTextColor(textColor);
                        if(!percentage) {
//                            data.setValueFormatter(new LargeValueFormatter());
                        } else {
                            data.setValueFormatter(new PercentFormatter());
                        }
                        chart.setDescription("");

                        chart.setData(data);
                        chart.requestLayout();
                    }
                });

            }

            private void setUpBarChart(final BarChart chart, final Map<Integer, Long> averages, final boolean useLargeFormatter){
                chart.post(new Runnable() {
                    @Override
                    public void run() {
                        int textColor = CAApp.getTextColor(chart.getContext());
                        boolean colorblind = CAApp.isColorblind(chart.getContext());
                        int accentColor = !colorblind ?
                                (CAApp.getTheme(chart.getContext()).equals("ocean") ?
                                        ContextCompat.getColor(chart.getContext(), R.color.graph_line_color) : ContextCompat.getColor(chart.getContext(), R.color.top_background))
                                : ContextCompat.getColor(chart.getContext(), R.color.white);
                        chart.setDrawBarShadow(false);
                        chart.setDrawValueAboveBar(false);
                        chart.setPinchZoom(false);
                        chart.setDoubleTapToZoomEnabled(false);
                        chart.setDrawGridBackground(false);
                        chart.setDrawValueAboveBar(true);
                        chart.setTouchEnabled(false);


                        setupXAxis(textColor, chart);

                        setupYAxis(textColor, chart);


                        YAxis yAxis2 = chart.getAxisLeft();
                        yAxis2.setLabelCount(6, false);
                        yAxis2.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
                        yAxis2.setTextColor(textColor);
                        yAxis2.setValueFormatter(new LargeValueFormatter());

                        Legend l = chart.getLegend();
                        l.setEnabled(false);

                        ArrayList<String> xVals = new ArrayList<String>();
                        for (int i = 1; i <= 10; i++)
                            xVals.add(i + "");

                        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
                        List<Integer> colorList = new ArrayList<Integer>();
                        for (int i = 0; i < 10; i++) {
                            double dValue = averages.get(i + 1);
                            float value = (float) dValue;
                            yVals1.add(new BarEntry(value, i));
                        }

//                        cleanUpTitles(type, xVals);

                        BarDataSet set1 = new BarDataSet(yVals1, "");
                        set1.setBarSpacePercent(20f);
                        set1.setColor(accentColor);
                        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
                        dataSets.add(set1);

                        BarData data = new BarData(xVals, dataSets);
                        data.setValueTextSize(10f);
                        data.setValueTextColor(textColor);
                        if (useLargeFormatter)
                            data.setValueFormatter(new LargeValueFormatter());
                        else
                            data.setValueFormatter(new PercentFormatter());

                        chart.setDescription("");

                        chart.setData(data);
                        chart.requestLayout();
                    }
                });
            }

            private void setUpClassCharts(final HorizontalBarChart chart, final Map<String, Long> averages, final boolean percentage) {
                chart.post(new Runnable() {
                    @Override
                    public void run() {
                        int textColor = CAApp.getTextColor(chart.getContext());
                        boolean colorblind = CAApp.isColorblind(chart.getContext());

                        chart.setDrawBarShadow(false);
                        chart.setDrawValueAboveBar(false);
                        chart.setPinchZoom(false);
                        chart.setDoubleTapToZoomEnabled(false);
                        chart.setDrawGridBackground(false);
                        chart.setDrawValueAboveBar(true);
                        chart.setTouchEnabled(false);

                        setupXAxis(textColor, chart);

                        setupYAxis(textColor, chart);

                        setupYAxis2(textColor, chart);

                        Legend l = chart.getLegend();
                        l.setEnabled(false);

                        ArrayList<String> xVals = new ArrayList<String>();
                        Iterator<String> itea = averages.keySet().iterator();
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
                            double dValue = averages.get(xVals.get(i));
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
                        if(!percentage) {
                            data.setValueFormatter(new LargeValueFormatter());
                        } else {
                            data.setValueFormatter(new PercentFormatter());
                        }
                        chart.setDescription("");

                        chart.setData(data);
                        chart.requestLayout();
                    }
                });

            }

        };
        new Thread(runnable).start();
    }

    private void setupYAxis2(int textColor, HorizontalBarChart chart) {
        YAxis yAxis2 = chart.getAxisLeft();
        yAxis2.setLabelCount(6, false);
        yAxis2.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxis2.setTextColor(textColor);
    }

    private void setupYAxis(int textColor, BarChart chart) {
        YAxis yAxis = chart.getAxisRight();
        yAxis.setLabelCount(4, false);
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxis.setTextColor(textColor);
        yAxis.setEnabled(false);
    }

    private void setupXAxis(int textColor, BarChart chart) {
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(textColor);
        xAxis.setDrawGridLines(true);
    }

    private class TopTenObj{

        ArrayList<String> names;
        ArrayList<Float> data;

        public TopTenObj() {
            this.names = new ArrayList<>();
            this.data = new ArrayList<>();
        }

        public void reverse(){
            Collections.reverse(names);
            Collections.reverse(data);
        }

        public void print(){
            Dlog.d("names", names.toString());
            Dlog.d("data", data.toString());
        }

        public int count(){
            int total = 0;
            if(names != null)
                total = names.size();
            else if(data != null)
                total = data.size();
            return total;
        }
    }

    @Subscribe
    public void onReceive(CaptainReceivedEvent event) {
        initView();
    }

    @Subscribe
    public void onRefresh(RefreshEvent event) {
        refreshing(true);
        chartAverageExperienceClass.clear();
        chartAverageExperience.clear();
        chartAverageWinRateClass.clear();
        chartAverageWinRate.clear();
        chartAverageDamageClass.clear();
        chartAverageDamage.clear();
        chartAverageSurvivalClass.clear();
        chartAverageSurvival.clear();
    }

    @Subscribe
    public void onProgressEvent(ProgressEvent event){
        if(mSwipeRefreshLayout != null){
            mSwipeRefreshLayout.setRefreshing(event.isRefreshing());
        }
    }
}
