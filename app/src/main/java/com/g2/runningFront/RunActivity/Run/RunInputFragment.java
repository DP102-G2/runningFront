package com.g2.runningFront.RunActivity.Run;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.g2.runningFront.Common.Common;
import com.g2.runningFront.R;
import com.google.gson.Gson;

import static android.content.Context.MODE_PRIVATE;

public class RunInputFragment extends Fragment {
    private Activity activity;

    UserBasic userBasic;

    View view;
    EditText etHeight, etWeight, etAge;
    Button btConfirm;
    RadioGroup rgGender;
    RadioButton rbMan, rbWoman;

    int height, weight, age, gender = 1;

    SharedPreferences pref;
    final private static String PREFERENCES_NAME = "UserBasic";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        pref = activity.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        activity.setTitle("輸入資訊");
        return inflater.inflate(R.layout.fragment_run_input, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getUserBasic();
        this.view = view;
        holdView();
    }

    private void holdView() {

        rgGender = view.findViewById(R.id.rip_rgGender);
        rbMan = view.findViewById(R.id.rip_rbMan);
        rbWoman = view.findViewById(R.id.rip_rbWoman);

        rgGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rip_rbMan:
                        gender = 1;
                        break;
                    case R.id.rip_rbWoman:
                        gender = 0;
                        break;
                }
            }
        });


        etHeight = view.findViewById(R.id.rip_etHeight);
        etWeight = view.findViewById(R.id.rip_etWeight);
        etAge = view.findViewById(R.id.rip_etAge);
        btConfirm = view.findViewById(R.id.runinput_btConfirm);

        if (userBasic.getHeight() != 0 & userBasic.getWeight() != 0 & userBasic.getAge() != 0) {

            etHeight.setText(String.valueOf(userBasic.getHeight()));
            etWeight.setText(String.valueOf(userBasic.getWeight()));
            etAge.setText(String.valueOf(userBasic.getAge()));

            switch (userBasic.getGender()){
                case 0:
                    rbMan.setChecked(false);
                    rbWoman.setChecked(true);
                    break;
                case 1:
                    rbMan.setChecked(true);
                    rbWoman.setChecked(false);
                    break;

            }

        }


        btConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (etAge.getText().toString().trim().equals("") || etHeight.getText().toString().trim().equals("") || etWeight.getText().toString().trim().equals("")) {
                    Common.toastShow(activity, "輸入資訊不可空白 ");
                    return;
                }

                height = Integer.parseInt(etHeight.getText().toString().trim());
                weight = Integer.parseInt(etWeight.getText().toString().trim());
                age = Integer.parseInt(etAge.getText().toString().trim());

                userBasic = new UserBasic(height, weight, age, gender);

                Bundle bundle = new Bundle();
                bundle.putSerializable("UserBasic", userBasic);

                Navigation.findNavController(view)
                        .navigate(R.id.action_runInput_to_bmiResult, bundle);

            }
        });
    }

    private void getUserBasic() {
        String obStr = pref.getString("UserBasic", "No Data");
        if (obStr != null) {
            userBasic = new Gson().fromJson(obStr, UserBasic.class);
        }
    }

}
