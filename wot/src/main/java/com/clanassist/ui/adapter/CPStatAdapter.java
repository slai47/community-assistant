package com.clanassist.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.clanassist.model.listmodel.CPStatModel;
import com.clanassist.tools.WN8ColorManager;
import com.clanassist.ui.views.OutlinedTextView;
import com.cp.assist.R;
import com.utilities.Utils;

import java.util.List;

/**
 * Created by Harrison on 6/20/2015.
 */
public class CPStatAdapter extends ArrayAdapter<CPStatModel> {

    private boolean isColorBlind;
    private int defaultBackground;

    public CPStatAdapter(Context context, int resource, List<CPStatModel> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_cp_stat, parent, false);
        }
        CPStatModel model = getItem(position);

        convertView.setBackgroundColor(!isColorBlind ? model.getBackgroundColor() : defaultBackground);

        OutlinedTextView text = (OutlinedTextView) convertView.findViewById(R.id.list_cp_stats_text);
        OutlinedTextView subtext = (OutlinedTextView) convertView.findViewById(R.id.list_cp_stats_subtext);
        OutlinedTextView middleText = (OutlinedTextView) convertView.findViewById(R.id.list_cp_stats_mvp);

        View differenceArea = convertView.findViewById(R.id.list_cp_stats_difference);
        TextView differenceText = (TextView) convertView.findViewById(R.id.list_cp_stats_difference);

        text.setText(model.getText());
        subtext.setText(model.getSubText());
        middleText.setText(model.getMidText());
        if (TextUtils.isEmpty(model.getMidText())) {
            middleText.setVisibility(View.GONE);
        } else {
            middleText.setVisibility(View.VISIBLE);
        }
        if (Math.abs(model.getDifference()) > 0) {
            String plusText = model.getDifference() > 0 ? "+" : "";
            differenceText.setText(plusText + Utils.getOneDepthDecimalFormatter().format(model.getDifference()));

            WN8ColorManager.setDifferenceColor(differenceText, model.getDifference());
            differenceArea.setBackgroundResource(R.color.black);
            differenceArea.setVisibility(View.VISIBLE);
        } else {
            differenceArea.setVisibility(View.GONE);
        }
        return convertView;
    }

    public boolean isColorBlind() {
        return isColorBlind;
    }

    public void setIsColorBlind(boolean isColorBlind) {
        this.isColorBlind = isColorBlind;
    }

    public int getDefaultBackground() {
        return defaultBackground;
    }

    public void setDefaultBackground(int defaultBackground) {
        this.defaultBackground = defaultBackground;
    }
}
