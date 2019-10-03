package com.g2.runningFront.SettingActivity;


import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.g2.runningFront.Common.Common;
import com.g2.runningFront.Common.CommonTask;
import com.g2.runningFront.Common.User;
import com.g2.runningFront.R;// res 目錄
import com.google.gson.Gson;
import com.google.gson.JsonObject;


public class SettingFragment extends Fragment {
    private static final String TAG = "TAG_SET";
    private Activity activity;
    private TextView textView;

    private Gson gson;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();

        gson = new Gson();

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

        Bundle bundle = getArguments();
        User user = (User) bundle.getSerializable("user");

        Common.toast(activity, "一般登入成功");
        textView.setText("一般登入成功\nUser_Id: "+user.getId()
                +"\nUser_No: "+String.valueOf(user.getNo()));

        if (Common.networkConnected(activity)) {
            String url = Common.URL_SERVER + "SettingServlet";

            JsonObject jo = new JsonObject();
            jo.addProperty("action", "getUser");
            jo.addProperty("user", new Gson().toJson(user));

            String outStr = jo.toString();
            CommonTask getTask = new CommonTask(url, outStr);

            try {
                String strIn = getTask.execute().get();
                jo = gson.fromJson(strIn, JsonObject.class);

                //String name = jo.getAsString("name");
                //String mail = jo.getAsString("mail");

            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
        // BALABALA

    }
}
