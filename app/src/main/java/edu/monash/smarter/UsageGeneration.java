package edu.monash.smarter;

import android.icu.text.SimpleDateFormat;

import java.sql.Date;
import java.text.DecimalFormat;

public class UsageGeneration {
    double ACUsage, WMUsage, fridgeUsage;
    SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("HH");
    Date currentDate = new Date(System.currentTimeMillis());
    String date = simpleDateFormat1.format(currentDate);
    int hour = Integer.parseInt(simpleDateFormat2.format(currentDate));
    String resid = "0";
    DecimalFormat df = new DecimalFormat("#.00");
    int WMHour = generateWMHour();

    int flag = 0;
    int counter = 0;
    int temperature = 0;
    DBManager dbManager;

    public UsageGeneration(DBManager dbManager, int temperature, String resid) {
        this.dbManager = dbManager;
        this.temperature = temperature;
        this.resid = resid;
    }

    public void generateData() {
        if (hour >= (WMHour + 3) && flag == 0) {
            ++flag;
        }
        String strHour = String.valueOf(hour);
        String fridgeUsage = String.valueOf(genrateFridgeUsage());
        String ACUsage = String.valueOf(generateACUsage());
        String WMUsage = String.valueOf(generateWMUsage(WMHour));
        dbManager.open();
        dbManager.insertUsage(resid, date, strHour, fridgeUsage, ACUsage, WMUsage, String.valueOf(temperature));
        dbManager.close();
    }

    public double genrateFridgeUsage() {
        return (Math.random() * 0.5) + 0.3;
    }

    public double generateACUsage() {
        if (counter < 10 && temperature > 20 && hour >= 9 && hour < 23) {
            ++counter;
            return (Math.random() * 4) + 1;
        }
        return 0;
    }

    public double generateWMUsage(int VMHour) {
        if (flag == 0 && hour >= VMHour && hour < (VMHour + 3)) {
            return (Math.random() * 0.9) + 0.4;
        }
        return 0;
    }

    public int generateWMHour() {
        return (int) (Math.random() * 12) + 6;
    }

}
