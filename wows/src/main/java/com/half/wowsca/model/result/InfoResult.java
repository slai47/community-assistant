package com.half.wowsca.model.result;

import com.half.wowsca.model.encyclopedia.items.AchievementInfo;
import com.half.wowsca.model.encyclopedia.items.EquipmentInfo;
import com.half.wowsca.model.encyclopedia.holders.ExteriorHolder;
import com.half.wowsca.model.encyclopedia.items.ShipInfo;
import com.half.wowsca.model.encyclopedia.items.ShipStat;
import com.half.wowsca.model.encyclopedia.holders.CaptainSkillHolder;

import java.util.Map;

/**
 * Created by slai4 on 9/21/2015.
 */
public class InfoResult {

    private Map<Long, ShipInfo> ships;

    private Map<String, AchievementInfo> achievements;

    private Map<Long, ShipStat> shipStat;

    private Map<Long, EquipmentInfo> equipment;

    private ExteriorHolder exteriorItem;

    private CaptainSkillHolder skillHolder;

    @Override
    public String toString() {
        return "InfoResult{" +
                "ships=" + (ships != null) +
                ", achievements=" + (achievements != null) +
                ", shipStat=" + shipStat +
                ", equipment=" + (equipment != null) +
                '}';
    }

    public Map<Long, ShipInfo> getShips() {
        return ships;
    }

    public void setShips(Map<Long, ShipInfo> ships) {
        this.ships = ships;
    }

    public Map<String, AchievementInfo> getAchievements() {
        return achievements;
    }

    public void setAchievements(Map<String, AchievementInfo> achievements) {
        this.achievements = achievements;
    }

    public Map<Long, ShipStat> getShipStat() {
        return shipStat;
    }

    public void setShipStat(Map<Long, ShipStat> shipStat) {
        this.shipStat = shipStat;
    }

    public Map<Long, EquipmentInfo> getEquipment() {
        return equipment;
    }

    public void setEquipment(Map<Long, EquipmentInfo> equipment) {
        this.equipment = equipment;
    }

    public ExteriorHolder getExteriorItem() {
        return exteriorItem;
    }

    public void setExteriorItem(ExteriorHolder exteriorItem) {
        this.exteriorItem = exteriorItem;
    }

    public CaptainSkillHolder getSkillHolder() {
        return skillHolder;
    }

    public void setSkillHolder(CaptainSkillHolder skillHolder) {
        this.skillHolder = skillHolder;
    }
}
