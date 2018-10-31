package com.clanassist.model.player.storage;

import com.clanassist.model.statistics.Statistics;

import java.util.Calendar;

/**
 * Created by Harrison on 10/25/2014.
 */
public class PlayerSavedStats {

    private Statistics stats;
    private Statistics clanStats;
    private Statistics strongholdStats;
    private float wn8;
    private float clanWN8;
    private float strongWN8;
    private long timestamp;
    private int treesKilled;

    public PlayerSavedStats(Statistics stats, Statistics clanStats, Statistics strong, float wn8, float clanWN8, float strongWN8, int treesKilled) {
        this.stats = stats;
        this.clanStats = clanStats;
        this.strongholdStats = strong;
        this.wn8 = wn8;
        this.strongWN8 = strongWN8;
        timestamp = Calendar.getInstance().getTimeInMillis();
        this.clanWN8 = clanWN8;
        this.treesKilled = treesKilled;
    }

    public Statistics getStats() {
        return stats;
    }

    public void setStats(Statistics stats) {
        this.stats = stats;
    }

    public float getWn8() {
        return wn8;
    }

    public void setWn8(float wn8) {
        this.wn8 = wn8;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public float getClanWN8() {
        return clanWN8;
    }

    public void setClanWN8(float clanWN8) {
        this.clanWN8 = clanWN8;
    }

    public Statistics getClanStats() {
        return clanStats;
    }

    public void setClanStats(Statistics clanStats) {
        this.clanStats = clanStats;
    }

    public int getTreesKilled() {
        return treesKilled;
    }

    public void setTreesKilled(int treesKilled) {
        this.treesKilled = treesKilled;
    }

    public float getStrongWN8() {
        return strongWN8;
    }

    public void setStrongWN8(float strongWN8) {
        this.strongWN8 = strongWN8;
    }

    public Statistics getStrongholdStats() {
        return strongholdStats;
    }

    public void setStrongholdStats(Statistics strongholdStats) {
        this.strongholdStats = strongholdStats;
    }

}
