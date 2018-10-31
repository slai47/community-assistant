package com.clanassist.model.events;

import com.clanassist.model.clan.Clan;
import com.clanassist.model.player.Player;

/**
 * Created by Harrison on 6/26/2014.
 */
public class ClanPlayerAddRemoveEvent {

    private Clan clan;
    private Player player;
    private boolean remove;
    private boolean fromSearch;

    public Clan getClan() {
        return clan;
    }

    public void setClan(Clan clan) {
        this.clan = clan;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public boolean isRemove() {
        return remove;
    }

    public void setRemove(boolean remove) {
        this.remove = remove;
    }

    public boolean isFromSearch() {
        return fromSearch;
    }

    public void setFromSearch(boolean fromSearch) {
        this.fromSearch = fromSearch;
    }
}
