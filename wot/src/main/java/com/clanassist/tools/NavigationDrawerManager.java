package com.clanassist.tools;

import android.content.Context;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.clanassist.CAApp;
import com.clanassist.SVault;
import com.clanassist.model.clan.Clan;
import com.clanassist.model.enums.DrawerType;
import com.clanassist.model.holders.DrawerChild;
import com.clanassist.model.player.Player;
import com.cp.assist.R;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.utilities.preferences.Prefs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Harrison on 7/1/2014.
 */
public class NavigationDrawerManager {

    public static final int BATTLE_ACTIVITY_ID = 100;
    public static final int CLAN_SEARCH_ID = 201;
    public static final int PLAYER_SEARCH_ID = 301;
    public static final int VEHICLE_STATS_ID = 400;
    public static final int SERVER_INFO_ID = 401;
    public static final int MAPS_ID = 500;
    public static final int DONATE_ID = 600;
    public static final int TWITCH_ID = 601;
    public static final int WEBSITE_AREA = 602;
    public static final int SETTINGS_ID = 700;
    public static final int CLAN_GROUP_ID = 200;
    public static final int PLAYER_GROUP_ID = 300;
    public static final int DEFAULT_GROUP = 0;

    public static Drawer.Result createDrawer(AppCompatActivity act,
                                             Toolbar bar,
//                                             ActionBarDrawerToggle toggle,
                                             Drawer.OnDrawerItemClickListener listener) {
//        boolean isLightTheme = CAApp.isLightTheme(act);

//        AccountHeader.Result header = new AccountHeader()
//                .withActivity(act)
//                .withHeaderBackground((!isLightTheme ? R.color.black : R.color.white)).build();

        int defaultSelection = 0;
        Prefs prefs = new Prefs(act);
        int id = prefs.getInt(SVault.PREF_SELECTED_USER_ID, 0);
        if (id != 0) {
            defaultSelection = 1;
        }

        Drawer.Result result = new Drawer()
                .withActivity(act)
                .withToolbar(bar)
//                .withAccountHeader(header)
                .addDrawerItems(
                        getDrawerItemList(act)
                )
                .withOnDrawerItemClickListener(listener)
                .withSelectedItem(defaultSelection)
                .build();
        return result;
    }

    private static PrimaryDrawerItem createPrimary(String name, int identifier, DrawerType type, int icon, boolean isGroup, boolean isLightTheme) {
        PrimaryDrawerItem item = new PrimaryDrawerItem();
        item.withName(name);
        item.withIdentifier(identifier);
        DrawerChild child = new DrawerChild();
        child.setTitle(name);
        child.setType(type);
        item.setTag(child);
        if (icon > 0) {
            item.withIcon(icon);
        }
        if (isGroup) {
            item.setEnabled(false);
            item.setDisabledTextColorRes((!isLightTheme ? R.color.white : R.color.black)); // change color
        }
        return item;
    }

    private static SecondaryDrawerItem createSecondary(String name, int id, DrawerType type, int icon) {
        SecondaryDrawerItem item = new SecondaryDrawerItem();
        item.withName(name);
        item.withIdentifier(id);

        DrawerChild child = new DrawerChild();
        child.setTitle(name);
        child.setType(type);
        item.setTag(child);

        if (icon > 0) {
            item.setIconRes(icon);
        }
        return item;
    }

//    public static ArrayList<IProfile> getServerArray(Context ctx){
//        ArrayList<IProfile> items = new ArrayList<IProfile>();
//
//        Server saved = CAApp.getServerType(ctx);
//        ProfileDrawerItem savedItem = new ProfileDrawerItem().withName(saved.getServerName().toUpperCase())
//                .withIcon(ctx.getResources().getDrawable(saved.getResDrawable()))
//                .withIdentifier(saved.ordinal());
//        items.add(savedItem);
//
//        for(Server s : Server.values()){
//            if(s != saved) {
//                ProfileDrawerItem item = new ProfileDrawerItem().withName(s.getServerName().toUpperCase())
//                        .withIcon(ctx.getResources().getDrawable(s.getResDrawable()))
//                        .withIdentifier(s.ordinal());
//                items.add(item);
//            }
//        }
//        return items;
//    }

