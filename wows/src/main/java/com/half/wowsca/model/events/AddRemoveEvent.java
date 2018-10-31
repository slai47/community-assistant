package com.half.wowsca.model.events;

import com.half.wowsca.model.Captain;

/**
 * Created by slai4 on 9/19/2015.
 */
public class AddRemoveEvent {

    private boolean remove;
    private Captain captain;

    public boolean isRemove() {
        return remove;
    }

    public void setRemove(boolean remove) {
        this.remove = remove;
    }

    public Captain getCaptain() {
        return captain;
    }

    public void setCaptain(Captain captain) {
        this.captain = captain;
    }
}
