package com.example.pc.attendance.viewModels.Converters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.pc.attendance.databinding.ClassDataBinding;
import com.example.pc.attendance.models.MyClass;
import com.example.pc.attendance.viewModels.Helpers.ClassViewHolder;
import com.example.pc.attendance.viewModels.MyClassVM;

import java.util.ArrayList;

/**
 * Created by PC on 19/03/2018.
 */

public class ClassRecyclerViewAdapter extends RecyclerView.Adapter<ClassViewHolder> {

    private Context context;
    private ArrayList<MyClass> classes;
    private LayoutInflater inflater;

    public ClassRecyclerViewAdapter(Context context, ArrayList<MyClass> classes) {
        this.context = context;
        this.classes = classes;
    }

    @Override
    public ClassViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(inflater == null){
            inflater = LayoutInflater.from(parent.getContext());
        }

        ClassDataBinding dataBinding = ClassDataBinding.inflate(inflater, parent, false);
        return new ClassViewHolder(dataBinding);
    }

    @Override
    public void onBindViewHolder(ClassViewHolder holder, int position) {
        MyClass mClass = classes.get(position);
        MyClassVM myClassVM = new MyClassVM(context, mClass);
        holder.bind(myClassVM);
    }

    @Override
    public int getItemCount() {
        return classes.size();
    }
}
