package com.clanassist.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.clanassist.CAApp;
import com.clanassist.model.infoobj.Achievement;
import com.clanassist.model.listmodel.AchievementInfo;
import com.cp.assist.R;
import com.squareup.picasso.Picasso;
import com.utilities.logging.Dlog;

import java.util.List;

/**
 * Created by slai4 on 9/23/2015.
 */
public class AchievementsAdapter extends ArrayAdapter<AchievementInfo> {

    public AchievementsAdapter(Context context, int resource, List<AchievementInfo> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_achievement, parent, false);
        }
        AchievementInfo key = getItem(position);
        Achievement info = CAApp.getInfoManager().getAchievements(getContext()).getAchievement(key.getName());
//        Dlog.d("AchievementAdapter", "key = " + key.getName() + " info = " + info);
        ImageView iv = (ImageView) convertView.findViewById(R.id.list_achievement_icon);
        TextView tvNumber = (TextView) convertView.findViewById(R.id.list_achievement_text);
        TextView tvDiff = (TextView) convertView.findViewById(R.id.list_achievement_difference);

        if (info != null) {
            Picasso.with(getContext()).load(info.getBigImage()).error(R.drawable.ic_missing_image).into(iv);
        }
        if (key.getNumber() == 0) {
            iv.setImageAlpha(125);
            tvNumber.setText("");
        } else {
            iv.setImageAlpha(255);
            tvNumber.setText(key.getNumber() + "");
        }

        tvDiff.setText("");
        convertView.setTag(key.getName());
        return convertView;
    }
}