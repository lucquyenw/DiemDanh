package com.example.pc.attendance.views;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.pc.attendance.R;
import com.example.pc.attendance.databinding.ActivityLoginBinding;
import com.example.pc.attendance.databinding.SearchToolbarBinding;
import com.example.pc.attendance.viewModels.LoginVM;

public class LoginActivity extends AppCompatActivity {

    private LoginVM loginVM;
    private ActivityLoginBinding binding;
    private SearchToolbarBinding searchToolbarBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        loginVM = new LoginVM(this);
        binding.setVm(loginVM);

    }
}
