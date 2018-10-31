package com.half.wowsca.model.events;

/**
 * Created by slai47 on 5/14/2017.
 */

public class ShipCompareEvent {

    private long shipId;
    private boolean cleared;

    public ShipCompareEvent(long shipId) {
        this.shipId = shipId;
    }

    public long getShipId() {
        return shipId;
    }

    public void setShipId(long shipId) {
        this.shipId = shipId;
    }

    public boolean isCleared() {
        return cleared;
    }

    public void setCleared(boolean cleared) {
        this.cleared = cleared;
    }
}
