package com.clanassist.tools;

import android.content.Context;

import com.clanassist.CAApp;
import com.clanassist.model.clan.Clan;
import com.clanassist.model.enums.Server;
import com.clanassist.model.player.Player;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Convernince methods to grab and retrieve objects from the saved files.
 * <p/>
 * Created by Obsidian47 on 3/2/14.
 */
public class CPManager {

    public static final String DIRECTORY_NAME = "cpassist";
    public static final String PLAYER_STATS_FOLDER = "playerstats";
    private static final String CLAN_SAVED_FILE_NAME = "lists_of_clans";
    private static final String PLAYER_SAVED_FILE_NAME = "list_of_players";

    public static Map<Integer, Clan> LIST_OF_CLANS;
    public static Map<Integer, Player> LIST_OF_PLAYERS;

    public static List<Clan> ADAPTER_CLANS_LIST;

    public static Map<Integer, Clan> getSavedClans(Context ctx) {
        Server server = CAApp.getServerType(ctx);
        Map<Integer, Clan> clans = new HashMap<Integer, Clan>();
        if (LIST_OF_CLANS == null) {
            try {
                Type typeOfHashMap = new TypeToken<Map<Integer, Clan>>() {
                }.getType();
                File dir = ctx.getDir(DIRECTORY_NAME, Context.MODE_PRIVATE);
                String serverSuffix = "";
                if (server != Server.US) {
                    serverSuffix = server.toString();
                }
                File clansFile = new File(dir, CLAN_SAVED_FILE_NAME + serverSuffix);
                FileReader fr = new FileReader(clansFile);
                clans = new Gson().fromJson(fr, typeOfHashMap);
                fr.close();
            } catch (Exception e) {
            }
        } else {
            clans = LIST_OF_CLANS;
        }
        return clans;
    }

    public static void saveClan(Context ctx, Clan clan) {
        if (LIST_OF_CLANS != null)
            LIST_OF_CLANS.put(clan.getClanId(), clan);
        else {
            Map<Integer, Clan> clans = getSavedClans(ctx);
            LIST_OF_CLANS = clans;
            LIST_OF_CLANS.put(clan.getClanId(), clan);
        }
        saveClans(ctx, LIST_OF_CLANS);
    }

    private static void saveClans(Context ctx, Map<Integer, Clan> clans) {
        // Clear out members? maybe turn all clans into copies here instead of in the memory one.
        Map<Integer, Clan> saveClans = new HashMap<Integer, Clan>();
        for (Clan c : clans.values()) {
            Clan copy = c.copy();
            saveClans.put(c.getClanId(), copy);
        }
        Server server = CAApp.getServerType(ctx);
        String serverSuffix = "";
        if (server != Server.US) {
            serverSuffix = server.toString();
        }
        File dir = ctx.getDir(DIRECTORY_NAME, Context.MODE_PRIVATE);
        File clansFile = new File(dir, CLAN_SAVED_FILE_NAME + serverSuffix);
        if (!clansFile.exists())
            try {
                clansFile.createNewFile();
            } catch (IOException e) {
            }
        try {
            FileOutputStream fos = new FileOutputStream(clansFile);
            PrintWriter pw = new PrintWriter(fos);
            pw.print(new Gson().toJson(saveClans));
            pw.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void removeClan(Context ctx, Clan clan) {
        Map<Integer, Clan> clans = getSavedClans(ctx);
        CPStorageManager.clearClan(ctx, clan.getClanId());
        clans.remove(clan.getClanId());
        if (LIST_OF_CLANS != null)
            LIST_OF_CLANS.remove(clan.getClanId());
        saveClans(ctx, clans);
    }

    public static Map<Integer, Player> getSavedPlayers(Context ctx) {
        Server server = CAApp.getServerType(ctx);
        Map<Integer, Player> players = new HashMap<Integer, Player>();
        if (LIST_OF_PLAYERS == null) {
            try {
                Type typeOfHashMap = new TypeToken<Map<Integer, Player>>() {
                }.getType();
                File dir = ctx.getDir(DIRECTORY_NAME, Context.MODE_PRIVATE);
                String serverSuffix = "";
                if (server != Server.US) {
                    serverSuffix = server.toString();
                }
                File clansFile = new File(dir, PLAYER_SAVED_FILE_NAME + serverSuffix);
                FileReader fr = new FileReader(clansFile);
                GsonBuilder builder = new GsonBuilder();
                builder.serializeSpecialFloatingPointValues();
                Gson gson = builder.create();
                players = gson.fromJson(fr, typeOfHashMap);
                fr.close();
            } catch (Exception e) {
            }
        } else
            players = LIST_OF_PLAYERS;
        return players;
    }

    public static void savePlayer(Context ctx, Player player) {
        if (LIST_OF_PLAYERS != null)
            LIST_OF_PLAYERS.put(player.getId(), player);
        else {
            Map<Integer, Player> players = getSavedPlayers(ctx);
            LIST_OF_PLAYERS = players;
            LIST_OF_PLAYERS.put(player.getId(), player);
        }
        savePlayers(ctx, LIST_OF_PLAYERS);
    }

    private static void savePlayers(Context ctx, Map<Integer, Player> players) {
        Map<Integer, Player> savePlayers = new HashMap<Integer, Player>();
        for (Player p : players.values()) {
            Player copy = p.copy();
            savePlayers.put(p.getId(), copy);
        }
        Server server = CAApp.getServerType(ctx);
        File dir = ctx.getDir(DIRECTORY_NAME, Context.MODE_PRIVATE);
        String serverSuffix = "";
        if (server != Server.US) {
            serverSuffix = server.toString();
        }
        File clansFile = new File(dir, PLAYER_SAVED_FILE_NAME + serverSuffix);
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
            pw.print(gson.toJson(savePlayers));
            pw.flush();
            fos.close();
        } catch (Exception e) {
        }
    }

    public static void removePlayer(Context ctx, Player player) {
        Map<Integer, Player> players = getSavedPlayers(ctx);
        players.remove(player.getId());
        if (LIST_OF_PLAYERS != null)
            LIST_OF_PLAYERS.remove(player.getId());
        savePlayers(ctx, players);
    }

    public static List<Clan> getClanList(Context ctx) {
        List<Clan> clans = new ArrayList<Clan>();
        for (Clan c : getSavedClans(ctx).values()) {
            clans.add(c);
        }
        return clans;
    }

    public static void clear() {
        LIST_OF_CLANS = null;
        LIST_OF_PLAYERS = null;
        ADAPTER_CLANS_LIST = null;
        HitManager.clear();
    }

}
