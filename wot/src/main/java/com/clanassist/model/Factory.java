package com.clanassist.model;

import android.content.Context;
import android.text.TextUtils;

import com.clanassist.CAApp;
import com.clanassist.backend.ParserHelper;
import com.clanassist.model.clan.Clan;
import com.clanassist.model.clan.Emblems;
import com.clanassist.model.encyclopedia.Map;
import com.clanassist.model.player.Player;
import com.clanassist.model.player.PlayerClan;
import com.clanassist.model.player.PlayerVehicleInfo;
import com.clanassist.model.statistics.Statistics;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Harrison on 6/12/2014.
 */
public class Factory {
    private static final String NAME = "name";
    private static final String MOTTO = "motto";
    private static final String MEMBERS_COUNT = "members_count";
    private static final String COLOR = "color";
    private static final String TAG = "tag";
    private static final String ABBREVIATION = "abbreviation";
    private static final String CREATED_AT = "created_at";
    private static final String DESCRIPTION = "description";
    private static final String DESCRIPTION_HTML = "description_html";
    private static final String ACCEPTS_JOIN = "accepts_join_requests";
    private static final String REQUEST_AVAILABILITY = "request_availability";
    private static final String IS_CLAN_DISBANDED = "is_clan_disbanded";
    private static final String MEMBERS = "members";
    private static final String ACCOUNT_ID = "account_id";
    private static final String ACCOUNT_NAME = "account_name";
    private static final String ROLE = "role";
    private static final String ROLE_I_18_N = "role_i18n";
    private static final String EMBLEMS = "emblems";
    private static final String BW_TANK = "bw_tank";
    private static final String LARGE = "large";
    private static final String SMALL = "small";
    private static final String MEDIUM = "medium";
    private static final String CLAN_ID = "clan_id";
    private static final String OWNER_ID = "leader_id";
    private static final String CREATOR_ID = "creator_id";
    private static final String OWNER_NAME = "leader_name";

    /**
     * Takes statistics object from the JSON feed and gives a Stats object back.
     *
     * @param object - json stats object
     * @return - statistics object
     */
    public static Statistics parseStatistics(JSONObject object) {
        Statistics stats = new Statistics();
        stats.setSpotted(object.optInt("spotted"));
        stats.setHits(object.optInt("hits"));
        stats.setAverageXp(object.optInt("battle_avg_xp"));
        stats.setDraws(object.optInt("draws"));
        stats.setWins(object.optInt("wins"));
        stats.setLosses(object.optInt("losses"));
        stats.setCapturePoints(object.optInt("capture_points"));
        stats.setBattles(object.optInt("battles"));
        stats.setDamageDealt(object.optInt("damage_dealt"));
        stats.setHitsPercentage(object.optInt("hits_percents"));
        stats.setDamageReceived(object.optInt("damage_received"));
        stats.setShots(object.optInt("shots"));
        stats.setXp(object.optInt("xp"));
        stats.setFrags(object.optInt("frags"));
        stats.setSurvivedBattles(object.optInt("survived_battles"));
        stats.setDroppedCapture_points(object.optInt("dropped_capture_points"));

        // new stuff
        stats.setAvgDamageAssistedTrack(object.optDouble("avg_damage_assisted_track"));
        stats.setAvgDamageBlocked(object.optDouble("avg_damage_blocked"));
        stats.setDirectHitsReceived(object.optInt("direct_hits_received"));
        stats.setExplosionHits(object.optInt("explosion_hits"));
        stats.setPiercingsReceived(object.optInt("piercings_received"));
        stats.setPiercings(object.optInt("piercings"));
        stats.setAvgDamageAssisted(object.optDouble("avg_damage_assisted"));
        stats.setAvgDamageAssistedRadio(object.optDouble("avg_damage_assisted_radio"));
        stats.setNoDamageDirectHitsReceived(object.optInt("no_damage_direct_hits_received"));
        stats.setExplosionHitsReceived(object.optInt("explosion_hits_received"));
        stats.setTankingFactor(object.optDouble("tanking_factor"));
        return stats;
    }

    /**
     * Gives vehicle info stats and all player info about tank.
     *
     * @param obj - json vehicle player info
     * @return
     */
    public static PlayerVehicleInfo parsePlayerVehicleInfo(JSONObject obj) {
        PlayerVehicleInfo info = new PlayerVehicleInfo();
        JSONObject allStats = obj.optJSONObject("all");
        JSONObject clanStats = obj.optJSONObject("clan");
        JSONObject strongholdDef = obj.optJSONObject("stronghold_defense");
        JSONObject strongholdSkr = obj.optJSONObject("stronghold_skirmish");

        if (allStats == null)
            allStats = obj.optJSONObject("statistics");
        info.setOverallStats(parseStatistics(allStats));

        if (clanStats != null)
            info.setClanStats(parseStatistics(clanStats));

        if(strongholdDef != null){
            Statistics def = parseStatistics(strongholdDef);
            Statistics skr = parseStatistics(strongholdSkr);
            mergeStatistics(skr, def);
            info.setStrongholdStats(skr);
        }

        info.setMarkOfMastery(obj.optInt("mark_of_mastery"));
        info.setMaxFrags(obj.optInt("max_frags"));
        info.setMaxXp(obj.optInt("max_xp"));
        info.setTankId(obj.optInt("tank_id"));
        return info;
    }

