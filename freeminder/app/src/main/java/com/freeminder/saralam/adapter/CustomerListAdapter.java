package com.freeminder.saralam.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ListView;
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
public class CustomerListAdapter extends BaseAdapter {

    private Activity activity;
    private DatabaseHandler db;
    private ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();
    private static LayoutInflater inflater = null;
    private String strKeyId, strName, strEmailId, strMobileNo, strService, strCustId, strGmailId, strPassword, strContent;
    private LinearLayout llClose, llCloseAction, llCloseAddAction;
    private EditText eTxtName, eTxtEmailId, eTxtMobileNo;
    private Spinner spinner;
    private Button btnSave, btnOK;
    private WindowManager.LayoutParams params;
    private WindowManager wm;
    private LayoutInflater inflaterDialog;
    private View myViewAddDetail, myViewAddAction, myViewAction, myViewDatePicker, myViewLoading;
    private ArrayList<HashMap<String, String>> custList, actionList;

    // variable to prevent continous click by the user
    private boolean screenlock = false;
    private boolean isEdit = false;
    private int mYear;
    private int mMonth;
    private int mDay;
    private boolean isSinceDate = true;

    //  myAddAction
    private EditText eTxtAcName, eTxtAcContent, eTxtAcSince, etxtAcUntil;
    private CheckBox checkEmail, checkSMS, checkVoice, checkRunSave;
    private Spinner spinnerAc, spinnerService, spinnerDom;
    private ListView listAction;
    private ActionListAdapter actionListAdapter;
    private LinearLayout llClosePicker;
    private Button btnSet;
    private DatePicker datePicker;
    private String parseObjectId;
    // Connection detector class
    ConnectionDetector cd;
    SessionManager sessionManager;

    public CustomerListAdapter(Activity act, ArrayList<HashMap<String, String>> list) {
        this.activity = act;
        arrayList = list;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        HashMap<String, String> map = new HashMap<String, String>();
        //map = sessionManager.getSetting();
        //	parseObjectId = map.get("parse_objectid");


        db = new DatabaseHandler();

        // initializing new window parameters
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);
        wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        inflaterDialog = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        sessionManager = new SessionManager(activity);
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
        if(convertView==null){
            vi = inflater.inflate(R.layout.row_list_item, null);

        }else {
            vi = convertView;
        }

        TextView txtItem = (TextView) vi.findViewById(R.id.txt_cust_name);
        ImageView imgDelete = (ImageView) vi.findViewById(R.id.img_delete);
        Button btnAddAction = (Button) vi.findViewById(R.id.btn_add_action);
        Button btnViewAction = (Button) vi.findViewById(R.id.btn_view_action);

        Log.d("row", "" + arrayList.get(position));
        txtItem.setText(arrayList.get(position).get("name"));

        txtItem.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                strName = arrayList.get(position).get("name");
                strEmailId = arrayList.get(position).get("email_id");
                strMobileNo = arrayList.get(position).get("mobile_no");
                strService = arrayList.get(position).get("service");
                strKeyId = arrayList.get(position).get("id");
                Log.d("", "" + strName + strEmailId + strMobileNo + strService);

