package com.example.pc.attendance.viewModels.Converters;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.pc.attendance.R;
import com.example.pc.attendance.models.OMClass;

import java.util.ArrayList;

/**
 * Created by azaudio on 7/6/2018.
 */

public class OMClassAdapter extends ArrayAdapter<OMClass> {

    private  LayoutInflater mInflater;
    private  Context mContext;
    private  ArrayList<OMClass> data=new ArrayList<>();
    private  int mResource;


    public OMClassAdapter(@NonNull Context context, int resource, @NonNull ArrayList<OMClass> objects) {
        super(context, resource,0, objects);

        this.mContext=context;
        mInflater=LayoutInflater.from(context);
        this.mResource=resource;
        this.data=objects;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    private View createItemView(int position, View convertView, ViewGroup parent){
        final View view = mInflater.inflate(mResource, parent, false);

        TextView tvOMTemp = (TextView) view.findViewById(R.id.tvOMTemp);


        OMClass omClass = data.get(position);

        tvOMTemp.setText(omClass.getTemp());


        return view;
    }




}
