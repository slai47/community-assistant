package com.half.wowsca.model.saveobjects;

import com.half.wowsca.model.Achievement;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by slai4 on 9/22/2015.
 */
public class SavedAchievements {

    private List<List<Achievement>> savedAchievements;

    public SavedAchievements() {
        savedAchievements = new ArrayList<List<Achievement>>();
    }

    public List<List<Achievement>> getSavedAchievements() {
        return savedAchievements;
    }

    public void setSavedAchievements(List<List<Achievement>> savedAchievements) {
        this.savedAchievements = savedAchievements;
    }
}
