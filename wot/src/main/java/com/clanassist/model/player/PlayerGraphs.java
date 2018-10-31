package com.clanassist.model.player;

import com.clanassist.model.search.results.PlayerResult;

import java.util.Map;

/**
 * Created by Harrison on 4/15/2015.
 */
public class PlayerGraphs {

    private Map<String, Double> battlesPerClass; // pie
    private Map<String, Double> averageWN8PerClass;
    private Map<String, Integer> tanksPerClass;

    private Map<Integer, Double> battlesPerTier; // bar
    private Map<Integer, Double> averageWN8PerTier; // bar
    private Map<Integer, Integer> tanksPerTier;

    private Map<String, Double> battlesPerNation; // hBar
    private Map<String, Double> averageWN8PerNation;
    private Map<String, Integer> tanksPerNation;

    private Map<String, Integer> bestWN8PerNation;
    private Map<String, Integer> bestWN8PerNationID;

    private Map<Integer, Integer> bestWN8PerTier;
    private Map<Integer, Integer> bestWN8PerTierID;

    private Map<String, Integer> bestWN8PerClassType;
    private Map<String, Integer> bestWN8PerClassTypeID;

    @Override
    public String toString() {
        return "PlayerGraphs{" +
                "battlesPerClass=" + battlesPerClass +
                ", averageWN8PerClass=" + averageWN8PerClass +
                ", battlesPerTier=" + battlesPerTier +
                ", averageWN8PerTier=" + averageWN8PerTier +
                ", battlesPerNation=" + battlesPerNation +
                ", averageWN8PerNation=" + averageWN8PerNation +
                ", bestWN8PerNation=" + bestWN8PerNation +
                ", bestWN8PerNationID=" + bestWN8PerNationID +
                ", bestWN8PerTier=" + bestWN8PerTier +
                ", bestWN8PerTierID=" + bestWN8PerTierID +
                ", bestWN8PerClassType=" + bestWN8PerClassType +
                ", bestWN8PerClassTypeID=" + bestWN8PerClassTypeID +
                ", tanksPerClass=" + tanksPerClass +
                ", tanksPerTier=" + tanksPerTier +
                ", tanksPerNation=" + tanksPerNation +
                '}';
    }

    public Map<String, Double> getBattlesPerClass() {
        return battlesPerClass;
    }

    public void setBattlesPerClass(Map<String, Double> battlesPerClass) {
        this.battlesPerClass = battlesPerClass;
    }

    public Map<String, Double> getAverageWN8PerClass() {
        return averageWN8PerClass;
    }

    public void setAverageWN8PerClass(Map<String, Double> averageWN8PerClass) {
        this.averageWN8PerClass = averageWN8PerClass;
    }

    public Map<Integer, Double> getBattlesPerTier() {
        return battlesPerTier;
    }

    public void setBattlesPerTier(Map<Integer, Double> battlesPerTier) {
        this.battlesPerTier = battlesPerTier;
    }

    public Map<Integer, Double> getAverageWN8PerTier() {
        return averageWN8PerTier;
    }

    public void setAverageWN8PerTier(Map<Integer, Double> averageWN8PerTier) {
        this.averageWN8PerTier = averageWN8PerTier;
    }

    public Map<String, Double> getBattlesPerNation() {
        return battlesPerNation;
    }

    public void setBattlesPerNation(Map<String, Double> battlesPerNation) {
        this.battlesPerNation = battlesPerNation;
    }

    public Map<String, Double> getAverageWN8PerNation() {
        return averageWN8PerNation;
    }

    public void setAverageWN8PerNation(Map<String, Double> averageWN8PerNation) {
        this.averageWN8PerNation = averageWN8PerNation;
    }

    public Map<String, Integer> getBestWN8PerNation() {
        return bestWN8PerNation;
    }

    public void setBestWN8PerNation(Map<String, Integer> bestWN8PerNation) {
        this.bestWN8PerNation = bestWN8PerNation;
    }

    public Map<String, Integer> getBestWN8PerNationID() {
        return bestWN8PerNationID;
    }

    public void setBestWN8PerNationID(Map<String, Integer> bestWN8PerNationID) {
        this.bestWN8PerNationID = bestWN8PerNationID;
    }

    public Map<Integer, Integer> getBestWN8PerTier() {
        return bestWN8PerTier;
    }

    public void setBestWN8PerTier(Map<Integer, Integer> bestWN8PerTier) {
        this.bestWN8PerTier = bestWN8PerTier;
    }

    public Map<Integer, Integer> getBestWN8PerTierID() {
        return bestWN8PerTierID;
    }

    public void setBestWN8PerTierID(Map<Integer, Integer> bestWN8PerTierID) {
        this.bestWN8PerTierID = bestWN8PerTierID;
    }

    public Map<String, Integer> getBestWN8PerClassType() {
        return bestWN8PerClassType;
    }

    public void setBestWN8PerClassType(Map<String, Integer> bestWN8PerClassType) {
        this.bestWN8PerClassType = bestWN8PerClassType;
    }

    public Map<String, Integer> getBestWN8PerClassTypeID() {
        return bestWN8PerClassTypeID;
    }

    public void setBestWN8PerClassTypeID(Map<String, Integer> bestWN8PerClassTypeID) {
        this.bestWN8PerClassTypeID = bestWN8PerClassTypeID;
    }

    public Map<String, Integer> getTanksPerClass() {
        return tanksPerClass;
    }

    public void setTanksPerClass(Map<String, Integer> tanksPerClass) {
        this.tanksPerClass = tanksPerClass;
    }

    public Map<Integer, Integer> getTanksPerTier() {
        return tanksPerTier;
    }

    public void setTanksPerTier(Map<Integer, Integer> tanksPerTier) {
        this.tanksPerTier = tanksPerTier;
    }

    public Map<String, Integer> getTanksPerNation() {
        return tanksPerNation;
    }

    public void setTanksPerNation(Map<String, Integer> tanksPerNation) {
        this.tanksPerNation = tanksPerNation;
    }
}