    public static Player parsePlayer(JSONObject obj) {
        Player p = new Player();
        JSONObject statistics = obj.optJSONObject("statistics");
        if (statistics != null) {
            JSONObject allStat = statistics.optJSONObject("all");
            JSONObject clanStats = statistics.optJSONObject("clan");
            JSONObject strongholdDef = statistics.optJSONObject("stronghold_defense");
            JSONObject strongholdSkr = statistics.optJSONObject("stronghold_skirmish");

            Statistics def = parseStatistics(strongholdDef);
            Statistics skr = parseStatistics(strongholdSkr);
            mergeStatistics(skr, def);

            p.setOverallStats(parseStatistics(allStat));
            p.setClanStats(parseStatistics(clanStats));

            p.setStrongholdStats(skr);

            p.setMaxDamage(allStat.optInt("max_damage"));
            p.setMaxDamageTankId(allStat.optInt("max_damage_tank_id"));

            p.setMaxXp(allStat.optInt("max_xp"));
            p.setMaxXpTankId(allStat.optInt("max_xp_tank_id"));

            p.setMaxFrags(allStat.optInt("max_frags"));
            p.setMaxFragsTankId(allStat.optInt("max_frags_tank_id"));

            p.setTreesCut(statistics.optInt("trees_cut"));
        }
        p.setId(obj.optInt("account_id"));

        long lbt = obj.optLong("last_battle_time");
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(lbt * ParserHelper.MILLIS);
        p.setLastBattleTime(c);

        long ca = obj.optLong("created_at");
        Calendar c1 = Calendar.getInstance();
        c1.setTimeInMillis(ca * ParserHelper.MILLIS);
        p.setCreatedAt(c1);

        long lt = obj.optLong("logout_at");
        Calendar lc = Calendar.getInstance();
        lc.setTimeInMillis(lt + ParserHelper.MILLIS);
        p.setLastLogoutTime(lc);

        if (obj.optInt("clan_id") != 0) {
            PlayerClan pc = new PlayerClan();
            pc.setClanId(obj.optInt("clan_id"));
            p.setClanInfo(pc);
        }

        p.setGlobalRating(obj.optInt("global_rating"));
        p.setName(obj.optString("nickname"));
        return p;
    }

    public static void mergeStatistics(Statistics main, Statistics sec){

        if(main != null && sec != null) {
            main.setSpotted(main.getSpotted() + sec.getSpotted());
            main.setHits(main.getHits() + sec.getHits());
            main.setAverageXp((main.getAverageXp() + sec.getAverageXp()) / 2);
            main.setDraws(main.getDraws() + sec.getDraws());
            main.setWins(main.getWins() + sec.getWins());
            main.setLosses(main.getLosses() + sec.getLosses());
            main.setCapturePoints(main.getCapturePoints() + sec.getCapturePoints());
            main.setBattles(main.getBattles() + sec.getBattles());
            main.setDamageDealt(main.getDamageDealt() + sec.getDamageDealt());
            main.setHitsPercentage((main.getHitsPercentage() + sec.getHitsPercentage()) / 2);
            main.setDamageReceived(main.getDamageReceived() + sec.getDamageReceived());
            main.setShots(main.getShots() + sec.getShots());
            main.setXp(main.getXp() + sec.getXp());
            main.setFrags(main.getFrags() + sec.getFrags());
            main.setSurvivedBattles(main.getSurvivedBattles() + sec.getSurvivedBattles());
            main.setDroppedCapture_points(main.getDroppedCapture_points() + sec.getDroppedCapture_points());

            // new stuff
            main.setAvgDamageAssistedTrack(main.getAvgDamageAssistedTrack() + sec.getAvgDamageAssistedTrack());
            main.setAvgDamageBlocked(main.getAvgDamageBlocked() + sec.getAvgDamageBlocked());
            main.setDirectHitsReceived(main.getDirectHitsReceived() + sec.getDirectHitsReceived());
            main.setExplosionHits(main.getExplosionHits() + sec.getExplosionHits());
            main.setPiercingsReceived(main.getPiercingsReceived() + sec.getPiercingsReceived());
            main.setPiercings(main.getPiercings() + sec.getPiercings());
            main.setAvgDamageAssisted(main.getAvgDamageAssisted() + sec.getAvgDamageAssisted());
            main.setAvgDamageAssistedRadio(main.getAvgDamageAssistedRadio() + sec.getAvgDamageAssistedRadio());
            main.setNoDamageDirectHitsReceived(main.getNoDamageDirectHitsReceived() + sec.getNoDamageDirectHitsReceived());
            main.setExplosionHitsReceived(main.getExplosionHitsReceived() + sec.getExplosionHitsReceived());
            main.setTankingFactor((main.getTankingFactor() + sec.getTankingFactor()) / 2);
        }
    }


