package com.clanassist.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.clanassist.CAApp;
import com.clanassist.alerts.Alert;
import com.clanassist.backend.GlobalWarParser;
import com.clanassist.model.clan.Battle;
import com.clanassist.model.clan.Clan;
import com.clanassist.model.events.UpdateClanListEvent;
import com.clanassist.model.interfaces.IRefresh;
import com.clanassist.model.search.enums.GlobalWarSearchType;
import com.clanassist.model.search.queries.GlobalWarQuery;
import com.clanassist.model.search.results.GlobalWarResult;
import com.clanassist.tools.CPManager;
import com.clanassist.ui.adapter.BattleExpandableListAdapter;
import com.clanassist.ui.images.ImageFragment;
import com.cp.assist.R;
import com.squareup.otto.Subscribe;
import com.utilities.Utils;
import com.utilities.search.Search;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Created by Obsidian47 on 3/6/14.
 */
public class ActivityFragment extends Fragment implements IRefresh {

    public static final int TIME_DIFFERENCE = 15;
    private TextView etNoResults;
    private ProgressBar pbProgress;

    private TextView tvTopText;

    private BattleExpandableListAdapter mAdapter;

    private static ExpandableListView mListView;

    public static Calendar lastSearchTime;

    private static boolean hasShown = false;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity, null, false);
        etNoResults = (TextView) view.findViewById(R.id.activity_no_result_text);
        pbProgress = (ProgressBar) view.findViewById(R.id.activity_pb);
        mListView = (ExpandableListView) view.findViewById(R.id.activity_list);
        tvTopText = (TextView) view.findViewById(R.id.activity_top_text);
        setRetainInstance(true);

        return view;
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
        UIUtils.refreshActionBar(getActivity(), getString(R.string.drawer_battle_activity));
        if (lastSearchTime != null) {
            Calendar c = Calendar.getInstance();
            long lastTime = Utils.convertToMinutes(lastSearchTime.getTimeInMillis());
            long now = Utils.convertToMinutes(c.getTimeInMillis());
            if ((now - lastTime) > TIME_DIFFERENCE) {
                lastSearchTime = null;
                CPManager.ADAPTER_CLANS_LIST = null;
            }
        }
        if (CPManager.ADAPTER_CLANS_LIST == null || listHasChanged()) {
            //search
            CAApp.LAST_REFRESHED_TIME = null;
            initSearch(false);
        } else {
            mAdapter = new BattleExpandableListAdapter(getActivity(), CPManager.ADAPTER_CLANS_LIST);
            mListView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }
        setLastSearchedTime();
        initListeners();
    }

    private void initListeners() {
        mListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                if (mAdapter != null) {
                    Object obj = mAdapter.getChild(groupPosition, childPosition);
                    if (obj != null) {
                        Battle battle = (Battle) obj;
                        if (battle != null) {
                            try {
                                ImageFragment fragment = new ImageFragment();
                                fragment.setUrl(CAApp.getImageRepo(v.getContext()) + battle.getArenaName() + "_grid.jpg");
                                getActivity().getSupportFragmentManager().beginTransaction()
                                        .add(R.id.container, fragment).addToBackStack("cwGrid").commit();
                            } catch (Exception e) {
                            }
                        }
                    }
                }
                return false;
            }
        });

    }

    private boolean listHasChanged() {
        boolean hasChanged = false;
        if (CPManager.ADAPTER_CLANS_LIST.size() != CPManager.getSavedClans(getActivity()).size())
            hasChanged = true;
        else {
            for (Clan clan : CPManager.ADAPTER_CLANS_LIST) {
                if (!hasChanged && CPManager.getSavedClans(getActivity()).get(clan.getClanId()) == null) {
                    hasChanged = true;
                }
            }
        }
        return hasChanged;
    }

    @Override
    public void onPause() {
        super.onPause();
        CAApp.getEventBus().unregister(this);
    }

    private void initSearch(boolean fromRefresh) {
        boolean isConnected = Utils.hasInternetConnection(getActivity());
        if (isConnected) {
            boolean canUpdate = CAApp.canCLanUpdate(getActivity());
            Map<Integer, Clan> clans = CPManager.getSavedClans(getActivity());
            if (canUpdate) {
                if (clans.size() != 0) {
                    if (!CAApp.IS_SEARCHING)
                        initSearch(getActivity());
                    if (mAdapter != null) {
                        mAdapter.setClans(null);
                        mAdapter.notifyDataSetChanged();
                    }
                    pbProgress.setVisibility(View.VISIBLE);
                    mListView.setVisibility(View.VISIBLE);
                    setLastSearchedTime();
                    etNoResults.setText(R.string.battle_activity_no_clans_text);
                    etNoResults.setVisibility(View.GONE);
                } else {
                    etNoResults.setText(R.string.battle_activity_no_clans_text);
                    tvTopText.setText("");
                    etNoResults.setVisibility(View.VISIBLE);
                    pbProgress.setVisibility(View.GONE);
                    mListView.setVisibility(View.GONE);
                }
            } else {
                setLastSearchedTime();
                mListView.setVisibility(View.VISIBLE);
                pbProgress.setVisibility(View.GONE);
                etNoResults.setVisibility(View.GONE);
            }
        } else {
            CAApp.LAST_REFRESHED_TIME = null;
            etNoResults.setText(R.string.no_internet_title);
            tvTopText.setText("");
            etNoResults.setVisibility(View.VISIBLE);
            pbProgress.setVisibility(View.GONE);
            mListView.setVisibility(View.GONE);
            Context ctx = getActivity();
            if (!hasShown && fromRefresh) {
                Alert.generalNoInternetDialogAlert(getActivity(), ctx.getString(R.string.no_internet_title), ctx.getString(R.string.no_internet_message), ctx.getString(R.string.no_internet_neutral_text));
                hasShown = true;
            }
        }
    }

    private void initSearch(Context context) {
        tvTopText.setText("");
        lastSearchTime = null;
        Search<GlobalWarQuery, GlobalWarResult> search = new Search<GlobalWarQuery, GlobalWarResult>(new GlobalWarParser(), null, CAApp.getEventBus());
        Map<Integer, Clan> clans = CPManager.getSavedClans(context);
        GlobalWarQuery[] queries = new GlobalWarQuery[clans.size()];
        int i = 0;
        for (Clan c : clans.values()) {
            c.setBattles(null);
            GlobalWarQuery query = new GlobalWarQuery();
            query.setWebAddress(CAApp.getBaseAddress(context));
            query.setApplicationIdString(CAApp.getApplicationIdURLString(context));
            query.setLanguage(context.getString(R.string.language));
            query.setType(GlobalWarSearchType.BATTLES);
            query.setClanId(c.getClanId());
            query.setMapId(CAApp.MAP_ID + "");
            queries[i] = query;
            i++;
        }
//        GlobalWarQuery query = new GlobalWarQuery();
//        query.setWebAddress(CAApp.getBaseAddress(context));
//        query.setApplicationIdString(CAApp.getApplicationIdURLString(context));
//        query.setLanguage(context.getString(R.string.language));
//        query.setType(GlobalWarSearchType.MAPS);
//        queries[i++] = query;
        search.execute(queries);
        CAApp.IS_SEARCHING = true;
    }

    @Subscribe
    public void onReceived(GlobalWarResult result) {
        CAApp.IS_SEARCHING = false;
        CAApp.LAST_REFRESHED_TIME = Calendar.getInstance();
        Map<Integer, Clan> clans = CPManager.getSavedClans(getActivity());
        if (result != null) {
            lastSearchTime = Calendar.getInstance();
            setLastSearchedTime();
            for (int i = 0; i < result.getClanIds().size(); i++) {
                int clanId = result.getClanIds().get(i);
                List<Battle> battles = result.getBattles().get(clanId);
                Clan c = clans.get(clanId);
                if (c != null) {
                    if (c.getBattles() != null) {
                        c.getBattles().addAll(battles);
                    } else {
                        c.setBattles(battles);
                    }
                }
            }
            CAApp.IS_SEARCHING = false;
            CPManager.LIST_OF_CLANS = clans;
            CPManager.ADAPTER_CLANS_LIST = CPManager.getClanList(getActivity());
            pbProgress.setVisibility(View.GONE);
            if (!result.getBattles().isEmpty()) {
                etNoResults.setVisibility(View.GONE);
                mListView.setVisibility(View.VISIBLE);
                if (mAdapter == null) {
                    mAdapter = new BattleExpandableListAdapter(getActivity(), CPManager.ADAPTER_CLANS_LIST);
                    mListView.setAdapter(mAdapter);
                } else {
                    mAdapter.setClans(CPManager.ADAPTER_CLANS_LIST);
                    mAdapter.notifyDataSetChanged();
                }
            }
//            getOtherCampaignBattles(result, clans);
        } else {
            etNoResults.setText(R.string.activity_no_results);
            etNoResults.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
        }
    }

