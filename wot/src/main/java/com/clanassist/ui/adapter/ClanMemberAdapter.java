package com.clanassist.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import com.clanassist.CAApp;
import com.clanassist.model.events.details.SortingFinishedEvent;
import com.clanassist.model.holders.ClanTankHolder;
import com.clanassist.model.interfaces.IListSort;
import com.clanassist.model.player.Player;
import com.clanassist.model.player.WN8StatsInfo;
import com.clanassist.model.statistics.Statistics;
import com.clanassist.tools.WN8ColorManager;
import com.clanassist.ui.UIUtils;
import com.cp.assist.R;
import com.utilities.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Created by Harrison on 9/25/2014.
 */
public class ClanMemberAdapter extends ArrayAdapter<Player> implements IListSort {

    public static final String DELIMITER = "~";

    private boolean isColorBlind;

    private MemberAdapterFilter filter;

    private List<Player> players;
    private ArrayList<Player> backupObjects;

    public ClanMemberAdapter(Context context, int resource, List<Player> objects) {
        super(context, resource, objects);
        players = objects;
        backupObjects = (ArrayList<Player>) objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Player p = getItem(position);
        View view = convertView;
        ClanTankHolder holder;
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.list_clan_tank, parent, false);
            holder = ClanTankHolder.getHolder(view, p.getName(), p.getId());
            UIUtils.setUpCard(view, 0);
        } else {
            holder = (ClanTankHolder) view.getTag();
        }

        holder.ivTank.setVisibility(View.GONE);
        holder.tvName.setText(p.getName() + "");
        holder.tvRole.setText(p.getClanInfo().getRole_i18n());
        holder.tvMastery.setVisibility(View.GONE);

        if (p.getOverallStats() != null) {
            holder.tvWn8.setText(((int) p.getWN8()) + "");
            WN8ColorManager.setColorForWN8(holder.aOverallWn8, (int) p.getWN8(), isColorBlind);

            holder.tvClanWn8.setText(((int) p.getClanWN8()) + "");
            WN8ColorManager.setColorForWN8(holder.aClanWn8, (int) p.getClanWN8(), isColorBlind);

            holder.tvSHWn8.setText(((int) p.getStrongholdWN8()) + "");
            WN8ColorManager.setColorForWN8(holder.aSHWn8, (int) p.getStrongholdWN8(), isColorBlind);

            holder.aWN8.setVisibility(View.VISIBLE);
            Statistics stats = p.getOverallStats();
            int battles = stats.getBattles();
            if (battles > 0) {
                int surBattle = battles - stats.getSurvivedBattles();
                if (surBattle <= 0)
                    surBattle = battles;
                holder.tvDamage.setText((stats.getDamageDealt() / (surBattle)) + "");
                holder.tvExp.setText(stats.getAverageXp() + "");
                holder.tvHitPer.setText(stats.getHitsPercentage() + "");
                double kd = ((double) stats.getFrags() / (battles - stats.getSurvivedBattles()));
                holder.tvKD.setText(Utils.getDefaultDecimalFormatter().format(kd) + "");
                holder.tvBattles.setText("" + battles);
                double winrate = ((double) stats.getWins() / battles) * 100;
                holder.tvWinRate.setText(Utils.getOneDepthDecimalFormatter().format(winrate) + "%");

                Calendar lastBattle = p.getLastBattleTime();
                holder.tvLastBattle.setText(Utils.getDayMonthYearFormatter(getContext()).format(lastBattle.getTime()));

                WN8StatsInfo info = p.getWn8StatsInfo();
                if(info != null) {
                    holder.aRecents.setVisibility(View.VISIBLE);

                    WN8ColorManager.setColorForWN8(holder.aRecentsDay, info.getPastDay(), isColorBlind);
                    holder.tvDayWn8.setText(info.getPastDay() + "");

                    WN8ColorManager.setColorForWN8(holder.aRecentsWeek, info.getPastWeek(), isColorBlind);
                    holder.tvWeekWn8.setText(info.getPastWeek() + "");

                    WN8ColorManager.setColorForWN8(holder.aRecentsMonth, info.getPastMonth(), isColorBlind);
                    holder.tvMonthWn8.setText(info.getPastMonth() + "");

                    WN8ColorManager.setColorForWN8(holder.aRecentsTwoMonth, info.getPastTwoMonths(), isColorBlind);
                    holder.tvTwoMonthWn8.setText(info.getPastTwoMonths() + "");

                } else {
                    holder.aRecents.setVisibility(View.GONE);
                }
                holder.aAdvancedSection.setVisibility(View.VISIBLE);

            } else {
                holder.aAdvancedSection.setVisibility(View.GONE);
                holder.aRecents.setVisibility(View.GONE);
            }

        } else {
            holder.aAdvancedSection.setVisibility(View.GONE);
            holder.aWN8.setVisibility(View.GONE);
        }
        view.setTag(holder);
        return view;
    }

    @Override
    public int getCount() {
        try {
            return players.size();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public Player getItem(int position) {
        return players.get(position);
    }

    public boolean isColorBlind() {
        return isColorBlind;
    }

    public void setColorBlind(boolean isColorBlind) {
        this.isColorBlind = isColorBlind;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public List<Player> getPlayers() {
        return players;
    }

    @Override
    public void sort(String sortType) {
        String[] sortTypes = getContext().getResources().getStringArray(R.array.clan_sorting);
        int i;
        for (i = 0; i < sortTypes.length; i++) {
            if (sortTypes[i].equals(sortType)) {
                break;
            }
        }
        try {
            switch (i) {
                case 0:
                    Collections.sort(players);
                    break;
                case 1:
                    Collections.sort(players, new Comparator<Player>() {
                        @Override
                        public int compare(Player lhs, Player rhs) {
                            String rhsName = rhs.getName();
                            String lhsName = lhs.getName();
                            return lhsName.compareToIgnoreCase(rhsName);
                        }
                    });
                    break;
                case 2:
                    Collections.sort(players, new Comparator<Player>() {
                        @Override
                        public int compare(Player lhs, Player rhs) {
                            Statistics lhsStats = lhs.getOverallStats();
                            Statistics rhsStats = rhs.getOverallStats();
                            int rhsBattles = 0;
                            int lhsBattles = 0;
                            try {
                                rhsBattles = rhsStats.getBattles();
                            } catch (Exception e) {
                            }
                            try {
                                lhsBattles = lhsStats.getBattles();
                            } catch (Exception e) {
                            }
                            return rhsBattles - lhsBattles;
                        }
                    });
                    break;
                case 3:
                    Collections.sort(players, new Comparator<Player>() {
                        @Override
                        public int compare(Player lhs, Player rhs) {
                            Statistics lhsStats = lhs.getOverallStats();
                            Statistics rhsStats = rhs.getOverallStats();
                            double lhsWR = 0;
                            double rhsWR = 0;
                            try {
                                lhsWR = ((float) lhsStats.getWins() / (float) lhsStats.getBattles()) * 100;
                            } catch (Exception e) {
                            }
                            try {
                                rhsWR = ((float) rhsStats.getWins() / (float) rhsStats.getBattles()) * 100;
                            } catch (Exception e) {
                            }
                            return (int) (rhsWR - lhsWR);
                        }
                    });
                    break;
                case 4:
                    Collections.sort(players, new Comparator<Player>() {
                        @Override
                        public int compare(Player lhs, Player rhs) {
                            return (int) (rhs.getWN8() - lhs.getWN8());
                        }
                    });
                    break;
                case 5:
                    Collections.sort(players, new Comparator<Player>() {
                        @Override
                        public int compare(Player lhs, Player rhs) {
                            return (int) (rhs.getClanWN8() - lhs.getClanWN8());
                        }
                    });
                    break;
                case 6:
                    Collections.sort(players, new Comparator<Player>() {
                        @Override
                        public int compare(Player lhs, Player rhs) {
                            return (int) (rhs.getStrongholdWN8() - lhs.getStrongholdWN8());
                        }
                    });
                    break;
                case 7:
                    Collections.sort(players, new Comparator<Player>() {
                        @Override
                        public int compare(Player lhs, Player rhs) {
                            Calendar lhsC = lhs.getLastBattleTime();
                            Calendar rhsC = rhs.getLastBattleTime();
                            if (lhsC == null || rhsC == null)
                                return 0;
                            else
                                return rhsC.getTime().compareTo(lhsC.getTime());
                        }
                    });
                    break;
                case 8:
                    Collections.sort(players, new Comparator<Player>() {
                        @Override
                        public int compare(Player lhs, Player rhs) {
                            WN8StatsInfo lhsInfo = lhs.getWn8StatsInfo();
                            WN8StatsInfo rhsInfo = rhs.getWn8StatsInfo();
                            int lhsWn8 = -1;
                            if(lhsInfo != null)
                                lhsWn8 = lhsInfo.getPastDay();
                            int rhsWn8 = -1;
                            if(rhsInfo != null)
                                rhsWn8 = rhsInfo.getPastDay();
                            return rhsWn8 - lhsWn8;
                        }
                    });
                    break;
                case 9:
                    Collections.sort(players, new Comparator<Player>() {
                        @Override
                        public int compare(Player lhs, Player rhs) {
                            WN8StatsInfo lhsInfo = lhs.getWn8StatsInfo();
                            WN8StatsInfo rhsInfo = rhs.getWn8StatsInfo();
                            int lhsWn8 = -1;
                            if(lhsInfo != null)
                                lhsWn8 = lhsInfo.getPastWeek();
                            int rhsWn8 = -1;
                            if(rhsInfo != null)
                                rhsWn8 = rhsInfo.getPastWeek();
                            return rhsWn8 - lhsWn8;
                        }
                    });
                    break;
                case 10:
                    Collections.sort(players, new Comparator<Player>() {
                        @Override
                        public int compare(Player lhs, Player rhs) {
                            WN8StatsInfo lhsInfo = lhs.getWn8StatsInfo();
                            WN8StatsInfo rhsInfo = rhs.getWn8StatsInfo();
                            int lhsWn8 = -1;
                            if(lhsInfo != null)
                                lhsWn8 = lhsInfo.getPastMonth();
                            int rhsWn8 = -1;
                            if(rhsInfo != null)
                                rhsWn8 = rhsInfo.getPastMonth();
                            return rhsWn8 - lhsWn8;
                        }
                    });
                    break;
                case 11:
                    Collections.sort(players, new Comparator<Player>() {
                        @Override
                        public int compare(Player lhs, Player rhs) {
                            WN8StatsInfo lhsInfo = lhs.getWn8StatsInfo();
                            WN8StatsInfo rhsInfo = rhs.getWn8StatsInfo();
                            int lhsWn8 = -1;
                            if(lhsInfo != null)
                                lhsWn8 = lhsInfo.getPastTwoMonths();
                            int rhsWn8 = -1;
                            if(rhsInfo != null)
                                rhsWn8 = rhsInfo.getPastTwoMonths();
                            return rhsWn8 - lhsWn8;
                        }
                    });
                    break;
            }
            CAApp.getEventBus().post(new SortingFinishedEvent());
            notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new MemberAdapterFilter();
        }
        return filter;
    }

    public void restoreAllCards() {
        players = (ArrayList<Player>) backupObjects.clone();
        notifyDataSetChanged();
    }

    private class MemberAdapterFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint == null) {
                results.values = backupObjects;
                results.count = backupObjects.size();
            } else {
                List<Player> filteredList = new ArrayList<Player>();
                Locale local = Locale.getDefault();
                String constraintVar = constraint.toString().toLowerCase(local);
                for (Player item : backupObjects) {
                    boolean accepted = item.getName().toLowerCase(local).contains(constraintVar);
                    if (accepted) {
                        filteredList.add(item);
                    }
                }
                results.values = filteredList;
                results.count = filteredList.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            if (results.count == 0)
                notifyDataSetInvalidated();
            else {
                players = (ArrayList<Player>) results.values;
                notifyDataSetChanged();
            }
        }
    }


}
