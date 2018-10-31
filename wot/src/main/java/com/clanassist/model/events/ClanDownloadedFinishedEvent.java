package com.clanassist.model.events;

/**
 * Created by Harrison on 1/13/2015.
 */
public class ClanDownloadedFinishedEvent {

    private String clanId;
    private boolean cancelled;

    public String getClanId() {
        return clanId;
    }

    public void setClanId(String clanId) {
        this.clanId = clanId;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
