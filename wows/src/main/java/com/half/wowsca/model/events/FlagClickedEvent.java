package com.half.wowsca.model.events;

/**
 * Created by slai4 on 4/25/2016.
 */
public class FlagClickedEvent {

    private long id;

    public FlagClickedEvent(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
