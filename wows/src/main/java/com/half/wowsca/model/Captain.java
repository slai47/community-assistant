package com.half.wowsca.model;

import com.half.wowsca.model.enums.Server;
import com.half.wowsca.model.ranked.RankedInfo;

import java.util.List;

/**
 * Created by slai4 on 9/15/2015.
 */
public class Captain {

    private int id;
    private String name;
    private Server server;
    private String clanName;

    private CaptainDetails details;

    private Statistics teamBattleDetails;

    private Statistics pveDetails;

    private Statistics pvpDiv3Details;
    private Statistics pvpDiv2Details;
    private Statistics pvpSoloDetails;

    private List<Ship> ships;

    private List<Achievement> achievements;

    private List<RankedInfo> rankedSeasons;

    private CaptainPrivateInformation information;

    public Captain copy() {
        Captain c = new Captain();
        c.setId(id);
        c.setName(name);
        c.setServer(server);
        return c;
    }

    @Override
    public String toString() {
        return "Captain{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", server=" + server +
                ", details=" + details +
                ", achievements=" + achievements +
                ", ships=" + ships +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public List<Ship> getShips() {
        return ships;
    }

    public void setShips(List<Ship> ships) {
        this.ships = ships;
    }

    public List<Achievement> getAchievements() {
        return achievements;
    }

    public void setAchievements(List<Achievement> achievements) {
        this.achievements = achievements;
    }

    public CaptainDetails getDetails() {
        return details;
    }

    public void setDetails(CaptainDetails details) {
        this.details = details;
    }

    public List<RankedInfo> getRankedSeasons() {
        return rankedSeasons;
    }

    public void setRankedSeasons(List<RankedInfo> rankedSeasons) {
        this.rankedSeasons = rankedSeasons;
    }

    public CaptainPrivateInformation getInformation() {
        return information;
    }

    public void setInformation(CaptainPrivateInformation information) {
        this.information = information;
    }

    public Statistics getTeamBattleDetails() {
        return teamBattleDetails;
    }

    public void setTeamBattleDetails(Statistics teamBattleDetails) {
        this.teamBattleDetails = teamBattleDetails;
    }

    public Statistics getPveDetails() {
        return pveDetails;
    }

    public void setPveDetails(Statistics pveDetails) {
        this.pveDetails = pveDetails;
    }

    public Statistics getPvpDiv3Details() {
        return pvpDiv3Details;
    }

    public void setPvpDiv3Details(Statistics pvpDiv3Details) {
        this.pvpDiv3Details = pvpDiv3Details;
    }

    public Statistics getPvpDiv2Details() {
        return pvpDiv2Details;
    }

    public void setPvpDiv2Details(Statistics pvpDiv2Details) {
        this.pvpDiv2Details = pvpDiv2Details;
    }

    public Statistics getPvpSoloDetails() {
        return pvpSoloDetails;
    }

    public void setPvpSoloDetails(Statistics pvpSoloDetails) {
        this.pvpSoloDetails = pvpSoloDetails;
    }

    public String getClanName() {
        return clanName;
    }

    public void setClanName(String clanName) {
        this.clanName = clanName;
    }
}
