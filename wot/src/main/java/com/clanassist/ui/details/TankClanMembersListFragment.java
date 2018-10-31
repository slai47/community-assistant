package com.clanassist.ui.details;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.clanassist.CAApp;
import com.clanassist.SVault;
import com.clanassist.model.clan.Clan;
import com.clanassist.model.infoobj.VehicleWN8;
import com.clanassist.model.events.ClanLoadedFinishedEvent;
import com.clanassist.model.events.details.ClanProfileHit;
import com.clanassist.model.events.details.PlayerClanAdvancedHit;
import com.clanassist.model.events.details.PlayerClearEvent;
import com.clanassist.model.events.details.SortingFinishedEvent;
import com.clanassist.model.interfaces.IDetails;
import com.clanassist.model.interfaces.IListSort;
import com.clanassist.model.player.Player;
import com.clanassist.model.player.PlayerVehicleInfo;
import com.clanassist.model.statistics.Statistics;
import com.clanassist.tools.CPStorageManager;
import com.clanassist.tools.DownloadedClanManager;
import com.clanassist.tools.HitManager;
import com.clanassist.tools.WN8ColorManager;
import com.clanassist.ui.adapter.ClanMemberAdapter;
import com.clanassist.ui.adapter.PlayerTankAdapter;
import com.cp.assist.R;
import com.squareup.otto.Subscribe;
import com.utilities.Utils;
import com.utilities.preferences.Prefs;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Harrison on 8/15/2014.
 */
public class TankClanMembersListFragment extends ListFragment {

    private EditText etFilter;
    private ImageView ivClearFilter;

    private View topPart;

    private View sortingArea;

    private TextView tvProgress;

    private Spinner sortSpinner;

    private int spinnerCheck;

    private IDetails details;

