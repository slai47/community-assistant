package com.clanassist.model.player;

import com.clanassist.model.player.minimized.MinimizedVehicleInfo;
import com.clanassist.model.statistics.Statistics;

/**
 * Created by Harrison on 6/2/2014.
 */
public class PlayerVehicleInfo {

    private int maxXp;
    private int maxFrags;
    private int markOfMastery;
    private int in_garage;
    private int tankId;

    private String name;

    private float WN8;
    private float clanWN8;
    private float strongholdWN8;

    private Statistics overallStats;
    private Statistics clanStats;
    private Statistics strongholdStats;

    public static PlayerVehicleInfo build(MinimizedVehicleInfo mvi) {
        PlayerVehicleInfo info = new PlayerVehicleInfo();
        info.setWN8((int) mvi.getWn8());
        info.setTankId(mvi.getId());
        info.setOverallStats(Statistics.build(mvi.getOverall()));
        info.setMarkOfMastery(mvi.getMas());
        info.setClanWN8(mvi.getCwn8());
        return info;
    }

    @Override
    public String toString() {
        return "PlayerVehicleInfo{" +
                "tankId=" + tankId +
                ", name='" + name + '\'' +
                ", WN8=" + WN8 +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getWN8() {
        return WN8;
    }

    public void setWN8(float WN8) {
        this.WN8 = WN8;
    }

    public int getMaxXp() {
        return maxXp;
    }

    public void setMaxXp(int maxXp) {
        this.maxXp = maxXp;
    }

    public int getMaxFrags() {
        return maxFrags;
    }

    public void setMaxFrags(int maxFrags) {
        this.maxFrags = maxFrags;
    }

    public int getMarkOfMastery() {
        return markOfMastery;
    }

    public void setMarkOfMastery(int markOfMastery) {
        this.markOfMastery = markOfMastery;
    }

    public int getIn_garage() {
        return in_garage;
    }

    public void setIn_garage(int in_garage) {
        this.in_garage = in_garage;
    }

    public int getTankId() {
        return tankId;
    }

    public void setTankId(int tankId) {
        this.tankId = tankId;
    }

    public Statistics getOverallStats() {
        return overallStats;
    }

    public void setOverallStats(Statistics overallStats) {
        this.overallStats = overallStats;
    }

    public Statistics getClanStats() {
        return clanStats;
    }

    public void setClanStats(Statistics clanStats) {
        this.clanStats = clanStats;
    }

    public float getClanWN8() {
        return clanWN8;
    }

    public void setClanWN8(float clanWN8) {
        this.clanWN8 = clanWN8;
    }

    public Statistics getStrongholdStats() {
        return strongholdStats;
    }

    public void setStrongholdStats(Statistics strongholdStats) {
        this.strongholdStats = strongholdStats;
    }

    public float getStrongholdWN8() {
        return strongholdWN8;
    }

    public void setStrongholdWN8(float strongholdWN8) {
        this.strongholdWN8 = strongholdWN8;
    }
}
