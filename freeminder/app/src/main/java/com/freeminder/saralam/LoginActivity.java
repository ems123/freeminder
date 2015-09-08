package com.freeminder.saralam;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.freeminder.saralam.utils.SessionManager;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class LoginActivity extends Activity {

    private EditText inputEmail,inputPassword;
    private TextView tv_LinkToRegistration;
    private Button btnLogin;
    private SessionManager sessionManager;
    private ProgressDialog loginProgress;
    private static final String USERNAME = "username";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);


        sessionManager = new SessionManager(getApplicationContext());
        if (sessionManager.isLoggedIn()) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);

            finish();
        }

        setContentView(R.layout.activity_login);

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        tv_LinkToRegistration = (TextView) findViewById(R.id.tv_LinkToRegistration);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isValidEmail(inputEmail.getText().toString()))
                    //Login login =new Login();
                    new login().execute(inputEmail.getText().toString(), inputPassword.getText().toString());
                else
                    Toast.makeText(getApplicationContext(), "Please enter valid email address!", Toast.LENGTH_LONG).show();

            }
        });

        tv_LinkToRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent registerIntent = new Intent(LoginActivity.this, Registration.class);
                startActivity(registerIntent);
                finish();
            }
        });




    }

    private class login extends AsyncTask<String ,Void ,HashMap<String, Object>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loginProgress = new ProgressDialog(LoginActivity.this, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
            loginProgress.setCancelable(false);
            loginProgress.setIndeterminate(false);
            //loginProgress.setMessage("Please Wait...");
            loginProgress.setTitle("Logging in ...");
            loginProgress.show();
        }


        HashMap<String, Object> loginParseObjectId = null;
        String ParseMessage;
        @Override
        protected HashMap<String, Object> doInBackground(String... strings) {

            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("username", strings[0]);
            params.put("password", strings[1]);


            try {
                loginParseObjectId = ParseCloud.callFunction("appLogin", params);
                Log.d("LoginActivity", " ParseObjectId:" + loginParseObjectId);

                /*Log.d("LoginActivity.class", " Status :" +loginParseObjectId.get("status"));
                Log.d("LoginActivity.class", " ParseObjectId: " +loginParseObjectId.get("id"));
                Log.d("LoginActivity.class", " User JSON object:" +loginParseObjectId.get("user"));*/

            } catch (ParseException e) {
                e.printStackTrace();
                String Msg = e.getMessage();
                try {
                    JSONObject parseJsonMsg = new JSONObject(Msg);
                    ParseMessage = parseJsonMsg.getString("message");
                } catch (JSONException excep) {
                    excep.printStackTrace();
                }
                Log.d("LoginActivity.java", "exception message :" + ParseMessage);


            }

            return loginParseObjectId;
        }

        @Override
        protected void onPostExecute(HashMap<String, Object> loginParseId) {
            super.onPostExecute(loginParseId);

            if (loginParseObjectId != null) {
                if (loginParseId.get("status").equals("success")) {


                   Log.d("LoginActivity.java", " parseuser class details :" + loginParseObjectId.get("user"));

                    HashMap<String,String> usermap  = (HashMap<String,String>) loginParseObjectId.get("user");

                    String bname = usermap.get("bname");
                    String email = usermap.get("email");
                    String service = usermap.get("service");
                    String mobile = usermap.get("mobile");


                    sessionManager.saveSetting(bname, email, inputPassword.getText().toString(), service, mobile);
                    sessionManager.createLoginSession(inputEmail.getText().toString(), loginParseObjectId.get("id").toString());
                    // need to add the login session
                    Intent homeScreen = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(homeScreen);
                    finish();

                }

            } else {
                if(loginProgress.isShowing())
                    loginProgress.cancel();
                if(ParseMessage.equals("invalid login parameters"))
                    Toast.makeText(LoginActivity.this, "Check Login Details", Toast.LENGTH_LONG).show();
            }

        }

    }
    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    private Boolean exit = false;

    @Override
    public void onBackPressed() {
        if (exit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }

    }



}
