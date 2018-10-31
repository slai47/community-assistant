package com.half.wowsca.model;

import com.half.wowsca.model.ranked.SeasonInfo;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by slai4 on 9/15/2015.
 */
public class Ship{

    private long shipId;

    //account info stuff
    private int lastBattleTime;

    //statistics
    private int distanceTraveled;

    //main battery stats
    private BatteryStats mainBattery;

    //secondary battery stats
    private BatteryStats secondaryBattery;

    //ramming
    private BatteryStats ramming;

    private BatteryStats torpedoes;

    private BatteryStats aircraft;

    //max xp
    private int maxXP;

    private long totalXP;
    private int survivedBattles;
    private int droppedCapturePoints;
    private int draws;
    private int planesKilled;
    private int battles;
    private int survivedWins;
    private int frags;
    private int maxFragsInBattle;
    private int capturePoints;

    private int maxDamage;
    private int wins;
    private int losses;
    private double totalDamage;

    private int maxPlanesKilled;

    private long buildingDamage;
    private long maxBuldingDamage;

    private int spotted;
    private double maxSpotted;

    private double maxDamageScouting;

    private double scoutingDamage;
    private double totalArgoDamage;
    private double torpArgoDamage;

    private double maxTotalArgo;

    private double suppressionCount;
    private double maxSuppressionCount;


    private List<SeasonInfo> rankedInfo;
    private float CARating;

    private Statistics pvpSolo;
    private Statistics pvpDiv2;
    private Statistics pvpDiv3;
    private Statistics pve;
    private Statistics teamBattles;

    public static Ship parse(JSONObject ship) {
        Ship s = new Ship();
        if (ship != null) {
            s.setDistanceTraveled(ship.optInt("distance"));
            s.setLastBattleTime(ship.optInt("last_battle_time"));
            s.setShipId(ship.optLong("ship_id"));
            s.setBattles(ship.optInt("battles"));

            JSONObject pvp = ship.optJSONObject("pvp");
            if (pvp != null) {
                s.setBattles(pvp.optInt("battles"));
                s.setMaxFragsInBattle(pvp.optInt("max_frags_battle"));
                s.setCapturePoints(pvp.optInt("capture_points"));
                s.setDraws(pvp.optInt("draws"));
                s.setMaxXP(pvp.optInt("max_xp"));
                s.setWins(pvp.optInt("wins"));
                s.setPlanesKilled(pvp.optInt("planes_killed"));
                s.setLosses(pvp.optInt("losses"));
                s.setTorpedoes(BatteryStats.parse(pvp.optJSONObject("torpedoes")));
                s.setMaxDamage(pvp.optInt("max_damage_dealt"));
                s.setTotalDamage(pvp.optDouble("damage_dealt"));
                s.setMaxPlanesKilled(pvp.optInt("max_planes_killed"));
                s.setAircraft(BatteryStats.parse(pvp.optJSONObject("aircraft")));
                s.setRamming(BatteryStats.parse(pvp.optJSONObject("ramming")));
                s.setMainBattery(BatteryStats.parse(pvp.optJSONObject("main_battery")));
                s.setSecondaryBattery(BatteryStats.parse(pvp.optJSONObject("second_battery")));
                s.setSurvivedWins(pvp.optInt("survived_wins"));
                s.setFrags(pvp.optInt("frags"));
                s.setTotalXP(pvp.optLong("xp"));
                s.setSurvivedBattles(pvp.optInt("survived_battles"));
                s.setDroppedCapturePoints(pvp.optInt("dropped_capture_points"));

                s.setBuildingDamage(pvp.optLong("damage_to_buildings"));
                s.setMaxBuldingDamage(pvp.optLong("max_damage_dealt_to_building"));
                s.setMaxSpotted(pvp.optDouble("max_ships_spotted"));
                s.setMaxDamageScouting(pvp.optDouble("max_damage_scouting"));
                s.setScoutingDamage(pvp.optDouble("damage_scouting"));
                s.setTotalArgoDamage(pvp.optDouble("art_agro"));
                s.setTorpArgoDamage(pvp.optDouble("torpedo_agro"));
                s.setMaxTotalArgo(pvp.optDouble("max_total_agro"));
                s.setSuppressionCount(pvp.optDouble("suppressions_count"));
                s.setMaxSuppressionCount(pvp.optDouble("max_suppressions_count"));
                s.setSpotted(pvp.optInt("ships_spotted"));
            }

            JSONObject pve = ship.optJSONObject("pve");
            s.setPve(Statistics.parse(pve));
            JSONObject pvp_solo = ship.optJSONObject("pvp_solo");
            s.setPvpSolo(Statistics.parse(pvp_solo));
            JSONObject pvp_div2 = ship.optJSONObject("pvp_div2");
            s.setPvpDiv2(Statistics.parse(pvp_div2));
            JSONObject pvp_div3 = ship.optJSONObject("pvp_div3");
            s.setPvpDiv3(Statistics.parse(pvp_div3));
            JSONObject club = ship.optJSONObject("club");
            s.setTeamBattles(Statistics.parse(club));
        }
        return s;
    }

