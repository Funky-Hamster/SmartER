package edu.monash.smarter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

public class RegisiterActivity extends Activity{
    int mYear,mMonth,mDay;
    EditText firstname, surname, address, mobile, email, username, password, postcode;
    Spinner nor, provider;
    Button datepicker,submit;
    final int DATE_DIALOG = 1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.regisiter);

        submit = findViewById(R.id.submit);
        datepicker = findViewById(R.id.registerdatepicker);
        firstname = findViewById(R.id.firname);
        surname = findViewById(R.id.surname);
        address = findViewById(R.id.address);
        mobile = findViewById(R.id.mobilenumber);
        postcode = findViewById(R.id.postcode);
        email = findViewById(R.id.emailaddress);
        nor = findViewById((R.id.numberofres));
        provider = findViewById(R.id.energyprovider);
        username = findViewById(R.id.username);
        password  = findViewById(R.id.password);
        Calendar cal = Calendar.getInstance();
        mDay = cal.get(Calendar.DAY_OF_MONTH);
        mMonth = cal.get(Calendar.MONTH);
        mYear = cal.get(Calendar.YEAR);


        datepicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View v) {
                new AsyncTask<String, Void,String>(){
                    @Override
                    protected String doInBackground(String... strings) {

                        String monthformat = String.format("%02d",mMonth);
                        String dayformat = String.format("%02d",mDay);
                        String regidate = Integer.toString(mYear)+"-"+monthformat+"-"+dayformat+"T00:00:00+11:00";
                        String dob = datepicker.getText().toString()+"T00:00:00+11:00";
                        //String firstname, String surname, String dob, String address, String postcode, String email, int mobile, int nor, String provider
                        ResidentInformation residentInformation = new ResidentInformation(firstname.getText().toString(),surname.getText().toString(),datepicker.getText().toString(),
                                address.getText().toString(),postcode.getText().toString(),email.getText().toString(),Integer.parseInt(mobile.getText().toString()), Integer.parseInt(nor.getSelectedItem().toString()),
                                provider.getSelectedItem().toString());
                        //tring username, String password, String registrationDate
                        UserCredentials userCredentials  =  new UserCredentials(username.getText().toString(), md5(password.getText().toString()), regidate);
                        RestClient.createCredentials(userCredentials);
                        RestClient.createInformation(residentInformation);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        super.onPostExecute(s);
                    }
                }.execute();
            }
        });
    }

    private DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int years, int monthOfYear, int dayOfMonth) {
            ++monthOfYear;
            String selectedDate = years + "-" + monthOfYear + "-" + dayOfMonth;
            display(selectedDate);
        }
    };

    private void show() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dialog = new DatePickerDialog(this, dateListener, year, month, day);
        dialog.show();
    }

    public void display(String selectedDate) {
        datepicker.setText(selectedDate);
    }

    public static String md5(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result.toUpperCase().substring(8, 24);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
