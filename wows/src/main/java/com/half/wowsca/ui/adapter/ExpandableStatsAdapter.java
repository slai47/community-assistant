package com.half.wowsca.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.half.wowsca.CAApp;
import com.half.wowsca.R;
import com.half.wowsca.model.enums.EncyclopediaType;
import com.half.wowsca.model.listModels.EncyclopediaChild;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by slai4 on 12/1/2015.
 */
public class ExpandableStatsAdapter extends BaseExpandableListAdapter {

    private List<String> headers;
    private Map<String, List<EncyclopediaChild>> values;
    private Context ctx;

    public ExpandableStatsAdapter(List<String> headers, Map<String, List<EncyclopediaChild>> values, Context ctx) {
        this.headers = headers;
        this.values = values;
        this.ctx = ctx;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String header = (String) getGroup(groupPosition);

        if(convertView == null){
            convertView = LayoutInflater.from(ctx).inflate(R.layout.list_group_stats, parent, false);
        }

//        ImageView img = (ImageView) convertView.findViewById(R.id.list_group_stats_img);
        TextView text = (TextView) convertView.findViewById(R.id.list_group_stats_text);
//        if(isExpanded){
//            img.setImageResource(R.drawable.expander_close_holo_dark);
//        } else {
//            img.setImageResource(R.drawable.expander_open_holo_dark);
//        }

        text.setText(header);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String header = (String) getGroup(groupPosition);
        boolean premium = false;
        if(header.equals(ctx.getString(R.string.premium_ship_stats))){
            premium = true;
        }
        EncyclopediaChild child = (EncyclopediaChild) getChild(groupPosition, childPosition);
        int layoutId = R.layout.list_child_bar;

        View view = LayoutInflater.from(ctx).inflate(layoutId, parent, false);

        TextView text = (TextView) view.findViewById(R.id.list_child_title);
        View chart = view.findViewById(R.id.list_child_graph);
        if(premium){
            int size = child.getValues().size() * 18;

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(chart.getLayoutParams());
            params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, ctx.getResources().getDisplayMetrics());
            chart.setLayoutParams(params);
        }

        createBarChart((HorizontalBarChart) chart, child);

        text.setText(child.getTitle());

        return view;
    }

    private void createBarChart(HorizontalBarChart chart, EncyclopediaChild child) {
        int textColor = CAApp.getTextColor(chart.getContext());
        boolean colorblind = CAApp.isColorblind(chart.getContext());
        int accentColor = !colorblind ?
                (CAApp.getTheme(chart.getContext()).equals("ocean") ?
                        ContextCompat.getColor(chart.getContext(), R.color.graph_line_color) : ContextCompat.getColor(chart.getContext(), R.color.top_background))
                : ContextCompat.getColor(chart.getContext(), R.color.white);
        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(false);
        chart.setPinchZoom(false);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setDrawValueAboveBar(true);
        chart.setTouchEnabled(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(textColor);
        xAxis.setDrawGridLines(true);

        YAxis yAxis = chart.getAxisRight();
        yAxis.setLabelCount(6, false);
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxis.setTextColor(textColor);
        yAxis.setEnabled(false);

        YAxis yAxis2 = chart.getAxisLeft();
        yAxis2.setLabelCount(6, false);
        yAxis2.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxis2.setTextColor(textColor);
        if(child.getType() == EncyclopediaType.LARGE_NUMBER) {
            yAxis2.setValueFormatter(new LargeValueFormatter());
        } else if(child.getType() == EncyclopediaType.PERCENT) {
            yAxis2.setValueFormatter(new MyYFormatter());
        }

        Legend l = chart.getLegend();
        l.setEnabled(false);
        List<String> xVals = new ArrayList<String>();
        List<Integer> colorList = new ArrayList<Integer>();
        for (int i = 0; i < child.getTitles().size(); i++){
            xVals.add(child.getTitles().get(i));
            String key = child.getTypes().get(i);
            if (key.equalsIgnoreCase("cruiser")) {
                colorList.add(Color.parseColor("#4CAF50"));
            } else if (key.equalsIgnoreCase("battleship")) {
                colorList.add(Color.parseColor("#F44336"));
            } else if (key.equalsIgnoreCase("aircarrier")) {
                colorList.add(Color.parseColor("#673AB7"));
            } else if (key.equalsIgnoreCase("destroyer")) {
                colorList.add(Color.parseColor("#FDD835"));
            }
        }

        List<BarEntry> yVals = new ArrayList<BarEntry>();
        for (int i = 0; i < child.getValues().size(); i++) {
            yVals.add(new BarEntry(child.getValues().get(i), i));
        }
        if(yVals.size() > 0) {
            BarDataSet set1 = new BarDataSet(yVals, "");
            set1.setColors(colorList);
            set1.setBarSpacePercent(20f);
            set1.setValueTextColor(textColor);

            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);

            BarData data = new BarData(xVals, dataSets);
            data.setValueTextSize(10f);
            data.setValueTextColor(textColor);
            if (child.getType() == EncyclopediaType.LARGE_NUMBER) {
                data.setValueFormatter(new LargeValueFormatter());
            } else if (child.getType() == EncyclopediaType.PERCENT) {
                data.setValueFormatter(new MyFormatter());
            }

            chart.setDescription("");
            chart.setData(data);
            chart.requestLayout();

            chart.animateY(1000);
        }
    }

    @Override
    public int getGroupCount() {
        try {
            return headers.size();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        try {
            return values.get(headers.get(groupPosition)).size();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public Object getGroup(int groupPosition) {
        return headers.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return values.get(headers.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }


    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    private class MyYFormatter implements YAxisValueFormatter {

        private DecimalFormat mFormat;

        public MyYFormatter() {
            this.mFormat = new DecimalFormat("##.##%");
        }

        public void change(String tag) {
            this.mFormat = new DecimalFormat("##.##%");
        }

        @Override
        public String getFormattedValue(float value, YAxis yAxis) {
            return mFormat.format(value);
        }
    }

    private class MyFormatter implements ValueFormatter {

        private DecimalFormat mFormat;

        public MyFormatter() {
            this.mFormat = new DecimalFormat("##.#%");
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return mFormat.format(value);
        }
    }

}
