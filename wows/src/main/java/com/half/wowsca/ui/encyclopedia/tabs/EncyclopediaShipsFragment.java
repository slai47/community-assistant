package com.half.wowsca.ui.encyclopedia.tabs;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.half.wowsca.CAApp;
import com.half.wowsca.R;
import com.half.wowsca.managers.CompareManager;
import com.half.wowsca.managers.InfoManager;
import com.half.wowsca.model.encyclopedia.holders.ShipsHolder;
import com.half.wowsca.model.encyclopedia.items.ShipInfo;
import com.half.wowsca.model.events.ShipCompareEvent;
import com.half.wowsca.ui.CAFragment;
import com.half.wowsca.ui.adapter.EncyclopediaAdapter;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by slai4 on 12/1/2015.
 */
public class EncyclopediaShipsFragment extends CAFragment {

    public static final String SEARCH = "search";
    public static final String TIER = "tier";
    public static final String NATION = "nation";
    private RecyclerView listView;
    private EncyclopediaAdapter adapter;
    private GridLayoutManager layoutManager;

    private View topArea;
    private EditText etSearch;
    private View delete;

    private Spinner sTier;
    private Spinner sNation;

    private String searchText;
    private int searchTier = EncyclopediaAdapter.EMPTY_FILTER;
    private int searchNation = EncyclopediaAdapter.EMPTY_FILTER;

