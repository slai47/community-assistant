package com.half.wowsca.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.half.wowsca.CAApp;
import com.half.wowsca.R;
import com.half.wowsca.model.encyclopedia.items.EquipmentInfo;
import com.half.wowsca.model.events.UpgradeClickEvent;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by slai4 on 4/26/2016.
 */
public class UpgradesAdapter extends RecyclerView.Adapter<UpgradesAdapter.UpgradesHolder> {

    private List<EquipmentInfo> items;
    private Context ctx;

    public UpgradesAdapter(List<EquipmentInfo> equipmentInfos, Context ctx) {
        this.items = equipmentInfos;
        this.ctx = ctx;
    }

    @Override
    public UpgradesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_captain_skill, parent, false);
        UpgradesHolder holder = new UpgradesHolder(convertView);
        return holder;
    }

    @Override
    public void onBindViewHolder(UpgradesHolder holder, int position) {
        EquipmentInfo exteriorItem = items.get(position);
        holder.id = exteriorItem.getId();
        holder.tvName.setText(exteriorItem.getName());
        Picasso.with(ctx).load(exteriorItem.getImage()).error(R.drawable.ic_missing_image).into(holder.ivIcon);
    }

    @Override
    public int getItemCount() {
        if (items != null)
            return items.size();
        else
            return 0;
    }

    public static class UpgradesHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView tvName;
        ImageView ivIcon;

        long id;

        public UpgradesHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.list_captain_skill_text);
            ivIcon = (ImageView) itemView.findViewById(R.id.list_captain_skill_image);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            CAApp.getEventBus().post(new UpgradeClickEvent(id));
        }
    }

}
