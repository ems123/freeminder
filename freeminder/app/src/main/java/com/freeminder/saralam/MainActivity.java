package com.freeminder.saralam;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.text.Editable;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.freeminder.saralam.adapter.CustomerListAdapter;
import com.freeminder.saralam.adapter.SendMsgAdapter;
import com.freeminder.saralam.adapter.TempContactAdapter;
import com.freeminder.saralam.utils.ConnectionDetector;
import com.freeminder.saralam.utils.CustomerModel;
import com.freeminder.saralam.utils.DatabaseHandler;
import com.freeminder.saralam.utils.SessionManager;
import com.freeminder.saralam.utils.TempModel;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class MainActivity extends Activity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    public TextView outputText;
    public int duplicate;

    ListView list,listView;
    LinearLayout llContactClose;
    private Button btnAddNew, btnSendAll, btnImportContact;

    private ImageView imgSetting;
    private DatabaseHandler db;
    private ArrayList<HashMap<String, String>> custList, contactlist,sendCustList;
    public ArrayList<HashMap<String, String>> finalList;
    private CustomerListAdapter customerListAdapter;

    //SendMsgAdapter SendMsgAdapter;
    boolean isEmailChecked, isSMSChecked;
    private LinearLayout llClose, llCloseMsg;
    private ListView listCust;
    public static CheckBox checkEmail, checkSMS;
    public static EditText eTxtMsg;
    public static String strMsg;
    private Button btnOK;
    public static Button btnSend;
    private EditText eTxtName, eTxtEmailId, eTxtMobileNo;
    private Spinner spinner;
    private WindowManager.LayoutParams params;
    public static WindowManager wm;
    private LayoutInflater inflater;
    public static View myViewAddDetail, myViewSendMsg;

    public static boolean screenlock = false;
    //public static PendingIntent pendingIntent;
    //GregorianCalendar calendar;
    //public static AlarmManager alarmManager;
    SessionManager sessionManager;
    private ProgressDialog pDialog, pDialogimportContact,pDialogAdd;
    private TempModel[] tempModel;
    private CustomerModel[] CustModel;
    private TempContactAdapter tempContactAdapter;
    private Button btnAddContact;
    ConnectionDetector cd;
    private String parseObjectId;
    private SendMsgAdapter sendMsgAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sessionManager = new SessionManager(MainActivity.this);
        HashMap<String, String> map = new HashMap<String, String>();
        map = sessionManager.getSetting();
