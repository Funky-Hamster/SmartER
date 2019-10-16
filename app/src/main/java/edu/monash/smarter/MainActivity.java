package edu.monash.smarter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    JSONObject data = null;
    JSONObject userData = null;
    protected DBManager dbManager;
    //private DBManager dbManager = new DBManager(getApplicationContext());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final SmartERApplication smartER = (SmartERApplication) getApplication();
        if(smartER.getResid().equals("0")){
            Intent newIntent = new Intent(this, LoginActivity.class);
            startActivity(newIntent);
        }

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        dbManager = new DBManager(this);
        dbManager.open();
        dbManager.deleteAll();
        dbManager.close();

        //store user data


        //smartER.setResid("1");
        smartER.setDbManager(dbManager);
        final String resid = smartER.getResid();

        new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }
            @Override
            protected String doInBackground(Void... params) {
                String result = RestClient.findResidentByResid(resid);
                String firstname = "";
                String address = "";
                String postcode = "";
                try {
                    userData = new JSONObject(result);
                    address = userData.getString("address");
                    firstname = userData.getString("firstname");
                    postcode = userData.getString("postcode");
                    smartER.setFirstname(firstname);
                    smartER.setAddress(address);
                    smartER.setPostcode(postcode);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            protected void onPostExecute(Void Void) {
                Log.d("Success", userData.toString());
                //String userData = residentData;

            }
        }.execute();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        getSupportActionBar().setTitle("SmartER");
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, new MainFragment()).commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Fragment nextFragment = null;
        switch (id) {
            case R.id.nav_main:
                //Intent newIntent = new Intent(this, MainActivity.class);
                //startActivity(newIntent);
                getSupportActionBar().setTitle("SmartER");
                nextFragment = new MainFragment();
                break;
            case R.id.nav_bar_graph:
                getSupportActionBar().setTitle("Bar Graph Report");
                nextFragment = new BarGraphFragment();
                break;
            case R.id.nav_line_graph:
                getSupportActionBar().setTitle("Line Graph Report");
                nextFragment = new LineGraphFragment();
                break;
            case R.id.nav_pie_chart:
                getSupportActionBar().setTitle("Pie Chart Report");
                nextFragment = new PieChartFragment();
                break;
            case R.id.nav_map:
                getSupportActionBar().setTitle("Map");
                nextFragment = new MapFragment();
                break;
            case R.id.nav_logout:
                Intent newIntent = new Intent(this, LoginActivity.class);
                startActivity(newIntent);
        }
        if (nextFragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame,
                    nextFragment).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
