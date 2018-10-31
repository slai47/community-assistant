package com.clanassist.backend.Tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.clanassist.CAApp;
import com.clanassist.model.ServerInfo;
import com.clanassist.model.ServerResult;
import com.clanassist.model.enums.Server;
import com.crashlytics.android.Crashlytics;
import com.utilities.Utils;
import com.utilities.logging.Dlog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

/**
 * Created by slai4 on 12/2/2015.
 */
public class GetServerInfo extends AsyncTask<String, Void, ServerResult> {

    private Context ctx;

    public GetServerInfo(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    protected ServerResult doInBackground(String... params) {
        ServerResult result = new ServerResult();
        String startUrl = "https://api.worldoftanks";
        String url = startUrl + Server.US.getSuffix() + "/wgn/servers/info/?application_id=" + Server.US.getApplicationId();
        Dlog.wtf("server URL", url);

        String url2 = startUrl + Server.EU.getSuffix() + "/wgn/servers/info/?application_id=" + Server.EU.getApplicationId();
        Dlog.wtf("server URL", url2);

        String url3 = startUrl + Server.RU.getSuffix() + "/wgn/servers/info/?application_id=" + Server.RU.getApplicationId();
        Dlog.wtf("server URL", url3);

        String url4 = startUrl + Server.SEA.getSuffix() + "/wgn/servers/info/?application_id=" + Server.SEA.getApplicationId();
        Dlog.wtf("server URL", url4);

        String naFeed = getURLResult(url);
        String euFeed = getURLResult(url2);
        String ruFeed = getURLResult(url3);
        String seaFeed = getURLResult(url4);

        parseRegion(naFeed, result, Server.US);
        parseRegion(euFeed, result, Server.EU);
        parseRegion(ruFeed, result, Server.RU);
        parseRegion(seaFeed, result, Server.SEA);

        return result;
    }

    @Override
    protected void onPostExecute(ServerResult server) {
        super.onPostExecute(server);
        CAApp.getEventBus().post(server);
    }

    private void parseRegion(String feed, ServerResult result, Server s) {
        JSONObject res = null;
        try {
            res = new JSONObject(feed);
        } catch (JSONException e) {
        }

        if (res != null) {
            JSONObject data = res.optJSONObject("data");
            if (data != null) {
                JSONArray wot = data.optJSONArray("wot");
                JSONArray wows = data.optJSONArray("wotb");
                if (wot != null) {
                    for (int i = 0; i < wot.length(); i++) {
                        JSONObject obj = wot.optJSONObject(i);
                        ServerInfo info = new ServerInfo();
                        info.setName(obj.optString("server"));
                        info.setPlayers(obj.optInt("players_online"));
                        info.setServer(s);
                        result.getWotNumbers().add(info);
                    }
                }
                if (wows != null) {
                    for (int i = 0; i < wows.length(); i++) {
                        JSONObject obj = wows.optJSONObject(i);
                        ServerInfo info = new ServerInfo();
                        info.setName(obj.optString("server"));
                        info.setPlayers(obj.optInt("players_online"));
                        info.setServer(s);
                        result.getWowsNumbers().add(info);
                    }
                }
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

}
