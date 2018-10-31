package com.half.wowsca.model.ranked;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by slai4 on 11/29/2015.
 */
public class RankedInfo extends SeasonInfo{

    private int maxRank;
    private int startRank;
    private int stars;
    private int rank;
    private int stage;

    public static RankedInfo parse(JSONObject obj){
        RankedInfo info = new RankedInfo();
        if(obj != null){
            JSONObject rank = obj.optJSONObject("rank_info");
            if(rank != null) {
                info.setMaxRank(rank.optInt("max_rank"));
                info.setStartRank(rank.optInt("start_rank"));
                info.setStars(rank.optInt("stars"));
                info.setRank(rank.optInt("rank"));
                info.setStage(rank.optInt("stage"));
            }
            JSONObject solo = obj.optJSONObject("rank_solo");
            JSONObject div2 = obj.optJSONObject("rank_div2");
            JSONObject div3 = obj.optJSONObject("rank_div3");
            if(solo != null){
                SeasonStats soloSeason = SeasonStats.parse(solo);
                info.setSolo(soloSeason);
            }
            if(div2 != null){
                SeasonStats div2Season = SeasonStats.parse(div2);
                info.setDiv2(div2Season);
            }
            if(div3 != null){
                SeasonStats div3Season = SeasonStats.parse(div3);
                info.setDiv3(div3Season);
            }
        }
        return info;
    }

    @Override
    public String toString() {
        return "RankedInfo{" +
                "maxRank=" + maxRank +
                ", startRank=" + startRank +
                ", stars=" + stars +
                ", rank=" + rank +
                ", stage=" + stage +
                ", solo= " + getSolo() +
                '}';
    }

    public int getMaxRank() {
        return maxRank;
    }

    public void setMaxRank(int maxRank) {
        this.maxRank = maxRank;
    }

    public int getStartRank() {
        return startRank;
    }

    public void setStartRank(int startRank) {
        this.startRank = startRank;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getStage() {
        return stage;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }

}
