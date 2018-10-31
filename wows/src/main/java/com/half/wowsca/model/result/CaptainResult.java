package com.half.wowsca.model.result;

import com.half.wowsca.model.Captain;

/**
 * Created by slai4 on 9/15/2015.
 */
public class CaptainResult {

    private Captain captain;

    private boolean hidden;

    public Captain getCaptain() {
        return captain;
    }

    public void setCaptain(Captain captain) {
        this.captain = captain;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }
}
