package com.clanassist.model.player.storage;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Harrison on 6/27/2015.
 */
public class VehicleStatsObj {

    private List<SparseArray<Integer>> vehicleStats;

    public VehicleStatsObj() {
        vehicleStats = new ArrayList<SparseArray<Integer>>();
    }

    public List<SparseArray<Integer>> getVehicleStats() {
        return vehicleStats;
    }

    public void setVehicleStats(List<SparseArray<Integer>> vehicleStats) {
        this.vehicleStats = vehicleStats;
    }
}