    public static IDrawerItem[] getDrawerItemList(Context ctx) {
        boolean isLightTheme = CAApp.isLightTheme(ctx);

        String current = CAApp.getAppLanguage(ctx);
        Locale myLocale = new Locale(current);
        Locale.setDefault(myLocale);
        Configuration config = new Configuration();
        config.locale = myLocale;
        ctx.getResources().updateConfiguration(config,
                ctx.getResources().getDisplayMetrics());

        List<IDrawerItem> items = new ArrayList<IDrawerItem>();
        String name = CAApp.getDefaultName(ctx);
        int id = CAApp.getDefaultId(ctx);
        if (id != 0) {
            PrimaryDrawerItem selectedUser = new PrimaryDrawerItem();
            selectedUser.withName(name);
            selectedUser.withIdentifier(id);
            DrawerChild child = new DrawerChild();
            child.setTitle(name);
            child.setId(id);
            child.setType(DrawerType.PLAYER);
            selectedUser.setIconRes((isLightTheme ? R.drawable.ic_drawer_favorite_light : R.drawable.ic_drawer_favorite));
            selectedUser.setTag(child);
            items.add(selectedUser);
        }

        items.add(createPrimary(ctx.getString(R.string.drawer_home), DEFAULT_GROUP, DrawerType.DEFAULT, (isLightTheme ? R.drawable.ic_drawer_home_light : R.drawable.ic_drawer_home), false, isLightTheme));

        items.add(new DividerDrawerItem());

        items.add(createPrimary(ctx.getString(R.string.drawer_player), PLAYER_GROUP_ID, DrawerType.PLAYER, (isLightTheme ? R.drawable.ic_drawer_player_light : R.drawable.ic_drawer_player), true, isLightTheme));

        items.add(createSecondary(ctx.getString(R.string.drawer_search), PLAYER_SEARCH_ID, DrawerType.PLAYER, (isLightTheme ? R.drawable.ic_search_light : R.drawable.ic_search)));

        Map<Integer, Player> player = CPManager.getSavedPlayers(ctx);
        if (player != null) {
            List<Player> playerList = new ArrayList<Player>(player.values());
            try {
                Collections.sort(playerList, new Comparator<Player>() {
                    @Override
                    public int compare(Player lhs, Player rhs) {
                        return lhs.getName().compareToIgnoreCase(rhs.getName());
                    }
                });
            } catch (Exception e) {
            }
            for (Player p : playerList) {
                if (id != p.getId())
                    items.add(createSecondary(p.getName(), p.getId(), DrawerType.PLAYER, 0));
            }
        }

        items.add(new DividerDrawerItem());

        items.add(createPrimary(ctx.getString(R.string.drawer_clan), CLAN_GROUP_ID, DrawerType.CLAN, (isLightTheme ? R.drawable.ic_drawer_clan_light : R.drawable.ic_drawer_clan), true, isLightTheme));

        items.add(createSecondary(ctx.getString(R.string.drawer_search), CLAN_SEARCH_ID, DrawerType.CLAN, (isLightTheme ? R.drawable.ic_search_light : R.drawable.ic_search)));

//        items.add(createSecondary(ctx.getString(R.string.drawer_battle_activity), BATTLE_ACTIVITY_ID, DrawerType.ACTIVITY, (isLightTheme ? R.drawable.ic_battle_activity_light : R.drawable.ic_battle_activity)));

        Map<Integer, Clan> clans = CPManager.getSavedClans(ctx);
        if (clans != null) {
            List<Clan> clanList = new ArrayList<Clan>(clans.values());
            try {
                Collections.sort(clanList, new Comparator<Clan>() {
                    @Override
                    public int compare(Clan lhs, Clan rhs) {
                        return lhs.getAbbreviation().compareToIgnoreCase(rhs.getAbbreviation());
                    }
                });
            } catch (Exception e) {
            }
            for (Clan c : clanList) {
                items.add(createSecondary(c.getAbbreviation(), c.getClanId(), DrawerType.CLAN, 0));
            }
        }

        items.add(new DividerDrawerItem());

        items.add(createPrimary(ctx.getString(R.string.drawer_encyclopedia), VEHICLE_STATS_ID, DrawerType.WN8, (isLightTheme ? R.drawable.ic_encyclopedia_light : R.drawable.ic_encyclopedia), false, isLightTheme));
        items.add(createPrimary(ctx.getString(R.string.drawer_donate), DONATE_ID, DrawerType.DONATE, 0, false, isLightTheme));
        items.add(createPrimary(ctx.getString(R.string.drawer_twitch_youtube), TWITCH_ID, DrawerType.TWITCH, 0, false, isLightTheme));
        items.add(createPrimary(ctx.getString(R.string.drawer_websites), WEBSITE_AREA, DrawerType.WEBSITE, 0, false, isLightTheme));
//        items.add(createPrimary(ctx.getString(R.string.drawer_maps), MAPS_ID, DrawerType.MAPS, (isLightTheme ? R.drawable.ic_drawer_maps_light : R.drawable.ic_drawer_maps), false, isLightTheme));
        items.add(createPrimary(ctx.getString(R.string.drawer_server_info), SERVER_INFO_ID, DrawerType.SERVER_INFO, 0, false, isLightTheme));

        items.add(createPrimary(ctx.getString(R.string.drawer_about), SETTINGS_ID, DrawerType.SETTINGS, (isLightTheme ? R.drawable.ic_drawer_settings_light : R.drawable.ic_drawer_settings), false, isLightTheme));

        IDrawerItem[] array = new IDrawerItem[items.size()];
        for (int i = 0; i < items.size(); i++) {
            array[i] = items.get(i);
        }

        return array;
    }
}
