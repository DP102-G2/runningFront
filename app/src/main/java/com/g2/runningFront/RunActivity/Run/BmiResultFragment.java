package com.g2.runningFront.RunActivity.Run;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.g2.runningFront.Common.Common;
import com.g2.runningFront.Common.CommonTask;
import com.g2.runningFront.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class BmiResultFragment extends Fragment {

    private Activity activity;
    private Bundle bundle;

    int user_no;

    View view;
    TextView tvBMI, tvSuggest;
    Button btConfirm;

    UserBasic userBasic;
    int bmi, height, weight, gender, age;
    SeekBar seekBar;
    String bmiSuggest;

    SharedPreferences pref;
    final private static String PREFERENCES_NAME = "preference";

    CommonTask commonTask;
    private static final String url = Common.URL_SERVER + "RunServlet";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        bundle = getArguments();
        pref = activity.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        user_no = Common.getUserNo(activity);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle("BMI");
        return inflater.inflate(R.layout.fragment_bmi_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        setUserBasic();
        holdView();


    }

    private void holdView() {
        tvBMI = view.findViewById(R.id.bmi_tvBMI);
        tvBMI.setText(String.valueOf(bmi));

        tvSuggest = view.findViewById(R.id.bmi_tvSuggest);
        tvSuggest.setText(bmiSuggest);

        seekBar = view.findViewById(R.id.bmi_sb);
        seekBar.setProgress(userBasic.getBmi());
        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });


        btConfirm = view.findViewById(R.id.bmi_btConfirm);
        btConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "UpdateUserBasic");
                    jsonObject.addProperty("user_no", user_no);
                    jsonObject.addProperty("UserBasic", new Gson().toJson(userBasic));

                    commonTask = new CommonTask(url, jsonObject.toString());
                    int count = Integer.parseInt(commonTask.execute().get());

                    if (count == 1) {
                        Common.toastShow(activity, "新增成功");
                    }

                } catch (Exception e) {

                    e.printStackTrace();
                }

                pref.edit().putString("UserBasic", new Gson().toJson(userBasic)).apply();
                Navigation.findNavController(view)
                        .navigate(R.id.action_bmiResult_to_runMain);
            }
        });
    }

    private void setUserBasic() {
        userBasic = (UserBasic) bundle.getSerializable("UserBasic");
        height = userBasic.getHeight();
        age = userBasic.getAge();
        weight = userBasic.getWeight();
        gender = userBasic.getGender();
        bmi = userBasic.getBmi();
        bmiSuggest = userBasic.getBMISuggest();





    }

}
