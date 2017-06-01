package com.example.maxx.test2.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.util.List;

/**
 * Created by MAXX on 01.06.2017.
 */

public class RetainedFragmentU extends Fragment {
    private List<String> dataU;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment
        setRetainInstance(true);
    }

    public void setData(List<String> data) {
        this.dataU = data;
    }

    public List<String> getData() {
        return dataU;
    }
}
