package com.freeminder.saralam.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.freeminder.saralam.R;
import com.freeminder.saralam.utils.ConnectionDetector;
import com.freeminder.saralam.utils.DatabaseHandler;
import com.freeminder.saralam.utils.SessionManager;
import com.parse.ParseCloud;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Sasikumar Reddy on 07-09-2015.
 */
public class ActionListAdapter extends BaseAdapter {

    private Activity activity;
    private DatabaseHandler db;
    private ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();
    private static LayoutInflater inflater = null;
    private LinearLayout llCloseAction;
    private Button btnSave;
    private WindowManager.LayoutParams params;
    private WindowManager wm;
    private LayoutInflater inflaterDialog;
    private View myViewAddAction, myViewAlert, myViewDatePicker, myViewLoading;
    private ArrayList<HashMap<String, String>> actionList;

    // variable to prevent continous click by the user
    private boolean screenlock = false;
    private boolean isEdit = false;
    private String strId, strCustId, strAcContent, strAcName, strSinceDate, strDom, strUntilDate, strEmail, strSMS, strVoice, strFrequency, strRunSave, strService, objectId;
    private String strKeyId, strMobileNo, strEmailId, strGmailId, strPassword, strContent;
    private int intPosition;
    private int mYear;
    private int mMonth;
    private int mDay;
    private boolean isSinceDate = true;

    //  myAddAction
    private EditText eTxtAcName, eTxtAcContent, eTxtAcSince, etxtAcUntil;
    private CheckBox checkEmail, checkSMS, checkVoice, checkRunSave;
    private Spinner spinnerAc, spinnerService, spinnerDom;
    private Button btnYes, btnNo;
    private LinearLayout llClosePicker;
    private Button btnSet;
    private DatePicker datePicker;

    // Connection detector class
    ConnectionDetector cd;
    SessionManager sessionManager;

    public ActionListAdapter(Activity act, ArrayList<HashMap<String, String>> list, String str_email, String str_mono) {
        this.activity = act;
        this.strMobileNo = str_mono;
        this.strEmailId = str_email;
        arrayList = list;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        db = new DatabaseHandler();
        sessionManager = new SessionManager(activity);

        // initializing new window parameters
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);
        wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        inflaterDialog = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View vi = convertView;
        if (convertView == null) {
            vi = inflater.inflate(R.layout.row_list_action, null);

        } else {
            vi = convertView;
        }

        TextView txtItem = (TextView) vi.findViewById(R.id.txt_action_name);
        ImageView imgDelete = (ImageView) vi.findViewById(R.id.img_action_delete);

        txtItem.setText(arrayList.get(position).get("action_name"));

        txtItem.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                strCustId = arrayList.get(position).get("cust_id");
                strId = arrayList.get(position).get("id");
                strAcName = arrayList.get(position).get("action_name");
                strAcContent = arrayList.get(position).get("content");
                strSinceDate = arrayList.get(position).get("since");
                strUntilDate = arrayList.get(position).get("until");
                strEmail = arrayList.get(position).get("email");
                strSMS = arrayList.get(position).get("sms");
                strVoice = arrayList.get(position).get("voice");
                strFrequency = arrayList.get(position).get("frequency");
                strRunSave = arrayList.get(position).get("run_on_save");
                strService = arrayList.get(position).get("service");
                strDom = arrayList.get(position).get("dom");
                objectId = arrayList.get(position).get("objectId");

