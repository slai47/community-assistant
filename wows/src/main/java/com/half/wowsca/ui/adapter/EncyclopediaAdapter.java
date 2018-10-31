package com.half.wowsca.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;



import com.half.wowsca.CAApp;
import com.half.wowsca.R;
import com.half.wowsca.managers.CompareManager;
import com.half.wowsca.model.encyclopedia.items.ShipInfo;
import com.half.wowsca.model.events.ShipCompareEvent;
import com.half.wowsca.ui.UIUtils;
import com.half.wowsca.ui.encyclopedia.ShipProfileActivity;
import com.squareup.picasso.Picasso;
import com.utilities.logging.Dlog;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by slai4 on 10/31/2015.
 */
public class EncyclopediaAdapter extends RecyclerView.Adapter<EncyclopediaAdapter.ShipViewHolder> {

    public static final int EMPTY_FILTER = -1;
    private List<ShipInfo> ships;

    private ArrayList<ShipInfo> backupShips;

    private Context ctx;


    public EncyclopediaAdapter(List<ShipInfo> ships, Context context) {
        this.ships = ships;
        backupShips = (ArrayList<ShipInfo>) ships;
        this.ctx = context;
    }

    public static class ShipViewHolder extends RecyclerView.ViewHolder {

        ImageView img;
        TextView name;
        View area;

        long shipId;
        String shipName;

        public ShipViewHolder(View itemView) {
            super(itemView);
            this.img = (ImageView) itemView.findViewById(R.id.list_encyclopedia_ship_image);
            this.name = (TextView) itemView.findViewById(R.id.list_encyclopedia_ship_name);
            this.area = itemView.findViewById(R.id.list_encyclopedia_area);
            area.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //send to encyclopedia page
                    Intent i = new Intent(img.getContext(), ShipProfileActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra(ShipProfileActivity.SHIP_ID, shipId);
                    ShipProfileActivity.MODULE_LIST = null;
                    img.getContext().startActivity(i);
                }
            });
            area.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //go to activity
                    if(!CompareManager.getSHIPS().contains(shipId))
                        CompareManager.addShipID(shipId);
                    else
                        CompareManager.removeShipID(shipId);
                    CAApp.getEventBus().post(new ShipCompareEvent(shipId));
                    return true;
                }
            });
        }
    }

    @Override
    public EncyclopediaAdapter.ShipViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_encyclopedia_ship, parent, false);
        UIUtils.setUpCard(convertView, 0);
        ShipViewHolder holder = new ShipViewHolder(convertView);
        return holder;
    }

    @Override
    public void onBindViewHolder(ShipViewHolder holder, int position) {
        ShipInfo info = ships.get(position);
        holder.shipId = info.getShipId();

        Picasso.with(ctx).load(info.getImage()).error(R.drawable.ic_missing_image).into(holder.img);
//        String nation = info.getNation();
//        if (nation.equals("ussr")) { // TODO translations
//            nation = "Russia";
//        } else if (nation.equals("germany")) {
//            nation = "Germany";
//        } else if (nation.equals("usa")) {
//            nation = "USA";
//        } else if (nation.equals("poland")) {
//            nation = "Poland";
//        } else if (nation.equals("japan")) {
//            nation = "Japan";
//        } else if (nation.equals("uk")) {
//            nation = "UK";
//        }
        if(CompareManager.getSHIPS().contains(info.getShipId())){
            holder.area.setBackgroundResource(R.drawable.compare_top_grid);
        } else {
            TypedValue outValue = new TypedValue();
            ctx.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
            holder.area.setBackgroundResource(outValue.resourceId);
        }
        holder.name.setText(info.getName());
        holder.shipName = info.getName();
    }

    @Override
    public int getItemCount() {
        if (ships != null)
            return ships.size();
        else
            return 0;
    }

    public void filter(CharSequence s, int nation, int tier) {
        Dlog.d("Filter", "s = " + s + " nation = " + nation + " tier = " + tier);
        int numberOfChecks = 0;
        if (!TextUtils.isEmpty(s))
            numberOfChecks++;

        if (nation >= 0)
            numberOfChecks++;

        if (tier >= 0)
            numberOfChecks++;
        Dlog.d("Filter", "checks = " + numberOfChecks);
        if (numberOfChecks > 0) {
            List<ShipInfo> filteredList = new ArrayList<ShipInfo>();
            Locale local = Locale.getDefault();
            String contraint = s.toString().toLowerCase(local);
            String[] nationList = {"usa", "uk", "ussr", "japan", "germany", "pan_asia", "poland", "france", "commonwealth"}; // update this when updating R.id.search_nation
            String nationStr = null;
            if (nation >= 0)
                nationStr = nationList[nation];
            // push tier up by one because of the position being off by one
            if (tier >= 0)
                tier = tier + 1;
            for (ShipInfo item : backupShips) {
                boolean accepted = false;
                accepted = item.getName().toLowerCase(local).contains(contraint)
                        && (nation == EMPTY_FILTER || item.getNation().toLowerCase(local).contains(nationStr))
                        && (tier == EMPTY_FILTER || item.getTier() == tier);

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
}