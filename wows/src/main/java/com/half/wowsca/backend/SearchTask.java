package com.half.wowsca.backend;

import android.os.AsyncTask;
import android.text.TextUtils;


import com.half.wowsca.CAApp;
import com.half.wowsca.model.Captain;
import com.half.wowsca.model.queries.SearchQuery;
import com.half.wowsca.model.result.SearchResults;
import com.utilities.Utils;
import com.utilities.logging.Dlog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

/**
 * Created by slai4 on 9/19/2015.
 */
public class SearchTask extends AsyncTask<SearchQuery, Void, SearchResults> {

    @Override
    protected SearchResults doInBackground(SearchQuery... params) {
        SearchQuery query = params[0];
        SearchResults results = new SearchResults();
        String url = CAApp.WOWS_API_SITE_ADDRESS + query.getServer().getSuffix() + "/wows/account/list/?application_id=" + query.getServer().getAppId() + "&search=" + query.getSearch();
        Dlog.wtf("Search URL", url);

        //send url
        String feed = getURLResult(url);

        //parse
        if (!TextUtils.isEmpty(feed)) {
            JSONObject feedJSON = null;
            try {
                feedJSON = new JSONObject(feed);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (feedJSON != null) {
                JSONArray users = feedJSON.optJSONArray("data");
                if (users != null)
                    for (int i = 0; i < users.length(); i++) {
                        JSONObject user = users.optJSONObject(i);
                        if (user != null) {
                            Captain c = new Captain();
                            c.setId(user.optLong("account_id"));
                            c.setName(user.optString("nickname"));
                            c.setServer(query.getServer());
                            results.getCaptains().add(c);
                        }
                    }
            }
        }
        return results;
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
    protected void onPostExecute(SearchResults searchResults) {
        super.onPostExecute(searchResults);
        CAApp.getEventBus().post(searchResults);
    }
}
