package com.g2.runningFront.RunActivity.Run;


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
import android.widget.Button;
import android.widget.TextView;

import com.g2.runningFront.R;
import com.g2.runningFront.RunActivity.Bmi;

import java.io.FileInputStream;
import java.io.ObjectInputStream;

import static android.content.ContentValues.TAG;

public class BmiResultFragment extends Fragment {

    private Activity activity;
    private TextView tvBMI;
    private Button bmi_btConfirm;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
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

        tvBMI = view.findViewById(R.id.tvBMI);
        Bmi bmi = null;
        try{
            bmi = loadBmi();
        }catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        String text = bmi == null ? "" : bmi.toString();
        tvBMI.setText(text);

        bmi_btConfirm = view.findViewById(R.id.bmi_btConfirm);
        bmi_btConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view)
                        .navigate(R.id.action_bmiResult_to_runMain);
            }
        });

    }

    private Bmi loadBmi() throws Exception {
        FileInputStream fis = activity.openFileInput("BMI");
        ObjectInputStream ois = new ObjectInputStream(fis);
        Bmi bmi = (Bmi) ois.readObject();
        ois.close();
        return bmi;
    }
}
