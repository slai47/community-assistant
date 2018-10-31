package com.clanassist.model.clan;

import com.clanassist.model.player.Player;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Obsidian47 on 3/2/14.
 */
public class Clan {

    //search info
    private int clanId;
    private String name;
    private String abbreviation;
    private String color;
    private int members_count;
    private String motto;
    private Emblems emblem;
    private Calendar createdAt;
    private int owner_id;
    private String ownerName;

    // details sections
    private String description;
    private String descriptionHtml;
    private boolean requestAvailability;
    private List<Player> members;

    private List<Battle> battles;
//    private List<Province> provinces;

    private boolean isClanDisbanded;

    private boolean detailsHit;

//    private int privateChipCount;
//    private int privateTreasury;

//    private int victoryPoints;

    public void readyForSave() {
        battles = null;
        members = null;
        detailsHit = false;
//        privateChipCount = 0;
//        privateTreasury = 0;
    }

    /**
     * returns a copy of the clan object
     *
     * @return - clone of current object
     */
    public Clan copy() {
        Clan another = new Clan();
        another.setClanId(getClanId());
        another.setName(getName());
        another.setAbbreviation(getAbbreviation());
        another.setColor(getColor());
        another.setMembers_count(getMembers_count());
        another.setMotto(getMotto());
        another.setEmblem(getEmblem());
        another.setCreatedAt(getCreatedAt());

        another.setOwner_id(getOwner_id());
        another.setOwnerName(getOwnerName());
//        another.setPrivateChipCount(getPrivateChipCount());
//        another.setPrivateTreasury(getPrivateTreasury());
//        another.setDescription(getDescription());
//        another.setDescriptionHtml(getDescriptionHtml());
//        another.setRequestAvailability(isRequestAvailability());
//        another.setMembers(getMembers());

//        another.setBattles(getBattles());

//        another.setClanDisbanded(isClanDisbanded());
        return another;
    }

    @Override
    public String toString() {
        return "Clan{" +
                "clanId=" + clanId +
                ", name='" + name + '\'' +
                ", abbreviation='" + abbreviation + '\'' +
                ", color='" + color + '\'' +
                ", members_count=" + members_count +
                ", motto='" + motto + '\'' +
                ", emblem=" + emblem +
                ", createdAt=" + createdAt +
//                ", owner_id=" + owner_id +
//                ", ownerName='" + ownerName + '\'' +
                ", description='" + description + '\'' +
                ", descriptionHtml='" + descriptionHtml + '\'' +
                ", requestAvailability=" + requestAvailability +
                ", members=" + members +
                ", battles=" + battles +
                ", isClanDisbanded=" + isClanDisbanded +
                '}';
    }

    public int getClanId() {
        return clanId;
    }

    public void setClanId(int clanId) {
        this.clanId = clanId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getMembers_count() {
        return members_count;
    }

    public void setMembers_count(int members_count) {
        this.members_count = members_count;
    }

    public String getMotto() {
        return motto;
    }

    public void setMotto(String motto) {
        this.motto = motto;
    }

    public Emblems getEmblem() {
        return emblem;
    }

    public void setEmblem(Emblems emblem) {
        this.emblem = emblem;
    }

    public int getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(int owner_id) {
        this.owner_id = owner_id;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescriptionHtml() {
        return descriptionHtml;
    }

    public void setDescriptionHtml(String descriptionHtml) {
        this.descriptionHtml = descriptionHtml;
    }

    public boolean isRequestAvailability() {
        return requestAvailability;
    }

    public void setRequestAvailability(boolean requestAvailability) {
        this.requestAvailability = requestAvailability;
    }

    public List<Player> getMembers() {
        return members;
    }

    public void setMembers(List<Player> members) {
        this.members = members;
    }

    public boolean isClanDisbanded() {
        return isClanDisbanded;
    }

    public void setClanDisbanded(boolean isClanDisbanded) {
        this.isClanDisbanded = isClanDisbanded;
    }

    public Calendar getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Calendar createdAt) {
        this.createdAt = createdAt;
    }

    public List<Battle> getBattles() {
        return battles;
    }

    public void setBattles(List<Battle> battles) {
        this.battles = battles;
    }

//    public List<Province> getProvinces() {
//        return provinces;
//    }
//
//    public void setProvinces(List<Province> provinces) {
//        this.provinces = provinces;
//    }

    public boolean isDetailsHit() {
        return detailsHit;
    }

    public void setDetailsHit(boolean detailsHit) {
        this.detailsHit = detailsHit;
    }

//    public int getPrivateChipCount() {
//        return privateChipCount;
//    }
//
//    public void setPrivateChipCount(int privateChipCount) {
//        this.privateChipCount = privateChipCount;
//    }
//
//    public int getPrivateTreasury() {
//        return privateTreasury;
//    }
//
//    public void setPrivateTreasury(int privateTreasury) {
//        this.privateTreasury = privateTreasury;
//    }
//
//    public int getVictoryPoints() {
//        return victoryPoints;
//    }
//
//    public void setVictoryPoints(int victoryPoints) {
//        this.victoryPoints = victoryPoints;
//    }

}
