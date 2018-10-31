package com.half.wowsca.backend;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.half.wowsca.CAApp;
import com.half.wowsca.model.encyclopedia.holders.AchievementsHolder;
import com.half.wowsca.model.encyclopedia.holders.CaptainSkillHolder;
import com.half.wowsca.model.encyclopedia.holders.ExteriorHolder;
import com.half.wowsca.model.encyclopedia.holders.ShipsHolder;
import com.half.wowsca.model.encyclopedia.holders.UpgradeHolder;
import com.half.wowsca.model.encyclopedia.holders.WarshipsStats;
import com.half.wowsca.model.encyclopedia.items.AchievementInfo;
import com.half.wowsca.model.encyclopedia.items.CaptainSkill;
import com.half.wowsca.model.encyclopedia.items.EquipmentInfo;
import com.half.wowsca.model.encyclopedia.items.ExteriorItem;
import com.half.wowsca.model.encyclopedia.items.ShipInfo;
import com.half.wowsca.model.encyclopedia.items.ShipStat;
import com.half.wowsca.model.enums.Server;
import com.half.wowsca.model.queries.InfoQuery;
import com.half.wowsca.model.result.InfoResult;
import com.utilities.Utils;
import com.utilities.logging.Dlog;
import com.utilities.preferences.Prefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by slai4 on 9/21/2015.
 */
public class GetNeededInfoTask extends AsyncTask<InfoQuery, Void, InfoResult> {

    public static final String SAVED_FRESH_DATA = "SAVED_FRESH_DATA";
    private Context ctx;

    @Override
    protected InfoResult doInBackground(InfoQuery... params) {

        String languagePart = "&language=" + CAApp.getServerLanguage(ctx);
        InfoQuery query = params[0];
        InfoResult result = new InfoResult();
        String url = CAApp.WOWS_API_SITE_ADDRESS + query.getServer().getSuffix() + "/wows/encyclopedia/ships/?application_id=" + query.getServer().getAppId() + languagePart;
        Dlog.wtf("SHIPS URL", url);
        String shipFeed = getURLResult(url);

        String url2 = CAApp.WOWS_API_SITE_ADDRESS + query.getServer().getSuffix() + "/wows/encyclopedia/achievements/?application_id=" + query.getServer().getAppId() + languagePart;
        Dlog.wtf("ACHIEVEMENTS URL", url2);
        String achievementFeed = getURLResult(url2);

        String url4 = CAApp.WOWS_API_SITE_ADDRESS + query.getServer().getSuffix() + "/wows/encyclopedia/consumables/?application_id=" + query.getServer().getAppId() + languagePart + "&type=Modernization";
        Dlog.wtf("UPGRADES URL", url4);
        String upgradesFeed = getURLResult(url4);

        String url5 = CAApp.WOWS_API_SITE_ADDRESS + query.getServer().getSuffix() + "/wows/encyclopedia/crewskills/?application_id=" + query.getServer().getAppId() + languagePart;
        Dlog.wtf("SKILLS URL", url5);
        String captainSkillsFeed = getURLResult(url5);

        String url6 = CAApp.WOWS_API_SITE_ADDRESS + query.getServer().getSuffix() + "/wows/encyclopedia/consumables/?application_id=" + query.getServer().getAppId() + languagePart + "&type=Flags";
        Dlog.wtf("EXTERIOR URL", url6);
        String exteriorItemsFeed = getURLResult(url6);

//        Server s = CAApp.getServerType(ctx);
//        String url7 = "https://api." + s.getWarshipsToday() + ".warships.today/json/wows/ratings/warships-today-rating/coefficients";
//        Dlog.wtf("WARSHIPS_TODAY URL", url7);
//        String WARSHIPS_TODAY = getURLResult(url7);

        Server s = CAApp.getServerType(ctx);
        String prefix = "";
        switch(s){
            case NA:
                prefix = "na";
                break;
            case EU:
                break;
            case RU:
                prefix = "ru";
                break;
            case SEA:
                prefix = "asia";
                break;
            case KR:
                prefix = "asia";
                break;
        }
        String url7 = "https://" + prefix + ".wows-numbers.com/ships";
        Dlog.wtf("WARSHIPS_TODAY URL", url7);
        String wowsNumbers = getURLResult(url7);

        parseShipInformation(query, result, shipFeed, languagePart);

        parseAchievementInfo(result, achievementFeed);

        parseUpgrades(result, upgradesFeed);

        parseCaptainSkills(result, captainSkillsFeed);

        parseExteriorItems(result, exteriorItemsFeed);

        parseWoWsNumbers(result, wowsNumbers);

        //save ships and infos to InfoManager
        if (result.getShips() != null && !result.getShips().isEmpty()) {
            ShipsHolder shipsHolder = new ShipsHolder();
            shipsHolder.setItems(result.getShips());
            CAApp.getInfoManager().setShipInfo(ctx, shipsHolder);
        }

        if (result.getAchievements() != null && !result.getAchievements().isEmpty()) {
            AchievementsHolder achi = new AchievementsHolder();
            achi.setItems(result.getAchievements());
            CAApp.getInfoManager().setAchievements(ctx, achi);
        }

        if (result.getShipStat() != null && !result.getShipStat().isEmpty()) {
            WarshipsStats stat = new WarshipsStats();
            stat.set(result.getShipStat());
            CAApp.getInfoManager().setWarshipsStats(ctx, stat);
        }

        if (result.getEquipment() != null && !result.getEquipment().isEmpty()) {
            UpgradeHolder e = new UpgradeHolder();
            e.setItems(result.getEquipment());
            CAApp.getInfoManager().setUpgrades(ctx, e);
        }

        if (result.getExteriorItem() != null && !result.getExteriorItem().getItems().isEmpty()){
            CAApp.getInfoManager().setExteriorItems(ctx, result.getExteriorItem());
        }

        if(result.getSkillHolder() != null && !result.getSkillHolder().getItems().isEmpty()){
            CAApp.getInfoManager().setCaptainSkills(ctx, result.getSkillHolder());
        }
        Dlog.wtf("Infomanager", "done " + result);

        CAApp.getInfoManager().updated(ctx);
        return result;
    }

