package com.g2.runningFront.SettingActivity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;

import com.g2.runningFront.Common.Common;
import com.g2.runningFront.Common.ImageTask;
import com.g2.runningFront.R;


/* 有關使用者登入狀態的檢查 */
import android.content.SharedPreferences;

/* 有關延後執行程式碼 */
import android.os.Handler;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import android.content.Intent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.g2.runningFront.SignInActivity.SignInActivity;


public class SettingMainFragment extends Fragment {
    private static String TAG = "TAG_SettingMain";
    private Activity activity;
    private ImageView imageView;
    private TextView textView;
    private Button button;

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

        imageView = view.findViewById(R.id.imageView);
        textView = view.findViewById(R.id.textView);

        /* （暫時性）前往登入 Activity 的臨時按鈕 */
        button = view.findViewById(R.id.set_btLogin);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent;
                intent = new Intent(activity, SignInActivity.class);
                startActivity(intent);

                //SettingActivity.this.finish();
                //前面區塊，根據要關閉的activity做更換

            }
        });

        /* 修改會資料按鈕 */
        view.findViewById(R.id.btUpdate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int no = activity.getSharedPreferences(Common.PREF,MODE_PRIVATE).getInt("user_no",0);
                Bundle bundle = new Bundle();
                bundle.putSerializable("user_no", no);

                Navigation.findNavController(textView).
                        navigate(R.id.action_settingMainFragment_to_settingUpadteFragment, bundle);

                /*Navigation.findNavController(imageView)
                        .navigate(R.id.action_settingMainFragment_to_settingUpadteFragment);*/
                /*Navigation.findNavController(imageView)
                        .navigate(R.id.action_settingMainFragment_to_settingUpadteFragment, bundle);*/
            }
        });

        /* 登出按鈕 */
        view.findViewById(R.id.btSignOut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences pref = activity.getSharedPreferences(Common.PREF,
                        MODE_PRIVATE);
                pref.edit().putBoolean("isSignIn", false)
                        .apply();

                textView.setText("已經登出");

                Log.e(TAG,"使用者登出，登入狀態\"isSignIn\"已修改。");

                /* 延時執行 */
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
        /* 在 onRusume 先檢查登入狀態 */
        Common.signIn(activity);

        int no = activity.getSharedPreferences(Common.PREF,MODE_PRIVATE).getInt("user_no",0);
        Log.d(TAG,"onResume 檢查到會員編號：" + no);
        Bitmap bitmap = null;

        // 規範圖片尺寸
        //int imageSize = getResources().getDisplayMetrics().widthPixels / 3;
        String url = Common.URL_SERVER + "SettingServlet";

        try {

            bitmap = new ImageTask(url, no).execute().get();

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setImageResource(R.drawable.user_no_image);
        }

    }

    /* 分析請求代碼，判斷如何處理 */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        SharedPreferences pref = activity.getSharedPreferences(Common.PREF,MODE_PRIVATE);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQ_SIGNIN:

                    textView.setText("isSignIn = "+ pref.getBoolean("isSignIn",false)
                            +"\nUser_No:\t\t"+pref.getInt("user_no",0));

                    break;
                default:
                    break;
            }
        }
    }
}