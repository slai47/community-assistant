package com.clanassist.model.clan.globalwar;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Obsidian47 on 3/9/14.
 * <p/>
 * used for information on a clan wars map location
 */
@Deprecated
public class Province {

    private String status;
    private List<String> neighbors;
    private String province_I18N;
    private int revenue;
    private int vehicleMaxLevel;
    private String arenaName;
    private String regionID;
    private String region_i18n;
    private int clanId;
    private String primaryRegion;
    private String provinceID;
    private Calendar primeTime;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getNeighbors() {
        return neighbors;
    }

    public void setNeighbors(List<String> neighbors) {
        this.neighbors = neighbors;
    }

    public String getProvince_I18N() {
        return province_I18N;
    }

    public void setProvince_I18N(String province_I18N) {
        this.province_I18N = province_I18N;
    }

    public int getRevenue() {
        return revenue;
    }

    public void setRevenue(int revenue) {
        this.revenue = revenue;
    }

    public int getVehicleMaxLevel() {
        return vehicleMaxLevel;
    }

    public void setVehicleMaxLevel(int vehicleMaxLevel) {
        this.vehicleMaxLevel = vehicleMaxLevel;
    }

    public String getArenaName() {
        return arenaName;
    }

    public void setArenaName(String arenaName) {
        this.arenaName = arenaName;
    }

    public String getRegionID() {
        return regionID;
    }

    public void setRegionID(String regionID) {
        this.regionID = regionID;
    }

    public String getRegion_i18n() {
        return region_i18n;
    }

    public void setRegion_i18n(String region_i18n) {
        this.region_i18n = region_i18n;
    }

    public int getClanId() {
        return clanId;
    }

    public void setClanId(int clanId) {
        this.clanId = clanId;
    }

    public String getPrimaryRegion() {
        return primaryRegion;
    }

    public void setPrimaryRegion(String primaryRegion) {
        this.primaryRegion = primaryRegion;
    }

    public String getProvinceID() {
        return provinceID;
    }

    public void setProvinceID(String provinceID) {
        this.provinceID = provinceID;
    }

    public Calendar getPrimeTime() {
        return primeTime;
    }

    public void setPrimeTime(Calendar primeTime) {
        this.primeTime = primeTime;
    }
}
