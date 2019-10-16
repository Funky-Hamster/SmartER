package edu.monash.smarter;

import android.app.Fragment;
import android.database.Cursor;
import android.database.SQLException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SQLiteOperation{

    private int temperature = 0;


    public void setTemperature(int temperature){
        this.temperature = temperature;
    }

    protected DBManager dbManager;

    public SQLiteOperation(DBManager dbManager) {
            this.dbManager = dbManager;
    }


    public void insertData() {
        try {
            dbManager.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        dbManager.insertUsage("1", "2018/04/03", "0", "0.3", "1", "0.5", String.valueOf(temperature));
        //sqliteTextView.setText(readData());
        dbManager.close();
    }

    public String readData() {
        try {
            dbManager.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Cursor c = dbManager.getAllData();
        //Cursor c = dbManager.getUsageByResid("1");
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
        return s;
    }

    public void deleteData() {
        try {
            dbManager.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        dbManager.deleteAll();
        dbManager.close();
    }
//
//    public void updateUserName(String id, String name) {
//        try {
//            dbManager.open();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        dbManager.updateUser(id, name); // calling updateUser from DBManager textView.setText(readData());
//        dbManager.close();
//    }

}
