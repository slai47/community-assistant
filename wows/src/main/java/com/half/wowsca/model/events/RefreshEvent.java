package com.half.wowsca.model.events;

/**
 * Created by slai4 on 10/7/2015.
 */
public class RefreshEvent {

    private boolean fromSwipe;

    public RefreshEvent(boolean fromSwipe) {
        this.fromSwipe = fromSwipe;
    }

    public boolean isFromSwipe() {
        return fromSwipe;
    }

    public void setFromSwipe(boolean fromSwipe) {
        this.fromSwipe = fromSwipe;
    }
}
