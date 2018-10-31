package com.half.wowsca.managers;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.half.wowsca.CAApp;
import com.half.wowsca.model.Achievement;
import com.half.wowsca.model.Captain;
import com.half.wowsca.model.CaptainDetails;
import com.half.wowsca.model.Ship;
import com.half.wowsca.model.events.CaptainSavedEvent;
import com.half.wowsca.model.saveobjects.SavedAchievements;
import com.half.wowsca.model.saveobjects.SavedDetails;
import com.half.wowsca.model.saveobjects.SavedShips;
import com.utilities.logging.Dlog;
import com.utilities.preferences.Prefs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by slai4 on 9/15/2015.
 */
public class StorageManager {

    private static final String STATS_FOLDER = "wowscacaptains";

    public static int getStatsMax(Context ctx){
        Prefs prefs = new Prefs(ctx);
        return prefs.getInt("stats_max", 10);
    }

    public static void setStatsMax(Context ctx, int num){
        Prefs prefs = new Prefs(ctx);
        prefs.setInt("stats_max", num);
    }

    public static int getShipsStatsMax(Context ctx){
        Prefs prefs = new Prefs(ctx);
        return prefs.getInt("ships_stats_max", 5);
    }

    public static void setShipsStatsMax(Context ctx, int num){
        Prefs prefs = new Prefs(ctx);
        prefs.setInt("ships_stats_max", num);
    }

