package com.example.memo_test;


import android.app.Activity;
import android.app.LauncherActivity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MyListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<ListItem> data = null;
    private int resource;

    public MyListAdapter(Context context, ArrayList<ListItem> data, int resource){
        this.context = context;
        this.data = data;
        this.resource = resource;
    }

    public int getCount(){
        return data.size();
    }

    public Object getItem(int position){
        return data.get(position);
    }

    public String getUUID(int position) {
        return data.get(position).getUuid();
    }


    public long getItemId(int position){
        return data.get(position).getId();
    }

    public View getView(int position, View convertView, ViewGroup parent){
        Activity activity = (Activity) context;
        ListItem item = (ListItem)getItem(position);
        if(convertView == null){
            convertView = activity.getLayoutInflater().inflate(resource,null);
        }
        ((TextView) convertView.findViewById(R.id.body)).setText(item.getBody());
        ((TextView) convertView.findViewById(R.id.uuid)).setText(item.getUuid());
        return convertView;
    }

    public void add(ListItem item) {
        data.add(item);
    }

    public void update(int position, ListItem data) {
        ListItem item = (ListItem) getItem(position);
        item.setUuid(data.getUuid());
        item.setBody(data.getBody());
        notifyDataSetChanged();
    }
}
