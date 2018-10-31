package com.clanassist.model.search.results;

import com.clanassist.model.clan.ClanGraphs;
import com.clanassist.model.player.minimized.MinimizedPlayer;

import java.util.List;

/**
 * Created by Harrison on 8/5/2014.
 */
public class ClanPlayerWN8sTaskResult {

    private List<MinimizedPlayer> players;

    private String clanId;

    private double overallWN8;
    private double overallClanWN8;
    private double overallSHWN8;

    private double averageExp;
    private double averageKills;
    private double averageBattles;
    private double averageDamage;
    private double averageWinRate;
    private double averageTier;

    private double averageClanWinRate;
    private double averageSHWinRate;

    private double totalBattles;
    private double totalKills;
    private double totalDamage;

    private double average7DayWn8;
    private double averageDayWn8;
    private double average30DayWn8;
    private double average60DayWn8;

    private int bestWN8AccountId;
    private String bestWN8AccountName;
    private int bestWN8AccountNumber;

    private int bestClanWN8AccountId;
    private String bestClanWN8AccountName;
    private int bestClanWN8AccountNumber;

    private int bestSHWN8AccountId;
    private String bestSHWN8AccountName;
    private int bestSHWN8AccountNumber;

    private long savedMillis;

    private ClanGraphs graphs;

    @Override
    public String toString() {
        return "ClanPlayerWN8sTaskResult{" +
                "clanId='" + clanId + '\'' +
                ", overallWN8=" + overallWN8 +
                ", averageExp=" + averageExp +
                ", averageKills=" + averageKills +
                ", averageBattles=" + averageBattles +
                ", averageDamage=" + averageDamage +
                ", averageWinRate=" + averageWinRate +
                ", bestWN8AccountId=" + bestWN8AccountId +
                ", bestWN8AccountName='" + bestWN8AccountName + '\'' +
                ", bestWN8AccountNumber=" + bestWN8AccountNumber +
                ", savedMillis=" + savedMillis +
                '}';
    }

    public List<MinimizedPlayer> getPlayers() {
        return players;
    }

    public void setPlayers(List<MinimizedPlayer> players) {
        this.players = players;
    }

    public double getOverallWN8() {
        return overallWN8;
    }

    public void setOverallWN8(double overallWN8) {
        this.overallWN8 = overallWN8;
    }

    public double getAverageExp() {
        return averageExp;
    }

    public void setAverageExp(double averageExp) {
        this.averageExp = averageExp;
    }

    public double getAverageKills() {
        return averageKills;
    }

    public void setAverageKills(double averageKills) {
        this.averageKills = averageKills;
    }

    public double getAverageBattles() {
        return averageBattles;
    }

    public void setAverageBattles(double averageBattles) {
        this.averageBattles = averageBattles;
    }

    public double getAverageDamage() {
        return averageDamage;
    }

    public void setAverageDamage(double averageDamage) {
        this.averageDamage = averageDamage;
    }

    public int getBestWN8AccountId() {
        return bestWN8AccountId;
    }

    public void setBestWN8AccountId(int bestWN8AccountId) {
        this.bestWN8AccountId = bestWN8AccountId;
    }

    public String getBestWN8AccountName() {
        return bestWN8AccountName;
    }

    public void setBestWN8AccountName(String bestWN8AccountName) {
        this.bestWN8AccountName = bestWN8AccountName;
    }

    public int getBestWN8AccountNumber() {
        return bestWN8AccountNumber;
    }

    public void setBestWN8AccountNumber(int bestWN8AccountNumber) {
        this.bestWN8AccountNumber = bestWN8AccountNumber;
    }

    public double getAverageWinRate() {
        return averageWinRate;
    }

    public void setAverageWinRate(double averageWinRate) {
        this.averageWinRate = averageWinRate;
    }

    public String getClanId() {
        return clanId;
    }

    public void setClanId(String clanId) {
        this.clanId = clanId;
    }

    public long getSavedMillis() {
        return savedMillis;
    }

