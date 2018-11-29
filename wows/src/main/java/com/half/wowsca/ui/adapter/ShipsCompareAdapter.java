package com.half.wowsca.ui.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.half.wowsca.CAApp;
import com.half.wowsca.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by slai4 on 7/29/2017.
 */

public class ShipsCompareAdapter extends RecyclerView.Adapter<ShipsCompareAdapter.ViewHolder> {

    private List<Map<String, Float>> ships;
    private List<String> graphNames;
    private Map<String, Integer> shipColors;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_compare_ship_graph, parent, false);
        ViewHolder holder = new ViewHolder(convertView);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BarChart chart = holder.barChart;

        Map<String, Float> info = ships.get(position);

        holder.tvTitle.setText(graphNames.get(position));

        int textColor = CAApp.getTextColor(chart.getContext());

        chart.setDrawBarShadow(false);
        chart.setDrawGridBackground(false);
        chart.setDrawValueAboveBar(true);
        chart.setDoubleTapToZoomEnabled(false);

        setupXAxis(textColor, chart);

        setupYAxis(textColor, chart);

        setupYAxis2(textColor, chart);

        Legend l = chart.getLegend();
        l.setEnabled(false);

        ArrayList<String> xVals = new ArrayList<String>();
        Iterator<String> itea = info.keySet().iterator();
        while (itea.hasNext()){
            xVals.add(itea.next());
        }
        int[] colors = new int[xVals.size()];

        for(int i = 0; i < xVals.size(); i++){
            String shipName = xVals.get(i);
            colors[i] = shipColors.get(shipName);
        }

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        for (int i = 0; i < xVals.size(); i++) {
            float value = (float) info.get(xVals.get(i));
            yVals1.add(new BarEntry(value, i));
        }

        BarDataSet set1 = new BarDataSet(yVals1, "");
        set1.setBarSpacePercent(20f);
        set1.setColors(colors);
        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);
        data.setValueTextSize(10f);
        data.setValueTextColor(textColor);

        chart.setDescription("");

        chart.setData(data);
        chart.requestLayout();
    }

    private void setupYAxis2(int textColor, BarChart chart) {
        YAxis yAxis2 = chart.getAxisLeft();
        yAxis2.setLabelCount(6, true);
        yAxis2.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxis2.setTextColor(textColor);
    }

    private void setupYAxis(int textColor, BarChart chart) {
        YAxis yAxis = chart.getAxisRight();
        yAxis.setLabelCount(4, true);
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxis.setTextColor(textColor);
        yAxis.setEnabled(false);
    }

    private void setupXAxis(int textColor, BarChart chart) {
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(textColor);
        xAxis.setDrawGridLines(true);
        xAxis.setLabelsToSkip(0);
        xAxis.setLabelRotationAngle(25);
    }

    @Override
    public int getItemCount() {
        return ships.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public BarChart barChart;
        public TextView tvTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            barChart = (BarChart) itemView.findViewById(R.id.list_compare_ship_graph);
            tvTitle = (TextView) itemView.findViewById(R.id.list_compare_ship_graph_text);
        }
    }

    public void setShips(List<Map<String, Float>> ships) {
        this.ships = ships;
        notifyDataSetChanged();
    }

    public List<String> getGraphNames() {
        return graphNames;
    }

    public void setGraphNames(List<String> graphNames) {
        this.graphNames = graphNames;
    }

    public Map<String, Integer> getShipColors() {
        return shipColors;
    }

    public void setShipColors(Map<String, Integer> shipColors) {
        this.shipColors = shipColors;
    }
}