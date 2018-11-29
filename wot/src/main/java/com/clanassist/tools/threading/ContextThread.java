package com.clanassist.tools.threading;

import android.app.Activity;
import android.os.Handler;
import androidx.fragment.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.clanassist.CAApp;
import com.clanassist.SVault;
import com.clanassist.model.events.details.ClanQuerySortFinished;
import com.clanassist.model.holders.ClanQueryListModel;
import com.clanassist.model.holders.ClanTankHolder;
import com.clanassist.model.player.Player;
import com.clanassist.model.player.minimized.MinimizedPlayer;
import com.clanassist.model.player.minimized.MinimizedVehicleInfo;
import com.clanassist.model.search.results.ClanPlayerWN8sTaskResult;
import com.clanassist.tools.CPStorageManager;
import com.clanassist.tools.HitManager;
import com.clanassist.tools.WN8ColorManager;
import com.clanassist.ui.UIUtils;
import com.clanassist.ui.details.DetailsTabbedFragment;
import com.cp.assist.R;
import com.utilities.Utils;
import com.utilities.preferences.Prefs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Harrison on 9/27/2014.
 */
public class ContextThread {

    private static final int MASTERY_TOTAL = 4;
    public static final String DELIMITER = "~";

    FragmentActivity mActivity;
    LinearLayout mContainer;

    ClanPlayerWN8sTaskResult result;

    String mSortType;

    boolean isColorBlind;

    int selectedId;

    private boolean isRunning;

    private boolean isCanceled;
    private final Object lock = new Object();

    public ContextThread(FragmentActivity activity, LinearLayout mContainer, String sortType) {
        this.mActivity = activity;
        this.mContainer = mContainer;
        mSortType = sortType;
        Prefs pref = new Prefs(mActivity);
        isColorBlind = pref.getBoolean(SVault.PREF_COLORBLIND, false);
        isCanceled = false;
        isRunning = false;
    }

