package com.half.wowsca.model.saveobjects;

import com.half.wowsca.model.Ship;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by slai4 on 10/16/2015.
 */
public class SavedShips {

    private Map<Long, List<Ship>> savedShips;

    public SavedShips() {
        savedShips = new HashMap<>();
    }

    public Map<Long, List<Ship>> getSavedShips() {
        return savedShips;
    }

    public void setSavedShips(Map<Long, List<Ship>> savedShips) {
        this.savedShips = savedShips;
    }
}
