package com.example.pc.attendance.viewModels.Interfaces;

import android.content.Context;

/**
 * Created by PC on 15/02/2018.
 */

public interface IConnectionVMContract {
    interface MainView{
        void loadData(String url);
    }

    interface ViewModel{
        void destroy();
    }
}
