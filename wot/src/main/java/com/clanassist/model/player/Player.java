package com.clanassist.model.player;

import com.clanassist.model.statistics.Statistics;
import com.clanassist.tools.PlayerHelper;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Created by Obsidian47 on 3/2/14.
 */
public class Player implements Comparable<Player> {

    //for lists
    private String name;
    private int id;

    //for details
    private Map<String, Integer> awards;

    //not ready yet
    private PlayerClan clanInfo;

    private Statistics overallStats;
    private Statistics clanStats;
    private Statistics strongholdStats;

    private int maxDamage;
    private int maxDamageTankId;

    private int maxFrags;
    private int maxFragsTankId;

    private int maxXp;
    private int maxXpTankId;

    private Calendar lastBattleTime;
    private Calendar createdAt;

    private Calendar lastLogoutTime;

    private int globalRating;

    private List<PlayerVehicleInfo> playerVehicleInfoList;

    private int bestTankIdWN8;
    private int bestTankWN8;

    private int treesCut;

    private float WN8;
    private float clanWN8;
    private float strongholdWN8;

    private Map<String, Integer> achievements;

    private Badges badges;

    private PlayerGraphs playerGraphs;

    private WN8StatsInfo wn8StatsInfo;

    /**
     * returns a basic player object from lists.
     *
     * @return clone of the current player object
     */
    public Player copy() {
        Player another = new Player();
        another.setName(getName());
        another.setId(getId());
        return another;
    }

    @Override
    public int compareTo(Player player) {
        int ret = 0;
        PlayerClan info1 = clanInfo;
        PlayerClan info2 = player.getClanInfo();
        if (info1 != null && info2 != null) {
            String role = info1.getRole();
            String role2 = info2.getRole();
            Integer rank1 = PlayerHelper.getRanks().get(role);
            Integer rank2 = PlayerHelper.getRanks().get(role2);
            int player1 = (rank1 != null) ? rank1 : 0;
            int player2 = (rank2 != null) ? rank2 : 0;
            if (player2 != player1)
                ret = player2 - player1;
        }
        return ret;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Map<String, Integer> getAwards() {
        return awards;
    }

    public void setAwards(Map<String, Integer> awards) {
        this.awards = awards;
    }

    public PlayerClan getClanInfo() {
        return clanInfo;
    }

    public void setClanInfo(PlayerClan clanInfo) {
        this.clanInfo = clanInfo;
    }

    public Statistics getOverallStats() {
        return overallStats;
    }

    public void setOverallStats(Statistics overallStats) {
        this.overallStats = overallStats;
    }

    public Statistics getClanStats() {
        return clanStats;
    }

    public void setClanStats(Statistics clanStats) {
        this.clanStats = clanStats;
    }

    public List<PlayerVehicleInfo> getPlayerVehicleInfoList() {
        return playerVehicleInfoList;
    }

    public void setPlayerVehicleInfoList(List<PlayerVehicleInfo> playerVehicleInfoList) {
        this.playerVehicleInfoList = playerVehicleInfoList;
    }

    public int getMaxDamage() {
        return maxDamage;
    }

    public void setMaxDamage(int maxDamage) {
        this.maxDamage = maxDamage;
    }

    public int getMaxDamageTankId() {
        return maxDamageTankId;
    }

    public void setMaxDamageTankId(int maxDamageTankId) {
        this.maxDamageTankId = maxDamageTankId;
    }

    public Calendar getLastBattleTime() {
        return lastBattleTime;
    }

    public void setLastBattleTime(Calendar lastBattleTime) {
        this.lastBattleTime = lastBattleTime;
    }

    public int getGlobalRating() {
        return globalRating;
    }

    public void setGlobalRating(int globalRating) {
        this.globalRating = globalRating;
    }

    public float getWN8() {
        return WN8;
    }

    public void setWN8(float WN8) {
        this.WN8 = WN8;
    }

    public Badges getBadges() {
        return badges;
    }

    public void setBadges(Badges badges) {
        this.badges = badges;
    }

    public Calendar getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Calendar createdAt) {
        this.createdAt = createdAt;
    }

    public int getBestTankIdWN8() {
        return bestTankIdWN8;
    }

    public void setBestTankIdWN8(int bestTankIdWN8) {
        this.bestTankIdWN8 = bestTankIdWN8;
    }

    public int getBestTankWN8() {
        return bestTankWN8;
    }

    public void setBestTankWN8(int bestTankWN8) {
        this.bestTankWN8 = bestTankWN8;
    }

    public int getMaxFrags() {
        return maxFrags;
    }

    public void setMaxFrags(int maxFrags) {
        this.maxFrags = maxFrags;
    }

    public int getMaxFragsTankId() {
        return maxFragsTankId;
    }

    public void setMaxFragsTankId(int maxFragsTankId) {
        this.maxFragsTankId = maxFragsTankId;
    }

    public int getMaxXp() {
        return maxXp;
    }

    public void setMaxXp(int maxXp) {
        this.maxXp = maxXp;
    }

    public int getMaxXpTankId() {
        return maxXpTankId;
    }

    public void setMaxXpTankId(int maxXpTankId) {
        this.maxXpTankId = maxXpTankId;
    }

    public int getTreesCut() {
        return treesCut;
    }

    public void setTreesCut(int treesCut) {
        this.treesCut = treesCut;
    }

    public Calendar getLastLogoutTime() {
        return lastLogoutTime;
    }

    public void setLastLogoutTime(Calendar lastLogoutTime) {
        this.lastLogoutTime = lastLogoutTime;
    }

    public PlayerGraphs getPlayerGraphs() {
        return playerGraphs;
    }

    public void setPlayerGraphs(PlayerGraphs playerGraphs) {
        this.playerGraphs = playerGraphs;
    }

    public float getClanWN8() {
        return clanWN8;
    }

    public void setClanWN8(float clanWN8) {
        this.clanWN8 = clanWN8;
    }

    public WN8StatsInfo getWn8StatsInfo() {
        return wn8StatsInfo;
    }

    public void setWn8StatsInfo(WN8StatsInfo wn8StatsInfo) {
        this.wn8StatsInfo = wn8StatsInfo;
    }

    public Statistics getStrongholdStats() {
        return strongholdStats;
    }

    public void setStrongholdStats(Statistics strongholdStats) {
        this.strongholdStats = strongholdStats;
    }

    public float getStrongholdWN8() {
        return strongholdWN8;
    }

    public void setStrongholdWN8(float strongholdWN8) {
        this.strongholdWN8 = strongholdWN8;
    }

    public Map<String, Integer> getAchievements() {
        return achievements;
    }

    public void setAchievements(Map<String, Integer> achievements) {
        this.achievements = achievements;
    }
}
