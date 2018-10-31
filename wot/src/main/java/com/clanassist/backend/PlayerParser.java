package com.clanassist.backend;

import android.content.Context;

import com.clanassist.SVault;
import com.clanassist.model.Factory;
import com.clanassist.model.infoobj.VehicleWN8;
import com.clanassist.model.enums.StatsChoice;
import com.clanassist.model.infoobj.Tank;
import com.clanassist.model.infoobj.Tanks;
import com.clanassist.model.player.Player;
import com.clanassist.model.player.PlayerVehicleInfo;
import com.clanassist.model.search.enums.PlayerSearchType;
import com.clanassist.model.search.queries.PlayerQuery;
import com.clanassist.model.search.results.PlayerResult;
import com.clanassist.tools.WN8Manager;
import com.crashlytics.android.Crashlytics;
import com.utilities.Utils;
import com.utilities.interfaces.IParser;
import com.utilities.logging.Dlog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Harrison on 6/3/2014.
 */
public class PlayerParser implements IParser<PlayerQuery, PlayerResult> {

    public static final int TOP_NUMBER_OF_BATTLES = 25;

    public Context ctx;

    public boolean dontCalculate;

    public Map<Integer, VehicleWN8> vehiclesWN8;

    public Tanks tanks;

    public PlayerParser(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public PlayerResult parse(PlayerQuery... queries) {
        PlayerResult pr = new PlayerResult();
        List<String> results = new ArrayList<String>();
        for (PlayerQuery q : queries) {
            Crashlytics.setString(SVault.LOG_PLAYER_PARSE, q.getUrl());
            try {
                URL feed = new URL(q.getUrl());
                Dlog.d("Player", "" + q.getUrl());
                results.add(Utils.getInputStreamResponse(feed));
                pr.getQueries().add(q);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (queries.length == 1) {
            pr.setQuery(queries[0]);
        }
        for (int i = 0; i < results.size(); i++) {
            String result = results.get(i);
            PlayerQuery pq = pr.getQueries().get(i);
            try {
                parseResult(pr, pq, result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return pr;
    }

    private void parseResult(PlayerResult pr, PlayerQuery pq, String feed) throws Exception {
        JSONObject result = null;
        try {
            result = new JSONObject(feed);
        } catch (JSONException e) {
        }
        if (result != null) {
            String status = result.optString("status");
            pr.setError(ParserHelper.getError(result));
            JSONObject meta = result.optJSONObject("meta");
            int count = meta.optInt("count");
            if (count != 0) {
                JSONArray dataArray = result.optJSONArray(ParserHelper.DATA);
                JSONObject data = result.optJSONObject(ParserHelper.DATA);
                if (pq.getType() == PlayerSearchType.SEARCH) {
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject player = dataArray.getJSONObject(i);
                        Player p = parsePlayer(player);
                        pr.getPlayers().add(p);
                    }
                } else if (pq.getType() == PlayerSearchType.DETAILS) {
                    JSONObject player = data.optJSONObject(pq.getAccount_id() + "");
                    pr.setPlayer(Factory.parsePlayer(player));
                } else if (pq.getType() == PlayerSearchType.TANKS || pq.getType() == PlayerSearchType.VEHICLES) {
                    JSONArray playerInfo = data.optJSONArray(pq.getAccount_id() + "");
                    if (playerInfo != null) {
                        int bestWN8 = 0;
                        int bestWN8id = 0;

                        Map<String, Integer> bestWN8PerClassType = null;
                        Map<String, Integer> bestWN8PerClassTypeID = null;
                        Map<Integer, Integer> bestWN8PerTier = null;
                        Map<Integer, Integer> bestWN8PerTierID = null;
                        Map<String, Integer> bestWN8PerNation = null;
                        Map<String, Integer> bestWN8PerNationID = null;
                        Map<String, Integer> tanksPerClassType = null;
                        Map<Integer, Integer> tanksPerTier = null;
                        Map<String, Integer> tanksPerNation = null;
                        Map<String, Double> battlesPerClassType = null;
                        Map<String, Double> averageWN8PerClassType = null;
                        Map<Integer, Double> battlesPerTier = null;
                        Map<Integer, Double> wn8PerTier = null;
                        Map<String, Double> wn8PerNation = null;
                        Map<String, Double> battlesPerNation = null;

                        if (!dontCalculate) {
                            bestWN8PerClassType = ParserHelper.createClassTypeIntMap();
                            bestWN8PerClassTypeID = ParserHelper.createClassTypeIntMap();
                            bestWN8PerTier = ParserHelper.createTierIntMap();
                            bestWN8PerTierID = ParserHelper.createTierIntMap();
                            bestWN8PerNation = new HashMap<String, Integer>();
                            bestWN8PerNationID = new HashMap<String, Integer>();

                            tanksPerClassType = ParserHelper.createClassTypeIntMap();
                            tanksPerTier = ParserHelper.createTierIntMap();
                            tanksPerNation = new HashMap<String, Integer>();

                            battlesPerClassType = ParserHelper.createClassTypeMap();
                            averageWN8PerClassType = ParserHelper.createClassTypeMap();
                            battlesPerTier = ParserHelper.createTierMap();
                            wn8PerTier = ParserHelper.createTierMap();
                            wn8PerNation = new HashMap<String, Double>();
                            battlesPerNation = new HashMap<String, Double>();
                        }

                        for (int i = 0; i < playerInfo.length(); i++) {
                            JSONObject tank = playerInfo.getJSONObject(i);
                            PlayerVehicleInfo info = Factory.parsePlayerVehicleInfo(tank);
                            if (info.getOverallStats().getBattles() > 0) {
                                if (pq.getType() == PlayerSearchType.VEHICLES) {
                                    VehicleWN8 vWN8 = vehiclesWN8.get(info.getTankId());
                                    Tank tankInfo = tanks.getTank(info.getTankId());
                                    if (vWN8 != null) {
                                        int wn8 = (int) WN8Manager.CalculateWN8(info, vWN8, StatsChoice.OVERALL);
                                        int clanWN8 = (int) WN8Manager.CalculateWN8(info, vWN8, StatsChoice.CLAN);
                                        int stronghold = (int) WN8Manager.CalculateWN8(info, vWN8, StatsChoice.STRONGHOLD);
                                        info.setWN8(wn8);
                                        info.setClanWN8(clanWN8);
                                        info.setStrongholdWN8(stronghold);
                                        if (wn8 > bestWN8 && info.getOverallStats().getBattles() > TOP_NUMBER_OF_BATTLES) {
                                            bestWN8 = wn8;
                                            bestWN8id = info.getTankId();
                                        }
                                        if (!dontCalculate) { // If you don't calculate, this won't run.
                                            info.setName(tankInfo.getName());
                                            averageWN8PerClassType.put(tankInfo.getClassType(), averageWN8PerClassType.get(tankInfo.getClassType()) + wn8);
                                            wn8PerTier.put(tankInfo.getTier(), wn8PerTier.get(tankInfo.getTier()) + wn8);
                                            if (info.getOverallStats() != null) {
                                                battlesPerClassType.put(tankInfo.getClassType(), battlesPerClassType.get(tankInfo.getClassType()) + info.getOverallStats().getBattles());
                                                battlesPerTier.put(tankInfo.getTier(), battlesPerTier.get(tankInfo.getTier()) + info.getOverallStats().getBattles());
                                            }
                                            battlesPerNation.put(tankInfo.getNation(), battlesPerNation.get(tankInfo.getNation()) + info.getOverallStats().getBattles());
                                            wn8PerNation.put(tankInfo.getNation(), wn8PerNation.get(tankInfo.getNation()) + wn8);

                                            tanksPerClassType.put(tankInfo.getClassType(), tanksPerClassType.get(tankInfo.getClassType()) + 1);
                                            tanksPerNation.put(tankInfo.getNation(), tanksPerNation.get(tankInfo.getNation()) + 1);
                                            tanksPerTier.put(tankInfo.getTier(), tanksPerTier.get(tankInfo.getTier()) + 1);

                                            if (bestWN8PerClassType.get(tankInfo.getClassType()) < wn8) {
                                                bestWN8PerClassType.put(tankInfo.getClassType(), wn8);
                                                bestWN8PerClassTypeID.put(tankInfo.getClassType(), info.getTankId());
                                            }

                                            if (bestWN8PerTier.get(tankInfo.getTier()) < wn8) {
                                                bestWN8PerTier.put(tankInfo.getTier(), wn8);
                                                bestWN8PerTierID.put(tankInfo.getTier(), info.getTankId());
                                            }

                                            if (bestWN8PerNation.get(tankInfo.getNation()) < wn8) {
                                                bestWN8PerNation.put(tankInfo.getNation(), wn8);
                                                bestWN8PerNationID.put(tankInfo.getNation(), info.getTankId());
                                            }
                                        }
                                    }
                                }
//                                pr.getBadges().addPlayerVehicleInfo(info);
//                                pr.getPlayerVehicleInfos().add(info);
                            }
                        }
//                        pr.setBestTankWN8(bestWN8);
//                        pr.setBestTankIdWN8(bestWN8id);

                        if (pq.getType() == PlayerSearchType.VEHICLES && !dontCalculate) {
                            // preform averages
                            ParserHelper.averageStrMap(averageWN8PerClassType, tanksPerClassType);
                            ParserHelper.averageIntMap(wn8PerTier, tanksPerTier);
                            ParserHelper.averageStrMap(wn8PerNation, tanksPerNation);

//                            pr.setAverageWN8PerType(averageWN8PerClassType);
//                            pr.setBattlesPerType(battlesPerClassType);
//                            pr.setAverageWN8PerTier(wn8PerTier);
//                            pr.setNumBattlesPerType(battlesPerTier);
//                            pr.setAverageWN8PerNation(wn8PerNation);
//                            pr.setBattlesPerNation(battlesPerNation);
//
//                            pr.setBestWN8PerClassType(bestWN8PerClassType);
//                            pr.setBestWN8PerClassTypeID(bestWN8PerClassTypeID);
//                            pr.setBestWN8PerNation(bestWN8PerNation);
//                            pr.setBestWN8PerNationID(bestWN8PerNationID);
//                            pr.setBestWN8PerTier(bestWN8PerTier);
//                            pr.setBestWN8PerTierID(bestWN8PerTierID);
//
//                            pr.setTanksPerClassType(tanksPerClassType);
//                            pr.setTanksPerNation(tanksPerNation);
//                            pr.setTanksPerTier(tanksPerTier);
                        }
                    }

//                    Collections.sort(pr.getPlayerVehicleInfos(), new Comparator<PlayerVehicleInfo>() {
//                        @Override
//                        public int compare(PlayerVehicleInfo lhs, PlayerVehicleInfo rhs) {
//                            return rhs.getOverallStats().getBattles() - lhs.getOverallStats().getBattles();
//                        }
//                    });
                }
            }
        }
    }

    private Player parsePlayer(JSONObject player) {
        Player p = new Player();
        p.setName(player.optString("nickname"));
        p.setId(player.optInt("account_id"));
        return p;
    }

    private void averageMap(Map<String, Double> map, int battles) {
        for (String str : map.keySet()) {
            Double x = map.get(str);
            x = x / battles;
            map.put(str, x);
        }
    }
}
