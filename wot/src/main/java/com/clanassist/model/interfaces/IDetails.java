package com.clanassist.model.interfaces;

import android.content.Context;

import com.clanassist.model.clan.Clan;
import com.clanassist.model.player.Player;

/**
 * Created by Harrison on 10/7/2014.
 */
public interface IDetails {

    Player getPlayer(Context ctx);

    Clan getClan(Context ctx);

    boolean isFromSearch();

    String getSortType();

    void setSortType(String string);

    boolean isPlayer();

    boolean hitProfile();

    int getAccountId();
}
