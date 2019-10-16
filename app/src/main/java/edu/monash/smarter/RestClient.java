package edu.monash.smarter;

import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Scanner;

public class RestClient {
    private static final String BASE_URI =
            "http://10.0.2.2:8080/SmartER/webresources";

    public static int verifyuserandpass(String username,String password){
        int result = -1;
        final String methodPath = "/smarter.credential";
        boolean userexist = false;
        boolean passcorrect = false;

        URL url = null;
        HttpURLConnection conn = null;
        String textResult = "";
        ArrayList<String> usernamelist = new ArrayList<>();
        ArrayList<String> passwordlist = new ArrayList<>();
        try {
            url = new URL(BASE_URI + methodPath);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            Scanner inStream = new Scanner(conn.getInputStream());
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }
            JSONArray array = new JSONArray(textResult);
            for(int i =0; i < array.length();i++){
                JSONObject js = array.getJSONObject(i);
                usernamelist.add(js.getString("username"));
                passwordlist.add(js.getString("password"));
            }

            ListIterator<String> usernamelist2 = usernamelist.listIterator();
            ListIterator<String> passwordlist2 = passwordlist.listIterator();
            while(usernamelist2.hasNext()){
                if(usernamelist2.next().equals(username)){
                    userexist = true;
                }
            }
            while(passwordlist2.hasNext()){
                if(passwordlist2.next().equals(password)){
                    passcorrect = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }

        if(userexist == false){
            result = 2;
        }else if (userexist == true&& passcorrect==false){
            result = 3;
        }else if (userexist == true&& passcorrect == true){
            result = 1;
        }
        return result;
    }

    public static String findAllResidents() {
        final String methodPath = "/smarter.resident/";
        URL url = null;
        HttpURLConnection conn = null;
        String textResult = "";
        try {
            url = new URL(BASE_URI + methodPath);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            Scanner inStream = new Scanner(conn.getInputStream());
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
        return textResult;
    }

    public static String findResidentByResid(String id){

        final String methodPath = "/smarter.resident/"+id;
        URL url = null;
        HttpURLConnection conn = null;
        String textResult = "";
        try {
            url = new URL(BASE_URI + methodPath);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");

            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            Scanner inStream = new Scanner(conn.getInputStream());
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
        return textResult;
    }

    public static String findUsageTotalByDateAndHour(String resid, Date date, String hour){ //hourly usage

        final String methodPath = "/smarter.usage/Task4.2/" + resid + "/" + date + "/" + hour;
        URL url = null;
        HttpURLConnection conn = null;
        String textResult = "";
        try {
            url = new URL(BASE_URI + methodPath);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");

            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            Scanner inStream = new Scanner(conn.getInputStream());
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
        return textResult;
    }

    public static String findDailyUsageByDate(String resid, Date date){ //daily usage
        final String methodPath = "/smarter.usage/Task5.2/" + resid + "/" + date + "/" + "daily";
        URL url = null;
        HttpURLConnection conn = null;
        String textResult = "";
        try {
            url = new URL(BASE_URI + methodPath);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");

            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            Scanner inStream = new Scanner(conn.getInputStream());
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
        return textResult;
    }

    public static String findDailyUsagePerHourByDate(String resid, Date date){ //daily usage per hour
        final String methodPath = "/smarter.usage/Task5.2/" + resid + "/" + date + "/" + "hourly";
        URL url = null;
        HttpURLConnection conn = null;
        String textResult = "";
        try {
            url = new URL(BASE_URI + methodPath);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");

            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            Scanner inStream = new Scanner(conn.getInputStream());
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
        return textResult;
    }

    public static String findApplianceUsageByDate(String resid, Date date){
        final String methodPath = "/smarter.usage/Task5.1/" + resid + "/" + date;
        URL url = null;
        HttpURLConnection conn = null;
        String textResult = "";
        try {
            url = new URL(BASE_URI + methodPath);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");

            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            Scanner inStream = new Scanner(conn.getInputStream());
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
        return textResult;
    }

    public static void insertUsageToREST(DBManager dbManager){
        try {
            dbManager.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Cursor c = dbManager.getAllData();
        String s = "";
        if (c.moveToFirst()) {
            do {
                s += /*"id: " + c.getString(0) + "\t" +*/ "Resid: " + c.getString(0)
                        + "\t" + "Usage Date: " + c.getString(1)
                        + "\t" + "Usage Hour: " + c.getString(2)
                        + "\t" + "Fridge Usage: " + c.getString(3)
                        + "\t" + "Air Conditioner Usage: " + c.getString(4)
                        + "\t" + "Washing Machine Usage: " + c.getString(5)
                        + "\t" + "Temperature: " + c.getString(6) + "\n";
            } while (c.moveToNext());
        }
        dbManager.close();
    }

    public static String createCredentials(UserCredentials credentials){
//        boolean check;
        final String methodPath = "smarter.credential";
        URL url = null;
        HttpURLConnection con = null;
//        String textResult = "";
        String result = "";
        try {
            Gson gson =new Gson();
            String stringcredentialsJson = gson.toJson(credentials);
            url = new URL("http://10.0.2.2:8080/SmartER/webresources/" + methodPath);
            //open the connection
            con = (HttpURLConnection) url.openConnection();
            //set the timeout
            con.setReadTimeout(10000);
            con.setConnectTimeout(15000);
            //set the connection method to POST
            con.setRequestMethod("POST"); //set the output to true
            con.setDoOutput(true);
            //set length of the data you want to send
            con.setFixedLengthStreamingMode(stringcredentialsJson.getBytes().length);
            //add HTTP headers
            con.setRequestProperty("Content-Type", "application/json");
            //Send the POST out
            PrintWriter out= new PrintWriter(con.getOutputStream());
            out.print(stringcredentialsJson);
            out.close();
            result = stringcredentialsJson;
            Log.i("error",new Integer(con.getResponseCode()).toString());
        } catch (Exception e)
        {
            e.printStackTrace();
            result ="Error";
        }
        finally {
            con.disconnect();
        }
        return result;
    }

    public static String createInformation(ResidentInformation information){
        final String methodPath = "smarter.resident";
        URL url = null;
        HttpURLConnection con = null;
        String result = "";
        try{
            Gson gson =new Gson();
            String stringinfoJson = gson.toJson(information);
            url = new URL("http://10.0.2.2:8080/SmartER/webresources/" + methodPath);
            con = (HttpURLConnection)url.openConnection();
            con.setReadTimeout(10000);
            con.setConnectTimeout(15000);
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setFixedLengthStreamingMode(stringinfoJson.getBytes().length);
            //add HTTP headers
            con.setRequestProperty("Content-Type", "application/json");
            //Send the POST out
            PrintWriter out= new PrintWriter(con.getOutputStream());
            out.print(stringinfoJson);
            out.close();
            result = stringinfoJson;
            Log.i("error",new Integer(con.getResponseCode()).toString());
        }catch (Exception e){
            e.printStackTrace();
            result = "Error";
        }finally {
            con.disconnect();
        }

        return result;
    }

    public static String findIdByUsername(String username){
        final String methodPath = "/smarter.credential/findByUsername/" + username;
        URL url = null;
        HttpURLConnection conn = null;
        String textResult = "";
        try {
            url = new URL(BASE_URI + methodPath);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");

            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            Scanner inStream = new Scanner(conn.getInputStream());
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
        return textResult;
    }

}
