package com.clanassist.backend;


import com.clanassist.SVault;
import com.clanassist.model.Factory;
import com.clanassist.model.clan.Clan;
import com.clanassist.model.search.enums.ClanSearchType;
import com.clanassist.model.search.queries.ClanQuery;
import com.clanassist.model.search.results.ClanResult;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.utilities.Utils;
import com.utilities.interfaces.IParser;
import com.utilities.logging.Dlog;
import com.utilities.search.Error;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Obsidian47 on 3/6/14.
 */
public class ClanParser implements IParser<ClanQuery, ClanResult> {

    private static final String CLAN_PARSE = "ClanParse";

    @Override
    public ClanResult parse(ClanQuery... queries) {
        ClanResult cr = new ClanResult();
        List<String> results = new ArrayList<String>();
        for (ClanQuery q : queries) {
            Crashlytics.setString(SVault.LOG_CLAN_PARSE, q.getUrl());
            Answers.getInstance().logCustom(new CustomEvent("GetClan").putCustomAttribute("Server", q.getWebAddress()));
            try {
                URL feed = new URL(q.getUrl());
                Dlog.d(CLAN_PARSE, "" + q.getUrl());
                results.add(Utils.getInputStreamResponse(feed));
                cr.getQueries().add(q);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (queries.length == 1) {
            cr.setQuery(queries[0]);
        }
        for (int i = 0; i < results.size(); i++) {
            String result = results.get(i);
            ClanQuery cq = cr.getQueries().get(i);
            try {
                parseResult(cr, cq, result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return cr;
    }

    /**
     * Turns the feed into JSON and parses the return into the result
     *
     * @param cr
     * @param query
     * @param feed
     * @throws Exception
     */
    private void parseResult(ClanResult cr, ClanQuery query, String feed) throws Exception {
        JSONObject result = null;
        try {
            result = new JSONObject(feed);
        } catch (JSONException e) {
//            Crashlytics.log(3, "JSON failure", "query = " + query.getUrl() + "    Feed = " + feed);
        }
        if (result != null) {
            cr.setError(ParserHelper.getError(result));
            JSONObject meta = result.optJSONObject("meta");
            int count = meta.optInt("count");
            if (count != 0) {
                JSONArray dataArray = result.optJSONArray(ParserHelper.DATA);
                JSONObject data = result.optJSONObject(ParserHelper.DATA);
                if (query.getType() == ClanSearchType.SEARCH) {
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject clan = dataArray.getJSONObject(i);
                        Clan c = Factory.parseClan(clan, false);
                        cr.getClans().add(c);
                    }
                } else if (query.getType() == ClanSearchType.DETAILS) {
                    JSONObject clan = data.optJSONObject(query.getClanId() + "");
                    if (clan == null) {
                        Error error = new Error();
                        error.setCode(404);
                        cr.setError(error);
                    } else
                        cr.setDetails(Factory.parseClan(clan, true));
                } else if (query.getType() == ClanSearchType.CLAN_MEMBER) {
                    JSONObject clan = data.optJSONObject(query.getMemberId() + "");
                    if (clan == null) {
                        Error error = new Error();
                        error.setCode(404);
                        cr.setError(error);
                    } else
                        cr.setPlayerClanInfo(Factory.parsePlayerClan(clan));
                }
            }
        }
    }

}
