package com.clanassist.ui.details.player;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.clanassist.CAApp;
import com.clanassist.SVault;
import com.clanassist.backend.PlayerParser;
import com.clanassist.backend.Tasks.GetTankerInfo;
import com.clanassist.listeners.AddRemovePlayerListener;
import com.clanassist.model.clan.Clan;
import com.clanassist.model.events.ClanPlayerAddRemoveEvent;
import com.clanassist.model.events.details.ClanProfileHit;
import com.clanassist.model.events.details.PlayerClanAdvancedHit;
import com.clanassist.model.events.details.PlayerClanHitFailed;
import com.clanassist.model.events.details.PlayerClearEvent;
import com.clanassist.model.events.details.PlayerDifferenceSavedEvent;
import com.clanassist.model.events.details.PlayerProfileHit;
import com.clanassist.model.events.details.WebScrapDoneEvent;
import com.clanassist.model.interfaces.IDetails;
import com.clanassist.model.interfaces.INameOfFragment;
import com.clanassist.model.listmodel.CPStatModel;
import com.clanassist.model.player.Player;
import com.clanassist.model.player.PlayerVehicleInfo;
import com.clanassist.model.player.storage.PlayerSavedStats;
import com.clanassist.model.player.storage.SavedStatsObj;
import com.clanassist.model.search.enums.PlayerSearchType;
import com.clanassist.model.search.queries.PlayerQuery;
import com.clanassist.model.search.results.PlayerResult;
import com.clanassist.model.statistics.Statistics;
import com.clanassist.tools.CPManager;
import com.clanassist.tools.CPStorageManager;
import com.clanassist.tools.HitManager;
import com.clanassist.tools.WN8ColorManager;
import com.clanassist.tools.WebsiteUrlBuilder;
import com.clanassist.ui.MainActivity;
import com.clanassist.ui.UIUtils;
import com.clanassist.ui.adapter.CPStatAdapter;
import com.clanassist.ui.details.DetailsTabbedFragment;
import com.cp.assist.R;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.utilities.Utils;
import com.utilities.preferences.Prefs;
import com.utilities.search.Search;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Obsidian47 on 3/1/14.
 */
public class PlayerDetailsFragment extends Fragment implements INameOfFragment {

    public static final String PLAYER_DETAILS = "Player Details";
    private CheckBox cbAddRemoveButton;
    private View addArea;

    private View detailsArea;
    private View clanDetailsArea;

    private View gridViewArea;
    private GridView gridView;
    private CPStatAdapter mAdapter;
    private TextView tvNoOverall;

    private View gridViewClanArea;
    private GridView gridViewClan;
    private CPStatAdapter mClanAdapter;
    private TextView tvNoClanStats;

    private View gridViewStrongHoldArea;
    private GridView gridViewStronghold;
    private CPStatAdapter mStrongholdAdapter;
    private TextView tvNoStrongStats;

    private ImageView ivClanImage;
    private TextView tvClanAbbr;
    private TextView tvClanName;
    private TextView tvClanJoinTime;
    private TextView tvClanRole;

    private TextView tvCreatedOn;
    private TextView tvLastBattleOn;

    private Button bWotLabs;
    private Button bWoTStats;
    private Button bClanTools;

    private IDetails details;

    private boolean isColorBlind;

    private float wn8Dif;
    private float clanWN8Dif;
    private float strongWN8Dif;

