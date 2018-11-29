package com.half.wowsca.ui.adapter;

import android.content.Context;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.half.wowsca.R;
import com.half.wowsca.model.enums.AverageType;
import com.half.wowsca.model.listModels.ListAverages;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by slai4 on 11/8/2015.
 */
public class AveragesAdapter extends ArrayAdapter<ListAverages> {

    private List<ListAverages> objects;

    public AveragesAdapter(Context context, int resource, List<ListAverages> objects) {
        super(context, resource, objects);
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_averages, parent, false);
        }
        TextView title = (TextView) convertView.findViewById(R.id.list_average_title);
        TextView text = (TextView) convertView.findViewById(R.id.list_average_avg);
        TextView midText = (TextView) convertView.findViewById(R.id.list_average_mid_text);

        ListAverages avg = getItem(position);

        title.setText(avg.getTitle());
        DecimalFormat formatter;

        if(avg.getType() == AverageType.LARGE_NUMBER){
            formatter = new DecimalFormat("#");
        } else if(avg.getType() == AverageType.PERCENT) {
            formatter = new DecimalFormat("#.#%");
        } else {
            formatter = new DecimalFormat("#.#");
        }
        float number = avg.getAverage() - avg.getExpected();

        midText.setText(formatter.format(avg.getAverage()) + "/" + formatter.format(avg.getExpected()));


        StringBuilder sb = new StringBuilder();
        if(number > 0){
            text.setTextColor(ContextCompat.getColor(getContext(), R.color.average_up));
            sb.append("+");
        } else if(number < 0){
            text.setTextColor(ContextCompat.getColor(getContext(), R.color.average_down));
        } else {
            text.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
            sb.append("");
        }
        sb.append(formatter.format(number));
        text.setText(sb.toString());

        return convertView;
    }

    @Override
    public ListAverages getItem(int position) {
        try {
            return objects.get(position);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public int getCount() {
        try {
            return objects.size();
        } catch (Exception e) {
            return 0;
        }
    }

    public void setObjects(List<ListAverages> objects) {
        this.objects = objects;
        notifyDataSetChanged();
    }
}
