package com.half.wowsca.model.encyclopedia.items;

import org.json.JSONObject;

/**
 * Created by slai4 on 10/16/2015.
 */
public class ShipStat {

//    private float cap_pts;
    private float wins;
    private float dmg_dlt;
    private float frags;
//    private float dr_cap_pts;
    private float pls_kd;

//    private float battles;
//    private float sr_wins;
//    private float avg_xp;
//    private float count;
//    private float sr_bat;

    public void parse(JSONObject obj) {
        if (obj != null) {
//            setCap_pts((float) obj.optDouble("capture_points"));
            setWins((float) obj.optDouble("wins"));
            setPls_kd((float) obj.optDouble("planes_killed"));
            setDmg_dlt((float) obj.optDouble("damage_dealt"));
            setFrags((float) obj.optDouble("frags"));
//            setDr_cap_pts((float) obj.optDouble("dropped_capture_points"));
//            setBattles((float) obj.optDouble("battles"));
//            setSr_wins((float) obj.optDouble("survived_wins"));
//            setAvg_xp((float) obj.optDouble("xp"));
//            setSr_bat((float) obj.optDouble("survived_battles"));
//            setCount((float) obj.optDouble("count"));
        }
    }

    @Override
    public String toString() {
        return "ShipStat{" +
//                "cap_pts=" + cap_pts +
                ", wins=" + wins +
                ", pls_kd=" + pls_kd +
//                ", battles=" + battles +
                ", dmg_dlt=" + dmg_dlt +
//                ", sr_wins=" + sr_wins +
                ", frags=" + frags +
//                ", avg_xp=" + avg_xp +
//                ", sr_bat=" + sr_bat +
//                ", dr_cap_pts=" + dr_cap_pts +
//                ", count=" + count +
                '}';
    }

    public float getWins() {
        return wins;
    }

    public void setWins(float wins) {
        this.wins = wins;
    }

    public float getPls_kd() {
        return pls_kd;
    }

    public void setPls_kd(float pls_kd) {
        this.pls_kd = pls_kd;
    }

//    public float getBattles() {
//        return battles;
//    }
//
//    public void setBattles(float battles) {
//        this.battles = battles;
//    }

    public float getDmg_dlt() {
        return dmg_dlt;
    }

    public void setDmg_dlt(float dmg_dlt) {
        this.dmg_dlt = dmg_dlt;
    }

//    public float getSr_wins() {
//        return sr_wins;
//    }
//
//    public void setSr_wins(float sr_wins) {
//        this.sr_wins = sr_wins;
//    }

    public float getFrags() {
        return frags;
    }

    public void setFrags(float frags) {
        this.frags = frags;
    }

//    public float getAvg_xp() {
//        return avg_xp;
//    }
//
//    public void setAvg_xp(float avg_xp) {
//        this.avg_xp = avg_xp;
//    }

//    public float getSr_bat() {
//        return sr_bat;
//    }
//
//    public void setSr_bat(float sr_bat) {
//        this.sr_bat = sr_bat;
//    }
//
//    public float getCount() {
//        return count;
//    }
//
//    public void setCount(float count) {
//        this.count = count;
//    }

//    public float getDr_cap_pts() {
//        return dr_cap_pts;
//    }
//
//    public void setDr_cap_pts(float dr_cap_pts) {
//        this.dr_cap_pts = dr_cap_pts;
//    }
}
