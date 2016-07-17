package com.example.valverde.valverderunkeeper.running.tempo_chart;

import android.content.Context;
import android.graphics.Color;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

public class TempoChart {
    private LineChart chart;

    public TempoChart(Context context) {
        chart = new LineChart(context);
        chart.setNoDataTextDescription("No data");
        chart.setDescription(null);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setDrawGridBackground(false);
        chart.setPinchZoom(true);
        chart.setBackgroundColor(Color.parseColor("#DCEDC8"));

        LineData data = new LineData();
        data.setValueTextColor(Color.BLACK);
        data.setHighlightEnabled(true);
        chart.setData(data);

        Legend legend = chart.getLegend();
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTextColor(Color.BLACK);

        XAxis x1 = chart.getXAxis();
        x1.setTextColor(Color.BLACK);
        x1.setDrawGridLines(false);
        x1.setAvoidFirstLastClipping(true);
        x1.setAxisLineWidth(2f);

        YAxis y1 = chart.getAxisLeft();
        y1.setTextColor(Color.BLACK);
        y1.setAxisMaxValue(22f);
        y1.setAxisMinValue(0f);
        y1.setAxisLineWidth(2f);
        y1.setGridLineWidth(1f);
        y1.setTextSize(14f);
        y1.setDrawGridLines(true);

        YAxis y2 = chart.getAxisRight();
        y2.setEnabled(false);
    }

    public void addEntry(float value) {
        LineData data = chart.getData();
        if (data != null) {
            ILineDataSet set = data.getDataSetByIndex(0);
            if (set == null) {
                set = createChartSet();
                data.addDataSet(set);
            }
            data.addXValue("");
            data.addEntry(new Entry(value, set.getEntryCount()), 0);
            chart.notifyDataSetChanged();
            chart.setVisibleXRange(6f, 6f);
            chart.moveViewToX(data.getXValCount() - 7);
        }
    }

    private LineDataSet createChartSet() {
        LineDataSet set = new LineDataSet(null, "running tempo");
        set.setDrawCubic(true);
        set.setCubicIntensity(0.2f);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(ColorTemplate.getHoloBlue());
        set.setLineWidth(2f);
        set.setCircleSize(6f);
        set.setFillAlpha(Color.BLACK);
        set.setHighLightColor(Color.rgb(244, 117, 177));
        set.setValueTextColor(Color.BLACK);
        set.setValueTextSize(12f);
        return set;
    }

    public LineChart getChart() {
        return chart;
    }
}