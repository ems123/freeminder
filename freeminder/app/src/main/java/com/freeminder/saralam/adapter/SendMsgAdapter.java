package com.freeminder.saralam.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import com.freeminder.saralam.R;
import com.freeminder.saralam.utils.CustomerModel;

/**
 * Created by Sasikumar Reddy on 07-09-2015.
 */
public class SendMsgAdapter extends ArrayAdapter<CustomerModel> {

    private static LayoutInflater inflater = null;
    int textViewResourceId;
    Context context;
    public CustomerModel[]  customerList;

    static class SendMsgAdapterHolder{

        CheckBox chkbox;
    }

    public SendMsgAdapter(Context context, int textViewResourceId, CustomerModel[] customerList) {
        super(context, textViewResourceId, customerList);
        this.textViewResourceId =textViewResourceId;
        this.context = context;
        this.customerList = customerList; // contactDataItems
    }


    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        SendMsgAdapterHolder adapterHolder = null;

        if (view == null) {
            inflater = ((Activity)context).getLayoutInflater();
            view = inflater.inflate(textViewResourceId, parent, false);

            adapterHolder = new SendMsgAdapterHolder();
            adapterHolder.chkbox = (CheckBox) view.findViewById(R.id.check_send);

            view.setTag(adapterHolder);

            adapterHolder.chkbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CheckBox cb = (CheckBox) view;
                    CustomerModel custModel = (CustomerModel) cb.getTag();
                    custModel.setChecked(cb.isChecked());
                }
            });

        }
        else
        {
            adapterHolder = (SendMsgAdapterHolder)view.getTag();
        }

        try {

            final CustomerModel custModel = customerList[position];
            adapterHolder.chkbox.setText(custModel.getName());
            adapterHolder.chkbox.setChecked(custModel.isChecked());
            adapterHolder.chkbox.setTag(custModel);

        }catch(Exception e){
            e.printStackTrace();

        }


        return view;
    }



}
