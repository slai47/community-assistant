package com.clanassist.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.clanassist.model.encyclopedia.Map;
import com.cp.assist.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Harrison on 4/4/2015.
 */
public class MapAdapter extends ArrayAdapter<Map> {


    private List<Map> objects;
    private ArrayList<Map> backupObjects;
    private MapSearchAdapterFilter filter;

    public MapAdapter(Context context, int resource, List<Map> objects) {
        super(context, resource, objects);
        this.objects = objects;
        backupObjects = (ArrayList<Map>) objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_map, parent, false);
        }
        Map item = objects.get(position);

        ImageView background = (ImageView) convertView.findViewById(R.id.list_map_image);
        TextView mapName = (TextView) convertView.findViewById(R.id.list_map_map_name);
        TextView battleTime = (TextView) convertView.findViewById(R.id.list_map_battle_time);
        TextView engagement = (TextView) convertView.findViewById(R.id.list_map_engagement_type);
        ImageView action = (ImageView) convertView.findViewById(R.id.list_map_action);
        TextView provinceName = (TextView) convertView.findViewById(R.id.list_map_province_name);
        TextView tvMap = (TextView) convertView.findViewById(R.id.list_map_map_type);

        action.setVisibility(View.GONE);
        engagement.setVisibility(View.GONE);
        battleTime.setVisibility(View.GONE);
        provinceName.setVisibility(View.GONE);
        tvMap.setVisibility(View.GONE);

        mapName.setText(item.getName_i18n());

        loadImage(background, item.getScreenUrl());

        convertView.setTag(item);
        return convertView;
    }

    private void loadImage(ImageView background, String url) {
        Picasso.with(getContext()).load(url).error(R.drawable.ic_missing_image).noFade().into(background);
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new MapSearchAdapterFilter();
        }
        return filter;
    }

    public void restoreAllCards() {
        objects = (ArrayList<Map>) backupObjects.clone();
        notifyDataSetChanged();
    }

    private class MapSearchAdapterFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint == null) {
                results.values = backupObjects;
                results.count = backupObjects.size();
            } else {
                List<Map> filteredList = new ArrayList<Map>();
                Locale local = Locale.getDefault();
                String constraintVar = constraint.toString().toLowerCase(local);
                for (Map item : backupObjects) {
                    String lower = item.getName_i18n().toLowerCase(local);
                    if (lower.contains(constraintVar)) {
                        filteredList.add(item);
                    }
                }
                results.values = filteredList;
                results.count = filteredList.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            if (results.count == 0)
                notifyDataSetInvalidated();
            else {
                objects = (ArrayList<Map>) results.values;
                notifyDataSetChanged();
            }
        }
    }

    @Override
    public int getCount() {
        return objects.size();
    }

}
