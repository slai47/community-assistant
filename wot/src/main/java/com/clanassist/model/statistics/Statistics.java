package com.clanassist.model.statistics;

import com.clanassist.model.player.StatsType;

/**
 * Statistics on a player or vehicle are stored here.
 * <p/>
 * Created by Obsidian47 on 3/2/14.
 */
public class Statistics {

    private StatsType type;
    private int spotted;
    private int hits;
    private int averageXp;
    private int draws;
    private int wins;
    private int losses;
    private int capturePoints;
    private int battles;
    private int damageDealt;
    private int hitsPercentage;
    private int damageReceived;
    private int shots;
    private int xp;
    private int frags;
    private int survivedBattles;
    private int droppedCapture_points;

    //New adds to api
    private double avgDamageAssistedTrack; // average damage upon your shooting the track
    private double avgDamageBlocked; // average damage blocked by armor per game
    private int directHitsReceived; // Direct hits
    private int explosionHits; // hits on enemy as a result of splash
    private int piercingsReceived; // pens received
    private int piercings; // pens
    private double avgDamageAssisted; // average damage caused with your assistance
    private double avgDamageAssistedRadio; // Average Damage upon your spotting of something
    private int noDamageDirectHitsReceived; // direct hits received that caused no damage
    private int explosionHitsReceived; // hits received as a result of splash to you
    private double tankingFactor; // ratio of damage blocked by armor from AP, HEAT and APCR shells to damage received from these types of shells

    public static Statistics build(MinimizedStatistics stats) {
        Statistics stat = new Statistics();
        if(stats != null) {
            stat.setAverageXp(stats.getaXp());
            stat.setWins(stats.getWins());
            stat.setSpotted(stats.getSpots());
            stat.setCapturePoints(stats.getCapPts());
            stat.setBattles(stats.getGames());
            stat.setDamageDealt(stats.getDamage());
            stat.setHitsPercentage(stats.getHitsPC());
            stat.setFrags(stats.getFrags());
            stat.setDroppedCapture_points(stats.getDefPts());
            stat.setSurvivedBattles(stats.getSur());
        }
        return stat;
    }

    @Override
    public String toString() {
        return "Statistics{" +
                "type=" + type +
                ", spotted=" + spotted +
                ", hits=" + hits +
                ", averageXp=" + averageXp +
                ", draws=" + draws +
                ", wins=" + wins +
                ", losses=" + losses +
                ", capturePoints=" + capturePoints +
                ", battles=" + battles +
                ", damageDealt=" + damageDealt +
                ", hitsPercentage=" + hitsPercentage +
                ", damageReceived=" + damageReceived +
                ", shots=" + shots +
                ", xp=" + xp +
                ", frags=" + frags +
                ", survivedBattles=" + survivedBattles +
                ", droppedCapture_points=" + droppedCapture_points +
                ", avgDamageAssistedTrack=" + avgDamageAssistedTrack +
                ", avgDamageBlocked=" + avgDamageBlocked +
                ", directHitsReceived=" + directHitsReceived +
                ", explosionHits=" + explosionHits +
                ", piercingsReceived=" + piercingsReceived +
                ", piercings=" + piercings +
                ", avgDamageAssisted=" + avgDamageAssisted +
                ", avgDamageAssistedRadio=" + avgDamageAssistedRadio +
                ", noDamageDirectHitsReceived=" + noDamageDirectHitsReceived +
                ", explosionHitsReceived=" + explosionHitsReceived +
                ", tankingFactor=" + tankingFactor +
                '}';
    }

    public StatsType getType() {
        return type;
    }

    public void setType(StatsType type) {
        this.type = type;
    }

    public int getSpotted() {
        return spotted;
    }

    public void setSpotted(int spotted) {
        this.spotted = spotted;
    }

    public int getHits() {
        return hits;
    }

    public void setHits(int hits) {
        this.hits = hits;
    }

    public int getAverageXp() {
        return averageXp;
    }