    public void setSavedMillis(long savedMillis) {
        this.savedMillis = savedMillis;
    }

    public ClanGraphs getGraphs() {
        return graphs;
    }

    public void setGraphs(ClanGraphs graphs) {
        this.graphs = graphs;
    }

    public double getAverageTier() {
        return averageTier;
    }

    public void setAverageTier(double averageTier) {
        this.averageTier = averageTier;
    }

    public double getTotalBattles() {
        return totalBattles;
    }

    public void setTotalBattles(double totalBattles) {
        this.totalBattles = totalBattles;
    }

    public double getTotalKills() {
        return totalKills;
    }

    public void setTotalKills(double totalKills) {
        this.totalKills = totalKills;
    }

    public double getTotalDamage() {
        return totalDamage;
    }

    public void setTotalDamage(double totalDamage) {
        this.totalDamage = totalDamage;
    }

    public double getOverallClanWN8() {
        return overallClanWN8;
    }

    public void setOverallClanWN8(double overallClanWN8) {
        this.overallClanWN8 = overallClanWN8;
    }

    public int getBestClanWN8AccountId() {
        return bestClanWN8AccountId;
    }

    public void setBestClanWN8AccountId(int bestClanWN8AccountId) {
        this.bestClanWN8AccountId = bestClanWN8AccountId;
    }

    public String getBestClanWN8AccountName() {
        return bestClanWN8AccountName;
    }

    public void setBestClanWN8AccountName(String bestClanWN8AccountName) {
        this.bestClanWN8AccountName = bestClanWN8AccountName;
    }

    public int getBestClanWN8AccountNumber() {
        return bestClanWN8AccountNumber;
    }

    public void setBestClanWN8AccountNumber(int bestClanWN8AccountNumber) {
        this.bestClanWN8AccountNumber = bestClanWN8AccountNumber;
    }

    public double getAverageClanWinRate() {
        return averageClanWinRate;
    }

    public void setAverageClanWinRate(double averageClanWinRate) {
        this.averageClanWinRate = averageClanWinRate;
    }

    public double getOverallSHWN8() {
        return overallSHWN8;
    }

    public void setOverallSHWN8(double overallSHWN8) {
        this.overallSHWN8 = overallSHWN8;
    }

    public double getAverageSHWinRate() {
        return averageSHWinRate;
    }

    public void setAverageSHWinRate(double averageSHWinRate) {
        this.averageSHWinRate = averageSHWinRate;
    }

    public int getBestSHWN8AccountId() {
        return bestSHWN8AccountId;
    }

    public void setBestSHWN8AccountId(int bestSHWN8AccountId) {
        this.bestSHWN8AccountId = bestSHWN8AccountId;
    }

    public String getBestSHWN8AccountName() {
        return bestSHWN8AccountName;
    }

    public void setBestSHWN8AccountName(String bestSHWN8AccountName) {
        this.bestSHWN8AccountName = bestSHWN8AccountName;
    }

    public int getBestSHWN8AccountNumber() {
        return bestSHWN8AccountNumber;
    }

    public void setBestSHWN8AccountNumber(int bestSHWN8AccountNumber) {
        this.bestSHWN8AccountNumber = bestSHWN8AccountNumber;
    }

    public double getAverage7DayWn8() {
        return average7DayWn8;
    }

    public void setAverage7DayWn8(double average7DayWn8) {
        this.average7DayWn8 = average7DayWn8;
    }

    public double getAverageDayWn8() {
        return averageDayWn8;
    }

    public void setAverageDayWn8(double averageDayWn8) {
        this.averageDayWn8 = averageDayWn8;
    }

    public double getAverage30DayWn8() {
        return average30DayWn8;
    }

    public void setAverage30DayWn8(double average30DayWn8) {
        this.average30DayWn8 = average30DayWn8;
    }

    public double getAverage60DayWn8() {
        return average60DayWn8;
    }

    public void setAverage60DayWn8(double average60DayWn8) {
        this.average60DayWn8 = average60DayWn8;
    }
}
