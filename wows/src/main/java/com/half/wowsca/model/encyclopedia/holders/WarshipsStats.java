package com.half.wowsca.model.encyclopedia.holders;

import com.half.wowsca.model.encyclopedia.items.ShipStat;

import java.util.Map;

/**
 * Created by slai4 on 10/16/2015.
 */
public class WarshipsStats {

    private Map<Long, ShipStat> SHIP_STATS;

    public ShipStat get(long id) {
        if (SHIP_STATS != null)
            return SHIP_STATS.get(id);
        else
            return null;

    }

    public void set(Map<Long, ShipStat> SHIP_INFO) {
        this.SHIP_STATS = SHIP_INFO;
    }

    public Map<Long, ShipStat> getSHIP_STATS() {
        return SHIP_STATS;
    }
}
