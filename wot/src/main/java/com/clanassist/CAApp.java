package com.clanassist;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.fragment.app.FragmentActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.clanassist.backend.Tasks.DownloadClanAsync;
import com.clanassist.model.infoobj.VehicleWN8;
import com.clanassist.model.enums.Server;
import com.clanassist.tools.CPManager;
import com.clanassist.tools.CPStorageManager;
import com.clanassist.tools.CompareManager;
import com.clanassist.tools.DownloadedClanManager;
import com.clanassist.tools.InfoManager;
import com.clanassist.ui.ActivityFragment;
import com.clanassist.ui.search.CPSearchFragment;
import com.clanassist.ui.search.EncyclopediaSearchFragment;
import com.cp.assist.BuildConfig;
import com.cp.assist.R;
import com.crashlytics.android.Crashlytics;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;
import com.utilities.logging.Dlog;
import com.utilities.preferences.Prefs;

import java.io.File;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Obsidian47 on 2/26/14.
 */
public class CAApp extends Application {

    public static final String SELECTED_SERVER_LANGUAGE = "selected_server_language";
    public static final int NOTIFICATION_NUMBER = 47;
    public static final int DAYS_BETWEEN_DOWNLOAD = 3;
    public static final String PREF_CLAN_UPDATE = "clanUpdate5";
    private static final String APP_LANGUAGE = "appLanguage";

    public static boolean DEVELOPMENT_MODE = false;

    public static final String CREATED_DATABASE = "isCreatedYet"; //remove

    public static final String LAUNCH_AMOUNT = "launch";
    public static final String PREF_FILE = "caapp";
    public static final String SERVER_NAME = "";
    public static final int MAP_ID = 1;

    public static final String WOT_API_SITE_ADDRESS = "http://api.worldoftanks";

    private static final Bus mEventBus = new Bus(ThreadEnforcer.ANY);

    public static final int TIME_DIFFERENCE = 5;
    public static final int MILLIS = 1000;
    public static final int SECONDSMINS = 60;
    public static final int HOURS = 24;
    public static final int EMPTY_NUMBER = -999;
    public static boolean HAS_SHOWN_FIRST_DIALOG = false;

    public static Calendar LAST_REFRESHED_TIME;
    public static boolean IS_SEARCHING = false;

    public static DownloadClanAsync ongoingTask;

    public static int SELECTED_VEHICLE_ID;

//    public static boolean REFRESHINGDB; // remove

    private static InfoManager infoManager;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        DEVELOPMENT_MODE = !BuildConfig.BUILD_TYPE.equals("release");
        Dlog.LOGGING_MODE = DEVELOPMENT_MODE;
        setUpGlobals();

        Prefs pref = new Prefs(getApplicationContext());
        int launch = pref.getInt(LAUNCH_AMOUNT, 0);
        launch++;
        pref.setInt(LAUNCH_AMOUNT, launch);

//        pref.setBoolean(SVault.PREF_FOUNDER, true);
//        setUpDatabase(getApplicationContext());
    }

    private void setUpGlobals() {
        CPManager.clear();
        DownloadedClanManager.init();
        CAApp.SELECTED_VEHICLE_ID = 0;
        infoManager = new InfoManager();
        CPStorageManager.deleteTempClan(getApplicationContext());
        CPStorageManager.deleteTempPlayer(getApplicationContext());
        cleanPrevClanStatsDir();
    }

    private void cleanPrevClanStatsDir() {
        File dir = getApplicationContext().getDir(CPManager.DIRECTORY_NAME, Context.MODE_PRIVATE);
        File prevClanStatsDir = new File(dir, CPStorageManager.PREV_SAVED_CLAN_STATS_DIR);
        if(prevClanStatsDir.exists()){
            CPStorageManager.delete(prevClanStatsDir);
        }
    }

    /**
     * REMOVE THIS
     *
     * @param ctx
     */
