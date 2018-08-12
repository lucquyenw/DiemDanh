package com.example.pc.attendance.viewModels.Converters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pc.attendance.R;
import com.example.pc.attendance.helpers.FileHelper;
import com.example.pc.attendance.models.Student;
import com.example.pc.attendance.views.AddPersonActivity;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by azaudio on 6/22/2018.
 */

public class OStudentRecylerViewAdapter extends RecyclerView.Adapter<OStudentRecylerViewAdapter.OStudentViewHolder>{
    private ArrayList<Student> students=new ArrayList<>();
    private Context context;
    private String TAG="OStudentViewAdapter";

    public OStudentRecylerViewAdapter(Context context,ArrayList<Student> students){
        this.students=students;
        this.context=context;
        Log.d(TAG,"running");
    }

    @Override
    public OStudentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.ostudent_list_item,parent,false);
        Log.d(TAG,"running");
        return new OStudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OStudentViewHolder holder, final int position) {
        holder.tvId.setText("MSSV: "+students.get(position).getId());
        holder.tvFullName.setText(students.get(position).getFullname());
        Log.d("Ostudent",students.get(position).getId());
        holder.btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id=students.get(position).getId();
                Intent intent=new Intent(context,AddPersonActivity.class);
                intent.putExtra("id",id);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                if(isNameAlreadyUsed(new FileHelper().getTrainingList(),id)){
                    Toast.makeText(context,"Image of this ID is already captured",Toast.LENGTH_SHORT).show();
                }else{
                    intent.putExtra("Folder","Training");
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    public class OStudentViewHolder extends RecyclerView.ViewHolder{
        TextView tvId;
        TextView tvFullName;
        Button btnCapture;

        public OStudentViewHolder(View itemView) {
            super(itemView);
            tvId=(TextView) itemView.findViewById(R.id.tvStudentID);
            tvFullName=(TextView) itemView.findViewById(R.id.tvStudentFN);
            btnCapture=(Button) itemView.findViewById(R.id.btnCapture);
        }
    }

    private boolean isNameAlreadyUsed(File[] list,String name){
        boolean used=false;
        if(list!=null && list.length>0){
            for(File person:list){
                String[] tokens=person.getAbsolutePath().split("/");
                final String foldername=tokens[tokens.length-1];
                if(foldername.equals(name)){
                    used=true;
                    break;
                }
            }
        }
        return used;
    }
}
