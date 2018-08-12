package com.example.pc.attendance.viewModels.Helpers;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.pc.attendance.databinding.SubjectDataBinding;
import com.example.pc.attendance.models.Subject;
import com.example.pc.attendance.viewModels.SubjectVM;

/**
 * Created by PC on 19/03/2018.
 */

public class SubjectViewHolder extends RecyclerView.ViewHolder {

    private SubjectDataBinding subjectDataBinding;

    public SubjectViewHolder(SubjectDataBinding dataBinding) {
        super(dataBinding.getRoot());
        this.subjectDataBinding = dataBinding;
    }

    public void bind(SubjectVM viewModel){
        this.subjectDataBinding.setViewModel(viewModel);
    }

    public SubjectDataBinding getSubjectDataBinding(){
        return this.subjectDataBinding;
    }
}
