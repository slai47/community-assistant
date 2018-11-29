package com.clanassist.backend.Tasks;

import android.content.Context;
import android.os.AsyncTask;
import androidx.annotation.NonNull;

import com.clanassist.CAApp;
import com.clanassist.backend.ParserHelper;
import com.clanassist.model.Factory;
import com.clanassist.model.enums.StatsChoice;
import com.clanassist.model.events.details.PlayerWN8Event;
import com.clanassist.model.infoobj.Tank;
import com.clanassist.model.infoobj.Tanks;
import com.clanassist.model.infoobj.VehicleWN8;
import com.clanassist.model.infoobj.WN8Data;
import com.clanassist.model.player.Badges;
import com.clanassist.model.player.Player;
import com.clanassist.model.player.PlayerGraphs;
import com.clanassist.model.player.PlayerVehicleInfo;
import com.clanassist.model.player.WN8StatsInfo;
import com.clanassist.model.search.queries.PlayerQuery;
import com.clanassist.model.search.results.PlayerResult;
import com.clanassist.tools.WN8Manager;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.utilities.Utils;
import com.utilities.logging.Dlog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by slai4 on 2/27/2016.
 */
public class GetTankerInfo extends AsyncTask<PlayerQuery, Void, PlayerResult>{

    public static final int TOP_NUMBER_OF_BATTLES = 25;

    private Context ctx;

    private boolean createGraphs;
    private boolean grabWN8;
    private boolean wn8Callback;
    private boolean grabClanInfo;
    private boolean grabAchievements;

    public GetTankerInfo(Context ctx, boolean createGraphs, boolean grabWN8, boolean grabClanInfo, boolean grabAchievements) {
        this.ctx = ctx;
        this.createGraphs = createGraphs;
        this.grabWN8 = grabWN8;
        this.grabClanInfo = grabClanInfo;
        this.grabAchievements = grabAchievements;
    }

    @Override
    protected PlayerResult doInBackground(PlayerQuery... params) {
        PlayerQuery query = params[0];


        PlayerResult result = getPlayerResult(query);

        return result;
    }

    @Override
    protected void onPostExecute(PlayerResult playerResult) {
        super.onPostExecute(playerResult);
        CAApp.getEventBus().post(playerResult);
    }

    @NonNull
    public PlayerResult getPlayerResult(PlayerQuery query) {

        Answers.getInstance().logCustom(new CustomEvent("GetCaptain").putCustomAttribute("Server", query.getWebAddress()));

        PlayerResult result = new PlayerResult();
        String url = CAApp.getBaseAddress(ctx) + "/wot/account/info/?" + CAApp.getApplicationIdURLString(ctx) + "&account_id=" + query.getAccount_id();
        Crashlytics.setString("DETAILS_URL", url);
        Dlog.wtf("Details URL", url);

        String playerDetailsFeed = getURLResult(url);

        String url2 = CAApp.getBaseAddress(ctx) + "/wot/tanks/stats/?" + CAApp.getApplicationIdURLString(ctx) + "&account_id=" + query.getAccount_id() + "&fields=all,clan,tank_id,max_xp,max_frags,mark_of_mastery,stronghold_defense,stronghold_skirmish";
        Crashlytics.setString("TANKS_URL", url2);
        Dlog.wtf("TANKS URL", url2);

        String playerTanksFeed = getURLResult(url2);

        // parse players and details feed, grab this stuff from player parser
        Player player = new Player();

        if(playerDetailsFeed != null){
            JSONObject playerResult = null;
            try {
                playerResult = new JSONObject(playerDetailsFeed);
            } catch (JSONException e) {
            }
            if(playerResult != null){
                JSONObject data = playerResult.optJSONObject("data");
                JSONObject playerObj = data.optJSONObject(query.getAccount_id() + "");
                if(playerObj != null)
                    player = Factory.parsePlayer(playerObj);
            }
        }

        if(playerTanksFeed != null){
            JSONObject playerResult = null;
            try {
                playerResult = new JSONObject(playerTanksFeed);
            } catch (JSONException e) {
            }
            if(playerResult != null){
                JSONObject data = playerResult.optJSONObject("data");
                if(data != null) {
                    JSONArray playerInfo = data.optJSONArray(query.getAccount_id() + "");
                    if (playerInfo != null) {
                        createTanksAndGraphs(player, playerInfo);

                        player.setWN8(WN8Manager.CalculateWN8s(ctx, player, StatsChoice.OVERALL));
                        player.setClanWN8(WN8Manager.CalculateWN8s(ctx, player, StatsChoice.CLAN));
                        player.setStrongholdWN8(WN8Manager.CalculateWN8s(ctx, player, StatsChoice.STRONGHOLD));

                        Collections.sort(player.getPlayerVehicleInfoList(), new Comparator<PlayerVehicleInfo>() {
                            @Override
                            public int compare(PlayerVehicleInfo lhs, PlayerVehicleInfo rhs) {
                                return rhs.getOverallStats().getBattles() - lhs.getOverallStats().getBattles();
                            }
                        });
                    }
                }
            }
        }

        if(grabAchievements) {
            grabAchievements(query, player);
        }

        //check for clan information before firing this off
        if(grabClanInfo && player.getClanInfo() != null) {
            grabPlayerClanInfo(query, player);
        }

        if(grabWN8){
            Get60DayTask task = new Get60DayTask(ctx);
            if(!wn8Callback) {
                PlayerWN8Event event = task.getPlayerWN8(player.getName());
                if (event.getPastMonth() != 0) {
                    WN8StatsInfo info = new WN8StatsInfo();
                    info.setPastWeek(event.getPastWeek());
                    info.setPastDay(event.getPastDay());
                    info.setPastMonth(event.getPastMonth());
                    info.setPastTwoMonths(event.getPastTwoMonths());
                    player.setWn8StatsInfo(info);
                } else {
                    player.setWn8StatsInfo(new WN8StatsInfo());
                }
            } else {
                task.execute(player.getName());
            }
        }
        result.setPlayer(player);
        return result;
    }



