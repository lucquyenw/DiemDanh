package com.example.pc.attendance.viewModels.Helpers;

import android.support.v7.widget.RecyclerView;

import com.example.pc.attendance.databinding.StudentDataBinding;
import com.example.pc.attendance.viewModels.StudentVM;

/**
 * Created by azaudio on 5/25/2018.
 */

public class StudentViewHolder extends RecyclerView.ViewHolder {
    private StudentDataBinding studentDataBinding;

    public StudentViewHolder(StudentDataBinding dataBinding) {
        super(dataBinding.getRoot());
        this.studentDataBinding = dataBinding;
    }

    public void bind(StudentVM viewModel){
        this.studentDataBinding.setStudent(viewModel);
    }

    public StudentDataBinding getClassDataBinding(){
        return this.studentDataBinding;
    }
}