/*
        try {


            if(map.get("name")==null || map.get("gmail")==null ||  map.get("password")==null || map.get("service")==null || map.get("number")==null) {
                ParseQuery<ParseUser> userObjectQuery = ParseUser.getQuery(); //ParseQuery.getQuery("User");
                Log.d("MainActivity.java","parse object id for fetching the login details :" + map.get("parse_objectid"));
                userObjectQuery.getInBackground(map.get("parse_objectid"), new GetCallback<ParseUser>() {
                    @Override
                    public void done(ParseUser parseUser, ParseException e) {

                        if (e == null) {
                            Log.d("MainActivity","parse UserClass details:" +parseUser.get("bname") +" "+parseUser.get("email")+" "+parseUser.get("mobile"));
                            sessionManager.saveSetting((String) parseUser.get("bname"), (String) parseUser.get("email"), (String) parseUser.get("password"), (String) parseUser.get("service"), (String) parseUser.get("mobile"));
                        }
                    }
                });
            }


        }catch(Exception e){
            e.printStackTrace();
        }*/
        // code used to fetch the objectid when app is opened

        Log.d(TAG, "objectid:" + map.get("parse_objectid"));

        if(!map.get("parse_objectid").isEmpty()){

            parseObjectId = map.get("parse_objectid");

        }


        listView = (ListView) findViewById(R.id.list_cust);
        imgSetting = (ImageView) findViewById(R.id.img_setting);
        btnAddNew = (Button) findViewById(R.id.btn_add_new_cust);
        btnSendAll = (Button) findViewById(R.id.btn_send_all);
        btnImportContact = (Button) findViewById(R.id.btn_import_contacts);


        list = (ListView) findViewById(R.id.tempCustList);

        sessionManager = new SessionManager(MainActivity.this);
        // initializing new window parameters
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        db = new DatabaseHandler();
        custList = new ArrayList<HashMap<String, String>>();

        // TODO need to call ASync task

        if(parseObjectId != null) {
            //fetch custList
            new CustomerList().execute();

        }


        if (sessionManager.getTimePeriod().equalsIgnoreCase("installation")) {
            sessionManager.setTimePeriod("installed");
		/*
			calendar = (GregorianCalendar) Calendar.getInstance();
	        Intent myIntent = new Intent(MainActivity.this, UpdatingService.class);
	        pendingIntent = PendingIntent.getService(MainActivity.this, 0, myIntent, 0);

	        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
	        alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), 86400000, pendingIntent);
	        Log.e("MainActivity", "Alaram manager");
	    */
        }


        btnAddNew.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (!screenlock)
                    // function to invoke system alert
                    setDetail();
                // block the user click till the action is completed
                screenlock = true;
            }
        });

        btnSendAll.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (custList != null) {
                    if (custList.size() > 0) {
                        if (!screenlock)
                            // function to invoke system alert
                            sendMsg();
                        // block the user click till the action is completed
                        screenlock = true;
                    }else {
                        Toast.makeText(MainActivity.this, "Customer list is empty", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(MainActivity.this, "Customer list is empty", Toast.LENGTH_SHORT).show();
                }

            }
        });

        imgSetting.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(MainActivity.this, Setting.class);
                startActivity(intent);
                finish();
            }
        });

        btnImportContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View args0) {
                // TODO import contacts from phone

                if (!screenlock) {
                    Log.d(TAG, "onCreate() import Contacts Async Task button event");
                    new ImportContacts().execute();
                }
                screenlock = true;

            }

        });

        /*sessionManager = new SessionManager(MainActivity.this);
        HashMap<String, String> map = new HashMap<String, String>();
        map = sessionManager.getSetting();
*/
        Log.d(TAG, "objectid:" + map.get("parse_objectid"));


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

		/*if (strMobileNo.length() < 10 || strMobileNo.length() > 10 || !strMobileNo.matches("\\d{10}")) {
			eTxtMobileNo.setError("Enter Valid Mobile No.");
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


    private class ImportContacts extends AsyncTask<Void,Void,List<TempModel>> {


        List<TempModel> contactsList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialogimportContact = new ProgressDialog(MainActivity.this, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
            pDialogimportContact.setCancelable(false);
            pDialogimportContact.setIndeterminate(false);
            pDialogimportContact.setMessage("Please Wait...");
            pDialogimportContact.setTitle("Importing Contacts");
            pDialogimportContact.show();

        }



        @Override
        protected List<TempModel> doInBackground(Void... voids) {

            HashMap<String, String> map = new HashMap<String, String>();

            SessionManager sessionManager = new SessionManager(MainActivity.this);
            map = sessionManager.getSetting();
            String globalService = map.get("service");

            String phoneNumber = null;
            String email = null;
            List<TempModel> listContacts = new ArrayList<TempModel>();

            Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
            String _ID = ContactsContract.Contacts._ID;
            String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
            String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

            Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
            String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
            String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

            Uri EmailCONTENT_URI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
            String EmailCONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
            String DATA = ContactsContract.CommonDataKinds.Email.DATA;

            ContentResolver contentResolver = getContentResolver();

            Cursor cursor = contentResolver.query(CONTENT_URI, null, null, null, null);


            // Loop for every contact in the phone
            if (cursor.getCount() > 0) {


                int size = cursor.getCount();
                //tempModel = new TempModel[size];
                int i = 0;

                HashSet<String> emailList = new HashSet<String>();

                while (cursor.moveToNext()) {


                    String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
                    ;
                    int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));

                    if (hasPhoneNumber > 0) {

                        String contact_id = cursor.getString(cursor.getColumnIndex(_ID));
                        // Query and loop for every phone number of the contact
                        Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[]{contact_id}, null);
                        // Query and loop for every email of the contact
                        Cursor emailCursor = contentResolver.query(EmailCONTENT_URI, null, EmailCONTACT_ID + " = ?", new String[]{contact_id}, null);


                        while (phoneCursor.moveToNext()) {

                            phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                            break;

                        }

                        phoneCursor.close();


                        while (emailCursor.moveToNext()) {


                            email = emailCursor.getString(emailCursor.getColumnIndex(DATA));
                            break;
                        }

                        emailCursor.close();

                        try {
                            if (phoneNumber != null && name != null && email != null && !phoneNumber.isEmpty() && !name.isEmpty() && !email.isEmpty() && !emailList.contains(email)) {

                                TempModel tempContact = new TempModel(name, phoneNumber, email, globalService, true);
                                emailList.add(email);
                                listContacts.add(tempContact);

                            } else if (phoneNumber != null && name != null && !phoneNumber.isEmpty() && !name.isEmpty() && emailList.contains(email)) {

                                email = "noemail@email.com"; // for contacts without any email id

                                TempModel tempContact = new TempModel(name, phoneNumber, email, globalService, true);
                                listContacts.add(tempContact);

                            }


                        } catch (Exception e) {
                            Log.e(TAG, " error : " + e);
                        }

                    }
                }

            }



            return listContacts;
        }


        @Override
        protected void onPostExecute(List<TempModel> tempModels) {
            super.onPostExecute(tempModels);

            if (pDialogimportContact.isShowing())
                pDialogimportContact.dismiss();
            contactsList = tempModels;
            getContacts(contactsList);
        }
    }


    public void getContacts(List<TempModel> Contacts) {


        //pDialog.setProgress();

        Log.d(TAG, "getContacts() Triggered");

        setContentView(R.layout.activity_import_contact);
        outputText = (TextView) findViewById(R.id.contactName);
        list = (ListView) findViewById(R.id.tempCustList);
        llContactClose = (LinearLayout) findViewById(R.id.ll_close_imp);
        btnAddContact = (Button) findViewById(R.id.btn_imp);



        List<TempModel> listContacts = Contacts;


        if(!listContacts.isEmpty()) {


            tempModel = new TempModel[listContacts.size()];
            try {
                for (int j = 0; j < listContacts.size(); j++) {

                    TempModel contact = listContacts.get(j);

                    String name = contact.getName();
                    String mail = contact.getEmail();
                    String number = contact.getNumber();
                    String service = contact.getService();

                    tempModel[j] = new TempModel(name ,number, mail,service,contact.isIsChecked());

                    Log.d(TAG, "name :" + name + " mail :" + mail + "number :" + number + " service: " + service);

                }
            }catch(NullPointerException err){
                err.printStackTrace();
            }
        }


        tempContactAdapter = new TempContactAdapter(this, R.layout.import_row, tempModel);

        list.setAdapter(tempContactAdapter);




        llContactClose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                screenlock = false;
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });




        btnAddContact.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                screenlock = false;
                Log.d(TAG, "import ok button event ");

                ImportContactsCreateAccount importContactsCreateAccount = new ImportContactsCreateAccount(tempContactAdapter);

                importContactsCreateAccount.execute();

            }
        });
    }



    private class ImportContactsCreateAccount extends AsyncTask<Void,Void,Void>{


        TempContactAdapter contactsAdapter;

        public ImportContactsCreateAccount(TempContactAdapter Contactlist){
            contactsAdapter = Contactlist;

        }
        @Override
        protected Void doInBackground(Void... Void) {


            duplicate = 0;

            TempModel[] selectedContactList = contactsAdapter.contactDataItems;

            for (int i = 0; i < selectedContactList.length; i++) {
                TempModel tmpmodel = selectedContactList[i];

                DatabaseHandler dbase = new DatabaseHandler();
                //int size = 10; //dbase.getCustomerCount(); // TODO need to get the size of the customers from parse

                if (tmpmodel!= null && tmpmodel.isIsChecked() && tmpmodel.getNumber()!=null && !tmpmodel.getName().isEmpty()) {

                    //dbase = new DatabaseHandler(MainActivity.this);
                    dbase.addCustomerToList(tmpmodel.getName(),
                            tmpmodel.getEmail(),
                            tmpmodel.getNumber(),
                            tmpmodel.getService(), parseObjectId);

                }


            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialogAdd = new ProgressDialog(MainActivity.this, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
            pDialogAdd.setCancelable(false);
            pDialogAdd.setIndeterminate(false);
            pDialogAdd.setMessage("Please Wait...");
            pDialogAdd.show();

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (pDialogAdd.isShowing())
                pDialogAdd.dismiss();

            Toast.makeText(MainActivity.this, "Please Wait While Updating List", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    Intent MainIntent = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(MainIntent);
                    finish();
                }
            }, 1000);

        }
    }

    // exiting the app on double black click
/*

    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onResume() {
        super.onResume();
        this.doubleBackToExitPressedOnce = false;
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

    }

*/
    /****    show Add Detail dialog   ****/
    public void setDetail(){

        myViewAddDetail = inflater.inflate(R.layout.add_customer_dialog, null);
        llClose = (LinearLayout) myViewAddDetail.findViewById(R.id.ll_close);
        btnOK = (Button) myViewAddDetail.findViewById(R.id.btn_ok);
        eTxtName = (EditText) myViewAddDetail.findViewById(R.id.etxt_name);
        eTxtEmailId = (EditText) myViewAddDetail.findViewById(R.id.etxt_email);
        eTxtMobileNo = (EditText) myViewAddDetail.findViewById(R.id.etxt_mobile);


        spinner = (Spinner) myViewAddDetail.findViewById(R.id.spinner);

        // Spinner click listener
        spinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);

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
        spinner.setAdapter(dataAdapter);

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

        btnOK.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (setValidation()) {
                    screenlock = false;
                    wm.removeView(myViewAddDetail);


                    db.addCustomerToList(eTxtName.getText().toString().trim(),
                            eTxtEmailId.getText().toString().trim(),
                            eTxtMobileNo.getText().toString().trim(),
                            spinner.getSelectedItem().toString(), parseObjectId);

                    Log.d(TAG, "ok button click for set name: " + eTxtName.getText().toString().trim() + "email :" + eTxtEmailId.getText().toString().trim() + "");


                    Toast.makeText(MainActivity.this, "Please wait ...", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            Intent MainIntent = new Intent(MainActivity.this,MainActivity.class);
                            startActivity(MainIntent);
                            finish();
                        }
                    }, 1000);

                }
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

    /****    Show alert widow with customer list    ****/
    public void sendMsg(){

        myViewSendMsg = inflater.inflate(R.layout.send_msg_dialog, null);
        llCloseMsg = (LinearLayout) myViewSendMsg.findViewById(R.id.ll_close_msg);
        btnSend = (Button) myViewSendMsg.findViewById(R.id.btn_send);
        eTxtMsg = (EditText) myViewSendMsg.findViewById(R.id.etxt_msg);
        listCust = (ListView) myViewSendMsg.findViewById(R.id.list_customer);
        checkEmail = (CheckBox) myViewSendMsg.findViewById(R.id.check_email);
        checkSMS = (CheckBox) myViewSendMsg.findViewById(R.id.check_sms);

        if (custList != null) {
            if (custList.size() > 0) {

                CustModel = new CustomerModel[custList.size()];

                for(int i=0; i<custList.size();i++)
                {

                    CustModel[i] = new CustomerModel(custList.get(i).get("id"),custList.get(i).get("name"),custList.get(i).get("mobile_no"),
                            custList.get(i).get("email_id"),custList.get(i).get("service"),true);




                }

                sendMsgAdapter = new SendMsgAdapter(this,R.layout.row_send_msg,CustModel); // need to set the parameters after configuring the SendMsgAdpater
                listCust.setAdapter(sendMsgAdapter);

            }
        }



        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                new sendEmailSMSEvent().execute();

            }




        });

        llCloseMsg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                screenlock = false;
                wm.removeView(myViewSendMsg);
            }
        });

        wm.addView(myViewSendMsg, params);

    }

    public class sendEmailSMSEvent extends AsyncTask<String, ArrayList<HashMap<String,String>>, ArrayList<HashMap<String,String>>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
            pDialog.setMessage("Please wait....");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }


        @Override
        protected ArrayList<HashMap<String,String>> doInBackground(String... strings) {

            sendCustList = new ArrayList<HashMap<String, String>>();
            CustomerModel[] selectedcustomer = sendMsgAdapter.customerList;
            JSONArray SendAllCustomerArray = new JSONArray();
            for (int i = 0; i < selectedcustomer.length; i++) {
                CustomerModel customer = selectedcustomer[i];

                if(customer.isChecked()){

                    Log.d("MainActivity.this", "Send all event names:" + customer.getName());

                    try {

                        if (checkSMS.isChecked()) {

                            JSONObject customerObject = new JSONObject();
                            customerObject.put(customer.getId(), "2");
                            SendAllCustomerArray.put(customerObject);
                            Log.d("MainActivity.this","object id:" +customer.getId() + "code :2 ");

                        } else if(checkEmail.isChecked()){

                            JSONObject customerObject = new JSONObject();
                            customerObject.put(customer.getId(), "1");
                            SendAllCustomerArray.put(customerObject);
                            Log.d("MainActivity.this", "object id:" + customer.getId() + "code :1 ");

                        }else{

                            JSONObject customerObject = new JSONObject();
                            customerObject.put(customer.getId(), "3");
                            SendAllCustomerArray.put(customerObject);
                            Log.d("MainActivity.this", "object id:" + customer.getId() + "code :3 ");
                        }


                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }

            }
            HashMap<String, Object> params = new HashMap<String, Object>();
            Log.d("DBHandler","JSON array :"+ SendAllCustomerArray);
            params.put("contacts", SendAllCustomerArray);


            try {

                ParseCloud.callFunction("sendAll", params);

            } catch (ParseException e) {
                e.printStackTrace();
            }


                                /* HashMap<String,String> custHashmap = new HashMap<String, String>();
                                 custHashmap.put("id", customer.getId());
                                 custHashmap.put("name", customer.getName());
                                 custHashmap.put("mobile_no", customer.getNumber());
                                 custHashmap.put("email_id", customer.getEmail());
                                 custHashmap.put("service", customer.getService());
                                 custHashmap.put("checked", "true");

                                 sendCustList.add(custHashmap);
*/


            return sendCustList;
        }


        @Override
        protected void onPostExecute(ArrayList<HashMap<String, String>> hashMaps) {
            super.onPostExecute(hashMaps);

            if(pDialog.isShowing())
                pDialog.dismiss();

            screenlock = false;
            wm.removeView(myViewSendMsg);
/*

                         if (eTxtMsg.getText().toString().trim().length() > 0) {
                             Log.d("send", "msg");
                             Log.d("array list", "" + sendCustList);
                             EmailNSMS(sendCustList, checkEmail.isChecked(),checkSMS.isChecked());
                             strMsg = eTxtMsg.getText().toString().trim();
                             screenlock = false;
                             wm.removeView(myViewSendMsg);
                         }else {
                             eTxtMsg.setError("Please enter message");
                         }
*/



        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position,
                               long id) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

    }


    public void EmailNSMS(ArrayList<HashMap<String, String>> list, boolean isEmail, boolean isSms){
        finalList = new ArrayList<HashMap<String,String>>();
        finalList = list;
        isEmailChecked = isEmail;
        isSMSChecked = isSms;
        new sendEmailSMS().execute();
    }

    /*********       Logout User        **********/
    public class sendEmailSMS extends AsyncTask<String, String, String> {

        @SuppressLint("InlinedApi")
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
            pDialog.setMessage("Please wait....");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            // Building Parameters
            try {
                Log.i("final List", "" + finalList);
                cd = new ConnectionDetector(MainActivity.this);

                for (int i = 0; i < finalList.size(); i++) {
                    if (finalList.get(i).get("checked").equalsIgnoreCase("true")) {

                        Log.e("MainActivity", "isEmailChecked==" + isEmailChecked + " isSMSChecked==" + isSMSChecked);
                        if (isSMSChecked) {
                            try {
                                Log.e("MainActivity", "sms manager");
                                SmsManager smsManager = SmsManager.getDefault();
                                smsManager.sendTextMessage(finalList.get(i).get("mobile_no"), null, strMsg, null, null);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        if (isEmailChecked) {
                            if (cd.isConnectingToInternet()) {
                                try {
                                    HashMap<String, String> map = new HashMap<String, String>();
                                    map = sessionManager.getSetting();

                                    if (map.get("gmail").length() > 0 && map.get("password").length() > 0) {

                                        //todo  change below customerinfo to the name from settings menu

                                        map = sessionManager.getSetting();
                                        String name = map.get("name");

                                        String subject = "Notification From  "+ name;

                                       // GMailSender sender = new GMailSender(map.get("gmail"), map.get("password"));
                                       // sender.sendMail(subject, strMsg, map.get("gmail"), finalList.get(i).get("email_id"));
                                       // Log.e("MainActivity", "sending email");
                                    }

                                } catch (Exception e) {
                                    Log.e("SendMail", e.getMessage(), e);
                                }
                            }
                        }

                    }
                }


            } catch (Exception e) {

            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
            if (pDialog != null) {
                if (pDialog.isShowing()) {
                    pDialog.dismiss();
                }
            }

        }
    }

    ProgressDialog customerProgress;

    public class CustomerList extends AsyncTask<Void,Void,ArrayList<HashMap<String, String>>>{

        @Override
        protected ArrayList<HashMap<String, String>> doInBackground(Void... voids) {

            return db.getCustList(parseObjectId);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            customerProgress = new ProgressDialog(MainActivity.this, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
            customerProgress.setCancelable(false);
            customerProgress.setIndeterminate(false);
            customerProgress.setMessage("Please Wait...");
            customerProgress.setTitle("Fetching  Contacts...");
            customerProgress.show();


        }

        @Override
        protected void onPostExecute(ArrayList<HashMap<String, String>> tempCustList) {
            super.onPostExecute(custList);
            if(customerProgress.isShowing())
                customerProgress.dismiss();
            // return the list
            //fetchedCustlist = hashMaps;
            custList = tempCustList;
            if (custList != null) {
                if (custList.size() > 0) {
                    customerListAdapter = new CustomerListAdapter(MainActivity.this, custList);
                    listView.setAdapter(customerListAdapter);
                }
            }else {
                Toast.makeText(MainActivity.this, "Customer list is empty", Toast.LENGTH_SHORT).show();
            }


        }
    }

    Boolean exit = false;

    @Override
    public void onBackPressed() {
        if (exit) {
            // finish(); // finish activity
            super.onBackPressed();
            return;
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 2000);

        }

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