    public static void savePlayerStats(final Context ctx, final Captain p) {
        Runnable runnable = new Runnable() {
            public void run() {
                SavedDetails statsList = getPlayerStats(ctx, CaptainManager.getCapIdStr(p));
                boolean save = true;
                try {
                    if (!statsList.getDetails().isEmpty()) {
                        CaptainDetails stat1 = statsList.getDetails().get(0);
                        if (stat1 != null) {
                            Dlog.d("StorageManager", "stat= " + stat1.getBattles() + " p" + p.getDetails().getBattles());
                            if (stat1.getBattles() == p.getDetails().getBattles())
                                save = false;
                        }
                    }
                } catch (Exception e) {
                }
//                Dlog.wtf("Storage amange", "save = " + save + " list = " + statsList.getDetails().size());
                if (save) {
                    int stats_max = getStatsMax(ctx);
                    int ship_stat_max = getShipsStatsMax(ctx);

                    addStat(statsList, p.getDetails(), stats_max);

                    SavedAchievements achievements = getPlayerAchievements(ctx, CaptainManager.getCapIdStr(p));
                    addAchievements(achievements, p.getAchievements(), stats_max);

                    SavedShips ships = getPlayerShips(ctx, CaptainManager.getCapIdStr(p));
                    if(p.getShips() != null && ships != null) {
                        for (Ship s : p.getShips()) {
                            if (ships != null && ships.getSavedShips().get(s.getShipId()) != null) {
                                Ship last = ships.getSavedShips().get(s.getShipId()).get(0);
                                if (last.getBattles() != s.getBattles())
                                    addShipStat(ships, s, ship_stat_max);
                            } else {
                                List<Ship> ss = new ArrayList<>();
                                ss.add(s);
                                ships.getSavedShips().put(s.getShipId(), ss);
                            }
                        }
                    }

                    GsonBuilder builder = new GsonBuilder();
                    builder.serializeSpecialFloatingPointValues();
                    Gson gson = builder.create();

                    File dir = ctx.getDir(CaptainManager.DIRECTORY_NAME, Context.MODE_PRIVATE);
                    File statsDir = new File(dir, STATS_FOLDER);
                    File statsFile = new File(statsDir, "" + CaptainManager.getCapIdStr(p));
                    if (!statsFile.exists())
                        try {
                            statsFile.createNewFile();
                        } catch (IOException e) {
                        }
                    try {
                        FileOutputStream fos = new FileOutputStream(statsFile);
                        PrintWriter pw = new PrintWriter(fos);
                        pw.print(gson.toJson(statsList));
                        pw.flush();
                        fos.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    File achievementsFile = new File(statsDir, "a" + CaptainManager.getCapIdStr(p));
                    try {
                        FileOutputStream fos = new FileOutputStream(achievementsFile);
                        PrintWriter pw = new PrintWriter(fos);
                        pw.print(gson.toJson(achievements));
                        pw.flush();
                        fos.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if(p.getShips() != null) {
                        File shipsFile = new File(statsDir, "s" + CaptainManager.getCapIdStr(p));
                        try {
                            FileOutputStream fos = new FileOutputStream(shipsFile);
                            PrintWriter pw = new PrintWriter(fos);
                            pw.print(gson.toJson(ships));
                            pw.flush();
                            fos.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                CAApp.getEventBus().post(new CaptainSavedEvent());
            }
        };
        Thread t = new Thread(runnable);
        t.start();
    }

    /**
     * DO this in a thread. Might take a long time
     *
     * @param ctx
     * @param accountId
     * @return
     */
    public static SavedDetails getPlayerStats(Context ctx, String accountId) {
        SavedDetails savedDetails = new SavedDetails();
        File dir = ctx.getDir(CaptainManager.DIRECTORY_NAME, Context.MODE_PRIVATE);
        File statsDir = new File(dir, STATS_FOLDER);
        if (!statsDir.exists()) {
            statsDir.mkdir();
        }
        try {
            File tempStats = new File(statsDir, accountId);
            FileReader fr = new FileReader(tempStats);

            GsonBuilder builder = new GsonBuilder();
            builder.serializeSpecialFloatingPointValues();
            Gson gson = builder.create();
            savedDetails = gson.fromJson(fr, SavedDetails.class);
            fr.close();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
        return savedDetails;
    }

    private static void addStat(SavedDetails stats, CaptainDetails stat, int stats_max) {
        stats.getDetails().add(0, stat);
        if (stats != null)
            if (stats.getDetails() != null)
                if (stats.getDetails().size() > stats_max)
                    stats.getDetails().remove(stats.getDetails().size() - 1);
    }

    /**
     * DO this in a thread. Might take a long time
     *
     * @param ctx
     * @param accountId
     * @return
     */
    public static SavedAchievements getPlayerAchievements(Context ctx, String accountId) {
        SavedAchievements savedAchievements = new SavedAchievements();
        File dir = ctx.getDir(CaptainManager.DIRECTORY_NAME, Context.MODE_PRIVATE);
        File statsDir = new File(dir, STATS_FOLDER);
        if (!statsDir.exists()) {
            statsDir.mkdir();
        }
        try {
            File tempStats = new File(statsDir, "a" + accountId);
            FileReader fr = new FileReader(tempStats);

            GsonBuilder builder = new GsonBuilder();
            builder.serializeSpecialFloatingPointValues();
            Gson gson = builder.create();
            savedAchievements = gson.fromJson(fr, SavedAchievements.class);
            fr.close();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
        return savedAchievements;
    }

    private static void addAchievements(SavedAchievements stats, List<Achievement> stat, int stats_max) {
        if(stats.getSavedAchievements() == null)
            stats = new SavedAchievements();
        stats.getSavedAchievements().add(0, stat);
        if (stats != null)
            if (stats.getSavedAchievements() != null)
                if (stats.getSavedAchievements().size() > stats_max)
                    stats.getSavedAchievements().remove(stats.getSavedAchievements().size() - 1);

    }

    public static SavedShips getPlayerShips(Context ctx, String accountId) {
        SavedShips savedDetails = new SavedShips();
        File dir = ctx.getDir(CaptainManager.DIRECTORY_NAME, Context.MODE_PRIVATE);
        File statsDir = new File(dir, STATS_FOLDER);
        if (!statsDir.exists()) {
            statsDir.mkdir();
        }
        try {
            File tempStats = new File(statsDir, "s" + accountId);
            FileReader fr = new FileReader(tempStats);

            GsonBuilder builder = new GsonBuilder();
            builder.serializeSpecialFloatingPointValues();
            Gson gson = builder.create();
            savedDetails = gson.fromJson(fr, SavedShips.class);
            fr.close();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
        return savedDetails;
    }

    private static void addShipStat(SavedShips stats, Ship ship, int ship_stat_max) {
        stats.getSavedShips().get(ship.getShipId()).add(0, ship);
        if (stats.getSavedShips().get(ship.getShipId()).size() > ship_stat_max)
            stats.getSavedShips().get(ship.getShipId()).remove(stats.getSavedShips().get(ship.getShipId()).size() - 1);
    }


    public static boolean clearDownloadedPlayers(Context ctx) {
        File dir = ctx.getDir(CaptainManager.DIRECTORY_NAME, Context.MODE_PRIVATE);
        File statsDir = new File(dir, STATS_FOLDER);
        delete(statsDir);
        return statsDir.delete();
    }

    public static void delete(File file) {
        if (file.isDirectory()) {
            for (File f : file.listFiles())
                delete(f);
        }
        file.delete();
    }


}
