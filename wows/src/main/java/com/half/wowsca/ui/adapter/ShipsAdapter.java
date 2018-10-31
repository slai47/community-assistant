package com.half.wowsca.ui.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.half.wowsca.CAApp;
import com.half.wowsca.R;
import com.half.wowsca.model.Ship;
import com.half.wowsca.model.ShipCompare;
import com.half.wowsca.model.encyclopedia.holders.ShipsHolder;
import com.half.wowsca.model.encyclopedia.items.ShipInfo;
import com.half.wowsca.model.events.ShipClickedEvent;
import com.half.wowsca.model.events.SortingDoneEvent;
import com.half.wowsca.model.saveobjects.SavedShips;
import com.half.wowsca.ui.UIUtils;
import com.utilities.Utils;
import com.utilities.logging.Dlog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Created by slai4 on 10/1/2015.
 */
public class ShipsAdapter extends RecyclerView.Adapter<ShipsAdapter.ShipViewHolder> {

    private List<Ship> ships;

    private ArrayList<Ship> backupShips;

    private Context ctx;

    private SavedShips savedShips;

    public ShipsAdapter(List<Ship> ships, Context ctx) {
        this.ships = ships;
        backupShips = (ArrayList<Ship>) ships;
        this.ctx = ctx;
    }

    public static class ShipViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvName;
        TextView nationTier;
        TextView tvBattles;
        TextView tvWinRate;
        TextView tvAverageExp;
        TextView tvAverageKills;
        TextView tvAverageDamage;
        TextView tvCARating;

        View aCARating;

        ImageView icon;

        long ship;
        int pos;

        View view;

        public ShipViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            this.tvName = (TextView) itemView.findViewById(R.id.snippet_ship_name);
            this.nationTier = (TextView) itemView.findViewById(R.id.snippet_ship_nation_tier);
            this.tvBattles = (TextView) itemView.findViewById(R.id.snippet_ship_battles);
            this.tvWinRate = (TextView) itemView.findViewById(R.id.snippet_ship_win_rate);
            this.tvAverageExp = (TextView) itemView.findViewById(R.id.snippet_ship_avg_exp);
            this.tvAverageKills = (TextView) itemView.findViewById(R.id.snippet_ship_avg_kills);
            this.tvAverageDamage = (TextView) itemView.findViewById(R.id.snippet_ship_avg_damage);
            this.icon = (ImageView) itemView.findViewById(R.id.snippet_ship_icon);
            this.tvCARating = (TextView) itemView.findViewById(R.id.snippet_ship_ca_rating);

