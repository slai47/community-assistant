package com.clanassist.backend;

import com.clanassist.SVault;
import com.clanassist.model.clan.Battle;
import com.clanassist.model.clan.globalwar.Campaign;
import com.clanassist.model.clan.globalwar.Province;
import com.clanassist.model.search.enums.GlobalWarSearchType;
import com.clanassist.model.search.queries.GlobalWarQuery;
import com.clanassist.model.search.results.GlobalWarResult;
import com.crashlytics.android.Crashlytics;
import com.utilities.Utils;
import com.utilities.interfaces.IParser;
import com.utilities.logging.Dlog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Harrison on 5/13/14.
 */
public class GlobalWarParser implements IParser<GlobalWarQuery, GlobalWarResult> {

    public static final String DATA = "data";
    private static final String NAME_I_18_N = "name_i18n";
    private static final String ARENAS = "arenas";
    private static final String PROVINCES_I18N = "provinces_i18n";
    private static final String TIME = "time";
    private static final String STARTED = "started";
    private static final String TYPE = "type";
    private static final String ARENA_ID = "arena_id";
    public static final String NAME_I_18_N1 = "name_i18n";
    public static final String PROVINCE_ID = "province_id";
    public static final String STATUS = "status";
    public static final String PROVINCE_I_18_N = "province_i18n";
    public static final String REVENUE = "revenue";
    public static final String VEHICLE_MAX_LEVEL = "vehicle_max_level";
    public static final String ARENA = "arena";
    public static final String PRIMARY_REGION = "primary_region";
    public static final String REGIONS = "regions";
    public static final String REGION_ID = "region_id";
    public static final String REGION_I_18_N = "region_i18n";
    public static final String CLAN_ID = "clan_id";
    public static final String PROVINCE_ID1 = "province_id";
    public static final String PRIME_TIME = "prime_time";
    public static final String STATE = "state";
    public static final String ACTIVE = "active";
    public static final String TYPE1 = "type";
    public static final String MAP_URL = "map_url";
    public static final String MAP_ID = "map_id";


    @Override
    public GlobalWarResult parse(GlobalWarQuery... queries) {
        GlobalWarResult gwr = new GlobalWarResult();
        List<String> results = new ArrayList<String>();
        for (GlobalWarQuery q : queries) {
            if (q != null) {
                Crashlytics.setString(SVault.LOG_GLOBALWAR_PARSE, q.getUrl());
                try {
                    URL feed = new URL(q.getUrl());
                    Dlog.d("GlobalWarParser", "" + q.getUrl());
                    results.add(Utils.getInputStreamResponse(feed));
                    gwr.getQueries().add(q);
                } catch (Exception e) {
                    e.printStackTrace();
                    Crashlytics.logException(e);
                }
            }
        }
        if (queries.length == 1) {
            gwr.setQuery(queries[0]);
        }
        for (int i = 0; i < results.size(); i++) {
            String result = results.get(i);
            GlobalWarQuery cq = gwr.getQueries().get(i);
            try {
                parseResult(cq, gwr, result);
            } catch (Exception e) {
                e.printStackTrace();
                Crashlytics.logException(e);
            }
        }
        return gwr;
    }


    private void parseResult(GlobalWarQuery query, GlobalWarResult gwr, String feed) throws Exception {
        JSONObject result = null;
        try {
            result = new JSONObject(feed);
        } catch (JSONException e) {
            Crashlytics.log(3, "JSON failure", "query = " + query.getUrl() + "    Feed = " + feed);
        }

        if (result != null) {
            gwr.setError(ParserHelper.getError(result));

            if (query.getType() == GlobalWarSearchType.PROVINCE) {
                JSONObject data = result.getJSONObject(ParserHelper.DATA);
                Province p = getProvince(data, query.getProvinceId());
                if (p != null)
                    gwr.getProvinces().put(query.getProvinceId(), p);
            } else if (query.getType() == GlobalWarSearchType.BATTLES) {
                JSONArray data = result.getJSONArray(ParserHelper.DATA);
                gwr.getBattles().put(query.getClanId(), getBattles(data, query));
                gwr.getClanIds().add(query.getClanId());
            } else if (query.getType() == GlobalWarSearchType.MAPS) {
                JSONArray data = result.getJSONArray(ParserHelper.DATA);
                gwr.getCampaigns().addAll(getCampaignMaps(data));
            }
        }
    }


