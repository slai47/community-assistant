package com.clanassist.ui.details.clan;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.clanassist.CAApp;
import com.clanassist.model.clan.Clan;
import com.clanassist.model.clan.ClanGraphs;
import com.clanassist.model.enums.ChartType;
import com.clanassist.model.events.ClanLoadedFinishedEvent;
import com.clanassist.model.infoobj.CombinedInfoObject;
import com.clanassist.model.infoobj.Tank;
import com.clanassist.model.infoobj.Tanks;
import com.clanassist.model.infoobj.VehicleWN8;
import com.clanassist.model.infoobj.WN8Data;
import com.clanassist.model.interfaces.IDetails;
import com.clanassist.model.search.results.ClanPlayerWN8sTaskResult;
import com.clanassist.tools.CPStorageManager;
import com.clanassist.tools.DownloadedClanManager;
import com.clanassist.ui.UIUtils;
import com.clanassist.ui.managers.ChartManager;
import com.cp.assist.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.squareup.otto.Subscribe;
import com.utilities.logging.Dlog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Harrison on 6/27/2015.
 */
public class ClanGraphsFragment extends Fragment {

    private IDetails details;

    private View aCharts;

    private BarChart chartGamesPerTier;
    private PieChart chartGamesPerClass;

    private BarChart chartWN8PerTier;
    private PieChart chartWN8PerClass;

    private HorizontalBarChart chartWN8PerTier10;
    private HorizontalBarChart chartGamesPerTier10;
    private HorizontalBarChart chartTanksPerTier10;


    private HorizontalBarChart chartWN8PerTier8;
    private HorizontalBarChart chartGamesPerTier8;
    private HorizontalBarChart chartTanksPerTier8;


    private HorizontalBarChart chartWN8PerTier6;
    private HorizontalBarChart chartGamesPerTier6;
    private HorizontalBarChart chartTanksPerTier6;

