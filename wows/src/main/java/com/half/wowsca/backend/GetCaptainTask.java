package com.half.wowsca.backend;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.SparseArray;




import com.half.wowsca.CAApp;
import com.half.wowsca.managers.CARatingManager;
import com.half.wowsca.model.Achievement;
import com.half.wowsca.model.Captain;
import com.half.wowsca.model.CaptainDetails;
import com.half.wowsca.model.CaptainPrivateInformation;
import com.half.wowsca.model.Ship;
import com.half.wowsca.model.ShipCompare;
import com.half.wowsca.model.Statistics;
import com.half.wowsca.model.encyclopedia.holders.ShipsHolder;
import com.half.wowsca.model.encyclopedia.holders.WarshipsStats;
import com.half.wowsca.model.encyclopedia.items.ShipInfo;
import com.half.wowsca.model.encyclopedia.items.ShipStat;
import com.half.wowsca.model.queries.CaptainQuery;
import com.half.wowsca.model.ranked.RankedInfo;
import com.half.wowsca.model.ranked.SeasonInfo;
import com.half.wowsca.model.result.CaptainResult;
import com.utilities.Utils;
import com.utilities.logging.Dlog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by slai4 on 9/19/2015.
 */
public class GetCaptainTask extends AsyncTask<CaptainQuery, Integer, CaptainResult> {

    private static final String DATA = "data";

    private Context ctx;

