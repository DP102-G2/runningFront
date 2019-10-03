package com.g2.runningFront.SignInActivity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.g2.runningFront.R;

public class SignInActivity extends AppCompatActivity {
    private TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        setTitle("登入 Activity");

        textView = findViewById(R.id.textView);
        textView.setText("這裡是 SigninActivity.java");

    }
}
