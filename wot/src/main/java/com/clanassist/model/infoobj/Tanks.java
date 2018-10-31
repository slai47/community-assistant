package com.clanassist.model.infoobj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by slai4 on 2/25/2016.
 */
public class Tanks {

    private Map<Integer, Tank> tanksList;

    public Tank getTank(int id){
        if(tanksList != null){
            return tanksList.get(id);
        } else {
            return null;
        }
    }

    public Map<Integer, Tank> getTanksList() {
        return tanksList;
    }

    public void setTanksList(Map<Integer, Tank> tanksList) {
        this.tanksList = tanksList;
    }
}
