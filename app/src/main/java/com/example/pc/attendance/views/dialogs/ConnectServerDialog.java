package com.example.pc.attendance.views.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;

import com.example.pc.attendance.R;
import com.example.pc.attendance.databinding.ConnectServerDialogBinding;
import com.example.pc.attendance.viewModels.ConnectionVM;

/**
 * Created by PC on 15/02/2018.
 */

public class ConnectServerDialog extends Dialog {

    private ConnectServerDialogBinding binding;
    private ConnectionVM connectionVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    public ConnectServerDialog(Context context) {
        super(context);
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.connect_server_dialog, null, false);
        setContentView(binding.getRoot());
        connectionVM = new ConnectionVM(this.getContext(), "ConnectServerDialog");
        binding.setViewModel(connectionVM);
        setTitle("Notice ");
    }
}