                if (!screenlock)
                    // function to invoke system alert
                    editAction();
                // block the user click till the action is completed
                screenlock = true;
            }
        });

        imgDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                strKeyId = arrayList.get(position).get("id");
                objectId = arrayList.get(position).get("objectId");
                intPosition = position;
                showAlert();
            }
        });

        return vi;
    }

    /****
     * show Add Action dialog
     ****/
    @SuppressLint("SimpleDateFormat")
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void editAction() {

        myViewAddAction = inflaterDialog.inflate(R.layout.view_action_detail, null);
        llCloseAction = (LinearLayout) myViewAddAction.findViewById(R.id.ll_edit_close_action);
        btnSave = (Button) myViewAddAction.findViewById(R.id.btn_save_action);
        eTxtAcName = (EditText) myViewAddAction.findViewById(R.id.etxt_edit_ac_name);
        eTxtAcContent = (EditText) myViewAddAction.findViewById(R.id.etxt_edit_ac_content);
        eTxtAcSince = (EditText) myViewAddAction.findViewById(R.id.etxt_edit_ac_since);
        etxtAcUntil = (EditText) myViewAddAction.findViewById(R.id.etxt_edit_ac_until);
        checkEmail = (CheckBox) myViewAddAction.findViewById(R.id.check_edit_email);
        checkSMS = (CheckBox) myViewAddAction.findViewById(R.id.check_edit_sms);
        checkVoice = (CheckBox) myViewAddAction.findViewById(R.id.check_edit_voice);
        checkRunSave = (CheckBox) myViewAddAction.findViewById(R.id.check_edit_run_on_save);
        spinnerDom = (Spinner) myViewAddAction.findViewById(R.id.spinner_dom);
        spinnerAc = (Spinner) myViewAddAction.findViewById(R.id.spinner_edit_ac);
        spinnerService = (Spinner) myViewAddAction.findViewById(R.id.spinner_edit_service);

        // Spinner click listener
        spinnerService.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) activity);

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
        ArrayAdapter<String> dataAda = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, cats);

        // Drop down layout style - list view with radio button
        dataAda.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinnerService.setAdapter(dataAda);


        spinnerDom.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) activity);

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

        ArrayAdapter<String> daycatsAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, dayCats);

        daycatsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerDom.setAdapter(daycatsAdapter);


        eTxtAcSince.setInputType(InputType.TYPE_NULL);
        etxtAcUntil.setInputType(InputType.TYPE_NULL);

        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df.format(c.getTime());
        eTxtAcSince.setText(formattedDate);

        spinnerAc.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) activity);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("onetime");
        categories.add("monthly");
        categories.add("weekly");
        categories.add("bi-weekly");
        categories.add("annual");
        categories.add("semi-annual");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinnerAc.setAdapter(dataAdapter);

        isEdit = false;
        etxtAcUntil.setText(strUntilDate);
        eTxtAcContent.setText(strAcContent);
        eTxtAcName.setText(strAcName);
        eTxtAcSince.setText(strSinceDate);
        spinnerDom.setSelection(((ArrayAdapter) spinnerDom.getAdapter()).getPosition(strDom));
        spinnerAc.setSelection(((ArrayAdapter) spinnerAc.getAdapter()).getPosition(strFrequency));
        spinnerService.setSelection(((ArrayAdapter) spinnerService.getAdapter()).getPosition(strService));

        if (strEmail.equalsIgnoreCase("true")) {
            checkEmail.setChecked(true);
        } else {
            checkEmail.setChecked(false);
        }
        if (strSMS.equalsIgnoreCase("true")) {
            checkSMS.setChecked(true);
        } else {
            checkSMS.setChecked(false);
        }
        if (strVoice.equalsIgnoreCase("true")) {
            checkVoice.setChecked(true);
        } else {
            checkVoice.setChecked(false);
        }
        if (strRunSave.equalsIgnoreCase("true")) {
            checkRunSave.setChecked(true);
        } else {
            checkRunSave.setChecked(false);
        }

        eTxtAcContent.setEnabled(false);
        eTxtAcSince.setEnabled(false);
        etxtAcUntil.setEnabled(false);
        eTxtAcName.setEnabled(false);
        spinnerDom.setEnabled(false);
        spinnerAc.setEnabled(false);
        checkEmail.setEnabled(false);
        checkSMS.setEnabled(false);
        checkVoice.setEnabled(false);
        checkRunSave.setEnabled(false);
        spinnerService.setEnabled(false);

        Log.d("ActionListAdapater", "spinner dom :" + spinnerDom.getSelectedItem().toString());
        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (isEdit) {
                    if (setActionValidate()) {

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

					/*	db.updateCustAction(strId, strCustId, eTxtAcName.getText().toString().trim(), cEmail, cSMS, cVoice,
								eTxtAcContent.getText().toString().trim(),
								eTxtAcSince.getText().toString().trim(),
								etxtAcUntil.getText().toString().trim(),
								spinnerAc.getSelectedItem().toString(), cRunSave, spinnerService.getSelectedItem().toString(),"",spinnerDom.getSelectedItem().toString());
					*/

                        //Action update in the cloud
                        db.updateCustomerAction(strId, strCustId, eTxtAcName.getText().toString().trim(), cEmail, cSMS, cVoice,
                                eTxtAcContent.getText().toString().trim(),
                                eTxtAcSince.getText().toString().trim(),
                                etxtAcUntil.getText().toString().trim(),
                                spinnerAc.getSelectedItem().toString(), cRunSave, spinnerService.getSelectedItem().toString(), "", spinnerDom.getSelectedItem().toString(), objectId);


                        //int dom = Integer.parseInt(spinnerDom.getSelectedItem().toString());
                        actionList = new ArrayList<HashMap<String, String>>();

                        //actionList = db.getAllActions(strCustId); //TODO need to check the dom

						/*if (actionList != null) {
							if (actionList.size() > 0) {
								arrayList = actionList;
								notifyDataSetChanged();
							}
						}else {
							Toast.makeText(activity, "Action list is empty", Toast.LENGTH_SHORT).show();
						}
						*/
                        eTxtAcContent.setEnabled(false);
                        eTxtAcSince.setEnabled(false);
                        etxtAcUntil.setEnabled(false);
                        eTxtAcName.setEnabled(false);
                        spinnerDom.setEnabled(false);
                        spinnerAc.setEnabled(false);
                        checkEmail.setEnabled(false);
                        checkSMS.setEnabled(false);
                        checkVoice.setEnabled(false);
                        checkRunSave.setEnabled(false);

                        Log.d("ALA.java ", "spinner dom :" + spinnerDom.getSelectedItem().toString());

                        btnSave.setText("EDIT");
                        isEdit = false;
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

                                    HashMap<String, String> params = new HashMap<String, String>();
                                    params.put("mobile", strMobileNo);
                                    params.put("text", eTxtAcContent.getText().toString());
                                    params.put("userId ", senderNameMap.get("parse_objectid"));
                                    params.put("sender", senderNameMap.get("name")); //add the sender name

                                    HashMap<String, String> smsStatus = ParseCloud.callFunction("sendSMS", params);

                                    Log.d("dbHandler.java", "sms Status :" + smsStatus.get("status"));
                                    //Toast.makeText(activity, "SMS Sent!", Toast.LENGTH_SHORT).show();

                                } catch (Exception e) {
                                    Toast.makeText(activity, "SMS failed, please try again later!", Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }
                            }

                            if (checkEmail.isChecked()) {
                                cd = new ConnectionDetector(activity);

                                if (cd.isConnectingToInternet()) {
                                    try {

                                        HashMap<String, String> map = new HashMap<String, String>();
                                        map = sessionManager.getSetting();

                                        if (map.get("gmail").length() > 0 && map.get("password").length() > 0) {
                                            strContent = eTxtAcContent.getText().toString();
                                            strGmailId = map.get("gmail");
                                            strPassword = map.get("password");
                                            //new sendEmail().execute();
                                            HashMap<String, String> params = new HashMap<String, String>();

                                            params.put("toEmail", strGmailId);
                                            params.put("toName", eTxtAcName.getText().toString().trim());
                                            params.put("messageText", strContent);
                                            params.put("fromInfo", map.get("name"));

                                            HashMap<String, String> emailStatus = ParseCloud.callFunction("sendEmail", params);

                                            Log.d("dbHandler.java", "email Status :" + emailStatus.get("status"));


                                        } else {
                                            Toast.makeText(activity, "Please save setting.", Toast.LENGTH_SHORT).show();
                                        }

                                    } catch (Exception e) {
                                        Log.e("SendMail", e.getMessage(), e);
                                    }
                                } else {
                                    Toast.makeText(activity, "You don't have internet connection.", Toast.LENGTH_SHORT).show();
                                }

                            }

                            try {
                                if (myViewLoading != null) {
                                    wm.removeView(myViewLoading);
                                    screenlock = false;
                                }
                            } catch (Exception e) {

                            }
                        }
                    }

                } else {
                    eTxtAcContent.setEnabled(true);
                    eTxtAcSince.setEnabled(true);
                    etxtAcUntil.setEnabled(true);
                    eTxtAcName.setEnabled(true);
                    spinnerDom.setEnabled(true);
                    spinnerAc.setEnabled(true);
                    checkEmail.setEnabled(true);
                    checkSMS.setEnabled(true);
                    checkVoice.setEnabled(true);
                    checkRunSave.setEnabled(true);
                    spinnerService.setEnabled(true);

                    Log.d("ALA.java ", "spinner dom :" + spinnerDom.getSelectedItem().toString());

                    btnSave.setText("SAVE");
                    isEdit = true;
                }

            }
        });

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


        llCloseAction.setOnClickListener(new View.OnClickListener() {

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

    /****
     * show Alert dialog
     ****/
    public void showAlert() {

        myViewAlert = inflaterDialog.inflate(R.layout.view_alert, null);
        btnYes = (Button) myViewAlert.findViewById(R.id.btn_yes);
        btnNo = (Button) myViewAlert.findViewById(R.id.btn_no);

        btnYes.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO need to update with delete action from settings level method stub
                //		db.removeAction(strKeyId);

                arrayList.remove(intPosition);
                notifyDataSetChanged();

                String[] param = { objectId};
                new removeAction().execute(param);

                screenlock = false;
                wm.removeView(myViewAlert);
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                screenlock = false;
                wm.removeView(myViewAlert);
            }
        });

        wm.addView(myViewAlert, params);
    }

    public class removeAction extends AsyncTask<String, Void, Void> {


       // ProgressDialog removeActionProgress;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           /* removeActionProgress = new ProgressDialog(activity, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
            removeActionProgress.setCancelable(false);
            removeActionProgress.setIndeterminate(false);
            //loginProgress.setMessage("Please Wait...");
            removeActionProgress.setTitle("Deleting Action...");
            removeActionProgress.show();*/


        }

        @Override
        protected Void doInBackground(String... params) {
            String objectId = params[0];

            db.removeAction(objectId);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
           /* if(removeActionProgress.isShowing()) {
                removeActionProgress.cancel();
                arrayList.remove(Arrayposition);
            }*/

            Toast.makeText(activity, "Action Deleted", Toast.LENGTH_SHORT).show();

        }
    }
        /****
         * validation
         ****/
        private boolean setActionValidate() {

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

            } else {
                eTxtAcSince.setError("Set Since Date");
                return false;
            }

            return true;
        }



        /** Validation of date
         * Date format = dd-MM-yyyy
         **/
	/*public boolean isThisDateValid(String dateToValidate){

		if(dateToValidate == null){
			return false;
		}

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
		sdf.setLenient(false);

		// if not valid, it will throw ParseException
		try {
			sdf.parse(dateToValidate);
			//System.out.println(date);
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return true;
	}*/

        /****
         * show View Action dialog
         ****/
        public void showDatePicker() {

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
                    } else {
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

        /*****
         * Show Loading dialog with progressbar
         *****/
        public void setLoading() {

            myViewLoading = inflater.inflate(R.layout.alert_loader, null);

            wm.addView(myViewLoading, params);
        }
	/*
	public class sendEmail extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			try {

                HashMap<String, String> map = new HashMap<String, String>();
                map = sessionManager.getSetting();

                map = sessionManager.getSetting();
                String name = map.get("name");


                String subject = "Notification From  "+ name;
				String message = strContent;
				String senderOfmail = strGmailId;
				String receiver = strEmailId;

			//	GMailSender sender = new GMailSender(strGmailId, strPassword);
			//	sender.sendMail(subject, message, senderOfmail, receiver);

			} catch (Exception e) {
				Log.e("error", e.getMessage(), e);
				return "Email Not Sent";
			}
			return "Email Sent";
		}
	}*/

    }

