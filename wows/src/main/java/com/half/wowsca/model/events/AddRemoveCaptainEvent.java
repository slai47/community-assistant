package com.half.wowsca.model.events;

/**
 * Created by slai4 on 10/10/2015.
 */
public class AddRemoveCaptainEvent {

    private boolean removed;

    public boolean isRemoved() {
        return removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }
}
