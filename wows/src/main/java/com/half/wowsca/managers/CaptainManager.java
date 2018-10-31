package com.half.wowsca.managers;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.half.wowsca.model.Captain;
import com.half.wowsca.model.enums.Server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by slai4 on 9/15/2015.
 */
public class CaptainManager {


    public static final String DIRECTORY_NAME = "wowsassist";
    private static final String PLAYER_SAVED_FILE_NAME = "list_of_captains";
    private static final String TEMP_STORAGE_FILE = "tempstored";

    private static Map<String, Captain> CAPTAINS;

    private static Captain TEMP;

    public static Map<String, Captain> getCaptains(Context ctx) {
        Map<String, Captain> caps = new HashMap<String, Captain>();
        if (CAPTAINS == null) {
            try {
                Type typeOfHashMap = new TypeToken<Map<String, Captain>>() {
                }.getType();
                File dir = ctx.getDir(DIRECTORY_NAME, Context.MODE_PRIVATE);
                File clansFile = new File(dir, PLAYER_SAVED_FILE_NAME);
                FileReader fr = new FileReader(clansFile);
                GsonBuilder builder = new GsonBuilder();
                builder.serializeSpecialFloatingPointValues();
                Gson gson = builder.create();
                caps = gson.fromJson(fr, typeOfHashMap);
                fr.close();
            } catch (Exception e) {
            }
            CAPTAINS = caps;
        } else {
            caps = CAPTAINS;
        }
        return caps;
    }

    public static String getCapIdStr(Captain c) {
        return c.getServer().toString() + c.getId();
    }

    public static String createCapIdStr(Server s, int id) {
        return s.toString() + id;
    }

    public static void saveCaptain(Context ctx, Captain c) {
        if(c != null) { // put in to prevent crash on line 59. If not fixed might be c.getServer is null
            if (CAPTAINS != null) {
                CAPTAINS.put(getCapIdStr(c), c);
            } else {
                Map<String, Captain> captains = getCaptains(ctx);
                CAPTAINS = captains;
                CAPTAINS.put(getCapIdStr(c), c);
            }
            saveCaptains(ctx, CAPTAINS);
        }
    }

    private static void saveCaptains(Context ctx, Map<String, Captain> clans) {
        // Clear out members? maybe turn all clans into copies here instead of in the memory one.
        Map<String, Captain> saveCaptains = new HashMap<String, Captain>();
        for (Captain c : clans.values()) {
            Captain copy = c.copy();
            saveCaptains.put(getCapIdStr(c), copy);
        }
        File dir = ctx.getDir(DIRECTORY_NAME, Context.MODE_PRIVATE);
        File captainFiles = new File(dir, PLAYER_SAVED_FILE_NAME);
        if (!captainFiles.exists())
            try {
                captainFiles.createNewFile();
            } catch (IOException e) {
            }
        try {
            FileOutputStream fos = new FileOutputStream(captainFiles);
            PrintWriter pw = new PrintWriter(fos);
            GsonBuilder builder = new GsonBuilder();
            builder.serializeSpecialFloatingPointValues();
            Gson gson = builder.create();
            pw.print(gson.toJson(saveCaptains));
            pw.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void removeCaptain(Context ctx, String id) {
        Map<String, Captain> captains = getCaptains(ctx);
        captains.remove(id);
        if (CAPTAINS != null) {
            CAPTAINS.remove(id);
        }
        saveCaptains(ctx, captains);
    }


    public static boolean fromSearch(Context ctx, Server s, int id) {
        return getCaptains(ctx).get(createCapIdStr(s, id)) == null;
    }

    public static Captain getTEMP(Context ctx) {
        if (TEMP == null)
            TEMP = getTempStoredPlayer(ctx);
        return TEMP;
    }

    public static void saveTempStoredCaptain(Context ctx, Captain captain) {
        File dir = ctx.getDir(DIRECTORY_NAME, Context.MODE_PRIVATE);
        File tempCaptainFile = new File(dir, TEMP_STORAGE_FILE);
        if (!tempCaptainFile.exists())
            try {
                tempCaptainFile.createNewFile();
            } catch (IOException e) {
            }
        try {
            FileOutputStream fos = new FileOutputStream(tempCaptainFile);
            PrintWriter pw = new PrintWriter(fos);
            GsonBuilder builder = new GsonBuilder();
            builder.serializeSpecialFloatingPointValues();
            Gson gson = builder.create();
            pw.print(gson.toJson(captain));
            pw.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Captain getTempStoredPlayer(Context ctx) {
        Captain c = null;
        try {
            File dir = ctx.getDir(DIRECTORY_NAME, Context.MODE_PRIVATE);
            File tempClan = new File(dir, TEMP_STORAGE_FILE);
            FileReader fr = new FileReader(tempClan);
            GsonBuilder builder = new GsonBuilder();
            builder.serializeSpecialFloatingPointValues();
            Gson gson = builder.create();
            c = gson.fromJson(fr, Captain.class);
            fr.close();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
        return c;
    }

    public static void deleteTemp(Context ctx) {
        try {
            TEMP = null;
            File dir = ctx.getDir(DIRECTORY_NAME, Context.MODE_PRIVATE);
            File temp = new File(dir, TEMP_STORAGE_FILE);
            if (temp.exists())
                temp.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
