package com.ioter.swingu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ioter.R;

import java.util.ArrayList;

public class AdapterScanner extends ArrayAdapter<ClassScanner>
{
    //ÇÑ¹ø »ý¼ºµÇ¸é º¯ÇÏÁö ¾Êµµ·Ï finalÀ» ºÙÀÎ´Ù.
    private final Context context;
    private final ArrayList<ClassScanner> deviceArrayList;
    //»ý¼ºÀÚ ¼±¾ð
    public AdapterScanner(Context context, ArrayList<ClassScanner> deviceArrayList) {

        super(context, R.layout.list_scanner, deviceArrayList);

        this.context = context;
        this.deviceArrayList = deviceArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // 1. Create inflater 
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 2. Get rowView from inflater

        View rowView = null;
        rowView = inflater.inflate(R.layout.list_scanner, parent, false);

        // 3. Get icon,title & counter views from the rowView
        ImageView device_icon = (ImageView) rowView.findViewById(R.id.dvIcon);
        TextView device_name = (TextView) rowView.findViewById(R.id.dvName);
        TextView device_mac = (TextView) rowView.findViewById(R.id.dvMac);
        ImageView device_type = (ImageView) rowView.findViewById(R.id.dvType);

        // 4. Set the text for textView 
        device_icon.setImageResource(deviceArrayList.get(position).getIcon());
        device_name.setText(deviceArrayList.get(position).getName());
        device_mac.setText(deviceArrayList.get(position).getMac());
        device_type.setImageResource(deviceArrayList.get(position).getType());

        // 5. retrn rowView
        return rowView;
    }

}

