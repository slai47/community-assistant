package com.clanassist.model.player.minimized;

import com.clanassist.model.player.Player;
import com.clanassist.model.player.PlayerVehicleInfo;
import com.clanassist.model.statistics.MinimizedStatistics;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Harrison on 1/24/2015.
 */
public class MinimizedPlayer {

    private int id;
    private String name;
    private float wn8;
    private float cWN8;
    private float shWN8;
    private long lbt; //last battle time
    private long lsd; //last sign in date
    private MinimizedStatistics stats;

    private List<MinimizedVehicleInfo> infos;

    private double dwn8;
    private double wwn8;
    private double mwn8;
    private double mmwn8;


    public static MinimizedPlayer build(Player p) {
        MinimizedPlayer player = new MinimizedPlayer();
        player.setId(p.getId());
        player.setWn8(p.getWN8());
        player.setName(p.getName());
        if(p.getOverallStats() != null)
            player.setStats(MinimizedStatistics.build(p.getOverallStats()));
        List<MinimizedVehicleInfo> mvis = new ArrayList<MinimizedVehicleInfo>();
        if(p.getPlayerVehicleInfoList() != null) {
            for (PlayerVehicleInfo info : p.getPlayerVehicleInfoList()) {
                mvis.add(MinimizedVehicleInfo.build(info));
            }
        }
        player.setInfos(mvis);

        if(p.getLastBattleTime() != null)
            player.setLbt(p.getLastBattleTime().getTimeInMillis());

        if(p.getLastLogoutTime() != null)
            player.setLsd(p.getLastLogoutTime().getTimeInMillis());

        player.setcWN8(p.getClanWN8());
        player.setShWN8(p.getStrongholdWN8());
        if(p.getWn8StatsInfo() != null){
            player.setDwn8(p.getWn8StatsInfo().getPastDay());
            player.setWwn8( p.getWn8StatsInfo().getPastWeek());
            player.setMwn8( p.getWn8StatsInfo().getPastMonth());
            player.setMmwn8( p.getWn8StatsInfo().getPastTwoMonths());
        }
        return player;
    }

    public MinimizedStatistics getStats() {
        return stats;
    }

    public void setStats(MinimizedStatistics stats) {
        this.stats = stats;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getWn8() {
        return wn8;
    }

    public void setWn8(float wn8) {
        this.wn8 = wn8;
    }

    public List<MinimizedVehicleInfo> getInfos() {
        return infos;
    }

    public void setInfos(List<MinimizedVehicleInfo> infos) {
        this.infos = infos;
    }

    public long getLbt() {
        return lbt;
    }

    public void setLbt(long lbt) {
        this.lbt = lbt;
    }

    public long getLsd() {
        return lsd;
    }

    public void setLsd(long lsd) {
        this.lsd = lsd;
    }

    public float getcWN8() {
        return cWN8;
    }

    public void setcWN8(float cWN8) {
        this.cWN8 = cWN8;
    }

    public float getShWN8() {
        return shWN8;
    }

    public void setShWN8(float shWN8) {
        this.shWN8 = shWN8;
    }

    public double getDwn8() {
        return dwn8;
    }

    public void setDwn8(double dwn8) {
        this.dwn8 = dwn8;
    }

    public double getWwn8() {
        return wwn8;
    }

    public void setWwn8(double wwn8) {
        this.wwn8 = wwn8;
    }

    public double getMwn8() {
        return mwn8;
    }

    public void setMwn8(double mwn8) {
        this.mwn8 = mwn8;
    }

    public double getMmwn8() {
        return mmwn8;
    }

    public void setMmwn8(double mmwn8) {
        this.mmwn8 = mmwn8;
    }
}