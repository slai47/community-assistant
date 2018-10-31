package com.half.wowsca.ui.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.half.wowsca.CAApp;
import com.half.wowsca.R;
import com.half.wowsca.model.Achievement;
import com.half.wowsca.model.encyclopedia.items.AchievementInfo;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

/**
 * Created by slai4 on 9/23/2015.
 */
public class AchievementsAdapter extends ArrayAdapter<Achievement> {

    private Map<String, Integer> savedAchievements;

    public AchievementsAdapter(Context context, int resource, List<Achievement> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_achievement, parent, false);
        }
        Achievement achievement = getItem(position);
        AchievementInfo info = CAApp.getInfoManager().getAchievements(getContext()).get(achievement.getName());
        ImageView iv = (ImageView) convertView.findViewById(R.id.list_achievement_icon);
        TextView tvNumber = (TextView) convertView.findViewById(R.id.list_achievement_text);
        TextView tvDiff = (TextView) convertView.findViewById(R.id.list_achievement_difference);

        if (info != null) {
            Picasso.with(getContext()).load(info.getImage()).error(R.drawable.ic_missing_image).into(iv);
        }
        if (achievement.getNumber() == 0) {
            iv.setImageAlpha(125);
            tvNumber.setText("");
        } else {
            iv.setImageAlpha(255);
            tvNumber.setText(achievement.getNumber() + "");
        }

        if(savedAchievements != null){
            Integer achi = savedAchievements.get(achievement.getName());
            if(achi != null) {
                int difference = achievement.getNumber() - achi;
                tvDiff.setText("+" + difference);
                tvDiff.setTextColor(ContextCompat.getColor(getContext(), R.color.average_up));
                if (difference > 0) {
                    tvDiff.setVisibility(View.VISIBLE);
                } else {
                    tvDiff.setVisibility(View.GONE);
                }
            } else {
                tvDiff.setVisibility(View.GONE);
            }
        } else {
            tvDiff.setVisibility(View.GONE);
        }
        convertView.setTag(achievement.getName());
        return convertView;
    }

    public void setSavedAchievements(Map<String, Integer> savedAchievements) {
        this.savedAchievements = savedAchievements;
    }
}