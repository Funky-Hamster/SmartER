package edu.monash.smarter;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;

public class LineGraphFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private View vLineGraph;
    private Spinner doh; //daily or hourly
    private LineChartView lineChartView;
    private LineChartData lineChartData;
    private int numberOfLines = 2;
    private int maxNumberOfLines = 2;
    private int numberOfPoints = 12;

    float[][] randomNumbersTab = new float[maxNumberOfLines][numberOfPoints];

    private boolean hasAxes = true;
    private boolean hasAxesNames = true;
    private boolean hasLines = true;
    private boolean hasPoints = true;
    private ValueShape shape = ValueShape.CIRCLE;
    private boolean isFilled = false;
    private boolean hasLabels = false;
    private boolean isCubic = false;
    private boolean hasLabelForSelected = false;
    private boolean pointsHaveDifferentColor;
    private boolean hasGradientToTransparent = false;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vLineGraph = inflater.inflate(R.layout.fragment_line_graph, container, false);
        lineChartView = vLineGraph.findViewById(R.id.line_graph);
        doh = vLineGraph.findViewById(R.id.daily_hourly);
        doh.setOnItemSelectedListener(this);
        generateHourlyData();

        // Disable viewport recalculations
        lineChartView.setViewportCalculationEnabled(false);

        lineChartView.setZoomType(ZoomType.HORIZONTAL);
        resetViewport();
        initEvent();
        return vLineGraph;
    }


    private void initEvent() {
        lineChartView.setOnValueTouchListener(new ValueTouchListener());
    }

    private void resetViewport() {
        final Viewport v = new Viewport(lineChartView.getMaximumViewport());
        v.bottom = 0;
        v.top = 100;
        v.left = 0;
        v.right = numberOfPoints - 1;
        lineChartView.setMaximumViewport(v);
        lineChartView.setCurrentViewport(v);
    }


    private void generateHourlyData() {
        SmartERApplication smartER = (SmartERApplication) (getActivity().getApplication());
        String resid = smartER.getResid();
        List<Line> lines = new ArrayList<Line>();
        String result = RestClient.findDailyUsagePerHourByDate(resid, Date.valueOf("2018-01-01"));
        for (int i = 0; i < numberOfLines; ++i) { //first: usage, second: temperature

            if (i == 0) {
                List<PointValue> values = new ArrayList<PointValue>();
                JSONArray userData = null;
                double total = 0.0;
                try {
                    userData = new JSONArray(result);
                    for (int k = 0; k < userData.length(); k++) {
                        JSONObject job = new JSONObject(userData.get(k).toString());
                        total = Double.parseDouble(job.getString("usage"));
                        values.add(new PointValue(k, (float) total * 10));
                    }
                    Line line = new Line(values);
                    line.setColor(ChartUtils.COLORS[i]);
                    line.setShape(shape);
                    line.setCubic(isCubic);
                    line.setFilled(isFilled);
                    line.setHasLabels(hasLabels);
                    line.setHasLabelsOnlyForSelected(hasLabelForSelected);
                    line.setHasLines(hasLines);
                    line.setHasPoints(hasPoints);
                    if (pointsHaveDifferentColor) {
                        line.setPointColor(ChartUtils.COLORS[(i + 1) % ChartUtils.COLORS.length]);
                    }
                    lines.add(line);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (i == 1) {
                List<PointValue> values = new ArrayList<PointValue>();
                JSONArray userData = null;
                double total = 0.0;
                try {
                    userData = new JSONArray(result);
                    for (int k = 0; k < userData.length(); k++) {
                        JSONObject job = new JSONObject(userData.get(k).toString());
                        total = Double.parseDouble(String.valueOf(job.getString("temperature")));
                        values.add(new PointValue(k, (float) total));
                    }
                    Line line = new Line(values);
                    line.setColor(ChartUtils.COLORS[i]);
                    line.setShape(shape);
                    line.setCubic(isCubic);
                    line.setFilled(isFilled);
                    line.setHasLabels(hasLabels);
                    line.setHasLabelsOnlyForSelected(hasLabelForSelected);
                    line.setHasLines(hasLines);
                    line.setHasPoints(hasPoints);
                    if (pointsHaveDifferentColor) {
                        line.setPointColor(ChartUtils.COLORS[(i + 1) % ChartUtils.COLORS.length]);
                    }
                    lines.add(line);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }

        lineChartData = new LineChartData(lines);
        if (hasAxes) {
            Axis axisX = new Axis();
            Axis axisYUsage = new Axis().setHasLines(true);
            Axis axisYTemperature = new Axis().setHasLines(true);
            if (hasAxesNames) {
                axisX.setTextColor(Color.BLACK);
                axisYUsage.setTextColor(Color.BLACK);
                axisYTemperature.setTextColor(Color.BLACK);
                axisX.setName("Hour/h");
                axisYUsage.setName("Usage(Blue)/0.1kWh");
                axisYTemperature.setName("Temperature(Purple)/°C");
            }
            lineChartData.setAxisXBottom(axisX);
            lineChartData.setAxisYLeft(axisYUsage);
            lineChartData.setAxisYRight(axisYTemperature);
        } else {
            lineChartData.setAxisXBottom(null);
            lineChartData.setAxisYLeft(null);
            lineChartData.setAxisYRight(null);
        }

        lineChartData.setBaseValue(Float.NEGATIVE_INFINITY);
        lineChartView.setLineChartData(lineChartData);
    }

    private void generateDailyData() {
        SmartERApplication smartER = (SmartERApplication) (getActivity().getApplication());
        String resid = smartER.getResid();
        List<PointValue> values = new ArrayList<PointValue>();
        List<Line> lines = new ArrayList<Line>();
        double total = 0.0;

        //First line: usage
        total = 0.0;
        values = new ArrayList<PointValue>();
        String result = RestClient.findDailyUsageByDate(resid, Date.valueOf("2017-12-30"));
        if (result.equals("[]")) {
            total = 0.0;
        } else {
            result = result.substring(1, result.length() - 1);
            JSONObject userData = null;
            try {
                userData = new JSONObject(result);
                String usage_totally = userData.getString("usage_totally");
                if (usage_totally.equals("null")) {
                    total = 0.0;
                } else {
                    total = Double.parseDouble(userData.getString("usage_totally"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            values.add(new PointValue(0, (float) total));
        }

        total = 0.0;
        result = RestClient.findDailyUsageByDate(resid, Date.valueOf("2017-12-31"));
        if (result.equals("[]")) {
            total = 0.0;
        } else {
            result = result.substring(1, result.length() - 1);
            JSONObject userData = null;
            try {
                userData = new JSONObject(result);
                String usage_totally = userData.getString("usage_totally");
                if (usage_totally.equals("null")) {
                    total = 0.0;
                } else {
                    total = Double.parseDouble(userData.getString("usage_totally"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            values.add(new PointValue(1, (float) total));
        }

        total = 0.0;
        result = RestClient.findDailyUsageByDate(resid, Date.valueOf("2018-01-01"));
        if (result.equals("[]")) {
            total = 0.0;
        } else {
            result = result.substring(1, result.length() - 1);
            JSONObject userData = null;
            try {
                userData = new JSONObject(result);
                String usage_totally = userData.getString("usage_totally");
                if (usage_totally.equals("null")) {
                    total = 0.0;
                } else {
                    total = Double.parseDouble(userData.getString("usage_totally"));
                }
                values.add(new PointValue(2, (float) total));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        values.add(new PointValue(0, (float) total));
        Line line = new Line(values);
        line.setColor(ChartUtils.COLORS[0]);
        line.setShape(shape);
        line.setCubic(isCubic);
        line.setFilled(isFilled);
        line.setHasLabels(hasLabels);
        line.setHasLabelsOnlyForSelected(hasLabelForSelected);
        line.setHasLines(hasLines);
        line.setHasPoints(hasPoints);
        if (pointsHaveDifferentColor) {
            line.setPointColor(ChartUtils.COLORS[(0 + 1) % ChartUtils.COLORS.length]);
        }
        lines.add(line);

        //Second line: temperature
        total = 0.0;
        values = new ArrayList<PointValue>();
        result = RestClient.findDailyUsageByDate(resid, Date.valueOf("2017-12-30"));
        if (result.equals("[]")) {
            total = 0.0;
        } else {
            result = result.substring(1, result.length() - 1);
            JSONObject userData = null;
            try {
                userData = new JSONObject(result);
                String usage_totally = userData.getString("temperature on average");
                if (usage_totally.equals("null")) {
                    total = 0.0;
                } else {
                    total = Double.parseDouble(userData.getString("temperature on average"));
                }
                values.add(new PointValue(0, (float) total ));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        total = 0.0;
        result = RestClient.findDailyUsageByDate(resid, Date.valueOf("2017-12-31"));
        if (result.equals("[]")) {
            total = 0.0;
        } else {
            result = result.substring(1, result.length() - 1);
            JSONObject userData = null;
            try {
                userData = new JSONObject(result);
                String usage_totally = userData.getString("temperature on average");
                if (usage_totally.equals("null")) {
                    total = 0.0;
                } else {
                    total = Double.parseDouble(userData.getString("temperature on average"));
                }
                values.add(new PointValue(1, (float) total ));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        total = 0.0;

        result = RestClient.findDailyUsageByDate(resid, Date.valueOf("2018-01-01"));
        if (result.equals("[]")) {
            total = 0.0;
        } else {
            result = result.substring(1, result.length() - 1);
            JSONObject userData = null;
            try {
                userData = new JSONObject(result);
                String usage_totally = userData.getString("temperature on average");
                if (usage_totally.equals("null")) {
                    total = 0.0;
                } else {
                    total = Double.parseDouble(userData.getString("temperature on average"));
                }
                values.add(new PointValue(2, (float) total ));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        line = new Line(values);
        line.setColor(ChartUtils.COLORS[1]);
        line.setShape(shape);
        line.setCubic(isCubic);
        line.setFilled(isFilled);
        line.setHasLabels(hasLabels);
        line.setHasLabelsOnlyForSelected(hasLabelForSelected);
        line.setHasLines(hasLines);
        line.setHasPoints(hasPoints);
        if (pointsHaveDifferentColor) {
            line.setPointColor(ChartUtils.COLORS[(1 + 1) % ChartUtils.COLORS.length]);
        }
        lines.add(line);


        lineChartData = new LineChartData(lines);
        if (hasAxes) {
            Axis axisX = new Axis();
            Axis axisYUsage = new Axis().setHasLines(true);
            Axis axisYTemperature = new Axis().setHasLines(true);
            if (hasAxesNames) {
                axisX.setTextColor(Color.BLACK);
                axisYUsage.setTextColor(Color.BLACK);
                axisX.setName("Date/YYYY-MM-DD");
                axisYUsage.setName("Usage/kWh");
                axisYTemperature.setName("Temperature");
            }
            lineChartData.setAxisXBottom(axisX);
            lineChartData.setAxisYLeft(axisYUsage);
            lineChartData.setAxisYRight(axisYTemperature);
        } else {
            lineChartData.setAxisXBottom(null);
            lineChartData.setAxisYLeft(null);
            lineChartData.setAxisYRight(null);
        }

        lineChartData.setBaseValue(Float.NEGATIVE_INFINITY);
        lineChartView.setLineChartData(lineChartData);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String daily_hourly = doh.getSelectedItem().toString();
        if (daily_hourly.equals("Hourly")) {
            numberOfPoints = 24;
            // Generate some random values.
            generateHourlyData();

            // Disable viewport recalculations, see toggleCubic() method for more info.
            lineChartView.setViewportCalculationEnabled(false);

            lineChartView.setZoomType(ZoomType.HORIZONTAL);
            resetViewport();   //Size of chart
            initEvent();
        } else if (daily_hourly.equals("Daily")) {
            numberOfPoints = 3;
            // Generate some random values.
            generateDailyData();

            // Disable viewport recalculations, see toggleCubic() method for more info.
            lineChartView.setViewportCalculationEnabled(false);

            lineChartView.setZoomType(ZoomType.HORIZONTAL);
            resetViewport();   //Size of chart
            initEvent();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private class ValueTouchListener implements LineChartOnValueSelectListener {

        @Override
        public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
            Toast.makeText(getActivity(), "Selected: " + value, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onValueDeselected() {

        }

    }
}
