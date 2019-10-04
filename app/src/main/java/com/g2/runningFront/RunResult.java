package com.g2.runningFront;


import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class RunResult extends Fragment {

    private Activity activity;
    private TextView tvTime,tvDistance,tvCalories,tvSpeed;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle("運動結果");
        return inflater.inflate(R.layout.fragment_run_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvTime = view.findViewById(R.id.tvTime);
        tvCalories = view.findViewById(R.id.tvCalories);
        tvDistance = view.findViewById(R.id.tvDistance);
        tvSpeed = view.findViewById(R.id.tvSpeed);

    }
}
