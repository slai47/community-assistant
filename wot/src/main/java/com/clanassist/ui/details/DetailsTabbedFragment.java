package com.clanassist.ui.details;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.clanassist.CAApp;
import com.clanassist.backend.ClanParser;
import com.clanassist.backend.PlayerParser;
import com.clanassist.backend.Tasks.Get60DayTask;
import com.clanassist.model.clan.Clan;
import com.clanassist.model.enums.StatsChoice;
import com.clanassist.model.events.ClanDownloadedFinishedEvent;
import com.clanassist.model.events.ClanLoadedFinishedEvent;
import com.clanassist.model.events.details.ClanProfileHit;
import com.clanassist.model.events.details.PlayerClanAdvancedHit;
import com.clanassist.model.events.details.PlayerClanHitFailed;
import com.clanassist.model.events.details.PlayerClearEvent;
import com.clanassist.model.events.details.PlayerProfileHit;
import com.clanassist.model.events.details.PlayerRefreshEvent;
import com.clanassist.model.events.details.PlayerWN8Event;
import com.clanassist.model.events.details.WebScrapDoneEvent;
import com.clanassist.model.interfaces.IDetails;
import com.clanassist.model.player.Player;
import com.clanassist.model.player.PlayerClan;
import com.clanassist.model.player.PlayerGraphs;
import com.clanassist.model.player.PlayerVehicleInfo;
import com.clanassist.model.player.WN8StatsInfo;
import com.clanassist.model.player.minimized.MinimizedPlayer;
import com.clanassist.model.player.minimized.MinimizedVehicleInfo;
import com.clanassist.model.search.enums.ClanSearchType;
import com.clanassist.model.search.enums.PlayerSearchType;
import com.clanassist.model.search.queries.ClanQuery;
import com.clanassist.model.search.queries.PlayerQuery;
import com.clanassist.model.search.results.ClanPlayerWN8sTaskResult;
import com.clanassist.model.search.results.ClanResult;
import com.clanassist.model.search.results.PlayerResult;
import com.clanassist.model.statistics.Statistics;
import com.clanassist.tools.CPManager;
import com.clanassist.tools.CPStorageManager;
import com.clanassist.tools.DownloadedClanManager;
import com.clanassist.tools.HitManager;
import com.clanassist.tools.WN8Manager;
import com.clanassist.ui.UIUtils;
import com.clanassist.ui.adapter.pager.DetailsPager;
import com.cp.assist.R;
import com.squareup.otto.Subscribe;
import com.utilities.logging.Dlog;
import com.utilities.search.Search;
import com.utilities.views.SlidingTabLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Harrison on 8/20/2014.
 */
public class DetailsTabbedFragment extends Fragment implements IDetails {

    public final String EXTRA_FROM_SEARCH = "fromSearch";
    public final String EXTRA_IS_PLAYER = "isPlayer";
    public final String EXTRA_SORT_TYPE = "sorttype";
    public final String EXTRA_ACCOUNT_ID = "accountId";
    public final String EXTRA_NAME = "name";
    public final String EXTRA_HIT_PROFILE = "hitprofile";
    public final String EXTRA_HIT_ADVANCED_PROFILE = "hitAdvProfile";

    DetailsPager mPagerAdapter;
    ViewPager mViewPager;
    SlidingTabLayout pagerTabs;
    View progress;

    private Player player;
    private Clan clan;

    private int accountId; //means player id or clanid
    private String name; //used for abbr or player name
    private boolean isPlayer;
    private boolean fromSearch;
    private String sortType;

