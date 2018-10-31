package com.half.wowsca.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.half.wowsca.R;
import com.half.wowsca.model.Captain;

import java.util.List;

/**
 * Created by slai4 on 10/14/2015.
 */
public class CompareAdapter extends ArrayAdapter<Captain> {

    public CompareAdapter(Context context, int resource, List<Captain> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_compare, parent, false);
        }
        Captain c = getItem(position);

        TextView text = (TextView) convertView.findViewById(R.id.list_compare_text);

        text.setText(c.getName());

        return convertView;
    }
}
