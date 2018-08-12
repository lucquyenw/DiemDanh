package com.example.pc.attendance.viewModels.Converters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.pc.attendance.databinding.StudentDataBinding;
import com.example.pc.attendance.models.Student;
import com.example.pc.attendance.viewModels.Helpers.StudentViewHolder;
import com.example.pc.attendance.viewModels.StudentVM;

import java.util.ArrayList;

/**
 * Created by azaudio on 5/25/2018.
 */

public class StudentRecylerViewAdapter extends RecyclerView.Adapter<StudentViewHolder> {

    private Context context;
    private ArrayList<Student> students;
    private LayoutInflater inflater;


    public StudentRecylerViewAdapter(Context context, ArrayList<Student> students) {
        this.context = context;
        this.students = students;
    }

    @Override
    public StudentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(inflater == null){
            inflater = LayoutInflater.from(parent.getContext());
        }

        StudentDataBinding dataBinding = StudentDataBinding.inflate(inflater, parent, false);
        return new StudentViewHolder(dataBinding);
    }

    @Override
    public void onBindViewHolder(StudentViewHolder holder, int position) {
        Student student = students.get(position);
        StudentVM studentVM = new StudentVM(context, student);

        holder.bind(studentVM);
    }

    @Override
    public int getItemCount() {
        return students.size();
    }
}
