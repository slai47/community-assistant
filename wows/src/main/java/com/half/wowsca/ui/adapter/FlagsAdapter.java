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
import com.half.wowsca.model.encyclopedia.items.ExteriorItem;
import com.half.wowsca.model.events.FlagClickedEvent;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by slai4 on 4/25/2016.
 */
public class FlagsAdapter extends RecyclerView.Adapter<FlagsAdapter.FlagsViewHolder> {

    private List<ExteriorItem> exteriorItems;
    private Context ctx;

    public FlagsAdapter(List<ExteriorItem> skills, Context ctx) {
        this.exteriorItems = skills;
        this.ctx = ctx;
    }

    @Override
    public FlagsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_captain_skill, parent, false);
        FlagsViewHolder holder = new FlagsViewHolder(convertView);
        return holder;
    }

    @Override
    public void onBindViewHolder(FlagsViewHolder holder, int position) {
        ExteriorItem exteriorItem = exteriorItems.get(position);
        holder.id = exteriorItem.getId();
        holder.tvName.setText(exteriorItem.getName());
        Picasso.with(ctx).load(exteriorItem.getImage()).error(R.drawable.ic_missing_image).into(holder.ivIcon);
    }

    @Override
    public int getItemCount() {
        if (exteriorItems != null)
            return exteriorItems.size();
        else
            return 0;
    }

    public static class FlagsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView tvName;
        ImageView ivIcon;

        long id;

        public FlagsViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.list_captain_skill_text);
            ivIcon = (ImageView) itemView.findViewById(R.id.list_captain_skill_image);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            CAApp.getEventBus().post(new FlagClickedEvent(id));
        }
    }

}
