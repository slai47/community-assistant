package com.half.wowsca.model.events;

/**
 * Created by slai47 on 11/1/2016.
 */

public class ProgressEvent {

    private boolean refreshing;

    public ProgressEvent(boolean refreshing){
        this.refreshing = refreshing;
    }

    public boolean isRefreshing() {
        return refreshing;
    }
}