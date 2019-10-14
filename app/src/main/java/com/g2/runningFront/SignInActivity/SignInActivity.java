package com.g2.runningFront.SignInActivity;

/* 有關於隱藏/顯示輸入鍵盤 */
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import static android.view.View.GONE;// 有關隱藏按鈕

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.g2.runningFront.Common.*;
import com.g2.runningFront.R;

/* Gson */
import com.g2.runningFront.RunActivity.MainActivity;
import com.g2.runningFront.ShopActivity.ShopActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

/* Google 登入 */
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

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

//        /* 回應成功代碼（傳回去發出 Intent 的頁面） */
//        setResult(RESULT_OK);
//
//        /* 延時執行 */
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//                Log.d(TAG, "登入成功，此頁消失");
//                SignInActivity.this.finish();// finish() 讓視窗（頁面）消失
//            }
//        },2000);// 延後2秒

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