    private void parseWoWsNumbers(InfoResult result, String wowsNumbers) {
        try {
            Document doc = Jsoup.parse(wowsNumbers);
            Elements scriptTags = doc.getElementsByTag("script");
            for (Element tag : scriptTags){
                for (DataNode node : tag.dataNodes()) {
                    String nodeData = node.getWholeData();
                    if(nodeData.contains("var dataProvider")) {
                        String[] dataSplit = nodeData.split("=");
                        String ships = dataSplit[2];
                        String[] secondSplit = ships.split(";");
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < secondSplit.length - 1; i++) {
                            sb.append(secondSplit[i]);
                        }
                        JSONArray list = new JSONArray(sb.toString());
                        Map<Long, ShipStat> shipStatMap = new HashMap<Long, ShipStat>();
                        for (int i = 0; i < list.length(); i++) {
                            JSONObject object = list.optJSONObject(i);
                            ShipStat stat = new ShipStat();
                            Long shipId = object.optLong("ship_id");
                            stat.setDmg_dlt(object.optInt("average_damage_dealt"));
                            stat.setFrags((float) object.optDouble("average_frags"));
                            stat.setPls_kd((float) object.optDouble("average_planes_killed"));
                            stat.setWins(((float) object.optDouble("win_rate")) / 100);
                            if (stat.getDmg_dlt() != -1)
                                shipStatMap.put(shipId, stat);
                        }
                        result.setShipStat(shipStatMap);
                        if(shipStatMap.size() > 250){
                            try {
                                Prefs prefs = new Prefs(ctx);
                                prefs.setString(SAVED_FRESH_DATA, sb.toString());
                                Dlog.d("GetNeededInfo", "Saved Info");
                            } catch (OutOfMemoryError e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            setupDefaultData(result);
        }
    }

    private void setupDefaultData(InfoResult result){
        // last updated 9/5/18
        Server s = CAApp.getServerType(ctx);
        String fileName = "";
        switch(s){
            case NA:
                fileName = "raw-data-na.txt";
                break;
            case EU:
                fileName = "raw-data-eu.txt";
                break;
            case RU:
                fileName = "raw-data-ru.txt";
                break;
            case SEA:
                fileName = "raw-data-asia.txt";
                break;
            case KR:
                fileName = "raw-data-asia.txt";
                break;
        }

        Prefs prefs = new Prefs(ctx);
        String lastOutput = prefs.getString(SAVED_FRESH_DATA, "");
        StringBuilder sb = new StringBuilder();
        if(TextUtils.isEmpty(lastOutput)) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(
                        new InputStreamReader(ctx.getAssets().open(fileName)));

                // do reading, usually loop until end of file reading
                String mLine;
                while ((mLine = reader.readLine()) != null) {
                    //process line
                    sb.append(mLine);
                }
            } catch (IOException e) {
                //log the exception
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        //log the exception
                    }
                }
            }
        } else {
            sb.append(lastOutput);
        }
        String output = sb.toString();
        if(!TextUtils.isEmpty(output)){
            try {
                JSONArray body = new JSONArray(output);
                Map<Long, ShipStat> shipStatMap = new HashMap<Long, ShipStat>();
                for (int i = 0; i < body.length(); i++) {
                    JSONObject object = body.optJSONObject(i);
                    ShipStat stat = new ShipStat();
                    Long shipId = object.optLong("ship_id");
                    stat.setDmg_dlt(object.optInt("average_damage_dealt"));
                    stat.setFrags((float) object.optDouble("average_frags"));
                    stat.setPls_kd((float) object.optDouble("average_planes_killed"));
                    stat.setWins(((float) object.optDouble("win_rate")) / 100);
                    if (stat.getDmg_dlt() != -1)
                        shipStatMap.put(shipId, stat);
                }
                result.setShipStat(shipStatMap);
            } catch (JSONException e) {
            }
        }
    }