//    private void setUpDatabase(Context ctx) {
//        SharedPreferences prefs = getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
//        boolean hasBeenCreatedInserted = prefs.getBoolean(CREATED_DATABASE, false);
//        if (!hasBeenCreatedInserted) {
//            StatsManager.insertAllIntoDatabase(ctx);
//            prefs.edit().putBoolean(CREATED_DATABASE, true).commit();
//        } else {
//            VehicleStatsDS ds = new VehicleStatsDS(ctx);
//            try {
//                ds.open();
//                List<VehicleWN8> list = ds.getAllVehiclesWN8s();
//                ds.close();
//                if (list.isEmpty())
//                    StatsManager.insertAllIntoDatabase(ctx);
//            } catch (SQLException e) {
//            }
//
//        }
//    }

    public static String getApplicationIdURLString(Context ctx) {
        Server s = getServerType(ctx);
        return "application_id=" + s.getApplicationId();
    }

    public static Server getServerType(Context ctx) {
        Prefs pref = new Prefs(ctx);
        Server s = Server.valueOf(pref.getString(SERVER_NAME, Server.US.toString()));
        Crashlytics.setString(SVault.LOG_SERVER, s.toString());
        return s;
    }

    public static String getBaseAddress(Context ctx) {
        Server s = getServerType(ctx);
        return WOT_API_SITE_ADDRESS + s.getSuffix();
    }

    public static String getImageRepo(Context ctx) {
        Server s = getServerType(ctx);
        //https://s3.amazonaws.com/wotca-us/maps/01_karelia_grid.jpg
        String url = "https://s3.amazonaws.com/wotca-us/maps/";
        if (s == Server.EU || s == Server.RU) {
            url = "https://s3.eu-central-1.amazonaws.com/wotca-eu1/maps/";
        }
        return url;
    }

    public static Bus getEventBus() {
        return mEventBus;
    }

    public static boolean canCLanUpdate(Context ctx) {
        boolean canUpdate = true;
        Calendar now = Calendar.getInstance();
        if (LAST_REFRESHED_TIME != null) {
            long then = LAST_REFRESHED_TIME.getTimeInMillis() / MILLIS / SECONDSMINS;
            long nowl = now.getTimeInMillis() / MILLIS / SECONDSMINS;
            if (Math.abs(then - nowl) < TIME_DIFFERENCE) {
                canUpdate = false;
            }
        }
        if (!canUpdate) {
            Toast.makeText(ctx, R.string.system_cant_update_message, Toast.LENGTH_LONG).show();
        }
        return canUpdate;
    }

    public static void taskStarted(DownloadClanAsync task) {
        ongoingTask = task;
    }

    public static void taskEnded() {
        ongoingTask = null;
    }

    public static boolean isTaskRunning() {
        return ongoingTask != null;
    }

    public static boolean canDownloadClan(Context ctx, String clanId) {
        long time = clanDownloadedTime(ctx, clanId);
        boolean canUpdate = true;
        if (time != 0) {
            long now = Calendar.getInstance().getTimeInMillis();
            long dif = now - time;
            long days = (((dif / MILLIS) / SECONDSMINS) / SECONDSMINS) / HOURS;
            if (days < DAYS_BETWEEN_DOWNLOAD) {
                canUpdate = false;
            }
        }
        return canUpdate;
    }

    public static void clanDownloaded(Context ctx, String clanId) {
        Prefs prefs = new Prefs(ctx);
        prefs.setLong(PREF_CLAN_UPDATE + clanId, Calendar.getInstance().getTimeInMillis());
    }

    public static void removeClanDownloaded(Context ctx, String clanId) {
        Prefs prefs = new Prefs(ctx);
        prefs.remove(PREF_CLAN_UPDATE + clanId);
    }

    public static long clanDownloadedTime(Context ctx, String clanId) {
        Prefs prefs = new Prefs(ctx);
        return prefs.getLong(PREF_CLAN_UPDATE + clanId, 0);
    }

    public static void setTheme(FragmentActivity act) {
        boolean theme = isLightTheme(act);
        if (!theme) { //dark theme
            act.setTheme(R.style.Theme_Cp_Material);
        } else { // light theme
            act.setTheme(R.style.Theme_Cp_Material_Light);
        }
    }

    public static boolean isLightTheme(Context act) {
        Prefs prefs = new Prefs(act);
        boolean theme = prefs.getBoolean(SVault.PREF_THEME_SELECTOR, false);
        return theme;
    }

    public static void relaunchApplication(Activity act) {
        act.finish();
        CPManager.clear();
        ActivityFragment.lastSearchTime = null;
        DownloadedClanManager.init();
        CAApp.SELECTED_VEHICLE_ID = 0;
        CPSearchFragment.clear();
        EncyclopediaSearchFragment.clear();
        CPStorageManager.deleteTempClan(act);
        CompareManager.clear();
        CPStorageManager.deleteTempPlayer(act);
        Intent i = act.getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(act.getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        act.startActivity(i);
    }

    public static String getDefaultName(Context ctx) {
        Prefs prefs = new Prefs(ctx);
        Server saved = CAApp.getServerType(ctx);
        String name = prefs.getString(SVault.PREF_SELECTED_USER_NAME + saved.getServerName(), null);
        return name;
    }

    public static int getDefaultId(Context ctx) {
        Server saved = CAApp.getServerType(ctx);
        Prefs prefs = new Prefs(ctx);
        int id = prefs.getInt(SVault.PREF_SELECTED_USER_ID + saved.getServerName(), 0);
        return id;
    }

    public static void setDefaultPlayer(Context ctx, String name, int id) {
        Server saved = CAApp.getServerType(ctx);
        Prefs prefs = new Prefs(ctx);
        if (!TextUtils.isEmpty(name)) {
            prefs.setInt(SVault.PREF_SELECTED_USER_ID + saved.getServerName(), id);
            prefs.setString(SVault.PREF_SELECTED_USER_NAME + saved.getServerName(), name);
        } else {
            prefs.remove(SVault.PREF_SELECTED_USER_NAME + saved.getServerName());
            prefs.remove(SVault.PREF_SELECTED_USER_ID + saved.getServerName());
        }
    }

    public static InfoManager getInfoManager() {
        return infoManager;
    }

    public static String getServerLanguage(Context ctx) {
        Prefs pref = new Prefs(ctx);
        String language = pref.getString(SELECTED_SERVER_LANGUAGE, ctx.getString(R.string.base_server_language));
        Crashlytics.setString("ServerLanguage", language);
        return language;
    }

    public static void setServerLanguage(Context ctx, String language) {
        Prefs pref = new Prefs(ctx);
        pref.setString(SELECTED_SERVER_LANGUAGE, language);
        Crashlytics.setString("ServerLanguage", language);
    }

    public static String getAppLanguage(Context ctx) {
        Prefs pref = new Prefs(ctx);
        String language = pref.getString(APP_LANGUAGE, ctx.getString(R.string.app_langauge_base));
        Crashlytics.setString("AppLanguage", language);
        return language;
    }

    public static void setAppLanguage(Context ctx, String language) {
        Prefs pref = new Prefs(ctx);
        pref.setString(APP_LANGUAGE, language);
        Crashlytics.setString("AppLanguage", language);
    }
}
