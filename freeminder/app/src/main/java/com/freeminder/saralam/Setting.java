package com.freeminder.saralam;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.freeminder.saralam.utils.ConnectionDetector;
import com.freeminder.saralam.utils.DatabaseHandler;
import com.freeminder.saralam.utils.SessionManager;
import com.freeminder.saralam.adapter.ActionListAdapter;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

public class Setting extends Activity implements AdapterView.OnItemSelectedListener {

    private Button btnViewEvent, btnAddEvent;
    private TextView txtSave, txtContactUs;
    private EditText eTxtName, eTxtEmailId, eTxtPassword, eTxtNumber;
    private SessionManager sessionManager;
    GregorianCalendar calendarSetting;
    private View imgBack;
    private LinearLayout llCloseAddAction, llCloseAction;
    ListView listAction;
    private Button btnOK;
    private WindowManager.LayoutParams params;
    private WindowManager wm;
    private LayoutInflater inflaterDialog;
    private View myViewAddAction, myViewDatePicker, myViewLoading, myViewAction;
    private ArrayList<HashMap<String, String>> actionList;
    private ActionListAdapter actionListAdapter;


    //  myAddAction
    private EditText eTxtAcName, eTxtAcContent, eTxtAcSince, etxtAcUntil;
    private CheckBox checkEmail, checkSMS, checkVoice, checkRunSave;
    private Spinner spinnerAc, spinnerService, spinnerGlobalService, spinnerDom;
    private LinearLayout llClosePicker, llCloseContactUs;
    private Button btnSet, btnContactUs;
    private DatePicker datePicker;

    // variable to prevent continous click by the user
    private boolean screenlock = false;
    private int mYear;
    private int mMonth;
    private int mDay;
    private boolean isSinceDate = true;

    // Connection detector class
    ConnectionDetector cd;
    private DatabaseHandler db;

    // parse object for user
    private ParseUser currentUser;
    private String parseObjectId;

     String bname = "";
     String password = "";
     String email = ""; //username &email
     String username = ""; //username &email
     String mobile = ""; //mobile
     String service = ""; //service


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        txtSave = (TextView) findViewById(R.id.txt_save);
        eTxtEmailId = (EditText) findViewById(R.id.etxt_gmail);
        eTxtName = (EditText) findViewById(R.id.etxt_name);
        eTxtPassword = (EditText) findViewById(R.id.etxt_password);
        eTxtNumber = (EditText) findViewById(R.id.etxt_number);
        btnAddEvent = (Button) findViewById(R.id.btn_add_event);
        btnViewEvent = (Button) findViewById(R.id.btn_view_action);
        btnContactUs = (Button) findViewById(R.id.btn_contactus);
        spinnerGlobalService = (Spinner) findViewById(R.id.spinner_detail);
        imgBack = (View) findViewById(R.id.img_logo_back);

        // Spinner click listener
        spinnerGlobalService.setOnItemSelectedListener(this);

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

        db = new DatabaseHandler();
        cd = new ConnectionDetector(getApplicationContext());
        sessionManager = new SessionManager(Setting.this);
        HashMap<String, String> map = new HashMap<String, String>();
        map = sessionManager.getSetting();

        if(!map.get("parse_objectid").isEmpty())
            parseObjectId = map.get("parse_objectid");

    // User settings for parse updateUser failure case
        bname = map.get("name");  //bname
        password = map.get("password");
        email = map.get("gmail"); //username &email
        username = map.get("gmail"); //username &email
        mobile = map.get("number"); //mobile
        service = map.get("service"); //service

        String strName = map.get("name");
        String strGmailId = map.get("gmail");
        String strPassword = map.get("password");
        String strService = map.get("service");
        String strNumber =map.get("number");

        if (strGmailId.length() > 0 && strPassword.length() > 0) {
            eTxtName.setText(strName);
            eTxtEmailId.setText(strGmailId);
            eTxtPassword.setText(strPassword);
            eTxtNumber.setText(strNumber);
            spinnerGlobalService.setSelection(getIndex(spinnerGlobalService, strService));
        }



