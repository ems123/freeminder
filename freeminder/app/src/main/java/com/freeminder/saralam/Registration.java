package com.freeminder.saralam;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.freeminder.saralam.utils.SessionManager;
import com.parse.ParseCloud;
import com.parse.ParseException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Registration extends Activity implements AdapterView.OnItemSelectedListener, View.OnClickListener {


    private static final String TAG = Registration.class.getSimpleName();
    private Button btnRegister;
    private TextView tv_LinkToLogin;
    private Spinner spinnerGlobalService;
    private EditText inputFullName;
    private EditText inputEmail;
    private EditText inputPassword,inputNumber;
    private ProgressDialog pDialog;
    private SessionManager sessionManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        inputFullName = (EditText) findViewById(R.id.name);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        inputNumber = (EditText) findViewById(R.id.number);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        tv_LinkToLogin = (TextView) findViewById(R.id.tv_LinkToLogin);
        spinnerGlobalService = (Spinner) findViewById(R.id.spinner_detail);

        // Spinner click listener
        spinnerGlobalService.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("GYM");
        categories.add("EDUCATION");
        categories.add("MOTOR SERVICE");
        categories.add("INSURANCE");
        categories.add("LEGAL");
        categories.add("COMMUNITY");
        categories.add("MEDICAL");
        categories.add("DIAGNOSTIC CENTER");
        categories.add("HOME");
        categories.add("CABLE/INTERNET SERVICE");
        categories.add("OTHER SUBSCRIPTIONS");


        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinnerGlobalService.setAdapter(dataAdapter);


        // Session manager
        sessionManager = new SessionManager(getApplicationContext());



        tv_LinkToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginIntent = new Intent(Registration.this,LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!inputFullName.getText().toString().isEmpty()&&!inputEmail.getText().toString().isEmpty()&&!inputNumber.getText().toString().isEmpty()&&!inputPassword.getText().toString().isEmpty()){
                    if(isValidEmail(inputEmail.getText().toString())&&isValidNumber(inputNumber.getText().toString())){

                        new Register().execute(inputFullName.getText().toString(), inputEmail.getText().toString(), inputPassword.getText().toString(), inputNumber.getText().toString(),spinnerGlobalService.getSelectedItem().toString());

                    }else
                        Toast.makeText(getApplicationContext(),
                                "Please Please enter Valid Number and Email ID!", Toast.LENGTH_LONG)
                                .show();

                }else
                    Toast.makeText(getApplicationContext(),
                            "Please enter your details!", Toast.LENGTH_LONG)
                            .show();

            }
        });



    }
    @Override
    public void onBackPressed() {

        Intent MainIntent = new Intent(Registration.this,LoginActivity.class);
        startActivity(MainIntent);
        finish();

    }


    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public final static boolean isValidNumber(CharSequence number){
        return !TextUtils.isEmpty(number) && Patterns.PHONE.matcher(number).matches();
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private class Register extends AsyncTask<String,Void, HashMap<String,Object>> {

        HashMap<String, Object> registerStatus= null;
        String ParseMessage;
        @Override
        protected HashMap<String, Object> doInBackground(String... strings) {

            HashMap<String, Object> params = new HashMap<String, Object>();

            params.put("username", strings[0]);  //bname
            params.put("password", strings[2]); //password
            params.put("email", strings[1]); //username &email
            params.put("mobile",strings[3] ); //mobile
            params.put("service", strings[4]); //service

            try{

                registerStatus = ParseCloud.callFunction("appSignup", params);
                Log.d("REGISTRATION.java", "parse response :" + registerStatus);


            }catch (ParseException parseException){
                parseException.printStackTrace();
                String Msg = parseException.getMessage();
                try {
                    JSONObject parseJsonMsg = new JSONObject(Msg);
                    ParseMessage = parseJsonMsg.getString("message");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("REGISTRATION.java", "exception message :" + ParseMessage);
                //Toast.makeText(Registration.this," "+ parseException.getMessage(),Toast.LENGTH_SHORT).show();
            }
            return registerStatus;

        }


        @Override
        protected void onPostExecute(HashMap<String, Object> registerStatus) {
            super.onPostExecute(registerStatus);

            if(registerStatus != null){

                if(registerStatus.get("status").equals("success")){

                    //sessionManager.setParseObjectId(loginParseObjectId.get("id").toString());
                    sessionManager.createLoginSession(inputEmail.getText().toString(), registerStatus.get("id").toString());
                    sessionManager.saveSetting(inputFullName.getText().toString(), inputEmail.getText().toString(), inputPassword.getText().toString(), spinnerGlobalService.getSelectedItem().toString(), inputNumber.getText().toString());
                    Intent homeScreen = new Intent(Registration.this,MainActivity.class);
                    startActivity(homeScreen);
                    finish();

                }
                /*else if(registerStatus.get("status").equals("failure") && registerStatus.get("message").equals("Mobile number already exists")){
                    Toast.makeText(Registration.this,"Failed to Register",Toast.LENGTH_SHORT).show();
                    Toast.makeText(Registration.this, "User with the above mobile number already exists", Toast.LENGTH_LONG).show();

                }*/



            }else{
                if(pDialog.isShowing())
                    pDialog.cancel();

                Toast.makeText(Registration.this,"Failed to Register",Toast.LENGTH_SHORT).show();
                Toast.makeText(Registration.this,ParseMessage,Toast.LENGTH_LONG).show();

                if(ParseMessage.equals("Mobile number already exists")){
                    inputNumber.setError("Duplicate Entry");
                }
            }

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Registration.this,ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
            pDialog.setCancelable(false);
            pDialog.setIndeterminate(false);
            pDialog.setMessage("Please Wait...");
            pDialog.show();


        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_registration, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
