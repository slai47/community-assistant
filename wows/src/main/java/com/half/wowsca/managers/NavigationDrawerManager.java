package com.half.wowsca.managers;

import android.content.Context;
import android.content.res.Configuration;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;

import com.half.wowsca.CAApp;
import com.half.wowsca.R;
import com.half.wowsca.model.Captain;
import com.half.wowsca.model.drawer.DrawerChild;
import com.half.wowsca.model.enums.DrawerType;
import com.half.wowsca.model.enums.Server;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.utilities.logging.Dlog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by slai4 on 11/29/2015.
 */
public class NavigationDrawerManager {

    public static final int SEARCH = 100;
    public static final int CAPTAINS = 700;
    private static final int DONATE_ID = 800;
    public static int SHIPOPEDIA = 200;
    public static int WEBSITES_TOOLS = 300;
    public static int TWITCH = 400;
    public static int YOUTUBERS = 500;
    public static int SETTINGS = 600;
    public static int SERVER = 900;


    public static Drawer createDrawer(AppCompatActivity act, Toolbar bar, Drawer.OnDrawerItemClickListener listener){

        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(act)
                .withHeaderBackground(R.drawable.launcher_icon)
                .build();

        Drawer result = new DrawerBuilder()
                .withActivity(act)
                .withToolbar(bar)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        getDrawerItemList(act)
                )
                .withOnDrawerItemClickListener(listener)
                .withTranslucentStatusBar(false)
                .withSelectedItem(-1)
                .build();
        return result;
    }

    private static PrimaryDrawerItem createPrimary(String name, int identifier, DrawerType type, int icon, boolean isGroup, boolean isDarkTheme) {
        PrimaryDrawerItem item = new PrimaryDrawerItem();
        item.withName(name);
        item.withIdentifier(identifier);
        if (!isGroup){
            DrawerChild child = new DrawerChild();
            child.setTitle(name);
            child.setType(type);
            item.withTag(child);
        }
        if (icon > 0) {
            item.withIcon(icon);
            if(!isDarkTheme) {
                item.withIconColorRes(R.color.material_primary);
                item.withIconTintingEnabled(true);
            }
        }
        return item;
    }

    private static SecondaryDrawerItem createSecondary(String name, long id, Server server, DrawerType type, int icon, boolean isDarkTheme) {
        SecondaryDrawerItem item = new SecondaryDrawerItem();
        item.withName(name);

        DrawerChild child = new DrawerChild();
        child.setTitle(name);
        child.setType(type);
        child.setId(id);
        child.setServer(server);
        item.withTag(child);

        if (icon > 0) {
            item.withIcon(icon);
            if(!isDarkTheme) {
                item.withIconColorRes(R.color.material_primary);
                item.withIconTintingEnabled(true);
            }
        }
        return item;
    }

    public static IDrawerItem[] getDrawerItemList(Context ctx) {
        boolean isDarkTheme = CAApp.isDarkTheme(ctx);

        String current = CAApp.getAppLanguage(ctx);
        Locale myLocale = new Locale(current);
        Configuration config = new Configuration();
        config.locale = myLocale;
        ctx.getResources().updateConfiguration(config,
                ctx.getResources().getDisplayMetrics());

        List<IDrawerItem> items = new ArrayList<IDrawerItem>();

        // add search
        items.add(createPrimary(ctx.getString(R.string.drawer_search), SEARCH, DrawerType.SEARCH, R.drawable.ic_search, false, isDarkTheme));

        // add other players
        items.add(createPrimary(ctx.getString(R.string.drawer_captains), 0, DrawerType.SEARCH, R.drawable.ic_captains, true, isDarkTheme));
        String favorite = CAApp.getSelectedId(ctx);
        Map<String, Captain> captains = CaptainManager.getCaptains(ctx);
        Collection<Captain> caps = captains.values();
        List<Captain> listCaps = new ArrayList<>(caps);
        Collections.sort(listCaps, new Comparator<Captain>() {
            @Override
            public int compare(Captain lhs, Captain rhs) {
                return lhs.getName().compareToIgnoreCase(rhs.getName());
            }
        });
        for (Captain captain : listCaps) {
            if (!CaptainManager.createCapIdStr(captain.getServer(), captain.getId()).equals(favorite)) {
                items.add(createSecondary(captain.getName(), captain.getId(), captain.getServer(), DrawerType.CAPTAIN, 0, isDarkTheme));
            }
        }
        items.add(new DividerDrawerItem());

        // add shipopedia
        items.add(createPrimary(ctx.getString(R.string.action_shipopedia), SHIPOPEDIA, DrawerType.SHIPOPEDIA, 0, false, isDarkTheme));

        // add websites and tools activity
        items.add(createPrimary(ctx.getString(R.string.action_websites), WEBSITES_TOOLS, DrawerType.WEBSITES_TOOLS, 0, false, isDarkTheme));

        // add twitch channels activity
        items.add(createPrimary(ctx.getString(R.string.action_twitch), TWITCH, DrawerType.TWITCH, 0, false, isDarkTheme));


        items.add(createPrimary(ctx.getString(R.string.action_donate), DONATE_ID, DrawerType.DONATE, 0, false, isDarkTheme));

        items.add(createPrimary(ctx.getString(R.string.action_server_info), SERVER, DrawerType.SERVER, 0, false, isDarkTheme));
        // add settings
        items.add(createPrimary(ctx.getString(R.string.action_settings), SETTINGS, DrawerType.SETTINGS, 0, false, isDarkTheme));

        IDrawerItem[] array = new IDrawerItem[items.size()];
        for (int i = 0; i < items.size(); i++) {
            array[i] = items.get(i);
        }

        return array;
    }
}
