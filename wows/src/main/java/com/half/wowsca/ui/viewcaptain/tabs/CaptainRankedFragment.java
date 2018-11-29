package com.half.wowsca.ui.viewcaptain.tabs;

import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.half.wowsca.CAApp;
import com.half.wowsca.R;
import com.half.wowsca.interfaces.ICaptain;
import com.half.wowsca.model.BatteryStats;
import com.half.wowsca.model.Captain;
import com.half.wowsca.model.Ship;
import com.half.wowsca.model.encyclopedia.items.ShipInfo;
import com.half.wowsca.model.events.CaptainReceivedEvent;
import com.half.wowsca.model.events.ProgressEvent;
import com.half.wowsca.model.events.RefreshEvent;
import com.half.wowsca.model.events.ShipClickedEvent;
import com.half.wowsca.model.ranked.RankedInfo;
import com.half.wowsca.model.ranked.SeasonInfo;
import com.half.wowsca.model.ranked.SeasonStats;
import com.half.wowsca.ui.CAFragment;
import com.half.wowsca.ui.UIUtils;
import org.greenrobot.eventbus.Subscribe;
import com.utilities.Utils;
import com.utilities.logging.Dlog;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by slai4 on 11/29/2015.
 */
public class CaptainRankedFragment extends CAFragment {

    private LinearLayout aSeasons;

