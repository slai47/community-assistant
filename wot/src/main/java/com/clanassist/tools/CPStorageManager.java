package com.clanassist.tools;

import android.content.Context;
import android.os.Environment;
import android.util.SparseArray;

import com.clanassist.CAApp;
import com.clanassist.model.clan.Clan;
import com.clanassist.model.enums.Server;
import com.clanassist.model.events.details.PlayerDifferenceSavedEvent;
import com.clanassist.model.events.details.PlayerFutureStatsSavedEvent;
import com.clanassist.model.player.Player;
import com.clanassist.model.player.PlayerVehicleInfo;
import com.clanassist.model.player.WN8StatsInfo;
import com.clanassist.model.player.storage.PlayerFutureStats;
import com.clanassist.model.player.storage.PlayerSavedStats;
import com.clanassist.model.player.storage.SavedStatsObj;
import com.clanassist.model.player.storage.VehicleStatsObj;
import com.clanassist.model.search.results.ClanPlayerWN8sTaskResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.utilities.Utils;
import com.utilities.logging.Dlog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;

/**
 * Created by Harrison on 7/14/2014.
 */
public class CPStorageManager {

    private static final String TEMP_STORAGE_FILE = "temp_clan";
    private static final String TEMP_STORAGE_FILE_PLAYER = "temp_player";
    private static final String SAVED_PLAYER_FILE_PREFIX = "pl_";
    private static final String SAVED_PLAYER_VEHICLE_FILE_PREFIX = "pl_v_";
    private static final String SAVED_PLAYER_FUTURE_FILE_PREFIX = "pl_f_";
    public static final String PREV_SAVED_CLAN_STATS_DIR = "clan_stats";
    private static final String SAVED_CLAN_STATS_DIR = "clan_stats_1";
    public static final int STATS_MAX = 40;
    public static final int VEHICLE_STATS_MAX = 5;

