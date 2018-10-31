package com.clanassist.ui.search;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.clanassist.CAApp;
import com.clanassist.backend.EncyclopediaParser;
import com.clanassist.model.infoobj.CombinedInfoObject;
import com.clanassist.model.infoobj.Tank;
import com.clanassist.model.infoobj.Tanks;
import com.clanassist.model.encyclopedia.Map;
import com.clanassist.model.enums.DrawerType;
import com.clanassist.model.events.details.TankSelectedEvent;
import com.clanassist.model.infoobj.WN8Data;
import com.clanassist.model.search.enums.EncyclopediaType;
import com.clanassist.model.search.queries.EncyclopediaQuery;
import com.clanassist.model.search.results.EncyclopediaResult;
import com.clanassist.ui.UIUtils;
import com.clanassist.ui.adapter.MapAdapter;
import com.clanassist.ui.adapter.VehicleStatsAdapter;
import com.clanassist.ui.details.clan.ClanQueryFragment;
import com.clanassist.ui.images.ImageFragment;
import com.cp.assist.R;
import com.squareup.otto.Subscribe;
import com.utilities.search.Search;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Harrison on 4/4/2015.
 */
public class EncyclopediaSearchFragment extends ListFragment implements AdapterView.OnItemClickListener {

    public static DrawerType TYPE;

    private EditText etSearchBox;
    private ImageView ivClear;
    private TextView tvNoResults;
    private ProgressBar pbProgressBar;
    private Button bSubmit;

    private VehicleStatsAdapter mStatsAdapter;
    private MapAdapter mMapAdapter;

    public static List<CombinedInfoObject> VEHICLE_WN8;

    public static EncyclopediaResult MAPS_RESULT;