    private List<Battle> getBattles(JSONArray battles, GlobalWarQuery query) {
        List<Battle> items = new ArrayList<Battle>();
        int clanId = query.getClanId();
//        JSONArray battles = data.optJSONArray("" + clanId);
        if (battles != null)
            for (int i = 0; i < battles.length(); i++) {
                JSONObject battle = battles.optJSONObject(i);
                long time = battle.optLong(TIME);

                Calendar cal = Calendar.getInstance();
                if (time != 0) {
                    cal.setTimeInMillis(time * ParserHelper.MILLIS);
                }
//                boolean started = battle.optBoolean(STARTED);
                String type = battle.optString(TYPE);
                String front_name = battle.optString("front_name");
                String province_id = battle.optString("province_id");
                String province_name = battle.optString("province_name");
                String front_id = battle.optString("front_id");

                Battle b = new Battle();
                b.setType(type);
                b.setProvince(province_id);
                b.setProvinceName(province_name);
                b.setArenaName_i18n(front_name);
                b.setArenaName(front_id);
                items.add(b);
//                JSONArray arenas = battle.optJSONArray(ARENAS);
//                JSONArray provincesJOSN = battle.optJSONArray(PROVINCES_I18N);
//                for (int k = 0; k < provincesJOSN.length(); k++) {
//                    JSONObject province = provincesJOSN.optJSONObject(k);
//                    JSONObject arena = arenas.optJSONObject(k);
//
//                    Battle b = new Battle();
//                    b.setStarted(started);
//                    b.setType(type);
//                    b.setHomeTeamId(clanId);
//                    if (time != 0)
//                        b.setTime(cal);
//                    b.setMapId(query.getMapId());
//
//                    try {
//                        b.setArenaName(arena.optString(ARENA_ID));
//                        b.setArenaName_i18n(arena.optString(NAME_I_18_N));
//                    } catch (Exception e) {
//                    }
//
//                    try {
//                        b.setProvince(province.optString(PROVINCE_ID));
//                        b.setProvinceName(province.optString(NAME_I_18_N1));
//                    } catch (Exception e) {
//                    }
//                    items.add(b);
//                }
                Collections.sort(items, new Comparator<Battle>() {
                    @Override
                    public int compare(Battle lhs, Battle rhs) {
                        Calendar lhsC = lhs.getTime();
                        Calendar rhsC = rhs.getTime();
                        long lTime = 0l, rTime = 0l;
                        if (lhsC != null) {
                            lTime = lhsC.getTimeInMillis();
                        }
                        if (rhsC != null) {
                            rTime = rhsC.getTimeInMillis();
                        }
                        return (int) (rTime - lTime);
                    }
                });
            }
        return items;
    }

    private List<Campaign> getCampaignMaps(JSONArray data) {
        List<Campaign> campaigns = new ArrayList<Campaign>();

        for (int i = 0; i < data.length(); i++) {
            Campaign m = new Campaign();
            JSONObject obj = data.optJSONObject(i);
            String state = obj.optString(STATE);
            boolean isActive = state.equals(ACTIVE);
            if (isActive) {
                m.setActive(isActive);
                m.setType(obj.optString(TYPE1));
                m.setMapUrl(obj.optString(MAP_URL));
                m.setMapId(obj.optString(MAP_ID));
                campaigns.add(m);
            }
        }
        return campaigns;
    }


    /**
     * Might be old is unused
     *
     * @param data
     * @param provinceId
     * @return
     */
    private Province getProvince(JSONObject data, String provinceId) {
        Province province = null;
        JSONObject provinceJson = data.optJSONObject(provinceId);
        if (provinceJson != null) {
            province = new Province();
            province.setStatus(provinceJson.optString(STATUS));
            //set neighbors NOT DONE

            province.setProvince_I18N(provinceJson.optString(PROVINCE_I_18_N));
            province.setRevenue(provinceJson.optInt(REVENUE));
            province.setVehicleMaxLevel(provinceJson.optInt(VEHICLE_MAX_LEVEL));
            province.setArenaName(provinceJson.optString(ARENA));

            //regionInfo
            province.setPrimaryRegion(provinceJson.optString(PRIMARY_REGION));
            try {
                JSONObject region = data.optJSONObject(REGIONS);
                JSONObject regionInfo = region.optJSONObject(province.getPrimaryRegion());
                province.setRegionID(regionInfo.optString(REGION_ID));
                province.setRegion_i18n(regionInfo.optString(REGION_I_18_N));
            } catch (Exception e) {

            }
            province.setClanId(provinceJson.optInt(CLAN_ID));
            province.setProvinceID(provinceJson.optString(PROVINCE_ID1));
            long prime_time = provinceJson.optLong(PRIME_TIME);
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(prime_time * ParserHelper.MILLIS);
            province.setPrimeTime(cal);
        }
        return province;
    }
}
