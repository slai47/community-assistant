package com.clanassist.model.clan;

import java.util.Calendar;

/**
 * Created by Obsidian47 on 3/2/14.
 */
public class Battle {

    private String province;
    private String provinceName;
    private String arenaName;
    private String arenaName_i18n;

    private boolean started;
    private Calendar time;
    private String type;
    private int homeTeamId;
    private String homeName;
    private int awayTeamId;
    private String awayName;
    private String mapId;

    @Override
    public String toString() {
        return "Battle{" +
                "provinces='" + province + '\'' +
                ", started=" + started +
                ", time=" + time +
                ", type='" + type + '\'' +
                ", homeTeamId=" + homeTeamId +
                ", homeName='" + homeName + '\'' +
                ", awayTeamId=" + awayTeamId +
                ", awayName='" + awayName + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        int result = province != null ? province.hashCode() : 0;
        result = 31 * result + (started ? 1 : 0);
        result = 31 * result + (time != null ? time.hashCode() : 0);
        result = 31 * result + (arenaName != null ? arenaName.hashCode() : 0);
        result = 31 * result + (arenaName != null ? arenaName_i18n.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + homeTeamId;
        result = 31 * result + (homeName != null ? homeName.hashCode() : 0);
        result = 31 * result + awayTeamId;
        result = 31 * result + (awayName != null ? awayName.hashCode() : 0);
        return result;
    }

    public int getUuid() {

        return hashCode();
    }


    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public Calendar getTime() {
        return time;
    }

    public void setTime(Calendar time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getHomeTeamId() {
        return homeTeamId;
    }

    public void setHomeTeamId(int homeTeamId) {
        this.homeTeamId = homeTeamId;
    }

    public String getHomeName() {
        return homeName;
    }

    public void setHomeName(String homeName) {
        this.homeName = homeName;
    }

    public int getAwayTeamId() {
        return awayTeamId;
    }

    public void setAwayTeamId(int awayTeamId) {
        this.awayTeamId = awayTeamId;
    }

    public String getAwayName() {
        return awayName;
    }

    public void setAwayName(String awayName) {
        this.awayName = awayName;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String provinces) {
        this.province = provinces;
    }

    public String getArenaName() {
        return arenaName;
    }

    public void setArenaName(String arenaName) {
        this.arenaName = arenaName;
    }

    public String getArenaName_i18n() {
        return arenaName_i18n;
    }

    public void setArenaName_i18n(String arenaName_i18n) {
        this.arenaName_i18n = arenaName_i18n;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getMapId() {
        return mapId;
    }

    public void setMapId(String mapId) {
        this.mapId = mapId;
    }
}