    private TextView tvCompareText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_encyclopedia_list, container, false);
        bindView(view);
        if(savedInstanceState != null){
            searchText = savedInstanceState.getString(SEARCH);
            searchTier = savedInstanceState.getInt(TIER);
            if(searchTier == 0){
                searchTier = EncyclopediaAdapter.EMPTY_FILTER;
            }
            searchNation = savedInstanceState.getInt(NATION);
            if(searchNation == 0){
                searchNation = EncyclopediaAdapter.EMPTY_FILTER;
            }
        }
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SEARCH, etSearch.getText().toString());
        outState.putInt(NATION, searchNation);
        outState.putInt(TIER, searchTier);
    }

    private void bindView(View view) {
        topArea = view.findViewById(R.id.encyclopedia_list_top_area);

        listView = (RecyclerView) view.findViewById(R.id.encyclopedia_list_listview);
        etSearch = (EditText) view.findViewById(R.id.encyclopedia_list_et);
        delete = view.findViewById(R.id.encyclopedia_list_et_delete);

        sTier = (Spinner) view.findViewById(R.id.encyclopedia_list_tier_selector);
        sNation = (Spinner) view.findViewById(R.id.encyclopedia_list_nation_selector);

        tvCompareText = (TextView) view.findViewById(R.id.encyclopedia_list_compare_text);
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
        if (listView.getAdapter() == null) {
            try {
                List<ShipInfo> ships = new ArrayList<ShipInfo>();
                for (ShipInfo info : CAApp.getInfoManager().getShipInfo(listView.getContext()).getItems().values()) {
                    ships.add(info);
                }
                Collections.sort(ships, new Comparator<ShipInfo>() {
                    @Override
                    public int compare(ShipInfo lhs, ShipInfo rhs) {
                        return lhs.getName().compareToIgnoreCase(rhs.getName());
                    }
                });
                Collections.sort(ships, new Comparator<ShipInfo>() {
                    @Override
                    public int compare(ShipInfo lhs, ShipInfo rhs) {
                        return rhs.getTier() - lhs.getTier();
                    }
                });
                adapter = new EncyclopediaAdapter(ships, listView.getContext());

                listView.setHasFixedSize(true);

                layoutManager = new GridLayoutManager(listView.getContext(), getResources().getInteger(R.integer.shipopedia_grid));
                layoutManager.setOrientation(GridLayoutManager.VERTICAL);
                listView.setLayoutManager(layoutManager);

                listView.setAdapter(adapter);

                if (!TextUtils.isEmpty(etSearch.getText().toString())) {
                    filter();
                }
            } catch (Exception e) {
                Toast.makeText(getContext(), R.string.resources_error, Toast.LENGTH_SHORT).show();
            }
        } else {
            if (!TextUtils.isEmpty(etSearch.getText().toString())) {
                filter();
            }
        }
        setUpFiltering();

        setupCompareFeature();
    }

    private void setupCompareFeature() {
        if(CAApp.getTheme(listView.getContext()).equals("ocean")){
            tvCompareText.setBackgroundColor(ContextCompat.getColor(listView.getContext(), R.color.bottom_background));
        } else {
            tvCompareText.setBackgroundColor(ContextCompat.getColor(listView.getContext(), R.color.material_background_dark));
        }
        setCompareText();
    }

    private void setUpFiltering() {
        if(!TextUtils.isEmpty(searchText)){
            etSearch.setText(searchText);
        }
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etSearch.setText("");
            }
        });
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapter != null) {
                    adapter.filter(s, searchNation, searchTier);
                }
                if (!TextUtils.isEmpty(etSearch.getText().toString())) {
                    delete.setVisibility(View.VISIBLE);
                } else {
                    delete.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        initNationSpinner();

        initTierSpinner();

        if (!TextUtils.isEmpty(etSearch.getText().toString())) {
            delete.setVisibility(View.VISIBLE);
        } else {
            delete.setVisibility(View.GONE);
        }
    }

    private void initNationSpinner() {
        List<String> nationList = new ArrayList<>();
        nationList.add(getString(R.string.encyclopedia_all));
        for(String str : getResources().getStringArray(R.array.search_nation)){
            nationList.add(str);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.ca_spinner_item_trans, nationList);
        adapter.setDropDownViewResource(!CAApp.isDarkTheme(sNation.getContext()) ? R.layout.ca_spinner_item : R.layout.ca_spinner_item_dark);
        sNation.setAdapter(adapter);
        if(searchNation > EncyclopediaAdapter.EMPTY_FILTER){
            sNation.setSelection(searchNation + 1);
        } else {
            searchNation = EncyclopediaAdapter.EMPTY_FILTER;
        }
        sNation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                searchNation = position - 1;
                filter();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void initTierSpinner() {
        List<String> tiersList = new ArrayList<>();
        tiersList.add(getString(R.string.encyclopedia_all));
        for(int i = 1; i < 11; i++){
            tiersList.add(i + "");
        }
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getContext(), R.layout.ca_spinner_item_trans, tiersList);
        adapter2.setDropDownViewResource(!CAApp.isDarkTheme(sTier.getContext()) ? R.layout.ca_spinner_item : R.layout.ca_spinner_item_dark);
        sTier.setAdapter(adapter2);
        if(searchTier > EncyclopediaAdapter.EMPTY_FILTER){
            sTier.setSelection(searchTier + 1);
        } else {
            searchTier = EncyclopediaAdapter.EMPTY_FILTER;
        }
        sTier.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                searchTier = position - 1;
                filter();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void filter(){
        if(adapter != null){
            adapter.filter(etSearch.getText().toString(), searchNation, searchTier);
        }
    }

    @Subscribe
    public void onShipCompare(ShipCompareEvent event){
        setCompareText();
        adapter.notifyDataSetChanged();
    }

    private void setCompareText() {
        ShipsHolder holder = new InfoManager().getShipInfo(tvCompareText.getContext());
        int size = CompareManager.getSHIPS().size();
        if(size > 0) {
            StringBuilder sb = new StringBuilder();
            int i = 0;
            for (long id : CompareManager.getSHIPS()) {
                ShipInfo info = holder.get(id);
                if (info != null) {
                    sb.append(info.getName() + (i + 1 < size ? ", " : ""));
                }
                i++;
            }
            tvCompareText.setText(sb.toString());
        } else {
            tvCompareText.setText(R.string.long_click_to_add_ship_to_compare_list);
        }
    }
}