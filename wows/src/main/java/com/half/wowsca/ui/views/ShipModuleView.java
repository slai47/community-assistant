package com.half.wowsca.ui.views;

import android.content.Context;
import androidx.gridlayout.widget.GridLayout;
import androidx.appcompat.widget.PopupMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.half.wowsca.CAApp;
import com.half.wowsca.R;
import com.half.wowsca.backend.GetShipEncyclopediaInfo;
import com.half.wowsca.managers.CompareManager;
import com.half.wowsca.managers.InfoManager;
import com.half.wowsca.model.encyclopedia.items.ShipInfo;
import com.half.wowsca.model.encyclopedia.items.ShipModuleItem;
import com.half.wowsca.model.events.ProgressEvent;
import com.utilities.logging.Dlog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by slai47 on 5/21/2017.
 */

public class ShipModuleView extends LinearLayout {

    public static final String PATTERN = "###,###,###";

    private long shipID;

    private GridLayout gridView;

    private TextView tvTitle;

    private TextView tvState;

    public ShipModuleView(Context context) {
        super(context);
        addBaseViews(context);
        initView();
    }

    private void addBaseViews(Context context) {
        setPadding(20, 20, 20, 20);
        setOrientation(VERTICAL);
        GridView.LayoutParams params = new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tvTitle = new TextView(context);
        tvTitle.setLayoutParams(params);
        tvTitle.setPadding(10, 10, 10, 20);
        addView(tvTitle);
        gridView = new GridLayout(context);
        gridView.setLayoutParams(params);
        gridView.setColumnCount(2);
        addView(gridView);
        tvState = new TextView(context);
        tvState.setLayoutParams(params);
        tvState.setVisibility(View.GONE);
        addView(tvState);
    }

    public void initView() {
        // grab module list for the ship ID;
        ShipInfo info = new InfoManager().getShipInfo(getContext()).get(shipID);
        if(info != null) {
            tvTitle.setText(info.getName());
            // build out grid view
            createGrid();
        }
    }