    public void setAverageXp(int averageXp) {
        this.averageXp = averageXp;
    }

    public int getDraws() {
        return draws;
    }

    public void setDraws(int draws) {
        this.draws = draws;
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

    public int getCapturePoints() {
        return capturePoints;
    }

    public void setCapturePoints(int capturePoints) {
        this.capturePoints = capturePoints;
    }

    public int getBattles() {
        return battles;
    }

    public void setBattles(int battles) {
        this.battles = battles;
    }

    public int getDamageDealt() {
        return damageDealt;
    }

    public void setDamageDealt(int damageDealt) {
        this.damageDealt = damageDealt;
    }

    public int getHitsPercentage() {
        return hitsPercentage;
    }

    public void setHitsPercentage(int hitsPercentage) {
        this.hitsPercentage = hitsPercentage;
    }

    public int getDamageReceived() {
        return damageReceived;
    }

    public void setDamageReceived(int damageReceived) {
        this.damageReceived = damageReceived;
    }

    public int getShots() {
        return shots;
    }

    public void setShots(int shots) {
        this.shots = shots;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public int getFrags() {
        return frags;
    }

    public void setFrags(int frags) {
        this.frags = frags;
    }

    public int getSurvivedBattles() {
        return survivedBattles;
    }

    public void setSurvivedBattles(int survivedBattles) {
        this.survivedBattles = survivedBattles;
    }

    public int getDroppedCapture_points() {
        return droppedCapture_points;
    }

    public void setDroppedCapture_points(int droppedCapture_points) {
        this.droppedCapture_points = droppedCapture_points;
    }

    public double getAvgDamageAssistedTrack() {
        return avgDamageAssistedTrack;
    }

    public void setAvgDamageAssistedTrack(double avgDamageAssistedTrack) {
        this.avgDamageAssistedTrack = avgDamageAssistedTrack;
    }

    public double getAvgDamageBlocked() {
        return avgDamageBlocked;
    }

    public void setAvgDamageBlocked(double avgDamageBlocked) {
        this.avgDamageBlocked = avgDamageBlocked;
    }

    public int getDirectHitsReceived() {
        return directHitsReceived;
    }

    public void setDirectHitsReceived(int directHitsReceived) {
        this.directHitsReceived = directHitsReceived;
    }

    public int getExplosionHits() {
        return explosionHits;
    }

    public void setExplosionHits(int explosionHits) {
        this.explosionHits = explosionHits;
    }

    public int getPiercingsReceived() {
        return piercingsReceived;
    }

    public void setPiercingsReceived(int piercingsReceived) {
        this.piercingsReceived = piercingsReceived;
    }

    public int getPiercings() {
        return piercings;
    }

    public void setPiercings(int piercings) {
        this.piercings = piercings;
    }

    public double getAvgDamageAssisted() {
        return avgDamageAssisted;
    }

    public void setAvgDamageAssisted(double avgDamageAssisted) {
        this.avgDamageAssisted = avgDamageAssisted;
    }

    public double getAvgDamageAssistedRadio() {
        return avgDamageAssistedRadio;
    }

    public void setAvgDamageAssistedRadio(double avgDamageAssistedRadio) {
        this.avgDamageAssistedRadio = avgDamageAssistedRadio;
    }

    public int getNoDamageDirectHitsReceived() {
        return noDamageDirectHitsReceived;
    }

    public void setNoDamageDirectHitsReceived(int noDamageDirectHitsReceived) {
        this.noDamageDirectHitsReceived = noDamageDirectHitsReceived;
    }

    public int getExplosionHitsReceived() {
        return explosionHitsReceived;
    }

    public void setExplosionHitsReceived(int explosionHitsReceived) {
        this.explosionHitsReceived = explosionHitsReceived;
    }

    public double getTankingFactor() {
        return tankingFactor;
    }

    public void setTankingFactor(double tankingFactor) {
        this.tankingFactor = tankingFactor;
    }
}
