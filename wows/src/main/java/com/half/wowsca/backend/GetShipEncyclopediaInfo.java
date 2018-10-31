package com.half.wowsca.backend;

import android.os.AsyncTask;
import android.text.TextUtils;


import com.half.wowsca.CAApp;
import com.half.wowsca.model.queries.ShipQuery;
import com.half.wowsca.model.result.ShipResult;
import com.utilities.Utils;
import com.utilities.logging.Dlog;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

/**
 * Created by slai4 on 11/1/2015.
 */
public class GetShipEncyclopediaInfo extends AsyncTask<ShipQuery, Void, ShipResult> {


    public static final String ARTILLERY = "artillery";
    public static final String TORPEDOES = "torpedoes";
    public static final String FIRE_CONTROL = "fire_control";
    public static final String FLIGHT_CONTROL = "flight_control";
    public static final String HULL = "hull";
    public static final String ENGINE = "engine";
    public static final String FIGHTER = "fighter";
    public static final String DIVE_BOMBER = "dive_bomber";
    public static final String TORPEDO_BOMBER = "torpedo_bomber";

    @Override
    protected ShipResult doInBackground(ShipQuery... params) {
        ShipQuery query = params[0];
        ShipResult result = new ShipResult();

        StringBuilder sb = new StringBuilder();
        sb.append(CAApp.WOWS_API_SITE_ADDRESS);
        sb.append(query.getServer().getSuffix());
        sb.append("/wows/encyclopedia/shipprofile/");
        sb.append("?application_id=" + query.getServer().getAppId());
        sb.append("&ship_id=" + query.getShipId());
        sb.append("&language=" + query.getLanguage());
        if(query.getModules() != null) {
            // artillery id
            Long artillery = query.getModules().get(ARTILLERY);
            if (artillery != null && artillery != 0l) {
                sb.append("&artillery_id=" + artillery);
            }
            //torpedoes id
            Long torps = query.getModules().get(TORPEDOES);
            if (torps != null && torps != 0l) {
                sb.append("&torpedoes_id=" + torps);
            }
            //fire control id
            Long fireControl = query.getModules().get(FIRE_CONTROL);
            if (fireControl != null && fireControl != 0l) {
                sb.append("&fire_control_id=" + fireControl);
            }
            //flight control id
            Long flightControl = query.getModules().get(FLIGHT_CONTROL);
            if (flightControl != null && flightControl != 0l) {
                sb.append("&flight_control_id=" + flightControl);
            }
            //hull id
            Long hull = query.getModules().get(HULL);
            if (hull != null && hull != 0l) {
                sb.append("&hull_id=" + hull);
            }
            //engine id
            Long engine = query.getModules().get(ENGINE);
            if (engine != null && engine != 0l) {
                sb.append("&engine_id=" + engine);
            }
            //fighter id
            Long fighter = query.getModules().get(FIGHTER);
            if (fighter != null && fighter != 0l) {
                sb.append("&fighter_id=" + fighter);
            }
            //dive bomber id
            Long diveBomber = query.getModules().get(DIVE_BOMBER);
            if (diveBomber != null && diveBomber != 0l) {
                sb.append("&dive_bomber_id=" + diveBomber);
            }
            //torpedo bomber id
            Long torpBomber = query.getModules().get(TORPEDO_BOMBER);
            if (torpBomber != null && torpBomber != 0l) {
                sb.append("&torpedo_bomber_id=" + torpBomber);
            }
        }
        String url = sb.toString();
        Dlog.wtf("SHIPINFO URL", url);

        String shipFeed = getURLResult(url);
        result.setShipId(query.getShipId());
        //parse this
        if (!TextUtils.isEmpty(shipFeed)) {
            JSONObject feed = null;
            try {
                feed = new JSONObject(shipFeed);
            } catch (JSONException e) {
            }
            if (feed != null) {
                JSONObject data = feed.optJSONObject("data");
                if(data != null) {
                    JSONObject ship = data.optJSONObject(query.getShipId() + "");
                    if (ship != null)
                        result.setShipInfo(ship.toString());
                }
            }
        }
        return result;
    }


    @Override
    protected void onPostExecute(ShipResult shipResult) {
        super.onPostExecute(shipResult);
        CAApp.getEventBus().post(shipResult);
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

}
