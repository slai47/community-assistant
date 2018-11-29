package com.half.wowsca.ui.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.half.wowsca.CAApp;
import com.half.wowsca.R;
import com.half.wowsca.model.encyclopedia.items.CaptainSkill;
import com.half.wowsca.model.events.CaptainSkillClickedEvent;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by slai4 on 4/25/2016.
 */
public class CaptainSkillsAdapter extends RecyclerView.Adapter<CaptainSkillsAdapter.SkillsViewHolder> {

    private List<CaptainSkill> skills;
    private Context ctx;

    public CaptainSkillsAdapter(List<CaptainSkill> skills, Context ctx) {
        this.skills = skills;
        this.ctx = ctx;
    }

    @Override
    public SkillsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_captain_skill, parent, false);
        SkillsViewHolder holder = new SkillsViewHolder(convertView);
        return holder;
    }

    @Override
    public void onBindViewHolder(SkillsViewHolder holder, int position) {
        CaptainSkill skill = skills.get(position);
        holder.id = skill.getId();
        holder.tvName.setText(skill.getName());
        Picasso.get().load(skill.getImage()).error(R.drawable.ic_missing_image).into(holder.ivIcon);
    }

    @Override
    public int getItemCount() {
        if (skills != null)
            return skills.size();
        else
            return 0;
    }

    public static class SkillsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView tvName;
        ImageView ivIcon;

        long id;

        public SkillsViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.list_captain_skill_text);
            ivIcon = (ImageView) itemView.findViewById(R.id.list_captain_skill_image);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            CAApp.getEventBus().post(new CaptainSkillClickedEvent(id));
        }
    }

}
