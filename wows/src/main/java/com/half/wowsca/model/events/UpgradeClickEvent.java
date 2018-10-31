package com.half.wowsca.model.events;

/**
 * Created by slai4 on 4/26/2016.
 */
public class UpgradeClickEvent {

    long id;

    public UpgradeClickEvent(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
