package com.clanassist.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.clanassist.listeners.AddRemovePlayerListener;
import com.clanassist.model.player.Player;
import com.clanassist.tools.CPManager;
import com.cp.assist.R;

import java.util.List;

/**
 * Created by Harrison on 6/3/2014.
 */
public class PlayerAdapter extends ArrayAdapter<Player> {

    public PlayerAdapter(Context context, int resource, List<Player> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        PlayerHolder holder;
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.list_player, parent, false);
            holder = new PlayerHolder();
            holder.tvName = (TextView) view.findViewById(R.id.list_player_name);
            holder.addArea = view.findViewById(R.id.list_player_button_area);
            holder.cbAdd = (CheckBox) view.findViewById(R.id.list_player_button);
            view.setTag(holder);
        } else {
            holder = (PlayerHolder) view.getTag();
        }

        Player p = getItem(position);
        holder.tvName.setText(p.getName());
        boolean checked = false;
        if (CPManager.getSavedPlayers(getContext()).get(p.getId()) != null) {
            checked = true;
        }
        holder.cbAdd.setChecked(checked);
        holder.addArea.setOnClickListener(new AddRemovePlayerListener(getContext(), p, holder.cbAdd, true));
        return view;
    }

    class PlayerHolder {
        private TextView tvName;
        private View addArea;
        private CheckBox cbAdd;
    }
}