//    private void getOtherCampaignBattles(GlobalWarResult result, Map<Integer, Clan> clans) {
//        if (result.getCampaigns().size() > 1) {
//            Search<GlobalWarQuery, GlobalWarResult> search = new Search<GlobalWarQuery, GlobalWarResult>(new GlobalWarParser(), null, CAApp.getEventBus());
//            GlobalWarQuery[] queries = new GlobalWarQuery[result.getCampaigns().size() * clans.size()];
//            int i = 0;
//            int mapId = 1;
//            for (Campaign campaign : result.getCampaigns()) {
//                if (!campaign.getType().equals("normal")) {
//                    for (Clan c : clans.values()) {
//                        GlobalWarQuery query = new GlobalWarQuery();
//                        query.setWebAddress(CAApp.getBaseAddress(getActivity()));
//                        query.setApplicationIdString(CAApp.getApplicationIdURLString(getActivity()));
//                        query.setLanguage(getActivity().getString(R.string.language));
//                        query.setType(GlobalWarSearchType.BATTLES);
//                        query.setClanId(c.getClanId());
//                        query.setMapId(mapId + "");
//                        queries[i] = query;
//                        i++;
//                    }
//                }
//                mapId++;
//            }
//            pbProgress.setVisibility(View.VISIBLE);
//            Handler h = new Handler();
//            h.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    pbProgress.setVisibility(View.GONE);
//                }
//            }, 3000);
//            search.execute(queries);
//        }
//    }

    @Override
    public void refresh() {
        if (CAApp.canCLanUpdate(getActivity())) {
            if (mAdapter != null) {
                mAdapter.setClans(null);
                mAdapter.notifyDataSetChanged();
            }
            initSearch(true);
        } else {
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    public static void updateList() {
        try {
            if (mListView != null)
                if (mListView.getAdapter() != null)
                    ((BattleExpandableListAdapter) mListView.getExpandableListAdapter()).notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void updateListItems(UpdateClanListEvent event) {
        if (CPManager.ADAPTER_CLANS_LIST != null) {
            Object obj = null;
            for (Clan clan : CPManager.ADAPTER_CLANS_LIST) {
                if (clan.getClanId() == event.getClan().getClanId()) {
                    obj = clan;
                }
            }
            if (obj != null) {
                CPManager.ADAPTER_CLANS_LIST.remove(obj);
            }
            updateList();
        } else {
            refresh();
        }
    }

    private void setLastSearchedTime() {
        if (lastSearchTime != null) {
            tvTopText.setText("Last Search at: " + Utils.getHourMinuteFormatter(tvTopText.getContext()).format(lastSearchTime.getTime()));
        } else {
            tvTopText.setText("");
        }
    }
}