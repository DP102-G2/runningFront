package com.g2.runningFront.SettingActivity;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;

import android.util.Log;// 除錯用
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.g2.runningFront.R;// res 目錄
import static android.view.View.GONE;/// UI

public class SettingMainFragment extends Fragment {
    private static final String TAG = "TAG_SETmain";
    private Activity activity;
    private TextView textView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity.setTitle("登入頁面");
        return inflater.inflate(R.layout.fragment_setting_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated");

        view.findViewById(R.id.btSignOut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setVisibility(GONE);
            }
        });
    }
}