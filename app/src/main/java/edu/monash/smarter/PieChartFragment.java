package edu.monash.smarter;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

public class PieChartFragment extends Fragment implements View.OnClickListener {
    View vPieChart;
    private PieChartView pieChart;
    private PieChartData pieChartData;
    private Button dateSelectButton;
    private TextView dateTest;
    private int fridgePercentage = 33;
    private int ACPercentage = 33;
    private int WMPercentage = 33;

    List<SliceValue> values = new ArrayList<SliceValue>();
    //private double[] data = {21, 20, 9, 2, 8, 33, 14, 12};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        vPieChart = inflater.inflate(R.layout.fragment_pie_chart, container, false);
        pieChart = vPieChart.findViewById(R.id.pie_chart);

        setPieChartData(Date.valueOf("2018-01-01"));
        initPieChart();

        dateTest = vPieChart.findViewById(R.id.dateTest);
        dateTest.setText("Year is 2018" + "Month is 01" + "Day is 01" + "\nRed: Frifdege "+ fridgePercentage +"%\nOrange: Air Conditioner "+ ACPercentage +"%\nBlue: Washing Machine "+ WMPercentage +"%");

        dateSelectButton = vPieChart.findViewById(R.id.btn_data_select);
        dateSelectButton.setOnClickListener(this);
        return vPieChart;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_data_select:
                show();
                break;
        }
    }

    private void show() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dialog = new DatePickerDialog(getActivity(), dateListener, year, month, day);
        dialog.show();
    }

    private DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int years, int monthOfYear, int dayOfMonth) {
            ++monthOfYear;
            String generatedDate = years + "-" + monthOfYear + "-" + dayOfMonth;
            setPieChartData(Date.valueOf(generatedDate));
            dateTest.setText("Year is " + years + "Month is " + monthOfYear + "Day is " + dayOfMonth + "\nRed: Frifdege "+ fridgePercentage +"%\nOrange: Air Conditioner "+ ACPercentage +"%\nBlue: Washing Machine "+ WMPercentage +"%");
            initPieChart();
        }
    };

    private void setPieChartData(Date date) {
        SmartERApplication smartER = (SmartERApplication) (getActivity().getApplication());
        String resid = smartER.getResid();
        String result = RestClient.findApplianceUsageByDate(resid, date);
        result = result.substring(1, result.length() - 1);
        double fridgeUsage = 1;
        double ACUsage = 1;
        double WMUsage = 1;
        double sum = 3;
        try {
            JSONObject job = new JSONObject(result.toString());
            if (!job.getString("Daily Washing Maching Usage").equals("null")) {
                fridgeUsage = Double.parseDouble(job.getString("Daily Fridge Usage"));
                ACUsage = Double.parseDouble(job.getString("Daily Air Conditioner Usage"));
                WMUsage = Double.parseDouble(job.getString("Daily Washing Maching Usage"));
                sum = fridgeUsage + ACUsage + WMUsage;
            }
            values = new ArrayList<SliceValue>();
            SliceValue sliceValue = new SliceValue((float) fridgeUsage , Color.parseColor("#FF4444"));
            fridgePercentage = (int) (fridgeUsage * 100 / sum);
            values.add(sliceValue);
            sliceValue = new SliceValue((float) ACUsage, Color.parseColor("#FFBB33"));
            ACPercentage = (int) (ACUsage * 100 / sum);
            values.add(sliceValue);
            sliceValue = new SliceValue((float) WMUsage, Color.parseColor("#33B5E5"));
            WMPercentage = (int) (WMUsage * 100 / sum);
            values.add(sliceValue);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void initPieChart() {
        pieChartData = new PieChartData();
        pieChartData.setHasLabels(true);
        pieChartData.setHasLabelsOnlyForSelected(false);
        pieChartData.setHasLabelsOutside(false);
        pieChartData.setHasCenterCircle(false);
        pieChartData.setValues(values);

        pieChart.setPieChartData(pieChartData);
        pieChart.setValueSelectionEnabled(true);
        pieChart.setAlpha(0.9f);
        pieChart.setCircleFillRatio(1f);
        pieChart.setOnValueTouchListener(new ValueTouchListener());
    }

    private class ValueTouchListener implements PieChartOnValueSelectListener {

        @Override
        public void onValueSelected(int arcIndex, SliceValue value) {
            Toast.makeText(getActivity(), "Selected: " + value + "kWh", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onValueDeselected() {
            // TODO Auto-generated method stub

        }
    }

}
