package com.half.wowsca.model.ranked;

import com.half.wowsca.model.BatteryStats;

import org.json.JSONObject;

/**
 * Created by slai4 on 11/29/2015.
 */
public class SeasonStats {
    
    private int maxFrags;
    private int capPts;
    private int draws;
    private int maxXP;
    private int wins;
    private int planesKilled;
    private int losses;
    private BatteryStats torps;
    private int battles;
    private int maxDamage;
    private long damage;
    private int maxPlanes;
    private BatteryStats aircraft;
    private BatteryStats ramming;
    private BatteryStats main;
    private BatteryStats secondary;
    private int surWins;
    private int frags;
    private long xp;
    private int survived;
    private int drpCapPts;

    public static SeasonStats parse(JSONObject obj){
        SeasonStats stats = new SeasonStats();
        if(obj != null){
            stats.setMaxFrags(obj.optInt("max_frags_battle"));
            stats.setCapPts(obj.optInt("capture_points"));
            stats.setDraws(obj.optInt("draws"));
            stats.setMaxXP(obj.optInt("max_xp"));
            stats.setWins(obj.optInt("wins"));
            stats.setPlanesKilled(obj.optInt("planes_killed"));
            stats.setLosses(obj.optInt("losses"));
            stats.setTorps(BatteryStats.parse(obj.optJSONObject("torpedoes")));
            stats.setBattles(obj.optInt("battles"));
            stats.setMaxDamage(obj.optInt("max_damage_dealt"));
            stats.setDamage(obj.optLong("damage_dealt"));
            stats.setPlanesKilled(obj.optInt("max_planes_killed"));
            stats.setAircraft(BatteryStats.parse(obj.optJSONObject("aircraft")));
            stats.setRamming(BatteryStats.parse(obj.optJSONObject("ramming")));
            stats.setMain(BatteryStats.parse(obj.optJSONObject("main_battery")));
            stats.setSecondary(BatteryStats.parse(obj.optJSONObject("second_battery")));
            stats.setSurWins(obj.optInt("survived_wins"));
            stats.setFrags(obj.optInt("frags"));
            stats.setXp(obj.optLong("xp"));
            stats.setSurvived(obj.optInt("survived_battles"));
            stats.setDrpCapPts(obj.optInt("dropped_capture_points"));
        }
        return stats;
    }

    @Override
    public String toString() {
        return "SeasonStats{" +
                "maxFrags=" + maxFrags +
                ", capPts=" + capPts +
                ", draws=" + draws +
                ", maxXP=" + maxXP +
                ", wins=" + wins +
                ", planesKilled=" + planesKilled +
                ", losses=" + losses +
                ", torps=" + torps +
                ", battles=" + battles +
                ", maxDamage=" + maxDamage +
                ", damage=" + damage +
                ", maxPlanes=" + maxPlanes +
                ", aircraft=" + aircraft +
                ", ramming=" + ramming +
                ", main=" + main +
                ", secondary=" + secondary +
                ", surWins=" + surWins +
                ", frags=" + frags +
                ", xp=" + xp +
                ", survived=" + survived +
                ", drpCapPts=" + drpCapPts +
                '}';
    }

    public int getMaxFrags() {
        return maxFrags;
    }

    public void setMaxFrags(int maxFrags) {
        this.maxFrags = maxFrags;
    }

    public int getCapPts() {
        return capPts;
    }

    public void setCapPts(int capPts) {
        this.capPts = capPts;
    }

    public int getDraws() {
        return draws;
    }

    public void setDraws(int draws) {
        this.draws = draws;
    }

    public int getMaxXP() {
        return maxXP;
    }

    public void setMaxXP(int maxXP) {
        this.maxXP = maxXP;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getPlanesKilled() {
        return planesKilled;
    }

    public void setPlanesKilled(int planesKilled) {
        this.planesKilled = planesKilled;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public BatteryStats getTorps() {
        return torps;
    }

    public void setTorps(BatteryStats torps) {
        this.torps = torps;
    }

    public int getBattles() {
        return battles;
    }

    public void setBattles(int battles) {
        this.battles = battles;
    }

    public int getMaxDamage() {
        return maxDamage;
    }

    public void setMaxDamage(int maxDamage) {
        this.maxDamage = maxDamage;
    }

    public long getDamage() {
        return damage;
    }

    public void setDamage(long damage) {
        this.damage = damage;
    }

    public int getMaxPlanes() {
        return maxPlanes;
    }

    public void setMaxPlanes(int maxPlanes) {
        this.maxPlanes = maxPlanes;
    }

    public BatteryStats getAircraft() {
        return aircraft;
    }

    public void setAircraft(BatteryStats aircraft) {
        this.aircraft = aircraft;
    }

    public BatteryStats getRamming() {
        return ramming;
    }

    public void setRamming(BatteryStats ramming) {
        this.ramming = ramming;
    }

    public BatteryStats getMain() {
        return main;
    }

    public void setMain(BatteryStats main) {
        this.main = main;
    }

    public BatteryStats getSecondary() {
        return secondary;
    }

    public void setSecondary(BatteryStats secondary) {
        this.secondary = secondary;
    }

    public int getSurWins() {
        return surWins;
    }

    public void setSurWins(int surWins) {
        this.surWins = surWins;
    }

    public int getFrags() {
        return frags;
    }

    public void setFrags(int frags) {
        this.frags = frags;
    }

    public long getXp() {
        return xp;
    }

    public void setXp(long xp) {
        this.xp = xp;
    }

    public int getSurvived() {
        return survived;
    }

    public void setSurvived(int survived) {
        this.survived = survived;
    }

    public int getDrpCapPts() {
        return drpCapPts;
    }

    public void setDrpCapPts(int drpCapPts) {
        this.drpCapPts = drpCapPts;
    }
}
