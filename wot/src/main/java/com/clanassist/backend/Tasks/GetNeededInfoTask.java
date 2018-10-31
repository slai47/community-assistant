package com.clanassist.backend.Tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.clanassist.CAApp;
import com.clanassist.model.events.InfoResult;
import com.clanassist.model.infoobj.Achievement;
import com.clanassist.model.infoobj.Achievements;
import com.clanassist.model.infoobj.InfoQuery;
import com.clanassist.model.infoobj.Tank;
import com.clanassist.model.infoobj.Tanks;
import com.crashlytics.android.Crashlytics;
import com.utilities.Utils;
import com.utilities.logging.Dlog;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by slai4 on 2/25/2016.
 */
public class GetNeededInfoTask extends AsyncTask<InfoQuery, Void, InfoResult>{

    private Context ctx;

    @Override
    protected InfoResult doInBackground(InfoQuery... params) {
        String serverLanguage = "&language=" + CAApp.getServerLanguage(ctx);
        String appId = CAApp.getApplicationIdURLString(ctx);
        String webAddress = CAApp.getBaseAddress(ctx);

        //set up tanks call
        String url = webAddress + "/wot/encyclopedia/vehicles/?" + appId + serverLanguage + "&fields=type,name,nation,is_premium,tier,is_gift,images,images";
        Dlog.wtf("TANKS URL", url);
        Crashlytics.setString("ENCYCLOPEDIA URL", url);
        String tanksFeed = getURLResult(url);

        // set up achievement info call
        String url2 = webAddress + "/wot/encyclopedia/achievements/?" + appId + serverLanguage;
        Dlog.wtf("ACHIEVEMENT URL", url2);
        Crashlytics.setString("ACHIEVEMENT URL", url);
        String achievementFeed = getURLResult(url2);

        //parse tanks
        Map<Integer, Tank> tankMap= null;
        if(tanksFeed != null){
            JSONObject result = null;
            try {
                result = new JSONObject(tanksFeed);
            } catch (JSONException e) {
            }
            if(result != null){
                JSONObject vehicles = result.optJSONObject("data");
                if(vehicles != null){
                    tankMap = new HashMap<Integer, Tank>();
                    Iterator<String> iter = vehicles.keys();
                    while (iter.hasNext()){
                        String key = iter.next();
                        JSONObject obj = vehicles.optJSONObject(key);
                        int tank_id = Integer.parseInt(key);
                        if(obj != null){
                            Tank tank = new Tank();
                            tank.setClassType(obj.optString("type"));
                            tank.setId(tank_id);
                            tank.setName(obj.optString("name"));
                            tank.setNation(obj.optString("nation"));
                            tank.setTier(obj.optInt("tier"));
                            tank.setIsPremium(obj.optBoolean("is_premium"));
                            tank.setIsGift(obj.optBoolean("is_gift"));
                            JSONObject images = obj.optJSONObject("images");
                            if(images != null){
                                tank.setImage(images.optString("small_icon"));
                                tank.setLargeImage(images.optString("big_icon"));
                                tank.setContour(images.optString("contour_icon"));
                            }
                            tankMap.put(tank_id, tank);
                        }
                    }
                }
            }
        }


        //parse achievements
        Map<String, Achievement> achievementMap = null;
        if(achievementFeed != null){
            JSONObject result = null;
            try {
                result = new JSONObject(achievementFeed);
            } catch (JSONException e) {
            }
            if(result != null){
                JSONObject achievements = result.optJSONObject("data");
                if(achievements != null){
                    Iterator<String> iter = achievements.keys();
                    achievementMap = new HashMap<String, Achievement>();
                    while (iter.hasNext()){
                        String key = iter.next();
                        JSONObject obj = achievements.optJSONObject(key);
                        if(obj != null){
                            Achievement achievement = new Achievement();
                            achievement.setName(obj.optString("name_i18n"));
                            achievement.setCondition(obj.optString("condition"));
                            achievement.setDescription(obj.optString("description"));
                            achievement.setInfo(obj.optString("hero_info"));
                            achievement.setOrder(obj.optInt("order"));
                            achievement.setSectionOrder(obj.optInt("section_order"));
                            achievement.setType(obj.optString("type"));
                            achievement.setImage(obj.optString("image"));
                            achievement.setBigImage(obj.optString("image_big"));
                            achievement.setSection(obj.optString("section_i18n"));
                            if(obj.optJSONArray("options") == null || (obj.optJSONArray("options") != null && obj.optJSONArray("options").length() == 0))
                                achievementMap.put(key, achievement);
//                            Dlog.d("GetNeededInformation", "key = " + key + " achi = " + (obj.optJSONArray("options") == null) + " " + (obj.optJSONArray("options") != null && obj.optJSONArray("options").length() == 0) + " map = " + achievementMap.get(key) );
                        }
                    }
                }
            }
        }

        //add them to infomanager
        if (tankMap != null) {
            Tanks tanks = new Tanks();
            tanks.setTanksList(tankMap);
            CAApp.getInfoManager().setTanks(ctx, tanks);
        }
        //add achievements to infomanage
        if(achievementMap != null){
            Achievements achievements = new Achievements();
            achievements.setAchievements(achievementMap);
            CAApp.getInfoManager().setAchievements(ctx, achievements);
        }

        return new InfoResult();
    }

    @Override
    protected void onPostExecute(InfoResult infoResult) {
        super.onPostExecute(infoResult);
        CAApp.getEventBus().post(infoResult);
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

    public void setCtx(Context ctx) {
        this.ctx = ctx;
    }
}
