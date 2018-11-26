package com.half.wowsca.model;

import org.json.JSONObject;

/**
 * Created by slai4 on 9/19/2015.
 */
public class CaptainDetails {

    private int tierLevel;
    private int tierLevelPoints;

    //account info stuff
    private long lastBattleTime;
    private long createdAt;
    private long updatedAt;
    private long lastLogoutTime;

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

    private int karma;
    private float averageTier;

    //max xp
    private long maxXPShipId;
    private long maxXP;

    //max planes
    private long maxPlanesKilledShipId;
    private int maxPlanesKilled;

    private long totalXP;
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

    private long maxSurvivalRateShipId;
    private float maxSurvivalRate;

    private long maxWinRateShipId;
    private float maxWinRate;

    private long maxTotalKillsShipId;
    private int maxTotalKills;

    private long maxPlayedShipId;
    private int maxPlayed;

    private long maxCARatingShipId;
    private float maxCARating;

    private long maxMostTraveledShipId;
    private int maxMostTraveled;

    private long maxAvgDmgShipId;
    private float maxAvgDmg;

    private long maxTotalPlanesShipId;
    private float maxTotalPlanes;

    private long maxTotalExpShipId;
    private long maxTotalExp;

    private long maxTotalDmgShipId;
    private double maxTotalDamage;

    private long maxSurvivedWinsShipId;
    private float maxSurvivedWins;

    private long maxMBAccuracyShipId;
    private float maxMBAccuracy;

    private long maxTBAccuracyShipId;
    private float maxTBAccuracy;

    private float CARating;

    private float cWinRate;
    private float cDamage;
    private float cKills;
    private float cCaptures;
    private float cDefReset;
    private float cPlanes;

//    private float cXP;
//    private float cSurvival;
//    private float cSurWins;

    private float expectedWinRate;
    private float expectedDamage;
    private float expectedKills;
    private float expectedPlanes;

//    private float expectedXP;
//    private float expectedSurvival;
//    private float expectedSurWins;

    private long buildingDamage;
    private long maxBuldingDamage;

    private int spotted;
    private long maxSpottedShipId;
    private double maxSpotted;

    private double maxDamageScouting;
    private long maxDamageScoutingShipId;

    private double scoutingDamage;
    private double totalArgoDamage;
    private double torpArgoDamage;

    private long maxArgoDamageShipId;
    private double maxTotalArgo;

    private long maxTorpArgoDamageShipId;
    private double maxTorpTotalArgo;

    private long maxSuppressionShipId;
    private double suppressionCount;
    private double maxSuppressionCount;

    private long maxDamageDealtToBuildingsShipId;

