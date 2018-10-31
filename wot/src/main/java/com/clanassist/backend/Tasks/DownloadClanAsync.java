package com.clanassist.backend.Tasks;

import android.content.Context;

import com.clanassist.CAApp;
import com.clanassist.SVault;
import com.clanassist.model.clan.ClanGraphs;
import com.clanassist.model.infoobj.Tank;
import com.clanassist.model.infoobj.Tanks;
import com.clanassist.model.infoobj.VehicleWN8;
import com.clanassist.model.events.ClanDownloadedFinishedEvent;
import com.clanassist.model.infoobj.WN8Data;
import com.clanassist.model.player.Player;
import com.clanassist.model.player.WN8StatsInfo;
import com.clanassist.model.player.minimized.MinimizedPlayer;
import com.clanassist.model.search.results.ClanPlayerWN8sTaskResult;
import com.clanassist.model.statistics.Statistics;
import com.clanassist.tools.CPStorageManager;
import com.cp.assist.R;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.squareup.otto.Bus;
import com.utilities.logging.Dlog;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Harrison on 5/23/2015.
 */
public class DownloadClanAsync {

    private static final String TAG = "DownloadClanAsync";

    public static final int FIRST_PROGRESS = 5;
    public static final int PROGRESS_GET_PLAYERS_INFO = 70;
    public static final int SECOND_PROGRESS = 75;
    public static final int PROGRESS_STATS = 25;
    public static final int TOTAL = 100;

    private Context ctx;
    private Bus eventBus;
    private String clanId;

    private boolean canceled;

    private TaskHelper taskHelper;

    private Map<Integer, Player> players;

    private int initialSize;

    private int numDone;

    private List<GetAllPlayerInfo> asyncTasks;

    private BlockingQueue<Runnable> mWorkQueue;

    private ThreadPoolExecutor executor;

    public DownloadClanAsync(Context ctx, Bus eventBus, String clanId, String clanName, List<Player> playerList) {
        this.ctx = ctx;
        this.eventBus = eventBus;
        this.clanId = clanId;
        taskHelper = new TaskHelper(ctx, clanName, Integer.parseInt(clanId));
        initialSize = playerList.size();
        players = new HashMap<Integer, Player>();
        for (Player p : playerList) {
            players.put(p.getId(), p);
        }
        canceled = false;
        numDone = 0;
    }

    public void start() {
        CAApp.taskStarted(this);
        Crashlytics.setString(SVault.LOG_CLAN_DOWNLOAD, clanId);
        String baseUrl = CAApp.getApplicationIdURLString(ctx);
        String language = ctx.getResources().getString(R.string.language);
        String server = CAApp.getBaseAddress(ctx);
        Answers.getInstance().logCustom(new CustomEvent("Clan Download").putCustomAttribute("Server", CAApp.getServerType(ctx).getServerName()).putCustomAttribute("Language", language));
        int number_of_cores = Runtime.getRuntime().availableProcessors();
        taskHelper.start();
        mWorkQueue = new LinkedBlockingQueue<Runnable>();
        executor = new ThreadPoolExecutor(number_of_cores, number_of_cores, 60, TimeUnit.SECONDS, mWorkQueue);
        asyncTasks = new ArrayList<GetAllPlayerInfo>();
        taskHelper.sendProgressEvent(FIRST_PROGRESS, TOTAL);
        taskHelper.sendDescriptionChange(ctx.getString(R.string.download_players));
        for (Integer i : players.keySet()) {
            GetAllPlayerInfo info = new GetAllPlayerInfo(ctx, this, baseUrl, language, server);
            info.setGrabRecentWN8(true);
            asyncTasks.add(info);
            info.executeOnExecutor(executor, i);
        }
    }

