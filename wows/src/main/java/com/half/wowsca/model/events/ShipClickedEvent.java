package com.half.wowsca.model.events;

/**
 * Created by slai4 on 12/14/2015.
 */
public class ShipClickedEvent {

    private long id;

    public ShipClickedEvent(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
