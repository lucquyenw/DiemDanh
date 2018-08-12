package com.example.pc.attendance.viewModels.Converters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.pc.attendance.databinding.SubjectDataBinding;
import com.example.pc.attendance.models.Subject;
import com.example.pc.attendance.viewModels.Helpers.SubjectViewHolder;
import com.example.pc.attendance.viewModels.SubjectVM;

import java.util.ArrayList;

/**
 * Created by PC on 19/03/2018.
 */

public class SubjectRecyclerViewAdapter extends RecyclerView.Adapter<SubjectViewHolder> {

    private Context context;
    private ArrayList<Subject> subjects;
    private LayoutInflater inflater;

    public SubjectRecyclerViewAdapter(Context context, ArrayList<Subject> subjects) {
        this.context = context;
        this.subjects = subjects;
    }

    @Override
    public SubjectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(inflater == null){
            inflater = LayoutInflater.from(parent.getContext());
        }

        SubjectDataBinding dataBinding = SubjectDataBinding.inflate(inflater, parent, false);
        return new SubjectViewHolder(dataBinding);
    }

    @Override
    public void onBindViewHolder(SubjectViewHolder holder, int position) {
        Subject subject = subjects.get(position);
        SubjectVM subjectVM = new SubjectVM(context, subject);

        holder.bind(subjectVM);
    }

    @Override
    public int getItemCount() {
        return subjects.size();
    }
}
