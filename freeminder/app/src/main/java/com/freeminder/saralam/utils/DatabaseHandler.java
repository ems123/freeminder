package com.freeminder.saralam.utils;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Sasikumar Reddy on 07-09-2015.
 */
public class DatabaseHandler {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "CustomerInfo";

    // Table name
    private static final String TABLE_CUSTOMER_DETAIL = "customer_detail";
    private static final String TABLE_CUSTOMER_ACTION = "customer_action";
    private static final String TABLE_PROCESS_RUN = "process_run";

    // Note Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_MOBILE_NO = "mobile_no";
    private static final String KEY_EMAIL = "email_id";
    private static final String KEY_SERVICE = "service";

    private static final String KEY_ACTION_ID = "id";
    private static final String KEY_CUST_ID = "cust_id";
    private static final String KEY_ACTION_NAME = "action_name";
    private static final String KEY_ACTION_EMAIL = "email";
    private static final String KEY_ACTION_SMS = "sms";
    private static final String KEY_ACTION_VOICE = "voice";
    private static final String KEY_ACTION_CONTENT = "content";
    private static final String KEY_ACTION_SINCE = "since";
    private static final String KEY_ACTION_UNTIL = "until";
    private static final String KEY_ACTION_FREQUENCY = "frequency";
    private static final String KEY_RUN_SAVE = "run_on_save";
    private static final String KEY_NEXT_RUN = "next_run";
    private static final String KEY_DOM = "dom";

    private static final String KEY_RUN_DATE = "run_date";
    private static final String KEY_RUN_START = "run_start";
    private static final String KEY_RUN_END = "run_end";
    private int dayom;


    /*public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }*/
/*

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        String CREATE_CUSTOMER_TABLE = "CREATE TABLE " + TABLE_CUSTOMER_DETAIL + "("
                + KEY_ID + " INTEGER PRIMARY KEY, " + KEY_NAME + " TEXT NOT NULL, " + KEY_EMAIL + " TEXT NOT NULL, " + KEY_MOBILE_NO + " TEXT NOT NULL, " + KEY_SERVICE + " TEXT NOT NULL" + ")";
        db.execSQL(CREATE_CUSTOMER_TABLE);

        String CREATE_ACTION_TABLE = "CREATE TABLE " + TABLE_CUSTOMER_ACTION + "("
                + KEY_ACTION_ID + " INTEGER PRIMARY KEY, " + KEY_CUST_ID + " TEXT NOT NULL, " + KEY_ACTION_NAME + " TEXT NOT NULL, "
                + KEY_ACTION_EMAIL + " TEXT NOT NULL, " + KEY_ACTION_SMS + " TEXT NOT NULL, " + KEY_ACTION_VOICE + " TEXT NOT NULL, "
                + KEY_ACTION_CONTENT + " TEXT NOT NULL, " + KEY_ACTION_SINCE + " TEXT NOT NULL, " + KEY_ACTION_UNTIL + " TEXT, "
                + KEY_ACTION_FREQUENCY + " TEXT NOT NULL, " + KEY_RUN_SAVE + " TEXT NOT NULL, " + KEY_SERVICE + " TEXT NOT NULL, " + KEY_NEXT_RUN + " TEXT, " + KEY_DOM + " INTEGER " + ")";
        db.execSQL(CREATE_ACTION_TABLE);

        String CREATE_PROCESS_RUN_TABLE = "CREATE TABLE " + TABLE_PROCESS_RUN + "("
                + KEY_ID + " INTEGER PRIMARY KEY, " + KEY_RUN_DATE + " TEXT NOT NULL, " + KEY_RUN_START + " TEXT NOT NULL, "
                + KEY_RUN_END + " TEXT NOT NULL" + ")";
        db.execSQL(CREATE_PROCESS_RUN_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CUSTOMER_DETAIL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CUSTOMER_ACTION);
        // Create tables again
        onCreate(db);
    }
*/