    @Override
    protected CaptainResult doInBackground(CaptainQuery... params) {
        CaptainQuery query = params[0];

        String token = !TextUtils.isEmpty(query.getToken()) ? "&access_token=" + query.getToken() : "";

        CaptainResult result = new CaptainResult();
        String url = CAApp.WOWS_API_SITE_ADDRESS + query.getServer().getSuffix() + "/wows/account/info/?application_id=" + query.getServer().getAppId() + "&account_id=" + query.getId() + token + "&extra=statistics.club,statistics.pve,statistics.pvp_div2,statistics.pvp_div3,statistics.pvp_solo";
        Dlog.wtf("Details URL", url);

        String playerDetailsFeed = getURLResult(url);

        String url2 = CAApp.WOWS_API_SITE_ADDRESS + query.getServer().getSuffix() + "/wows/ships/stats/?application_id=" + query.getServer().getAppId() + "&account_id=" + query.getId() + "&extra=club,pve,pvp_div2,pvp_div3,pvp_solo";
        Dlog.wtf("Ships URL", url2);

        String playerShipsFeed = getURLResult(url2);

        String url3 = CAApp.WOWS_API_SITE_ADDRESS + query.getServer().getSuffix() + "/wows/account/achievements/?application_id=" + query.getServer().getAppId() + "&account_id=" + query.getId();
        Dlog.wtf("Achievement URL", url3);

        String achievementsFeed = getURLResult(url3);

        String ranked = CAApp.WOWS_API_SITE_ADDRESS + query.getServer().getSuffix() + "/wows/seasons/accountinfo/?application_id=" + query.getServer().getAppId() + "&account_id=" + query.getId();
        Dlog.wtf("RankedAccountURL", ranked);

        String rankedAccountFeed = getURLResult(ranked);

        String rankedShipStats = CAApp.WOWS_API_SITE_ADDRESS + query.getServer().getSuffix() + "/wows/seasons/shipstats/?application_id=" + query.getServer().getAppId() + "&account_id=" + query.getId();
        Dlog.wtf("RankedShipURL", rankedShipStats);

        String rankedShipsFeed = getURLResult(rankedShipStats);

        String clanInfoQuery = CAApp.WOWS_API_SITE_ADDRESS + query.getServer().getSuffix() + "/wows/clans/accountinfo/?application_id=" + query.getServer().getAppId() + "&account_id=" + query.getId() + "&extra=clan";

        Dlog.wtf("ClanInfoQuery", clanInfoQuery);

        String clanInfoFeed = getURLResult(clanInfoQuery);

        if (!TextUtils.isEmpty(playerDetailsFeed)) {
            Captain captain = new Captain();
            JSONObject playerDetailsResult = null;
            try {
                playerDetailsResult = new JSONObject(playerDetailsFeed);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (playerDetailsResult != null) {
                captain.setId(query.getId());
                captain.setName(query.getName());
                captain.setServer(query.getServer());
                JSONObject data = playerDetailsResult.optJSONObject(DATA);
                if (data != null && data.has("" + query.getId())) {
                    JSONObject playerObject = data.optJSONObject("" + query.getId());
                    if(playerObject != null) {
                        boolean hidden = playerObject.optBoolean("hidden_profile");
                        if (!hidden) {
                            captain.setDetails(CaptainDetails.parse(playerObject));

                            JSONObject stats = playerObject.optJSONObject("statistics");

                            captain.setPveDetails(Statistics.parse(stats.optJSONObject("pve")));
                            captain.setPvpSoloDetails(Statistics.parse(stats.optJSONObject("pvp_solo")));
                            captain.setPvpDiv2Details(Statistics.parse(stats.optJSONObject("pvp_div2")));
                            captain.setPvpDiv3Details(Statistics.parse(stats.optJSONObject("pvp_div3")));
                            captain.setTeamBattleDetails(Statistics.parse(stats.optJSONObject("club")));

                            grabPrivateInformation(captain, playerObject);

                            grabShipInformationAndGraphs(query, playerShipsFeed, achievementsFeed, captain);

                            grabRankedInfo(query, rankedAccountFeed, captain);

                            grabRankedShipsInfo(query, rankedShipsFeed, captain);

                            grabClanInfo(captain, clanInfoFeed);

                            result.setHidden(false);
                        } else {
                            result.setHidden(true);
                        }
                    } else {
                    }
                    result.setCaptain(captain);
                }
            }
        }
        return result;
    }

    private void grabClanInfo(Captain captain, String clanInfoFeed) {
        JSONObject playerDetailsResult = null;
        try {
            playerDetailsResult = new JSONObject(clanInfoFeed);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(playerDetailsResult != null){
            JSONObject data = playerDetailsResult.optJSONObject(DATA);
            if(data != null){
                JSONObject playerObject = data.optJSONObject(captain.getId() + "");
                if(playerObject != null) {
                    JSONObject clan = playerObject.optJSONObject("clan");
                    if (clan != null) {
                        captain.setClanName(clan.optString("tag"));
                    }
                }
            }
        }
    }

    private void grabPrivateInformation(Captain captain, JSONObject playerObject) {
        JSONObject privInfo = playerObject.optJSONObject("private");
        if(privInfo != null){
            CaptainPrivateInformation information = new CaptainPrivateInformation();
            information.parse(privInfo);
            captain.setInformation(information);
        }
    }

    private void grabShipInformationAndGraphs(CaptainQuery query, String playerShipsFeed, String achievementsFeed, Captain captain) {
        if (!TextUtils.isEmpty(playerShipsFeed)) {
            JSONObject shipsResult = null;
            try {
                shipsResult = new JSONObject(playerShipsFeed);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (shipsResult != null) {
                JSONObject shipsData = shipsResult.optJSONObject(DATA);
                if (shipsData != null) {
                    JSONArray ships = shipsData.optJSONArray("" + query.getId());
                    captain.setShips(new ArrayList<Ship>());
                    float eDamage = 0, eWinRate = 0, eKills = 0
//                            , eXP = 0
                            , ePlanes = 0
//                            , eSurvial = 0, eSurWins = 0
                            ;
                    SparseArray<Float> ratingsPerTier = new SparseArray<>();
                    SparseArray<Float> shipsPerTier = new SparseArray<>(); // this is to average the ratings
                    SparseArray<Float> battlePerTier = new SparseArray<>();

                    int tiers = 0;
                    float totalCARating = 0;
                    float numOfShips = 0;
                    WarshipsStats stats = CAApp.getInfoManager().getShipStats(ctx);
                    ShipsHolder shipsMap = CAApp.getInfoManager().getShipInfo(ctx);
                    if (ships != null) {

                        float maxSurvivalRate = 0;
                        long maxSurvivalRateShipId = 0l;

                        float maxWinRate = 0;
                        long maxWinRateShipId = 0l;

                        int maxTotalKills = 0;
                        long maxTotalKillsShipId = 0l;

                        int maxPlayed = 0;
                        long maxPlayedShipId = 0l;

                        float maxCARating = 0;
                        long maxCARatingShipId = 0l;

                        int maxMostTraveled = 0;
                        long maxMostTraveledShipId = 0;

                        float maxAvgDmg = 0;
                        long maxAvgDmgShipId = 0l;

                        float maxTotalPlanes = 0;
                        long maxTotalPlanesShipId = 0l;

                        long maxTotalExp = 0;
                        long maxTotalExpShipId = 0l;

                        double maxTotalDamage = 0;
                        long maxTotalDmgShipId = 0l;

                        float maxSurvivedWins = 0;
                        long maxTotalSurWinsShipId = 0l;

                        float maxMBAccuracy = 0;
                        long maxMBAccuracyShipId = 0l;

                        float maxTBAccuracy = 0;
                        long maxTBAccuracyShipId = 0l;

                        double maxSpotted  = 0;
                        long maxSpottedShipId = 0l;

                        double maxDamageScouting = 0;
                        long maxDamageScoutingShipId = 0l;

                        double maxArgo = 0;
                        long maxArgoShipId = 0l;

                        double maxTorpArgo = 0;
                        long maxTorpArgoShipId = 0l;

                        double maxSuppressionCount = 0;
                        long maxSuppressionCountShipId = 0l;

                        for (int i = 0; i < ships.length(); i++) {
                            JSONObject ship = ships.optJSONObject(i);
                            Ship s = Ship.parse(ship);
                            ShipStat stat = stats.get(s.getShipId());
                            ShipInfo shipInfo = shipsMap.get(s.getShipId());
                            float battles = s.getBattles();
                            if(battles > 0 && shipInfo != null){
                                int tier = shipInfo.getTier();
                                tiers += (tier * battles);
                                if(stat != null){
                                    float rating = CARatingManager.CalculateCAShipRating(s, stat);
                                    s.setCARating(rating);
                                    addTierNumber(ratingsPerTier, tier, rating);
                                    totalCARating += rating;

                                    eDamage += stat.getDmg_dlt();
                                    eWinRate += stat.getWins();
                                    eKills += stat.getFrags();
                                    ePlanes += stat.getPls_kd();
                                }
                                addTierNumber(shipsPerTier, tier, 1);
                                addTierNumber(battlePerTier, tier, s.getBattles());

                                if (maxTotalKills < s.getFrags()) {
                                    maxTotalKills = s.getFrags();
                                    maxTotalKillsShipId = s.getShipId();
                                }
                                if (maxPlayed < s.getBattles()) {
                                    maxPlayed = s.getBattles();
                                    maxPlayedShipId = s.getShipId();
                                }
                                if (maxTotalPlanes < s.getPlanesKilled()) {
                                    maxTotalPlanes = s.getPlanesKilled();
                                    maxTotalPlanesShipId = s.getShipId();
                                }
                                if (maxTotalExp < s.getTotalXP()) {
                                    maxTotalExp = s.getTotalXP();
                                    maxTotalExpShipId = s.getShipId();
                                }
                                if (maxTotalDamage < s.getTotalDamage()) {
                                    maxTotalDamage = s.getTotalDamage();
                                    maxTotalDmgShipId = s.getShipId();
                                }
                                if (maxMostTraveled < s.getDistanceTraveled()) {
                                    maxMostTraveled = s.getDistanceTraveled();
                                    maxMostTraveledShipId = s.getShipId();
                                }
                                //                            float maxSpotted = 0;
                                if (maxSpotted < s.getMaxSpotted()) {
                                    maxSpotted = s.getMaxSpotted();
                                    maxSpottedShipId = s.getShipId();
                                }
                                //                            float maxDamageScouting = 0;
                                if (maxDamageScouting < s.getMaxDamageScouting()) {
                                    maxDamageScouting = s.getMaxDamageScouting();
                                    maxDamageScoutingShipId = s.getShipId();
                                }
                                //                            float maxArgo = 0;
                                if (maxArgo < s.getTotalArgoDamage()) {
                                    maxArgo = s.getTotalArgoDamage();
                                    maxArgoShipId = s.getShipId();
                                }
                                //                            float maxTorpArgo = 0;
                                if (maxTorpArgo < s.getTorpArgoDamage()) {
                                    maxTorpArgo = s.getTorpArgoDamage();
                                    maxTorpArgoShipId = s.getShipId();
                                }
                                //                            float maxSuppressionCount = 0;
                                if (maxSuppressionCount < s.getMaxSuppressionCount()) {
                                    maxSuppressionCount = s.getMaxSuppressionCount();
                                    maxSuppressionCountShipId = s.getShipId();
                                }
                                float avgDmg = (float) (s.getTotalDamage() / battles);
                                if (maxAvgDmg < avgDmg) {
                                    maxAvgDmg = avgDmg;
                                    maxAvgDmgShipId = s.getShipId();
                                }
                                if(s.getMainBattery().getShots() > 0){
                                    float mbAcc = s.getMainBattery().getHits() / (float) s.getMainBattery().getShots();
                                    if(maxMBAccuracy < mbAcc){
                                        maxMBAccuracy = mbAcc;
                                        maxMBAccuracyShipId = s.getShipId();
                                    }
                                }

                                if(s.getTorpedoes().getShots() > 0){
                                    float tbAcc = s.getTorpedoes().getHits() / (float) s.getTorpedoes().getShots();
                                    if(maxTBAccuracy < tbAcc){
                                        maxTBAccuracy = tbAcc;
                                        maxTBAccuracyShipId = s.getShipId();
                                    }
                                }
                                if (battles > 4) {
                                    float winRate = s.getWins() / battles;
                                    float survivalRate = s.getWins() / battles;
                                    float surviedWins = s.getSurvivedWins() / battles;
                                    if (maxWinRate < winRate) {
                                        maxWinRate = winRate;
                                        maxWinRateShipId = s.getShipId();
                                    }
                                    if (maxSurvivalRate < survivalRate) {
                                        maxSurvivalRate = survivalRate;
                                        maxSurvivalRateShipId = s.getShipId();
                                    }
                                    if (maxSurvivedWins < surviedWins) {
                                        maxSurvivedWins = surviedWins;
                                        maxTotalSurWinsShipId = s.getShipId();
                                    }
                                    //Calculate per ship number
                                    float carating = s.getCARating();
                                    if (maxCARating < carating) {
                                        maxCARating = carating;
                                        maxCARatingShipId = s.getShipId();
                                    }
                                }
                                numOfShips++;
                                captain.getShips().add(s);
                            }
                        }

                        CaptainDetails details = captain.getDetails();
                        if (details != null && numOfShips > 0 && details.getBattles() > 0) {

                            details.setMaxSurvivalRate(maxSurvivalRate);
                            details.setMaxSurvivalRateShipId(maxSurvivalRateShipId);

                            details.setMaxWinRate(maxWinRate);
                            details.setMaxWinRateShipId(maxWinRateShipId);

                            details.setMaxTotalKills(maxTotalKills);
                            details.setMaxTotalKillsShipId(maxTotalKillsShipId);

                            details.setMaxPlayed(maxPlayed);
                            details.setMaxPlayedShipId(maxPlayedShipId);

                            details.setMaxCARating(maxCARating);
                            details.setMaxCARatingShipId(maxCARatingShipId);

                            details.setMaxMostTraveled(maxMostTraveled);
                            details.setMaxMostTraveledShipId(maxMostTraveledShipId);

                            details.setMaxAvgDmg(maxAvgDmg);
                            details.setMaxAvgDmgShipId(maxAvgDmgShipId);

                            details.setMaxTotalPlanes(maxTotalPlanes);
                            details.setMaxTotalPlanesShipId(maxTotalPlanesShipId);

                            details.setMaxTotalExp(maxTotalExp);
                            details.setMaxTotalExpShipId(maxTotalExpShipId);

                            details.setMaxTotalDamage(maxTotalDamage);
                            details.setMaxTotalDmgShipId(maxTotalDmgShipId);

                            details.setMaxSurvivedWins(maxSurvivedWins);
                            details.setMaxSurvivedWinsShipId(maxTotalSurWinsShipId);

                            details.setMaxTBAccuracy(maxTBAccuracy);
                            details.setMaxTBAccuracyShipId(maxTBAccuracyShipId);

                            details.setMaxMBAccuracy(maxMBAccuracy);
                            details.setMaxMBAccuracyShipId(maxMBAccuracyShipId);

//                            float maxSpotted = 0;
                            details.setMaxSpotted(maxSpotted);
                            details.setMaxSpottedShipId(maxSpottedShipId);
//                            float maxDamageScouting = 0;
                            details.setMaxDamageScouting(maxDamageScouting);
                            details.setMaxDamageScoutingShipId(maxDamageScoutingShipId);
//                            float maxArgo = 0;
                            details.setMaxTotalArgo(maxArgo);
                            details.setMaxArgoDamageShipId(maxArgoShipId);
//                            float maxTorpArgo = 0;

                            Dlog.d("GetCaptain","torp = " + maxTorpArgo + " id = " + maxTorpArgoShipId);
                            details.setMaxTorpTotalArgo(maxTorpArgo);
                            details.setMaxTorpArgoDamageShipId(maxTorpArgoShipId);
//                            float maxSuppressionCount = 0;
                            details.setMaxSuppressionCount(maxSuppressionCount);
                            details.setMaxSuppressionShipId(maxSuppressionCountShipId);

                            float battles = details.getBattles();
                            details.setcDamage((float) ((details.getTotalDamage() / battles)));
                            details.setcWinRate(((details.getWins() / battles)));
                            details.setcCaptures(((details.getCapturePoints() / battles)));
                            details.setcDefReset(((details.getDroppedCapturePoints() / battles)));
                            details.setcKills(((details.getFrags() / battles)));
                            details.setcPlanes(((details.getPlanesKilled() / battles)));

//                            details.setcXP(((details.getTotalXP() / battles)));
//                            details.setcSurvival(((details.getSurvivedBattles() / battles)));
//                            details.setcSurWins(((details.getSurvivedWins() / battles)));

                            details.setExpectedDamage(eDamage / numOfShips);
                            details.setExpectedKills(eKills / numOfShips);
                            details.setExpectedPlanes(ePlanes / numOfShips);
                            details.setExpectedWinRate(eWinRate / numOfShips);

//                            details.setExpectedSurvival(eSurvial / total);
//                            details.setExpectedSurWins(eSurWins / total);
//                            details.setExpectedXP(eXP / total);

                            // average out the ratings with shipspertier. Ratingspertier / ships per tier.
                            // take every battle per tier and find ratio of games
                            // take ratio per tier of games and average rating per and times them.

                            float caRating = 0;
                            for( int i = 1; i <= shipsPerTier.size(); i++ ){
                                Float ratingTotal = ratingsPerTier.get(i);
                                Float shipsTotal = shipsPerTier.get(i);
                                Float battlesTotal = battlePerTier.get(i);

                                if(ratingTotal != null && shipsTotal != null && battlesTotal != null && battlesTotal > 0){
                                    // avgRating = total rating per tier / ship per tier
                                    float tierRatingAverage = ratingTotal / shipsTotal;
                                    // percentageRatio = total battles per tier / total games
                                    float tierRatio = battlesTotal / battles;
    //                                Dlog.d("CARating", tierRatingAverage + " ratio = " + tierRatio + " tier = " + i + " ratio = " + (tierRatingAverage * tierRatio));

                                    caRating += tierRatingAverage * tierRatio;
                                }
                            }
                            // rating += avgRating per tier * percentageRatio of games for tier

                            float averageTier = tiers / battles;
                            details.setAverageTier(averageTier);

//                            Dlog.wtf("Captain Rating", "rating = " + caRating);
                            details.setCARating(caRating);
                        }
                        Collections.sort(captain.getShips(), new ShipCompare().battlesComparator);
                    }
                }
                grabAchievements(query, achievementsFeed, captain);
            }
        }
    }


    private void grabRankedShipsInfo(CaptainQuery query, String rankedShipsFeed, Captain captain) {
        if (!TextUtils.isEmpty(rankedShipsFeed)) {
            JSONObject rankedShipsResult = null;
            try {
                rankedShipsResult = new JSONObject(rankedShipsFeed);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (rankedShipsResult != null) {
                JSONObject rankedShipData = rankedShipsResult.optJSONObject(DATA);
                JSONArray playerRankedShip = rankedShipData.optJSONArray(query.getId() + "");
                Map<Long, List<SeasonInfo>> seasonMap = new HashMap<Long, List<SeasonInfo>>();
                ShipsHolder shipsMap = CAApp.getInfoManager().getShipInfo(ctx);
                if (playerRankedShip != null) {
                    for (int i = 0; i < playerRankedShip.length(); i++) {
                        JSONObject obj = playerRankedShip.optJSONObject(i);
                        JSONObject seasons = obj.optJSONObject("seasons");
                        if (seasons.length() > 0) {
                            long shipId = obj.optLong("ship_id");
                            List<SeasonInfo> seasonList = new ArrayList<>();
                            Iterator<String> itea = seasons.keys();
                            while (itea.hasNext()) {
                                String key = itea.next();
                                JSONObject seasonObj = seasons.optJSONObject(key);
                                SeasonInfo sInfo = RankedInfo.parse(seasonObj);
                                sInfo.setSeasonNum(key);
                                try {
                                    String shipName = shipsMap.get(shipId).getName();
                                } catch (Exception e) {
                                }
                                seasonList.add(sInfo);
                            }
                            seasonMap.put(shipId, seasonList);
                        }
                    }

                    if (captain.getShips() != null) {
                        for (Ship s : captain.getShips()) {
                            List<SeasonInfo> info = seasonMap.get(s.getShipId());
                            if (info != null) {
                                s.setRankedInfo(info);
                            }
                        }
                    }
                }
            }
        }
    }

    public static void addTierNumber(SparseArray<Float> array, int key, float value){
        Float cKill = array.get(key);
        if(cKill == null){
            cKill = value;
        } else {
            cKill += value;
        }
        array.put(key, cKill);
    }

    private void grabRankedInfo(CaptainQuery query, String rankedAccountFeed, Captain captain) {
        if (!TextUtils.isEmpty(rankedAccountFeed)) {
            JSONObject rankedAccountResult = null;
            try {
                rankedAccountResult = new JSONObject(rankedAccountFeed);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (rankedAccountResult != null) {
                JSONObject rankedData = rankedAccountResult.optJSONObject(DATA);
                JSONObject playerRanked = rankedData.optJSONObject(query.getId() + "");
                if (playerRanked != null) {
                    JSONObject seasons = playerRanked.optJSONObject("seasons");
                    Iterator<String> iter = seasons.keys();
                    captain.setRankedSeasons(new ArrayList<RankedInfo>());
                    while (iter.hasNext()) {
                        String key = iter.next();
                        JSONObject season = seasons.optJSONObject(key);
                        RankedInfo seasonInfo = RankedInfo.parse(season);
                        seasonInfo.setSeasonNum(key);
                        try {
                            Integer seasonKey = Integer.parseInt(key);
                            seasonInfo.setSeasonInt(seasonKey);
                        } catch (NumberFormatException e) {
                        }
                        captain.getRankedSeasons().add(seasonInfo);
                    }
                }
            }
        }
    }

    private void grabAchievements(CaptainQuery query, String achievementsFeed, Captain captain) {
        if (!TextUtils.isEmpty(achievementsFeed)) {
            JSONObject achievementResult = null;
            try {
                achievementResult = new JSONObject(achievementsFeed);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (achievementResult != null) {
                JSONObject acheievementData = achievementResult.optJSONObject(DATA);
                if (acheievementData != null) {
                    JSONObject achievementsUser = acheievementData.optJSONObject("" + query.getId());
                    if (achievementsUser != null) {
                        JSONObject battleAchievements = achievementsUser.optJSONObject("battle");
                        JSONObject progressAchievements = achievementsUser.optJSONObject("progress");
                        captain.setAchievements(new ArrayList<Achievement>());
                        Iterator<String> iter = battleAchievements.keys();
                        while (iter.hasNext()) {
                            String key = iter.next();
                            AddAchievement(captain, battleAchievements, key);
                        }

                        Iterator<String> iter2 = progressAchievements.keys();
                        while (iter2.hasNext()) {
                            String key = iter2.next();
                            AddAchievement(captain, progressAchievements, key);
                        }

                    }
                }
            }
        }
    }

    private void AddAchievement(Captain captain, JSONObject battleAchievements, String key) {
        int number = battleAchievements.optInt(key);
        Achievement achi = new Achievement();
        achi.setName(key);
        achi.setNumber(number);
        captain.getAchievements().add(achi);
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


    @Override
    protected void onPostExecute(CaptainResult captainResult) {
        super.onPostExecute(captainResult);
        CAApp.getEventBus().post(captainResult);
    }

    public Context getCtx() {
        return ctx;
    }

    public void setCtx(Context ctx) {
        this.ctx = ctx;
    }
}
