package com.example.pc.attendance.viewModels.Helpers;

import android.support.v7.widget.RecyclerView;

import com.example.pc.attendance.databinding.ClassDataBinding;
import com.example.pc.attendance.viewModels.MyClassVM;

/**
 * Created by PC on 19/03/2018.
 */

public class ClassViewHolder extends RecyclerView.ViewHolder {

    private ClassDataBinding classDataBinding;

    public ClassViewHolder(ClassDataBinding dataBinding) {
        super(dataBinding.getRoot());
        this.classDataBinding = dataBinding;
    }

    public void bind(MyClassVM viewModel){
        this.classDataBinding.setViewModel1(viewModel);
    }

    public ClassDataBinding getClassDataBinding(){
        return this.classDataBinding;
    }
}