    @Override
    public String toString() {
        return "shipId=" + shipId +
                ", battles=" + battles +
                ", lastBattleTime=" + lastBattleTime +
                ", distanceTraveled=" + distanceTraveled +
                ", maxXP=" + maxXP +
                ", totalXP=" + totalXP +
                ", survivedBattles=" + survivedBattles +
                ", droppedCapturePoints=" + droppedCapturePoints +
                ", draws=" + draws +
                ", planesKilled=" + planesKilled +
                ", survivedWins=" + survivedWins +
                ", frags=" + frags +
                ", maxFragsInBattle=" + maxFragsInBattle +
                ", capturePoints=" + capturePoints +
                ", maxDamage=" + maxDamage +
                ", wins=" + wins +
                ", losses=" + losses +
                ", totalDamage=" + totalDamage +
                ", maxPlanesKilled=" + maxPlanesKilled;
    }

    public long getShipId() {
        return shipId;
    }

    public void setShipId(long shipId) {
        this.shipId = shipId;
    }

    public int getLastBattleTime() {
        return lastBattleTime;
    }

    public void setLastBattleTime(int lastBattleTime) {
        this.lastBattleTime = lastBattleTime;
    }

    public int getDistanceTraveled() {
        return distanceTraveled;
    }

    public void setDistanceTraveled(int distanceTraveled) {
        this.distanceTraveled = distanceTraveled;
    }

    public BatteryStats getMainBattery() {
        return mainBattery;
    }

    public void setMainBattery(BatteryStats mainBattery) {
        this.mainBattery = mainBattery;
    }

    public BatteryStats getSecondaryBattery() {
        return secondaryBattery;
    }

    public void setSecondaryBattery(BatteryStats secondaryBattery) {
        this.secondaryBattery = secondaryBattery;
    }

    public BatteryStats getRamming() {
        return ramming;
    }

    public void setRamming(BatteryStats ramming) {
        this.ramming = ramming;
    }

    public BatteryStats getTorpedoes() {
        return torpedoes;
    }

    public void setTorpedoes(BatteryStats torpedoes) {
        this.torpedoes = torpedoes;
    }

    public BatteryStats getAircraft() {
        return aircraft;
    }

    public void setAircraft(BatteryStats aircraft) {
        this.aircraft = aircraft;
    }

    public int getMaxXP() {
        return maxXP;
    }

    public void setMaxXP(int maxXP) {
        this.maxXP = maxXP;
    }

    public long getTotalXP() {
        return totalXP;
    }

    public void setTotalXP(long totalXP) {
        this.totalXP = totalXP;
    }

    public int getSurvivedBattles() {
        return survivedBattles;
    }

    public void setSurvivedBattles(int survivedBattles) {
        this.survivedBattles = survivedBattles;
    }

    public int getDroppedCapturePoints() {
        return droppedCapturePoints;
    }

    public void setDroppedCapturePoints(int droppedCapturePoints) {
        this.droppedCapturePoints = droppedCapturePoints;
    }

    public int getDraws() {
        return draws;
    }

    public void setDraws(int draws) {
        this.draws = draws;
    }

    public int getPlanesKilled() {
        return planesKilled;
    }

    public void setPlanesKilled(int planesKilled) {
        this.planesKilled = planesKilled;
    }

    public int getBattles() {
        return battles;
    }

    public void setBattles(int battles) {
        this.battles = battles;
    }

    public int getSurvivedWins() {
        return survivedWins;
    }

    public void setSurvivedWins(int survivedWins) {
        this.survivedWins = survivedWins;
    }

    public int getFrags() {
        return frags;
    }

