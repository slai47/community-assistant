package com.clanassist.model.player.storage;

import com.clanassist.model.player.WN8StatsInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Harrison on 6/27/2015.
 */
public class PlayerFutureStats {

    private List<WN8StatsInfo> statsInfos;

    public PlayerFutureStats() {
        this.statsInfos = new ArrayList<WN8StatsInfo>();
    }

    public List<WN8StatsInfo> getStatsInfos() {
        return statsInfos;
    }

    public void setStatsInfos(List<WN8StatsInfo> statsInfos) {
        this.statsInfos = statsInfos;
    }
}