        // initializing new window parameters
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);
        wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        inflaterDialog = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        eTxtName.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (eTxtName.getText().toString().trim().length() > 0) {
                    eTxtName.setError(null);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });

        eTxtPassword.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (eTxtPassword.getText().toString().trim().length() > 0) {
                    eTxtPassword.setError(null);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });

        eTxtEmailId.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (eTxtEmailId.getText().toString().trim().length() > 0) {
                    if (android.util.Patterns.EMAIL_ADDRESS.matcher(eTxtEmailId.getText().toString().trim()).matches()) {
                        eTxtEmailId.setError(null);
                    }else{
                        eTxtEmailId.setError("Enter valid EmailID");
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });


        txtSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub


                if (setValidation()) {


                    // SAVE ON PARSE HERE METHOD IN THE GETOBJECT METHOD CREATE ANOTHER METHOD TO UPDATE
                    // THE OBJECTID IN SETTINGS

                    new saveSettingsOnParse().execute();

                  /*  if (sessionManager.getTimePeriod().equalsIgnoreCase("installed")) {

                        Log.d("settings.java", "get timeperiod from session manager:" + sessionManager.getTimePeriod());
                        calendarSetting = (GregorianCalendar) Calendar.getInstance();
						Intent myIntent = new Intent(Setting.this, UpdatingService.class);
						pendingIntentSetting = PendingIntent.getService(Setting.this, 0, myIntent, 0);

						Log.e("Setting", "calender time in milliseconds:" + calendarSetting.getTimeInMillis());


						alarmManagerSetting = (AlarmManager) getSystemService(ALARM_SERVICE);
						alarmManagerSetting.set(AlarmManager.RTC, TimeDifference(), pendingIntentSetting);
						alarmManagerSetting.setRepeating(AlarmManager.RTC, calendarSetting.getTimeInMillis(), 86400000, pendingIntentSetting);
						Log.e("Setting", "Alaram manager");
*/
/*
						//uncomment above code and comment below  Start
					    Calendar cal = Calendar.getInstance();
						cal.set(Calendar.SECOND, 0);
						cal.set(Calendar.HOUR_OF_DAY, 0);
						cal.set(Calendar.MINUTE, 0);
						cal.add(Calendar.DAY_OF_MONTH, 1);

						alarmManagerSetting = (AlarmManager) getSystemService(ALARM_SERVICE);
						// alarmManagerSetting.set(AlarmManager.RTC, TimeDifference(), pendingIntentSetting);
						alarmManagerSetting.setRepeating(AlarmManager.RTC, cal.getTimeInMillis(), 86400000, pendingIntentSetting);

*/                        //end

                    }
                    //else if (sessionManager.getTimePeriod().)

                    Intent mainIntent = new Intent(Setting.this,MainActivity.class);
                    startActivity(mainIntent);
                    finish();
                }

        });





        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent closeIntent = new Intent(Setting.this, MainActivity.class);
                startActivity(closeIntent);
                finish();
            }
        });

        btnAddEvent.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (!screenlock)
                    // function to invoke system alert
                    setAction();
                // block the user click till the action is completed
                screenlock = true;
            }
        });

        btnViewEvent.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                //showAction();
                new ParseAction().execute();

            }
        });

        btnContactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contactUs();
            }
        });

    }

    private int getIndex(Spinner spinnerGlobalService,String strService){

        int index = 0;

        for (int i=0;i<spinnerGlobalService.getCount();i++){
            if (spinnerGlobalService.getItemAtPosition(i).toString().equalsIgnoreCase(strService)){
                index = i;
                break;
            }
        }

        return index;
    }

    private void contactUs(){

        setContentView(R.layout.view_contactus_dialog);
        txtContactUs = (TextView) findViewById(R.id.tv_instruction);
        //  eTxtMsg = (EditText) findViewById(R.id.et_msg);
        llCloseContactUs = (LinearLayout) findViewById(R.id.ll_close_contactus);
        btnContactUs = (Button) findViewById(R.id.btn_contactus_send);


        txtContactUs.setText(" Please contact marikanti@gmail.com for technical help ");

        llCloseContactUs.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent closeContactUs = new Intent(Setting.this, Setting.class);
                startActivity(closeContactUs);
                finish();

            }
        });


        btnContactUs.setVisibility(View.INVISIBLE);
    }


    private long TimeDifference(){    // need to check the alaram time and the triggring action time around 12


        Calendar cal = Calendar.getInstance();
        Log.d("settings.java "," time before setting :" + cal.getTime());
        Log.d("settings.java ", " calendertime in millisec: " + cal.getTimeInMillis());
        Log.d("settings.java ", " system time in millisec :" + System.currentTimeMillis());

        //cal.setTime();

        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.HOUR_OF_DAY,0);
        //cal.set(Calendar.HOUR, 23);
        cal.set(Calendar.MINUTE, 0);
        cal.add(Calendar.DAY_OF_MONTH, 1);

        Log.d("settings.java", " time After setting day+1 and 00 hours :" + cal.getTime());

        Log.d("settings.java", " set time in millis :" + cal.getTimeInMillis());
        Log.d("settings.java", " time of the system in millis" + Calendar.getInstance().getTimeInMillis());

        long timediff = cal.getTimeInMillis() - Calendar.getInstance().getTimeInMillis(); //+1000;

        Log.d("settings.java", " current time: " + Calendar.getInstance() + " setted time in alaram should be 23.59.59 but is : " + cal.getTime() + "time diff in millisec :" + timediff);
        return timediff;
    }


    /****   validation    ****/
    private boolean setValidation(){

        String strName = "" + eTxtName.getText().toString().trim();
        String strEmailID = "" + eTxtEmailId.getText().toString().trim();
        String strPassword = "" + eTxtPassword.getText().toString().trim();
        String strService = "" + spinnerGlobalService.getSelectedItem().toString().trim();
        String strNumber = "" + eTxtNumber.getText().toString().trim();
        if (strName.length() == 0) {
            eTxtName.setError("Enter Name");
            return false;
        }

        if (strEmailID.length() > 0) {
            if (android.util.Patterns.EMAIL_ADDRESS.matcher(strEmailID).matches()) {

            }else{
                eTxtEmailId.setError("Enter valid EmailID");
                return false;
            }
        }else {
            eTxtEmailId.setError("Enter EmailID");
            return false;
        }

        if (strPassword.length() == 0) {
            eTxtPassword.setError("Enter Password");
            return false;
        }
        if(strNumber.length() == 0){
            eTxtNumber.setError("Please Enter Number");
            return false;
        }else if( strNumber.length() > 10 || strNumber.length() < 10 || !strNumber.matches("\\d{10}")  ){
            eTxtNumber.setError("Please Enter Valid Number");
            return false;
        }


        if(strService.length() ==0)
        {
            spinnerGlobalService.setPrompt("Select the Service");
        }

        return true;
    }

    /****   validation    ****/
    private boolean setActionValidation(){

        String strName = "" + eTxtAcName.getText().toString().trim();
        String strContent = "" + eTxtAcContent.getText().toString().trim();
        String strSince = "" + eTxtAcSince.getText().toString().trim();

        if (strName.length() == 0) {
            eTxtAcName.setError("Enter Name");
            return false;
        }

        if (strContent.length() == 0) {

            eTxtAcContent.setError("Enter Content");
            return false;
        }

        if (strSince.length() > 0) {

        }else {
            eTxtAcSince.setError("Set Since Date");
            return false;
        }

        return true;
    }

    /****    show Add Action dialog   ****/
    @SuppressLint("SimpleDateFormat")
    public void setAction(){

        myViewAddAction = inflaterDialog.inflate(R.layout.add_action_dialog, null);
        llCloseAddAction = (LinearLayout) myViewAddAction.findViewById(R.id.ll_close_action);
        btnOK = (Button) myViewAddAction.findViewById(R.id.btn_action_ok);
        eTxtAcName = (EditText) myViewAddAction.findViewById(R.id.etxt_ac_name);
        eTxtAcContent = (EditText) myViewAddAction.findViewById(R.id.etxt_ac_content);
        eTxtAcSince = (EditText) myViewAddAction.findViewById(R.id.etxt_ac_since);
        etxtAcUntil = (EditText) myViewAddAction.findViewById(R.id.etxt_ac_until);
        checkEmail = (CheckBox) myViewAddAction.findViewById(R.id.check_email);
        checkSMS = (CheckBox) myViewAddAction.findViewById(R.id.check_sms);
        checkVoice = (CheckBox) myViewAddAction.findViewById(R.id.check_voice);
        checkRunSave = (CheckBox) myViewAddAction.findViewById(R.id.check_run_on_save);
        spinnerDom =(Spinner) myViewAddAction.findViewById(R.id.spinner_dom);
        spinnerAc = (Spinner) myViewAddAction.findViewById(R.id.spinner_ac);
        spinnerService = (Spinner) myViewAddAction.findViewById(R.id.spinner_service);

        // Spinner click listener
        spinnerService.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> cats = new ArrayList<String>();

        cats.add("GYM");
        cats.add("EDUCATION");
        cats.add("MOTOR SERVICE");
        cats.add("INSURANCE");
        cats.add("LEGAL");
        cats.add("COMMUNITY");
        cats.add("MEDICAL");
        cats.add("DIAGNOSTIC CENTER");
        cats.add("HOME");
        cats.add("CABLE/INTERNET SERVICE");
        cats.add("OTHER SUBSCRIPTIONS");


        // Creating adapter for spinner
        ArrayAdapter<String> dataAda = new ArrayAdapter<String>(Setting.this, android.R.layout.simple_spinner_item, cats);

        // Drop down layout style - list view with radio button
        dataAda.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinnerService.setAdapter(dataAda);

        eTxtAcSince.setInputType(InputType.TYPE_NULL);
        etxtAcUntil.setInputType(InputType.TYPE_NULL);


        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df.format(c.getTime());
        eTxtAcSince.setText(formattedDate);

        spinnerDom.setOnItemSelectedListener(this);

        List<String> dayCats = new ArrayList<String>();
        dayCats.add("1");
        dayCats.add("2");
        dayCats.add("3");
        dayCats.add("4");
        dayCats.add("5");
        dayCats.add("6");
        dayCats.add("7");
        dayCats.add("8");
        dayCats.add("9");
        dayCats.add("10");
        dayCats.add("11");
        dayCats.add("12");
        dayCats.add("13");
        dayCats.add("14");
        dayCats.add("15");
        dayCats.add("16");
        dayCats.add("17");
        dayCats.add("18");
        dayCats.add("19");
        dayCats.add("20");
        dayCats.add("21");
        dayCats.add("22");
        dayCats.add("23");
        dayCats.add("24");
        dayCats.add("25");
        dayCats.add("26");
        dayCats.add("27");
        dayCats.add("28");
        dayCats.add("29");
        dayCats.add("30");
        dayCats.add("31");

        ArrayAdapter<String> daycatsAdapter = new ArrayAdapter<String>(Setting.this,android.R.layout.simple_spinner_item,dayCats);

        daycatsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerDom.setAdapter(daycatsAdapter);


        spinnerAc.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("onetime");
        categories.add("monthly");
        categories.add("weekly");
        categories.add("bi-weekly");
        categories.add("annual");
        categories.add("semi-annual");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(Setting.this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinnerAc.setAdapter(dataAdapter);

        eTxtAcSince.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                isSinceDate = true;
                Log.i("Click", "On Edit text");
                showDatePicker();
            }
        });

        etxtAcUntil.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                isSinceDate = false;
                Log.i("Click", "On Edit text");
                showDatePicker();
            }
        });



        btnOK.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (setActionValidation()) {

                    String cEmail = "false", cSMS = "false", cVoice = "false", cRunSave = "false";

                    if (checkEmail.isChecked()) {
                        cEmail = "true";
                    }
                    if (checkSMS.isChecked()) {
                        cSMS = "true";
                    }
                    if (checkVoice.isChecked()) {
                        cVoice = "true";
                    }
                    if (checkRunSave.isChecked()) {
                        cRunSave = "true";
                    }


                    Log.d("Settings.java ", "spinner dom :" + spinnerDom.getSelectedItem().toString());

                    db.addActionToList("0", eTxtAcName.getText().toString().trim(), cEmail, cSMS, cVoice,
                            eTxtAcContent.getText().toString().trim(),
                            eTxtAcSince.getText().toString().trim(),
                            etxtAcUntil.getText().toString().trim(),
                            spinnerAc.getSelectedItem().toString(), cRunSave, spinnerService.getSelectedItem().toString(), "", spinnerDom.getSelectedItem().toString(), parseObjectId);




                    screenlock = false;
                    wm.removeView(myViewAddAction);

                    HashMap<String, String> senderNameMap = new HashMap<String, String>();
                    senderNameMap = sessionManager.getSetting();

                    if (checkRunSave.isChecked()) {
                        setLoading();
                        if (checkSMS.isChecked()) {
                            try {
                                //SmsManager smsManager = SmsManager.getDefault();
                                //smsManager.sendTextMessage(strMobileNo, null, eTxtAcContent.getText().toString(), null, null);
                                //Toast.makeText(Setting.this, "SMS Sent!", Toast.LENGTH_SHORT).show();

							/*	 HashMap<String,String> params = new HashMap<String, String>();
                                params.put("mobile",strMobileNo);
                                params.put("text", eTxtAcContent.getText().toString());
                                params.put("userId ", senderNameMap.get("parse_objectid"));
                                params.put("sender",senderNameMap.get("name")); //add the sender name

								HashMap<String,String> smsStatus = ParseCloud.callFunction("sendSMS", params);

								Log.d("dbHandler.java","sms Status :"+smsStatus.get("status"));
								//Toast.makeText(activity, "SMS Sent!", Toast.LENGTH_SHORT).show();

*/
                            } catch (Exception e) {
                                Toast.makeText(Setting.this, "SMS faild, please try again later!", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }

                        if (checkEmail.isChecked()) {
                            cd = new ConnectionDetector(Setting.this);

                            if (cd.isConnectingToInternet()) {
                                try {
                                    HashMap<String, String> map = new HashMap<String, String>();
                                    map = sessionManager.getSetting();

                                    if (map.get("gmail").length() > 0 && map.get("password").length() > 0) {
/*
										strContent = eTxtAcContent.getText().toString();
										strGmailId = map.get("gmail");
										strPassword = map.get("password");
										//new sendEmail().execute();

										HashMap<String,String> params = new HashMap<String, String>();

										params.put("toEmail", strGmailId);
										params.put("toName", eTxtAcName.getText().toString().trim());
										params.put("messageText", strContent);
										params.put("fromInfo", map.get("name"));

										HashMap<String,String> emailStatus = ParseCloud.callFunction("sendEmail", params);

										Log.d("dbHandler.java","email Status :"+emailStatus.get("status"));



*/
                                    }else {
                                        Toast.makeText(Setting.this, "Please save setting.", Toast.LENGTH_SHORT).show();
                                    }

                                } catch (Exception e) {
                                    Log.e("SendMail", e.getMessage(), e);
                                }
                            }else {
                                Toast.makeText(Setting.this, "You don't have internet connection.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        try {
                            if(myViewLoading != null){
                                wm.removeView(myViewLoading);
                                screenlock = false;
                            }
                        } catch (Exception e) {

                        }
                    }

                }
            }
        });

        llCloseAddAction.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                screenlock = false;
                wm.removeView(myViewAddAction);
            }
        });

        eTxtAcName.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (eTxtAcName.getText().toString().trim().length() > 0) {
                    eTxtAcName.setError(null);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });

        eTxtAcContent.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (eTxtAcContent.getText().toString().trim().length() > 0) {
                    eTxtAcContent.setError(null);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });

        eTxtAcSince.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (eTxtAcSince.getText().toString().length() > 0) {

                    eTxtAcSince.setError(null);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });

        wm.addView(myViewAddAction, params);
    }

    /****    show View Action dialog   ****/
    public void showDatePicker(){

        myViewDatePicker = inflaterDialog.inflate(R.layout.view_date_picker, null);
        llClosePicker = (LinearLayout) myViewDatePicker.findViewById(R.id.ll_close_picker);
        btnSet = (Button) myViewDatePicker.findViewById(R.id.btn_set_date);
        datePicker = (DatePicker) myViewDatePicker.findViewById(R.id.datepicker);

        Log.d("Day", "" + datePicker.getDayOfMonth());
        Log.d("Month", "" + datePicker.getMonth());
        Log.d("Year", "" + datePicker.getYear());

        btnSet.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Log.d("Day", "" + datePicker.getDayOfMonth());
                Log.d("Month", "" + datePicker.getMonth());
                Log.d("Year", "" + datePicker.getYear());

                mDay = datePicker.getDayOfMonth();
                mMonth = datePicker.getMonth();
                mMonth = mMonth + 1;
                mYear = datePicker.getYear();

                if (isSinceDate) {
                    eTxtAcSince.setText(mDay + "/" + mMonth + "/" + mYear);
                }else {
                    etxtAcUntil.setText(mDay + "/" + mMonth + "/" + mYear);
                }

                screenlock = false;
                wm.removeView(myViewDatePicker);
            }
        });

        llClosePicker.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                screenlock = false;
                wm.removeView(myViewDatePicker);
            }
        });

        wm.addView(myViewDatePicker, params);
    }

    /*****    Show Loading dialog with progressbar    *****/
    public void setLoading(){

        myViewLoading = inflaterDialog.inflate(R.layout.alert_loader, null);

        wm.addView(myViewLoading, params);
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                               long arg3) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }

    /****    show View Action dialog   ****/
    public void showAction(){

        myViewAction = inflaterDialog.inflate(R.layout.view_action_dialog, null);
        llCloseAction = (LinearLayout) myViewAction.findViewById(R.id.ll_close_acdetail);
        listAction = (ListView) myViewAction.findViewById(R.id.list_action);

        Calendar cal = Calendar.getInstance();

        actionList = new ArrayList<HashMap<String, String>>();
        //actionList = db.getAllActions("0");

        //TODO Need to add the Actionlist


        if (actionList != null) {
            if (actionList.size() > 0) {
                actionListAdapter = new ActionListAdapter(Setting.this, actionList, "", "");
                listAction.setAdapter(actionListAdapter);
            }
        }else {
            Toast.makeText(Setting.this, "Action list is empty", Toast.LENGTH_SHORT).show();
        }

        llCloseAction.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                screenlock = false;
                wm.removeView(myViewAction);
            }
        });

        wm.addView(myViewAction, params);
    }

    //async task for actionlist fetching from parse API

    ProgressDialog pDialogGetParseActions;

    private class ParseAction extends AsyncTask<Void,Void,ArrayList<HashMap<String, String>>> {


        @Override
        protected ArrayList<HashMap<String, String>> doInBackground(Void... voids) {


            return db.ParseActionList(parseObjectId);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialogGetParseActions = new ProgressDialog(Setting.this, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
            pDialogGetParseActions.setCancelable(false);
            pDialogGetParseActions.setIndeterminate(false);
            pDialogGetParseActions.setMessage("Please Wait...");
            pDialogGetParseActions.setTitle("Fetching  Actions...");
            pDialogGetParseActions.show();


        }

        @Override
        protected void onPostExecute(ArrayList<HashMap<String, String>> parseActions) {
            super.onPostExecute(parseActions);
            if (pDialogGetParseActions.isShowing())
                pDialogGetParseActions.dismiss();

            showAction(parseActions);
        }
    }


    public void showAction(ArrayList<HashMap<String, String>> actionList){

        myViewAction = inflaterDialog.inflate(R.layout.view_action_dialog, null);
        llCloseAction = (LinearLayout) myViewAction.findViewById(R.id.ll_close_acdetail);
        listAction = (ListView) myViewAction.findViewById(R.id.list_action);

        //Calendar cal = Calendar.getInstance();

        //actionList = new ArrayList<HashMap<String, String>>();
        //actionList = db.getAllActions("0");



        Log.d("Settings.java","actionlist size :" + actionList.size() );
        if (actionList != null) {
            if (actionList.size() > 0) {

                actionListAdapter = new ActionListAdapter(Setting.this, actionList, "", "");
                listAction.setAdapter(actionListAdapter);
            }
        }else {
            Toast.makeText(Setting.this, "Action list is empty", Toast.LENGTH_SHORT).show();
        }

        llCloseAction.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                screenlock = false;
                wm.removeView(myViewAction);
            }
        });

        wm.addView(myViewAction, params);
    }

    private ProgressDialog updateProgress;

    // for parse api to fetch the objectid from "user" class

    private class saveSettingsOnParse extends AsyncTask<String ,Void ,HashMap<String, Object>> {
/*
        SessionManager sessionManager = new SessionManager(Setting.this);
        HashMap<String, String> map = new HashMap<String, String>();
        map = sessionManager.;
        */

        String name = "";
        String stremail = "";
        String strpassword ="";
        String strservice= "";
        String strnumber = "";

       @Override
        protected void onPreExecute(){

           super.onPreExecute();
    // updated details from settings page
          name = eTxtName.getText().toString();
           stremail = eTxtEmailId.getText().toString();
           strpassword = eTxtPassword.getText().toString();
           strservice = spinnerGlobalService.getSelectedItem().toString();
           strnumber = eTxtNumber.getText().toString();


            updateProgress = new ProgressDialog(Setting.this, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
            updateProgress.setCancelable(false);
            updateProgress.setIndeterminate(false);
            //loginProgress.setMessage("Please Wait...");
            updateProgress.setTitle("Updating...");
            updateProgress.show();
        }


        String ParseMessage ="";
        HashMap<String, Object> updateUser =null;

        @Override
        protected HashMap<String, Object> doInBackground(String... strings) {



            Log.d("Settings.java","saving settings on parse ");


            Log.d("Settings.java", "updating details to parse : " + bname + " " + username + " " + email + " " + mobile + " " + service);

            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("userId", parseObjectId);
            params.put("username",name);
            params.put("service",strservice);
            params.put("mobile",strnumber);
            params.put("password",strpassword);
            params.put("email",stremail);

            try {

                updateUser = ParseCloud.callFunction("updateUser", params);
                Log.d("DBHandler", " object:" + updateUser);

            } catch (ParseException e) {
                e.printStackTrace();
                String Msg = e.getMessage();
                try {
                    JSONObject parseJsonMsg = new JSONObject(Msg);
                    ParseMessage = parseJsonMsg.getString("message");
                } catch (JSONException excep) {
                    excep.printStackTrace();
                }
                Log.d("Setting.java", "exception message :" + ParseMessage);


            }

            return updateUser;
        }


        @Override
        protected void onPostExecute(HashMap<String, Object> updateUser) {
            super.onPostExecute(updateUser);

            if (updateUser != null) {
                if (updateUser.get("status").equals("success")) {
                    if(updateProgress.isShowing())
                        updateProgress.dismiss();
                    if(updateUser.get("message").equals("User details successfully updated")){
                        sessionManager.saveSetting(eTxtName.getText().toString(), eTxtEmailId.getText().toString(), eTxtPassword.getText().toString(), spinnerGlobalService.getSelectedItem().toString(), eTxtNumber.getText().toString());
                        Toast.makeText(Setting.this,(String) updateUser.get("message"),Toast.LENGTH_LONG).show();
                    }

                }
            }else{
                Toast.makeText(Setting.this,ParseMessage,Toast.LENGTH_LONG).show();
                //reverting to old settings saved in oncreate method in this activity
                sessionManager.saveSetting(bname,email,password,service,mobile);
            }

        }
    }

    private void updateParseUserId(String parseId){

        sessionManager = new SessionManager(Setting.this);
        HashMap<String, String> map = new HashMap<String, String>();
        map = sessionManager.getSetting();

        if(map.get("parse_objectid").isEmpty()){

            sessionManager.setParseObjectId(parseId);
            Log.d("Setting.java class", "Parse objectId updated in Session:" + parseId);
            //parseObjectId class level variable used to send as a parameter in the actionlist
            // here it is updated during the first run
            parseObjectId = parseId;
        }

        Log.d("Setting.class", "Map objectId: " + map.get("parse_objectid") + " fetched objectid:" + parseId);
    }

    @Override
    public void onBackPressed() {

        Intent MainIntent = new Intent(Setting.this,MainActivity.class);
        startActivity(MainIntent);
        finish();

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_setting, menu);
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