    public void setFrags(int frags) {
        this.frags = frags;
    }

    public int getMaxFragsInBattle() {
        return maxFragsInBattle;
    }

    public void setMaxFragsInBattle(int maxFragsInBattle) {
        this.maxFragsInBattle = maxFragsInBattle;
    }

    public int getCapturePoints() {
        return capturePoints;
    }

    public void setCapturePoints(int capturePoints) {
        this.capturePoints = capturePoints;
    }

    public int getMaxDamage() {
        return maxDamage;
    }

    public void setMaxDamage(int maxDamage) {
        this.maxDamage = maxDamage;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public double getTotalDamage() {
        return totalDamage;
    }

    public void setTotalDamage(double totalDamage) {
        this.totalDamage = totalDamage;
    }

    public int getMaxPlanesKilled() {
        return maxPlanesKilled;
    }

    public void setMaxPlanesKilled(int maxPlanesKilled) {
        this.maxPlanesKilled = maxPlanesKilled;
    }

    public List<SeasonInfo> getRankedInfo() {
        return rankedInfo;
    }

    public void setRankedInfo(List<SeasonInfo> rankedInfo) {
        this.rankedInfo = rankedInfo;
    }

    public void setCARating(float CARating) {
        this.CARating = CARating;
    }

    public float getCARating() {
        return CARating;
    }

    public Statistics getPvpSolo() {
        return pvpSolo;
    }

    public void setPvpSolo(Statistics pvpSolo) {
        this.pvpSolo = pvpSolo;
    }

    public Statistics getPvpDiv2() {
        return pvpDiv2;
    }

    public void setPvpDiv2(Statistics pvpDiv2) {
        this.pvpDiv2 = pvpDiv2;
    }

    public Statistics getPvpDiv3() {
        return pvpDiv3;
    }

    public void setPvpDiv3(Statistics pvpDiv3) {
        this.pvpDiv3 = pvpDiv3;
    }

    public Statistics getPve() {
        return pve;
    }

    public void setPve(Statistics pve) {
        this.pve = pve;
    }

    public Statistics getTeamBattles() {
        return teamBattles;
    }

    public void setTeamBattles(Statistics teamBattles) {
        this.teamBattles = teamBattles;
    }

    public long getBuildingDamage() {
        return buildingDamage;
    }

    public void setBuildingDamage(long buildingDamage) {
        this.buildingDamage = buildingDamage;
    }

    public long getMaxBuldingDamage() {
        return maxBuldingDamage;
    }

    public void setMaxBuldingDamage(long maxBuldingDamage) {
        this.maxBuldingDamage = maxBuldingDamage;
    }

    public double getMaxSpotted() {
        return maxSpotted;
    }

    public void setMaxSpotted(double maxSpotted) {
        this.maxSpotted = maxSpotted;
    }

    public double getMaxDamageScouting() {
        return maxDamageScouting;
    }

    public void setMaxDamageScouting(double maxDamageScouting) {
        this.maxDamageScouting = maxDamageScouting;
    }

    public double getScoutingDamage() {
        return scoutingDamage;
    }

    public void setScoutingDamage(double scoutingDamage) {
        this.scoutingDamage = scoutingDamage;
    }

    public double getTotalArgoDamage() {
        return totalArgoDamage;
    }

    public void setTotalArgoDamage(double totalArgoDamage) {
        this.totalArgoDamage = totalArgoDamage;
    }

    public double getTorpArgoDamage() {
        return torpArgoDamage;
    }

    public void setTorpArgoDamage(double torpArgoDamage) {
        this.torpArgoDamage = torpArgoDamage;
    }

    public double getMaxTotalArgo() {
        return maxTotalArgo;
    }

    public void setMaxTotalArgo(double maxTotalArgo) {
        this.maxTotalArgo = maxTotalArgo;
    }

    public double getSuppressionCount() {
        return suppressionCount;
    }

    public void setSuppressionCount(double suppressionCount) {
        this.suppressionCount = suppressionCount;
    }

    public double getMaxSuppressionCount() {
        return maxSuppressionCount;
    }

    public void setMaxSuppressionCount(double maxSuppressionCount) {
        this.maxSuppressionCount = maxSuppressionCount;
    }

    public int getSpotted() {
        return spotted;
    }

    public void setSpotted(int spotted) {
        this.spotted = spotted;
    }
}
