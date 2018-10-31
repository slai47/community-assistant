package com.half.wowsca.model.ranked;

/**
 * Created by slai4 on 11/29/2015.
 */
public class SeasonInfo {

    private String seasonNum;
    private Integer seasonInt;

    private SeasonStats solo;
    private SeasonStats div2;
    private SeasonStats div3;

    public String getSeasonNum() {
        return seasonNum;
    }

    public void setSeasonNum(String seasonNum) {
        this.seasonNum = seasonNum;
    }

    public SeasonStats getSolo() {
        return solo;
    }

    public void setSolo(SeasonStats solo) {
        this.solo = solo;
    }

    public SeasonStats getDiv2() {
        return div2;
    }

    public void setDiv2(SeasonStats div2) {
        this.div2 = div2;
    }

    public SeasonStats getDiv3() {
        return div3;
    }

    public void setDiv3(SeasonStats div3) {
        this.div3 = div3;
    }

    public Integer getSeasonInt() {
        return seasonInt;
    }

    public void setSeasonInt(Integer seasonInt) {
        this.seasonInt = seasonInt;
    }
}
