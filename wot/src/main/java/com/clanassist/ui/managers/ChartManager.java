package com.clanassist.ui.managers;

import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.widget.LinearLayout;

import com.clanassist.SVault;
import com.clanassist.model.enums.ChartType;
import com.clanassist.tools.WN8ColorManager;
import com.cp.assist.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.utilities.preferences.Prefs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Harrison on 6/28/2015.
 */
public class ChartManager {

    public static Map<String, Double> convertDoubleMap(Map<Integer, Double> map) {
        Map<String, Double> hash = new HashMap<String, Double>();
        Iterator<Integer> itea = map.keySet().iterator();
        while (itea.hasNext()) {
            int key = itea.next();
            double value = map.get(key);
            hash.put(key + "", value);
        }
        return hash;
    }

    public static Map<Integer, Double> convertMap(Map<Integer, Integer> map) {
        Map<Integer, Double> hash = new HashMap<Integer, Double>();
        Iterator<Integer> itea = map.keySet().iterator();
        while (itea.hasNext()) {
            int key = itea.next();
            double value = map.get(key);
            hash.put(key, value);
        }
        return hash;
    }

    public static Map<String, Double> convertStrMap(Map<String, Integer> map) {
        Map<String, Double> hash = new HashMap<String, Double>();
        Iterator<String> itea = map.keySet().iterator();
        while (itea.hasNext()) {
            String key = itea.next();
            double value = map.get(key);
            hash.put(key, value);
        }
        return hash;
    }


    public static void buildBarChart(BarChart chart, Map<Integer, Double> chartData, boolean isLightTheme, int height, ChartType type, boolean isWN8, boolean useFormatter) {
        int textColor = !isLightTheme ? chart.getResources().getColor(R.color.material_text_primary) : chart.getResources().getColor(R.color.material_light_text_secondary);
        int accentColor = chart.getResources().getColor(R.color.material_accent);
        boolean colorblind = new Prefs(chart.getContext()).getBoolean(SVault.PREF_COLORBLIND, false);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) chart.getLayoutParams();
        params.height = height;

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
        yAxis2.setValueFormatter(new LargeValueFormatter());

        Legend l = chart.getLegend();
        l.setEnabled(false);

        ArrayList<String> xVals = new ArrayList<String>();
        Iterator<Integer> itea = chartData.keySet().iterator();
        while (itea.hasNext()) {
            String key = itea.next() + "";
            xVals.add(key);
        }

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        List<Integer> colorList = new ArrayList<Integer>();
        for (int i = 0; i < xVals.size(); i++) {
            double dValue = chartData.get(Integer.parseInt(xVals.get(i)));
            float value = (float) dValue;
            if (isWN8) {
                colorList.add(WN8ColorManager.getBackgroundColor(chart.getContext(), (int) value));
            }
            yVals1.add(new BarEntry(value, i));
        }

        cleanUpTitles(type, xVals);

