package edu.monash.smarter;

import android.app.Fragment;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Date;


public class MapFragment extends Fragment implements OnMapReadyCallback, AdapterView.OnItemSelectedListener {
    public static MapView mapView;

    public static GoogleMap map;

    private final LatLng googleLatLng = new LatLng(0, 0);

    //private Button bSubmit;
    private Spinner doh; //daily or hourly

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View vMap = inflater.inflate(R.layout.fragment_map,
                container, false);
        try {

            // Gets the MapView from the XML layout and creates it
            mapView = (MapView) vMap.findViewById(R.id.mapview);
            mapView.onCreate(savedInstanceState);
            mapView.onResume();
            // Gets to GoogleMap from the MapView and does initialization stuff
            mapView.getMapAsync((OnMapReadyCallback) this);
        } catch (Exception e) {
            System.out.println(e);
        }
        doh = vMap.findViewById(R.id.daily_hourly);
        //bSubmit = vMap.findViewById(R.id.b_submit);
        //bSubmit.setOnClickListener(this);
        doh.setOnItemSelectedListener(this);
        return vMap;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        addMarker(googleMap, 1); //type = 1: hourly, type = 2: daily
        //googleMap.clear();
        map = googleMap;
    }

    public LatLng getGoogleLatLng(String address_input, String postcode) {
        URL url = null;
        JSONObject data = null;
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
            JSONObject geometry = new JSONObject(results).getJSONObject("geometry");
            JSONObject location = geometry.getJSONObject("location");
            lat = location.getDouble("lat");
            lng = location.getDouble("lng");
            latLng = new LatLng(lat, lng);

        } catch (Exception e)

        {
            e.printStackTrace();
        }
        return latLng;
    }

    public void addMarker(GoogleMap googleMap, int type) {
        String jsonText = RestClient.findAllResidents();
        String address = "";
        String postcode = "";
        String firstname = "";
        String resid = "";
        JSONObject job = null;
        String message = "Usage: ";
        String result = "";
        String color = "red";
        SmartERApplication smartER = (SmartERApplication)(getActivity().getApplication());
        try {
            JSONArray JAData = new JSONArray(jsonText.toString());
            for (int i = 0; i < JAData.length(); i++) {
                job = JAData.getJSONObject(i);
                address = job.getString("address");
                postcode = job.getString("postcode");
                firstname = job.getString("firstname");
                resid = String.valueOf(job.getInt("resid"));
                LatLng latLng = getGoogleLatLng(address, postcode);
                if (type == 1){ //hourly
                    double total = 0.0;
                    result = RestClient.findUsageTotalByDateAndHour(resid, Date.valueOf("2018-01-01"), new SimpleDateFormat("HH").format(new Date(System.currentTimeMillis())));
                    if (result.equals("[]")){
                        total = 0.0;
                    }
                    else{
                        result = result.substring(1, result.length() - 1);
                        JSONObject userData = new JSONObject(result);
                        total = Double.parseDouble(userData.getString("total"));
                    }
                    message = "Hourly usage: " + total + "kWh";
                    //message += "\nAddress: " + address;

                    if(total > 1.5) {
                        color = "red";
                    }
                    else {
                        color = "green";
                    }
                }
                else if (type == 2){ //daily
                    double total = 0.0;
                    result = RestClient.findDailyUsageByDate(resid, Date.valueOf("2018-01-01"));
                    if (result.equals("[]")){
                        total = 0.0;
                    }
                    else{
                        result = result.substring(1, result.length() - 1);
                        JSONObject userData = new JSONObject(result);
                        String usage_totally = userData.getString("usage_totally");
                        if (usage_totally.equals("null")){
                            total = 0.0;
                        }
                        else{
                            total = Double.parseDouble(userData.getString("usage_totally"));
                        }
                    }

                    message = "Daily usage: " + total + "kWh";
                    //message += "\nAddress: " + address;

                    if(total > 21) {
                        color = "red";
                    }
                    else {
                        color = "green";
                    }
                }

                if (color.equals("green")) {
                    googleMap.addMarker(new MarkerOptions().position(latLng)
                            .title("Location of " + firstname)
                            .snippet(message)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                }
                else{
                    googleMap.addMarker(new MarkerOptions().position(latLng)
                            .title("Location of " + firstname)
                            .snippet(message));
                }
                if(resid.equals(smartER.getResid())){
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                }
            }
            googleMap.setMinZoomPreference(15);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String daily_hourly = doh.getSelectedItem().toString();
        if (daily_hourly.equals("Hourly")) {
            map.clear();
            addMarker(map, 1);

        } else if (daily_hourly.equals("Daily")) {
            map.clear();
            addMarker(map, 2);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