    /**
     * Add new customer to list
     */
    public void addCustomerToList(String name, String emailid, String mo_no, String service, String parseObjectId) {
/*

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_EMAIL, emailid);
        values.put(KEY_MOBILE_NO, mo_no);
        values.put(KEY_SERVICE, service);

        // Inserting Row
        db.insert(TABLE_CUSTOMER_DETAIL, null, values);
        db.close(); // Closing database connection
*/

        ParseObject customerParseList = new ParseObject("CustomerDetails");
        customerParseList.put("name", name);
        customerParseList.put("email", emailid);
        customerParseList.put("mobile", mo_no);
        customerParseList.put("service", service);
        customerParseList.put("parentId", parseObjectId);
        customerParseList.saveEventually();


    }

    /**
     * Add new Action to Customer
     */
    public void addActionToList(String cust_id, String name, String email, String sms, String voice, String content,
                                String since, String until, String frequency, String run_save, String service, String next_run, String dom, String parseObjectId) {

   /*     SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_CUST_ID, cust_id);
        values.put(KEY_ACTION_NAME, name);
        values.put(KEY_ACTION_EMAIL, email);
        values.put(KEY_ACTION_SMS, sms);
        values.put(KEY_ACTION_VOICE, voice);
        values.put(KEY_ACTION_CONTENT, content);
        values.put(KEY_ACTION_SINCE, since);
        values.put(KEY_ACTION_UNTIL, until);
        values.put(KEY_ACTION_FREQUENCY, frequency);
        values.put(KEY_RUN_SAVE, run_save);
        values.put(KEY_SERVICE, service);
        values.put(KEY_NEXT_RUN, next_run);
        values.put(KEY_DOM, dom);

        // Inserting Row
        long actionId = db.insert(TABLE_CUSTOMER_ACTION, null, values);
        db.close(); // Closing database connection
*/
        // parse actionList update to cloud
        ParseObject actionListParseObject = new ParseObject("CustomerAction");
        //actionListParseObject.put("actionid",actionId);
        actionListParseObject.put("customerid", "0");
        actionListParseObject.put("actionname", name);
        actionListParseObject.put("email", email);
        actionListParseObject.put("sms", sms);
        actionListParseObject.put("voice", voice);
        actionListParseObject.put("content", content);
        actionListParseObject.put("actionsince", since);
        actionListParseObject.put("actionuntil", until);
        actionListParseObject.put("actionfrequency", frequency);
        actionListParseObject.put("runonsave", run_save);
        actionListParseObject.put("service", service);
        actionListParseObject.put("nextrun", "");
        actionListParseObject.put("dom", dom);
        actionListParseObject.put("parentId", parseObjectId);
        actionListParseObject.saveEventually();


    }

    /**
     * get customer from list
     */
  /*  public ArrayList<HashMap<String, String>> getCustList() {
        ArrayList<HashMap<String, String>> custList = new ArrayList<HashMap<String, String>>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CUSTOMER_DETAIL, null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> tmpMap = new HashMap<String, String>();
                tmpMap.put(KEY_ID, cursor.getString(0));
                tmpMap.put(KEY_NAME, cursor.getString(1));
                tmpMap.put(KEY_EMAIL, cursor.getString(2));
                tmpMap.put(KEY_MOBILE_NO, cursor.getString(3));
                tmpMap.put(KEY_SERVICE, cursor.getString(4));
                tmpMap.put("checked", "true");

                custList.add(tmpMap);
            } while (cursor.moveToNext());
        }

        db.close();
        return custList;
    }
*/
    public long createProcessRun(String runDate, String startDate, String parseObjectId) {

  /*      long processId;
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_RUN_DATE, runDate);
        values.put(KEY_RUN_START, startDate);
        values.put(KEY_RUN_END, "");

        // Inserting Row
        processId = db.insert(TABLE_PROCESS_RUN, null, values);
        db.close(); // Closing database connection
*/
        //parse object to update the process run

        ParseObject parseProcessRun = new ParseObject("ProcessRun");
        parseProcessRun.put("processid", "");    // TODO need to update the processid
        parseProcessRun.put("rundate", runDate);
        parseProcessRun.put("runstart", startDate);
        parseProcessRun.put("runend", "");
        parseProcessRun.put("parentId", parseObjectId);
        parseProcessRun.saveEventually();


        return new Long("10");
    }