    private void grabPlayerClanInfo(PlayerQuery query, Player player) {
        String url4 = CAApp.getBaseAddress(ctx) + "/wgn/clans/membersinfo/?" + CAApp.getApplicationIdURLString(ctx) + "&account_id=" + query.getAccount_id();
        Crashlytics.setString("CLAN_INFO_URL", url4);
        Dlog.wtf("Clan Info URL", url4);

        String clanInfoFeed = getURLResult(url4);

        //parse this without using the parsers, grab this stuff from clan parser
        JSONObject playerResult = null;
        try {
            playerResult = new JSONObject(clanInfoFeed);
        } catch (Exception e) {
        }
        if(playerResult != null) {
            JSONObject data = playerResult.optJSONObject("data");
            if(data != null) {
                JSONObject clan = data.optJSONObject(query.getAccount_id() + "");
                if (clan != null)
                    player.setClanInfo(Factory.parsePlayerClan(clan));
            }
        }
    }

    private void grabAchievements(PlayerQuery query, Player player) {
        String url3 = CAApp.getBaseAddress(ctx) + "/wot/account/achievements/?" + CAApp.getApplicationIdURLString(ctx) + "&account_id=" + query.getAccount_id();
        Crashlytics.setString("ACHIEVEMENTS_URL", url3);
        Dlog.wtf("Achievement URL", url3);

        String achievementsFeed = getURLResult(url3);
        //parse this
        JSONObject playerResult = null;
        try {
            playerResult = new JSONObject(achievementsFeed);
        } catch (Exception e) {
        }
        if(playerResult != null) {
            JSONObject data = playerResult.optJSONObject("data");
            if(data != null) {
                JSONObject playerAchievements = data.optJSONObject(query.getAccount_id() + "");
                if(playerAchievements != null) {
                    Map<String, Integer> achiMap = new HashMap<String, Integer>();
                    Iterator<String> iter = playerAchievements.keys();
                    while (iter.hasNext()) { // go through the type of achievements
                        String keyType = iter.next();
                        JSONObject achievementType = playerAchievements.optJSONObject(keyType);
                        Iterator<String> iter2 = achievementType.keys();
                        while (iter2.hasNext()) { // go through achievements in a type
                            String keyAchi = iter2.next();
                            Integer num = achievementType.optInt(keyAchi);
                            achiMap.put(keyAchi, num);
                        }
                    }
                    player.setAchievements(achiMap);
                }
            }
        }
    }