    private boolean hitProfile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details_tabbed, container, false);
        if (savedInstanceState != null) {
            fromSearch = savedInstanceState.getBoolean(EXTRA_FROM_SEARCH);
            isPlayer = savedInstanceState.getBoolean(EXTRA_IS_PLAYER);
            accountId = savedInstanceState.getInt(EXTRA_ACCOUNT_ID);
            name = savedInstanceState.getString(EXTRA_NAME);
            sortType = savedInstanceState.getString(EXTRA_SORT_TYPE);
            hitProfile = savedInstanceState.getBoolean(EXTRA_HIT_PROFILE);
        }
        bindView(view);
        return view;
    }

    private void bindView(View view) {
        mViewPager = (ViewPager) view.findViewById(R.id.fragment_details_tabbed_pager);
        mPagerAdapter = new DetailsPager(getChildFragmentManager(), view.getContext(), isPlayer, this);
        pagerTabs = (SlidingTabLayout) view.findViewById(R.id.fragment_details_tabbed_pager_tab);
        Integer[] iconResourceArray = null;
        if(!isPlayer){
            iconResourceArray = new Integer[4];
            iconResourceArray[0] = R.drawable.ic_drawer_clan;
            iconResourceArray[1] = R.drawable.ic_drawer_player;
            iconResourceArray[2] = R.drawable.ic_drawer_stats;
            iconResourceArray[3] = R.drawable.ic_search;
        } else {
            iconResourceArray = new Integer[5];
            iconResourceArray[0] = R.drawable.ic_drawer_player;
            iconResourceArray[1] = R.drawable.ic_graph;
            iconResourceArray[2] = R.drawable.ic_drawer_stats;
            iconResourceArray[3] = R.drawable.ic_medal;
            iconResourceArray[4] = R.drawable.ic_tank;
        }
        pagerTabs.setIconResourceArray(iconResourceArray);

        pagerTabs.setDistributeEvenly(true);
        pagerTabs.setTextColor(getResources().getColor(R.color.material_accent));
        pagerTabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return ContextCompat.getColor(getContext(), R.color.material_accent);
            }
        });
        mViewPager.setAdapter(mPagerAdapter);
        pagerTabs.setViewPager(mViewPager);

        progress = view.findViewById(R.id.fragment_details_tabbed_progress);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(EXTRA_FROM_SEARCH, fromSearch);
        outState.putBoolean(EXTRA_IS_PLAYER, isPlayer);
        outState.putString(EXTRA_SORT_TYPE, sortType);
        outState.putInt(EXTRA_ACCOUNT_ID, accountId);
        outState.putString(EXTRA_NAME, name);
        outState.putBoolean(EXTRA_HIT_PROFILE, hitProfile);
    }

    @Override
    public void onResume() {
        super.onResume();
        CAApp.getEventBus().register(this);
        UIUtils.refreshActionBar(getActivity(), name);
        checkIfWeNeedToMerge();
        if(isPlayer){
            boolean hitProfile = HitManager.hasHitPlayerProfile(accountId);
            if(fromSearch)
                hitProfile = this.hitProfile;
            if(!hitProfile)
                progress.setVisibility(View.VISIBLE);
            else
                progress.setVisibility(View.GONE);

        } else {
            boolean hitProfile = HitManager.hasHitClanProfile(accountId);
            if(fromSearch)
                hitProfile = this.hitProfile;
            if(!hitProfile)
                progress.setVisibility(View.VISIBLE);
            else
                progress.setVisibility(View.GONE);

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        CAApp.getEventBus().unregister(this);
    }

    @Subscribe
    public void onReceived(ClanResult result) {
        if(!isPlayer) {
            progress.setVisibility(View.GONE);
            if (result != null) {
                Player p = getPlayer(getActivity());
                Dlog.d("DetailsTabbed", "p = " + p);
                if (p == null) {
                    boolean isSameClan = true;
                    if (result.getQuery() != null)
                        isSameClan = result.getQuery().getClanId() == accountId;
                    if (isSameClan) {
                        if (clan == null)
                            getClan(getActivity());
                        try {
                            Clan c = result.getDetails();
                            clan.setName(c.getName());
                            clan.setCreatedAt(c.getCreatedAt());
                            clan.setColor(c.getColor());
                            clan.setEmblem(c.getEmblem());
                            clan.setMotto(c.getMotto());
                            clan.setMembers_count(c.getMembers_count());
                            clan.setClanDisbanded(c.isClanDisbanded());
                            clan.setAbbreviation(c.getAbbreviation());
                            clan.setMembers(c.getMembers());
                            clan.setClanDisbanded(c.isClanDisbanded());
                            clan.setDescription(c.getDescription());
                            clan.setDescriptionHtml(c.getDescriptionHtml());
                            clan.setRequestAvailability(c.isRequestAvailability());
                            if (!fromSearch)
                                HitManager.hitClanProfile(clan.getClanId());
                            else
                                hitProfile = true;

                            Dlog.d("HitManagerOnRecieved", "hitManager = " + HitManager.hasHitClanProfile(accountId) + " hitProfile = " + hitProfile);

                            if (!fromSearch)
                                CPManager.saveClan(getActivity(), clan);
                            else
                                CPStorageManager.saveTempStoredClan(getActivity(), clan);
                            CAApp.getEventBus().post(new ClanProfileHit());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    if (result.getPlayerClanInfo() != null) {
                        PlayerClan clanInfo = result.getPlayerClanInfo();
                        p.setClanInfo(clanInfo);
                        if (!fromSearch) {
                            CPManager.savePlayer(getActivity(), p);
                        } else {
                            CPStorageManager.saveTempStoredPlayer(getActivity(), p);
                        }
                        CAApp.getEventBus().post(new ClanProfileHit());
                    }
                }
            }
        }
    }

    @Subscribe
    public void onReceived(PlayerResult result) {
        Dlog.d("DetailsTabbedOnPlayer", "onReceived");
        if(isPlayer) {
            progress.setVisibility(View.GONE);
            if (result != null) {

                Player p = result.getPlayer();
                boolean isForThisPlayer = true;
                if (result.getQuery() != null)
                    isForThisPlayer = result.getQuery().getAccount_id() == accountId;
                if (isForThisPlayer) {
                    if (p != null) {
                        player = p;
                        if (!fromSearch) {
                            CPManager.savePlayer(getActivity(), p);
                        } else {
                            CPStorageManager.saveTempStoredPlayer(getActivity(), p);
                        }
                        if (!fromSearch)
                            HitManager.hitPlayerProfile(player.getId());
                        else
                            hitProfile = true;

                        //send hit to the fragments
                        CAApp.getEventBus().post(new PlayerProfileHit());

                    } else {
                        //send hit to the fragments
                        CAApp.getEventBus().post(new PlayerClanHitFailed());
                        Toast.makeText(getActivity(), "Failure grabbing player information", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    int id = result.getQuery().getAccount_id();
                }
            }
        }
    }

    @Subscribe
    public void onWN8HitReceived(PlayerWN8Event event) {
        if (player == null)
            getPlayer(getActivity());

        if (player != null && event.getName() != null && event.getName().equals(name)) {
            if (event.getPastMonth() != 0) {
                WN8StatsInfo info = new WN8StatsInfo();
                info.setPastWeek(event.getPastWeek());
                info.setPastDay(event.getPastDay());
                info.setPastMonth(event.getPastMonth());
                info.setPastTwoMonths(event.getPastTwoMonths());
                CPStorageManager.savePlayerFutureStats(getActivity(), player.getId(), info);
                player.setWn8StatsInfo(info);
                if (!fromSearch) {
                    CPManager.savePlayer(getActivity(), player);
                } else {
                    CPStorageManager.saveTempStoredPlayer(getActivity(), player);
                }
                CAApp.getEventBus().post(new WebScrapDoneEvent());
            }
        }
    }

    @Subscribe
    public void onRefreshEvent(PlayerRefreshEvent event) {
        HitManager.removePlayerProfileHit(accountId);
        Player player = new Player();
        player.setId(accountId);
        player.setName(name);
        this.player = player;
        if (fromSearch) {
            CPStorageManager.saveTempStoredPlayer(getActivity(), player);
        } else {
            CPManager.savePlayer(getActivity(), player);
        }
        hitProfile = false;
        CAApp.getEventBus().post(new PlayerClearEvent());
        progress.setVisibility(View.VISIBLE);
    }

    @Subscribe
    public void downloadComplete(ClanDownloadedFinishedEvent event) {
        if (!event.isCancelled()) {
            Clan c = getClan(getActivity());
            if (c != null && event.getClanId().equals(c.getClanId() + "")) {
                mergeClanAndResult();
            } else {
                DownloadedClanManager.removeResult(event.getClanId());
            }
        } else {
            Clan c = getClan(getActivity());
            if (c != null && event.getClanId().equals(c.getClanId() + "")) {
                DownloadedClanManager.removeResult(c.getClanId() + "");
                ClanLoadedFinishedEvent e = new ClanLoadedFinishedEvent();
                e.setClanId(event.getClanId());
                e.setMerged(true);
                CAApp.getEventBus().post(e);
            }
        }
    }

    @Subscribe
    public void clanLoaded(ClanLoadedFinishedEvent event) {
        Clan c = getClan(getActivity());
        if (c != null && event.getClanId().equals(c.getClanId() + "") && !event.isMerged()) {
            mergeClanAndResult();
        }
    }

    private void checkIfWeNeedToMerge() {
        Clan c = getClan(getActivity());
        if (c != null) {
            if (DownloadedClanManager.hasClanDownloadLoaded(getActivity(), c.getClanId() + "") && hitProfile()) {
                if (c.getMembers() != null) {
                    try {
                        Player p = c.getMembers().get(0);
                        if (p.getOverallStats() == null) {
                            mergeClanAndResult();
                        }
                    } catch (Exception e) {
                    }
                }
            }
        }
    }

    private void mergeClanAndResult() {
        Clan c = getClan(mViewPager.getContext());
        if (c != null && c.getMembers() != null) {
            ClanPlayerWN8sTaskResult result = DownloadedClanManager.getClanDownload(c.getClanId() + "");
            if (result != null) {
                Map<Integer, Player> players = new HashMap<Integer, Player>();
                for (Player p : c.getMembers())
                    players.put(p.getId(), p);
                for (MinimizedPlayer mp : result.getPlayers()) {
                    Player p = players.get(mp.getId());
                    if (p != null) {
                        p.setWN8(mp.getWn8());
                        p.setClanWN8(mp.getcWN8());
                        p.setStrongholdWN8(mp.getShWN8());
                        Calendar cal = Calendar.getInstance();
                        cal.setTimeInMillis(mp.getLbt());
                        p.setLastBattleTime(cal);
                        p.setOverallStats(Statistics.build(mp.getStats()));
                        p.setPlayerVehicleInfoList(new ArrayList<PlayerVehicleInfo>());
                        if(mp.getInfos() != null)
                            for (MinimizedVehicleInfo infos : mp.getInfos()) {
                                p.getPlayerVehicleInfoList().add(PlayerVehicleInfo.build(infos));
                            }

                        WN8StatsInfo info = new WN8StatsInfo();
                        info.setPastDay((int) mp.getDwn8());
                        info.setPastWeek((int) mp.getWwn8());
                        info.setPastMonth((int) mp.getMwn8());
                        info.setPastTwoMonths((int) mp.getMmwn8());
                        p.setWn8StatsInfo(info);

                        players.put(mp.getId(), p);
                    }
                }

                List<Player> members = new ArrayList<Player>(players.values());
                c.setMembers(members);
                if (!fromSearch) {
                    CPManager.saveClan(getActivity(), c);
                } else {
//                    CPStorageManager.saveTempStoredClan(getActivity(), c);
                }
                clan = null;
                getClan(getActivity());

                ClanLoadedFinishedEvent e = new ClanLoadedFinishedEvent();
                e.setClanId(c.getClanId() + "");
                e.setMerged(true);
                CAApp.getEventBus().post(e);
            }
        }
    }

    public String getUrlClanName(Context ctx) {
        String clanName = name;
        Clan c = getClan(ctx);
        if (c != null)
            clanName = c.getAbbreviation();
        return clanName;
    }

    public String getUrlPlayerName(Context ctx) {
        String playerName = name;
        Player p = getPlayer(ctx);
        if (p != null)
            playerName = p.getName();
        return playerName;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPlayer() {
        return isPlayer;
    }

    @Override
    public boolean hitProfile() {
        boolean hit = false;
        if (fromSearch) {
            hit = hitProfile;
        } else {
            if (isPlayer())
                hit = HitManager.hasHitPlayerProfile(accountId);
            else
                hit = HitManager.hasHitClanProfile(accountId);
        }
        Dlog.d("Details", "hitProfile = " + hit);
        return hit;
    }

    public void setPlayer(boolean isPlayer) {
        this.isPlayer = isPlayer;
    }

    public Player getPlayer(Context ctx) {
        if (!fromSearch) {
            if (player == null && accountId != CAApp.EMPTY_NUMBER)
                player = CPManager.getSavedPlayers(ctx).get(accountId);
        } else {
            if (player == null)
                player = CPStorageManager.getTempStoredPlayer(ctx);
            if (player != null) {
                accountId = player.getId();
                name = player.getName();
            }
        }
        return player;
    }

    public Clan getClan(Context ctx) {
        if (!fromSearch) {
            if (clan == null && accountId != CAApp.EMPTY_NUMBER)
                clan = CPManager.getSavedClans(ctx).get(accountId);
        } else {
            if (clan == null)
                clan = CPStorageManager.getTempStoredClan(ctx);
            if (clan != null) {
                accountId = clan.getClanId();
                name = clan.getName();
            }
        }
        return clan;
    }

    public boolean isFromSearch() {
        return fromSearch;
    }

    public void setFromSearch(boolean fromSearch) {
        this.fromSearch = fromSearch;
    }

    public String getSortType() {
        return sortType;
    }

    public void setSortType(String sortType) {
        this.sortType = sortType;
    }


}
