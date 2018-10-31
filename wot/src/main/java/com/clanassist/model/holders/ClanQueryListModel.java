package com.clanassist.model.holders;

/**
 * Created by Harrison on 1/28/2015.
 */
public class ClanQueryListModel {

    private String playerName;
    private int playerId;
    private int tankWn8;
    private int clanWn8;
    private int battles;

    private double winRate;
    private double avgExp;
    private double kd;
    private double hitPercentage;
    private double avgDam;
    private double survivedBattles;

    private int mastery;

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getTankWn8() {
        return tankWn8;
    }

    public void setTankWn8(int tankWn8) {
        this.tankWn8 = tankWn8;
    }

    public int getBattles() {
        return battles;
    }

    public void setBattles(int battles) {
        this.battles = battles;
    }

    public double getWinRate() {
        return winRate;
    }

    public void setWinRate(double winRate) {
        this.winRate = winRate;
    }

    public double getAvgExp() {
        return avgExp;
    }

    public void setAvgExp(double avgExp) {
        this.avgExp = avgExp;
    }

    public double getKd() {
        return kd;
    }

    public void setKd(double kd) {
        this.kd = kd;
    }

    public double getHitPercentage() {
        return hitPercentage;
    }

    public void setHitPercentage(double hitPercentage) {
        this.hitPercentage = hitPercentage;
    }

    public double getAvgDam() {
        return avgDam;
    }

    public void setAvgDam(double avgDam) {
        this.avgDam = avgDam;
    }

    public double getSurvivedBattles() {
        return survivedBattles;
    }

    public void setSurvivedBattles(double survivedBattles) {
        this.survivedBattles = survivedBattles;
    }

    public int getMastery() {
        return mastery;
    }

    public void setMastery(int mastery) {
        this.mastery = mastery;
    }

    public int getClanWn8() {
        return clanWn8;
    }

    public void setClanWn8(int clanWn8) {
        this.clanWn8 = clanWn8;
    }
}