  /*  public ArrayList<HashMap<String, String>> getSysActionList(int dom) {

        String cust_id = "0";

        ArrayList<HashMap<String, String>> actionList = new ArrayList<HashMap<String, String>>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CUSTOMER_ACTION + " WHERE " + KEY_CUST_ID + "='" + cust_id + "'" + " AND " + KEY_DOM + "= " + dom, null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> tmpMap = new HashMap<String, String>();
                tmpMap.put(KEY_ACTION_ID, cursor.getString(0));
                tmpMap.put(KEY_CUST_ID, cursor.getString(1));
                tmpMap.put(KEY_ACTION_NAME, cursor.getString(2));
                tmpMap.put(KEY_ACTION_EMAIL, cursor.getString(3));
                tmpMap.put(KEY_ACTION_SMS, cursor.getString(4));
                tmpMap.put(KEY_ACTION_VOICE, cursor.getString(5));
                tmpMap.put(KEY_ACTION_CONTENT, cursor.getString(6));
                tmpMap.put(KEY_ACTION_SINCE, cursor.getString(7));
                tmpMap.put(KEY_ACTION_UNTIL, cursor.getString(8));
                tmpMap.put(KEY_ACTION_FREQUENCY, cursor.getString(9));
                tmpMap.put(KEY_RUN_SAVE, cursor.getString(10));
                tmpMap.put(KEY_SERVICE, cursor.getString(11));
                tmpMap.put(KEY_NEXT_RUN, cursor.getString(12));
                tmpMap.put(KEY_DOM, cursor.getString(13));


                actionList.add(tmpMap);
            } while (cursor.moveToNext());
        }


        db.close();
        return actionList;
    }
*/

    public void updateProcessRun(String key_id, String runDate, String startDate, String endDate, String parseObjectId) {

  /*      SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_RUN_DATE, runDate);
        values.put(KEY_RUN_START, startDate);
        values.put(KEY_RUN_END, endDate);


        // updating row
        int returnInt = db.update(TABLE_PROCESS_RUN, values, KEY_ID + "='" + key_id + "'", null);

        db.close();
*/
        // TODO incomplete

        //update column based on processid

        ParseQuery<ParseObject> updateQuery = ParseQuery.getQuery("ProcessRun");
        updateQuery.whereEqualTo("processid", key_id);

        updateQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, com.parse.ParseException e) {

                if (e == null) {

                    Log.d("DBHandler.java", "parseobjectlist :" + list);
                    //need to process the list
                }

            }
        });


        // return new int[];

    }


   /* public int updateCustActionNextRun(String key_id, String nextRun) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NEXT_RUN, nextRun);

        // updating row
        int returnInt = db.update(TABLE_CUSTOMER_ACTION, values, KEY_ID + "='" + key_id + "'", null);

        db.close();
        return returnInt;

    }
*/

    /**
     * get action from list
     */
/*
    public ArrayList<HashMap<String, String>> getActionList(String cust_id, int dom) {
        ArrayList<HashMap<String, String>> actionList = new ArrayList<HashMap<String, String>>();
        SQLiteDatabase db = this.getReadableDatabase();

        //SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        //Date currentDate = new Date();
        //String today = formatter.format(currentDate);


        // Log.d("db handler.."," current date :"+today);
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CUSTOMER_ACTION + " WHERE " + KEY_CUST_ID + "='" + cust_id + "'" + " AND " + KEY_DOM + "=" + dom, null);


        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> tmpMap = new HashMap<String, String>();
                tmpMap.put(KEY_ACTION_ID, cursor.getString(0));
                tmpMap.put(KEY_CUST_ID, cursor.getString(1));
                tmpMap.put(KEY_ACTION_NAME, cursor.getString(2));
                tmpMap.put(KEY_ACTION_EMAIL, cursor.getString(3));
                tmpMap.put(KEY_ACTION_SMS, cursor.getString(4));
                tmpMap.put(KEY_ACTION_VOICE, cursor.getString(5));
                tmpMap.put(KEY_ACTION_CONTENT, cursor.getString(6));
                tmpMap.put(KEY_ACTION_SINCE, cursor.getString(7));
                tmpMap.put(KEY_ACTION_UNTIL, cursor.getString(8));
                tmpMap.put(KEY_ACTION_FREQUENCY, cursor.getString(9));
                tmpMap.put(KEY_RUN_SAVE, cursor.getString(10));
                tmpMap.put(KEY_SERVICE, cursor.getString(11));
                tmpMap.put(KEY_NEXT_RUN, cursor.getString(12));
                tmpMap.put(KEY_DOM, cursor.getString(13));

              */