        BarDataSet set1 = new BarDataSet(yVals1, "");
        set1.setBarSpacePercent(20f);
        if(!colorblind)
            if (colorList.size() > 0)
                set1.setColors(colorList);
            else
                set1.setColor(accentColor);
        else{
            Resources ctx = chart.getContext().getResources();
            set1.setColor(isLightTheme ? ctx.getColor(R.color.material_primary) : ctx.getColor(R.color.material_light_window_background));
        }
        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);
        data.setValueTextSize(10f);
        data.setValueTextColor(textColor);
        if (useFormatter)
            data.setValueFormatter(new LargeValueFormatter());

        chart.setDescription("");

        chart.setData(data);
        chart.requestLayout();
    }


    public static void buildBarChart(BarChart chart, List<Integer> xList, List<Double> numbers, boolean isLightTheme, int height, ChartType type, boolean isWN8, boolean useFormatter) {
        int textColor = !isLightTheme ? chart.getResources().getColor(R.color.material_text_primary) : chart.getResources().getColor(R.color.material_light_text_secondary);
        int accentColor = chart.getResources().getColor(R.color.material_accent);
        boolean colorblind = new Prefs(chart.getContext()).getBoolean(SVault.PREF_COLORBLIND, false);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) chart.getLayoutParams();
        params.height = height;

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
        yAxis2.setValueFormatter(new LargeValueFormatter());

        Legend l = chart.getLegend();
        l.setEnabled(false);

        List<String> xVals = new ArrayList<String>();
        for (Integer tier : xList)
            xVals.add(tier + "");

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        List<Integer> colorList = new ArrayList<Integer>();
        for (int i = 0; i < numbers.size(); i++) {
            double dValue = numbers.get(i);
            float value = (float) dValue;
            if (isWN8) {
                colorList.add(WN8ColorManager.getBackgroundColor(chart.getContext(), (int) value));
            }
            yVals1.add(new BarEntry(value, i));
        }

        cleanUpTitles(type, xVals);

        BarDataSet set1 = new BarDataSet(yVals1, "");
        set1.setBarSpacePercent(20f);
        if(!colorblind)
            if (colorList.size() > 0)
                set1.setColors(colorList);
            else
                set1.setColor(accentColor);
        else{
            Resources ctx = chart.getContext().getResources();
            set1.setColor(isLightTheme ? ctx.getColor(R.color.material_primary) : ctx.getColor(R.color.material_light_window_background));
        }

        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);
        data.setValueTextSize(10f);
        data.setValueTextColor(textColor);
        if (useFormatter)
            data.setValueFormatter(new LargeValueFormatter());

        chart.setDescription("");

        chart.setData(data);
        chart.requestLayout();
    }


    public static void buildHorizontalBarChart(HorizontalBarChart chart, Map<String, Double> chartData, boolean isLightTheme, int height, ChartType type, boolean isWN8, boolean useFormatter) {
        int textColor = !isLightTheme ? chart.getResources().getColor(R.color.material_text_primary) : chart.getResources().getColor(R.color.material_light_text_secondary);
        int accentColor = chart.getResources().getColor(R.color.material_accent);
        boolean colorblind = new Prefs(chart.getContext()).getBoolean(SVault.PREF_COLORBLIND, false);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) chart.getLayoutParams();
        params.height = height;

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
        if(!isWN8)
            yAxis.setLabelCount(6, false);
        else
            yAxis.setLabelCount(4, false);
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxis.setTextColor(textColor);
        yAxis.setEnabled(false);

        YAxis yAxis2 = chart.getAxisLeft();
        if(!isWN8)
            yAxis.setLabelCount(6, false);
        else
            yAxis.setLabelCount(4, false);
        yAxis2.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxis2.setTextColor(textColor);
        yAxis2.setValueFormatter(new LargeValueFormatter());

        Legend l = chart.getLegend();
        l.setEnabled(false);

        ArrayList<String> xVals = new ArrayList<String>();
        Iterator<String> itea = chartData.keySet().iterator();
        while (itea.hasNext()) {
            String key = itea.next();
            xVals.add(key);
        }

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        List<Integer> colorList = new ArrayList<Integer>();
        for (int i = 0; i < xVals.size(); i++) {
            double dValue = chartData.get(xVals.get(i));
            float value = (float) dValue;
            if (isWN8) {
                colorList.add(WN8ColorManager.getBackgroundColor(chart.getContext(), (int) value));
            }
            yVals1.add(new BarEntry(value, i));
        }

        cleanUpTitles(type, xVals);

        BarDataSet set1 = new BarDataSet(yVals1, "");
        set1.setBarSpacePercent(20f);
        if(!colorblind)
            if (isWN8)
                set1.setColors(colorList);
            else if (type == ChartType.NATION) {
                set1.setColors(ColorTemplate.COLORFUL_COLORS);
            } else
                set1.setColor(accentColor);
        else{
            Resources ctx = chart.getContext().getResources();
            set1.setColor(isLightTheme ? ctx.getColor(R.color.material_primary) : ctx.getColor(R.color.material_light_window_background));
        }

        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);
        data.setValueTextSize(10f);
        data.setValueTextColor(textColor);
        if (useFormatter)
            data.setValueFormatter(new LargeValueFormatter());

        chart.setDescription("");

        chart.setData(data);
        chart.requestLayout();
    }

    public static void buildHorizontalBarChart(HorizontalBarChart chart, List<String> names, List<Double> numbers, boolean isLightTheme, int height, ChartType type, boolean isWN8, boolean isGames, boolean useFormatter) {
        int textColor = !isLightTheme ? chart.getResources().getColor(R.color.material_text_primary) : chart.getResources().getColor(R.color.material_light_text_secondary);
        int accentColor = chart.getResources().getColor(R.color.material_accent);
        boolean colorblind = new Prefs(chart.getContext()).getBoolean(SVault.PREF_COLORBLIND, false);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) chart.getLayoutParams();
        params.height = height;

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
        if(!isWN8)
            yAxis.setLabelCount(6, false);
        else
            yAxis.setLabelCount(4, false);
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxis.setTextColor(textColor);
        yAxis.setEnabled(false);

        YAxis yAxis2 = chart.getAxisLeft();
        if(!isWN8)
            yAxis2.setLabelCount(6, false);
        else
            yAxis2.setLabelCount(4, false);
        yAxis2.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxis2.setTextColor(textColor);

        Legend l = chart.getLegend();
        l.setEnabled(false);

        List<String> xVals = names;

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        List<Integer> colorList = new ArrayList<Integer>();
        for (int i = 0; i < numbers.size(); i++) {
            double dValue = numbers.get(i);
            float value = (float) dValue;
            if (isWN8) {
                colorList.add(WN8ColorManager.getBackgroundColor(chart.getContext(), (int) value));
            } else if (isGames) {
                colorList.add(WN8ColorManager.getBattlesColor(chart.getContext(), (int) value));
            }
            yVals1.add(new BarEntry(value, i));
        }

        cleanUpTitles(type, xVals);

        BarDataSet set1 = new BarDataSet(yVals1, "");
        set1.setBarSpacePercent(20f);
        if(!colorblind)
            if (isWN8 || isGames)
                set1.setColors(colorList);
            else if (type == ChartType.NATION) {
                set1.setColors(ColorTemplate.COLORFUL_COLORS);
            } else
                set1.setColor(accentColor);
        else{
            Resources ctx = chart.getContext().getResources();
            set1.setColor(isLightTheme ? ctx.getColor(R.color.material_primary) : ctx.getColor(R.color.material_light_window_background));
        }

        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);
        data.setValueTextSize(10f);
        data.setValueTextColor(textColor);
        if (useFormatter)
            data.setValueFormatter(new LargeValueFormatter());

        chart.setDescription("");

        chart.setData(data);
        chart.requestLayout();
    }


    public static void buildPieChart(PieChart chart, Map<String, Double> chartData, boolean isLightTheme, int height, ChartType type, boolean isWN8, boolean useFormatter) {
        int textColor = !isLightTheme ? chart.getResources().getColor(R.color.material_text_secondary) : chart.getResources().getColor(R.color.material_light_text_secondary);
        boolean colorblind = new Prefs(chart.getContext()).getBoolean(SVault.PREF_COLORBLIND, false);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) chart.getLayoutParams();
        params.height = height;

        chart.setRotationEnabled(false);

        chart.setDrawHoleEnabled(true);
        chart.setHoleColorTransparent(true);
        chart.setTransparentCircleRadius(50);
        chart.setHoleRadius(50);

        if (isWN8 && type == ChartType.CLASS)
            chart.setDrawSliceText(true);
        else
            chart.setDrawSliceText(false);


        Legend l = chart.getLegend();
        l.setTextColor(textColor);
        l.setPosition(Legend.LegendPosition.LEFT_OF_CHART);
        l.setForm(Legend.LegendForm.CIRCLE);

        ArrayList<String> xVals = new ArrayList<String>();
        Iterator<String> itea = chartData.keySet().iterator();
        while (itea.hasNext()) {
            String key = itea.next();
            xVals.add(key);
        }

        ArrayList<Entry> yVals1 = new ArrayList<Entry>();
        List<Integer> colorList = new ArrayList<Integer>();
        for (int i = 0; i < xVals.size(); i++) {
            double dValue = chartData.get(xVals.get(i));
            float value = (float) dValue;
            if (isWN8) {
                colorList.add(WN8ColorManager.getBackgroundColor(chart.getContext(), (int) value));
            }
            yVals1.add(new Entry(value, i));
        }
        cleanUpTitles(type, xVals);

        PieDataSet dataSet = new PieDataSet(yVals1, "");

        ArrayList<PieDataSet> dataSets = new ArrayList<PieDataSet>();
        dataSets.add(dataSet);

        if(!colorblind) {
            if (isWN8)
                dataSet.setColors(colorList);
            else if (type == ChartType.CLASS) {
                List<Integer> colors = getClassColorList();
                dataSet.setColors(colors);
            }
        }else{
            dataSet.setColors(ColorTemplate.LIBERTY_COLORS);
        }
        PieData data = new PieData(xVals, dataSet);
        data.setValueTextColor(chart.getResources().getColor(R.color.black));
        data.setValueTextSize(14);
        chart.setDescription("");
        if (useFormatter)
            data.setValueFormatter(new LargeValueFormatter());

        chart.highlightValues(null);

        chart.setData(data);
        chart.requestLayout();
    }

    public static void buildLineChart(LineChart chart, List<String> xList, List<Float> numbers, boolean isLightTheme, int height) {
        int textColor = !isLightTheme ? chart.getResources().getColor(R.color.material_text_primary) : chart.getResources().getColor(R.color.material_light_text_secondary);
        int accentColor = chart.getResources().getColor(R.color.material_accent);
        boolean colorblind = new Prefs(chart.getContext()).getBoolean(SVault.PREF_COLORBLIND, false);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) chart.getLayoutParams();
        params.height = height;

        chart.setDoubleTapToZoomEnabled(false);
        chart.setPinchZoom(false);

        chart.setDescription("");

        chart.setDragEnabled(false);
        chart.setScaleEnabled(false);
        chart.setDrawGridBackground(false);

        chart.getLegend().setEnabled(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(textColor);
        xAxis.setDrawGridLines(true);

        YAxis yAxis = chart.getAxisRight();
        yAxis.setEnabled(false);

        YAxis yAxis2 = chart.getAxisLeft();
        yAxis2.setLabelCount(6, false);
        yAxis2.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxis2.setTextColor(textColor);
        yAxis2.setStartAtZero(false);

        List<Entry> yVals = new ArrayList<Entry>();
        for (int i = 0; i < numbers.size(); i++) {
            yVals.add(new Entry(numbers.get(i), i));
        }

        LineDataSet set = new LineDataSet(yVals, "");

        set.setColor(accentColor);
        set.setLineWidth(1f);
        set.setCircleSize(2f);
        set.setFillColor(accentColor);
        set.setDrawValues(false);
        set.setCircleColor(accentColor);

        List<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(set);

        LineData data = new LineData(xList, dataSets);
        chart.setData(data);
        chart.requestLayout();
    }

    public static void buildLinesChart(LineChart chart, List<String> xList, List<List<Float>> numbers, boolean isLightTheme, int height) {
        int textColor = !isLightTheme ? chart.getResources().getColor(R.color.material_text_primary) : chart.getResources().getColor(R.color.material_light_text_secondary);
        boolean colorblind = new Prefs(chart.getContext()).getBoolean(SVault.PREF_COLORBLIND, false);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) chart.getLayoutParams();
        params.height = height;

        chart.setDoubleTapToZoomEnabled(true);
        chart.setPinchZoom(true);

        chart.setDescription("");

        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setDrawGridBackground(false);


        chart.getLegend().setEnabled(true);
        chart.getLegend().setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
        chart.getLegend().setTextColor(textColor);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(textColor);
        xAxis.setDrawGridLines(true);

        YAxis yAxis = chart.getAxisRight();
        yAxis.setEnabled(false);

        YAxis yAxis2 = chart.getAxisLeft();
        yAxis2.setLabelCount(6, false);
        yAxis2.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxis2.setTextColor(textColor);
        yAxis2.setStartAtZero(false);

        List<Integer> colors = getRecentColorList();
        List<String> labels = new ArrayList<>();
        labels.add(chart.getContext().getString(R.string.overall) + " " + chart.getContext().getString(R.string.wn8_cap));
        labels.add(chart.getContext().getString(R.string.day_wn8));
        labels.add(chart.getContext().getString(R.string.week_wn8));
        labels.add(chart.getContext().getString(R.string.month_wn8));
        labels.add(chart.getContext().getString(R.string.two_month_wn8));

        List<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        for(int j = 0; j < numbers.size(); j++){
            List<Float> lineNumbers = numbers.get(j);
            List<Entry> yVals = new ArrayList<Entry>();
            for (int i = 0; i < lineNumbers.size(); i++) {
                yVals.add(new Entry(lineNumbers.get(i), i));
            }

            LineDataSet set = new LineDataSet(yVals, labels.get(j));

            set.setColor(colors.get(j));
            set.setLineWidth(1f);
            set.setCircleSize(2f);
            set.setFillColor(colors.get(j));
            set.setDrawValues(false);
            set.setCircleColor(colors.get(j));

            dataSets.add(set);
        }


        LineData data = new LineData(xList, dataSets);
        chart.setData(data);
        chart.requestLayout();
    }

    @NonNull
    private static List<Integer> getRecentColorList() {
        List<Integer> colors = new ArrayList<Integer>();
        colors.add(Color.parseColor("#ff8800"));
        colors.add(Color.parseColor("#FFEB3B"));
        colors.add(Color.parseColor("#8BC34A"));
        colors.add(Color.parseColor("#03A9F4"));
        colors.add(Color.parseColor("#7E57C2"));
        return colors;
    }

    @NonNull
    private static List<Integer> getClassColorList() {
        List<Integer> colors = new ArrayList<Integer>();
        colors.add(Color.parseColor("#F44336"));
        colors.add(Color.parseColor("#2196F3"));
        colors.add(Color.parseColor("#FF9800"));
        colors.add(Color.parseColor("#9C27B0"));
        colors.add(Color.parseColor("#4CAF50"));
        return colors;
    }

    private static void cleanUpTitles(ChartType type, List<String> xVals) {
        if (type == ChartType.CLASS)
            for (int i = 0; i < xVals.size(); i++) {
                String s = xVals.get(i);
                String newStr = null;
                if (s.equals("heavyTank")) {
                    newStr = "Heavy";
                } else if (s.equals("AT-SPG"))
                    newStr = "TD";
                else if (s.equals("mediumTank"))
                    newStr = "Medium";
                else if (s.equals("lightTank"))
                    newStr = "Light";

                if (newStr != null)
                    xVals.set(i, newStr);
            }
        else if (type == ChartType.NATION) {
            for (int i = 0; i < xVals.size(); i++) {
                String s = xVals.get(i);
                String newStr = null;
                if (s.equals("germany")) {
                    newStr = "Germany";
                } else if (s.equals("france"))
                    newStr = "France";
                else if (s.equals("russia"))
                    newStr = "USSR";
                else if (s.equals("jp"))
                    newStr = "Japan";
                else if (s.equals("china"))
                    newStr = "China";
                else if (s.equals("czech"))
                    newStr = "Czech";
                else if (s.equals("sweden"))
                    newStr = "Sweden";

                if (newStr == null)
                    newStr = s.toUpperCase();

                xVals.set(i, newStr);
            }
        }
    }
}
