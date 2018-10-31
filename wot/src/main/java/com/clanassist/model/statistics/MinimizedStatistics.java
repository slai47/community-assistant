package com.clanassist.model.statistics;

/**
 * Created by Harrison on 1/24/2015.
 */
public class MinimizedStatistics {

    private int aXp;
    private int wins;
    private int spots;
    private int capPts;
    private int games;
    private int damage;
    private int hitsPC;
    private int frags;
    private int defPts;
    private int sur;

    public static MinimizedStatistics build(Statistics stats) {
        MinimizedStatistics ms = new MinimizedStatistics();
        ms.setaXp(stats.getAverageXp());
        ms.setWins(stats.getWins());
        ms.setCapPts(stats.getCapturePoints());
        ms.setGames(stats.getBattles());
        ms.setDamage(stats.getDamageDealt());
        ms.setSpots(stats.getSpotted());
        ms.setHitsPC(stats.getHitsPercentage());
        ms.setFrags(stats.getFrags());
        ms.setDefPts(stats.getDroppedCapture_points());
        ms.setSur(stats.getSurvivedBattles());
        return ms;
    }

    public int getaXp() {
        return aXp;
    }

    public void setaXp(int aXp) {
        this.aXp = aXp;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getSpots() {
        return spots;
    }

    public void setSpots(int spots) {
        this.spots = spots;
    }

    public int getCapPts() {
        return capPts;
    }

    public void setCapPts(int capPts) {
        this.capPts = capPts;
    }

    public int getGames() {
        return games;
    }

    public void setGames(int games) {
        this.games = games;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public int getHitsPC() {
        return hitsPC;
    }

    public void setHitsPC(int hitsPC) {
        this.hitsPC = hitsPC;
    }

    public int getFrags() {
        return frags;
    }

    public void setFrags(int frags) {
        this.frags = frags;
    }

    public int getDefPts() {
        return defPts;
    }

    public void setDefPts(int defPts) {
        this.defPts = defPts;
    }

    public int getSur() {
        return sur;
    }

    public void setSur(int sur) {
        this.sur = sur;
    }
}
