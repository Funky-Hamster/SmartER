package edu.monash.smarter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends Activity {
    public EditText usernametext,passwordtext;
    Button loginButton,RegisiterButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        loginButton = (Button)findViewById(R.id.loginbutton);
        RegisiterButton = findViewById(R.id.createaacount);
    }

    public void regisiter(View view){
        Intent intent = new Intent(this,RegisiterActivity.class);
        startActivity(intent);
    }

    public void login(View view) throws NoSuchAlgorithmException {
        usernametext = findViewById(R.id.username);
        passwordtext = findViewById(R.id.password);
        String username = usernametext.getText().toString();
        String password = passwordtext.getText().toString();
        if(username.equals("")){
            Toast.makeText(LoginActivity.this,"Username is empty!",Toast.LENGTH_SHORT).show();
        }
        if(password.equals("")){
            Toast.makeText(LoginActivity.this,"Password is empty!",Toast.LENGTH_SHORT).show();
        }
        else{
            password = md5(password);
            int result = checkUser(username, password);
            if(result == 1){
                Toast.makeText(LoginActivity.this,"Login successfully!",Toast.LENGTH_SHORT).show();
                SmartERApplication smartER = (SmartERApplication) getApplication();
                String textResult = RestClient.findIdByUsername(username);
                textResult = textResult.substring(1, textResult.length() - 1);
                try {
                    JSONObject job = new JSONObject(textResult.toString());
                    String residTemp = String.valueOf(job.get("resid"));
                    int index = residTemp.indexOf(",\"resid\":");
                    String resid = residTemp.substring(index + 9, index + 10);
                    smartER.setResid(resid);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent newIntent = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(newIntent);
            }else if (result == 2){
                Toast.makeText(LoginActivity.this,"Username doesn't exist!",Toast.LENGTH_SHORT).show();
            }else if (result == 3){
                Toast.makeText(LoginActivity.this,"Password is incorrect!" + password,Toast.LENGTH_SHORT).show();
            }
        }
    }

    public int checkUser(String username, String password){
        int result = RestClient.verifyuserandpass(username, password);
        return result;
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
