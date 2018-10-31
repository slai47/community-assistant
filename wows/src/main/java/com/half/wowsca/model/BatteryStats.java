package com.half.wowsca.model;

import org.json.JSONObject;

/**
 * Created by slai4 on 9/19/2015.
 */
public class BatteryStats {


    private int maxFrags;
    private int frags;
    private int hits;
    private int shots;
    private long maxFragsShipId;

    public static BatteryStats parse(JSONObject json) {
        BatteryStats stats = new BatteryStats();
        if (json != null) {
            stats.setMaxFrags(json.optInt("max_frags_battle"));
            stats.setFrags(json.optInt("frags"));
            stats.setHits(json.optInt("hits"));
            stats.setMaxFragsShipId(json.optLong("max_frags_ship_id"));
            stats.setShots(json.optInt("shots"));
        }
        return stats;
    }

    @Override
    public String toString() {
        return "Battery{" +
                "maxFrags=" + maxFrags +
                ", frags=" + frags +
                ", hits=" + hits +
                ", shots=" + shots +
                ", maxFragsShipId=" + maxFragsShipId +
                '}';
    }

    public int getMaxFrags() {
        return maxFrags;
    }

    public void setMaxFrags(int maxFrags) {
        this.maxFrags = maxFrags;
    }

    public int getFrags() {
        return frags;
    }

    public void setFrags(int frags) {
        this.frags = frags;
    }

    public int getHits() {
        return hits;
    }

    public void setHits(int hits) {
        this.hits = hits;
    }

    public int getShots() {
        return shots;
    }

    public void setShots(int shots) {
        this.shots = shots;
    }

    public long getMaxFragsShipId() {
        return maxFragsShipId;
    }

    public void setMaxFragsShipId(long maxFragsShipId) {
        this.maxFragsShipId = maxFragsShipId;
    }
}
