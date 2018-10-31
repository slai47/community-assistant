package com.clanassist.listeners;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.clanassist.CAApp;
import com.clanassist.model.events.ClanPlayerAddRemoveEvent;
import com.clanassist.model.player.Player;
import com.clanassist.tools.CPManager;
import com.cp.assist.R;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;

import java.util.Map;

/**
 * Created by Harrison on 6/26/2014.
 */
public class AddRemovePlayerListener implements
        View.OnClickListener {

    private Player player;
    private Context ctx;
    private boolean fromSearch;
    private CheckBox box;

    public AddRemovePlayerListener(Context ctx, Player player1, CheckBox box, boolean fromSearch) {
        this.player = player1;
        this.ctx = ctx;
        this.fromSearch = fromSearch;
        this.box = box;
    }

    @Override
    public void onClick(View view) {
        Map<Integer, Player> players = CPManager.getSavedPlayers(ctx);
        if (players.get(player.getId()) == null) {
            ClanPlayerAddRemoveEvent event = new ClanPlayerAddRemoveEvent();
            event.setPlayer(player);
            CAApp.getEventBus().post(event);
            box.setChecked(true);
            Toast.makeText(ctx, player.getName() + " " + ctx.getString(R.string.list_clan_added_message), Toast.LENGTH_SHORT).show();
            Answers.getInstance().logCustom(new CustomEvent("CP Added").putCustomAttribute("Type", "Player"));
        } else {
            ClanPlayerAddRemoveEvent event = new ClanPlayerAddRemoveEvent();
            event.setPlayer(player);
            event.setRemove(true);
            event.setFromSearch(fromSearch);
            CAApp.getEventBus().post(event);
            box.setChecked(false);
            Toast.makeText(ctx, player.getName() + " " + ctx.getString(R.string.list_clan_removed_message), Toast.LENGTH_SHORT).show();
            Answers.getInstance().logCustom(new CustomEvent("CP Removed").putCustomAttribute("Type", "Player"));
        }
    }
}