    private void createTanksAndGraphs(Player player, JSONArray playerInfo) {
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

        if (createGraphs) {
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

        Tanks tanks = CAApp.getInfoManager().getTanks(ctx);
        WN8Data wn8Data = CAApp.getInfoManager().getWN8Data(ctx);

        player.setBadges(new Badges());
        player.setPlayerVehicleInfoList(new ArrayList<PlayerVehicleInfo>());

        for (int i = 0; i < playerInfo.length(); i++) {
            JSONObject tank = playerInfo.optJSONObject(i);
            PlayerVehicleInfo info = Factory.parsePlayerVehicleInfo(tank);
            if (info.getOverallStats().getBattles() > 0) {
                VehicleWN8 vWN8 = wn8Data.getWN8(info.getTankId());
                Tank tankInfo = tanks.getTank(info.getTankId());
                if (vWN8 != null && tankInfo != null) {
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
                    if (createGraphs) {
                        info.setName(tankInfo.getName());

                        averageWN8PerClassType.put(tankInfo.getClassType(), averageWN8PerClassType.get(tankInfo.getClassType()) + wn8);
                        wn8PerTier.put(tankInfo.getTier(), wn8PerTier.get(tankInfo.getTier()) + wn8);
                        if (info.getOverallStats() != null) {
                            battlesPerClassType.put(tankInfo.getClassType(), battlesPerClassType.get(tankInfo.getClassType()) + info.getOverallStats().getBattles());
                            battlesPerTier.put(tankInfo.getTier(), battlesPerTier.get(tankInfo.getTier()) + info.getOverallStats().getBattles());
                        }
//                        Dlog.d("GetTankerInfo","tankInfo = " + tankInfo.getNation() + " bpnV = " + battlesPerNation.get(tankInfo.getNation()) + " infoBattles = " + info.getOverallStats().getBattles());

                        Double battlesNation = battlesPerNation.get(tankInfo.getNation());
                        if(battlesNation == null)
                            battlesNation = 0.0;
                        battlesPerNation.put(tankInfo.getNation(), battlesNation + info.getOverallStats().getBattles());

                        Double wn8Nation = wn8PerNation.get(tankInfo.getNation());
                        if(wn8Nation == null)
                            wn8Nation = 0.0;
                        wn8PerNation.put(tankInfo.getNation(), wn8Nation + wn8);

                        Integer tanksNation = tanksPerNation.get(tankInfo.getNation());
                        if(tanksNation == null)
                            tanksNation = 0;
                        tanksPerNation.put(tankInfo.getNation(), tanksNation + 1);

                        tanksPerClassType.put(tankInfo.getClassType(), tanksPerClassType.get(tankInfo.getClassType()) + 1);
                        tanksPerTier.put(tankInfo.getTier(), tanksPerTier.get(tankInfo.getTier()) + 1);

                        if (bestWN8PerClassType.get(tankInfo.getClassType()) < wn8) {
                            bestWN8PerClassType.put(tankInfo.getClassType(), wn8);
                            bestWN8PerClassTypeID.put(tankInfo.getClassType(), info.getTankId());
                        }

                        if (bestWN8PerTier.get(tankInfo.getTier()) < wn8) {
                            bestWN8PerTier.put(tankInfo.getTier(), wn8);
                            bestWN8PerTierID.put(tankInfo.getTier(), info.getTankId());
                        }

                        Integer bestWN8Nation = bestWN8PerNation.get(tankInfo.getNation());
                        if(bestWN8Nation == null)
                            bestWN8Nation = 0;
                        if (bestWN8Nation < wn8) {
                            bestWN8PerNation.put(tankInfo.getNation(), wn8);
                            bestWN8PerNationID.put(tankInfo.getNation(), info.getTankId());
                        }
                    }
                }
                player.getBadges().addPlayerVehicleInfo(info);
                player.getPlayerVehicleInfoList().add(info);
            }
        }
        player.setBestTankWN8(bestWN8);
        player.setBestTankIdWN8(bestWN8id);

        if (createGraphs) {
            // preform averages
            ParserHelper.averageStrMap(averageWN8PerClassType, tanksPerClassType);
            ParserHelper.averageIntMap(wn8PerTier, tanksPerTier);
            ParserHelper.averageStrMap(wn8PerNation, tanksPerNation);

            PlayerGraphs graphs = new PlayerGraphs();

            graphs.setAverageWN8PerClass(averageWN8PerClassType);
            graphs.setBattlesPerClass(battlesPerClassType);
            graphs.setAverageWN8PerTier(wn8PerTier);
            graphs.setBattlesPerTier(battlesPerTier);
            graphs.setAverageWN8PerNation(wn8PerNation);
            graphs.setBattlesPerNation(battlesPerNation);

            graphs.setBestWN8PerClassType(bestWN8PerClassType);
            graphs.setBestWN8PerClassTypeID(bestWN8PerClassTypeID);
            graphs.setBestWN8PerNation(bestWN8PerNation);
            graphs.setBestWN8PerNationID(bestWN8PerNationID);
            graphs.setBestWN8PerTier(bestWN8PerTier);
            graphs.setBestWN8PerTierID(bestWN8PerTierID);

            graphs.setTanksPerClass(tanksPerClassType);
            graphs.setTanksPerNation(tanksPerNation);
            graphs.setTanksPerTier(tanksPerTier);

            player.setPlayerGraphs(graphs);
        }
    }

    public String getURLResult(String url) {
        String results = null;
        try {
            URL feed = new URL(url);
            results = Utils.getInputStreamResponse(feed);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    public boolean isWn8Callback() {
        return wn8Callback;
    }

    public void setWn8Callback(boolean wn8Callback) {
        this.wn8Callback = wn8Callback;
    }
}
