package com.clanassist.model.player.minimized;

import com.clanassist.model.player.PlayerVehicleInfo;
import com.clanassist.model.statistics.MinimizedStatistics;

/**
 * Created by Harrison on 1/24/2015.
 */
public class MinimizedVehicleInfo {

    private int id;
    private float wn8;
    private float cwn8;
    private float shwn8;
    private MinimizedStatistics overall;
    private int mas;

    public static MinimizedVehicleInfo build(PlayerVehicleInfo info) {
        MinimizedVehicleInfo mvi = new MinimizedVehicleInfo();
        mvi.setId(info.getTankId());
        mvi.setWn8(info.getWN8());
        mvi.setCwn8(info.getClanWN8());
        mvi.setShwn8(info.getStrongholdWN8());
        mvi.setMas(info.getMarkOfMastery());
        mvi.setOverall(MinimizedStatistics.build(info.getOverallStats()));
        return mvi;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getWn8() {
        return wn8;
    }

    public void setWn8(float wn8) {
        this.wn8 = wn8;
    }

    public MinimizedStatistics getOverall() {
        return overall;
    }

    public void setOverall(MinimizedStatistics overall) {
        this.overall = overall;
    }

    public int getMas() {
        return mas;
    }

    public void setMas(int mas) {
        this.mas = mas;
    }

    public float getCwn8() {
        return cwn8;
    }

    public void setCwn8(float cwn8) {
        this.cwn8 = cwn8;
    }

    public float getShwn8() {
        return shwn8;
    }

    public void setShwn8(float shwn8) {
        this.shwn8 = shwn8;
    }
}
