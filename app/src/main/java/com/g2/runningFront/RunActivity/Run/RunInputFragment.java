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
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.g2.runningFront.R;
import com.g2.runningFront.RunActivity.Bmi;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

public class RunInputFragment extends Fragment {
    private Activity activity;
    private EditText etHeight, etWeight, etAge;
    private Button runinput_btConfirm;
    private RadioGroup sex;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
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
        sex = view.findViewById(R.id.radioGroup);//sex用radiogroup還是radiobutton
        etHeight = view.findViewById(R.id.etHeight);
        etWeight = view.findViewById(R.id.etWeight);
        etAge = view.findViewById(R.id.etAge);
        runinput_btConfirm = view.findViewById(R.id.runinput_btConfirm);
        runinput_btConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String height_length = etHeight.getText().toString().trim();
                String weight_length = etWeight.getText().toString().trim();
                String age_length = etAge.getText().toString().trim();

//                if (height_length.length() <= 0||weight_length.length() <=0 || age_length.length() <=0) {
//                    Toast.makeText(getActivity(), "輸入資訊不可空白 ",Toast.LENGTH_LONG).show();
//                    return;
//                }

//                double height = Double.valueOf(etHeight.getText().toString().trim());
//                double weight = Double.valueOf(etWeight.getText().toString().trim());
//                int age = Integer.valueOf(etAge.getText().toString().trim());
//                Bmi bmi = new Bmi(height, weight);
//                Bundle bundle = new Bundle();
//                //sex要用什麼型別
//                int s = 0;
//                switch (sex.getCheckedRadioButtonId()) {
//                    case R.id.btnMan:
//                        s = 1;
//                        break;
//                    case R.id.btnWoman:
//                        s = 0;
//                        break;
//                }
//                bundle.putInt("sex", s);
//                bundle.putSerializable("BMI", bmi);
//                bundle.putInt("age", age);


//                try {
//                    saveBmi(bmi);
//                } catch (IOException e) {
//                    Log.e(TAG, e.toString());
//                }
//                Navigation.findNavController(view)
//                        .navigate(R.id.action_runInput_to_bmiResult, bundle);
                Navigation.findNavController(view)
                        .navigate(R.id.action_runInput_to_bmiResult);

            }
        });
    }


    private void saveBmi(Bmi bmi) throws IOException {
        FileOutputStream fos = activity.openFileOutput("BMI", MODE_PRIVATE);
        ObjectOutputStream out = new ObjectOutputStream(fos);
        out.writeObject(bmi);
        out.close();
    }


}