    // have to use account/achievements
    public static void parseAwards(JSONObject obj, Player p) {
        JSONObject array = obj.optJSONObject("achievements");
        if (array != null) {
            p.setAwards(new HashMap<String, Integer>());
            Iterator<String> iterator = array.keys();
            while (iterator.hasNext()) {
                String ob = iterator.next();
                p.getAwards().put(ob, array.optInt(ob));
            }
        }
    }

    public static Clan parseClan(JSONObject obj, boolean parseDetails) {
        Clan c = new Clan();
        c.setName(obj.optString(NAME));
        c.setClanId(obj.optInt(CLAN_ID));
        c.setOwner_id(obj.optInt(OWNER_ID));
        c.setOwnerName(obj.optString(OWNER_NAME));
        c.setMotto(obj.optString(MOTTO));
        c.setMembers_count(obj.optInt(MEMBERS_COUNT));
        c.setColor(obj.optString(COLOR));
        c.setAbbreviation(obj.optString(TAG));

        //add dates to the object
        long time = obj.optLong(CREATED_AT);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time * ParserHelper.MILLIS);
        c.setCreatedAt(cal);

        c.setEmblem(getClanEmblems(obj));

        if (parseDetails) {
            c.setDescription(obj.optString(DESCRIPTION));
            c.setDescriptionHtml(obj.optString(DESCRIPTION_HTML));
            c.setRequestAvailability(obj.optBoolean(ACCEPTS_JOIN));
            c.setClanDisbanded(obj.optBoolean(IS_CLAN_DISBANDED));
            JSONArray members = obj.optJSONArray(MEMBERS);
            if (members != null) {
                c.setMembers(new ArrayList<Player>());
                for (int i = 0; i < members.length(); i++) {
                    JSONObject member = members.optJSONObject(i);
                    if (member != null) {
                        Player p = new Player();
                        p.setName(member.optString(ACCOUNT_NAME));
                        p.setId(member.optInt(ACCOUNT_ID));
                        PlayerClan info = new PlayerClan();
                        info.setClanId(c.getClanId());
                        info.setRole(member.optString(ROLE));
                        info.setRole_i18n(member.optString(ROLE_I_18_N));

                        Calendar since = Calendar.getInstance();
                        since.setTimeInMillis(member.optLong("joined_at") * ParserHelper.MILLIS);
                        info.setSince(since);

                        p.setClanInfo(info);
                        c.getMembers().add(p);
                    }
                }
            }
        }
        return c;
    }

    private static Emblems getClanEmblems(JSONObject clan) {
        JSONObject obj = clan.optJSONObject(EMBLEMS);
        if (obj != null) {
            Emblems e = new Emblems();
            JSONObject x32 = obj.optJSONObject("x32");
            JSONObject x24 = obj.optJSONObject("x24");
            JSONObject x256 = obj.optJSONObject("x256");
            JSONObject x64 = obj.optJSONObject("x64");
            JSONObject x128 = obj.optJSONObject("x128");

            if (x64 != null) {
                String wotImage = x64.optString("wot");
                e.setBw_tank(wotImage);
            }
            if (x32 != null) {
                String x32Image = x32.optString("portal");
                e.setMedium(x32Image);
            }
            if (x256 != null) {
                String x256Image = x256.optString("wowp");
                e.setXlarge(x256Image);
            }
            if (x128 != null) {
                String x128Image = x128.optString("portal");
                e.setLarge(x128Image);
            }
            if (x24 != null) {
                String x24Image = x24.optString("portal");
                e.setSmall(x24Image);
            }
            return e;
        }
        return new Emblems();
    }


    public static Map parseEncyclopediaMap(Context ctx, JSONObject obj) {
        Map map = new Map();
        map.setDescription(obj.optString(DESCRIPTION));
        map.setId(obj.optString("arena_id"));
        map.setName_i18n(obj.optString("name_i18n"));
        //https://s3-eu-west-1.amazonaws.com/wotca/maps/01_karelia_grid.jpg
        map.setGridUrl(CAApp.getImageRepo(ctx) + map.getId() + "_grid.jpg");
        map.setScreenUrl(CAApp.getImageRepo(ctx) + map.getId() + "_screen.jpg");
        if (!TextUtils.isEmpty(map.getId())) {
            Picasso.with(ctx).load(map.getScreenUrl()).fetch();
        }
        return map;
    }

    public static PlayerClan parsePlayerClan(JSONObject obj) {
        PlayerClan pc = new PlayerClan();
        JSONObject clan = obj.optJSONObject("clan");
        pc.setEmblems(getClanEmblems(clan));
        pc.setName(clan.optString(NAME));
        pc.setAbbr(clan.optString(TAG));
        pc.setColor(clan.optString(COLOR));
        pc.setClanId(clan.optInt(CLAN_ID));
        pc.setRole(obj.optString(ROLE));
        pc.setClanId(clan.optInt(CLAN_ID));
        pc.setRole_i18n(obj.optString(ROLE_I_18_N));
        long joined = obj.optLong("joined_at");
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(joined * ParserHelper.MILLIS);
        pc.setSince(c);
        return pc;
    }
}
