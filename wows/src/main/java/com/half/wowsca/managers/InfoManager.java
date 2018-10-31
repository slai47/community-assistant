package com.half.wowsca.managers;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.half.wowsca.model.encyclopedia.holders.AchievementsHolder;
import com.half.wowsca.model.encyclopedia.holders.ShipsHolder;
import com.half.wowsca.model.encyclopedia.holders.UpgradeHolder;
import com.half.wowsca.model.encyclopedia.holders.ExteriorHolder;
import com.half.wowsca.model.encyclopedia.holders.WarshipsStats;
import com.half.wowsca.model.encyclopedia.holders.CaptainSkillHolder;
import com.utilities.logging.Dlog;
import com.utilities.preferences.Prefs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Calendar;

/**
 * Created by slai4 on 9/23/2015.
 */
public class InfoManager {

    public static final String SHIP_INFO_FILE = "shipInfoFile";
    public static final String ACHIEVEMENT_INFO_FILE = "achievementInfoFile";
    public static final String WARSHIP_STATS_INFO_FILE = "warshipStatsInfoFile";
    public static final String EQUIPMENT_INFO_FILE = "equipmentInfoFile";
    public static final String INFO_UPDATED_TIME = "info_updated_time";
    private static final int DAYS_BETWEEN_DOWNLOAD = 5;
    public static final String EXTERIOR_ITEMS_FILE = "exterior_items";
    public static final String CAPTAIN_SKILLS_FILE = "captain_skills_file";

    private ShipsHolder shipInfo;

    private UpgradeHolder upgrades;

    private AchievementsHolder achievementInfo;

    private WarshipsStats warshipsStats;

    private ExteriorHolder exteriorItems;

    private CaptainSkillHolder captainSkills;

