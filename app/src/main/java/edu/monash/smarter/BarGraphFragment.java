package edu.monash.smarter;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;

public class BarGraphFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    //Only on date '2018-01-01' has records
    private View vBarGraph;
    private Spinner doh; //daily or hourly
    private int numColumns = 0;
    private ColumnChartView columnChartView;
    private ColumnChartData columnChartData;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vBarGraph = inflater.inflate(R.layout.fragment_bar_graph, container, false);
        columnChartView = vBarGraph.findViewById(R.id.bar_chart);
        doh = vBarGraph.findViewById(R.id.daily_hourly);
        doh.setOnItemSelectedListener(this);
        generateData(24);
        return vBarGraph;
    }

    private void generateData(int num){
        SmartERApplication smartER = (SmartERApplication) (getActivity().getApplication());
        String resid = smartER.getResid();
        numColumns = num;
        List<Column> columns = new ArrayList<>();
        List<SubcolumnValue> values = new ArrayList<>();
        double total = 0;

        if(num == 24){

            String result = RestClient.findDailyUsagePerHourByDate(resid, Date.valueOf("2018-01-01"));
            if (result.equals("[]")){
                total = 0.0;
            }
            else{
                JSONArray userData = null;
                try {
                    userData = new JSONArray(result);
                    for(int i = 0; i < userData.length(); i++){
                        JSONObject job = new JSONObject(userData.get(i).toString());
                        total = Double.parseDouble(job.getString("usage"));
                        values = new ArrayList<>();
                        values.add(new SubcolumnValue((float) total, ChartUtils.pickColor()));
                        Column column = new Column(values);
                        columns.add(column);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            columnChartData = new ColumnChartData(columns);
            columnChartView.setColumnChartData(columnChartData);

            Axis axisBootom = new Axis();
            Axis axisLeft = new Axis();

            columnChartData.setAxisXBottom(axisBootom);
            columnChartData.setAxisYLeft(axisLeft);

            List<AxisValue> axisValuess=new ArrayList<>();
            for(int i = 0; i < numColumns; i++){
                axisValuess.add(new AxisValue(i).setLabel(i + " hour"));
            }
            axisBootom.setName("Hour/h");
            axisLeft.setName("Usage/kWh");
            axisBootom.setValues(axisValuess);
        }
        else if(num == 3){
            Date date = Date.valueOf("2018-01-01");
            String result = RestClient.findDailyUsageByDate(resid, Date.valueOf("2017-12-30"));
            if (result.equals("[]")){
                total = 0.0;
            }
            else{
                result = result.substring(1, result.length() - 1);
                JSONObject userData = null;
                try {
                    userData = new JSONObject(result);
                    String usage_totally = userData.getString("usage_totally");
                    if (usage_totally.equals("null")){
                        total = 0.0;
                    }
                    else{
                        total = Double.parseDouble(userData.getString("usage_totally"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            values = new ArrayList<>();
            values.add(new SubcolumnValue((float) total, ChartUtils.pickColor()));
            Column column = new Column(values);
            columns.add(column);

            result = RestClient.findDailyUsageByDate(resid, Date.valueOf("2017-12-31"));
            if (result.equals("[]")){
                total = 0.0;
            }
            else{
                result = result.substring(1, result.length() - 1);
                JSONObject userData = null;
                try {
                    userData = new JSONObject(result);
                    String usage_totally = userData.getString("usage_totally");
                    if (usage_totally.equals("null")){
                        total = 0.0;
                    }
                    else{
                        total = Double.parseDouble(userData.getString("usage_totally"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            values = new ArrayList<>();
            values.add(new SubcolumnValue((float) total, ChartUtils.pickColor()));
            column = new Column(values);
            columns.add(column);

            result = RestClient.findDailyUsageByDate(resid, Date.valueOf("2018-01-01"));
            if (result.equals("[]")){
                total = 0.0;
            }
            else{
                result = result.substring(1, result.length() - 1);
                JSONObject userData = null;
                try {
                    userData = new JSONObject(result);
                    String usage_totally = userData.getString("usage_totally");
                    if (usage_totally.equals("null")){
                        total = 0.0;
                    }
                    else{
                        total = Double.parseDouble(userData.getString("usage_totally"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            values = new ArrayList<>();
            values.add(new SubcolumnValue((float) total, ChartUtils.pickColor()));
            column = new Column(values);
            columns.add(column);

            columnChartData = new ColumnChartData(columns);
            columnChartView.setColumnChartData(columnChartData);

            Axis axisBootom = new Axis();
            Axis axisLeft = new Axis();

            columnChartData.setAxisXBottom(axisBootom);
            columnChartData.setAxisYLeft(axisLeft);

            List<AxisValue> axisValuess=new ArrayList<>();
            axisValuess.add(new AxisValue(0).setLabel("2017-12-30"));
            axisValuess.add(new AxisValue(0).setLabel("2017-12-31"));
            axisValuess.add(new AxisValue(0).setLabel("2018-01-01"));
            axisBootom.setName("Date");
            axisLeft.setName("Usage/kWh");
            axisBootom.setValues(axisValuess);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String daily_hourly = doh.getSelectedItem().toString();
        if (daily_hourly.equals("Hourly")) {
            generateData(24);
        } else if (daily_hourly.equals("Daily")) {
            generateData(3);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