    public void createClanQueryList(ClanPlayerWN8sTaskResult taskResult, final int selectedId) {
        this.result = taskResult;
        this.selectedId = selectedId;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                if (result.getPlayers() != null && !result.getPlayers().isEmpty()) {
                    List<ClanQueryListModel> items = new ArrayList<ClanQueryListModel>();
                    for (MinimizedPlayer player : result.getPlayers()) {
                        for (MinimizedVehicleInfo info : player.getInfos()) {
                            if (info.getId() == selectedId) {
                                try {
                                    ClanQueryListModel item = new ClanQueryListModel();
                                    item.setPlayerId(player.getId());
                                    item.setPlayerName(player.getName());
                                    item.setBattles(info.getOverall().getGames());
                                    item.setClanWn8((int) info.getCwn8());

                                    item.setWinRate(((float) info.getOverall().getWins() / (float) info.getOverall().getGames()) * 100);
                                    item.setTankWn8((int) info.getWn8());
                                    item.setAvgDam(info.getOverall().getDamage() / info.getOverall().getGames());
                                    item.setAvgExp(info.getOverall().getaXp());
                                    item.setHitPercentage(info.getOverall().getHitsPC());
                                    item.setKd(((double) info.getOverall().getFrags() / (info.getOverall().getGames() - info.getOverall().getSur())));
                                    item.setSurvivedBattles(info.getOverall().getSur());

                                    item.setMastery(info.getMas());

                                    items.add(item);
                                } catch (Exception e) {
                                }
                                break;
                            }
                        }
                    }
                    ClanQuerySortFinished event = new ClanQuerySortFinished();
                    event.setEmpty(items.isEmpty());
                    CAApp.getEventBus().post(event);
                    setUpClanMembers(items);
                }
            }
        });
        t.start();
    }

    public void setUpClanMembers(List<ClanQueryListModel> players) {
        clearContainerView(mContainer, mActivity);

        Collections.sort(players, new Comparator<ClanQueryListModel>() {
            @Override
            public int compare(ClanQueryListModel lhs, ClanQueryListModel rhs) {
                return rhs.getTankWn8() - lhs.getTankWn8();
            }
        });
        LayoutInflater inflater = LayoutInflater.from(mActivity);
        for (ClanQueryListModel p : players) {
            View view = inflater.inflate(R.layout.list_clan_tank, mContainer, false);
            ClanTankHolder holder = ClanTankHolder.getHolder(view, p.getPlayerName(), p.getPlayerId());
            UIUtils.setUpCard(view, 0);
            holder.aAdvancedSection.setVisibility(View.VISIBLE);
            holder.aWN8.setVisibility(View.VISIBLE);
            holder.aRecents.setVisibility(View.GONE);
            holder.tvRecentTitle.setVisibility(View.GONE);
            holder.ivTank.setVisibility(View.GONE);
            holder.tvRole.setVisibility(View.GONE);
            holder.aLastBattle.setVisibility(View.GONE);
            holder.aClanWn8.setVisibility(View.GONE);

            holder.tvName.setText(p.getPlayerName());
            holder.tvWn8.setText(p.getTankWn8() + "");
//            holder.tvClanWn8.setText(p.getClanWn8() + "");

//            WN8ColorManager.setColorForWN8(holder.aClanWn8, p.getClanWn8(), isColorBlind);
            WN8ColorManager.setColorForWN8(holder.aOverallWn8, p.getTankWn8(), isColorBlind);

            holder.tvMastery.setVisibility(View.VISIBLE);
            holder.tvMastery.setText(getMasteryBadgeLevel(p.getMastery()));

            holder.tvDamage.setText((p.getAvgDam()) + "");
            holder.tvExp.setText(p.getAvgExp() + "");
            holder.tvHitPer.setText(p.getHitPercentage() + "");
            holder.tvKD.setText(Utils.getDefaultDecimalFormatter().format(p.getKd()) + "");
            holder.tvBattles.setText("" + p.getBattles());
            holder.tvWinRate.setText(Utils.getOneDepthDecimalFormatter().format(p.getWinRate()) + "%");

            view.setTag(p.getPlayerId() + DELIMITER + p.getPlayerName());
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String tag = (String) v.getTag();
                        String[] array = tag.split(DELIMITER);
                        int id = Integer.parseInt(array[0]);
                        String name = array[1];
                        Player player = new Player();
                        player.setId(id);
                        player.setName(name);
                        CPStorageManager.saveTempStoredPlayer(mActivity, player);
                        HitManager.removePlayerProfileHit(player.getId());

                        DetailsTabbedFragment frag = new DetailsTabbedFragment();
                        frag.setFromSearch(true);
                        frag.setAccountId(id);
                        frag.setPlayer(true);
                        frag.setName(name);
                        mActivity.getSupportFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                                .replace(R.id.container, frag)
                                .addToBackStack(name).commit();
                        Handler h = new Handler();
                        h.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mActivity.invalidateOptionsMenu();
                            }
                        }, 500);
                    } catch (NumberFormatException e) {
                    }
                }
            });
            sendToUIThread(mContainer, view, mActivity);
        }
    }

    private String getMasteryBadgeLevel(int mastery) {
        String mas = "";
        if (mastery == MASTERY_TOTAL) {
            mas = "M";
        } else if (mastery == 1) {
            mastery = 3;
        } else if (mastery == 3) {
            mastery = 1;
        }
        if (mastery < MASTERY_TOTAL)
            mas = mastery + "";
        return mas;
    }

    private void clearContainerView(final LinearLayout container, Activity activity) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                container.removeAllViews();
            }
        });
    }

    private void sendToUIThread(final LinearLayout container, final View view, Activity act) {
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                container.addView(view);
            }
        });
    }

    public boolean isRunning() {
        synchronized (lock) {
            return isRunning;
        }
    }

    public void setRunning(boolean isRunning) {
        synchronized (lock) {
            this.isRunning = isRunning;
        }
    }

    public boolean isCanceled() {
        synchronized (lock) {
            return isCanceled;
        }
    }

    public void cancel() {
        synchronized (lock) {
            this.isCanceled = true;
        }
    }
}
