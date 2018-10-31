package com.clanassist.tools;

import android.content.Context;

import com.clanassist.CAApp;
import com.clanassist.model.events.ClanLoadedFinishedEvent;
import com.clanassist.model.search.results.ClanPlayerWN8sTaskResult;
import com.utilities.logging.Dlog;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Harrison on 1/20/2015.
 */
public class DownloadedClanManager {

    private static Map<String, ClanPlayerWN8sTaskResult> results;

    private static Map<String, String> loadRunning;

    public static void init() {
        results = new HashMap<String, ClanPlayerWN8sTaskResult>();
        loadRunning = new HashMap<String, String>();
    }

    public static boolean isClanLoading(String clanId) {
        return loadRunning.get(clanId) != null;
    }

    public static boolean hasClanDownloadLoaded(Context ctx, String clanId) {
        return results.get(clanId) != null;
    }

    public static void loadClanDownload(final Context ctx, final int clanId) {
        Dlog.d("DownloadClanManager", "loadClanDownload");
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    loadRunning.put(clanId + "", "true");
                    ClanPlayerWN8sTaskResult result = CPStorageManager.getClanTaskResult(ctx, clanId);
                    if (result != null) {
                        results.put(clanId + "", result);
                        loadRunning.remove(clanId + "");
                    }
                    ClanLoadedFinishedEvent event = new ClanLoadedFinishedEvent();
                    event.setClanId(clanId + "");
                    CAApp.getEventBus().post(event);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    public static void removeResult(String clanId) {
        results.remove(clanId);
    }

    public static void putResult(String clanId, ClanPlayerWN8sTaskResult result) {
        results.put(clanId, result);
    }

    public static void clearResults() {
        results.clear();
    }

    public static ClanPlayerWN8sTaskResult getClanDownload(String clanId) {
        return results.get(clanId);
    }


}
