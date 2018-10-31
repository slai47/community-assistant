package com.half.wowsca.model;

import org.json.JSONObject;

/**
 * Created by slai4 on 5/1/2016.
 */
public class Statistics {

    //main battery stats
    private BatteryStats mainBattery;

    //secondary battery stats
    private BatteryStats secondaryBattery;

    //ramming
    private BatteryStats ramming;

    private BatteryStats torpedoes;

    private BatteryStats aircraft;

    //max xp
    private long maxXPShipId;
    private long maxXP;

    //max planes
    private long maxPlanesKilledShipId;
    private int maxPlanesKilled;

    private int totalXP;
    private int survivedBattles;
    private int droppedCapturePoints;
    private int draws;
    private int planesKilled;
    private int battles;
    private int survivedWins;
    private int frags;
    private long maxFragsInBattleShipId;
    private int maxFragsInBattle;
    private int capturePoints;

    private int maxDamage;
    private long maxDamageShipId;
    private int wins;
    private int losses;
    private double totalDamage;

    private double shipsSpotted;

    private double maxSuppressionCount;

    private long buildingDamage;
    private long maxBuldingDamage;

    private double maxSpotted;

    private double maxDamageScouting;

    private double scoutingDamage;
    private double totalArgoDamage;
    private double torpArgoDamage;

    private double maxTotalArgo;

    private double suppressionCount;

    public static Statistics parse(JSONObject obj){
        Statistics statistics = new Statistics();
        if(obj != null) {
            statistics.setBattles(obj.optInt("battles"));
            statistics.setMaxXP(obj.optLong("max_xp"));
            statistics.setMainBattery(BatteryStats.parse(obj.optJSONObject("main_battery")));
            statistics.setMaxXPShipId(obj.optLong("max_xp_ship_id"));
            statistics.setSecondaryBattery(BatteryStats.parse(obj.optJSONObject("second_battery")));
            statistics.setMaxFragsInBattleShipId(obj.optLong("max_frags_ship_id"));
            statistics.setTotalXP(obj.optInt("xp"));
            statistics.setSurvivedBattles(obj.optInt("survived_battles"));
            statistics.setDroppedCapturePoints(obj.optInt("dropped_capture_points"));
            statistics.setDraws(obj.optInt("draws"));
            statistics.setPlanesKilled(obj.optInt("planes_killed"));
            statistics.setSurvivedWins(obj.optInt("survived_wins"));
            statistics.setFrags(obj.optInt("frags"));
            statistics.setMaxFragsInBattle(obj.optInt("max_frags_battle"));
            statistics.setCapturePoints(obj.optInt("capture_points"));
            statistics.setRamming(BatteryStats.parse(obj.optJSONObject("ramming")));
            statistics.setTorpedoes(BatteryStats.parse(obj.optJSONObject("torpedoes")));
            statistics.setMaxPlanesKilledShipId(obj.optLong("max_planes_killed_ship_id"));
            statistics.setAircraft(BatteryStats.parse(obj.optJSONObject("aircraft")));
            statistics.setMaxDamage(obj.optInt("max_damage_dealt"));
            statistics.setMaxDamageShipId(obj.optLong("max_damage_dealt_ship_id"));
            statistics.setWins(obj.optInt("wins"));
            statistics.setLosses(obj.optInt("losses"));
            statistics.setTotalDamage(obj.optDouble("damage_dealt"));
            statistics.setMaxPlanesKilled(obj.optInt("max_planes_killed"));

            statistics.setShipsSpotted(obj.optDouble("ships_spotted"));

            statistics.setMaxSuppressionCount(obj.optDouble("max_suppressions_count"));

            statistics.setBuildingDamage(obj.optLong("damage_to_buildings"));
            statistics.setMaxBuldingDamage(obj.optLong("max_damage_dealt_to_building"));
            statistics.setMaxSpotted(obj.optDouble("max_ships_spotted"));
            statistics.setMaxDamageScouting(obj.optDouble("max_damage_scouting"));
            statistics.setScoutingDamage(obj.optDouble("damage_scouting"));
            statistics.setTotalArgoDamage(obj.optDouble("art_agro"));
            statistics.setTorpArgoDamage(obj.optDouble("torpedo_agro"));
            statistics.setMaxTotalArgo(obj.optDouble("max_total_agro"));
            statistics.setSuppressionCount(obj.optDouble("suppressions_count"));
        }
        return statistics;
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

    public long getMaxXPShipId() {
        return maxXPShipId;
    }

    public void setMaxXPShipId(long maxXPShipId) {
        this.maxXPShipId = maxXPShipId;
    }

    public long getMaxXP() {
        return maxXP;
    }

    public void setMaxXP(long maxXP) {
        this.maxXP = maxXP;
    }

    public long getMaxPlanesKilledShipId() {
        return maxPlanesKilledShipId;
    }

    public void setMaxPlanesKilledShipId(long maxPlanesKilledShipId) {
        this.maxPlanesKilledShipId = maxPlanesKilledShipId;
    }

    public int getMaxPlanesKilled() {
        return maxPlanesKilled;
    }

    public void setMaxPlanesKilled(int maxPlanesKilled) {
        this.maxPlanesKilled = maxPlanesKilled;
    }

    public int getTotalXP() {
        return totalXP;
    }

    public void setTotalXP(int totalXP) {
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

    public long getMaxFragsInBattleShipId() {
        return maxFragsInBattleShipId;
    }

    public void setMaxFragsInBattleShipId(long maxFragsInBattleShipId) {
        this.maxFragsInBattleShipId = maxFragsInBattleShipId;
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

    public long getMaxDamageShipId() {
        return maxDamageShipId;
    }

    public void setMaxDamageShipId(long maxDamageShipId) {
        this.maxDamageShipId = maxDamageShipId;
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

    public double getShipsSpotted() {
        return shipsSpotted;
    }

    public void setShipsSpotted(double shipsSpotted) {
        this.shipsSpotted = shipsSpotted;
    }

    public double getMaxSuppressionCount() {
        return maxSuppressionCount;
    }

    public void setMaxSuppressionCount(double maxSuppressionCount) {
        this.maxSuppressionCount = maxSuppressionCount;
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
}
