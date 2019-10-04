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
    private static final int REQ_SIGNIN = 5;

    /* Common. */
    public final static String PREF = "preference";
    /* 偏好設定系列變數 */
    private SharedPreferences pref;
    boolean isSignIn;

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

        /* 取得當前偏好設定 */
        pref = activity.getSharedPreferences("preferences", MODE_PRIVATE);

        textView = view.findViewById(R.id.textView);
//        textView.setText("isSignIn = "+ pref.getBoolean("isSignIn",false)
//                +"\nUser_No:\t\t"+pref.getInt("user_no",0));

        /* 登出按鈕 */
        view.findViewById(R.id.btSignOut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences pref = activity.getSharedPreferences("preference",
                        MODE_PRIVATE);

                pref.edit().putBoolean("isSignIn", false)
                        .apply();
                view.setVisibility(View.GONE);

                Log.d(TAG,"偏好設定修改成登出。");

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        /**
                         * 延时执行的代码
                         */
                        Intent signInIntent = new Intent(activity, SignInActivity.class);
                        /* 請求轉往登入頁面
                         * startActivity(Intent) */
                        startActivity(signInIntent);

                    }
                },3000);// 延後3秒
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();

        // 從偏好設定檔中取得登入狀態來決定是否顯示「登出」
        pref = activity.getSharedPreferences("preference", MODE_PRIVATE);

        try{
            isSignIn = pref.getBoolean("isSignIn", false);
        } catch (ClassCastException c){
            c.printStackTrace();
        } finally {
            if(!isSignIn){
                /* onResume 檢查到未登入，將切換到登入頁 */
                Intent signInIntent = new Intent(activity, SignInActivity.class);
                /* 請求登入頁面的結果（登入成功/登入失敗）
                 * startActivityForResult(Intent, 請求代碼); */
                startActivityForResult(signInIntent, REQ_SIGNIN);
                return;
            }
            Log.d(TAG, "onResume 檢查已經登入");
        }
    }

    /* 分析請求代碼，判斷如何處理 */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQ_SIGNIN:

                    /* 連線 Servlet 取得使用者大頭貼，並且存入本地 */
                    textView.setText("isSignIn = "+ pref.getBoolean("isSignIn",false)
                            +"\nUser_No:\t\t"+pref.getInt("user_no",0));
                    break;
            }
        }
    }

}