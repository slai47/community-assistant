package com.half.wowsca.ui.encyclopedia.tabs;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;



import com.half.wowsca.CAApp;
import com.half.wowsca.R;
import com.half.wowsca.alerts.Alert;
import com.half.wowsca.model.encyclopedia.holders.UpgradeHolder;
import com.half.wowsca.model.encyclopedia.items.EquipmentInfo;
import com.half.wowsca.model.events.UpgradeClickEvent;
import com.half.wowsca.ui.CAFragment;
import com.half.wowsca.ui.adapter.UpgradesAdapter;
import com.half.wowsca.ui.encyclopedia.ShipProfileActivity;
import org.greenrobot.eventbus.Subscribe;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by slai4 on 4/26/2016.
 */
public class UpgradesFragment extends CAFragment {

    RecyclerView recyclerView;
    GridLayoutManager layoutManager;
    UpgradesAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_recycler_view, container, false);
        recyclerView = (RecyclerView) view;
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        CAApp.getEventBus().register(this);
        UpgradeHolder holder = CAApp.getInfoManager().getUpgrades(getContext());
        if(holder != null && holder.getItems() != null && recyclerView.getAdapter() == null){
            layoutManager = new GridLayoutManager(getContext(), getResources().getInteger(R.integer.shipopedia_upgrade_grid));
            layoutManager.setOrientation(GridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);

            List<EquipmentInfo> upgrades = new ArrayList<>();
            upgrades.addAll(holder.getItems().values());

            Collections.sort(upgrades, new Comparator<EquipmentInfo>() {
                @Override
                public int compare(EquipmentInfo lhs, EquipmentInfo rhs) {
                    return lhs.getName().compareToIgnoreCase(rhs.getName());
                }
            });
            Collections.sort(upgrades, new Comparator<EquipmentInfo>() {
                @Override
                public int compare(EquipmentInfo lhs, EquipmentInfo rhs) {
                    return lhs.getPrice() - rhs.getPrice();
                }
            });

            adapter = new UpgradesAdapter(upgrades, getContext());
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        CAApp.getEventBus().unregister(this);
    }

    @Subscribe
    public void upgradeClicked(UpgradeClickEvent event){
        UpgradeHolder holder = CAApp.getInfoManager().getUpgrades(getContext());
        EquipmentInfo info = holder.get(event.getId());
        if (info != null) {
            DecimalFormat formatter = new DecimalFormat(ShipProfileActivity.PATTERN);
            Alert.createGeneralAlert(getActivity(), info.getName(), info.getDescription() + getString(R.string.encyclopedia_upgrade_cost) + formatter.format(info.getPrice()), getString(R.string.dismiss), R.drawable.ic_upgrade);
        } else {
            Toast.makeText(getContext(), R.string.resources_error, Toast.LENGTH_SHORT).show();
        }

    }



}
