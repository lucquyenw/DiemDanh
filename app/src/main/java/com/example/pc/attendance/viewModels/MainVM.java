package com.example.pc.attendance.viewModels;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.ObservableArrayList;
import android.view.View;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Created by PC on 25/02/2018.
 */

public class MainVM extends BaseObservable {

    private static final String TAG = "Main VM";

    private Context context;
    private ObservableArrayList<String> items;
    private Stack<Integer> fragmentBackStacks = new Stack<>();


    public MainVM(Context context){
        this.context = context;

        items = new ObservableArrayList<String>();
        items.add("Truong Hong Phat");
        items.add("Truong Hong Phat");
        items.add("Truong Hong Phat");
        items.add("Truong Hong Phat");
        items.add("Truong Hong Phat");
    }

    @Bindable
    public ObservableArrayList<String> getItems() {
        return items;
    }

    public void setItems(ObservableArrayList<String> items) {
        this.items = items;
    }

    public int popFragmentBackStacks() {
        if(!fragmentBackStacks.isEmpty())
            return fragmentBackStacks.pop();
        return -1;
    }

    public void addFragmentBackStacks(int fragmentId){
        fragmentBackStacks.add(fragmentId);
    }
}