    public static void savePlayerStats(final Context ctx, final Player p) {
        Runnable runnable = new Runnable() {
            public void run() {
                SavedStatsObj statsList = getPlayerStats(ctx, p.getId());
                boolean save = true;
                try {
                    if (!statsList.getStats().isEmpty()) {
                        PlayerSavedStats stat1 = statsList.getStats().get(0);
                        if (stat1 != null) {
                            if (stat1.getStats().getBattles() == p.getOverallStats().getBattles())
                                save = false;
                        }
                    }
                } catch (Exception e) {
                }
                Dlog.d("Storage amange", "save = " + save);
                if (save) {
                    SparseArray<Integer> vehicleWn8s = new SparseArray<Integer>();
                    if (p.getPlayerVehicleInfoList() != null) {
                        for (PlayerVehicleInfo info : p.getPlayerVehicleInfoList()) {
                            vehicleWn8s.put(info.getTankId(), (int) info.getWN8());
                        }
                    }
                    PlayerSavedStats stats = new PlayerSavedStats(p.getOverallStats(), p.getClanStats(), p.getStrongholdStats(), p.getWN8(), p.getClanWN8(), p.getStrongholdWN8(), p.getTreesCut());
                    VehicleStatsObj vehicleStatsObj = getPlayerVehicleStats(ctx, p.getId());

                    addStat(statsList, stats);
                    addVStat(vehicleStatsObj, vehicleWn8s);

                    GsonBuilder builder = new GsonBuilder();
                    builder.serializeSpecialFloatingPointValues();
                    Gson gson = builder.create();

                    File dir = ctx.getDir(CPManager.DIRECTORY_NAME, Context.MODE_PRIVATE);
                    File statsDir = new File(dir, CPManager.PLAYER_STATS_FOLDER);
                    File statsFile = new File(statsDir, SAVED_PLAYER_FILE_PREFIX + p.getId());
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

                    File vehicleStatsFile = new File(statsDir, SAVED_PLAYER_VEHICLE_FILE_PREFIX + p.getId());
                    if (!vehicleStatsFile.exists())
                        try {
                            vehicleStatsFile.createNewFile();
                        } catch (IOException e) {
                        }
                    try {
                        FileOutputStream fos = new FileOutputStream(vehicleStatsFile);
                        PrintWriter pw = new PrintWriter(fos);
                        pw.print(gson.toJson(vehicleStatsFile));
                        pw.flush();
                        fos.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                CAApp.getEventBus().post(new PlayerDifferenceSavedEvent());
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
    public static SavedStatsObj getPlayerStats(Context ctx, int accountId) {
        SavedStatsObj overallStats = new SavedStatsObj();
        File dir = ctx.getDir(CPManager.DIRECTORY_NAME, Context.MODE_PRIVATE);
        File statsDir = new File(dir, CPManager.PLAYER_STATS_FOLDER);
        if (!statsDir.exists()) {
            statsDir.mkdir();
        }
        try {
            File tempStats = new File(statsDir, SAVED_PLAYER_FILE_PREFIX + accountId);
            FileReader fr = new FileReader(tempStats);
            overallStats = new Gson().fromJson(fr, SavedStatsObj.class);
            fr.close();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
        return overallStats;
    }

    private static void addStat(SavedStatsObj stats, PlayerSavedStats stat) {
        if(stats == null)
            stats = new SavedStatsObj();
        stats.getStats().add(0, stat);
        if(stats.getStats() != null)
            if (stats.getStats().size() > STATS_MAX)
                stats.getStats().remove(stats.getStats().size() - 1);

    }

    /**
     * DO this in a thread. Might take a long time
     *
     * @param ctx
     * @param accountId
     * @return
     */
    public static VehicleStatsObj getPlayerVehicleStats(Context ctx, int accountId) {
        VehicleStatsObj vehicleStatsObj = new VehicleStatsObj();
        File dir = ctx.getDir(CPManager.DIRECTORY_NAME, Context.MODE_PRIVATE);
        File statsDir = new File(dir, CPManager.PLAYER_STATS_FOLDER);
        if (!statsDir.exists()) {
            statsDir.mkdir();
        }
        try {
            File tempStats = new File(statsDir, SAVED_PLAYER_VEHICLE_FILE_PREFIX + accountId);
            FileReader fr = new FileReader(tempStats);
            vehicleStatsObj = new Gson().fromJson(fr, VehicleStatsObj.class);
            fr.close();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
        return vehicleStatsObj;
    }

    private static void addVStat(VehicleStatsObj stats, SparseArray<Integer> stat) {
        if(stats == null){
            stats = new VehicleStatsObj();
        }
        stats.getVehicleStats().add(0, stat);
        if (stats.getVehicleStats().size() > VEHICLE_STATS_MAX) {
            stats.getVehicleStats().remove(stats.getVehicleStats().size() - 1);
        }
    }

    public static void savePlayerFutureStats(final Context ctx, final int id, final WN8StatsInfo stats) {
        Runnable runnable = new Runnable() {
            public void run() {

                PlayerFutureStats futureStats = getPlayerFutureStats(ctx, id);
                addFStat(futureStats, stats);

                GsonBuilder builder = new GsonBuilder();
                builder.serializeSpecialFloatingPointValues();
                Gson gson = builder.create();

                File dir = ctx.getDir(CPManager.DIRECTORY_NAME, Context.MODE_PRIVATE);
                File statsDir = new File(dir, CPManager.PLAYER_STATS_FOLDER);
                File statsFile = new File(statsDir, SAVED_PLAYER_FUTURE_FILE_PREFIX + id);
                if (!statsFile.exists())
                    try {
                        statsFile.createNewFile();
                    } catch (IOException e) {
                    }
                try {
                    FileOutputStream fos = new FileOutputStream(statsFile);
                    PrintWriter pw = new PrintWriter(fos);
                    pw.print(gson.toJson(futureStats));
                    pw.flush();
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                CAApp.getEventBus().post(new PlayerFutureStatsSavedEvent());
            }
        };
        Thread t = new Thread(runnable);
        t.start();
    }

    public static PlayerFutureStats getPlayerFutureStats(Context ctx, int accountId) {
        PlayerFutureStats overallStats = new PlayerFutureStats();
        File dir = ctx.getDir(CPManager.DIRECTORY_NAME, Context.MODE_PRIVATE);
        File statsDir = new File(dir, CPManager.PLAYER_STATS_FOLDER);
        if (!statsDir.exists()) {
            statsDir.mkdir();
        }
        try {
            File tempStats = new File(statsDir, SAVED_PLAYER_FUTURE_FILE_PREFIX + accountId);
            FileReader fr = new FileReader(tempStats);
            overallStats = new Gson().fromJson(fr, PlayerFutureStats.class);
            fr.close();
        } catch (FileNotFoundException e) {
        } catch (Exception e) {
        }
        return overallStats;
    }

    private static void addFStat(PlayerFutureStats stats, WN8StatsInfo stat) {
        if(stats.getStatsInfos().size() > 1) {
            WN8StatsInfo past = stats.getStatsInfos().get(0);
            if((past.getPastTwoMonths() != stat.getPastTwoMonths() && stat.getPastTwoMonths() != 0)
                    || (past.getPastMonth() != stat.getPastMonth() && stat.getPastMonth() != 0)
                    || (past.getPastWeek() != stat.getPastWeek() && stat.getPastWeek() != 0)
                    || (past.getPastDay() != stat.getPastDay() && stat.getPastDay() != 0)
                    )
            {
                Dlog.d("CPStorage", "Saving WN8 Differnece " + (past.getPastTwoMonths() != stat.getPastTwoMonths() && stat.getPastTwoMonths() != 0) + " "
                        + (past.getPastMonth() != stat.getPastMonth() && stat.getPastMonth() != 0) + " "
                        + (past.getPastWeek() != stat.getPastWeek() && stat.getPastWeek() != 0) + " "
                        + (past.getPastDay() != stat.getPastDay() && stat.getPastDay() != 0));
                stats.getStatsInfos().add(0, stat);
            }
        } else {
            Dlog.d("CPStorage", "Adding WN8 Differnece");
            stats.getStatsInfos().add(0, stat);
        }
        if (stats.getStatsInfos().size() > STATS_MAX) {
            stats.getStatsInfos().remove(stats.getStatsInfos().size() - 1);
        }
    }


    public static Clan getTempStoredClan(Context ctx) {
        Clan c = null;
        try {
            File dir = ctx.getDir(CPManager.DIRECTORY_NAME, Context.MODE_PRIVATE);
            File tempClan = new File(dir, TEMP_STORAGE_FILE);
            FileReader fr = new FileReader(tempClan);
            c = new Gson().fromJson(fr, Clan.class);
            fr.close();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
        return c;
    }

    public static void deleteTempClan(Context ctx) {
        try {
            File dir = ctx.getDir(CPManager.DIRECTORY_NAME, Context.MODE_PRIVATE);
            File tempClan = new File(dir, TEMP_STORAGE_FILE);
            if (tempClan.exists())
                tempClan.delete();
        } catch (Exception e) {
        }
    }


    public static void saveTempStoredClan(Context ctx, Clan clan) {
        File dir = ctx.getDir(CPManager.DIRECTORY_NAME, Context.MODE_PRIVATE);
        File clansFile = new File(dir, TEMP_STORAGE_FILE);
        if (!clansFile.exists())
            try {
                clansFile.createNewFile();
            } catch (IOException e) {
            }
        try {
            FileOutputStream fos = new FileOutputStream(clansFile);
            PrintWriter pw = new PrintWriter(fos);
            pw.print(new Gson().toJson(clan));
            pw.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Player getTempStoredPlayer(Context ctx) {
        Player p = null;
        try {
            File dir = ctx.getDir(CPManager.DIRECTORY_NAME, Context.MODE_PRIVATE);
            File tempClan = new File(dir, TEMP_STORAGE_FILE_PLAYER);
            FileReader fr = new FileReader(tempClan);
            GsonBuilder builder = new GsonBuilder();
            builder.serializeSpecialFloatingPointValues();
            Gson gson = builder.create();
            p = gson.fromJson(fr, Player.class);
            fr.close();
        } catch (FileNotFoundException e) {
        } catch (Exception e) {
        }
        return p;
    }

    public static void saveTempStoredPlayer(Context ctx, Player player) {
        File dir = ctx.getDir(CPManager.DIRECTORY_NAME, Context.MODE_PRIVATE);
        File clansFile = new File(dir, TEMP_STORAGE_FILE_PLAYER);
        if (!clansFile.exists())
            try {
                clansFile.createNewFile();
            } catch (IOException e) {
            }
        try {
            FileOutputStream fos = new FileOutputStream(clansFile);
            PrintWriter pw = new PrintWriter(fos);
            GsonBuilder builder = new GsonBuilder();
            builder.serializeSpecialFloatingPointValues();
            Gson gson = builder.create();
            pw.print(gson.toJson(player));
            pw.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteTempPlayer(Context ctx) {
        try {
            File dir = ctx.getDir(CPManager.DIRECTORY_NAME, Context.MODE_PRIVATE);
            File tempClan = new File(dir, TEMP_STORAGE_FILE_PLAYER);
            if (tempClan.exists())
                tempClan.delete();
        } catch (Exception e) {
        }
    }

    public static ClanPlayerWN8sTaskResult getClanTaskResult(Context ctx, int clanId) {
        Dlog.d("getClanTaskResult", "grabbing clan " + clanId);
        File dir = ctx.getDir(CPManager.DIRECTORY_NAME, Context.MODE_PRIVATE);
        File clanStatsDir = new File(dir, SAVED_CLAN_STATS_DIR);
        Server server = CAApp.getServerType(ctx);
        File serverDir = new File(clanStatsDir, server.getServerName());
        File clanDir = new File(serverDir, "" + clanId);
        ClanPlayerWN8sTaskResult result = null;
        if (!clanStatsDir.exists()) {
            clanStatsDir.mkdir();
            serverDir.mkdir();
        } else if (!clanDir.exists()) {
            clanDir.mkdir();
        } else {
            String[] files = clanDir.list();
            if (files.length > 0) {
                String name = files[files.length - 1];
                for (File f : clanDir.listFiles()) {
                    Dlog.d("CPStorageClanTask", "file = " + f.getAbsolutePath());
                }
                File file = new File(clanDir, name);
                Dlog.d("getClanTaskResult", "file size = " + file.length() + " file = " + file.getAbsolutePath());
                try {
                    FileReader fr = new FileReader(file);
                    GsonBuilder builder = new GsonBuilder();
                    builder.serializeSpecialFloatingPointValues();
                    result = builder.create().fromJson(fr, ClanPlayerWN8sTaskResult.class);
                    fr.close();
                } catch (FileNotFoundException e) {
                } catch (IOException e) {
                }
            }
        }
        return result;
    }

    public static File saveClanTaskResult(Context ctx, int clanId, ClanPlayerWN8sTaskResult result) {
        File dir = ctx.getDir(CPManager.DIRECTORY_NAME, Context.MODE_PRIVATE);
        File clanStatsDir = new File(dir, SAVED_CLAN_STATS_DIR);
        Server server = CAApp.getServerType(ctx);
        File serverDir = new File(clanStatsDir, server.getServerName());
        File clanDir = new File(serverDir, "" + clanId);
        clanStatsDir.mkdir();
        serverDir.mkdir();
        clanDir.mkdir();
        result.setSavedMillis(Calendar.getInstance().getTimeInMillis());
        File file = new File(clanDir, Calendar.getInstance().getTimeInMillis() + ".txt");
        try {
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(fos);
            GsonBuilder builder = new GsonBuilder();
            builder.serializeSpecialFloatingPointValues();
            pw.print(builder.create().toJson(result));
            pw.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            file = null;
        }
//        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/wot-assistant/";
//        if (Utils.canUseExternalStorage()) {
//            try {
//                File edir = new File(path);
//                edir.mkdirs();
//                File eclanDir = new File(edir, clanId + "");
//                eclanDir.mkdir();
//                File efile = new File(eclanDir, Calendar.getInstance().getTimeInMillis() + ".txt");
//                try {
//                    efile.createNewFile();
//                    FileOutputStream fos = new FileOutputStream(efile);
//                    PrintWriter pw = new PrintWriter(fos);
//                    GsonBuilder builder = new GsonBuilder();
//                    builder.serializeSpecialFloatingPointValues();
//                    pw.print(builder.create().toJson(result));
//                    pw.flush();
//                    fos.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                Dlog.d("CPStorageManager", "storage = " + Utils.canUseExternalStorage() + " path = " + path + " efile = " + efile.exists() + " epath = " + efile.getAbsolutePath());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
        DownloadedClanManager.putResult(clanId + "", result);
        return file;
    }

    public static boolean hasClanTaskResult(Context ctx, int clanId) {
        File dir = ctx.getDir(CPManager.DIRECTORY_NAME, Context.MODE_PRIVATE);
        File clanStatsDir = new File(dir, SAVED_CLAN_STATS_DIR);
        Server server = CAApp.getServerType(ctx);
        File serverDir = new File(clanStatsDir, server.getServerName());
        File clanDir = new File(serverDir, "" + clanId);
        File[] list = clanDir.listFiles();
        return list != null && list.length > 0;
    }

    public static boolean clearDownloadedClansResult(Context ctx) {
        File dir = ctx.getDir(CPManager.DIRECTORY_NAME, Context.MODE_PRIVATE);
        File clanStatsDir = new File(dir, SAVED_CLAN_STATS_DIR);
        File prevClanStatsDir = new File(dir, PREV_SAVED_CLAN_STATS_DIR);

        if(prevClanStatsDir.exists()) {
            delete(prevClanStatsDir);
        }

        DownloadedClanManager.clearResults();
        delete(clanStatsDir);
        return clanStatsDir.delete();
    }

    public static boolean clearClan(Context ctx, int clanId) {
        File dir = ctx.getDir(CPManager.DIRECTORY_NAME, Context.MODE_PRIVATE);
        File clanStatsDir = new File(dir, SAVED_CLAN_STATS_DIR);
        Server server = CAApp.getServerType(ctx);
        File serverDir = new File(clanStatsDir, server.getServerName());
        File clanDir = new File(serverDir, "" + clanId);

        HitManager.removeClanProfileHit(clanId);

        DownloadedClanManager.removeResult(clanId + "");
        delete(clanDir);
        return clanStatsDir.delete();
    }

    public static boolean clearDownloadedPlayers(Context ctx) {
        File dir = ctx.getDir(CPManager.DIRECTORY_NAME, Context.MODE_PRIVATE);
        File statsDir = new File(dir, CPManager.PLAYER_STATS_FOLDER);
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
