package com.example.pc.attendance.viewModels.Converters;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.pc.attendance.R;

import java.util.ArrayList;

/**
 * Created by azaudio on 6/16/2018.
 */

public class DateAdapter extends ArrayAdapter<String> {
    Context context;
    ArrayList<String> data;
    LayoutInflater inflater;
    public DateAdapter(@NonNull Context context, int resource, ArrayList<String> objects) {
        super(context, resource, objects);
        this.context=context;
        this.data=objects;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view=LayoutInflater.from(context).inflate(R.layout.spinner_dropdown_item,null);
        TextView tvDate=view.findViewById(R.id.checkedDate);
        tvDate.setText(data.get(position));

        return view;
    }
}
