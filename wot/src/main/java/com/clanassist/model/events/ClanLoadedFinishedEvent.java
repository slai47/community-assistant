package com.clanassist.model.events;

/**
 * Created by Harrison on 1/20/2015.
 */
public class ClanLoadedFinishedEvent {

    private String clanId;
    private boolean merged;

    public boolean isMerged() {
        return merged;
    }

    public void setMerged(boolean merged) {
        this.merged = merged;
    }

    public String getClanId() {
        return clanId;
    }

    public void setClanId(String clanId) {
        this.clanId = clanId;
    }
}
