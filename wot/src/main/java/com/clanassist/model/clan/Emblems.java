package com.clanassist.model.clan;

import android.text.TextUtils;

/**
 * Created by Obsidian47 on 3/2/14.
 * <p/>
 * Used for clan icons on different sizes.
 */
public class Emblems {

    private String bw_tank; // 64
    private String xlarge; //256
    private String large; //128
    private String small; //24
    private String medium; //32

    public String getLargest() {
        String ret = small;
        if (!TextUtils.isEmpty(medium)) {
            ret = medium;
        }
        if (!TextUtils.isEmpty(bw_tank)) {
            ret = bw_tank;
        }
        if (!TextUtils.isEmpty(large)) {
            ret = large;
        }
        if (!TextUtils.isEmpty(xlarge)) {
            ret = xlarge;
        }
        return ret;
    }

    public String getLarge() {
        return large;
    }

    public void setLarge(String large) {
        this.large = large;
    }

    public String getSmall() {
        return small;
    }

    public void setSmall(String small) {
        this.small = small;
    }

    public String getBw_tank() {
        return bw_tank;
    }

    public void setBw_tank(String bw_tank) {
        this.bw_tank = bw_tank;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    public String getXlarge() {
        return xlarge;
    }

    public void setXlarge(String xlarge) {
        this.xlarge = xlarge;
    }
}