/*  Log.d("db handler.."," since date :..");
               // Log.d("db handler.."," since date :.." +cursor.getString(7));
                try {
                    if(formatter.parse(today).after(formatter.parse(cursor.getString(7)))){

                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }*//*


                actionList.add(tmpMap);
            } while (cursor.moveToNext());
        }

        db.close();
        return actionList;
    }
     */
/*
     * gets all action
     * *//*


    public ArrayList<HashMap<String, String>> getAllActions(String cust_id) {
        ArrayList<HashMap<String, String>> actionList = new ArrayList<HashMap<String, String>>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CUSTOMER_ACTION + " WHERE " + KEY_CUST_ID + "=" + cust_id, null);


        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> tmpMap = new HashMap<String, String>();
                tmpMap.put(KEY_ACTION_ID, cursor.getString(0));
                tmpMap.put(KEY_CUST_ID, cursor.getString(1));
                tmpMap.put(KEY_ACTION_NAME, cursor.getString(2));
                tmpMap.put(KEY_ACTION_EMAIL, cursor.getString(3));
                tmpMap.put(KEY_ACTION_SMS, cursor.getString(4));
                tmpMap.put(KEY_ACTION_VOICE, cursor.getString(5));
                tmpMap.put(KEY_ACTION_CONTENT, cursor.getString(6));
                tmpMap.put(KEY_ACTION_SINCE, cursor.getString(7));
                tmpMap.put(KEY_ACTION_UNTIL, cursor.getString(8));
                tmpMap.put(KEY_ACTION_FREQUENCY, cursor.getString(9));
                tmpMap.put(KEY_RUN_SAVE, cursor.getString(10));
                tmpMap.put(KEY_SERVICE, cursor.getString(11));
                tmpMap.put(KEY_NEXT_RUN, cursor.getString(12));
                tmpMap.put(KEY_DOM, cursor.getString(13));

                actionList.add(tmpMap);
            } while (cursor.moveToNext());
        }

        db.close();
        return actionList;
    }


    */
/**
 * remove customer from list
 *//*

    public void removeCustomer(String key_id) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CUSTOMER_DETAIL, KEY_ID + "=" + key_id, null);
        db.close();
    }

    */
/**
 * remove action from list
 *//*

    public void removeAction(String key_id) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CUSTOMER_ACTION, KEY_ACTION_ID + "=" + key_id, null);
        db.close();
    }

    */
/**
 * update Customer table
 *//*

    public int updateCustDetail(String key_id, String name, String emailid, String mo_no, String service) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_EMAIL, emailid);
        values.put(KEY_MOBILE_NO, mo_no);
        values.put(KEY_SERVICE, service);

        // updating row
        int returnInt = db.update(TABLE_CUSTOMER_DETAIL, values, KEY_ID + "='" + key_id + "'", null);

        db.close();
        return returnInt;

    }
*/

    /**
     * update Action table
     */
