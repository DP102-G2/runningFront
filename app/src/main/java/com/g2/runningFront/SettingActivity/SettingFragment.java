package com.g2.runningFront.SettingActivity;;


import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.g2.runningFront.Common.User;
import com.g2.runningFront.R;// res 目錄


public class SettingFragment extends Fragment {
    private static final String TAG = "TAG_SET";
    private Activity activity;
    private TextView textView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle("會員設定");
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textView = view.findViewById(R.id.textView);

        Bundle bundle = new Bundle();
        String url = (String) bundle.getSerializable("image");

        textView.setText("一般登入成功");

        // BALABALA

    }
}
