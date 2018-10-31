package com.clanassist.model.infoobj;

/**
 * Created by slai4 on 3/6/2016.
 */
public class CombinedInfoObject {

    private Tank tank;
    private VehicleWN8 wn8;

    public CombinedInfoObject(Tank tank, VehicleWN8 wn8) {
        this.tank = tank;
        this.wn8 = wn8;
    }

    public Tank getTank() {
        return tank;
    }

    public void setTank(Tank tank) {
        this.tank = tank;
    }

    public VehicleWN8 getWn8() {
        return wn8;
    }

    public void setWn8(VehicleWN8 wn8) {
        this.wn8 = wn8;
    }
}
