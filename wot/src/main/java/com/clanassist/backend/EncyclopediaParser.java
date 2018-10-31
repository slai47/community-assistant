package com.clanassist.backend;

import android.content.Context;

import com.clanassist.SVault;
import com.clanassist.model.Factory;
import com.clanassist.model.encyclopedia.Map;
import com.clanassist.model.search.enums.EncyclopediaType;
import com.clanassist.model.search.queries.EncyclopediaQuery;
import com.clanassist.model.search.results.EncyclopediaResult;
import com.crashlytics.android.Crashlytics;
import com.utilities.Utils;
import com.utilities.interfaces.IParser;
import com.utilities.logging.Dlog;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Harrison on 4/4/2015.
 */
public class EncyclopediaParser implements IParser<EncyclopediaQuery, EncyclopediaResult> {

    public Context ctx;

    public EncyclopediaParser(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public EncyclopediaResult parse(EncyclopediaQuery... queries) {
        EncyclopediaResult er = new EncyclopediaResult();
        List<String> results = new ArrayList<String>();
        for (EncyclopediaQuery q : queries) {
            if (q != null) {
                Crashlytics.setString(SVault.LOG_ENCYCLO_PARSE, q.getUrl());
                try {
                    URL feed = new URL(q.getUrl());
                    Dlog.d("EncyclopediaParse", "" + q.getUrl());
                    results.add(Utils.getInputStreamResponse(feed));
                    er.getQueries().add(q);
                } catch (Exception e) {
                    e.printStackTrace();
                    Crashlytics.logException(e);
                }
            }
        }

        for (int i = 0; i < results.size(); i++) {
            String result = results.get(i);
            EncyclopediaQuery eq = er.getQueries().get(i);
            try {
                parseResult(eq, er, result);
            } catch (Exception e) {
                e.printStackTrace();
                Crashlytics.logException(e);
            }
        }
        return er;
    }


    private void parseResult(EncyclopediaQuery query, EncyclopediaResult er, String feed) throws Exception {
        JSONObject result = null;
        try {
            result = new JSONObject(feed);
        } catch (JSONException e) {
            Crashlytics.log(3, "JSON failure", "query = " + query.getUrl() + " Feed = " + feed);
        }

        if (result != null) {
            if (query.getType() == EncyclopediaType.MAPS) {
                JSONObject data = result.getJSONObject(ParserHelper.DATA);
                Iterator iterator = data.keys();
                while (iterator.hasNext()) {
                    String key = (String) iterator.next();
                    JSONObject obj = data.optJSONObject(key);
                    Map m = Factory.parseEncyclopediaMap(ctx, obj);
                    if (!m.getId().contains("00"))
                        er.getMaps().add(m);
                }
            }
            Collections.sort(er.getMaps(), new Comparator<Map>() {
                @Override
                public int compare(Map lhs, Map rhs) {
                    return lhs.getName_i18n().compareTo(rhs.getName_i18n());
                }
            });
        }
    }
}