/*    public int updateCustAction(String key_id, String cust_id, String name, String email, String sms, String voice, String content,
                                String since, String until, String frequency, String run_save, String service, String next_run, String dom) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_CUST_ID, cust_id);
        values.put(KEY_ACTION_NAME, name);
        values.put(KEY_ACTION_EMAIL, email);
        values.put(KEY_ACTION_SMS, sms);
        values.put(KEY_ACTION_VOICE, voice);
        values.put(KEY_ACTION_CONTENT, content);
        values.put(KEY_ACTION_SINCE, since);
        values.put(KEY_ACTION_UNTIL, until);
        values.put(KEY_ACTION_FREQUENCY, frequency);
        values.put(KEY_RUN_SAVE, run_save);
        values.put(KEY_SERVICE, service);
        values.put(KEY_NEXT_RUN, next_run);
        values.put(KEY_DOM, dom);

        // updating row
        int returnInt = db.update(TABLE_CUSTOMER_ACTION, values, KEY_ID + "='" + key_id + "'", null);

        db.close();
        return returnInt;

    }*/

/*
    public int getCustomerCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        int count = (int) DatabaseUtils.queryNumEntries(db, TABLE_CUSTOMER_DETAIL);
        return count;
    }


    public int getDOM(String custid) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT " + KEY_DOM + " FROM " + TABLE_CUSTOMER_ACTION + " WHERE " + KEY_CUST_ID + "=" + custid, null);

        if (cursor.moveToFirst()) {
            dayom = Integer.parseInt(cursor.getString(0));
        }

        return dayom;
    }

    public HashMap<String, String> getCustomer(String id) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CUSTOMER_DETAIL + " WHERE " + KEY_ID + "='" + id + "'", null);


        HashMap<String, String> tmpMap = new HashMap<String, String>();

        if (cursor.moveToFirst()) {
            do {
                tmpMap.put(KEY_ID, cursor.getString(0));
                tmpMap.put(KEY_NAME, cursor.getString(1));
                tmpMap.put(KEY_MOBILE_NO, cursor.getString(3));
                tmpMap.put(KEY_EMAIL, cursor.getString(2));
                tmpMap.put(KEY_SERVICE, cursor.getString(4));

            } while (cursor.moveToNext());
        }

        db.close();

        return tmpMap;
    }
*/


    public ArrayList<HashMap<String, String>> ParseActionList(String parseObjectId) {

        final List[] tempList = new ArrayList[1];

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("parentId", parseObjectId);

        List<ParseObject> ParseActionObjects = new ArrayList<ParseObject>();
        try {

            HashMap<String, Object> ActionObject = ParseCloud.callFunction("getActions", params);
            Log.d("DBHandler", " object:" + ActionObject);

            List<ParseObject> parseActionArray = (List<ParseObject>) ActionObject.get("actions");

            for(int i=0;i<parseActionArray.size() ;i++){

                ParseObject tempObject = parseActionArray.get(i);
                Log.d("DBHandler", " parseActionsarray count  , Action content and name :" + " " +i + " "+tempObject.get("Content") + " "+  tempObject.get("actionname") );
                //if(tempObject.isDataAvailable())

                ParseActionObjects.add(tempObject);
            }

        } catch (com.parse.ParseException e) {
            e.printStackTrace();
        }



        // Log.d("DBHandler","List object:" +actionList);
        ArrayList<HashMap<String, String>> actionArrayListReturn = new ArrayList<HashMap<String, String>>();


        Iterator<ParseObject> actionIterator = ParseActionObjects.iterator();

        while (actionIterator.hasNext()) {
            HashMap<String, String> tmpMap = new HashMap<String, String>();
            ParseObject action = actionIterator.next();


            tmpMap.put("id", action.getString("actionid"));
            tmpMap.put("cust_id", action.getString("customerid"));
            tmpMap.put("action_name", action.getString("actionname"));
            tmpMap.put("email", action.getString("email"));
            tmpMap.put("sms", action.getString("sms"));
            tmpMap.put("voice", action.getString("voice"));
            tmpMap.put("content", action.getString("content"));
            tmpMap.put("since", action.getString("actionsince"));
            tmpMap.put("until", action.getString("actionuntil"));
            tmpMap.put("frequency", action.getString("actionfrequency"));
            tmpMap.put("run_on_save", action.getString("runonsave"));
            tmpMap.put("service", action.getString("service"));
            tmpMap.put("next_run", action.getString("nextrun"));
            tmpMap.put("dom", action.getString("dom"));
            tmpMap.put("objectId",action.getObjectId());

            Log.d("DBHandler.java","hashmap Action name " + action.getString("actionname") +" is added to actionArrayList ");
            actionArrayListReturn.add(tmpMap);
        }


        return actionArrayListReturn;
    }


    public void updateCustomerAction(String key_id, String customerid, String name, String email, String sms, String voice, String content,
                                     String since, String until, String frequency, String run_save, String service, String next_run, String dom, String objectId) {

        final String customer_id = customerid;
        final String ActionName = name;
        final String AcEmail = email;
        final String AcSms = sms;
        final String AcVoice = voice;
        final String AcContent = content;
        final String AcSince = since;
        final String AcUntil = until;
        final String AcFrequency =frequency;
        final String AcRunSave = run_save;
        final String AcService = service;
        final String AcNextRun = next_run;
        final String AcDom = dom;

        Log.d("DBHandler.java","Object id for the action : "+objectId + " action name :"+ ActionName);
        ParseQuery<ParseObject> updateActionQuery = ParseQuery.getQuery("CustomerAction");

        updateActionQuery.getInBackground(objectId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject updateAction, com.parse.ParseException e) {

                updateAction.put("customerid", customer_id);
                updateAction.put("actionname", ActionName);
                updateAction.put("email", AcEmail);
                updateAction.put("sms", AcSms);
                updateAction.put("voice", AcVoice);
                updateAction.put("content", AcContent);
                updateAction.put("actionsince", AcSince);
                updateAction.put("actionuntil", AcUntil);
                updateAction.put("actionfrequency", AcFrequency);
                updateAction.put("runonsave", AcRunSave);
                updateAction.put("service", AcService);
                updateAction.put("nextrun", AcNextRun);
                updateAction.put("dom", AcDom);

                updateAction.saveEventually();
            }
        });


    }

    public void updateCustomer(String objectId, String name,String email,String mobile,String Service){

        final String custname = name;
        final String custemail =email;
        final String custmobile = mobile;
        final String custservice = Service;

        final ParseQuery<ParseObject> updateCustomerQuery = ParseQuery.getQuery("CustomerDetails");

        updateCustomerQuery.getInBackground(objectId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {

                if(e == null) {

                    parseObject.put("name", custname);
                    parseObject.put("email", custemail);
                    parseObject.put("mobile", custmobile);
                    parseObject.put("service", custservice);

                    parseObject.saveEventually();
                }


            }
        });

    }




    // Runs in the back ground and doesn't need Synchronous Task.
    // Called in UpdatingService.java
    public ArrayList<HashMap<String, String>> getSysActionList(int dom,String parentId) {

        String cust_id = "0";

        ArrayList<HashMap<String, String>> actionList = new ArrayList<HashMap<String, String>>();



        final ArrayList<List> tmpActionList = new ArrayList<List>();
        ParseQuery<ParseObject> sysActionList = ParseQuery.getQuery("CustomerAction");
        sysActionList.whereEqualTo("parentId", parentId);
        sysActionList.whereEqualTo("dom", dom);
        sysActionList.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {

                    tmpActionList.add(0, list);
                    Log.d("DBHandler.java","database handler actionlist :" +list);
                }

            }
        });


        List<ParseObject> ActionList = new ArrayList<ParseObject>();
        ActionList = tmpActionList.get(0);

        Iterator actionIterator = ActionList.iterator();

        while (actionIterator.hasNext()) {
            HashMap<String, String> tmpMap = new HashMap<String, String>();

            ParseObject action = (ParseObject) actionIterator.next();

            tmpMap.put("id", action.getString("actionid"));
            tmpMap.put("cust_id", action.getString("customerid"));
            tmpMap.put("action_name", action.getString("actionname"));
            tmpMap.put("email", action.getString("email"));
            tmpMap.put("sms", action.getString("sms"));
            tmpMap.put("voice", action.getString("voice"));
            tmpMap.put("content", action.getString("content"));
            tmpMap.put("since", action.getString("actionsince"));
            tmpMap.put("until", action.getString("actionuntil"));
            tmpMap.put("frequency", action.getString("actionfrequency"));
            tmpMap.put("run_on_save", action.getString("runonsave"));
            tmpMap.put("service", action.getString("service"));
            tmpMap.put("next_run", action.getString("nextrun"));
            tmpMap.put("dom", action.getString("dom"));
            tmpMap.put("objectId",action.getObjectId());

            Log.d("DBHandler.java","hashmap Action name " + action.getString("actionname") +" is added to actionArrayList ");
            actionList.add(tmpMap);
        }

        return actionList;
    }


    public ArrayList<HashMap<String, String>> getCustList(String parentId) {
        ArrayList<HashMap<String, String>> custList = new ArrayList<HashMap<String, String>>();

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("parentId", parentId);

        List<ParseObject> ParseCustomerObjects = new ArrayList<ParseObject>();
        try {

            HashMap<String, Object> ActionObject = ParseCloud.callFunction("getCustomers", params);
            Log.d("DBHandler", " object:" + ActionObject);

            List<ParseObject> parseCustomerArray = (List<ParseObject>) ActionObject.get("customers");

            for(int i=0;i<parseCustomerArray.size() ;i++){

                ParseObject tempObject = parseCustomerArray.get(i);
                Log.d("DBHandler", " parsecustomersarray count  , Action content and name :" + " " +i + " "+tempObject.get("name") + " "+  tempObject.get("mobile") );

                ParseCustomerObjects.add(tempObject);
            }


        }catch(Exception e){
            e.printStackTrace();
        }


        Iterator CustIterator = ParseCustomerObjects.iterator();

        while (CustIterator.hasNext()){
            ParseObject custdetail =(ParseObject) CustIterator.next();

            HashMap<String, String> tmpMap = new HashMap<String, String>();
            tmpMap.put(KEY_ID, custdetail.getObjectId());
            tmpMap.put(KEY_NAME, custdetail.getString("name"));
            tmpMap.put(KEY_EMAIL, custdetail.getString("email"));
            tmpMap.put(KEY_MOBILE_NO, custdetail.getString("mobile"));
            tmpMap.put(KEY_SERVICE, custdetail.getString("service"));
            tmpMap.put("checked", "true");
            Log.d("DBHandler","objectID :"+custdetail.getObjectId());
            custList.add(tmpMap);

        }

        return custList;
    }

    public void deleteCustomer(String objectId) {

        //String deleteStatus = "false";
        HashMap<String, Object> params = new HashMap<String, Object>();
        Log.d("DBHandler","objectId :"+ objectId);
        params.put("customerId", objectId);

        String deleteStatus = null;
        try {

            HashMap<String, Object> deleteMsg = ParseCloud.callFunction("deleteCustomer", params);
            deleteStatus = (String) deleteMsg.get("status");
            Log.d("dbHandler.java","delete status :" +deleteMsg.get("status"));
        } catch (Exception e) {
            e.printStackTrace();
        }

      /*  if (deleteStatus.equals("success")) {
            return true;
        } else {
            return false;
        }
*/

    }


    public void removeAction(String objectId) {

        //String deleteStatus = "false";
        HashMap<String, Object> params = new HashMap<String, Object>();
        Log.d("DBHandler","objectId :"+ objectId);
        params.put("objectId", objectId);

        String deleteStatus = null;
        try {

            HashMap<String, Object> deleteMsg = ParseCloud.callFunction("removeAction", params);
            deleteStatus = (String) deleteMsg.get("status");
            Log.d("dbHandler.java","delete status :" +deleteMsg.get("status") +" "+ deleteStatus);
        } catch (Exception e) {
            e.printStackTrace();
        }



    }

}
