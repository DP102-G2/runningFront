package com.g2.runningFront.SettingActivity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

/* 有關圖片處理 */
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

/* 使用 Gson */
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;

import com.g2.runningFront.Common.*;
import com.g2.runningFront.R;


/* 有關使用者登入狀態的檢查 */
import android.content.SharedPreferences;

/* 有關延後執行程式碼 */
import android.os.Handler;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static com.g2.runningFront.Common.Common.round;

import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.g2.runningFront.SignInActivity.SignInActivity;
import com.google.gson.JsonObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SettingMainFragment extends Fragment {
    private static String TAG = "TAG_SettingMain";
    private Activity activity;
    private ImageView imageView;
    private TextView textView;
    private Button button;
    private EditText et;

    private Gson gson;

    /* 請求登入的代碼 */
    private static final int REQ_SIGNIN = 50;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();

        /* 為了在此使用 Gson */
        gson = new Gson();

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

        /* 檢查會員註冊的帳號名稱是否有重複 */
        et = view.findViewById(R.id.et);
        et.addTextChangedListener(new TextWatcher() {

            @Override   //這三個方法一定要 Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String new_id = et.getText().toString().trim();

                JsonObject jo = new JsonObject();
                jo.addProperty("action","checkId");
                jo.addProperty("new_id", new_id);

                String url = Common.URL_SERVER + "SettingServlet";
                CommonTask checkIdTask = new CommonTask(url, jo.toString());

                boolean isIdValid = false;
                try {

                    String strIn = checkIdTask.execute().get();
                    Log.e(TAG,"" + strIn);
                    isIdValid = gson.fromJson(strIn, Boolean.class);

                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
                if (isIdValid && new_id.length() <= 16) {
                    // Id OK
                    et.setTextColor(Color.BLACK);
                } else {
                    // Id 不OK
                    et.setTextColor(Color.RED);
                }

            }

            @Override   //這三個方法一定要 Override
            public void afterTextChanged(Editable editable) {
                return;
            }

            @Override   //這三個方法一定要 Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                return;
            }

        });

        /* ❗️（暫時性）前往登入 Activity 的臨時按鈕❗️ */
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
                bundle.putInt("user_no", no);

                Navigation.findNavController(textView).
                        navigate(R.id.action_settingMainFragment_to_settingUpadteFragment, bundle);
            }

        });

        /* 登出按鈕 */
        view.findViewById(R.id.btSignOut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences pref = activity.getSharedPreferences(Common.PREF,
                        MODE_PRIVATE);
                pref.edit()
                        .putBoolean("isSignIn", false)

                        /* 清除偏好設定 */
                        .clear()

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

        /* 取得會員大頭貼圖像 */
        int no = activity.getSharedPreferences(Common.PREF,MODE_PRIVATE).getInt("user_no",0);
        Log.d(TAG,"onResume 檢查到會員編號：" + no);

        JsonObject jo = new JsonObject();
        jo.addProperty("action","getImage");
        jo.addProperty("user_no", no);

        String url = Common.URL_SERVER + "SettingServlet";
        CommonTask imageTask = new CommonTask(url, jo.toString());

        Bitmap bitmap = null;

        try {

            /* 用 Base64 解碼 Servlet 端 編碼而成的文字變成 byte[]
             * 再用 BitmapFactory 把 byte[] 換成 bitmap 以供 UI 元件貼圖 */
            byte[] image = Base64.decode(imageTask.execute().get(), Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        if (bitmap != null) {

            /* 把圖片套上變成圓形的方法 */
            imageView.setImageBitmap(round(bitmap));

        } else {
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.user_no_image);
            imageView.setImageBitmap(round(bitmap));
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

    /**
     * 用正規表達式檢查字串，回傳布林值
     * @param string    任意字串
     * @return boolean
     */
    private boolean matches(String string){
        return string.matches("");

        // 長度5-16，由數字、英文字母、_ 組成 --  ^\\w{5,16}$
        // 長度5以上的密碼 --                  ^[A-Za-z0-9]+.{5,}$
        // Email --                         ^\w+([-+.]\w+)@\w+([-.]\w+).\w+([-.]\w+)*$
        // 由數字或英文字母組成 --              ^[A-Za-z0-9]+$

    }
}