            this.aCARating = itemView.findViewById(R.id.snippet_ship_ca_rating_area);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            CAApp.getEventBus().post(new ShipClickedEvent(ship));
            CAApp.setLastShipPos(pos);
        }
    }

    @Override
    public ShipViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_ship, parent, false);
        ShipViewHolder holder = new ShipViewHolder(convertView);
        return holder;
    }

    @Override
    public void onBindViewHolder(ShipViewHolder holder, int position) {
        Ship s = ships.get(position);
        ShipInfo info = CAApp.getInfoManager().getShipInfo(ctx).get(s.getShipId());
        float battles = s.getBattles();
        UIUtils.setUpCard(holder.view, R.id.list_ship_area);
        String nameText = ctx.getString(R.string.unknown);
        holder.ship = s.getShipId();
        holder.pos = position;

        if (info != null) {
            nameText = info.getName();
            UIUtils.setShipImage(holder.icon, info);
            String nation = UIUtils.getNationText(ctx, info.getNation());

            holder.nationTier.setText(nation + " - " + info.getTier());
            if (info.isPremium()) {
                holder.tvName.setTextColor(ContextCompat.getColor(ctx, R.color.premium_shade));
            } else {
                holder.tvName.setTextColor(ContextCompat.getColor(ctx, R.color.white));
            }
        }
        holder.aCARating.setVisibility(View.VISIBLE);
        holder.tvName.setText(nameText);
        if (battles > 0) {
            holder.tvBattles.setText("" + ((int) battles));
            float avgExp = s.getTotalXP() / battles;
            holder.tvAverageExp.setText((int) avgExp + "");
            float wr = (s.getWins() / battles) * 100.0f;
            holder.tvWinRate.setText(Utils.getDefaultDecimalFormatter().format(wr) + "%");
            int kdBattles = (int) battles;
            if (kdBattles != s.getSurvivedBattles()) {
                kdBattles = (int) (battles - s.getSurvivedBattles());
            }
            float kd = (float) s.getFrags() / kdBattles;
            holder.tvAverageKills.setText("" + Utils.getDefaultDecimalFormatter().format(kd));
            int avgDamage = (int) (s.getTotalDamage() / battles);
            holder.tvAverageDamage.setText("" + avgDamage);
            holder.tvCARating.setText("" + Math.round(s.getCARating()));
        } else {
            holder.tvBattles.setText("0");
            holder.tvAverageExp.setText("0");
            holder.tvWinRate.setText("0");
            holder.tvAverageKills.setText("0");
            holder.tvAverageDamage.setText("0");
        }
    }

    @Override
    public int getItemCount() {
        if (ships != null)
            return ships.size();
        else
            return 0;
    }

    public void sort(String sortStr) {
        Dlog.wtf("ShipsAdapter", "sort = " + sortStr);
        String[] sortTypes = ctx.getResources().getStringArray(R.array.ship_sorting);
        int i;
        for (i = 0; i < sortTypes.length; i++) {
            if (sortTypes[i].equals(sortStr)) {
                break;
            }
        }
        ShipCompare compare = new ShipCompare();
//        StringBuilder sb = new StringBuilder();
//        for (Ship ship : ships) {
//            sb.append("[" + ship.getShipId()+ "],");
//        }
//        Dlog.d("Sort1", sb.toString());

        compare.setShipsHolder(CAApp.getInfoManager().getShipInfo(ctx));
        try {
            switch (i) {
                case 0: //Battles
                    Collections.sort(ships, compare.battlesComparator);
                    break;
                case 1: // Names
                    Collections.sort(ships, compare.namesComparator);
                    break;
                case 2: // avg exp
                    Collections.sort(ships, compare.averageExpComparator);
                    break;
                case 3:// avg damage
                    Collections.sort(ships, compare.averageDamageComparator);
                    break;
                case 4: // win rate
                    Collections.sort(ships, compare.winRateComparator);
                    break;
                case 5: // Kills / death
                    Collections.sort(ships, compare.killsDeathComparator);
                    break;
                case 6:// total damage
                    Collections.sort(ships, compare.damageComparator);
                    break;
                case 7: // total Kills
                    Collections.sort(ships, compare.killsComparator);
                    break;
                case 8: // Plane Kills
                    Collections.sort(ships, compare.planeKillsComparator);
                    break;
                case 9: // Tier 10-1
                    Collections.sort(ships, compare.tierDescendingComparator);
                    break;
                case 10:// Tier 1-10
                    Collections.sort(ships, compare.tierAscendingComparator);
                    break;
                case  11: // ca rating
                    Collections.sort(ships, compare.CARatingComparator);
                    break;
                case 12: // main battery
                    Collections.sort(ships, compare.accuracyComparator);
                    break;
                case 13: // torp acc
                    Collections.sort(ships, compare.accuractTorpsComparator);
                    break;
            }
            CAApp.getEventBus().post(new SortingDoneEvent());

            notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void filter(CharSequence s) {
        if (!TextUtils.isEmpty(s)) {
            List<Ship> filteredList = new ArrayList<Ship>();
            Locale local = Locale.getDefault();
            String contraint = s.toString().toLowerCase(local);
            ShipsHolder shipsHolder = CAApp.getInfoManager().getShipInfo(ctx);
            Integer tierContraints = null;
            try {
                tierContraints = Integer.parseInt(contraint);
            } catch (NumberFormatException e) {
            }
            if (tierContraints != null) {
                if (tierContraints > 10 || tierContraints < 1) {
                    tierContraints = null;
                }
            }
            for (Ship item : backupShips) {
                ShipInfo info = shipsHolder.get(item.getShipId());
                boolean accepted = false;
                if (info != null) {
                    if (tierContraints == null) {
                        accepted = info.getName().toLowerCase(local).contains(contraint);
                    } else {
                        accepted = info.getTier() == tierContraints;
                    }
                }
                if (accepted) {
                    filteredList.add(item);
                }
            }
            this.ships = filteredList;
            notifyDataSetChanged();
        } else {
            ships = backupShips;
            notifyDataSetChanged();
        }
    }

    public SavedShips getSavedShips() {
        return savedShips;
    }

    public void setSavedShips(SavedShips savedShips) {
        this.savedShips = savedShips;
    }

}
