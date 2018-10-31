package com.half.wowsca.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.half.wowsca.R;
import com.half.wowsca.listener.AddRemoveListener;
import com.half.wowsca.managers.CaptainManager;
import com.half.wowsca.model.Captain;

import java.util.List;

/**
 * Created by slai4 on 9/19/2015.
 */
public class SearchAdapter extends ArrayAdapter<Captain> {

    public SearchAdapter(Context context, int resource, List<Captain> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_search, parent, false);
        }
        Captain c = getItem(position);

        TextView name = (TextView) convertView.findViewById(R.id.list_search_name);
        View view = convertView.findViewById(R.id.list_search_checkbox_area);
        CheckBox box = (CheckBox) convertView.findViewById(R.id.list_search_checkbox);

        name.setText(c.getName());

        if (CaptainManager.getCaptains(getContext()).get(CaptainManager.createCapIdStr(c.getServer(), c.getId())) != null) {
            box.setChecked(true);
        } else {
            box.setChecked(false);
        }
        view.setOnClickListener(new AddRemoveListener(c, getContext(), box));
        return convertView;
    }
}