    private void createGrid() {
        gridView.removeAllViews();
        Dlog.d("ShipModuleView", "createGrid");
        gridView.post(new Runnable() {
            @Override
            public void run() {
                ShipInfo shipInfo = CAApp.getInfoManager().getShipInfo(getContext()).get(shipID);
                Dlog.d("ShipModuleView", "items = " + shipInfo.getItems());

                Map<String, Long> moduleList = CompareManager.getModuleList().get(shipID);
                if(moduleList == null)
                    moduleList = new HashMap<String, Long>();
                buildDefaultModuleList(shipInfo, moduleList);

                Dlog.d("ShipModuleView", "moduleList = " + moduleList);
                CompareManager.getModuleList().put(shipID, moduleList);

                ShipModuleItem artillery = shipInfo.getItems().get(moduleList.get(GetShipEncyclopediaInfo.ARTILLERY));
                ShipModuleItem torps = shipInfo.getItems().get(moduleList.get(GetShipEncyclopediaInfo.TORPEDOES));
                ShipModuleItem fireControl = shipInfo.getItems().get(moduleList.get(GetShipEncyclopediaInfo.FIRE_CONTROL));
                ShipModuleItem flightControl = shipInfo.getItems().get(moduleList.get(GetShipEncyclopediaInfo.FLIGHT_CONTROL));
                ShipModuleItem hull = shipInfo.getItems().get(moduleList.get(GetShipEncyclopediaInfo.HULL));
                ShipModuleItem engine = shipInfo.getItems().get(moduleList.get(GetShipEncyclopediaInfo.ENGINE));
                ShipModuleItem fighter = shipInfo.getItems().get(moduleList.get(GetShipEncyclopediaInfo.FIGHTER));
                ShipModuleItem diveBomber = shipInfo.getItems().get(moduleList.get(GetShipEncyclopediaInfo.DIVE_BOMBER));
                ShipModuleItem torpBomber = shipInfo.getItems().get(moduleList.get(GetShipEncyclopediaInfo.TORPEDO_BOMBER));

                // Add needed items to list
                List<Boolean> hasOptions = new ArrayList<>();
                List<ShipModuleItem> items = new ArrayList<>();

                addNecessaryModules(shipInfo, artillery, torps, fireControl, flightControl, hull, engine, fighter, diveBomber, torpBomber, hasOptions, items);

                buildModuleLists(hasOptions, items);
            }

            private void addNecessaryModules(ShipInfo shipInfo, ShipModuleItem artillery, ShipModuleItem torps, ShipModuleItem fireControl, ShipModuleItem flightControl, ShipModuleItem hull, ShipModuleItem engine, ShipModuleItem fighter, ShipModuleItem diveBomber, ShipModuleItem torpBomber, List<Boolean> hasOptions, List<ShipModuleItem> items) {
                if(artillery != null) {
                    items.add(artillery);
                    hasOptions.add(shipInfo.getArtillery().size() > 1);
                }
                if(torps != null) {
                    items.add(torps);
                    hasOptions.add(shipInfo.getTorps().size() > 1);
                }
                if(fireControl != null) {
                    items.add(fireControl);
                    hasOptions.add(shipInfo.getFireControl().size() > 1);
                }
                if(flightControl != null) {
                    items.add(flightControl);
                    hasOptions.add(shipInfo.getFlightControl().size() > 1);
                }
                if(hull != null) {
                    items.add(hull);
                    hasOptions.add(shipInfo.getHull().size() > 1);
                }
                if(engine != null) {
                    items.add(engine);
                    hasOptions.add(shipInfo.getEngine().size() > 1);
                }
                if(fighter != null) {
                    items.add(fighter);
                    hasOptions.add(shipInfo.getFighter().size() > 1);
                }
                if(diveBomber != null) {
                    items.add(diveBomber);
                    hasOptions.add(shipInfo.getDiveBomber().size() > 1);
                }
                if(torpBomber != null) {
                    items.add(torpBomber);
                    hasOptions.add(shipInfo.getTorpBomb().size() > 1);
                }
            }

            private void buildDefaultModuleList(ShipInfo shipInfo, Map<String, Long> moduleList) {
                if(shipInfo != null && moduleList.isEmpty()){
                    moduleList.put(GetShipEncyclopediaInfo.ARTILLERY, getBaseModuleToList(shipInfo, shipInfo.getArtillery()));
                    moduleList.put(GetShipEncyclopediaInfo.TORPEDOES, getBaseModuleToList(shipInfo, shipInfo.getTorps()));
                    moduleList.put(GetShipEncyclopediaInfo.FIRE_CONTROL, getBaseModuleToList(shipInfo, shipInfo.getFireControl()));
                    moduleList.put(GetShipEncyclopediaInfo.FLIGHT_CONTROL, getBaseModuleToList(shipInfo, shipInfo.getFlightControl()));
                    moduleList.put(GetShipEncyclopediaInfo.HULL, getBaseModuleToList(shipInfo, shipInfo.getHull()));
                    moduleList.put(GetShipEncyclopediaInfo.ENGINE, getBaseModuleToList(shipInfo, shipInfo.getEngine()));
                    moduleList.put(GetShipEncyclopediaInfo.FIGHTER, getBaseModuleToList(shipInfo, shipInfo.getFighter()));
                    moduleList.put(GetShipEncyclopediaInfo.DIVE_BOMBER, getBaseModuleToList(shipInfo, shipInfo.getDiveBomber()));
                    moduleList.put(GetShipEncyclopediaInfo.TORPEDO_BOMBER, getBaseModuleToList(shipInfo, shipInfo.getTorpBomb()));
                }
            }

            private long getBaseModuleToList(ShipInfo info, List<Long> modules){
                if(modules != null && modules.size() > 0) {
                    for (int i = 0; i < modules.size(); i++) {
                        ShipModuleItem item = info.getItems().get(modules.get(i));
                        if (item.isDefault()) {
                            return item.getId();
                        }
                    }
                    return modules.get(0);
                } else
                    return 0;
            }

            private void cleanModuleTitle(TextView tv, String title){
                switch (title){
                    case "Suo":
                        title = getContext().getString(R.string.fire_control);
                        break;
                    case "FlightControl":
                        title = getContext().getString(R.string.flight_control);
                        break;
                    case "TorpedoBomber":
                        title = getContext().getString(R.string.torpedo_bomber);
                        break;
                    case "DiveBomber":
                        title = getContext().getString(R.string.dive_bomber);
                        break;
                }
                tv.setText(title);
            }

            private boolean buildModuleLists(List<Boolean> hasOptions, List<ShipModuleItem> items) {
                boolean hasAnOption = false;
                for(int i = 0; i < items.size(); i++){
                    View convertView = LayoutInflater.from(gridView.getContext()).inflate(R.layout.list_ship_module, gridView, false);
                    GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                    params.columnSpec = GridLayout.spec(i % 2, 1, 1);
                    params.setMargins(5,5,5,5);
                    convertView.setLayoutParams(params);

                    TextView tv = (TextView) convertView.findViewById(R.id.list_module_top);
                    TextView tvText = (TextView) convertView.findViewById(R.id.list_module_text);

                    ShipModuleItem item = items.get(i);

                    boolean hasOpt = hasOptions.get(i);

                    if(hasOpt){
                        hasAnOption = true;
                        convertView.setBackgroundResource(R.drawable.encyclopedia_module_white);
                    } else {
                        convertView.setBackgroundResource(R.drawable.compare_normal_grid);
                    }

                    StringBuilder sb = new StringBuilder();
                    sb.append(item.getName());

                    tvText.setText(sb.toString());

                    cleanModuleTitle(tv, item.getType());

                    convertView.setTag(item.getId());

                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Long id = (Long) v.getTag();
                            if(id != null){
                                final ShipInfo shipInfo = CAApp.getInfoManager().getShipInfo(gridView.getContext()).get(shipID);
                                ShipModuleItem item = shipInfo.getItems().get(id);
                                List<Long> typeIds = new ArrayList<Long>();
                                switch (item.getType()){
                                    case "Suo":
                                        typeIds = shipInfo.getFireControl();
                                        break;
                                    case "FlightControl":
                                        typeIds = shipInfo.getFlightControl();
                                        break;
                                    case "DiveBomber":
                                        typeIds = shipInfo.getDiveBomber();
                                        break;
                                    case "Fighter":
                                        typeIds = shipInfo.getFighter();
                                        break;
                                    case "Artillery":
                                        typeIds = shipInfo.getArtillery();
                                        break;
                                    case "Hull":
                                        typeIds = shipInfo.getHull();
                                        break;
                                    case "TorpedoBomber":
                                        typeIds = shipInfo.getTorpBomb();
                                        break;
                                    case "Torpedoes":
                                        typeIds = shipInfo.getTorps();
                                        break;
                                    case "Engine":
                                        typeIds = shipInfo.getEngine();
                                        break;
                                }
                                Collections.sort(typeIds, new Comparator<Long>() {
                                    @Override
                                    public int compare(Long lhs, Long rhs) {
                                        ShipModuleItem lhsItem = shipInfo.getItems().get(lhs);
                                        ShipModuleItem rhsItem = shipInfo.getItems().get(rhs);
                                        return lhsItem.getName().compareToIgnoreCase(rhsItem.getName());
                                    }
                                });
                                if(typeIds.size() > 1) {
                                    PopupMenu menu = new PopupMenu(gridView.getContext(), v);
                                    menu.setGravity(Gravity.CENTER);
                                    final Map<Integer, ShipModuleItem> mapOfItems = new HashMap<Integer, ShipModuleItem>();
                                    Menu m = menu.getMenu();

                                    long current = (long) v.getTag();
                                    for (int i = 0; i < typeIds.size(); i++){
                                        ShipModuleItem it = shipInfo.getItems().get(typeIds.get(i));
                                        if(it != null && it.getId() != current) {
                                            m.add(0, i, 0, it.getName());
                                            mapOfItems.put(i, it);
                                        }
                                    }
                                    menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                        @Override
                                        public boolean onMenuItemClick(MenuItem item) {
                                            ShipModuleItem i = mapOfItems.get(item.getItemId());
                                            Map<String, Long> MODULE_LIST = CompareManager.getModuleList().get(shipID);
                                            if(MODULE_LIST == null)
                                                MODULE_LIST = new HashMap<String, Long>();
                                            Dlog.d("ShipModuleView", "id = " + i.getId());
                                            Dlog.d("ShipModuleView", "moduleListB = " + MODULE_LIST.toString());
                                            switch (i.getType()){
                                                case "Suo":
                                                    MODULE_LIST.put(GetShipEncyclopediaInfo.FIRE_CONTROL, i.getId());
                                                    break;
                                                case "FlightControl":
                                                    MODULE_LIST.put(GetShipEncyclopediaInfo.FLIGHT_CONTROL, i.getId());
                                                    break;
                                                case "DiveBomber":
                                                    MODULE_LIST.put(GetShipEncyclopediaInfo.DIVE_BOMBER, i.getId());
                                                    break;
                                                case "Fighter":
                                                    MODULE_LIST.put(GetShipEncyclopediaInfo.FIGHTER, i.getId());
                                                    break;
                                                case "Artillery":
                                                    MODULE_LIST.put(GetShipEncyclopediaInfo.ARTILLERY, i.getId());
                                                    break;
                                                case "Hull":
                                                    MODULE_LIST.put(GetShipEncyclopediaInfo.HULL, i.getId());
                                                    break;
                                                case "TorpedoBomber":
                                                    MODULE_LIST.put(GetShipEncyclopediaInfo.TORPEDO_BOMBER, i.getId());
                                                    break;
                                                case "Torpedoes":
                                                    MODULE_LIST.put(GetShipEncyclopediaInfo.TORPEDOES, i.getId());
                                                    break;
                                                case "Engine":
                                                    MODULE_LIST.put(GetShipEncyclopediaInfo.ENGINE, i.getId());
                                                    break;
                                            }
                                            Dlog.d("ShipModuleView", "moduleListA = " + MODULE_LIST.toString());
                                            CompareManager.getModuleList().put(shipID, MODULE_LIST);
                                            Dlog.d("ShipModuleView", "moduleListS = " + CompareManager.getModuleList().get(shipID).toString());
                                            //Update the screen
                                            CompareManager.GRABBING_INFO = true;
                                            CompareManager.searchShip(getContext(), shipID);
                                            CAApp.getEventBus().post(new ProgressEvent(true));
                                            return false;
                                        }
                                    });
                                    menu.show();
                                }

                            }
                        }
                    });

                    gridView.addView(convertView);
                }
                return hasAnOption;
            }
        });
    }

    public long getShipID() {
        return shipID;
    }

    public void setShipID(long shipID) {
        this.shipID = shipID;
    }

    public GridLayout getGridView() {
        return gridView;
    }

    public void setGridView(GridLayout gridView) {
        this.gridView = gridView;
    }

    public TextView getTvTitle() {
        return tvTitle;
    }

    public void setTvTitle(TextView tvTitle) {
        this.tvTitle = tvTitle;
    }

    public TextView getTvState() {
        return tvState;
    }

    public void setTvState(TextView tvState) {
        this.tvState = tvState;
    }
}