    private int battleDif;
    private int clanBattleDif;
    private int strongBattleDif;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player_details, container, false);
        bindView(view);
        return view;
    }

    private void bindView(View view) {
        cbAddRemoveButton = (CheckBox) view.findViewById(R.id.player_details_addremove_button);
        addArea = view.findViewById(R.id.player_details_addremove_button_area);

        detailsArea = view.findViewById(R.id.player_details_details_area);

        gridViewArea = view.findViewById(R.id.player_details_gridview_area);
        gridView = (GridView) view.findViewById(R.id.player_details_gridview);
        tvNoOverall = (TextView) view.findViewById(R.id.player_details_gridview_no_label);

        gridViewClan = (GridView) view.findViewById(R.id.player_details_gridview_clan);
        gridViewClanArea = view.findViewById(R.id.player_details_gridview_clan_area);
        tvNoClanStats = (TextView) view.findViewById(R.id.player_details_gridview_clan_no_label);

        gridViewStronghold = (GridView) view.findViewById(R.id.player_details_gridview_stronghold);
        gridViewStrongHoldArea = view.findViewById(R.id.player_details_gridview_stronghold_area);
        tvNoStrongStats = (TextView) view.findViewById(R.id.player_details_gridview_stronghold_no_label);

        clanDetailsArea = view.findViewById(R.id.player_details_clan_info_area);
        tvClanAbbr = (TextView) view.findViewById(R.id.player_details_clan_info_abbr);
        ivClanImage = (ImageView) view.findViewById(R.id.player_details_clan_info_image);
        tvClanName = (TextView) view.findViewById(R.id.player_details_clan_info_name);
        tvClanJoinTime = (TextView) view.findViewById(R.id.player_details_clan_info_time_there);
        tvClanRole = (TextView) view.findViewById(R.id.player_details_clan_info_role);

        tvCreatedOn = (TextView) view.findViewById(R.id.player_details_account_info_create);
        tvLastBattleOn = (TextView) view.findViewById(R.id.player_details_account_info_last_battle);

        bWotLabs = (Button) view.findViewById(R.id.player_details_open_wotlabs);
        bWoTStats = (Button) view.findViewById(R.id.player_details_open_wotstats);
        bClanTools = (Button) view.findViewById(R.id.player_details_open_clan_tools);

        UIUtils.setUpCard(detailsArea, 0);
        UIUtils.setUpCard(gridViewArea, 0);
        UIUtils.setUpCard(gridViewClanArea, 0);
        UIUtils.setUpCard(gridViewStrongHoldArea, 0);
        UIUtils.setUpCard(view, R.id.player_details_web_links_area);
    }

    @Override
    public void onResume() {
        super.onResume();
        CAApp.getEventBus().register(this);
        initView();
        if (details.hitProfile()) {
            findPlayerDifference(null);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        CAApp.getEventBus().unregister(this);
    }

    // send off a account and tanks search when needed.
    private void initView() {
        Context ctx = getActivity();
        if(ctx == null){
            ctx = detailsArea.getContext();
        }
        if(ctx != null) {
            Prefs pref = new Prefs(ctx);
            isColorBlind = pref.getBoolean(SVault.PREF_COLORBLIND, false);
            if (details == null)
                details = (IDetails) getActivity().getSupportFragmentManager().findFragmentById(R.id.container);
            Player p = details.getPlayer(ctx);
            if (p != null) {
                if (CPManager.getSavedPlayers(ctx).get(p.getId()) != null) {
                    cbAddRemoveButton.setChecked(true);
                    if (TextUtils.isEmpty(CAApp.getDefaultName(ctx)) && getActivity() != null) {
                        UIUtils.createBookmarkingDialogIfNeeded((MainActivity) getActivity(), p);
                    }
                } else {
                    cbAddRemoveButton.setChecked(false);
                }
                addArea.setOnClickListener(new AddRemovePlayerListener(cbAddRemoveButton.getContext(), details.getPlayer(ctx), cbAddRemoveButton, false));
                boolean hasHitProfile = details.hitProfile();
                if (hasHitProfile) {
                    Statistics stats = p.getOverallStats();
                    Statistics clanStats = p.getClanStats();
                    Statistics strongholdStats = p.getStrongholdStats();
                    if (stats != null) {
                        int battles = stats.getBattles();
                        int clanBattles = clanStats.getBattles();
                        int strongholdBattles = strongholdStats.getBattles();
                        gridViewArea.setVisibility(View.VISIBLE);
                        gridViewClanArea.setVisibility(View.VISIBLE);
                        int defaultBackground = (!CAApp.isLightTheme(ctx) ? ctx.getResources().getColor(R.color.material_grid_view_default) : ctx.getResources().getColor(R.color.material_light_grid_view_default));
                        if (battles > 0) {
                            double kd = (double) stats.getFrags() / (battles - stats.getSurvivedBattles());
                            double winRate = ((double) stats.getWins() / battles) * 100;
                            double damageDealtToReceived = ((double) stats.getDamageDealt() / (double) stats.getDamageReceived());

                            List<CPStatModel> models = new ArrayList<CPStatModel>();
                            CPStatModel wn8Model = new CPStatModel(Utils.getDefaultDecimalFormatter().format(p.getWN8()), getString(R.string.wn8_cap), "", WN8ColorManager.getBackgroundColor(ctx, (int) p.getWN8()));
                            wn8Model.setDifference(wn8Dif);
                            models.add(wn8Model);

                            if (p.getWn8StatsInfo() != null) {
                                CPStatModel day = new CPStatModel("" + p.getWn8StatsInfo().getPastDay(), getString(R.string.last_24_wn8), "", WN8ColorManager.getBackgroundColor(ctx, p.getWn8StatsInfo().getPastDay()));
                                models.add(day);

                                CPStatModel week = new CPStatModel("" + p.getWn8StatsInfo().getPastWeek(), getString(R.string.last_7_wn8), "", WN8ColorManager.getBackgroundColor(ctx, p.getWn8StatsInfo().getPastWeek()));
                                models.add(week);

                                CPStatModel month = new CPStatModel("" + p.getWn8StatsInfo().getPastMonth(), getString(R.string.last_30_wn8), "", WN8ColorManager.getBackgroundColor(ctx, p.getWn8StatsInfo().getPastMonth()));
                                models.add(month);

                                CPStatModel twoMonth = new CPStatModel("" + p.getWn8StatsInfo().getPastTwoMonths(), getString(R.string.last_60_wn8), "", WN8ColorManager.getBackgroundColor(ctx, p.getWn8StatsInfo().getPastTwoMonths()));
                                models.add(twoMonth);
                            }

                            CPStatModel battleModel = new CPStatModel(battles + "", getString(R.string.battles), null, WN8ColorManager.getBattlesColor(ctx, battles));
                            battleModel.setDifference(battleDif);
                            models.add(battleModel);

                            models.add(new CPStatModel(Utils.getOneDepthDecimalFormatter().format(winRate), getString(R.string.win_rate), null, WN8ColorManager.getWinRateColor(ctx, winRate)));

                            models.add(new CPStatModel(Utils.getDefaultDecimalFormatter().format(kd), getString(R.string.k_d), null, WN8ColorManager.getKillsColor(ctx, kd)));

                            models.add(new CPStatModel(stats.getAverageXp() + "", getString(R.string.avg_exp), null, defaultBackground));

                            models.add(new CPStatModel(stats.getHitsPercentage() + "%", getString(R.string.hit_percentage), null, defaultBackground));

                            models.add(new CPStatModel((stats.getDamageDealt() / battles) + "", getString(R.string.avg_damage), null, defaultBackground));

                            models.add(new CPStatModel(Utils.getDefaultDecimalFormatter().format(damageDealtToReceived), getString(R.string.dmg_factor), null, defaultBackground));

                            mAdapter = new CPStatAdapter(ctx, R.layout.list_cp_stat, models);
                            mAdapter.setIsColorBlind(isColorBlind);
                            mAdapter.setDefaultBackground(defaultBackground);
                            gridView.setAdapter(mAdapter);
                            tvNoOverall.setVisibility(View.GONE);
                        } else {
                            gridView.setVisibility(View.GONE);
                            tvNoOverall.setVisibility(View.VISIBLE);
                        }
                        if (clanBattles > 0) {
                            double kd = (double) clanStats.getFrags() / (clanBattles - clanStats.getSurvivedBattles());
                            double winRate = ((double) clanStats.getWins() / clanBattles) * 100;
                            double damageDealtToReceived = ((double) clanStats.getDamageDealt() / (double) clanStats.getDamageReceived());

                            List<CPStatModel> models = new ArrayList<CPStatModel>();

                            CPStatModel wn8Model = new CPStatModel(Utils.getDefaultDecimalFormatter().format(p.getClanWN8()), getString(R.string.wn8_cap), "", WN8ColorManager.getBackgroundColor(ctx, (int) p.getClanWN8()));
                            wn8Model.setDifference(clanWN8Dif);
                            models.add(wn8Model);

                            CPStatModel battlesModel = new CPStatModel(clanBattles + "", getString(R.string.battles), null, WN8ColorManager.getBattlesColor(ctx, clanBattles));
                            battlesModel.setDifference(clanBattleDif);
                            models.add(battlesModel);

                            models.add(new CPStatModel(Utils.getOneDepthDecimalFormatter().format(winRate), getString(R.string.win_rate), null, WN8ColorManager.getWinRateColor(ctx, winRate)));

                            models.add(new CPStatModel(Utils.getDefaultDecimalFormatter().format(kd), getString(R.string.k_d), null, WN8ColorManager.getKillsColor(ctx, kd)));

                            models.add(new CPStatModel(clanStats.getAverageXp() + "", getString(R.string.avg_exp), null, defaultBackground));

                            models.add(new CPStatModel(clanStats.getHitsPercentage() + "%", getString(R.string.hit_percentage), null, defaultBackground));

                            models.add(new CPStatModel((clanStats.getDamageDealt() / clanBattles) + "", getString(R.string.avg_damage), null, defaultBackground));

                            models.add(new CPStatModel(Utils.getDefaultDecimalFormatter().format(damageDealtToReceived), getString(R.string.dmg_factor), null, defaultBackground));

                            mClanAdapter = new CPStatAdapter(ctx, R.layout.list_cp_stat, models);
                            mClanAdapter.setDefaultBackground(defaultBackground);
                            mClanAdapter.setIsColorBlind(isColorBlind);
                            gridViewClan.setAdapter(mClanAdapter);
                            tvNoClanStats.setVisibility(View.GONE);
                        } else {
                            gridViewClan.setVisibility(View.GONE);
                            tvNoClanStats.setVisibility(View.VISIBLE);
                        }

                        if (strongholdBattles > 0) {
                            double kd = (double) strongholdStats.getFrags() / (strongholdBattles - strongholdStats.getSurvivedBattles());
                            double winRate = ((double) strongholdStats.getWins() / strongholdBattles) * 100;
                            double damageDealtToReceived = ((double) strongholdStats.getDamageDealt() / (double) strongholdStats.getDamageReceived());

                            List<CPStatModel> models = new ArrayList<CPStatModel>();

                            CPStatModel wn8Model = new CPStatModel(Utils.getDefaultDecimalFormatter().format(p.getStrongholdWN8()), getString(R.string.wn8_cap), "", WN8ColorManager.getBackgroundColor(ctx, (int) p.getStrongholdWN8()));
                            wn8Model.setDifference(strongWN8Dif);
                            models.add(wn8Model);

                            CPStatModel battlesModel = new CPStatModel(strongholdBattles + "", getString(R.string.battles), null, WN8ColorManager.getBattlesColor(ctx, strongholdBattles));
                            battlesModel.setDifference(strongBattleDif);
                            models.add(battlesModel);

                            models.add(new CPStatModel(Utils.getOneDepthDecimalFormatter().format(winRate), getString(R.string.win_rate), null, WN8ColorManager.getWinRateColor(ctx, winRate)));

                            models.add(new CPStatModel(Utils.getDefaultDecimalFormatter().format(kd), getString(R.string.k_d), null, WN8ColorManager.getKillsColor(ctx, kd)));

                            models.add(new CPStatModel(strongholdStats.getAverageXp() + "", getString(R.string.avg_exp), null, defaultBackground));

                            models.add(new CPStatModel(strongholdStats.getHitsPercentage() + "%", getString(R.string.hit_percentage), null, defaultBackground));

                            models.add(new CPStatModel((strongholdStats.getDamageDealt() / strongholdBattles) + "", getString(R.string.avg_damage), null, defaultBackground));

                            models.add(new CPStatModel(Utils.getDefaultDecimalFormatter().format(damageDealtToReceived), getString(R.string.dmg_factor), null, defaultBackground));

                            mStrongholdAdapter = new CPStatAdapter(ctx, R.layout.list_cp_stat, models);
                            mStrongholdAdapter.setDefaultBackground(defaultBackground);
                            mStrongholdAdapter.setIsColorBlind(isColorBlind);
                            gridViewStronghold.setAdapter(mStrongholdAdapter);
                            tvNoStrongStats.setVisibility(View.GONE);
                        } else {
                            gridViewStronghold.setVisibility(View.GONE);
                            tvNoStrongStats.setVisibility(View.VISIBLE);
                        }
                    }
                    if (p.getClanInfo() != null) {
                        if (!TextUtils.isEmpty(p.getClanInfo().getName())) {
                            int color = Color.parseColor(p.getClanInfo().getColor());
                            tvClanRole.setText(p.getClanInfo().getRole_i18n());
                            tvClanName.setText(p.getClanInfo().getName());
                            tvClanAbbr.setText(p.getClanInfo().getAbbr());
                            tvClanAbbr.setTag(p.getClanInfo().getColor());
                            tvClanAbbr.setTextColor(color);

                            if (!TextUtils.isEmpty(p.getClanInfo().getEmblems().getBw_tank()))
                                Picasso.with(ctx).load(p.getClanInfo().getEmblems().getBw_tank()).into(ivClanImage);

                            Calendar joinedAt = p.getClanInfo().getSince();
                            Calendar now = Calendar.getInstance();
                            long joined = joinedAt.getTimeInMillis();
                            long nowT = now.getTimeInMillis();
                            long dif = Utils.convertToDays(nowT - joined);

                            tvClanJoinTime.setText(getString(R.string.player_details_joined) + " " + dif + " " + getString(R.string.player_details_days_ago));

                            clanDetailsArea.setVisibility(View.VISIBLE);
                            clanDetailsArea.setTag(p.getClanInfo().getClanId());
                            clanDetailsArea.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Integer clanId = (Integer) clanDetailsArea.getTag();
                                    if (clanId != null) {
                                        DetailsTabbedFragment frag = new DetailsTabbedFragment();
                                        Clan c = CPManager.getSavedClans(v.getContext()).get(clanId);
                                        frag.setFromSearch(c == null);
                                        if (c == null) {
                                            Clan clan = new Clan();
                                            clan.setClanId(clanId);
                                            clan.setName(tvClanName.getText().toString());
                                            clan.setColor((String) tvClanAbbr.getTag());
                                            CPStorageManager.saveTempStoredClan(v.getContext(), clan);
                                            HitManager.removeClanProfileHit(clan.getClanId());
                                        }
                                        frag.setAccountId(clanId);
                                        frag.setPlayer(false);
                                        frag.setName(tvClanName.getText().toString());
                                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, frag).addToBackStack(clanId + "").commit();
                                        Handler h = new Handler();
                                        h.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                getActivity().invalidateOptionsMenu();
                                            }
                                        }, 500);
                                    }
                                }
                            });
                        }
                    }
                    DateFormat df = Utils.getDayMonthYearFormatter(ctx);
                    if (p.getCreatedAt() != null)
                        tvCreatedOn.setText(df.format(p.getCreatedAt().getTime()));
                    else
                        tvCreatedOn.setText(getString(R.string.unknown));
                    if (p.getLastBattleTime() != null)
                        tvLastBattleOn.setText(df.format(p.getLastBattleTime().getTime()));
                    else
                        tvLastBattleOn.setText(getString(R.string.unknown));
                }
                if (!hasHitProfile)
                    startQuery();
            }
            setUpButtons();
        }
    }

    private void setUpButtons() {
        bWotLabs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Answers.getInstance().logCustom(new CustomEvent("Details Website").putCustomAttribute("Website", "WotLabs"));
                Fragment frag = getActivity().getSupportFragmentManager().findFragmentById(R.id.container);
                DetailsTabbedFragment fragment2 = (DetailsTabbedFragment) frag;
                String url2 = WebsiteUrlBuilder.getWoTLabsAddress(getActivity(), fragment2.isPlayer(), fragment2.getUrlPlayerName(getActivity()), fragment2.getUrlClanName(getActivity()));
                Intent i2 = new Intent(Intent.ACTION_VIEW);
                i2.setData(Uri.parse(url2));
                startActivity(i2);
            }
        });
        bWoTStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Answers.getInstance().logCustom(new CustomEvent("Details Website").putCustomAttribute("Website", "WoTStats"));
                Fragment frag = getActivity().getSupportFragmentManager().findFragmentById(R.id.container);
                DetailsTabbedFragment fragment3 = (DetailsTabbedFragment) frag;
                String url3 = WebsiteUrlBuilder.getWotStatsAddress(getActivity(), fragment3.isPlayer(), fragment3.getUrlPlayerName(getActivity()), fragment3.getUrlClanName(getActivity()));
                Intent i3 = new Intent(Intent.ACTION_VIEW);
                i3.setData(Uri.parse(url3));
                startActivity(i3);
            }
        });
        bClanTools.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Answers.getInstance().logCustom(new CustomEvent("Details Website").putCustomAttribute("Website", "ClanTools"));
                Fragment frag = getActivity().getSupportFragmentManager().findFragmentById(R.id.container);
                DetailsTabbedFragment fragment3 = (DetailsTabbedFragment) frag;
                String url3 = WebsiteUrlBuilder.getClanToolsAddress(getActivity(), fragment3.isPlayer(), fragment3.getAccountId());
                Intent i3 = new Intent(Intent.ACTION_VIEW);
                i3.setData(Uri.parse(url3));
                startActivity(i3);
            }
        });

    }

    @Subscribe
    public void findPlayerDifference(PlayerDifferenceSavedEvent event) {
        new Runnable() {
            @Override
            public void run() {
                try {
                    Player p = details.getPlayer(getActivity());
                    SavedStatsObj obj = CPStorageManager.getPlayerStats(getActivity(), p.getId());
                    if (obj != null) {
                        if (obj.getStats() != null) {
                            if (obj.getStats().size() > 1) {
                                PlayerSavedStats now = obj.getStats().get(0);
                                PlayerSavedStats then = obj.getStats().get(1);// gets us the last time the player came here
                                wn8Dif = now.getWn8() - then.getWn8();
                                battleDif = now.getStats().getBattles() - then.getStats().getBattles();

                                clanWN8Dif = now.getClanWN8() - then.getClanWN8();
                                clanBattleDif = now.getClanStats().getBattles() - then.getClanStats().getBattles();

                                strongWN8Dif = now.getStrongWN8() - then.getStrongWN8();
                                if(now.getStrongholdStats() != null && then.getStrongholdStats() != null)
                                    strongBattleDif = now.getStrongholdStats().getBattles() - then.getStrongholdStats().getBattles();
                            }
                        }
                        if (battleDif != 0) {
                            gridView.post(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        initView();
                                    } catch (IllegalStateException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }
                    obj = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.run();
    }

//    @Subscribe
//    public void findPlayerFutureDifference(PlayerFutureStatsSavedEvent event){
//        new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Player p = details.getPlayer(getActivity());
//                    PlayerFutureStats future = CPStorageManager.getPlayerFutureStats(getActivity(), p.getId());
//                    if(future != null){
//                        if(future.getStatsInfos().size() > 0){
//                            WN8StatsInfo now = future.getStatsInfos().get(0);
//                            WN8StatsInfo then = future.getStatsInfos().get(1);// gets us the last time the player came here
//                            prevPastDay = now.getPastDay() - then.getPastDay();
//                            prevPastWeek = now.getPastWeek() - then.getPastWeek();
//                            prevPastMonth = now.getPastMonth() - then.getPastMonth();
//                            prevPastTwoMonths = now.getPastTwoMonths() - then.getPastTwoMonths();
//                        }
//                    }
//                    if(prevPastDay != 0) {
//                        gridView.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                initView();
//                            }
//                        });
//                    }
//                    future = null;
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }.run();
//    }

    private void startQuery() {
        boolean hasConnection = Utils.hasInternetConnection(getActivity());
        if (hasConnection) {
//            Search<PlayerQuery, PlayerResult> search = new Search<PlayerQuery, PlayerResult>(new PlayerParser(getActivity()), null, CAApp.getEventBus());
            PlayerQuery query = new PlayerQuery();
            query.setAccount_id(details.getPlayer(getActivity()).getId());
            query.setApplicationIdString(CAApp.getApplicationIdURLString(getActivity()));
            query.setLanguage(getResources().getString(R.string.language));
            query.setWebAddress(CAApp.getBaseAddress(getActivity()));
//            search.execute(query);

            GetTankerInfo info = new GetTankerInfo(getContext(), true, true, true, true);
            info.setWn8Callback(true);
            info.execute(query);

        } else {
            Toast.makeText(getActivity(), getActivity().getString(R.string.no_internet_title), Toast.LENGTH_SHORT).show();
        }
    }

    @Subscribe
    public void onReceived(PlayerProfileHit result) {
        Player p = details.getPlayer(getActivity());
        if (p != null)
            initView();
    }

    @Subscribe
    public void onClanReceived(ClanProfileHit result) {
        Player p = details.getPlayer(getActivity());
        if (p != null)
            if (p.getClanInfo() != null)
                initView();
    }

    @Subscribe
    public void onRefreshClearEvent(PlayerClearEvent event) {
        tvClanAbbr.setText("");
        tvClanName.setText("");
        tvClanJoinTime.setText("");
        tvClanRole.setText("");

        mAdapter = null;
        mClanAdapter = null;

        gridView.setAdapter(null);
        gridViewClan.setAdapter(null);
        gridViewStronghold.setAdapter(null);

        tvCreatedOn.setText("");
        tvLastBattleOn.setText("");
        startQuery();
    }

    @Subscribe
    public void onScrapDone(WebScrapDoneEvent event) {
        initView();
    }

    @Subscribe
    public void onReceivedFail(PlayerClanHitFailed result) {
        Toast.makeText(getActivity(), R.string.failure, Toast.LENGTH_SHORT).show();
    }

    @Subscribe
    public void addRemoveClanPlayer(ClanPlayerAddRemoveEvent event) {
        if (details.isFromSearch())
            if (event.getPlayer() != null) {
                Player item = new Player();
                item.setId(event.getPlayer().getId());
                item.setName(event.getPlayer().getName());
                if (!event.isRemove()) {
                    CPManager.savePlayer(getActivity(), item);
                } else {
                    Prefs prefs = new Prefs(getActivity());
                    int selected = prefs.getInt(SVault.PREF_SELECTED_USER_ID, 0);
                    if (event.getPlayer().getId() == selected) {
                        prefs.remove(SVault.PREF_SELECTED_USER_NAME);
                        prefs.remove(SVault.PREF_SELECTED_USER_ID);
                    }
                    CPManager.removePlayer(getActivity(), item);
                }
            }
    }

    @Override
    public String getNameOfFragment() {
        String fragName = null;
        try {
            fragName = details.getPlayer(getActivity()).getName();
        } catch (Exception e) {
        }
        if (!TextUtils.isEmpty(fragName))
            fragName = PLAYER_DETAILS;
        return fragName;
    }

    public IDetails getDetails() {
        return details;
    }

    public void setDetails(IDetails details) {
        this.details = details;
    }
}

