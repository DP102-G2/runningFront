package com.g2.runningFront.SettingActivity;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;

import com.g2.runningFront.Common.Common;
import com.g2.runningFront.R;


/* 有關使用者登入狀態的檢查 */
import android.content.SharedPreferences;

/* 有關延後執行程式碼 */
import android.os.Handler;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import android.content.Intent;
import android.widget.TextView;

import com.g2.runningFront.SignInActivity.SignInActivity;


public class SettingMainFragment extends Fragment {
    private static String TAG = "TAG_SettingMainFrag";
    private Activity activity;
    private TextView textView;

    /* 請求登入的代碼 */
    private static final int REQ_SIGNIN = 50;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity.setTitle("設定主頁面");
        return inflater.inflate(R.layout.fragment_setting_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textView = view.findViewById(R.id.textView);

        /* 登出按鈕 */
        view.findViewById(R.id.btSignOut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences pref = activity.getSharedPreferences(Common.PREF,
                        MODE_PRIVATE);
                pref.edit().putBoolean("isSignIn", false)
                        .apply();
                view.setVisibility(View.GONE);

                textView.setText("已經登出");

                Log.d(TAG,"偏好設定修改成登出。");

                /*
                 * 延時執行
                 */
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        //請求轉往登入頁面
                        Intent signInIntent = new Intent(activity, SignInActivity.class);
                        startActivityForResult(signInIntent,REQ_SIGNIN);
                    }
                },4000);// 延後4秒
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();

        Common.signIn(activity);

    }

    /* 分析請求代碼，判斷如何處理 */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        SharedPreferences p = activity.getSharedPreferences(Common.PREF,MODE_PRIVATE);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQ_SIGNIN:

                    /* 連線 Servlet 取得使用者大頭貼，並且存入本地 */

                    textView.setText("isSignIn = "+ p.getBoolean("isSignIn",false)
                            +"\nUser_No:\t\t"+p.getInt("user_no",0));
                    Log.d(TAG,"isSignIn = "+ p.getBoolean("isSignIn",false)
                            +"\nUser_No:\t\t"+p.getInt("user_no",0));
                    break;
                default:
                    break;
            }
        }
    }

}