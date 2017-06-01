package com.example.maxx.test2.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.util.List;

/**
 * Created by MAXX on 01.06.2017.
 */

public class RetainedFragment extends Fragment {
    private List<String> data;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment
        setRetainInstance(true);
    }

    public void setData(List<String> data) {
        this.data = data;
    }

    public List<String> getData() {
        return data;
    }
}
