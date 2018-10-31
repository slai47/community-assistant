package com.half.wowsca.ui.viewcaptain.tabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.half.wowsca.CAApp;
import com.half.wowsca.R;
import com.half.wowsca.interfaces.ICaptain;
import com.half.wowsca.model.Captain;
import com.half.wowsca.model.events.CaptainReceivedEvent;
import com.half.wowsca.model.events.ProgressEvent;
import com.half.wowsca.model.events.RefreshEvent;
import com.half.wowsca.model.events.ScrollToEvent;
import com.half.wowsca.model.events.SortingDoneEvent;
import com.half.wowsca.ui.CAFragment;
import com.half.wowsca.ui.adapter.ShipsAdapter;
import org.greenrobot.eventbus.Subscribe;
import com.utilities.logging.Dlog;
import com.utilities.preferences.Prefs;

/**
 * Created by slai4 on 9/15/2015.
 */
public class CaptainShipsFragment extends CAFragment {

    public static final String SAVED_SORT = "savedSort";
    private Spinner sSorter;

    private EditText etSearch;
    private View delete;

    private RecyclerView recyclerView;
    private ShipsAdapter adapter;
    private GridLayoutManager layoutManager;

    private boolean spinnerCheck;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_captain_ships, container, false);
        bindView(view);
        return view;
    }

    private void bindView(View view) {
        sSorter = (Spinner) view.findViewById(R.id.ships_spinner);
        etSearch = (EditText) view.findViewById(R.id.ships_search);
        delete = view.findViewById(R.id.ships_delete);
        recyclerView = (RecyclerView) view.findViewById(R.id.ships_list);

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
        if (captain != null && captain.getShips() != null) {
            refreshing(false);
            sSorter.setEnabled(true);
            etSearch.setEnabled(true);

            if(recyclerView.getAdapter() == null) {
                recyclerView.setHasFixedSize(false);

                layoutManager = new GridLayoutManager(getContext(), getResources().getInteger(R.integer.ship_rows));
                layoutManager.setOrientation(GridLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(layoutManager);

                adapter = new ShipsAdapter(captain.getShips(), getContext());
                recyclerView.setAdapter(adapter);
            } else if (adapter != null){
                Prefs pref = new Prefs(recyclerView.getContext());
                adapter.notifyDataSetChanged();
            }

            if(recyclerView.getAdapter() != null && CAApp.getLastShipPos() != 0){
                recyclerView.scrollToPosition(CAApp.getLastShipPos());
            }
            setUpSearching();
            setUpSorting();
        } else {
            if(captain != null && captain.getShips() == null)
                refreshing(true);
            sSorter.setEnabled(false);
            etSearch.setEnabled(false);
        }
    }

    private void setUpSorting() {
        if (sSorter.getAdapter() == null) {
            spinnerCheck = false;
            ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.ship_sorting, R.layout.ca_spinner_item_trans);
            sortAdapter.setDropDownViewResource(!CAApp.isDarkTheme(sSorter.getContext()) ? R.layout.ca_spinner_item : R.layout.ca_spinner_item_dark);
            sSorter.setAdapter(sortAdapter);
            refreshSortingChoice(true);
            sSorter.setEnabled(true);
            sSorter.setEnabled(true);
            sSorter.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    spinnerCheck = true;
                    return false;
                }
            });

            sSorter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Dlog.wtf("OnItemSelected", "check = " + spinnerCheck + " pos = " + position);
                    if(!spinnerCheck){
                        return;
                    } else {
                        String sortType = (String) parent.getItemAtPosition(position);
                        Prefs prefs = new Prefs(getContext());
                        prefs.setString(SAVED_SORT, sortType);
                        try {
                            if (adapter == null)
                                adapter = (ShipsAdapter) recyclerView.getAdapter();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (adapter != null) {
                            adapter.sort(sortType);
                            CAApp.setLastShipPos(0);
                            sSorter.setEnabled(false);
                        } else {
                            Toast.makeText(getContext(), "Oops something went wrong. Refresh the view to fix.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

        } else {
            refreshSortingChoice(false);
        }
    }

    private void refreshSortingChoice(boolean clearCheck) {
        Prefs prefs = new Prefs(getContext());
        String savedSort = prefs.getString(SAVED_SORT, "");
        if (!TextUtils.isEmpty(savedSort)) {
            String[] sortTypes = getResources().getStringArray(R.array.ship_sorting);
            for (int i = 0; i < sortTypes.length; i++) {
                if (sortTypes[i].equals(savedSort)) {
                    if(clearCheck)
                        spinnerCheck = false; // because spinners suck
                    sSorter.setSelection(i);
                    break;
                }
            }
        }
    }

    private void setUpSearching() {
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
                    adapter.filter(s);
                }
                if (!TextUtils.isEmpty(etSearch.getText().toString())) {
                    sSorter.setEnabled(false);
                    delete.setVisibility(View.VISIBLE);
                } else {
                    sSorter.setEnabled(true);
                    delete.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        if (!TextUtils.isEmpty(etSearch.getText().toString())) {
            sSorter.setEnabled(false);
            delete.setVisibility(View.VISIBLE);
        } else {
            sSorter.setEnabled(true);
            delete.setVisibility(View.GONE);
        }
    }

    @Subscribe
    public void onReceive(CaptainReceivedEvent event) {
        initView();
    }

    @Subscribe
    public void onSortDone(SortingDoneEvent event) {
        sSorter.post(new Runnable() {
            @Override
            public void run() {
                sSorter.setEnabled(true);
            }
        });
    }

    @Subscribe
    public void onRefresh(RefreshEvent event) {
        refreshing(true);
        adapter = null;
        recyclerView.setAdapter(null);
        etSearch.setText("");
        sSorter.setAdapter(null);
    }

    @Subscribe
    public void onScrollEvent(ScrollToEvent event) {
        Dlog.wtf("Onscroll", "pos = " + event.getPosition());
        try {
            layoutManager.scrollToPositionWithOffset(event.getPosition(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void onProgressEvent(ProgressEvent event){
        if(mSwipeRefreshLayout != null){
            mSwipeRefreshLayout.setRefreshing(event.isRefreshing());
        }
    }
}