    private void parseWarshipsToday(InfoResult result, String warships_today) {
        if(warships_today != null) {
            JSONObject object = null;
            try {
                object = new JSONObject(warships_today);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (object != null) {
                    JSONArray data = object.optJSONArray("expected");
                    Map<Long, ShipStat> shipStatMap = new HashMap<Long, ShipStat>();
                    for(int i = 0; i < data.length(); i++){
                        JSONObject obj = data.optJSONObject(i);
                        long shipId = obj.optLong("ship_id");
                        ShipStat info = new ShipStat();
                        info.parse(obj);
                        shipStatMap.put(shipId, info);
                    }
                    result.setShipStat(shipStatMap);
            }
        }
    }

    private void parseCaptainSkills(InfoResult result, String captainSkillsFeed) {
        if(captainSkillsFeed != null){
            JSONObject object = null;
            try {
                object = new JSONObject(captainSkillsFeed);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(object != null){
                JSONObject data = object.optJSONObject("data");
                if (data != null) {
                    Iterator<String> iter = data.keys();
                    CaptainSkillHolder exteriorItems = new CaptainSkillHolder();
                    while (iter.hasNext()){
                        String key = iter.next();
                        JSONObject item = data.optJSONObject(key);
                        CaptainSkill skill = new CaptainSkill();
                        skill.setId(Long.parseLong(key));
                        skill.parse(item);
                        exteriorItems.put(key, skill);
                    }
                    result.setSkillHolder(exteriorItems);
                }
            }
        }
    }

    private void parseExteriorItems(InfoResult result, String exteriorItemsFeed) {
        if(exteriorItemsFeed != null){
            JSONObject object = null;
            try {
                object = new JSONObject(exteriorItemsFeed);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(object != null){
                JSONObject data = object.optJSONObject("data");
                if (data != null) {
                    Iterator<String> iter = data.keys();
                    ExteriorHolder exteriorItems = new ExteriorHolder();
                    while (iter.hasNext()){
                        String key = iter.next();
                        JSONObject item = data.optJSONObject(key);
                        ExteriorItem exteriorItem = new ExteriorItem();
                        exteriorItem.parse(item);
                        exteriorItems.put(exteriorItem.getId(), exteriorItem);
                    }
                    result.setExteriorItem(exteriorItems);
                }
            }
        }
    }

    private void parseUpgrades(InfoResult result, String upgradesFeed) {
        if (upgradesFeed != null) {
            JSONObject object = null;
            try {
                object = new JSONObject(upgradesFeed);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (object != null) {
                JSONObject data = object.optJSONObject("data");
                if (data != null) {
                    Map<Long, EquipmentInfo> equipmentInfo = new HashMap<Long, EquipmentInfo>();
                    Iterator<String> keys = data.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        long id = Long.parseLong(key);
                        JSONObject equipment = data.optJSONObject(key);
                        if (equipment != null) {
                            EquipmentInfo info = new EquipmentInfo();
                            info.parse(equipment);
                            info.setId(id);
                            equipmentInfo.put(id, info);
                        }
                    }
                    result.setEquipment(equipmentInfo);
                }
            }
        }
    }

    private void parseAverageData(InfoResult result, String warshipstatsFeed) {
        if (warshipstatsFeed != null) {
            JSONObject object = null;
            try {
                object = new JSONObject(warshipstatsFeed);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (object != null) {
                JSONObject data = object.optJSONObject("data");
                Map<Long, ShipStat> shipStatMap = new HashMap<Long, ShipStat>();
                Iterator<String> keys = data.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    JSONObject shipStat = data.optJSONObject(key);
                    if (shipStat != null) {
                        ShipStat info = new ShipStat();
                        info.parse(shipStat);
                        shipStatMap.put(Long.parseLong(key), info);
                    }
                }
                result.setShipStat(shipStatMap);
            }
        }
    }

    private void parseAchievementInfo(InfoResult result, String achievementFeed) {
        if (achievementFeed != null) {
            JSONObject object = null;
            try {
                object = new JSONObject(achievementFeed);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (object != null) {
                JSONObject data = object.optJSONObject("data");
                if (data != null) {
                    JSONObject battle = data.optJSONObject("battle");
                    if(battle != null) {
                        Map<String, AchievementInfo> achievementInfo = new HashMap<String, AchievementInfo>();
                        Iterator<String> keys = battle.keys();
                        while (keys.hasNext()) {
                            String key = keys.next();
                            JSONObject achievement = battle.optJSONObject(key);
                            if (achievement != null) {
                                AchievementInfo info = new AchievementInfo();
                                info.setName(achievement.optString("name"));
                                info.setDescription(achievement.optString("description"));
                                info.setImage(achievement.optString("image"));
                                info.setId(key);
                                achievementInfo.put(key, info);
                            }
                        }
                        result.setAchievements(achievementInfo);
                    }
                }
            }
        }
    }

    private void parseShipInformation(InfoQuery query, InfoResult result, String shipFeed, String languagePart) {
        if (shipFeed != null) {
            JSONObject object = null;
            try {
                object = new JSONObject(shipFeed);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (object != null) {
                JSONObject meta = object.optJSONObject("meta");
                int page = meta.optInt("page");
                int pageTotal = meta.optInt("page_total");
                page++;

                List<JSONObject> feeds = new ArrayList<>();
                JSONObject data = object.optJSONObject("data");
                feeds.add(data);
                while (page <= pageTotal){
                    String url = CAApp.WOWS_API_SITE_ADDRESS + query.getServer().getSuffix() + "/wows/encyclopedia/ships/?application_id=" + query.getServer().getAppId() + languagePart + "&page_no=" + page;
                    Dlog.wtf("SHIPS URL AGAIN", url);
                    String feed = getURLResult(url);
                    JSONObject obj = null;
                    try {
                        obj = new JSONObject(feed);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if(obj != null) {
                        JSONObject datum = obj.optJSONObject("data");
                        feeds.add(datum);
                    }
                    page++;
                }
                Map<Long, ShipInfo> ships = new HashMap<>();
                for(JSONObject datum : feeds) {
                    if (datum != null) {
                        Iterator<String> keys = datum.keys();
                        while (keys.hasNext()) {
                            String key = keys.next();
                            long id = Long.parseLong(key);
                            JSONObject ship = datum.optJSONObject(key);
                            if (ship != null) {
                                ShipInfo info = new ShipInfo();
                                info.parse(ship);
                                info.setShipId(id);
                                ships.put(id, info);
                            }
                        }
                    }
                }
                result.setShips(ships);
            }
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

    @Override
    protected void onPostExecute(InfoResult shipsResult) {
        super.onPostExecute(shipsResult);
        CAApp.getEventBus().post(shipsResult);
    }

    public void setCtx(Context ctx) {
        this.ctx = ctx;
    }
}
