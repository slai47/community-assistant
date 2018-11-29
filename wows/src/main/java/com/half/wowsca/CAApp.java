package com.half.wowsca;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import androidx.fragment.app.FragmentActivity;
import androidx.core.content.ContextCompat;

import com.half.wowsca.managers.CompareManager;
import com.half.wowsca.managers.InfoManager;
import com.half.wowsca.model.enums.Server;
import com.half.wowsca.model.enums.ShortcutRoutes;
import com.half.wowsca.ui.SettingActivity;
import com.utilities.logging.Dlog;
import com.utilities.preferences.Prefs;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by slai4 on 9/15/2015.
 */
public class CAApp extends Application {

    public static final String FORCE_UPDATE_DATA = "forceUpdateData5";
    public static final String APP_LANGUAGE = "appLanguage";
    public static boolean DEVELOPMENT_MODE = false;

    public static final String PREF_FILE = "caapp";

    public static final String WOWS_API_SITE_ADDRESS = "https://api.worldofwarships";

    public static final String SELECTED_SERVER = "selected_server";
    public static final String SELECTED_SERVER_LANGUAGE = "selected_server_language";
    public static final String SELECTED_ID = "selectedId";
    public static final String COLORBLIND_MODE = "colorblindMode";
    public static final String LAUNCH_COUNT = "launchCount";

    public static boolean HAS_SHOWN_FIRST_DIALOG = false;

    private static int lastShipPos;

    private static InfoManager infoManager;

    public static ShortcutRoutes ROUTING;

    @Override
    public void onCreate() {
        super.onCreate();
//        AuthInfo.delete(getApplicationContext());
//        InfoManager.purge(getApplicationContext());
        DEVELOPMENT_MODE = !BuildConfig.BUILD_TYPE.equals("release");
        Dlog.LOGGING_MODE = DEVELOPMENT_MODE;
        Prefs pref = new Prefs(getApplicationContext());
        int launchNumber = pref.getInt(LAUNCH_COUNT, 0);
        launchNumber++;
        pref.setInt(LAUNCH_COUNT, launchNumber);
        boolean refreshedData = pref.getBoolean(FORCE_UPDATE_DATA, false);
        if(!refreshedData){
            Dlog.d("CAApp", "refreshing data");
            InfoManager.purge(getApplicationContext());
            pref.setBoolean(FORCE_UPDATE_DATA, true);
        }

        // this is a temp fix to remove ko from the settings page. This isn't always supported across servers
        if(CAApp.getServerLanguage(getApplicationContext()).equals("ko")){
            CAApp.setServerLanguage(getApplicationContext(), getString(R.string.base_server_language));
        }
    }

    public static void relaunchApplication(Activity act){
        act.finish();
        CompareManager.clear();
        Intent i = act.getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(act.getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        act.startActivity(i);
    }

    public static void setTheme(FragmentActivity act) {
        String theme = getTheme(act);
        if (theme.equals("ocean")) { //dark theme
            act.setTheme(R.style.Theme_CA_Material_Ocean);
        }
//        else if(theme.equals("light")){ // light theme
//            act.setTheme(R.style.Theme_CA_Material_Light);
//        }
        else if(theme.equals("dark")){
            act.setTheme(R.style.Theme_CA_Material_Dark);
        }
    }

    public static boolean isOceanTheme(Context ctx){
        String theme = getTheme(ctx);
        return theme.equals("ocean");
    }

    public static boolean isDarkTheme(Context ctx){
        String theme = getTheme(ctx);
        return theme.equals("dark");
    }

    public static String getTheme(Context ctx){
        Prefs prefs = new Prefs(ctx);
        return prefs.getString(SettingActivity.THEME_CHOICE, "ocean");
    }

    public static boolean isNoArp(Context ctx){
        Prefs prefs = new Prefs(ctx);
        return prefs.getBoolean(SettingActivity.NO_ARP, false);
    }

    public static int getTextColor(Context ctx){
//        CAApp.isLightTheme(ctx) ? ContextCompat.getColor(ctx, R.color.material_text_primary_light) : for a different theme
        return ContextCompat.getColor(ctx, R.color.material_text_primary);
    }

    public static InfoManager getInfoManager() {
        if(infoManager == null){
            infoManager = new InfoManager();
        }
        return infoManager;
    }

    public static EventBus getEventBus() {
        return EventBus.getDefault();
    }

    public static Server getServerType(Context ctx) {
        Prefs pref = new Prefs(ctx);
        Server s = Server.valueOf(pref.getString(SELECTED_SERVER, Server.NA.toString()));
        return s;
    }

    public static void setServerType(Context ctx, Server s) {
        Prefs pref = new Prefs(ctx);
        pref.setString(SELECTED_SERVER, s.toString());
    }

    public static String getServerLanguage(Context ctx) {
        Prefs pref = new Prefs(ctx);
        String language = pref.getString(SELECTED_SERVER_LANGUAGE, ctx.getString(R.string.base_server_language));
        return language;
    }

    public static void setServerLanguage(Context ctx, String language) {
        Prefs pref = new Prefs(ctx);
        pref.setString(SELECTED_SERVER_LANGUAGE, language);
    }

    public static String getAppLanguage(Context ctx) {
        Prefs pref = new Prefs(ctx);
        String language = pref.getString(APP_LANGUAGE, ctx.getString(R.string.app_langauge_base));
        return language;
    }

    public static void setAppLanguage(Context ctx, String language) {
        Prefs pref = new Prefs(ctx);
        pref.setString(APP_LANGUAGE, language);
    }

    public static String getSelectedId(Context ctx) {
        Prefs pref = new Prefs(ctx);
        String selectedId = pref.getString(SELECTED_ID, null);
        return selectedId;
    }

    public static void setSelectedId(Context ctx, String id) {
        Prefs pref = new Prefs(ctx);
        if (id != null)
            pref.setString(SELECTED_ID, id);
        else
            pref.remove(SELECTED_ID);
    }

    public static boolean isColorblind(Context ctx) {
        Prefs pref = new Prefs(ctx);
        return pref.getBoolean(COLORBLIND_MODE, false);
    }

    public static void setColorblindMode(Context ctx, boolean mode) {
        Prefs pref = new Prefs(ctx);
        pref.setBoolean(COLORBLIND_MODE, mode);
    }

    /**
     *
     * @param pos sending 0 clears out the previous number
     */
    public static void setLastShipPos(int pos){
        lastShipPos = pos;
    }

    public static int getLastShipPos() {
        return lastShipPos;
    }
}
