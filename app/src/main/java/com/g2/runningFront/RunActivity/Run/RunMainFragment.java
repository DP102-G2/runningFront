package com.g2.runningFront.RunActivity.Run;


import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.g2.runningFront.Common.Common;
import com.g2.runningFront.Common.CommonTask;
import com.g2.runningFront.Common.TimestampTypeAdapter;
import com.g2.runningFront.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

// 取偏好設定，如果沒有就跳到最前面

public class RunMainFragment extends Fragment {
    private Activity activity;
    private Button btStartRun;
    TextView tvTime, tvDistance, tvCalorie;
    View view;
    List<Run> runList = new ArrayList<>();
    CommonTask runTask;
    String formatTime;
    int wDistance = 0, wTime = 0, wCalorie = 0;
    private static final String url = Common.URL_SERVER + "RunServlet";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle("運動");
        return inflater.inflate(R.layout.fragment_run_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        runList = getRun();
        if (runList != null) {
            getWeekData();
            holdView();
        }

    }

    private void holdView() {

        btStartRun = view.findViewById(R.id.rm_btStartRun);
        btStartRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view)
                        .navigate(R.id.action_runMain_to_runStart);
            }
        });

        tvTime = view.findViewById(R.id.rm_tvTime);
        tvCalorie = view.findViewById(R.id.rm_tvCalorie);
        tvDistance = view.findViewById(R.id.rm_tvDistance);

        tvTime.setText(formatTime);
        tvDistance.setText(String.valueOf(wDistance / 1000) + " km ");
        tvCalorie.setText(String.valueOf(wCalorie) + " 卡 ");
    }

    private List<Run> getRun() {

        List<Run> runs = new ArrayList<>();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("yyyyMMddhhmmss");
        gsonBuilder.registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter());
        Gson gson = gsonBuilder.create();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "getWeekRunList");
        jsonObject.addProperty("userNo", 1);
        // 之後要補足資料

        try {

            runTask = new CommonTask(url, jsonObject.toString());
            String runListStr = runTask.execute().get();
            Type listType = new TypeToken<List<Run>>() {
            }.getType();
            runs = gson.fromJson(runListStr, listType);

        } catch (Exception e) {
            e.printStackTrace();
        }


        return runs;
    }

    private void getWeekData() {

        wCalorie = 0;
        wDistance = 0;
        wTime = 0;

        for (Run run : runList) {
            wCalorie += run.getCalorie();
            wDistance += run.getDistance();
            wTime += run.getTime();

        }
        int minutes = ((wTime) / 60) % 60;
        int hours = (wTime) / 3600;

        formatTime = hours + " 小時 , " + minutes + " 分鐘  ";

    }


}