    public boolean isInfoThere(Context ctx) {
        boolean isInfoThere = false;
        File dir = ctx.getDir(CaptainManager.DIRECTORY_NAME, Context.MODE_PRIVATE);
        File shipInfo = new File(dir, SHIP_INFO_FILE);
        File achievementInfo = new File(dir, ACHIEVEMENT_INFO_FILE);
        File warshipsInfo = new File(dir, WARSHIP_STATS_INFO_FILE);
        File equipmentInfo = new File(dir, EQUIPMENT_INFO_FILE);
        isInfoThere = shipInfo.exists() && achievementInfo.exists()
                && warshipsInfo.exists() && equipmentInfo.exists()
        ;
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
            if (days < DAYS_BETWEEN_DOWNLOAD) {
                canUpdate = false;
            }
            Dlog.wtf("InfoManager", "canUpdate = " + canUpdate + " days = " + days + " dif = " + dif);
        }
        return canUpdate;

    }

    /**
     * Load on backthread. Grabs all saved data and starts up info manager
     *
     * @param ctx
     */
    public void load(Context ctx){
        getAchievements(ctx);
        getShipInfo(ctx);
        getShipStats(ctx);
        getUpgrades(ctx);
        getCaptainSkills(ctx);
        getExteriorItems(ctx);
    }

    public void updated(Context ctx) {
        Prefs pref = new Prefs(ctx);
        pref.setLong(INFO_UPDATED_TIME, Calendar.getInstance().getTimeInMillis());
    }

    public ShipsHolder getShipInfo(Context ctx) {
        if (shipInfo == null || (shipInfo.getItems() != null && shipInfo.getItems().size() == 0)) {
            try {
                File dir = ctx.getDir(CaptainManager.DIRECTORY_NAME, Context.MODE_PRIVATE);
                File tempStats = new File(dir, SHIP_INFO_FILE);
                FileReader fr = new FileReader(tempStats);
                GsonBuilder builder = new GsonBuilder();
                builder.serializeSpecialFloatingPointValues();
                Gson gson = builder.create();
                shipInfo = gson.fromJson(fr, ShipsHolder.class);
                fr.close();
            } catch (FileNotFoundException e) {
            } catch (Exception e) {
            }
        }
        if (shipInfo == null) {
            shipInfo = new ShipsHolder();
        }
        return shipInfo;
    }

    public void setShipInfo(Context ctx, ShipsHolder shipInfo) {
        this.shipInfo = shipInfo;

        GsonBuilder builder = new GsonBuilder();
        builder.serializeSpecialFloatingPointValues();
        Gson gson = builder.create();

        File dir = ctx.getDir(CaptainManager.DIRECTORY_NAME, Context.MODE_PRIVATE);
        File achievementsFile = new File(dir, SHIP_INFO_FILE);
        try {
            FileOutputStream fos = new FileOutputStream(achievementsFile);
            PrintWriter pw = new PrintWriter(fos);
            pw.print(gson.toJson(shipInfo));
            pw.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public AchievementsHolder getAchievements(Context ctx) {
        if (achievementInfo == null || (achievementInfo.getItems() != null && achievementInfo.getItems().size() == 0)) {
            try {
                File dir = ctx.getDir(CaptainManager.DIRECTORY_NAME, Context.MODE_PRIVATE);
                File tempStats = new File(dir, ACHIEVEMENT_INFO_FILE);
                FileReader fr = new FileReader(tempStats);
                GsonBuilder builder = new GsonBuilder();
                builder.serializeSpecialFloatingPointValues();
                Gson gson = builder.create();
                achievementInfo = gson.fromJson(fr, AchievementsHolder.class);
                fr.close();
            } catch (FileNotFoundException e) {
            } catch (Exception e) {
            }
        }
        if (achievementInfo == null) {
            achievementInfo = new AchievementsHolder();
        }
        return achievementInfo;
    }

    public void setAchievements(Context ctx, AchievementsHolder achievementInfo) {
        this.achievementInfo = achievementInfo;
        GsonBuilder builder = new GsonBuilder();
        builder.serializeSpecialFloatingPointValues();
        Gson gson = builder.create();

        File dir = ctx.getDir(CaptainManager.DIRECTORY_NAME, Context.MODE_PRIVATE);
        File achievementsFile = new File(dir, ACHIEVEMENT_INFO_FILE);
        try {
            FileOutputStream fos = new FileOutputStream(achievementsFile);
            PrintWriter pw = new PrintWriter(fos);
            pw.print(gson.toJson(achievementInfo));
            pw.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public WarshipsStats getShipStats(Context ctx) {
        if (warshipsStats == null || (warshipsStats.getSHIP_STATS() != null && warshipsStats.getSHIP_STATS().size() == 0)) {
            try {
                File dir = ctx.getDir(CaptainManager.DIRECTORY_NAME, Context.MODE_PRIVATE);
                File tempStats = new File(dir, WARSHIP_STATS_INFO_FILE);
                FileReader fr = new FileReader(tempStats);
                GsonBuilder builder = new GsonBuilder();
                builder.serializeSpecialFloatingPointValues();
                Gson gson = builder.create();
                warshipsStats = gson.fromJson(fr, WarshipsStats.class);
                fr.close();
            } catch (FileNotFoundException e) {
            } catch (Exception e) {
            }
        }
        if (warshipsStats == null)
            warshipsStats = new WarshipsStats();
        return warshipsStats;
    }

    public void setWarshipsStats(Context ctx, WarshipsStats warshipsStats) {
        this.warshipsStats = warshipsStats;
        GsonBuilder builder = new GsonBuilder();
        builder.serializeSpecialFloatingPointValues();
        Gson gson = builder.create();

        File dir = ctx.getDir(CaptainManager.DIRECTORY_NAME, Context.MODE_PRIVATE);
        File achievementsFile = new File(dir, WARSHIP_STATS_INFO_FILE);
        try {
            FileOutputStream fos = new FileOutputStream(achievementsFile);
            PrintWriter pw = new PrintWriter(fos);
            pw.print(gson.toJson(warshipsStats));
            pw.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public UpgradeHolder getUpgrades(Context ctx) {
        if (upgrades == null || (upgrades.getItems() != null && upgrades.getItems().size() == 0)) {
            try {
                File dir = ctx.getDir(CaptainManager.DIRECTORY_NAME, Context.MODE_PRIVATE);
                File tempStats = new File(dir, EQUIPMENT_INFO_FILE);
                FileReader fr = new FileReader(tempStats);
                GsonBuilder builder = new GsonBuilder();
                builder.serializeSpecialFloatingPointValues();
                Gson gson = builder.create();
                upgrades = gson.fromJson(fr, UpgradeHolder.class);
                fr.close();
            } catch (FileNotFoundException e) {
            } catch (Exception e) {
            }
        }
        if (upgrades == null) {
            upgrades = new UpgradeHolder();
        }
        return upgrades;
    }

    public void setUpgrades(Context ctx, UpgradeHolder equipment) {
        this.upgrades = equipment;

        GsonBuilder builder = new GsonBuilder();
        builder.serializeSpecialFloatingPointValues();
        Gson gson = builder.create();

        File dir = ctx.getDir(CaptainManager.DIRECTORY_NAME, Context.MODE_PRIVATE);
        File achievementsFile = new File(dir, EQUIPMENT_INFO_FILE);
        try {
            FileOutputStream fos = new FileOutputStream(achievementsFile);
            PrintWriter pw = new PrintWriter(fos);
            pw.print(gson.toJson(equipment));
            pw.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ExteriorHolder getExteriorItems(Context ctx){
        if(exteriorItems == null || (exteriorItems.getItems() != null && exteriorItems.getItems().size() == 0)){
            try {
                File dir = ctx.getDir(CaptainManager.DIRECTORY_NAME, Context.MODE_PRIVATE);
                File tempStats = new File(dir, EXTERIOR_ITEMS_FILE);
                FileReader fr = new FileReader(tempStats);
                GsonBuilder builder = new GsonBuilder();
                builder.serializeSpecialFloatingPointValues();
                Gson gson = builder.create();
                exteriorItems = gson.fromJson(fr, ExteriorHolder.class);
                fr.close();
            } catch (FileNotFoundException e) {
            } catch (Exception e) {
            }
        }
        if(exteriorItems == null){
            exteriorItems = new ExteriorHolder();
        }
        return exteriorItems;
    }

    public void setExteriorItems(Context ctx, ExteriorHolder exteriorHolder) {
        this.exteriorItems = exteriorHolder;

        GsonBuilder builder = new GsonBuilder();
        builder.serializeSpecialFloatingPointValues();
        Gson gson = builder.create();

        File dir = ctx.getDir(CaptainManager.DIRECTORY_NAME, Context.MODE_PRIVATE);
        File achievementsFile = new File(dir, EXTERIOR_ITEMS_FILE);
        try {
            FileOutputStream fos = new FileOutputStream(achievementsFile);
            PrintWriter pw = new PrintWriter(fos);
            pw.print(gson.toJson(exteriorHolder));
            pw.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CaptainSkillHolder getCaptainSkills(Context ctx){
        if(captainSkills == null || (captainSkills.getItems() != null && captainSkills.getItems().size() == 0)){
            try {
                File dir = ctx.getDir(CaptainManager.DIRECTORY_NAME, Context.MODE_PRIVATE);
                File tempStats = new File(dir, CAPTAIN_SKILLS_FILE);
                FileReader fr = new FileReader(tempStats);
                GsonBuilder builder = new GsonBuilder();
                builder.serializeSpecialFloatingPointValues();
                Gson gson = builder.create();
                captainSkills = gson.fromJson(fr, CaptainSkillHolder.class);
                fr.close();
            } catch (FileNotFoundException e) {
            } catch (Exception e) {
            }
        }
        if(captainSkills == null){
            captainSkills = new CaptainSkillHolder();
        }
        return captainSkills;
    }

    public void setCaptainSkills(Context ctx, CaptainSkillHolder captainSkillHolder) {
        this.captainSkills = captainSkillHolder;

        GsonBuilder builder = new GsonBuilder();
        builder.serializeSpecialFloatingPointValues();
        Gson gson = builder.create();

        File dir = ctx.getDir(CaptainManager.DIRECTORY_NAME, Context.MODE_PRIVATE);
        File achievementsFile = new File(dir, CAPTAIN_SKILLS_FILE);
        try {
            FileOutputStream fos = new FileOutputStream(achievementsFile);
            PrintWriter pw = new PrintWriter(fos);
            pw.print(gson.toJson(captainSkillHolder));
            pw.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void purge(Context ctx) {
        File dir = ctx.getDir(CaptainManager.DIRECTORY_NAME, Context.MODE_PRIVATE);
        File shipInfo = new File(dir, SHIP_INFO_FILE);
        File achievementInfo = new File(dir, ACHIEVEMENT_INFO_FILE);
        File warshipStatsFile = new File(dir, WARSHIP_STATS_INFO_FILE);
        File upgradesFile = new File(dir, EQUIPMENT_INFO_FILE);
        File exteriorFile = new File(dir, EXTERIOR_ITEMS_FILE);
        File skillsFile = new File(dir, CAPTAIN_SKILLS_FILE);
        shipInfo.delete();
        achievementInfo.delete();
        warshipStatsFile.delete();
        upgradesFile.delete();
        exteriorFile.delete();
        skillsFile.delete();
    }
}
