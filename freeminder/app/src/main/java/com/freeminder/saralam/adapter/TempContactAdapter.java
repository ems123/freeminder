package com.freeminder.saralam.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.freeminder.saralam.R;
import com.freeminder.saralam.utils.TempModel;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Sasikumar Reddy on 07-09-2015.
 */
public class TempContactAdapter extends ArrayAdapter<TempModel> {

    public TempModel contactDataItems[] = null;
    Context context;
    Runnable runnable;
    private Activity activity;
    private ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();
    private static LayoutInflater inflater = null;
    private WindowManager.LayoutParams params;
    private WindowManager wm;
    private LayoutInflater inflaterDialog;
    int textViewResourceId;
    public ArrayList<HashMap<String, String>> custList = new ArrayList<HashMap<String,String>>();
    private static final String KEY_NAME = "name";
    private static final String KEY_MOBILE_NO = "mobile_no";
    private static final String KEY_EMAIL = "email_id";
    private static final String KEY_SERVICE = "service";



    static class TempContactAdapterHolder
    {

        TextView name;
        CheckBox checkBox;

    }

    public TempContactAdapter(Context context, int textViewResourceId, TempModel[] contactDataItems) {
        super(context, textViewResourceId, contactDataItems);
        this.textViewResourceId =textViewResourceId;
        this.context = context;
        this.contactDataItems = contactDataItems;
    }



    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        TempContactAdapterHolder contactHolder = null;

        if (view == null) {
            inflater = ((Activity)context).getLayoutInflater();
            view = inflater.inflate(textViewResourceId, parent, false);

            contactHolder = new TempContactAdapterHolder();
            contactHolder.name = (TextView) view.findViewById(R.id.contactName);
            contactHolder.checkBox = (CheckBox) view.findViewById(R.id.checkBox);
            view.setTag(contactHolder);

            contactHolder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CheckBox cb = (CheckBox) view;
                    TempModel tmpModel = (TempModel) cb.getTag();
                    tmpModel.setChecked(cb.isChecked());
                }
            });

        }
        else
        {
            contactHolder = (TempContactAdapterHolder)view.getTag();
        }

        try {

            final TempModel tempModel = contactDataItems[position];
            contactHolder.name.setText(tempModel.getName());
            contactHolder.checkBox.setChecked(tempModel.isIsChecked());
            contactHolder.checkBox.setTag(tempModel);

            contactHolder.checkBox.setVisibility(View.VISIBLE);
            contactHolder.name.setVisibility(View.VISIBLE);

        }catch(Exception e){
            e.printStackTrace();

        }

        return view;
    }




}
