package com.g2.runningFront.ShopActivity;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.g2.runningFront.R;
import com.g2.runningFront.RunActivity.MainActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShopServerFragment extends Fragment {


    public ShopServerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shop_server, container, false);
    }


}