    private LinearLayout aShips;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ranked, container, false);
        bindView(view);
        return view;
    }

    private void bindView(View view) {
        aSeasons = (LinearLayout) view.findViewById(R.id.ranked_container);
        aShips = (LinearLayout) view.findViewById(R.id.ranked_ship_container);

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

        if (captain != null && captain.getRankedSeasons() != null && captain.getShips() != null) {
            Dlog.wtf("Ranked","seasons = " + captain.getRankedSeasons());
            refreshing(false);
            createSeasonList(aSeasons, captain.getRankedSeasons(), captain.getShips());

        } else {
            aSeasons.removeAllViews();
            View view = LayoutInflater.from(getContext()).inflate(R.layout.list_season, aSeasons, false);

            View aHas = view.findViewById(R.id.list_season_has_info);
            View aNo = view.findViewById(R.id.list_season_no_info);
            TextView tvNoInfo = (TextView) view.findViewById(R.id.list_season_no_info_text);
            aNo.setVisibility(View.VISIBLE);
            aHas.setVisibility(View.GONE);
            tvNoInfo.setText(getString(R.string.search_no_results));
            UIUtils.setUpCard(view, R.id.list_season_no_info_card);

            aSeasons.addView(view);
        }
    }

    private void createSeasonList(LinearLayout container, List<RankedInfo> seasons, List<Ship> ships){
        container.removeAllViews();

        Collections.sort(seasons, new Comparator<RankedInfo>() {
            @Override
            public int compare(RankedInfo lhs, RankedInfo rhs) {
                if(lhs.getSeasonInt() != null && rhs.getSeasonInt() != null)
                    return rhs.getSeasonInt().compareTo(lhs.getSeasonInt());
                else
                    return rhs.getSeasonNum().compareToIgnoreCase(lhs.getSeasonNum());
            }
        });
        Map<String, List<Ship>> shipMap = new HashMap<>();
        final Map<String, SeasonStats> seasonMap = new HashMap<>();
        for(RankedInfo info : seasons){
            shipMap.put(info.getSeasonNum(), new ArrayList<Ship>());
        }

        for(Ship s : ships){
            if(s.getRankedInfo() != null){
                for(SeasonInfo info : s.getRankedInfo()){
                    if(info.getSolo() != null){
                        List<Ship> seasonShips = shipMap.get(info.getSeasonNum());
                        if(seasonShips == null){
                            seasonShips = new ArrayList<>();
                            shipMap.put(info.getSeasonNum(), seasonShips);
                        }
                        seasonShips.add(s);
                        seasonMap.put(info.getSeasonNum() + s.getShipId(), info.getSolo());
                    }
                }
            }
        }

        for(final RankedInfo info : seasons){
            View view = LayoutInflater.from(getContext()).inflate(R.layout.list_season, aSeasons, false);

            View aHas = view.findViewById(R.id.list_season_has_info);
            View aNo = view.findViewById(R.id.list_season_no_info);

            TextView tvNoInfo = (TextView) view.findViewById(R.id.list_season_no_info_text);

            LinearLayout llStars = (LinearLayout) view.findViewById(R.id.list_season_star_amount);

            TextView tvRank = (TextView) view.findViewById(R.id.list_season_rank);
            TextView tvMaxRank = (TextView) view.findViewById(R.id.list_season_max_rank);
            TextView tvTitle = (TextView) view.findViewById(R.id.list_season_title);

            TextView tvWinRate = (TextView) view.findViewById(R.id.list_season_winrate);
            TextView tvSurvivalRate = (TextView) view.findViewById(R.id.list_season_survival_rate);

            TextView tvBattles = (TextView) view.findViewById(R.id.list_season_battles);
            TextView tvAvgDmg = (TextView) view.findViewById(R.id.list_season_avg_dmg);
            TextView tvAvgKills = (TextView) view.findViewById(R.id.list_season_avg_kills);
            TextView tvAvgCaps = (TextView) view.findViewById(R.id.list_season_avg_caps);
            TextView tvAvgResets = (TextView) view.findViewById(R.id.list_season_avg_resets);
            TextView tvAvgPlanes = (TextView) view.findViewById(R.id.list_season_avg_planes);
            TextView tvAvgXP = (TextView) view.findViewById(R.id.list_season_avg_xp);

            TextView tvBatteryMain = (TextView) view.findViewById(R.id.list_season_battery_kills_main);
            TextView tvBatteryAircraft = (TextView) view.findViewById(R.id.list_season_battery_kills_aircraft);
            TextView tvBatteryTorps = (TextView) view.findViewById(R.id.list_season_battery_kills_torps);
            TextView tvBatteryOther = (TextView) view.findViewById(R.id.list_season_battery_kills_other);

            TextView tvTopDamage = (TextView) view.findViewById(R.id.list_season_top_damage);
            TextView tvTopExp = (TextView) view.findViewById(R.id.list_season_top_exp);

            View aShips = view.findViewById(R.id.list_season_ships_area);
            View aShipsTop = view.findViewById(R.id.list_season_ships_top_area);
            LinearLayout llShips = (LinearLayout) view.findViewById(R.id.list_season_ships_container);
            ImageView ivShipsArea = (ImageView) view.findViewById(R.id.list_season_ships_image);

            aShips.setTag(ivShipsArea);
            aShipsTop.setTag(aShips);
            if(ivShipsArea.getVisibility() == View.VISIBLE)
                aShipsTop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View ships = (View) v.getTag();
                        ImageView iv = (ImageView) ships.getTag();
                        if (ships.getVisibility() == View.VISIBLE) {
                            iv.setImageResource(R.drawable.ic_expand);
                            ships.setVisibility(View.GONE);
                        } else {
                            iv.setImageResource(R.drawable.ic_collapse);
                            ships.setVisibility(View.VISIBLE);
                        }
                    }
                });

            if(info.getSolo() != null) {
                aHas.setVisibility(View.VISIBLE);
                aNo.setVisibility(View.GONE);

                UIUtils.setUpCard(view, R.id.list_season_info_card);

                tvRank.setText(info.getRank() + "");

                tvMaxRank.setText(" / " + info.getStartRank());

                llStars.removeAllViews();
                for(int i = 0; i < info.getStars(); i++){
                    ImageView iv = new ImageView(getContext());
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(30,30);
                    iv.setLayoutParams(params);
                    iv.setImageResource(R.drawable.ic_star);
                    iv.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(getContext(), R.color.premium_shade), PorterDuff.Mode.MULTIPLY));
                    llStars.addView(iv);
                }

                tvTitle.setText(getString(R.string.ranked_season) + " " + info.getSeasonNum());


                SeasonStats stats = info.getSolo();
                float battles = stats.getBattles();
                if(battles > 0) {
                    float winrate = stats.getWins() / battles * 100;
                    float survival = stats.getSurvived() / battles * 100;
                    float avgDmg = stats.getDamage() / battles;
                    float avgCaps = stats.getCapPts() / battles;
                    float avgResets = stats.getDrpCapPts() / battles;
                    float avgKills = stats.getFrags() / battles;
                    float avgPlanes = stats.getPlanesKilled() / battles;
                    float avgXP = stats.getXp() / battles;

                    DecimalFormat format = new DecimalFormat("###,###,###");
                    tvTopDamage.setText(format.format(stats.getMaxDamage()) + "");
                    tvTopExp.setText(stats.getMaxXP() + "");

                    tvBattles.setText((int) battles + "");

                    tvWinRate.setText(Utils.getDefaultDecimalFormatter().format(winrate) + "%");
                    tvSurvivalRate.setText(Utils.getDefaultDecimalFormatter().format(survival) + "%");

                    tvAvgDmg.setText(Utils.getOneDepthDecimalFormatter().format(avgDmg));
                    tvAvgKills.setText(Utils.getOneDepthDecimalFormatter().format(avgKills));
                    tvAvgCaps.setText(Utils.getOneDepthDecimalFormatter().format(avgCaps));
                    tvAvgResets.setText(Utils.getOneDepthDecimalFormatter().format(avgResets));
                    tvAvgPlanes.setText(Utils.getOneDepthDecimalFormatter().format(avgPlanes));
                    tvAvgXP.setText(Utils.getOneDepthDecimalFormatter().format(avgXP));

                    tvBatteryMain.setText(createBatteryString(stats.getMain()));
                    tvBatteryTorps.setText(createBatteryString(stats.getTorps()));
                    tvBatteryAircraft.setText(createBatteryString(stats.getAircraft()));
                    int otherTotal = stats.getFrags() - stats.getMain().getFrags() - stats.getAircraft().getFrags() - stats.getTorps().getFrags();
                    tvBatteryOther.setText(otherTotal + "");

                    if(ships != null){
                        List<Ship> seasonsShips = shipMap.get(info.getSeasonNum());
                        final String seasonName = info.getSeasonNum();
                        Collections.sort(seasonsShips, new Comparator<Ship>() {
                            @Override
                            public int compare(Ship lhs, Ship rhs) {
                                String id = seasonName + lhs.getShipId();
                                SeasonStats shipStats = seasonMap.get(id);
                                String id2 = seasonName + rhs.getShipId();
                                SeasonStats shipStats2 = seasonMap.get(id2);
                                return shipStats2.getBattles() - shipStats.getBattles();
                            }
                        });
                        if(seasons.size() > 0){
                            aShipsTop.setVisibility(View.VISIBLE);
                            if(ivShipsArea.getVisibility() == View.GONE){
                                aShips.setVisibility(View.VISIBLE);
                            } else {
                                aShips.setVisibility(View.GONE);
                            }
                            llShips.removeAllViews();

                            View shipViewTitle = LayoutInflater.from(getContext()).inflate(R.layout.list_ranked_ships_title, llShips, false);
                            llShips.addView(shipViewTitle);

                            for(Ship s : seasonsShips){
                                String id = info.getSeasonNum() + s.getShipId();
                                SeasonStats shipStats = seasonMap.get(id);
                                ShipInfo shipInfo = CAApp.getInfoManager().getShipInfo(getContext()).get(s.getShipId());
                                if(shipInfo != null && shipStats.getBattles() > 0) {
                                    View shipView = LayoutInflater.from(getContext()).inflate(R.layout.list_ranked_ships, llShips, false);
                                    TextView title = (TextView) shipView.findViewById(R.id.list_ranked_ships_title);
                                    TextView one = (TextView) shipView.findViewById(R.id.list_ranked_ships_1);
                                    TextView two = (TextView) shipView.findViewById(R.id.list_ranked_ships_2);
                                    TextView three = (TextView) shipView.findViewById(R.id.list_ranked_ships_3);

                                    title.setText(shipInfo.getName());
                                    one.setText(shipStats.getBattles() + "");

                                    DecimalFormat formatter = Utils.getOneDepthDecimalFormatter();

                                    two.setText(formatter.format((shipStats.getWins() / (float) shipStats.getBattles()) * 100) + "%");
                                    three.setText(formatter.format((shipStats.getSurvived() / (float) shipStats.getBattles()) * 100) + "%");

                                    shipView.setClickable(true);
                                    shipView.setTag(s.getShipId());
                                    shipView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Long s = (Long) v.getTag();
                                            if(s != null){
                                                CAApp.getEventBus().post(new ShipClickedEvent(s));
                                            }
                                        }
                                    });

                                    llShips.addView(shipView);
                                }
                            }
                        } else {
                            aShipsTop.setVisibility(View.GONE);
                            aShips.setVisibility(View.GONE);
                        }
                    }
                }
            } else {
                aNo.setVisibility(View.VISIBLE);
                aHas.setVisibility(View.GONE);
                tvNoInfo.setText(getString(R.string.no_data_for_season) + info.getSeasonNum());
                UIUtils.setUpCard(view, R.id.list_season_no_info_card);
            }
            container.addView(view);
        }
    }

    public static String createBatteryString(BatteryStats stats){
        StringBuilder sb = new StringBuilder();
        sb.append(stats.getFrags());
        if(stats.getShots() > 0){
            sb.append("\n");
            sb.append(Utils.getOneDepthDecimalFormatter().format(((float) stats.getHits()/ (float)stats.getShots()) * 100) + "%");
        }
        return sb.toString();
    }

    private void createShipList(){
        aShips.removeAllViews();
    }

    @Subscribe
    public void onReceive(CaptainReceivedEvent event) {
        initView();
    }

    @Subscribe
    public void onRefresh(RefreshEvent event) {
        //clear out elements
        refreshing(true);
        aSeasons.removeAllViews();
    }

    @Subscribe
    public void onProgressEvent(ProgressEvent event){
        if(mSwipeRefreshLayout != null){
            mSwipeRefreshLayout.setRefreshing(event.isRefreshing());
        }
    }
}
