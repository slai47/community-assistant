package com.clanassist.listeners;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.clanassist.CAApp;
import com.clanassist.model.clan.Clan;
import com.clanassist.model.events.ClanPlayerAddRemoveEvent;
import com.clanassist.tools.CPManager;
import com.cp.assist.R;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;

import java.util.Map;

/**
 * Created by Harrison on 6/26/2014.
 */
public class AddRemoveClanListener implements
        View.OnClickListener {

    private Clan clan;
    private Context ctx;
    private boolean fromSearch;
    private CheckBox box;

    public AddRemoveClanListener(Context ctx, Clan clan, CheckBox box, boolean fromSearch) {
        this.clan = clan;
        this.ctx = ctx;
        this.fromSearch = fromSearch;
        this.box = box;
    }

    @Override
    public void onClick(View view) {
        Map<Integer, Clan> clans = CPManager.getSavedClans(ctx);
        if (clans.get(clan.getClanId()) == null) {
            ClanPlayerAddRemoveEvent event = new ClanPlayerAddRemoveEvent();
            event.setClan(clan);
            CAApp.getEventBus().post(event);
            box.setChecked(true);
            Toast.makeText(ctx, clan.getAbbreviation() + " " + ctx.getString(R.string.list_clan_added_message), Toast.LENGTH_SHORT).show();
            Answers.getInstance().logCustom(new CustomEvent("CP Added").putCustomAttribute("Type", "Clan"));
        } else {
            ClanPlayerAddRemoveEvent event = new ClanPlayerAddRemoveEvent();
            event.setClan(clan);
            event.setRemove(true);
            event.setFromSearch(fromSearch);
            CAApp.getEventBus().post(event);
            box.setChecked(false);
            Toast.makeText(ctx, clan.getAbbreviation() + " " + ctx.getString(R.string.list_clan_removed_message), Toast.LENGTH_SHORT).show();
            Answers.getInstance().logCustom(new CustomEvent("CP Removed").putCustomAttribute("Type", "Clan"));
        }
    }
}
