package com.half.wowsca.model.enums;

/**
 * Created by slai4 on 4/17/2016.
 */
public enum TwitchStatus {

    LIVE(-2),
    YOUTUBE(-1),
    OFFLINE(1);

    int order;

    TwitchStatus(int order) {
        this.order = order;
    }

    public int getOrder() {
        return order;
    }
}