    public static CaptainDetails parse(JSONObject obj) {
        CaptainDetails d = new CaptainDetails();
        if (obj != null) {
            d.setLastBattleTime(obj.optLong("last_battle_time"));
            d.setTierLevel(obj.optInt("leveling_tier"));
            d.setCreatedAt(obj.optLong("created_at"));
            d.setTierLevelPoints(obj.optInt("leveling_points"));
            d.setUpdatedAt(obj.optLong("updated_at"));
            d.setLastLogoutTime(obj.optLong("logout_at"));
            d.setKarma(obj.optInt("karma"));
            JSONObject stats = obj.optJSONObject("statistics");
            if (stats != null) {
                d.setDistanceTraveled(stats.optInt("distance"));
                d.setBattles(stats.optInt("battles"));
                JSONObject pvp = stats.optJSONObject("pvp");
                if (pvp != null) {
                    d.setBattles(pvp.optInt("battles"));
                    d.setMaxXP(pvp.optLong("max_xp"));
                    d.setMainBattery(BatteryStats.parse(pvp.optJSONObject("main_battery")));
                    d.setMaxXPShipId(pvp.optLong("max_xp_ship_id"));
                    d.setSecondaryBattery(BatteryStats.parse(pvp.optJSONObject("second_battery")));
                    d.setMaxFragsInBattleShipId(pvp.optLong("max_frags_ship_id"));
                    d.setTotalXP(pvp.optLong("xp"));
                    d.setSurvivedBattles(pvp.optInt("survived_battles"));
                    d.setDroppedCapturePoints(pvp.optInt("dropped_capture_points"));
                    d.setDraws(pvp.optInt("draws"));
                    d.setPlanesKilled(pvp.optInt("planes_killed"));
                    d.setSurvivedWins(pvp.optInt("survived_wins"));
                    d.setFrags(pvp.optInt("frags"));
                    d.setMaxFragsInBattle(pvp.optInt("max_frags_battle"));
                    d.setCapturePoints(pvp.optInt("capture_points"));
                    d.setRamming(BatteryStats.parse(pvp.optJSONObject("ramming")));
                    d.setTorpedoes(BatteryStats.parse(pvp.optJSONObject("torpedoes")));
                    d.setMaxPlanesKilledShipId(pvp.optLong("max_planes_killed_ship_id"));
                    d.setAircraft(BatteryStats.parse(pvp.optJSONObject("aircraft")));
                    d.setMaxDamage(pvp.optInt("max_damage_dealt"));
                    d.setMaxDamageShipId(pvp.optLong("max_damage_dealt_ship_id"));
                    d.setWins(pvp.optInt("wins"));
                    d.setLosses(pvp.optInt("losses"));
                    d.setTotalDamage(pvp.optDouble("damage_dealt"));
                    d.setMaxPlanesKilled(pvp.optInt("max_planes_killed"));

                    d.setBuildingDamage(pvp.optLong("damage_to_buildings"));
                    d.setMaxBuldingDamage(pvp.optLong("max_damage_dealt_to_building"));
                    d.setMaxSpottedShipId(pvp.optLong("max_ships_spotted_ship_id"));
                    d.setMaxSpotted(pvp.optDouble("max_ships_spotted"));
                    d.setMaxDamageScouting(pvp.optDouble("max_damage_scouting"));
                    d.setMaxDamageScoutingShipId(pvp.optLong("max_damage_scouting_ship_id"));
                    d.setScoutingDamage(pvp.optDouble("damage_scouting"));
                    d.setTotalArgoDamage(pvp.optDouble("art_agro"));
                    d.setTorpArgoDamage(pvp.optDouble("torpedo_agro"));
                    d.setMaxArgoDamageShipId(pvp.optLong("max_total_agro_ship_id"));
                    d.setMaxTotalArgo(pvp.optDouble("max_total_agro"));
                    d.setMaxSuppressionShipId(pvp.optLong("max_suppression_ship_id"));
                    d.setSuppressionCount(pvp.optDouble("suppressions_count"));
                    d.setMaxSuppressionCount(pvp.optDouble("max_suppressions_count"));
                    d.setMaxDamageDealtToBuildingsShipId(pvp.optLong("max_damage_dealt_to_buildings_ship_id"));
                    d.setSpotted(pvp.optInt("ship_spotted"));
                }
            }
        }
        return d;
    }

    @Override
    public String toString() {
        return "CaptainDetails{" +
                "tierLevel=" + tierLevel +
                ", tierLevelPoints=" + tierLevelPoints +
                ", distanceTraveled=" + distanceTraveled +
                ", maxXPShipId=" + maxXPShipId +
                ", maxXP=" + maxXP +
                ", maxPlanesKilledShipId=" + maxPlanesKilledShipId +
                ", maxPlanesKilled=" + maxPlanesKilled +
                ", totalXP=" + totalXP +
                ", survivedBattles=" + survivedBattles +
                ", droppedCapturePoints=" + droppedCapturePoints +
                ", draws=" + draws +
                ", planesKilled=" + planesKilled +
                ", battles=" + battles +
                ", survivedWins=" + survivedWins +
                ", frags=" + frags +
                ", maxFragsInBattleShipId=" + maxFragsInBattleShipId +
                ", maxFragsInBattle=" + maxFragsInBattle +
                ", capturePoints=" + capturePoints +
                ", maxDamage=" + maxDamage +
                ", maxDamageShipId=" + maxDamageShipId +
                ", wins=" + wins +
                ", losses=" + losses +
                ", totalDamage=" + totalDamage +
                ", cDamage=" + cDamage +
                ", cWinRate=" + cWinRate +
                ", cCaptures=" + cCaptures +
                ", cKills=" + cKills +
                ", cDefReset=" + cDefReset +
//                ", cXP=" + cXP +
                ", cPlanes=" + cPlanes +
//                ", cSurvival=" + cSurvival +
//                ", cSurWins=" + cSurWins +
                '}';
    }

