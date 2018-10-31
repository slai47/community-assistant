package com.clanassist.model.player.storage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Harrison on 10/25/2014.
 */
public class SavedStatsObj {

    private List<PlayerSavedStats> stats;

    public SavedStatsObj() {
        this.stats = new ArrayList<PlayerSavedStats>();
    }

    @Override
    public String toString() {
        return "SavedStatsObj{" +
                "stats=" + stats +
                '}';
    }

    public List<PlayerSavedStats> getStats() {
        return stats;
    }

    public void setStats(List<PlayerSavedStats> stats) {
        this.stats = stats;
    }
}
