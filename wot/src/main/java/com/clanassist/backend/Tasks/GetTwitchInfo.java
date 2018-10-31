package com.clanassist.backend.Tasks;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.clanassist.CAApp;
import com.clanassist.model.TwitchObj;
import com.utilities.Utils;
import com.utilities.logging.Dlog;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

/**
 * Created by slai4 on 12/3/2015.
 */
public class GetTwitchInfo extends AsyncTask<String, Void, TwitchObj> {
    @Override
    protected TwitchObj doInBackground(String... params) {
        TwitchObj obj = new TwitchObj(params[0]);

        String url = "https://api.twitch.tv/kraken/streams/" + params[0];
        String url2 = "https://api.twitch.tv/kraken/channels/" + params[0];
        Dlog.wtf("Stream URL", url);

        String feed = getURLResult(url);

        if (!TextUtils.isEmpty(feed)) {
            JSONObject result = null;
            try {
                result = new JSONObject(feed);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (result != null) {
                JSONObject stream = result.optJSONObject("stream");
                if (stream != null) {
                    obj.setLive(true);
                    obj.setGamePlaying(stream.optString("game"));

                    JSONObject preview = stream.optJSONObject("preview");
                    obj.setThumbnail(preview.optString("large"));

                    JSONObject channel = stream.optJSONObject("channel");
                    obj.setLogo(channel.optString("logo"));
                    obj.setStreamName(channel.optString("status"));

                } else {
                    obj.setLive(false);
                    String channelInfoFeed = getURLResult(url2);
                    Dlog.wtf("Channel URL", url2);
                    JSONObject channelResult = null;
                    try {
                        channelResult = new JSONObject(channelInfoFeed);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (channelInfoFeed != null) {
                        obj.setThumbnail(channelResult.optString("video_banner"));
                        obj.setLogo(channelResult.optString("logo"));
                    } else {
                    }
                }
            } else {
                obj.setLive(false);
            }
        }
        return obj;
    }


    @Override
    protected void onPostExecute(TwitchObj twitchObj) {
        super.onPostExecute(twitchObj);
        CAApp.getEventBus().post(twitchObj);
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