                if (!screenlock)
                    // function to invoke system alert
                    showDetail();
                // block the user click till the action is completed
                screenlock = true;
            }
        });

        imgDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                alertDialog.setTitle("Alert");
                alertDialog.setMessage("Are you sure you want to delete this detail?");

                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        //db.removeCustomer(arrayList.get(position).get("id"));
                        //arrayList.remove(position);


                        //db.deleteCustomer(strKeyId);
                        String[] param = { arrayList.get(position).get("id"), String.valueOf(arrayList.get(position))};
                        new deleteCustomer().execute(param);
                        arrayList.remove(position);
                        notifyDataSetChanged();
                        dialog.cancel();

                    }
                });

                alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.cancel();
                    }
                });
                alertDialog.show();

            }
        });

        btnAddAction.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                strCustId = arrayList.get(position).get("id");
                strEmailId = arrayList.get(position).get("email_id");
                strMobileNo = arrayList.get(position).get("mobile_no");
                setAction(strCustId);
            }
        });

        btnViewAction.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                strCustId = arrayList.get(position).get("id");
                strEmailId = arrayList.get(position).get("email_id");
                strMobileNo = arrayList.get(position).get("mobile_no");
                new showActionList().execute(arrayList.get(position).get("id"));
                //showAction();
            }
        });

        return vi;
    }

    /****    show Add Detail dialog   ****/
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void showDetail(){

        myViewAddDetail = inflaterDialog.inflate(R.layout.customer_detail, null);
        llClose = (LinearLayout) myViewAddDetail.findViewById(R.id.ll_close_detail);
        eTxtName = (EditText) myViewAddDetail.findViewById(R.id.etxt_name_detail);
        eTxtEmailId = (EditText) myViewAddDetail.findViewById(R.id.etxt_email_detail);
        eTxtMobileNo = (EditText) myViewAddDetail.findViewById(R.id.etxt_mobile_detail);
        //txtService = (TextView) myViewAddDetail.findViewById(R.id.txt_service);
        spinner = (Spinner) myViewAddDetail.findViewById(R.id.spinner_detail);
        btnSave = (Button) myViewAddDetail.findViewById(R.id.btn_save);

        spinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) activity);

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
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

        isEdit = false;
        eTxtEmailId.setText(strEmailId);
        eTxtMobileNo.setText(strMobileNo);
        eTxtName.setText(strName);
        spinner.setSelection(((ArrayAdapter)spinner.getAdapter()).getPosition(strService));
        //txtService.setText(strService);
        eTxtEmailId.setEnabled(false);
        eTxtMobileNo.setEnabled(false);
        eTxtName.setEnabled(false);
        spinner.setEnabled(false);

        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (isEdit) {
                    if (setValidation()) {


                        db.updateCustomer(strKeyId, eTxtName.getText().toString().trim(),
                                eTxtEmailId.getText().toString().trim(),
                                eTxtMobileNo.getText().toString().trim(),
                                spinner.getSelectedItem().toString());


		/*				db.updateCustDetail(strKeyId, eTxtName.getText().toString().trim(),
								eTxtEmailId.getText().toString().trim(),
								eTxtMobileNo.getText().toString().trim(),
								spinner.getSelectedItem().toString());

		*/				custList = new ArrayList<HashMap<String, String>>();

                        // TODO update the local customermethod with parse getactionlist

                        custList = db.getCustList(parseObjectId);

                        if (custList != null) {
                            if (custList.size() > 0) {
                                arrayList = custList;
                                notifyDataSetChanged();
                            }
                        }else {
                            Toast.makeText(activity, "Customer list is empty", Toast.LENGTH_SHORT).show();
                        }

                        eTxtEmailId.setEnabled(false);
                        eTxtMobileNo.setEnabled(false);
                        eTxtName.setEnabled(false);
                        spinner.setEnabled(false);

                        btnSave.setText("EDIT");
                        isEdit = false;
                        screenlock = false;
                        wm.removeView(myViewAddDetail);
                    }

                }else {
                    eTxtEmailId.setEnabled(true);
                    eTxtMobileNo.setEnabled(true);
                    eTxtName.setEnabled(true);
                    spinner.setEnabled(true);

                    btnSave.setText("SAVE");
                    isEdit = true;
                }
            }
        });

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

        eTxtMobileNo.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (eTxtMobileNo.getText().toString().trim().length() > 0) {
                    eTxtMobileNo.setError(null);
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

        llClose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                screenlock = false;
                wm.removeView(myViewAddDetail);
            }
        });

        wm.addView(myViewAddDetail, params);
    }

    /****   validation    ****/
    private boolean setValidation(){

        String strName = "" + eTxtName.getText().toString().trim();
        String strEmailID = "" + eTxtEmailId.getText().toString().trim();
        String strMobileNo = "" + eTxtMobileNo.getText().toString().trim();
        //String strService = "" + eTxtService.getText().toString().trim();

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

		/*if (strMobileNo.length() == 0) {
			eTxtMobileNo.setError("Enter Mobile No.");
			return false;
		}*/

        if(strMobileNo.length() == 0){
            eTxtMobileNo.setError("Please Enter Number");
            return false;
        }else if( strMobileNo.length() > 10 || strMobileNo.length() < 10 || !strMobileNo.matches("\\d{10}")  ){
            eTxtMobileNo.setError("Please Enter Valid Number");
            return false;
        }


		/*if (strService.length() == 0) {
			eTxtService.setError("Enter Service");
			return false;
		}*/

        return true;
    }

    /****    show Add Action dialog   ****/
    @SuppressLint("SimpleDateFormat")
    public void setAction(String ObjectId){

        final String objectId = ObjectId;

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
        spinnerDom = (Spinner) myViewAddAction.findViewById(R.id.spinner_dom);
        spinnerAc = (Spinner) myViewAddAction.findViewById(R.id.spinner_ac);
        spinnerService = (Spinner) myViewAddAction.findViewById(R.id.spinner_service);

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

        eTxtAcSince.setInputType(InputType.TYPE_NULL);
        etxtAcUntil.setInputType(InputType.TYPE_NULL);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df.format(c.getTime());
        eTxtAcSince.setText(formattedDate);

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

        ArrayAdapter<String> daycatsAdapter = new ArrayAdapter<String>(activity,android.R.layout.simple_spinner_item,dayCats);

        daycatsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerDom.setAdapter(daycatsAdapter);



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

                    Log.d("CLA.java ","spinner dom :" +spinnerDom.getSelectedItem().toString());
                    db.addActionToList(strCustId, eTxtAcName.getText().toString().trim(), cEmail, cSMS, cVoice,
                            eTxtAcContent.getText().toString().trim(),
                            eTxtAcSince.getText().toString().trim(),
                            etxtAcUntil.getText().toString().trim(),
                            spinnerAc.getSelectedItem().toString(), cRunSave, spinnerService.getSelectedItem().toString(), "", spinnerDom.getSelectedItem().toString(), objectId);//parseObjectId

                    screenlock = false;
                    wm.removeView(myViewAddAction);

                    HashMap<String, String> senderNameMap = new HashMap<String, String>();
                    senderNameMap = sessionManager.getSetting();

                    if (checkRunSave.isChecked()) {
                        setLoading();
                        if (checkSMS.isChecked()) {
                            try {
								/*SmsManager smsManager = SmsManager.getDefault();
								smsManager.sendTextMessage(strMobileNo, null, eTxtAcContent.getText().toString(), null, null);
*/

                                HashMap<String,String> params = new HashMap<String, String>();
                                params.put("mobile",strMobileNo);
                                params.put("text", eTxtAcContent.getText().toString());
                                params.put("userId ", senderNameMap.get("parse_objectid"));
                                params.put("sender",senderNameMap.get("name")); //add the sender name

                                HashMap<String,String> smsStatus = ParseCloud.callFunction("sendSMS", params);

                                Log.d("dbHandler.java","sms Status :"+smsStatus.get("status"));
                                //Toast.makeText(activity, "SMS Sent!", Toast.LENGTH_SHORT).show();



                            } catch (Exception e) {
                                Toast.makeText(activity, "SMS faild, please try again later!", Toast.LENGTH_SHORT).show();
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
                                        HashMap<String,String> params = new HashMap<String, String>();

                                        params.put("toEmail", strGmailId);
                                        params.put("toName", eTxtAcName.getText().toString().trim());
                                        params.put("messageText", strContent);
                                        params.put("fromInfo", map.get("name"));

                                        HashMap<String,String> emailStatus = ParseCloud.callFunction("sendEmail", params);

                                        Log.d("dbHandler.java","email Status :"+emailStatus.get("status"));
                                    }else {
                                        Toast.makeText(activity, "Please save setting.", Toast.LENGTH_SHORT).show();
                                    }

                                } catch (Exception e) {
                                    Log.e("SendMail", e.getMessage(), e);
                                }
                            }else {
                                Toast.makeText(activity, "You don't have internet connection.", Toast.LENGTH_SHORT).show();
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
/*
	public void showAction(){

		myViewAction = inflaterDialog.inflate(R.layout.view_action_dialog, null);
		llCloseAction = (LinearLayout) myViewAction.findViewById(R.id.ll_close_acdetail);
		listAction = (ListView) myViewAction.findViewById(R.id.list_action);

		actionList = new ArrayList<HashMap<String, String>>();

		// actionList = db.getAllActions(strCustId);

		//new ActionList().execute();

	}
*/

	/* fetch actions AsyncMethod  */
    ProgressDialog actionProgress;
    public class showActionList extends AsyncTask<String,Void,ArrayList<HashMap<String, String>>> {

        @Override
        protected ArrayList<HashMap<String, String>> doInBackground(String... param) {
            String objectId = param[0];
            return db.ParseActionList(objectId);  //need to pass objectID

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
			/*actionProgress = new ProgressDialog(this,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
			actionProgress.setCancelable(false);
			actionProgress.setIndeterminate(false);
			actionProgress.setMessage("Please Wait...");
			actionProgress.setTitle("Fetching  Contacts...");
			actionProgress.show();
*/
        }

        @Override
        protected void onPostExecute(ArrayList<HashMap<String, String>> actionList) {
            super.onPostExecute(actionList);
			/*if(customerProgress.isShowing())
				customerProgress.dismiss();
*/
            myViewAction = inflaterDialog.inflate(R.layout.view_action_dialog, null);
            llCloseAction = (LinearLayout) myViewAction.findViewById(R.id.ll_close_acdetail);
            listAction = (ListView) myViewAction.findViewById(R.id.list_action);


            if (actionList != null) {
                if (actionList.size() > 0) {
                    actionListAdapter = new ActionListAdapter(activity, actionList, strEmailId, strMobileNo);
                    listAction.setAdapter(actionListAdapter);
                }
            }else {
                Toast.makeText(activity, "Customer list is empty", Toast.LENGTH_SHORT).show();
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

    /** Validation of date
     * Date format = dd-MM-yyyy
     **/
	/*public boolean isThisDateValid(String dateToValidate){

		if(dateToValidate == null){
			return false;
		}

		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
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

        myViewLoading = inflater.inflate(R.layout.alert_loader, null);

        wm.addView(myViewLoading, params);
    }


    public class deleteCustomer extends AsyncTask<String,Void,Void>{

        String Arrayposition;
        @Override
        protected Void doInBackground(String... params) {
            String objectId = params[0];
            Arrayposition = params[1];
            db.deleteCustomer(objectId);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            arrayList.remove(Arrayposition);
        }
    }


    public class sendEmail extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            /*try {


                HashMap<String, String> map = new HashMap<String, String>();
                map = sessionManager.getSetting();
                map = sessionManager.getSetting();
                String name = map.get("name");

                String subject = "Notification From  "+ name;
                String message = strContent;
                String senderOfmail = strGmailId;
                String receiver = strEmailId;

             *//*   GMailSender sender = new GMailSender(strGmailId, strPassword);
                sender.sendMail(subject, message, senderOfmail, receiver);*//*

            } catch (Exception e) {
                Log.e("error", e.getMessage(), e);
                return "Email Not Sent";
            }*/
            return "Email Sent";
        }
    }


}
