package com.clanassist.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Toast;

import com.clanassist.CAApp;
import com.clanassist.model.events.details.SortingFinishedEvent;
import com.clanassist.model.holders.ClanTankHolder;
import com.clanassist.model.infoobj.Tank;
import com.clanassist.model.interfaces.IListSort;
import com.clanassist.model.player.PlayerVehicleInfo;
import com.clanassist.model.statistics.Statistics;
import com.clanassist.tools.PlayerHelper;
import com.clanassist.tools.WN8ColorManager;
import com.clanassist.tools.comparators.PlayerTanksComparator;
import com.clanassist.ui.UIUtils;
import com.cp.assist.R;
import com.squareup.picasso.Picasso;
import com.utilities.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Created by Harrison on 9/25/2014.
 */
public class PlayerTankAdapter extends ArrayAdapter<PlayerVehicleInfo> implements IListSort {

    private static final int MASTERY_TOTAL = 4;

    private List<PlayerVehicleInfo> objects;
    private ArrayList<PlayerVehicleInfo> backupObjects;

    private boolean isColorBlind;

    private VehicleAdapterFilter filter;

    public PlayerTankAdapter(Context context, int resource, List<PlayerVehicleInfo> objects) {
        super(context, resource, objects);
        this.objects = objects;
        backupObjects = (ArrayList<PlayerVehicleInfo>) objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PlayerVehicleInfo info = getObjects().get(position);
        ClanTankHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_clan_tank, parent, false);
            holder = ClanTankHolder.getHolder(convertView, info.getName(), info.getTankId());
            holder.aAdvancedSection.setVisibility(View.VISIBLE);
            holder.aWN8.setVisibility(View.VISIBLE);
            holder.aClanWn8.setVisibility(View.GONE);
            holder.aLastBattle.setVisibility(View.GONE);
            holder.aRecents.setVisibility(View.GONE);
            holder.tvRecentTitle.setVisibility(View.GONE);
            holder.tvRole.setVisibility(View.GONE);
            UIUtils.setUpCard(convertView, 0);
        } else {
            holder = (ClanTankHolder) convertView.getTag();
        }

        Tank wn = CAApp.getInfoManager().getTanks(getContext()).getTank(info.getTankId());
        if(wn != null) {
            holder.tvName.setText(wn.getName());
            Picasso.with(getContext()).load(wn.getContour()).into(holder.ivTank);
            holder.ivTank.setVisibility(View.VISIBLE);
        } else {
            holder.tvName.setText(info.getTankId() + "");
            holder.ivTank.setVisibility(View.GONE);
        }
        try {
        } catch (Exception e) {
        }

        double winrate = ((double) info.getOverallStats().getWins() / info.getOverallStats().getBattles()) * 100;
        float wn8 = info.getWN8();
        float clanWn8 = info.getClanWN8();

        holder.tvWn8.setText(Utils.getDefaultDecimalFormatter().format(wn8) + "");

        WN8ColorManager.setColorForWN8(holder.aOverallWn8, (int) wn8, isColorBlind);

        holder.tvMastery.setText(PlayerHelper.getMasteryBadgeLevel(info.getMarkOfMastery()) + "");
        holder.tvMastery.setVisibility(View.VISIBLE);

        Statistics overall = info.getOverallStats();
        int battles = overall.getBattles();
        if (battles > 0) {
            holder.tvDamage.setText((overall.getDamageDealt() / battles) + "");

            holder.tvExp.setText(overall.getAverageXp() + "");
            holder.tvHitPer.setText(overall.getHitsPercentage() + "");

            double kd = ((double) overall.getFrags() / (battles - overall.getSurvivedBattles()));
            holder.tvKD.setText(Utils.getDefaultDecimalFormatter().format(kd) + "");
            holder.tvBattles.setText("" + battles);

            holder.tvWinRate.setText(Utils.getOneDepthDecimalFormatter().format(winrate) + "%");
        }
        convertView.setTag(holder);
        return convertView;
    }

    @Override
    public int getCount() {
        try {
            return objects.size();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public PlayerVehicleInfo getItem(int position) {
        return objects.get(position);
    }

    @Override
    public void sort(String sortType) {
        String[] sortTypes = getContext().getResources().getStringArray(R.array.advanced_sorting);
        int i;
        for (i = 0; i < sortTypes.length; i++) {
            if (sortTypes[i].equals(sortType)) {
                break;
            }
        }
        if(objects == null)
            objects = backupObjects;
        if(objects != null) {
            PlayerTanksComparator comp = new PlayerTanksComparator();
            switch (i) {
                case 0:
                    Collections.sort(objects, comp.battlesComparator);
                    break;
                case 1:
                    Collections.sort(objects, comp.winComparator);
                    break;
                case 2:
                    Collections.sort(objects, comp.masteryComparator);
                    break;
                case 3:
                    Collections.sort(objects, comp.wn8Comparator);
                    break;
                case 4:
                    Collections.sort(objects, comp.avgExpComparator);
                    break;
                case 5:
                    Collections.sort(objects, comp.avgDamComparator);
                    break;
                case 6:
                    Collections.sort(objects, comp.kdComparator);
                    break;
                case 7:
                    comp.compareByTankName(getContext(), objects);
                    break;
                case 8:
                    comp.compareByTier(getContext(), objects);
                    break;
                case 9:
                    comp.compareByTierReverse(getContext(), objects);
                    break;
            }
        }
        CAApp.getEventBus().post(new SortingFinishedEvent());
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new VehicleAdapterFilter();
        }
        return filter;
    }

    public void restoreAllCards() {
        objects = (ArrayList<PlayerVehicleInfo>) backupObjects.clone();
        notifyDataSetChanged();
    }

    private class VehicleAdapterFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint == null) {
                results.values = backupObjects;
                results.count = backupObjects.size();
            } else {
                try {
                    List<PlayerVehicleInfo> filteredList = new ArrayList<PlayerVehicleInfo>();
                    Locale local = Locale.getDefault();
                    String constraintVar = constraint.toString().toLowerCase(local);
                    for (PlayerVehicleInfo item : backupObjects) {
                        if (!TextUtils.isEmpty(item.getName())) {
                            boolean accepted = item.getName().toLowerCase(local).contains(constraintVar);
                            if (accepted) {
                                filteredList.add(item);
                            }
                        }
                    }
                    results.values = filteredList;
                    results.count = filteredList.size();
                } catch (Exception e) {
                    Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            if (results.count == 0)
                notifyDataSetInvalidated();
            else {
                objects = (ArrayList<PlayerVehicleInfo>) results.values;
                notifyDataSetChanged();
            }
        }
    }

    public List<PlayerVehicleInfo> getObjects() {
        return objects;
    }

    public void setObjects(List<PlayerVehicleInfo> objects) {
        this.objects = objects;
    }

    public void setColorBlind(boolean isColorBlind) {
        this.isColorBlind = isColorBlind;
    }
}
