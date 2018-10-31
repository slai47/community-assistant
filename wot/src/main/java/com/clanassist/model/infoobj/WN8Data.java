package com.clanassist.model.infoobj;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by slai4 on 3/2/2016.
 */
public class WN8Data {

    private Map<Integer, VehicleWN8> wn8Map;

    public VehicleWN8 getWN8(Integer id){
        if(wn8Map != null){
            return wn8Map.get(id);
        } else {
            return null;
        }
    }

    public void addWN8(VehicleWN8 wn8){
        if(wn8Map == null)
            wn8Map = new HashMap<Integer, VehicleWN8>();
        wn8Map.put(wn8.getId(), wn8);
    }

    public Map<Integer, VehicleWN8> getWN8s() {
        return wn8Map;
    }

    public void setWN8s(Map<Integer, VehicleWN8> tanksList) {
        this.wn8Map = tanksList;
    }

}
