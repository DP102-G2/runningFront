package com.g2.runningFront.RunActivity.Run;


import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.g2.runningFront.R;

import java.util.Timer;
import java.util.TimerTask;

public class RunStartFragment extends Fragment {

    private Activity activity;
    private Button runstart_btStart;
    private TextView runstart_tvTime,runstart_tvDistance,runstart_tvCalories,runstart_tvSpeed;
    long time;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle("開始運動");
        return inflater.inflate(R.layout.fragment_run_start, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        runstart_tvTime = view.findViewById(R.id.runstart_tvTime);
        runstart_tvCalories = view.findViewById(R.id.runstart_tvCalories);
        runstart_tvDistance = view.findViewById(R.id.runstart_tvDistance);
        runstart_tvSpeed = view.findViewById(R.id.runstart_tvSpeed);

        runstart_tvTime.setText("0:0:0");
        runstart_tvCalories.setText("0");
        runstart_tvDistance.setText("0.0");
        runstart_tvSpeed.setText("0.0");

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                time += 1;//累積的總秒數
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int seconds = ((int ) time)%60;
                        int minutes = (((int ) time)/60)%60;
                        int hours = ((int ) time)/3600;

                        runstart_tvTime.setText(hours+":"+minutes+":"+seconds);

                    }
                });
            }
        }, 0,1000);


        runstart_btStart = view.findViewById(R.id.runstart_btStart);
        runstart_btStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.setTitle("目前運動狀態");
                runstart_btStart.setText("完成");

            }
        });
    }
}
