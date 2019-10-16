package edu.monash.smarter;

import android.app.Fragment;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MainFragment extends Fragment {

    View vMain;
    protected DBManager dbManager;
    private TextView temperatureTextView;
    private TextView sqliteTextView;
    //private TextView userTextView;
    private TextView welcomeTextView;
    private TextView timeTextView;
    private TextView hourlyUsageView;
    private ImageView status;
    JSONObject data = null;
    JSONObject userData = null;
    private String city = null;
    double total = 0.0;
    private UsageGeneration ug;
    private int hourForGenerating = 0;
    private Button send;
    private Button create;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        vMain = inflater.inflate(R.layout.fragment_main, container, false);
        temperatureTextView = (TextView) vMain.findViewById(R.id.temperature_text);
        sqliteTextView = (TextView) vMain.findViewById(R.id.sqliteTextView);
        //userTextView = (TextView) vMain.findViewById(R.id.userTextView);
        welcomeTextView = (TextView) vMain.findViewById(R.id.welcome_text);
        timeTextView = (TextView) vMain.findViewById(R.id.time_text);
        hourlyUsageView = (TextView) vMain.findViewById(R.id.hourly_usage);
        status = (ImageView) vMain.findViewById(R.id.status);
        send = (Button) vMain.findViewById(R.id.send);
        create = (Button) vMain.findViewById(R.id.create);

        SmartERApplication smartER = (SmartERApplication) (getActivity().getApplication());

        dbManager = smartER.getDbManager();

        //Get time
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date date = new Date(System.currentTimeMillis());
        String currentTime = simpleDateFormat.format(date);
        timeTextView.setText("Currenr time: " + currentTime);

        //Set date (date with full data: 2018-01-01)
        smartER.setDate(Date.valueOf("2018-01-01"));
        //get hourly usage
        simpleDateFormat = new SimpleDateFormat("HH");
        final String currentHour = simpleDateFormat.format(date);

        //Temperature
        city = getCity(smartER.getAddress(), smartER.getPostcode());
        final int temperature = getTemperature(city);
        temperatureTextView.setText("Temperature: " + String.valueOf(temperature) + "°C");

        final String resid = smartER.getResid();

        ug = new UsageGeneration(dbManager, temperature, resid);
        ug.generateData();
        final SQLiteOperation sqlite = new SQLiteOperation(dbManager);

        //SQLite test
        sqliteTextView.setText("Current hour usage:\n" + sqlite.readData());
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqlite.deleteData();
                sqliteTextView.setText("Current hour usage:\n" + sqlite.readData());
            }
        });
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ug.generateData();
                sqliteTextView.setText("Current hour usage:\n" + sqlite.readData());
            }
        });
        //Address and postcode test
        String userText = "Address: " + smartER.getAddress() + " Postcode: " + smartER.getPostcode();
        //userTextView.setText(userText);

        //Welcome text
        String welComeText = "Welcome: " + smartER.getFirstname();
        welcomeTextView.setText(welComeText);

        //getHourlyUsage
        final Date currentDate = smartER.getDate();
        getHourlyUsage(resid, currentDate, currentHour);

        Timer timer = new Timer(true);

        //Set automatically generate
        TimerTask task = new TimerTask() {
            public void run() {
                Date date = new Date(System.currentTimeMillis());
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH");
                final int initialHour = Integer.parseInt(simpleDateFormat.format(date));
                UsageGeneration ug = new UsageGeneration(dbManager, temperature, resid);
                ug.generateData();
                ++hourForGenerating;
                if((hourForGenerating - initialHour) == 24){ //empty all generated data
                    sqlite.deleteData();
                }
            }
        };
        timer.schedule(task, 3600000);


        return vMain;
    }

    public int getTemperature(String city) {
        URL url = null;
        int temperature = 0;
        try {
            url = new URL("http://api.openweathermap.org/data/2.5/weather?q=" + city + "&APPID=91ba4739d3760ab0407ce2016c3b1ecc");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuffer json = new StringBuffer(1024);
            String tmp = "";
            while ((tmp = reader.readLine()) != null)
                json.append(tmp).append("\n");
            reader.close();
            data = new JSONObject(json.toString());
            if (data.getInt("cod") != 200) {
                System.out.println("Cancelled");
            }
            temperature = (int) (data.getJSONObject("main").getDouble("temp") - 273.15);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return temperature;
    }

    public void getHourlyUsage(String resid, Date date, String hour) {

        String result = RestClient.findUsageTotalByDateAndHour(resid, date, hour);
        result = result.substring(1, result.length() - 1);
        total = 0.0;
        try {
            userData = new JSONObject(result);
            total = Double.parseDouble(userData.getString("total"));
            if (total > 1.5) {
                hourlyUsageView.setText("Your hourly usage is more than 1.5 kWh.\nPlease save energy, it is " + total + " kWh");
                //status.setBackgroundColor(Color.parseColor("#FFBB33"));
                status.setImageResource(R.drawable.unhappy);

            } else {
                hourlyUsageView.setText("Your hourly usage is less than 1.5 kWh,\nPlease continue saving, it is " + total + " kWh");
                //status.setBackgroundColor(Color.parseColor("#99CC00"));
                status.setImageResource(R.drawable.happy);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            hourlyUsageView.setText("Your hourly usage is less than 1.5 kWh,\nPlease continue saving, it is 0.0 kWh");
            //status.setBackgroundColor(Color.parseColor("#99CC00"));
            status.setImageResource(R.drawable.happy);
        }
    }

    public String getCity(String address_input, String postcode) {
        String city = null;
        URL url = null;
        JSONObject data = null;
        JSONArray address_components = null;
        double lat = 0.0;
        double lng = 0.0;
        String address = "";
        LatLng latLng = null;
        try {
            address = address_input.replace(" ", "+");
            url = new URL("https://maps.googleapis.com/maps/api/geocode/json?address=" + address + "&components=post_code:" + postcode + "&key=AIzaSyCem_RRyMOdYSbADH2QONc6kzTq0W3kA1c");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuffer json = new StringBuffer(1024);
            String tmp = "";
            while ((tmp = reader.readLine()) != null)
                json.append(tmp).append("\n");
            reader.close();
            data = new JSONObject(json.toString());
            String results = data.getJSONArray("results").get(0).toString();
            address_components = new JSONObject(results).getJSONArray("address_components");
            String temp = address_components.get(3).toString();
            JSONObject long_name = new JSONObject(temp);
            city = long_name.getString("long_name");
            city = city.replace(" City", "");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return city;
    }

}
