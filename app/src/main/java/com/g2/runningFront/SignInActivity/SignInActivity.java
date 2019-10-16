package com.g2.runningFront.SignInActivity;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.g2.runningFront.Common.*;
import com.g2.runningFront.R;

/* Gson */
import com.g2.runningFront.RunActivity.MainActivity;
import com.g2.runningFront.ShopActivity.ShopActivity;

/* 從偏好設定檢視登入裝態 */
import android.content.SharedPreferences;

public class SignInActivity extends AppCompatActivity {
    private static final String TAG = "TAG_SignIn_Activity";

    @Override
    protected void onStart() {
        super.onStart();

        /* 查詢偏好設定檢查登入狀態，『已登入』則自動退出 登入Activity */
        SharedPreferences pref = getSharedPreferences(Common.PREF, MODE_PRIVATE);

        boolean isSignIn = pref.getBoolean("isSignIn", false);
        if (isSignIn) {

            setResult(RESULT_OK);
            Log.d(TAG, "onStart 檢查已經登入，此頁不會出現。");
            finish();

        } else {
            pref.edit().putBoolean("isSignIn", false)

                    /* 清除偏好設定 */
                    //.clear()

                    .apply();
            Log.d(TAG, "onStart 檢查未登入");
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
    }

    /* AppBar 按鈕 */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.option_menu, menu);
        menu.removeItem(R.id.opSetting);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {

            case R.id.opShop:
                intent = new Intent(this, ShopActivity.class);
                startActivity(intent);
                this.finish();
                return true;
            case R.id.opRun:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                this.finish();
                return true;
            case R.id.opSetting:
                return true;

        }
        return true;
    }

}