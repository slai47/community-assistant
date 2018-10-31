package com.clanassist.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.clanassist.listeners.AddRemoveClanListener;
import com.clanassist.model.clan.Clan;
import com.clanassist.tools.CPManager;
import com.clanassist.ui.UIUtils;
import com.cp.assist.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Obsidian47 on 3/6/14.
 */
public class ClanAdapter extends ArrayAdapter<Clan> {

    public ClanAdapter(Context context, int resource, List<Clan> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null)
            view = LayoutInflater.from(getContext()).inflate(R.layout.list_clan, parent, false);
        UIUtils.setUpCard(view, 0);
        Clan clan = getItem(position);

        TextView name = (TextView) view.findViewById(R.id.list_clan_name);
        TextView abbr = (TextView) view.findViewById(R.id.list_clan_abbr);
//        TextView cmdName = (TextView) view.findViewById(R.id.list_clan_cmd_name);
        ImageView image = (ImageView) view.findViewById(R.id.list_clan_image);
        TextView memberCount = (TextView) view.findViewById(R.id.list_clan_members);
        View addArea = view.findViewById(R.id.list_clan_button_area);
        CheckBox add = (CheckBox) view.findViewById(R.id.list_clan_button);

        name.setText(clan.getName());
        abbr.setText("[" + clan.getAbbreviation() + "]");
        int color = Color.parseColor(clan.getColor());
        abbr.setTextColor(color);
//        cmdName.setText(clan.getOwnerName());
        memberCount.setText(clan.getMembers_count() + "");
        boolean checked = false;
        if (CPManager.getSavedClans(getContext()).get(clan.getClanId()) != null)
            checked = true;
        add.setChecked(checked);
        addArea.setOnClickListener(new AddRemoveClanListener(getContext(), clan, add, true));
//        add.setOnCheckedChangeListener(new AddRemoveClanListener(getContext(), clan, true));
        Picasso.with(getContext()).load(clan.getEmblem().getBw_tank()).into(image);
        view.setTag(clan);
        return view;
    }
}
