package com.clanassist.model.infoobj;

import java.util.Map;

/**
 * Created by slai4 on 2/25/2016.
 */
public class Achievements {

    private Map<String, Achievement> achievementList;

    public Achievement getAchievement(String id){
        if(achievementList != null){
            return achievementList.get(id);
        } else {
            return null;
        }
    }

    public Map<String, Achievement> getAchievements() {
        return achievementList;
    }

    public void setAchievements(Map<String, Achievement> tanksList) {
        this.achievementList = tanksList;
    }
}