    private TextView tvError;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clan_graphs, container, false);
        bindView(view);
        return view;
    }

    private void bindView(View view) {
        UIUtils.setUpCard(view, R.id.clan_graph_chart_games_per_class);
        UIUtils.setUpCard(view, R.id.clan_graph_chart_games_per_tier);
        UIUtils.setUpCard(view, R.id.clan_graph_chart_games_per_tier10);
        UIUtils.setUpCard(view, R.id.clan_graph_chart_wn8_per_class);
        UIUtils.setUpCard(view, R.id.clan_graph_chart_wn8_per_tier);
        UIUtils.setUpCard(view, R.id.clan_graph_chart_wn8_per_tier10);
        UIUtils.setUpCard(view, R.id.clan_graph_chart_tanks_per_tier10);

        UIUtils.setUpCard(view, R.id.clan_graph_chart_wn8_per_tier6);
        UIUtils.setUpCard(view, R.id.clan_graph_chart_tanks_per_tier6);
        UIUtils.setUpCard(view, R.id.clan_graph_chart_games_per_tier6);

        UIUtils.setUpCard(view, R.id.clan_graph_chart_wn8_per_tier8);
        UIUtils.setUpCard(view, R.id.clan_graph_chart_tanks_per_tier8);
        UIUtils.setUpCard(view, R.id.clan_graph_chart_games_per_tier8);

        chartGamesPerTier = (BarChart) view.findViewById(R.id.clan_graph_games_per_tier);
        chartGamesPerClass = (PieChart) view.findViewById(R.id.clan_graph_games_per_class);

        chartWN8PerClass = (PieChart) view.findViewById(R.id.clan_graph_wn8_per_class);
        chartWN8PerTier = (BarChart) view.findViewById(R.id.clan_graph_wn8_per_tier);

        chartWN8PerTier10 = (HorizontalBarChart) view.findViewById(R.id.clan_graph_wn8_per_tier10);
        chartGamesPerTier10 = (HorizontalBarChart) view.findViewById(R.id.clan_graph_games_per_tier10);
        chartTanksPerTier10 = (HorizontalBarChart) view.findViewById(R.id.clan_graph_tanks_per_tier10);

        chartWN8PerTier8 = (HorizontalBarChart) view.findViewById(R.id.clan_graph_wn8_per_tier8);
        chartGamesPerTier8 = (HorizontalBarChart) view.findViewById(R.id.clan_graph_games_per_tier8);
        chartTanksPerTier8 = (HorizontalBarChart) view.findViewById(R.id.clan_graph_tanks_per_tier8);

        chartWN8PerTier6 = (HorizontalBarChart) view.findViewById(R.id.clan_graph_wn8_per_tier6);
        chartGamesPerTier6 = (HorizontalBarChart) view.findViewById(R.id.clan_graph_games_per_tier6);
        chartTanksPerTier6 = (HorizontalBarChart) view.findViewById(R.id.clan_graph_tanks_per_tier6);

        tvError = (TextView) view.findViewById(R.id.clan_graph_error_text);
        aCharts = view.findViewById(R.id.clan_graph_charts);
    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
        CAApp.getEventBus().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        CAApp.getEventBus().unregister(this);
    }

    private void initView() {
        try {
            if (details == null) {
                details = (IDetails) getActivity().getSupportFragmentManager().findFragmentById(R.id.container);
            }
        } catch (Exception e) {
        }
        if(details != null){
            Clan clan = details.getClan(getActivity());
            if (clan != null) {
                boolean isLoaded = DownloadedClanManager.hasClanDownloadLoaded(getActivity(), clan.getClanId() + "");
                boolean isDownloaded = CPStorageManager.hasClanTaskResult(getActivity(), clan.getClanId());
                if (!isDownloaded) {
                    tvError.setText(R.string.details_query_clan_info_not_downloaded);
                    tvError.setVisibility(View.VISIBLE);
                    aCharts.setVisibility(View.GONE);
                } else if (!isLoaded) {
                    tvError.setText(R.string.details_query_loading);
                    tvError.setVisibility(View.VISIBLE);
                    aCharts.setVisibility(View.GONE);
                } else {
                    //setUpGraphs
                    ClanPlayerWN8sTaskResult result = DownloadedClanManager.getClanDownload(clan.getClanId() + "");
                    ClanGraphs graphs = result.getGraphs();
                    if (graphs != null) {
                        Dlog.d("ClanGraphs","graphs = " + graphs.getAvgDamagePerTier6());
                        Dlog.d("ClanGraphs","graphs = " + graphs.getAvgDamagePerTier8());

                        aCharts.setVisibility(View.VISIBLE);
                        tvError.setVisibility(View.GONE);

                        boolean isLightTheme = CAApp.isLightTheme(getActivity());
                        int height = UIUtils.getChartHeight(getActivity());

                        List<Double> wn8PerTier = new ArrayList<Double>();
                        List<Double> gamesPerTier = new ArrayList<Double>();

                        List<Integer> tiers = new ArrayList<Integer>();
                        Iterator<Integer> gamesIter = graphs.getGamesPerTier().keySet().iterator();
                        while (gamesIter.hasNext()) {
                            int key = gamesIter.next();
                            tiers.add(key);
                        }
                        Collections.sort(tiers, new Comparator<Integer>() {
                            @Override
                            public int compare(Integer lhs, Integer rhs) {
                                return lhs - rhs;
                            }
                        });
                        for (Integer v : tiers) {
                            wn8PerTier.add(graphs.getWn8PerTier().get(v));
                            gamesPerTier.add(graphs.getGamesPerTier().get(v));
                        }

                        ChartManager.buildBarChart(chartGamesPerTier, tiers, gamesPerTier, isLightTheme, height, ChartType.TIER, false, true);
                        ChartManager.buildBarChart(chartWN8PerTier, tiers, wn8PerTier, isLightTheme, height, ChartType.TIER, true, false);

                        ChartManager.buildPieChart(chartGamesPerClass, graphs.getGamesPerClassType(), isLightTheme, height, ChartType.CLASS, false, true);
                        ChartManager.buildPieChart(chartWN8PerClass, graphs.getWn8PerClassType(), isLightTheme, height, ChartType.CLASS, true, false);


                        WN8Data wn8Data = CAApp.getInfoManager().getWN8Data(chartGamesPerClass.getContext());
                        Tanks tanks = CAApp.getInfoManager().getTanks(chartGamesPerClass.getContext());

                        List<Double> wn8PerTier10 = new ArrayList<Double>();
                        List<Double> tanksPerTier10 = new ArrayList<Double>();
                        List<Double> gamesPerTier10 = new ArrayList<Double>();

                        List<Double> wn8PerTier8 = new ArrayList<Double>();
                        List<Double> tanksPerTier8 = new ArrayList<Double>();
                        List<Double> gamesPerTier8 = new ArrayList<Double>();

                        List<Double> wn8PerTier6 = new ArrayList<Double>();
                        List<Double> tanksPerTier6 = new ArrayList<Double>();
                        List<Double> gamesPerTier6 = new ArrayList<Double>();

                        List<CombinedInfoObject> tier10s = new ArrayList<CombinedInfoObject>();
                        Iterator<Integer> wn8Iter = graphs.getWn8PerTier10().keySet().iterator();
                        while (wn8Iter.hasNext()) {
                            int key = wn8Iter.next();
                            Tank t = tanks.getTank(key);
                            VehicleWN8 wn8 = wn8Data.getWN8(key);
                            if(t != null && wn8 != null) {
                                CombinedInfoObject object = new CombinedInfoObject(t, wn8);
                                tier10s.add(object);
                            }
                        }

                        List<CombinedInfoObject> tier8s = new ArrayList<CombinedInfoObject>();
                        Iterator<Integer> wn88Iter = graphs.getWn8PerTier8().keySet().iterator();
                        while (wn88Iter.hasNext()) {
                            int key = wn88Iter.next();
                            Tank t = tanks.getTank(key);
                            VehicleWN8 wn8 = wn8Data.getWN8(key);
                            if(t != null && wn8 != null) {
                                CombinedInfoObject object = new CombinedInfoObject(t, wn8);
                                tier8s.add(object);
                            }
                        }

                        List<CombinedInfoObject> tier6s = new ArrayList<CombinedInfoObject>();
                        Iterator<Integer> wn86Iter = graphs.getWn8PerTier6().keySet().iterator();
                        while (wn86Iter.hasNext()) {
                            int key = wn86Iter.next();
                            Tank t = tanks.getTank(key);
                            VehicleWN8 wn8 = wn8Data.getWN8(key);
                            if(t != null && wn8 != null) {
                                CombinedInfoObject object = new CombinedInfoObject(t, wn8);
                                tier6s.add(object);
                            }
                        }
                        Collections.sort(tier10s, new Comparator<CombinedInfoObject>() {
                            @Override
                            public int compare(CombinedInfoObject lhs, CombinedInfoObject rhs) {
                                return rhs.getTank().getName().compareToIgnoreCase(lhs.getTank().getName());
                            }
                        });
                        Collections.sort(tier8s, new Comparator<CombinedInfoObject>() {
                            @Override
                            public int compare(CombinedInfoObject lhs, CombinedInfoObject rhs) {
                                return rhs.getTank().getName().compareToIgnoreCase(lhs.getTank().getName());
                            }
                        });
                        Collections.sort(tier6s, new Comparator<CombinedInfoObject>() {
                            @Override
                            public int compare(CombinedInfoObject lhs, CombinedInfoObject rhs) {
                                return rhs.getTank().getName().compareToIgnoreCase(lhs.getTank().getName());
                            }
                        });
                        List<String> namesList10 = new ArrayList<String>();
                        for (CombinedInfoObject v : tier10s) {
                            if(graphs.getGamesPerTier10().get(v.getTank().getId()) > 0) {
                                namesList10.add(v.getTank().getName());
                                wn8PerTier10.add(graphs.getWn8PerTier10().get(v.getTank().getId()));
                                tanksPerTier10.add(graphs.getTotalTankersPerTier10().get(v.getTank().getId()));
                                gamesPerTier10.add(graphs.getGamesPerTier10().get(v.getTank().getId()));
                            }
                        }

                        List<String> namesList8 = new ArrayList<String>();
                        for (CombinedInfoObject v : tier8s) {
                            if(graphs.getGamesPerTier8().get(v.getTank().getId()) > 0) {
                                namesList8.add(v.getTank().getName());
                                wn8PerTier8.add(graphs.getWn8PerTier8().get(v.getTank().getId()));
                                tanksPerTier8.add(graphs.getTotalTankersPerTier8().get(v.getTank().getId()));
                                gamesPerTier8.add(graphs.getGamesPerTier8().get(v.getTank().getId()));
                            }
                        }

                        List<String> namesList6 = new ArrayList<String>();
                        for (CombinedInfoObject v : tier6s) {
                            if(graphs.getGamesPerTier6().get(v.getTank().getId()) > 0) {
                                namesList6.add(v.getTank().getName());
                                wn8PerTier6.add(graphs.getWn8PerTier6().get(v.getTank().getId()));
                                tanksPerTier6.add(graphs.getTotalTankersPerTier6().get(v.getTank().getId()));
                                gamesPerTier6.add(graphs.getGamesPerTier6().get(v.getTank().getId()));
                            }
                        }

                        Dlog.d("ClanGraphs","list = " + namesList10);

                        Dlog.d("ClanGraphs","list = " + namesList8);

                        Dlog.d("ClanGraphs","list = " + namesList6);

                        // tier 10
                        ChartManager.buildHorizontalBarChart(chartWN8PerTier10, namesList10, wn8PerTier10, isLightTheme, (int) (height * UIUtils.CHART_SIZE_PORTION), ChartType.TIER, true, false, false);

                        ChartManager.buildHorizontalBarChart(chartTanksPerTier10, namesList10, tanksPerTier10, isLightTheme, (int) (height * UIUtils.CHART_SIZE_PORTION), ChartType.TIER, false, false, false);

                        ChartManager.buildHorizontalBarChart(chartGamesPerTier10, namesList10, gamesPerTier10, isLightTheme, (int) (height * UIUtils.CHART_SIZE_PORTION), ChartType.TIER, false, true, true);

                        // tier 8
                        ChartManager.buildHorizontalBarChart(chartWN8PerTier8, namesList8, wn8PerTier8, isLightTheme, (int) (height * UIUtils.CHART_SIZE_PORTION), ChartType.TIER, true, false, false);

                        ChartManager.buildHorizontalBarChart(chartTanksPerTier8, namesList8, tanksPerTier8, isLightTheme, (int) (height * UIUtils.CHART_SIZE_PORTION), ChartType.TIER, false, false, false);

                        ChartManager.buildHorizontalBarChart(chartGamesPerTier8, namesList8, gamesPerTier8, isLightTheme, (int) (height * UIUtils.CHART_SIZE_PORTION), ChartType.TIER, false, true, true);

                        // tier 6
                        ChartManager.buildHorizontalBarChart(chartWN8PerTier6, namesList6, wn8PerTier6, isLightTheme, (int) (height * UIUtils.CHART_SIZE_PORTION), ChartType.TIER, true, false, false);

                        ChartManager.buildHorizontalBarChart(chartTanksPerTier6, namesList6, tanksPerTier6, isLightTheme, (int) (height * UIUtils.CHART_SIZE_PORTION), ChartType.TIER, false, false, false);

                        ChartManager.buildHorizontalBarChart(chartGamesPerTier6, namesList6, gamesPerTier6, isLightTheme, (int) (height * UIUtils.CHART_SIZE_PORTION), ChartType.TIER, false, true, true);

                    } else {
                        aCharts.setVisibility(View.GONE);
                        tvError.setVisibility(View.VISIBLE);
                        tvError.setText(getString(R.string.clan_graphs_no_graphs));
                    }
                }
            }
        }
    }

    @Subscribe
    public void clanLoaded(ClanLoadedFinishedEvent event) {
        Clan c = getDetails().getClan(getActivity());
        if (c != null && event.getClanId().equals(c.getClanId() + "") && event.isMerged()) {
            aCharts.post(new Runnable() {
                @Override
                public void run() {
                    initView();
                }
            });
        }
    }

    public IDetails getDetails() {
        return details;
    }

    public void setDetails(IDetails details) {
        this.details = details;
    }
}
