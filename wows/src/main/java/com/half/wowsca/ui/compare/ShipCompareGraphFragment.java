package com.half.wowsca.ui.compare;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.utils.ColorTemplate;
import com.half.wowsca.CAApp;
import com.half.wowsca.R;
import com.half.wowsca.managers.CompareManager;
import com.half.wowsca.managers.InfoManager;
import com.half.wowsca.model.ShipInformation;
import com.half.wowsca.model.encyclopedia.holders.ShipsHolder;
import com.half.wowsca.model.encyclopedia.items.ShipStat;
import com.half.wowsca.ui.CAFragment;
import com.half.wowsca.ui.adapter.ShipsCompareAdapter;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by slai47 on 4/19/2017.
 */

public class ShipCompareGraphFragment extends CAFragment {

    RecyclerView recyclerView;
    ShipsCompareAdapter adapter;


    /**
    * this will produce all the graphs from the ship data grabbed
    */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_compare_ships_graphs, container, false);
        onBind(view);
        return view;
    }

    private void onBind(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.compare_ships_list);
        GridLayoutManager manager = new GridLayoutManager(view.getContext() , getResources().getInteger(R.integer.twitch_cols));
        recyclerView.setLayoutManager(manager);
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
        ShipsHolder holder = new InfoManager().getShipInfo(recyclerView.getContext());

        List<Map<String, Float>> graphsList = new ArrayList<>();
        List<String> graphNames = new ArrayList<>();
        Map<String, Integer> shipColors = new HashMap<>();

        Map<String, Float> health = new HashMap<>();
        Map<String, Float> survivalHealth = new HashMap<>();
        Map<String, Float> planeAmounts = new HashMap<>();

        Map<String, Float> overallMin = new HashMap<>();
        Map<String, Float> overallMax = new HashMap<>();
        Map<String, Float> deckMin = new HashMap<>();
        Map<String, Float> deckMax = new HashMap<>();
        Map<String, Float> extremitiesMin = new HashMap<>();
        Map<String, Float> extremitiesMax = new HashMap<>();
        Map<String, Float> casemateMin = new HashMap<>();
        Map<String, Float> casemateMax = new HashMap<>();
        Map<String, Float> citadelMin = new HashMap<>();
        Map<String, Float> citadelMax = new HashMap<>();

        Map<String, Float> artilleryTotal = new HashMap<>();
        Map<String, Float> torpTotal = new HashMap<>();
        Map<String, Float> antiAirTotal = new HashMap<>();
        Map<String, Float> aircraftTotal = new HashMap<>();
        Map<String, Float> mobilityTotal = new HashMap<>();
        Map<String, Float> concealmentTotal = new HashMap<>();

        Map<String, Float> turningRadius = new HashMap<>();

        Map<String, Float> speed = new HashMap<>();
        Map<String, Float> rudderTime = new HashMap<>();

        Map<String, Float> artiDistance = new HashMap<>();
        Map<String, Float> secondaryRange = new HashMap<>();

        Map<String, Float> torpDistance = new HashMap<>();
        Map<String, Float> torpReload = new HashMap<>();
        Map<String, Float> torpMaxDamage = new HashMap<>();
        Map<String, Float> torpSpeed = new HashMap<>();
        Map<String, Float> torpSlots = new HashMap<>();
        Map<String, Float> torpBarrels = new HashMap<>();
        Map<String, Float> torpGuns = new HashMap<>();

        Map<String, Float> torpBDistance = new HashMap<>();
        Map<String, Float> torpBprepareTime = new HashMap<>();
        Map<String, Float> torpBDamage = new HashMap<>();
        Map<String, Float> torpBMaxSpeed = new HashMap<>();
        Map<String, Float> fighterSquadrons = new HashMap<>();
        Map<String, Float> bomberSquadrons = new HashMap<>();
        Map<String, Float> torpedoSquadrons = new HashMap<>();

        Map<String, Float> concealmentDistanceShip = new HashMap<>();
        Map<String, Float> concealmentDistancePlane = new HashMap<>();

        Map<String, Float> artiAPDmg = new HashMap<>();
        Map<String, Float> artiHEDmg = new HashMap<>();
        Map<String, Float> artiHEBurnProb = new HashMap<>();
        Map<String, Float> artiRotation = new HashMap<>();
        Map<String, Float> artiMaxDispersion = new HashMap<>();
        Map<String, Float> artiTurrets = new HashMap<>();
        Map<String, Float> artiBarrels = new HashMap<>();
        Map<String, Float> artiGunRate = new HashMap<>();

        Map<String, Float> diveBPrepareTime = new HashMap<>();
        Map<String, Float> diveBMaxDmg = new HashMap<>();
        Map<String, Float> diveBBurnProbably = new HashMap<>();

        Map<String, Float> topAARange = new HashMap<>();


        Map<String, Float> warshipStatsDmg = new HashMap<>();
        Map<String, Float> warshipStatsWR = new HashMap<>();
        Map<String, Float> warshipStatsKills = new HashMap<>();
        Map<String, Float> warshipStatsPlanes = new HashMap<>();

        int[] colors = ColorTemplate.PASTEL_COLORS;
        int i = 0;

        for(Long shipId : CompareManager.getSHIPS()) {
            String shipInfo = CompareManager.getShipInformation().get(shipId);
            if(shipInfo != null) {
                ShipInformation ship = new ShipInformation();
                ship.parse(shipInfo);
                String shipName = holder.get(shipId).getName();

                shipColors.put(shipName, colors[i]);

                addGraphInfo(health, shipName, (float) ship.getHealth());

                addGraphInfo(survivalHealth, shipName, (float) ship.getSurvivalHealth());
                addGraphInfo(planeAmounts, shipName, (float) ship.getPlanesAmount());

                addGraphInfo(overallMin, shipName, (float) ship.getOverallMin());
                addGraphInfo(overallMax, shipName, (float) ship.getOverallMax());
                addGraphInfo(deckMin, shipName, (float) ship.getDeckMin());
                addGraphInfo(deckMax, shipName, (float) ship.getDeckMax());
                addGraphInfo(extremitiesMin, shipName, (float) ship.getExtremitiesMin());
                addGraphInfo(extremitiesMax, shipName, (float) ship.getExtremitiesMax());
                addGraphInfo(casemateMin, shipName, (float) ship.getCasemateMin());
                addGraphInfo(casemateMax, shipName, (float) ship.getCasemateMax());
                addGraphInfo(citadelMin, shipName, (float) ship.getCitadelMin());
                addGraphInfo(citadelMax, shipName, (float) ship.getCitadelMax());

                addGraphInfo(artilleryTotal, shipName, (float) ship.getArtilleryTotal());
                addGraphInfo(torpTotal, shipName, (float) ship.getTorpTotal());
                addGraphInfo(antiAirTotal, shipName, (float) ship.getAntiAirTotal());
                addGraphInfo(aircraftTotal, shipName, (float) ship.getAircraftTotal());
                addGraphInfo(mobilityTotal, shipName, (float) ship.getMobilityTotal());
                addGraphInfo(concealmentTotal, shipName, (float) ship.getConcealmentTotal());


                addGraphInfo(turningRadius, shipName, (float) ship.getTurningRadius());

                addGraphInfo(speed, shipName, (float) ship.getSpeed());
                addGraphInfo(rudderTime, shipName, (float) ship.getRudderTime());

                addGraphInfo(artiDistance, shipName, (float) ship.getArtiDistance());
                addGraphInfo(secondaryRange, shipName, (float) ship.getSecondaryRange());

                addGraphInfo(torpDistance, shipName, (float) ship.getTorpDistance());
                addGraphInfo(torpReload, shipName, (float) ship.getTorpReload());
                addGraphInfo(torpMaxDamage, shipName, (float) ship.getTorpMaxDamage());
                addGraphInfo(torpSpeed, shipName, (float) ship.getTorpSpeed());
                addGraphInfo(torpSlots, shipName, (float) ship.getTorpSlots());
                addGraphInfo(torpBarrels, shipName, (float) ship.getTorpBarrels());
                addGraphInfo(torpGuns, shipName, (float) ship.getTorpGuns());


                addGraphInfo(torpBDistance, shipName, (float) ship.getTorpBDistance());
                addGraphInfo(torpBprepareTime, shipName, (float) ship.getTorpBprepareTime());
                addGraphInfo(torpBDamage, shipName, (float) ship.getTorpBDamage());
                addGraphInfo(torpBMaxSpeed, shipName, (float) ship.getTorpBMaxSpeed());
                addGraphInfo(fighterSquadrons, shipName, (float) ship.getFighterSquadrons());
                addGraphInfo(bomberSquadrons, shipName, (float) ship.getBomberSquadrons());
                addGraphInfo(torpedoSquadrons, shipName, (float) ship.getTorpedoSquadrons());

                addGraphInfo(concealmentDistanceShip, shipName, (float) ship.getConcealmentDistanceShip());
                addGraphInfo(concealmentDistancePlane, shipName, (float) ship.getConcealmentDistancePlane());

                addGraphInfo(artiAPDmg, shipName, (float) ship.getArtiAPDmg());
                addGraphInfo(artiHEDmg, shipName, (float) ship.getArtiHEDmg());
                addGraphInfo(artiHEBurnProb, shipName, (float) ship.getArtiHEBurnProb());
                addGraphInfo(artiRotation, shipName, (float) ship.getArtiRotation());
                addGraphInfo(artiMaxDispersion, shipName, (float) ship.getArtiMaxDispersion());
                addGraphInfo(artiTurrets, shipName, (float) ship.getArtiTurrets());
                addGraphInfo(artiBarrels, shipName, (float) ship.getArtiBarrels());
                addGraphInfo(artiGunRate, shipName, (float) ship.getArtiGunRate());

                addGraphInfo(diveBPrepareTime, shipName, (float) ship.getDiveBPrepareTime());
                addGraphInfo(diveBMaxDmg, shipName, (float) ship.getDiveBMaxDmg());
                addGraphInfo(diveBBurnProbably, shipName, (float) ship.getDiveBBurnProbably());

                addGraphInfo(topAARange, shipName, (float) ship.getTopAARange());

                ShipStat stats = CAApp.getInfoManager().getShipStats(getContext()).get(shipId);
                if(stats != null) {
                    addGraphInfo(warshipStatsDmg, shipName, stats.getDmg_dlt());
                    addGraphInfo(warshipStatsWR, shipName, (stats.getWins() * 100));
                    addGraphInfo(warshipStatsKills, shipName, stats.getFrags());
                    addGraphInfo(warshipStatsPlanes, shipName, stats.getPls_kd());
                }
                i++;
            }
        }

        setGraphInfo(graphsList, graphNames, artilleryTotal, getString(R.string.encyclopedia_artillery));

        setGraphInfo(graphsList, graphNames, torpTotal, getString(R.string.encyclopedia_torps));
        setGraphInfo(graphsList, graphNames, antiAirTotal, getString(R.string.encyclopedia_aa_def));
        setGraphInfo(graphsList, graphNames, aircraftTotal, getString(R.string.encyclopedia_aircraft));
        setGraphInfo(graphsList, graphNames, mobilityTotal, getString(R.string.encyclopedia_mobility));
        setGraphInfo(graphsList, graphNames, concealmentTotal, getString(R.string.encyclopedia_concealment));

        setGraphInfo(graphsList, graphNames, health, getString(R.string.encyclopedia_survivability));

        setGraphInfo(graphsList, graphNames, survivalHealth, getString(R.string.encyclopedia_health));

        setGraphInfo(graphsList, graphNames, concealmentDistanceShip, getString(R.string.encyclopedia_spotted_range));
        setGraphInfo(graphsList, graphNames, concealmentDistancePlane, getString(R.string.encyclopedia_aircraft_spotted_range));

        setGraphInfo(graphsList, graphNames, turningRadius, getString(R.string.encyclopedia_turning_radius));

        setGraphInfo(graphsList, graphNames, speed, getString(R.string.encyclopedia_speed));
        setGraphInfo(graphsList, graphNames, rudderTime, getString(R.string.encyclopedia_rudder_time));

        setGraphInfo(graphsList, graphNames, artiDistance, getString(R.string.encyclopedia_gun_range));
        setGraphInfo(graphsList, graphNames, secondaryRange, getString(R.string.encyclopedia_secondary_range));

        setGraphInfo(graphsList, graphNames, artiAPDmg, getString(R.string.ap_damage));
        setGraphInfo(graphsList, graphNames, artiHEDmg, getString(R.string.he_damage));
        setGraphInfo(graphsList, graphNames, artiHEBurnProb, getString(R.string.he_burn_chance));
        setGraphInfo(graphsList, graphNames, artiRotation, getString(R.string.gun_rotation_speed));
        setGraphInfo(graphsList, graphNames, artiMaxDispersion, getString(R.string.gun_dispersion));
        setGraphInfo(graphsList, graphNames, artiTurrets, getString(R.string.num_of_turrets));
        setGraphInfo(graphsList, graphNames, artiBarrels, getString(R.string.encyclopedia_num_guns));
        setGraphInfo(graphsList, graphNames, artiGunRate, getString(R.string.encyclopedia_arty_fr));

        setGraphInfo(graphsList, graphNames, overallMin, getString(R.string.minimum_armor));
        setGraphInfo(graphsList, graphNames, overallMax, getString(R.string.maximum_armor));
        setGraphInfo(graphsList, graphNames, deckMin, getString(R.string.deck_minimum_armor));
        setGraphInfo(graphsList, graphNames, deckMax, getString(R.string.deck_maximum_armor));
        setGraphInfo(graphsList, graphNames, extremitiesMin, getString(R.string.extremities_min_armor));
        setGraphInfo(graphsList, graphNames, extremitiesMax, getString(R.string.extremities_max_armor));
        setGraphInfo(graphsList, graphNames, casemateMin, getString(R.string.casemate_min_armor));
        setGraphInfo(graphsList, graphNames, casemateMax, getString(R.string.casemate_max_armor));
        setGraphInfo(graphsList, graphNames, citadelMin, getString(R.string.citadel_min_armor));
        setGraphInfo(graphsList, graphNames, citadelMax, getString(R.string.citadel_max_armor));

        setGraphInfo(graphsList, graphNames, torpDistance, getString(R.string.encyclopedia_torp_range));
        setGraphInfo(graphsList, graphNames, torpReload, getString(R.string.encyclopedia_torp_fr));
        setGraphInfo(graphsList, graphNames, torpMaxDamage, getString(R.string.encyclopedia_torp_damage));
        setGraphInfo(graphsList, graphNames, torpSpeed, getString(R.string.encyclopedia_torp_speed));
        setGraphInfo(graphsList, graphNames, torpSlots, getString(R.string.num_of_torp_turret));
        setGraphInfo(graphsList, graphNames, torpBarrels, getString(R.string.encyclopedia_num_torps));
        setGraphInfo(graphsList, graphNames, torpGuns, getString(R.string.encyclopedia_num_torps));

        setGraphInfo(graphsList, graphNames, planeAmounts, getString(R.string.encyclopedia_num_planes));

        setGraphInfo(graphsList, graphNames, fighterSquadrons, getString(R.string.fighter_squadrons));
        setGraphInfo(graphsList, graphNames, bomberSquadrons, getString(R.string.bomber_squadron));
        setGraphInfo(graphsList, graphNames, torpedoSquadrons, getString(R.string.torpedo_squadrons));

        setGraphInfo(graphsList, graphNames, torpBDistance, getString(R.string.encyclopedia_torp_range));
        setGraphInfo(graphsList, graphNames, torpBprepareTime, getString(R.string.encyclopedia_tb_prep_time));
        setGraphInfo(graphsList, graphNames, torpBDamage, getString(R.string.encyclopedia_tb_torp_dam));
        setGraphInfo(graphsList, graphNames, torpBMaxSpeed, getString(R.string.encyclopedia_tb_torp_speed));

        setGraphInfo(graphsList, graphNames, diveBPrepareTime, getString(R.string.encyclopedia_db_prep_time));
        setGraphInfo(graphsList, graphNames, diveBMaxDmg, getString(R.string.encyclopedia_db_damage));
        setGraphInfo(graphsList, graphNames, diveBBurnProbably, getString(R.string.db_burn_chance));

        setGraphInfo(graphsList,graphNames, warshipStatsDmg, getString(R.string.damage));
        setGraphInfo(graphsList,graphNames, warshipStatsWR, getString(R.string.win_rate));
        setGraphInfo(graphsList,graphNames, warshipStatsKills, getString(R.string.kills_game));
        setGraphInfo(graphsList,graphNames, warshipStatsPlanes, getString(R.string.planes_downed_game));

        if(adapter == null)
            adapter = new ShipsCompareAdapter();

        adapter.setShips(graphsList);
        adapter.setGraphNames(graphNames);
        adapter.setShipColors(shipColors);

        if(recyclerView.getAdapter() == null)
            recyclerView.setAdapter(adapter);
    }

    private void setGraphInfo(List<Map<String, Float>> graphs, List<String> graphNames, Map<String, Float> graphInfo, String title){
        if(graphInfo.size() > 0) {
            graphs.add(graphInfo);
            graphNames.add(title);
        }
    }

    private void addGraphInfo(Map<String, Float> map, String shipName, Float value){
        if(value != 0.0f){
            map.put(shipName, value);
        }
    }

    @Subscribe
    public void onRefresh(Long shipId){
        initView();
    }
}