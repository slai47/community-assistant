package com.clanassist.model.events;

import com.clanassist.model.clan.Clan;

/**
 * Created by Harrison on 6/26/2014.
 */
public class UpdateClanListEvent {

    private Clan clan;

    public Clan getClan() {
        return clan;
    }

    public void setClan(Clan clan) {
        this.clan = clan;
    }
}
