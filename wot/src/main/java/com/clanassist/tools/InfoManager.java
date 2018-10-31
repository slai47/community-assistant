package com.clanassist.tools;

import android.content.Context;
import android.util.SparseArray;

import com.clanassist.model.infoobj.Achievements;
import com.clanassist.model.infoobj.Tanks;
import com.clanassist.model.infoobj.VehicleWN8;
import com.clanassist.model.infoobj.WN8Data;
import com.cp.assist.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.utilities.logging.Dlog;
import com.utilities.preferences.Prefs;
import com.utilities.vaults.StringVault;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.StringTokenizer;

/**
 * Created by slai4 on 2/25/2016.
 */
public class InfoManager {

    public static final String INFO_DIR = "tank_info_dir";

    public static final String TANKS_INFO_FILE = "TANKS_INFO_FILE";
    public static final String ACHIEVEMENT_INFO_FILE = "ACHIEVEMENT_INFO_FILE";
    public static final String INFO_UPDATED_TIME = "info_updated_time";

    private Tanks tanks;

    private Achievements achievements;

    private WN8Data wn8Data;

    private SparseArray<ArrayList<VehicleWN8>> tierLists;

    public boolean isInfoThere(Context ctx) {
        boolean isInfoThere = false;
        File dir = ctx.getDir(INFO_DIR, Context.MODE_PRIVATE);
        File shipInfo = new File(dir, TANKS_INFO_FILE);
        File achievementInfo = new File(dir, ACHIEVEMENT_INFO_FILE);
        isInfoThere = shipInfo.exists() && achievementInfo.exists();
        if (timeToUpdate(ctx)) {
            isInfoThere = false;
        }
        return isInfoThere;
    }

    private boolean timeToUpdate(Context ctx) {
        Prefs pref = new Prefs(ctx);
        long time = pref.getLong(INFO_UPDATED_TIME, Calendar.getInstance().getTimeInMillis());
        boolean canUpdate = true;
        if (time != 0) {
            long now = Calendar.getInstance().getTimeInMillis();
            long dif = now - time;
            long days = (((dif / 1000) / 60) / 60) / 24;
            if (days < 3) {
                canUpdate = false;
            }
            Dlog.wtf("InfoManager", "canCLanUpdate = " + canUpdate + " days = " + days + " dif = " + dif);
        }
        return canUpdate;
    }

    public void updated(Context ctx) {
        Prefs pref = new Prefs(ctx);
        pref.setLong(INFO_UPDATED_TIME, Calendar.getInstance().getTimeInMillis());
    }

    public Tanks getTanks(Context ctx) {
        if (tanks == null || (tanks.getTanksList() != null && tanks.getTanksList().size() == 0)) {
            try {
                File dir = ctx.getDir(INFO_DIR, Context.MODE_PRIVATE);
                File tempStats = new File(dir, TANKS_INFO_FILE);
                FileReader fr = new FileReader(tempStats);
                GsonBuilder builder = new GsonBuilder();
                builder.serializeSpecialFloatingPointValues();
                Gson gson = builder.create();
                tanks = gson.fromJson(fr, Tanks.class);
                fr.close();
            } catch (FileNotFoundException e) {
            } catch (Exception e) {
            }
        }
        if (tanks == null) {
            tanks = new Tanks();
        }
        return tanks;
    }

    public void setTanks(Context ctx, Tanks tanks) {
        this.tanks = tanks;

        GsonBuilder builder = new GsonBuilder();
        builder.serializeSpecialFloatingPointValues();
        Gson gson = builder.create();

        File dir = ctx.getDir(INFO_DIR, Context.MODE_PRIVATE);
        File achievementsFile = new File(dir, TANKS_INFO_FILE);
        try {
            FileOutputStream fos = new FileOutputStream(achievementsFile);
            PrintWriter pw = new PrintWriter(fos);
            pw.print(gson.toJson(tanks));
            pw.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Achievements getAchievements(Context ctx) {
        if (achievements == null || (achievements.getAchievements() != null && achievements.getAchievements().size() == 0)) {
            try {
                File dir = ctx.getDir(INFO_DIR, Context.MODE_PRIVATE);
                File tempStats = new File(dir, ACHIEVEMENT_INFO_FILE);
                FileReader fr = new FileReader(tempStats);
                GsonBuilder builder = new GsonBuilder();
                builder.serializeSpecialFloatingPointValues();
                Gson gson = builder.create();
                achievements = gson.fromJson(fr, Achievements.class);
                fr.close();
            } catch (FileNotFoundException e) {
            } catch (Exception e) {
            }
        }
        if (achievements == null) {
            achievements = new Achievements();
        }
        return achievements;
    }

    public void setAchievements(Context ctx, Achievements achievements) {
        this.achievements = achievements;

        GsonBuilder builder = new GsonBuilder();
        builder.serializeSpecialFloatingPointValues();
        Gson gson = builder.create();

        File dir = ctx.getDir(INFO_DIR, Context.MODE_PRIVATE);
        File achievementsFile = new File(dir, ACHIEVEMENT_INFO_FILE);
        try {
            FileOutputStream fos = new FileOutputStream(achievementsFile);
            PrintWriter pw = new PrintWriter(fos);
            pw.print(gson.toJson(achievements));
            pw.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public WN8Data getWN8Data(Context ctx) {
        if (wn8Data == null || (wn8Data.getWN8s() != null && wn8Data.getWN8s().size() == 0)) {
            wn8Data = new WN8Data();
            BufferedReader br = null;
            String line = "";
            String cvsSplitBy = ",";
            StringTokenizer st = null;
            try {
                InputStream stream = ctx.getResources().openRawResource(R.raw.expected_tank_values_30);
                br = new BufferedReader(new InputStreamReader(stream, Charset.forName(StringVault.DEFAULT_ENCODING)));
                while ((line = br.readLine()) != null) {
                    st = new StringTokenizer(line, cvsSplitBy);
                    VehicleWN8 obj = new VehicleWN8();
                    obj.setId(Integer.parseInt(st.nextToken()));
                    obj.setFrag(Double.parseDouble(st.nextToken()));
                    obj.setDmg(Double.parseDouble(st.nextToken()));
                    obj.setSpot(Double.parseDouble(st.nextToken()));
                    obj.setDef(Double.parseDouble(st.nextToken()));
                    obj.setWin(Double.parseDouble(st.nextToken()));
                    wn8Data.addWN8(obj);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (br != null)
                    try {
                        br.close();
                    } catch (IOException e) {
                    }
            }
        }
        if (wn8Data == null) {
            wn8Data = new WN8Data();
        }
        return wn8Data;
    }

    public void purge(Context ctx) {
        File dir = ctx.getDir(INFO_DIR, Context.MODE_PRIVATE);
        File shipInfo = new File(dir, TANKS_INFO_FILE);
        File achievementInfo = new File(dir, ACHIEVEMENT_INFO_FILE);
        shipInfo.delete();
        achievementInfo.delete();
    }

}