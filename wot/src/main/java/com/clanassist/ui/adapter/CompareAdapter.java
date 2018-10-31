package com.clanassist.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.clanassist.CAApp;
import com.clanassist.model.player.Player;
import com.cp.assist.R;

import java.util.List;

/**
 * Created by slai4 on 10/14/2015.
 */
public class CompareAdapter extends ArrayAdapter<Player> {

    public CompareAdapter(Context context, int resource, List<Player> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_compare, parent, false);
        }
        Player c = getItem(position);

        TextView text = (TextView) convertView.findViewById(R.id.list_compare_text);
        boolean isLightTheme = CAApp.isLightTheme(getContext());

        text.setTextColor(isLightTheme ? getContext().getResources().getColor(R.color.black) : getContext().getResources().getColor(R.color.white));
        text.setText(c.getName());

        return convertView;
    }
}