    private int selectedId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, null, false);
        bindView(view);
        initView(view);
        return view;
    }

    private void bindView(View view) {
        etSearchBox = (EditText) view.findViewById(R.id.search_et_search);
        ivClear = (ImageView) view.findViewById(R.id.search_clear);
//        if(CAApp.isLightTheme(ivClear.getContext()))
//            ivClear.setImageResource(R.drawable.ic_cross_light);
//        else
//            ivClear.setImageResource(R.drawable.ic_cross);

        tvNoResults = (TextView) view.findViewById(R.id.search_no_results);
        pbProgressBar = (ProgressBar) view.findViewById(R.id.search_progress);
        bSubmit = (Button) view.findViewById(R.id.search_submit);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("filler", true);
    }

    @Override
    public void onResume() {
        super.onResume();
        CAApp.getEventBus().register(this);
        String title = getString(R.string.drawer_vehicle_stats);
        if (TYPE == DrawerType.MAPS)
            title = getString(R.string.drawer_maps);
        UIUtils.refreshActionBar(getActivity(), title);
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        getListView().setOnItemClickListener(this);
        if (TYPE == DrawerType.CHOOSE) {
            bSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CAApp.SELECTED_VEHICLE_ID = selectedId;
                    ClanQueryFragment.REFRESH = true;
                    closeKeyboard(getActivity());
                    getActivity().onBackPressed();
                    CAApp.getEventBus().post(new TankSelectedEvent());
                }
            });
            if (!CAApp.isLightTheme(ivClear.getContext())) {
                getView().setBackgroundResource(R.color.image_fragment_background);
            } else {
                getView().setBackgroundResource(R.color.image_fragment_background_light);
            }
            bSubmit.setVisibility(View.VISIBLE);
        } else {
            bSubmit.setVisibility(View.GONE);
        }
        if (TYPE == DrawerType.WN8 || TYPE == DrawerType.CHOOSE) {
            gotStats();
        }

        if (MAPS_RESULT == null && TYPE == DrawerType.MAPS) {
            Search<EncyclopediaQuery, EncyclopediaResult> search = new Search<EncyclopediaQuery, EncyclopediaResult>(new EncyclopediaParser(pbProgressBar.getContext()), null, CAApp.getEventBus());
            EncyclopediaQuery query = new EncyclopediaQuery();
            query.setType(EncyclopediaType.MAPS);
            query.setWebAddress(CAApp.getBaseAddress(etSearchBox.getContext()));
            query.setApplicationIdString(CAApp.getApplicationIdURLString(etSearchBox.getContext()));
            query.setLanguage(getString(R.string.language));
            search.execute(query);
            pbProgressBar.setVisibility(View.VISIBLE);
        } else {
            pbProgressBar.setVisibility(View.GONE);
        }
        if (CAApp.isLightTheme(getActivity())) {
            etSearchBox.setHintTextColor(Color.WHITE);
        }
    }

    private void initView(View view) {
        if (TYPE == DrawerType.WN8 || TYPE == DrawerType.CHOOSE) {
            etSearchBox.setImeOptions(EditorInfo.IME_ACTION_DONE);
            if (TYPE == DrawerType.WN8)
                etSearchBox.setHint(getString(R.string.search_fragment_wn_hint));
            else
                etSearchBox.setHint(getString(R.string.search_choose_hint));
            etSearchBox.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (mStatsAdapter == null) {
                        List<CombinedInfoObject> items = new ArrayList<CombinedInfoObject>();
                        items.addAll(getStats(getActivity()));
                        mStatsAdapter = new VehicleStatsAdapter(getActivity(), R.layout.list_vehicle_stat, items);
                        setListAdapter(mStatsAdapter);
                    }
                    mStatsAdapter.getFilter().filter(s);
//                    mStatsAdapter.notifyDataSetChanged();
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        } else if (TYPE == DrawerType.MAPS) {
            etSearchBox.setHint(getString(R.string.search_map_hint));
            etSearchBox.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (etSearchBox.getText().toString().length() > 0)
                        if (mMapAdapter == null) {

                        } else {
                            mMapAdapter.getFilter().filter(s);
                            mMapAdapter.notifyDataSetChanged();
                        }
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

        }
        if (TYPE != DrawerType.CHOOSE)
            bSubmit.setVisibility(View.GONE);
        else
            bSubmit.setVisibility(View.VISIBLE);
        etSearchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(etSearchBox.getText().toString().trim())) {
                    ivClear.setVisibility(View.GONE);
                } else {
                    ivClear.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        ivClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etSearchBox.setText("");
            }
        });
    }

    @Subscribe
    public void onMapReceive(EncyclopediaResult result) {
        if (result != null) {
            mMapAdapter = new MapAdapter(getActivity(), R.layout.list_map, result.getMaps());
            setListAdapter(mMapAdapter);
            pbProgressBar.setVisibility(View.GONE);
        }
    }

    private List<CombinedInfoObject> getStats(Context ctx) {
        if (VEHICLE_WN8 == null) {
            VEHICLE_WN8 = new ArrayList<CombinedInfoObject>();
            Tanks tanks = CAApp.getInfoManager().getTanks(ctx);
            WN8Data data = CAApp.getInfoManager().getWN8Data(ctx);
            if(tanks.getTanksList() != null) {
                for (Tank t : tanks.getTanksList().values()) {
                    CombinedInfoObject info = new CombinedInfoObject(t, data.getWN8(t.getId()));
                    VEHICLE_WN8.add(info);
                }
            }
        }
        return VEHICLE_WN8;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        try {
            if (TYPE == DrawerType.CHOOSE) {
                selectedId = (Integer) view.getTag();
                ((VehicleStatsAdapter) getListAdapter()).setSelectedId(selectedId);
//                Dlog.d("SearchFragment", "id = " + selectedId);
                return;
            } else if (TYPE == DrawerType.MAPS) {
                Map item = (Map) view.getTag();
                ImageFragment fragment = new ImageFragment();
                fragment.setUrl(item.getGridUrl());
                fragment.setDescription(item.getDescription());
                getActivity().getSupportFragmentManager().beginTransaction()
//                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
                        .add(R.id.container, fragment).addToBackStack("mapGrid").commit();
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), R.string.error, Toast.LENGTH_SHORT).show();
        }
    }

    public static void clear() {
        VEHICLE_WN8 = null;
        MAPS_RESULT = null;
    }

    private void gotStats() {
        if (mStatsAdapter == null) {
            mStatsAdapter = new VehicleStatsAdapter(getActivity(), R.layout.list_vehicle_stat, getStats(getActivity()));
        }
        setListAdapter(mStatsAdapter);
    }

    public void closeKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etSearchBox.getWindowToken(), 0);
    }

    @Override
    public void onPause() {
        super.onPause();
        CAApp.getEventBus().unregister(this);
    }

}
