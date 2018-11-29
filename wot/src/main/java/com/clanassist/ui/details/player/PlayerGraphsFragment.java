package com.clanassist.ui.details.player;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.clanassist.CAApp;
import com.clanassist.model.enums.ChartType;
import com.clanassist.model.events.details.PlayerClanAdvancedHit;
import com.clanassist.model.events.details.PlayerClearEvent;
import com.clanassist.model.events.details.PlayerProfileHit;
import com.clanassist.model.interfaces.IDetails;
import com.clanassist.model.player.PlayerGraphs;
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
 * Created by Harrison on 6/20/2015.
 */
public class PlayerGraphsFragment extends Fragment {

    private IDetails details;

    private BarChart chartBattlePerTier;
    private PieChart chartBattlesPerType;
    private HorizontalBarChart chartBattlesPerNation;

    private BarChart chartWN8PerTier;
    private PieChart chartWN8PerType;
    private HorizontalBarChart chartWN8PerNation;

    private BarChart chartTanksPerTier;
    private PieChart chartTanksPerType;
    private HorizontalBarChart chartTanksPerNation;

//    private View progress;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player_graphs, container, false);
        bindView(view);
        return view;
    }

    private void bindView(View view) {

        UIUtils.setUpCard(view, R.id.player_graph_chart_battles_per_tier);
        chartBattlePerTier = (BarChart) view.findViewById(R.id.player_graph_battles_per_tier);

        UIUtils.setUpCard(view, R.id.player_graph_chart_battles_per_class);
        chartBattlesPerType = (PieChart) view.findViewById(R.id.player_graph_battles_per_class);

        UIUtils.setUpCard(view, R.id.player_graph_chart_battles_per_nation);
        chartBattlesPerNation = (HorizontalBarChart) view.findViewById(R.id.player_graph_battles_per_nation);

        UIUtils.setUpCard(view, R.id.player_graph_chart_wn8_per_tier);
        chartWN8PerTier = (BarChart) view.findViewById(R.id.player_graph_wn8_per_tier);

        UIUtils.setUpCard(view, R.id.player_graph_chart_wn8_per_class);
        chartWN8PerType = (PieChart) view.findViewById(R.id.player_graph_wn8_per_class);

        UIUtils.setUpCard(view, R.id.player_graph_chart_wn8_per_nation);
        chartWN8PerNation = (HorizontalBarChart) view.findViewById(R.id.player_graph_wn8_per_nation);

        UIUtils.setUpCard(view, R.id.player_graph_chart_tanks_per_tier);
        chartTanksPerTier = (BarChart) view.findViewById(R.id.player_graph_tanks_per_tier);

        UIUtils.setUpCard(view, R.id.player_graph_chart_tanks_per_class);
        chartTanksPerType = (PieChart) view.findViewById(R.id.player_graph_tanks_per_class);

        UIUtils.setUpCard(view, R.id.player_graph_chart_tanks_per_nation);
        chartTanksPerNation = (HorizontalBarChart) view.findViewById(R.id.player_graph_tanks_per_nation);

//        progress = view.findViewById(R.id.player_graph_progress);
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
            ctx = chartBattlePerTier.getContext();
        }
        if(ctx != null) {
            if (details == null) {
                details = (IDetails) getActivity().getSupportFragmentManager().findFragmentById(R.id.container);
            }
            if (details.getPlayer(getActivity()) != null) {
                PlayerGraphs graphs = details.getPlayer(ctx).getPlayerGraphs();
                Dlog.d("GraphsFragment", "Graphs = " + graphs);
                if (graphs != null) {
                    boolean isLightTheme = CAApp.isLightTheme(ctx);
                    int height = UIUtils.getChartHeight(getActivity());

                    List<Double> wn8PerTier = new ArrayList<Double>();
                    List<Double> tanksPerTier = new ArrayList<Double>();
                    List<Double> gamesPerTier = new ArrayList<Double>();

                    List<Integer> tiers = new ArrayList<Integer>();
                    if (graphs.getBattlesPerTier() != null) {
                        Iterator<Integer> wn8Iter = graphs.getBattlesPerTier().keySet().iterator();
                        while (wn8Iter.hasNext()) {
                            int key = wn8Iter.next();
                            tiers.add(key);
                        }
                        Collections.sort(tiers, new Comparator<Integer>() {
                            @Override
                            public int compare(Integer lhs, Integer rhs) {
                                return lhs - rhs;
                            }
                        });
                        for (Integer v : tiers) {
                            wn8PerTier.add(graphs.getAverageWN8PerTier().get(v));
                            double numTanks = graphs.getTanksPerTier().get(v);
                            tanksPerTier.add(numTanks);
                            gamesPerTier.add(graphs.getBattlesPerTier().get(v));
                        }

//                        Dlog.d("PlayerGraphs", "" + wn8PerTier);
                        if(wn8PerTier.size() > 0)
                            ChartManager.buildBarChart(chartWN8PerTier, tiers, wn8PerTier, isLightTheme, height, ChartType.TIER, true, false);
//                        Dlog.d("PlayerGraphs", "" + graphs.getAverageWN8PerNation());
                        if(graphs.getAverageWN8PerNation().size() > 0)
                            ChartManager.buildHorizontalBarChart(chartWN8PerNation, graphs.getAverageWN8PerNation(), isLightTheme, height, ChartType.NATION, true, false);
//                        Dlog.d("PlayerGraphs", "" + graphs.getAverageWN8PerClass());
                        if(graphs.getAverageWN8PerClass().size() > 0)
                            ChartManager.buildPieChart(chartWN8PerType, graphs.getAverageWN8PerClass(), isLightTheme, height, ChartType.CLASS, true, false);

//                        Dlog.d("PlayerGraphs", "" + gamesPerTier);
                        if(gamesPerTier.size() > 0)
                            ChartManager.buildBarChart(chartBattlePerTier, tiers, gamesPerTier, isLightTheme, height, ChartType.TIER, false, true);
//                        Dlog.d("PlayerGraphs", "" + graphs.getBattlesPerClass());
                        if(graphs.getBattlesPerClass().size() > 0)
                            ChartManager.buildPieChart(chartBattlesPerType, graphs.getBattlesPerClass(), isLightTheme, height, ChartType.CLASS, false, true);
//                        Dlog.d("PlayerGraphs", "" + graphs.getBattlesPerNation());
                        if(graphs.getBattlesPerNation().size() > 0)
                            ChartManager.buildHorizontalBarChart(chartBattlesPerNation, graphs.getBattlesPerNation(), isLightTheme, height, ChartType.NATION, false, false);

//                        Dlog.d("PlayerGraphs", "" + tanksPerTier);
                        if(tanksPerTier.size() > 0)
                            ChartManager.buildBarChart(chartTanksPerTier, tiers, tanksPerTier, isLightTheme, height, ChartType.TIER, false, true);
//                        Dlog.d("PlayerGraphs", "" + graphs.getTanksPerNation());
                        if(graphs.getTanksPerNation().size() > 0)
                            ChartManager.buildHorizontalBarChart(chartTanksPerNation, ChartManager.convertStrMap(graphs.getTanksPerNation()), isLightTheme, height, ChartType.NATION, false, true);
//                        Dlog.d("PlayerGraphs", "" + graphs.getTanksPerClass());
                        if(graphs.getTanksPerClass().size() > 0)
                            ChartManager.buildPieChart(chartTanksPerType, ChartManager.convertStrMap(graphs.getTanksPerClass()), isLightTheme, height, ChartType.CLASS, false, true);
                    }
//                    progress.setVisibility(View.GONE);
                } else {
//                    progress.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Subscribe
    public void onReceived(PlayerProfileHit result) {
        initView();
    }


    @Subscribe
    public void onRefreshClearEvent(PlayerClearEvent event) {
        chartTanksPerTier.clear();
        chartBattlePerTier.clear();
        chartBattlesPerNation.clear();
        chartBattlesPerType.clear();
        chartTanksPerNation.clear();
        chartTanksPerType.clear();
        chartWN8PerNation.clear();
        chartWN8PerTier.clear();
        chartWN8PerType.clear();
    }

    public void setDetails(IDetails details) {
        this.details = details;
    }
}