    public synchronized void playerReceived(Player p) {
        players.put(p.getId(), p);
//        Dlog.d(TAG, "" + p.getId() + " p = " + p);
        anotherDone();
        int done = getNumberDone();
        int initialSize = initialPlayerSize();
        updateTaskHelperDownloadProgress(done, initialSize);
        if (done == initialSize || isCanceled()) {
            Dlog.d("Downloads", "Done");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    finishUp();
                }
            }).start();
        }
    }

    private void updateTaskHelperDownloadProgress(int current, int total) {
        float percentageDone = PROGRESS_GET_PLAYERS_INFO * ((float) current / total);
        taskHelper.sendProgressEvent(FIRST_PROGRESS + (int) percentageDone, TOTAL);
    }

    private void finishUp() {
        if (!canceled) {
            ClanPlayerWN8sTaskResult result = new ClanPlayerWN8sTaskResult();

            taskHelper.sendProgressEvent(SECOND_PROGRESS, TOTAL);
            taskHelper.sendDescriptionChange(ctx.getString(R.string.download_calculating));

            float bestWn8 = -1;
            int bestId = -1;
            float bestClanWn8 = -1;
            int bestClanId = -1;
            float bestSHWn8 = -1;
            int bestSHId = -1;

            double averageDayWn8 = 0, average7DayWn8 = 0, average30DayWn8 = 0, average60DayWn8 = 0;

            double averageExp = 0, averageKills = 0, averageBattles = 0, averageDamage = 0, averageWinRate = 0, averageWn8 = 0, averageClanWN8 = 0, averageClanWinRate = 0, averageSHWN8 = 0, averageSHWinRate = 0;
            Set<Integer> playerIds = players.keySet();
            for (Integer id : playerIds) {
                Player p = players.get(id);
                float wn8 = p.getWN8();
                float clanWN8 = p.getClanWN8();
                float strongHoldWN8 = p.getStrongholdWN8();

                //use each player and get the WN8 for them and set it in their own wn8
                if (wn8 > bestWn8) {
                    bestWn8 = wn8;
                    bestId = id;
                }
                if (clanWN8 > bestClanWn8) {
                    bestClanWn8 = clanWN8;
                    bestClanId = id;
                }
                if(strongHoldWN8 > bestSHWn8){
                    bestSHId = id;
                    bestSHWn8 = strongHoldWN8;
                }


                Statistics stats = p.getOverallStats();
                Statistics clan = p.getClanStats();
                Statistics stronghold = p.getStrongholdStats();
                averageWn8 += wn8;
                averageClanWN8 += clanWN8;
                averageSHWN8 += strongHoldWN8;
                if(stats != null) {
                    double battles = stats.getBattles();
                    averageExp += stats.getAverageXp();
                    averageBattles += stats.getBattles();
                    if (battles > 0) {
                        averageDamage += (stats.getDamageDealt() / battles);
                        averageKills += (stats.getFrags() / (battles - stats.getSurvivedBattles()));
                        averageWinRate += ((stats.getWins() / battles) * 100);
                    }
                }
                if(clan != null) {
                    double cwBattles = clan.getBattles();
                    if (cwBattles > 0) {
                        averageClanWinRate += ((clan.getWins() / cwBattles) * 100);
                    }
                }
                if(stronghold != null){
                    double shBattles = stronghold.getBattles();
                    if(shBattles > 0){
                        averageSHWinRate += ((stronghold.getWins() / shBattles) * 100);
                    }
                }
                WN8StatsInfo info = p.getWn8StatsInfo();
                if(info != null){
                    averageDayWn8 += info.getPastDay();
                    average7DayWn8 += info.getPastWeek();
                    average30DayWn8 += info.getPastMonth();
                    average60DayWn8 += info.getPastTwoMonths();
                }
            }
            taskHelper.sendProgressEvent(SECOND_PROGRESS + (PROGRESS_STATS / 4), TOTAL);

            Map<Integer, VehicleWN8> tier10Vehicles = new HashMap<Integer, VehicleWN8>();
            Map<Integer, VehicleWN8> tier8Vehicles = new HashMap<Integer, VehicleWN8>();
            Map<Integer, VehicleWN8> tier6Vehicles = new HashMap<Integer, VehicleWN8>();
            WN8Data wn8Data = CAApp.getInfoManager().getWN8Data(ctx);
            Tanks tanks = CAApp.getInfoManager().getTanks(ctx);

            for(Tank t : tanks.getTanksList().values()){
                if(t.getTier() == 10){
                    tier10Vehicles.put(t.getId(), wn8Data.getWN8(t.getId()));
                } else if(t.getTier() == 8){
                    tier8Vehicles.put(t.getId(), wn8Data.getWN8(t.getId()));
                } else if(t.getTier() == 6){
                    tier6Vehicles.put(t.getId(), wn8Data.getWN8(t.getId()));
                }
            }

            ClanGraphs graph = ClanGraphs.create(tier10Vehicles.keySet(), tier8Vehicles.keySet(), tier6Vehicles.keySet());

            if (!isCanceled() && !canceled) {
                if (bestId != -1) {
                    Player p = players.get(bestId);
                    result.setBestWN8AccountName(p.getName());
                    result.setBestWN8AccountId(bestId);
                    result.setBestWN8AccountNumber((int) bestWn8);
                }
                if (bestClanId != -1) {
                    Player p = players.get(bestClanId);
                    result.setBestClanWN8AccountName(p.getName());
                    result.setBestClanWN8AccountId(bestClanId);
                    result.setBestClanWN8AccountNumber((int) bestClanWn8);
                }
                if(bestSHId != -1){
                    Player p = players.get(bestSHId);
                    result.setBestSHWN8AccountName(p.getName());
                    result.setBestSHWN8AccountId(bestSHId);
                    result.setBestSHWN8AccountNumber((int) bestSHWn8);

                }
                double count = initialSize;
                result.setTotalBattles(averageBattles);
                result.setTotalDamage(averageDamage);
                result.setTotalKills(averageKills);

                if (count > 0) {
                    averageExp = averageExp / count;
                    averageBattles = averageBattles / count;
                    averageDamage = averageDamage / count;
                    averageKills = averageKills / count;
                    averageWinRate = averageWinRate / count;
                    averageWn8 = averageWn8 / count;
                    averageClanWN8 = averageClanWN8 / count;
                    averageClanWinRate = averageClanWinRate / count;
                    averageSHWN8 = averageSHWN8 / count;
                    averageSHWinRate = averageSHWinRate / count;
                    averageDayWn8 = averageDayWn8 / count;
                    average7DayWn8 = average7DayWn8 / count;
                    average30DayWn8 = average30DayWn8 / count;
                    average60DayWn8 = average60DayWn8 / count;
                }

                result.setAverageBattles((int) averageBattles);
                result.setAverageDamage((int) averageDamage);
                result.setAverageExp(averageExp);
                result.setAverageKills(averageKills);
                result.setAverageWinRate(averageWinRate);
                result.setOverallWN8(averageWn8);
                result.setOverallClanWN8(averageClanWN8);
                result.setAverageClanWinRate(averageClanWinRate);
                result.setAverageSHWinRate(averageSHWinRate);
                result.setOverallSHWN8(averageSHWN8);

                result.setAverageDayWn8(averageDayWn8);
                result.setAverage7DayWn8(average7DayWn8);
                result.setAverage30DayWn8(average30DayWn8);
                result.setAverage60DayWn8(average60DayWn8);

                taskHelper.sendProgressEvent(SECOND_PROGRESS + (PROGRESS_STATS / 2), TOTAL);
                List<MinimizedPlayer> playersList = new ArrayList<MinimizedPlayer>();
                for (Player player : players.values()) {
                    playersList.add(MinimizedPlayer.build(player));
                }

                taskHelper.sendDescriptionChange(ctx.getString(R.string.download_graphs));
                int averageTier = graph.calculate(playersList, wn8Data.getWN8s(), CAApp.getInfoManager().getTanks(ctx));
                Dlog.d(TAG, "graph = " + graph.toString());

                result.setAverageTier(averageTier);
                result.setGraphs(graph);
                result.setPlayers(playersList);
                result.setClanId(clanId);
                Dlog.d(TAG, "done");
                CPStorageManager.saveClanTaskResult(ctx, Integer.parseInt(clanId), result);
                taskHelper.done();
                CAApp.taskEnded();
                CAApp.clanDownloaded(ctx, clanId);
                Dlog.d(TAG, "End");
                if (result != null) {
                    ClanDownloadedFinishedEvent event = new ClanDownloadedFinishedEvent();
                    event.setClanId(clanId);
                    eventBus.post(event);
                }
            } else {
                onCanceled();
            }
        } else {
            for (GetAllPlayerInfo info : asyncTasks) {
                info.setCanceled(true);
            }
            onCanceled();
        }
    }

    public void onCanceled() {
        Dlog.d(TAG, "onCanceled");
        for (GetAllPlayerInfo info : asyncTasks) {
            info.setCanceled(true);
        }
        taskHelper.cancel();
        ClanDownloadedFinishedEvent event = new ClanDownloadedFinishedEvent();
        event.setCancelled(true);
        event.setClanId(clanId);
        eventBus.post(event);
        CAApp.ongoingTask = null;

        executor.shutdown();
        try {
            if (executor.awaitTermination(10, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }

    }

    public synchronized boolean isCanceled() {
        return canceled;
    }

    private synchronized int initialPlayerSize() {
        return initialSize;
    }

    private synchronized int getNumberDone() {
        return numDone;
    }

    private synchronized void anotherDone() {
        numDone++;
    }

    public synchronized void cancel(boolean cancel) {
        canceled = cancel;
    }

    public String getClanId() {
        return clanId;
    }

    public void setClanId(String clanId) {
        this.clanId = clanId;
    }
}