    private ClanMemberAdapter clanAdapter;
    private PlayerTankAdapter tankAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details_tanks_members, container, false);
        bindView(view);
        return view;
    }

    private void bindView(View view) {
        sortSpinner = (Spinner) view.findViewById(R.id.fragment_details_tanks_members_spinner);
        topPart = view.findViewById(R.id.fragment_details_tanks_members_top_part);
        sortingArea = view.findViewById(R.id.fragment_details_tanks_members_sorting_area);

        etFilter = (EditText) view.findViewById(R.id.fragment_details_tanks_members_get_info_edit);
        ivClearFilter = (ImageView) view.findViewById(R.id.fragment_details_tanks_members_get_info_edit_clear);

    }

    @Override
    public void onResume() {
        super.onResume();
        CAApp.getEventBus().register(this);
        initView(true);
    }

    private void initView(boolean forceUpdate) {
        if (details == null)
            details = (IDetails) getActivity().getSupportFragmentManager().findFragmentById(R.id.container);
        Clan c = null;
        String sortType = details.getSortType();
        Prefs pref = new Prefs(sortSpinner.getContext());
        boolean isColorBlind = pref.getBoolean(SVault.PREF_COLORBLIND, false);
        if (details.isPlayer()) {
            setUpPlayerAdapter(sortType, isColorBlind);
        } else if (details.hitProfile() && !details.isPlayer()) {
            c = details.getClan(getActivity());
            setUpClanAdapter(c, sortType, isColorBlind, forceUpdate);
        }
        setUpFiltering();
        setUpSortingSpinner(forceUpdate, c, sortType);
    }

    private void setUpFiltering() {
        etFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (tankAdapter != null) {
                    tankAdapter.getFilter().filter(s);
                }
                if (clanAdapter != null) {
                    clanAdapter.getFilter().filter(s);
                }

                if (!TextUtils.isEmpty(etFilter.getText().toString())) {
                    sortSpinner.setEnabled(false);
                } else {
                    sortSpinner.setEnabled(true);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        etFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(etFilter.getText().toString().trim())) {
                    ivClearFilter.setVisibility(View.GONE);
                } else {
                    ivClearFilter.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        ivClearFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etFilter.setText("");
            }
        });
        if (!TextUtils.isEmpty(etFilter.getText().toString())) {
            sortSpinner.setEnabled(false);
        } else {
            sortSpinner.setEnabled(true);
        }

        if (CAApp.isLightTheme(etFilter.getContext()))
            etFilter.setTextColor(getResources().getColor(R.color.material_light_text_secondary));
    }

    private void setUpSortingSpinner(boolean forceUpdate, Clan c, String sortType) {
        if (!forceUpdate)
            if (c != null && sortSpinner.getAdapter() != null) {
                if (DownloadedClanManager.hasClanDownloadLoaded(getActivity(), c.getClanId() + "") && sortSpinner.getAdapter().getCount() < 3) {// reset the sort spinner if it wasn't updated when the clan was downloaded.
                    forceUpdate = true;
                }
            }

        if (sortSpinner.getAdapter() == null || forceUpdate) {
            int sortingArray = R.array.advanced_sorting;
            if (!details.isPlayer()) {
                sortingArray = R.array.clan_basic_sorting;
                if (c != null) {
                    if (c.getMembers() != null && !c.getMembers().isEmpty()) {
                        sortingArray = R.array.clan_sorting;
                    }
                }
            }
            int layoutId = android.R.layout.simple_spinner_item;
            if (CAApp.isLightTheme(sortSpinner.getContext()))
                layoutId = android.R.layout.simple_spinner_dropdown_item;

            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), sortingArray, layoutId);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sortSpinner.setAdapter(adapter);
            if (!TextUtils.isEmpty(sortType)) {
                String[] sortTypes = getResources().getStringArray(sortingArray);
                for (int i = 0; i < sortTypes.length; i++) {
                    if (sortTypes[i].equals(sortType)) {
                        spinnerCheck = -1; // because spinners suck
                        sortSpinner.setSelection(i);
                        break;
                    }
                }
            }
            sortSpinner.setEnabled(true);
            sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    spinnerCheck++;
                    if (spinnerCheck > 1) {
                        String sortType = (String) parent.getItemAtPosition(position);
                        details.setSortType(sortType);
                        IListSort sort = null;
                        if (tankAdapter != null)
                            sort = tankAdapter;
                        if (clanAdapter != null)
                            sort = clanAdapter;
                        if (sort != null) {
                            try {
                                sort.sort(sortType);
                                sortSpinner.setEnabled(false);
                            } catch (Exception e) {
                                initView(false);
                                Toast.makeText(view.getContext(), R.string.error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }
    }

    private void setUpPlayerAdapter(String sortType, boolean isColorBlind) {
        Player p = details.getPlayer(getActivity());
        if (p != null) {
            if (tankAdapter == null) {
                tankAdapter = new PlayerTankAdapter(getActivity(), R.layout.list_clan_tank, p.getPlayerVehicleInfoList());
                tankAdapter.setColorBlind(isColorBlind);
                setListAdapter(tankAdapter);
                tankAdapter.sort(sortType);
            } else {
                tankAdapter.setObjects(p.getPlayerVehicleInfoList());
                tankAdapter.sort(sortType);
            }
            getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    PlayerTankAdapter adapter = (PlayerTankAdapter) getListAdapter();
                    if (adapter != null) {
                        PlayerVehicleInfo info = adapter.getItem(position);
                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_tank_difference, null, false);
                        VehicleWN8 wn8 = CAApp.getInfoManager().getWN8Data(view.getContext()).getWN8(info.getTankId());

                        Prefs pref = new Prefs(view.getContext());
                        boolean isColorBlind = pref.getBoolean(SVault.PREF_COLORBLIND, false);
                        Statistics overall = info.getOverallStats();
                        if (wn8 != null && overall != null) {
                            TextView wins = (TextView) v.findViewById(R.id.dialog_dif_wins);
                            TextView winsAvg = (TextView) v.findViewById(R.id.dialog_dif_wins_avg);
                            TextView winsDelta = (TextView) v.findViewById(R.id.dialog_dif_wins_delta);

                            TextView dmg = (TextView) v.findViewById(R.id.dialog_dif_dmg);
                            TextView dmgAvg = (TextView) v.findViewById(R.id.dialog_dif_dmg_avg);
                            TextView dmgDelta = (TextView) v.findViewById(R.id.dialog_dif_dmg_delta);

                            TextView def = (TextView) v.findViewById(R.id.dialog_dif_def);
                            TextView defAvg = (TextView) v.findViewById(R.id.dialog_dif_def_avg);
                            TextView defDelta = (TextView) v.findViewById(R.id.dialog_dif_def_delta);

                            TextView frag = (TextView) v.findViewById(R.id.dialog_dif_frag);
                            TextView fragAvg = (TextView) v.findViewById(R.id.dialog_dif_frag_avg);
                            TextView fragDelta = (TextView) v.findViewById(R.id.dialog_dif_frag_delta);

                            TextView spot = (TextView) v.findViewById(R.id.dialog_dif_spot);
                            TextView spotAvg = (TextView) v.findViewById(R.id.dialog_dif_spot_avg);
                            TextView spotDelta = (TextView) v.findViewById(R.id.dialog_dif_spot_delta);

                            DecimalFormat df = Utils.getOneDepthDecimalFormatter();
                            float sWins = ((float) overall.getWins() / (float) overall.getBattles()) * 100;
                            double winDelta = sWins - wn8.getWin();
                            wins.setText("" + df.format(sWins));
                            winsAvg.setText("" + df.format(wn8.getWin()));
                            winsDelta.setText("" + df.format(winDelta));

                            float sDmg = (float) (overall.getDamageDealt() / overall.getBattles());
                            double dmgsDelta = sDmg - wn8.getDmg();
                            dmg.setText("" + df.format(sDmg));
                            dmgAvg.setText("" + df.format(wn8.getDmg()));
                            dmgDelta.setText("" + df.format(dmgsDelta));

                            float sFrags = (float) overall.getFrags() / ((float) (overall.getBattles() - overall.getSurvivedBattles()));
                            double fragDetla = sFrags - wn8.getFrag();
                            frag.setText("" + df.format(sFrags));
                            fragAvg.setText("" + df.format(wn8.getFrag()));
                            fragDelta.setText("" + df.format(fragDetla));

                            float sDef = (float) overall.getDroppedCapture_points() / (float) overall.getBattles();
                            double defsDelta = sDef - wn8.getDef();
                            def.setText("" + df.format(sDef));
                            defAvg.setText("" + df.format(wn8.getDef()));
                            defDelta.setText("" + df.format(defsDelta));

                            float sSpot = (float) overall.getSpotted() / (float) overall.getBattles();
                            double spotsDelta = sSpot - wn8.getSpot();
                            spot.setText("" + df.format(sSpot));
                            spotAvg.setText("" + df.format(wn8.getSpot()));
                            spotDelta.setText("" + df.format(spotsDelta));

                            if (!isColorBlind) {
                                WN8ColorManager.setDifferenceColorCustom(winsDelta, winDelta);
                                WN8ColorManager.setDifferenceColorCustom(dmgDelta, dmgsDelta);
                                WN8ColorManager.setDifferenceColorCustom(fragDelta, fragDetla);
                                WN8ColorManager.setDifferenceColorCustom(defDelta, defsDelta);
                                WN8ColorManager.setDifferenceColorCustom(spotDelta, spotsDelta);
                            }

                            builder.setTitle(getString(R.string.dialog_title));
                            builder.setPositiveButton(R.string.dismiss, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                            builder.setView(v);
                            try {
                                builder.show();
                            } catch (Exception e) {
                            }
                        } else {
                            Toast.makeText(view.getContext(), getString(R.string.dialog_fail_text), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }

    private void setUpClanAdapter(Clan c, String sortType, boolean isColorBlind, boolean forceUpdate) {
        if (c != null && c.getMembers() != null && !c.getMembers().isEmpty()) {
            if (clanAdapter == null || forceUpdate) {
                clanAdapter = new ClanMemberAdapter(getActivity(), R.layout.list_clan_tank, c.getMembers());
                clanAdapter.setColorBlind(isColorBlind);
                clanAdapter.sort(sortType);
                setListAdapter(clanAdapter);
            } else {
                clanAdapter.setPlayers(c.getMembers());
                clanAdapter.sort(sortType);
                clanAdapter.notifyDataSetChanged();
            }
            getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long i) {
                    ClanMemberAdapter adapter = (ClanMemberAdapter) getListAdapter();
                    if (adapter != null) {
                        List<Player> players = adapter.getPlayers();
                        Player p = players.get(position);
                        int id = p.getId();
                        String name = p.getName();
                        Player player = new Player();
                        player.setId(id);
                        player.setName(name);
                        CPStorageManager.saveTempStoredPlayer(getActivity(), player);

                        HitManager.removePlayerProfileHit(player.getId());

                        DetailsTabbedFragment frag = new DetailsTabbedFragment();
                        frag.setFromSearch(true);
                        frag.setAccountId(id);
                        frag.setPlayer(true);
                        frag.setName(name);
                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                                .replace(R.id.container, frag).addToBackStack(name).commit();
                    }
                }
            });
        }
    }

    @Subscribe
    public void clanLoaded(ClanLoadedFinishedEvent event) {
        Clan c = getDetails().getClan(getActivity());
        if (c != null && event.getClanId().equals(c.getClanId() + "")) {
            sortSpinner.post(new Runnable() {
                @Override
                public void run() {
                    initView(true);
                }
            });
        }
    }

    @Subscribe
    public void sortFinished(SortingFinishedEvent event) {
        sortSpinner.post(new Runnable() {
            @Override
            public void run() {
                sortSpinner.setEnabled(true);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        CAApp.getEventBus().unregister(this);
    }

    @Subscribe
    public void onPlayerClanReceive(ClanProfileHit result) {
        if (details.getClan(getActivity()) != null)
            initView(false);
    }

    @Subscribe
    public void onReceivedAdv(PlayerClanAdvancedHit result) {
        Player p = details.getPlayer(getActivity());
        if (p != null) {
            initView(false);
        }
    }

    @Subscribe
    public void onRefreshClearEvent(PlayerClearEvent event) {
        setListAdapter(null);
        tankAdapter = null;
    }

    public IDetails getDetails() {
        return details;
    }

    public void setDetails(IDetails details) {
        this.details = details;
    }
}