    public int getTierLevel() {
        return tierLevel;
    }

    public void setTierLevel(int tierLevel) {
        this.tierLevel = tierLevel;
    }

    public int getTierLevelPoints() {
        return tierLevelPoints;
    }

    public void setTierLevelPoints(int tierLevelPoints) {
        this.tierLevelPoints = tierLevelPoints;
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

    public long getMaxFragsInBattleShipId() {
        return maxFragsInBattleShipId;
    }

    public void setMaxFragsInBattleShipId(long maxFragsInBattleShipId) {
        this.maxFragsInBattleShipId = maxFragsInBattleShipId;
    }

    public long getLastBattleTime() {
        return lastBattleTime;
    }

    public void setLastBattleTime(long lastBattleTime) {
        this.lastBattleTime = lastBattleTime;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public long getLastLogoutTime() {
        return lastLogoutTime;
    }

    public void setLastLogoutTime(long lastLogoutTime) {
        this.lastLogoutTime = lastLogoutTime;
    }

    public float getcDamage() {
        return cDamage;
    }

    public void setcDamage(float cDamage) {
        this.cDamage = cDamage;
    }

    public float getcWinRate() {
        return cWinRate;
    }

    public void setcWinRate(float cWinRate) {
        this.cWinRate = cWinRate;
    }

    public float getcCaptures() {
        return cCaptures;
    }

    public void setcCaptures(float cCaptures) {
        this.cCaptures = cCaptures;
    }

    public float getcKills() {
        return cKills;
    }

    public void setcKills(float cKills) {
        this.cKills = cKills;
    }

    public float getcDefReset() {
        return cDefReset;
    }

    public void setcDefReset(float cDefReset) {
        this.cDefReset = cDefReset;
    }

//    public float getcXP() {
//        return cXP;
//    }
//
//    public void setcXP(float cXP) {
//        this.cXP = cXP;
//    }
//
    public float getcPlanes() {
        return cPlanes;
    }

    public void setcPlanes(float cPlanes) {
        this.cPlanes = cPlanes;
    }
//
//    public float getcSurvival() {
//        return cSurvival;
//    }
//
//    public void setcSurvival(float cSurvival) {
//        this.cSurvival = cSurvival;
//    }
//
//    public float getcSurWins() {
//        return cSurWins;
//    }
//
//    public void setcSurWins(float cSurWins) {
//        this.cSurWins = cSurWins;
//    }

    public float getExpectedDamage() {
        return expectedDamage;
    }

    public void setExpectedDamage(float expectedDamage) {
        this.expectedDamage = expectedDamage;
    }

    public float getExpectedWinRate() {
        return expectedWinRate;
    }

    public void setExpectedWinRate(float expectedWinRate) {
        this.expectedWinRate = expectedWinRate;
    }

    public float getExpectedKills() {
        return expectedKills;
    }

    public void setExpectedKills(float expectedKills) {
        this.expectedKills = expectedKills;
    }

//    public float getExpectedXP() {
//        return expectedXP;
//    }
//
//    public void setExpectedXP(float expectedXP) {
//        this.expectedXP = expectedXP;
//    }

    public float getExpectedPlanes() {
        return expectedPlanes;
    }

    public void setExpectedPlanes(float expectedPlanes) {
        this.expectedPlanes = expectedPlanes;
    }

//    public float getExpectedSurvival() {
//        return expectedSurvival;
//    }
//
//    public void setExpectedSurvival(float expectedSurvival) {
//        this.expectedSurvival = expectedSurvival;
//    }
//
//    public float getExpectedSurWins() {
//        return expectedSurWins;
//    }
//
//    public void setExpectedSurWins(float expectedSurWins) {
//        this.expectedSurWins = expectedSurWins;
//    }

    public long getMaxSurvivalRateShipId() {
        return maxSurvivalRateShipId;
    }

    public void setMaxSurvivalRateShipId(long maxSurvivalRateShipId) {
        this.maxSurvivalRateShipId = maxSurvivalRateShipId;
    }

    public float getMaxSurvivalRate() {
        return maxSurvivalRate;
    }

    public void setMaxSurvivalRate(float maxSurvivalRate) {
        this.maxSurvivalRate = maxSurvivalRate;
    }

    public long getMaxWinRateShipId() {
        return maxWinRateShipId;
    }

    public void setMaxWinRateShipId(long maxWinRateShipId) {
        this.maxWinRateShipId = maxWinRateShipId;
    }

    public float getMaxWinRate() {
        return maxWinRate;
    }

    public void setMaxWinRate(float maxWinRate) {
        this.maxWinRate = maxWinRate;
    }

    public long getMaxTotalKillsShipId() {
        return maxTotalKillsShipId;
    }

    public void setMaxTotalKillsShipId(long maxTotalKillsShipId) {
        this.maxTotalKillsShipId = maxTotalKillsShipId;
    }

    public int getMaxTotalKills() {
        return maxTotalKills;
    }

    public void setMaxTotalKills(int maxTotalKills) {
        this.maxTotalKills = maxTotalKills;
    }

    public long getMaxPlayedShipId() {
        return maxPlayedShipId;
    }

    public void setMaxPlayedShipId(long maxPlayedShipId) {
        this.maxPlayedShipId = maxPlayedShipId;
    }

    public int getMaxPlayed() {
        return maxPlayed;
    }

    public void setMaxPlayed(int maxPlayed) {
        this.maxPlayed = maxPlayed;
    }

    public long getMaxCARatingShipId() {
        return maxCARatingShipId;
    }

    public void setMaxCARatingShipId(long maxCARatingShipId) {
        this.maxCARatingShipId = maxCARatingShipId;
    }

    public float getMaxCARating() {
        return maxCARating;
    }

    public void setMaxCARating(float maxCARating) {
        this.maxCARating = maxCARating;
    }

    public long getMaxAvgDmgShipId() {
        return maxAvgDmgShipId;
    }

    public void setMaxAvgDmgShipId(long maxAvgDmgShipId) {
        this.maxAvgDmgShipId = maxAvgDmgShipId;
    }

    public float getMaxAvgDmg() {
        return maxAvgDmg;
    }

    public void setMaxAvgDmg(float maxAvgDmg) {
        this.maxAvgDmg = maxAvgDmg;
    }

    public float getCARating() {
        return CARating;
    }

    public void setCARating(float CARating) {
        this.CARating = CARating;
    }

    public long getMaxTotalPlanesShipId() {
        return maxTotalPlanesShipId;
    }

    public void setMaxTotalPlanesShipId(long maxTotalPlanesShipId) {
        this.maxTotalPlanesShipId = maxTotalPlanesShipId;
    }

    public float getMaxTotalPlanes() {
        return maxTotalPlanes;
    }

    public void setMaxTotalPlanes(float maxTotalPlanes) {
        this.maxTotalPlanes = maxTotalPlanes;
    }

    public long getMaxTotalExpShipId() {
        return maxTotalExpShipId;
    }

    public void setMaxTotalExpShipId(long maxTotalExpShipId) {
        this.maxTotalExpShipId = maxTotalExpShipId;
    }

    public long getMaxTotalExp() {
        return maxTotalExp;
    }

    public void setMaxTotalExp(long maxTotalExp) {
        this.maxTotalExp = maxTotalExp;
    }

    public long getMaxTotalDmgShipId() {
        return maxTotalDmgShipId;
    }

    public void setMaxTotalDmgShipId(long maxTotalDmgShipId) {
        this.maxTotalDmgShipId = maxTotalDmgShipId;
    }

    public double getMaxTotalDamage() {
        return maxTotalDamage;
    }

    public void setMaxTotalDamage(double maxTotalDamage) {
        this.maxTotalDamage = maxTotalDamage;
    }

    public long getMaxSurvivedWinsShipId() {
        return maxSurvivedWinsShipId;
    }

    public void setMaxSurvivedWinsShipId(long maxSurvivedWinsShipId) {
        this.maxSurvivedWinsShipId = maxSurvivedWinsShipId;
    }

    public float getMaxSurvivedWins() {
        return maxSurvivedWins;
    }

    public void setMaxSurvivedWins(float maxSurvivedWins) {
        this.maxSurvivedWins = maxSurvivedWins;
    }

    public long getMaxMostTraveledShipId() {
        return maxMostTraveledShipId;
    }

    public void setMaxMostTraveledShipId(long maxMostTraveledShipId) {
        this.maxMostTraveledShipId = maxMostTraveledShipId;
    }

    public int getMaxMostTraveled() {
        return maxMostTraveled;
    }

    public void setMaxMostTraveled(int maxMostTraveled) {
        this.maxMostTraveled = maxMostTraveled;
    }

    public int getKarma() {
        return karma;
    }

    public void setKarma(int karma) {
        this.karma = karma;
    }

    public float getAverageTier() {
        return averageTier;
    }

    public void setAverageTier(float averageTier) {
        this.averageTier = averageTier;
    }

    public long getMaxMBAccuracyShipId() {
        return maxMBAccuracyShipId;
    }

    public void setMaxMBAccuracyShipId(long maxMBAccuracyShipId) {
        this.maxMBAccuracyShipId = maxMBAccuracyShipId;
    }

    public float getMaxMBAccuracy() {
        return maxMBAccuracy;
    }

    public void setMaxMBAccuracy(float maxMBAccuracy) {
        this.maxMBAccuracy = maxMBAccuracy;
    }

    public long getMaxTBAccuracyShipId() {
        return maxTBAccuracyShipId;
    }

    public void setMaxTBAccuracyShipId(long maxTBAccuracyShipId) {
        this.maxTBAccuracyShipId = maxTBAccuracyShipId;
    }

    public float getMaxTBAccuracy() {
        return maxTBAccuracy;
    }

    public void setMaxTBAccuracy(float maxTBAccuracy) {
        this.maxTBAccuracy = maxTBAccuracy;
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

    public long getMaxSpottedShipId() {
        return maxSpottedShipId;
    }

    public void setMaxSpottedShipId(long maxSpottedShipId) {
        this.maxSpottedShipId = maxSpottedShipId;
    }

    public long getMaxDamageScoutingShipId() {
        return maxDamageScoutingShipId;
    }

    public void setMaxDamageScoutingShipId(long maxDamageScoutingShipId) {
        this.maxDamageScoutingShipId = maxDamageScoutingShipId;
    }

    public long getMaxArgoDamageShipId() {
        return maxArgoDamageShipId;
    }

    public void setMaxArgoDamageShipId(long maxArgoDamageShipId) {
        this.maxArgoDamageShipId = maxArgoDamageShipId;
    }

    public long getMaxSuppressionShipId() {
        return maxSuppressionShipId;
    }

    public void setMaxSuppressionShipId(long maxSuppressionShipId) {
        this.maxSuppressionShipId = maxSuppressionShipId;
    }

    public long getMaxDamageDealtToBuildingsShipId() {
        return maxDamageDealtToBuildingsShipId;
    }

    public void setMaxDamageDealtToBuildingsShipId(long maxDamageDealtToBuildingsShipId) {
        this.maxDamageDealtToBuildingsShipId = maxDamageDealtToBuildingsShipId;
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

    public long getMaxTorpArgoDamageShipId() {
        return maxTorpArgoDamageShipId;
    }

    public void setMaxTorpArgoDamageShipId(long maxTorpArgoDamageShipId) {
        this.maxTorpArgoDamageShipId = maxTorpArgoDamageShipId;
    }

    public double getMaxTorpTotalArgo() {
        return maxTorpTotalArgo;
    }

    public void setMaxTorpTotalArgo(double maxTorpTotalArgo) {
        this.maxTorpTotalArgo = maxTorpTotalArgo;
    }
}
