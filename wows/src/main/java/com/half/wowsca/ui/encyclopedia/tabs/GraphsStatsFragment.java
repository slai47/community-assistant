package com.half.wowsca.ui.encyclopedia.tabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.half.wowsca.CAApp;
import com.half.wowsca.R;
import com.half.wowsca.model.encyclopedia.holders.WarshipsStats;
import com.half.wowsca.model.encyclopedia.items.ShipInfo;
import com.half.wowsca.model.encyclopedia.items.ShipStat;
import com.half.wowsca.model.enums.EncyclopediaType;
import com.half.wowsca.model.listModels.EncyclopediaChild;
import com.half.wowsca.ui.adapter.ExpandableStatsAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by slai4 on 12/1/2015.
 */
public class GraphsStatsFragment extends Fragment {

    public static final int PREMIUM_KEY = 47;
    ExpandableListView listView;
    ExpandableStatsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_encyclopedia_stats, container, false);
        bindView(view);
        return view;
    }

    private void bindView(View view) {
        listView = (ExpandableListView) view.findViewById(R.id.encyclopedia_stats_list);
    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void initView() {
        List<String> headers = new ArrayList<>();
        Map<String, List<EncyclopediaChild>> data = new HashMap<>();

        if(listView.getAdapter() == null) {
            Map<Integer, List<ShipInfo>> shipsInfos = getShipInfos();

            createTierInfo(10, shipsInfos.get(10), headers, data);
            createTierInfo(PREMIUM_KEY, shipsInfos.get(PREMIUM_KEY), headers, data);
            createTierInfo(9, shipsInfos.get(9), headers, data);
            createTierInfo(8, shipsInfos.get(8), headers, data);
            createTierInfo(7, shipsInfos.get(7), headers, data);
            createTierInfo(6, shipsInfos.get(6), headers, data);
            createTierInfo(5, shipsInfos.get(5), headers, data);
            createTierInfo(4, shipsInfos.get(4), headers, data);
            createTierInfo(3, shipsInfos.get(3), headers, data);
            createTierInfo(2, shipsInfos.get(2), headers, data);
            createTierInfo(1, shipsInfos.get(1), headers, data);

            adapter = new ExpandableStatsAdapter(headers, data, getContext());
            listView.setAdapter(adapter);
        } else {
        }
    }

    private void createTierInfo(int tier, List<ShipInfo> ships, List<String> headers, Map<String, List<EncyclopediaChild>> data){
        if(ships != null && !ships.isEmpty()) {
            List<EncyclopediaChild> children = new ArrayList<>();

            List<String> shipsTitles = new ArrayList<>();
            List<Float> damages = new ArrayList<>();
            List<Float> winRate = new ArrayList<>();
            List<Float> kills = new ArrayList<>();
            List<Float> planesDropped = new ArrayList<>();
            List<String> types = new ArrayList<>();

            WarshipsStats stats = CAApp.getInfoManager().getShipStats(listView.getContext());

            Collections.sort(ships, new Comparator<ShipInfo>() {
                @Override
                public int compare(ShipInfo lhs, ShipInfo rhs) {
                    return rhs.getName().compareToIgnoreCase(lhs.getName());
                }
            });

            if (tier != PREMIUM_KEY) {
                Collections.sort(ships, new Comparator<ShipInfo>() {
                    @Override
                    public int compare(ShipInfo lhs, ShipInfo rhs) {
                        return lhs.getType().compareToIgnoreCase(rhs.getType());
                    }
                });
            } else {
                Collections.sort(ships, new Comparator<ShipInfo>() {
                    @Override
                    public int compare(ShipInfo lhs, ShipInfo rhs) {
                        return lhs.getTier() - rhs.getTier();
                    }
                });
            }

            for (ShipInfo info : ships) {
                ShipStat stat = stats.get(info.getShipId());
                if (stat != null) {
                    shipsTitles.add(info.getName());
                    damages.add(stat.getDmg_dlt());
                    winRate.add(stat.getWins());
                    kills.add(stat.getFrags());
                    planesDropped.add(stat.getPls_kd());
                    types.add(info.getType());
                }
            }
//            Dlog.wtf("Tier " + tier, "kills = " + kills.toString());

            children.add(EncyclopediaChild.create(EncyclopediaType.LARGE_NUMBER, damages, shipsTitles, types, getString(R.string.encyclopedia_average_damage)));
            children.add(EncyclopediaChild.create(EncyclopediaType.PERCENT, winRate, shipsTitles, types, getString(R.string.encyclopedia_winrate)));
            children.add(EncyclopediaChild.create(EncyclopediaType.NONE, kills, shipsTitles, types, getString(R.string.encyclopedia_kills)));

            children.add(EncyclopediaChild.create(EncyclopediaType.NONE, planesDropped, shipsTitles, types, getString(R.string.planes_downed_encyclo)));

            String header = getString(R.string.encyclopedia_tier_start) + " " + tier + " " + getString(R.string.encyclopedia_tier_end);
            if (tier == PREMIUM_KEY) {
                header = getString(R.string.premium_ship_stats);
            }
            headers.add(header);
            data.put(header, children);
        }
    }

    private Map<Integer, List<ShipInfo>> getShipInfos(){
        Map<Integer, List<ShipInfo>> ships = new HashMap<>();
        ships.put(1, new ArrayList<ShipInfo>());
        ships.put(2, new ArrayList<ShipInfo>());
        ships.put(3, new ArrayList<ShipInfo>());
        ships.put(4, new ArrayList<ShipInfo>());
        ships.put(5, new ArrayList<ShipInfo>());
        ships.put(6, new ArrayList<ShipInfo>());
        ships.put(7, new ArrayList<ShipInfo>());
        ships.put(8, new ArrayList<ShipInfo>());
        ships.put(9, new ArrayList<ShipInfo>());
        ships.put(10, new ArrayList<ShipInfo>());
        ships.put(PREMIUM_KEY, new ArrayList<ShipInfo>());

        try {
            for (ShipInfo info : CAApp.getInfoManager().getShipInfo(listView.getContext()).getItems().values()) {
                ships.get(info.getTier()).add(info);
                if(info.isPremium())
                    ships.get(PREMIUM_KEY).add(info);
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), getString(R.string.resources_error), Toast.LENGTH_SHORT).show();
        }

        return ships;
    }
}
