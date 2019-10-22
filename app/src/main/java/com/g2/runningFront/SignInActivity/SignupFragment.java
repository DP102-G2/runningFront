package com.g2.runningFront.SignInActivity;


import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.g2.runningFront.Common.Common;
import com.g2.runningFront.Common.CommonTask;
import com.g2.runningFront.R;

/* 使用 Gson 相關 */
import com.google.gson.Gson;
import com.google.gson.JsonObject;

/* 有關建立日期、日期格式 */
import java.util.Date;
import com.google.gson.GsonBuilder;


public class SignupFragment extends Fragment {
    private final static String TAG = "TAG_Signup";
    private Activity activity;
    private EditText etId, etName, etPW, etEmail;
    private TextView tvIdBtn, tvNameBtn, tvPWBtn, tvEmailBtn;
    private ImageView ivIdCheck, ivIdCross, ivNameCheck, ivNameCross, ivPWCheck, ivPWCross, ivEmailCheck, ivEmailCross, ivCheckBig;
    private Button btSignup;

    private Animator animator;
    private Interpolator interpolator = new LinearInterpolator();

    private Gson gson;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();

        /* 為了在此使用 Gson */
        gson = new Gson();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle("註冊會員");
        return inflater.inflate(R.layout.fragment_signup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btSignup = view.findViewById(R.id.btSignup);

        /* 在觸發文字監聽器以前，暫時先隱藏所有的打勾/畫叉 icon */
        ivIdCheck = view.findViewById(R.id.ivIdCheck);
        ivIdCross = view.findViewById(R.id.ivIdCross);
        ivNameCheck = view.findViewById(R.id.ivNameCheck);
        ivNameCross = view.findViewById(R.id.ivNameCross);
        ivPWCheck = view.findViewById(R.id.ivPWCheck);
        ivPWCross = view.findViewById(R.id.ivPWCross);
        ivEmailCheck = view.findViewById(R.id.ivEmailCheck);
        ivEmailCross = view.findViewById(R.id.ivEmailCross);
        ivCheckBig = view.findViewById(R.id.ivCheckBig);
        hideViews(ivIdCheck, ivIdCross, ivNameCheck, ivNameCross,
                ivPWCheck, ivPWCross, ivEmailCheck, ivEmailCross, ivCheckBig);

        /* 輸入註冊帳號 */
        etId = view.findViewById(R.id.etId);
        tvIdBtn = view.findViewById(R.id.tvIdBtn);
        etId.addTextChangedListener(new TextWatcher() {

            @Override //這三個方法一定要 Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override //這三個方法一定要 Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override //這三個方法一定要 Override
            public void afterTextChanged(Editable editable) {

                String try_id = etId.getText().toString().trim();

                /* 連線 Servlet 檢查 ID 是否已註冊 */
                boolean isIdUsed;
                isIdUsed = checkIdonSQL(try_id);

                /* 檢查 ID 是否符合正規表達式 */
                setImageforId(try_id, ivIdCheck, ivIdCross, isIdUsed);

            }

        });

        /* 輸入註冊暱稱 */
        etName = view.findViewById(R.id.etName);
        tvNameBtn = view.findViewById(R.id.tvNameBtn);
        etName.addTextChangedListener(new TextWatcher() {
            @Override //這三個方法一定要 Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override //這三個方法一定要 Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override //這三個方法一定要 Override
            public void afterTextChanged(Editable editable) {

                String try_Name = etName.getText().toString().trim();

                /* 檢查 暱稱 是否符合規定 */
                boolean isNameValid = false;

                if(try_Name.length() >= 1 && try_Name.length() <= 30){

                    isNameValid = matches("NAME_REGEXP", try_Name);

                }

                if(isNameValid){
                    ivNameCheck.setVisibility(View.VISIBLE);
                    ivNameCross.setVisibility(View.GONE);
                    btSignup.setEnabled(true);
                    tvNameBtn.setText("＊必填");
                } else {
                    ivNameCross.setVisibility(View.VISIBLE);
                    ivNameCheck.setVisibility(View.GONE);
                    btSignup.setEnabled(false);
                    tvNameBtn.setText("＊暱稱長度不符規定");
                }

            }
        });

        /* 輸入註冊密碼 */
        etPW = view.findViewById(R.id.etPW);
        /* 隱藏鍵盤方法 */
        etPW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeys(activity);
            }
        });
        tvPWBtn = view.findViewById(R.id.tvPWBtn);
        etPW.addTextChangedListener(new TextWatcher() {
            @Override //這三個方法一定要 Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override //這三個方法一定要 Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override //這三個方法一定要 Override
            public void afterTextChanged(Editable editable) {

                String try_password = etPW.getText().toString().trim();

                boolean isPWValid;
                /* 規範密碼的長度 */
                if(try_password.length() < 4 || try_password.length() > 30){
                    isPWValid = false;
                } else {

                    /* 檢查 密碼 是否符合正規表達式 */
                    isPWValid = matches("PASSWORD_REGEXP", try_password);

                }
                setImage(isPWValid, ivPWCheck, ivPWCross);
                btSignup.setEnabled((isPWValid) ? true : false);
                tvPWBtn.setText((isPWValid) ? "＊必填" : "＊密碼不符合規定");

            }
        });

        etEmail = view.findViewById(R.id.etEmail);
        tvEmailBtn = view.findViewById(R.id.tvEmailBtn);
        etEmail.addTextChangedListener(new TextWatcher() {
            @Override //這三個方法一定要 Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override //這三個方法一定要 Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override //這三個方法一定要 Override
            public void afterTextChanged(Editable editable) {

                String try_Email = etEmail.getText().toString().trim();

                /* 檢查 暱稱 是否符合規定 */
                boolean isEmailValid = false;

                if(try_Email.length() >= 8 && try_Email.length() <= 60){

                    isEmailValid = matches("EMAIL_REGEXP", try_Email);

                }

                if(isEmailValid){
                    ivEmailCheck.setVisibility(View.VISIBLE);
                    ivEmailCross.setVisibility(View.GONE);
                    btSignup.setEnabled(true);
                    tvEmailBtn.setText("＊必填");
                } else {
                    ivEmailCross.setVisibility(View.VISIBLE);
                    ivEmailCheck.setVisibility(View.GONE);
                    tvEmailBtn.setText("＊輸入信箱不符合規定");
                    btSignup.setEnabled(false);
                }

            }
        });

        /* 註冊會員按鈕 */
        btSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /* 隱藏鍵盤 */
                hideKeys(activity);

                String id = etId.getText().toString().trim();
                String password = etPW.getText().toString().trim();
                String name = etName.getText().toString().trim();
                String email = etEmail.getText().toString().trim();

                if(id.length() <= 0 || password.length() <= 0
                        || name.length() <= 0 || email.length() <= 0){
                    Common.toastShow(activity,"輸入資料不符規定");
                    return;
                }

                if (Common.networkConnected(activity)) {

                    String url = Common.URL_SERVER + "SettingServlet";

                    JsonObject jo = new JsonObject();
                    jo.addProperty("action", "signup");
                    jo.addProperty("user_id", id);
                    jo.addProperty("user_pw", password);
                    jo.addProperty("user_name", name);
                    jo.addProperty("user_email", email);

                    /* 會員註冊日期 */
                    Gson gson = new GsonBuilder()
                            .setDateFormat("yyyy-MM-dd")// 2020-01-02
                            .create();
                    /* 將 new Date() 轉為 Json，並且要符合以上的日期表示法
                     * 但是在 Servlet 端會變成字串型態，需要再利用 Gson 轉成 Date */
                    String date = gson.toJson(new Date());
                    jo.addProperty("user_regtime", date);

                    Log.d(TAG, "即將送出的註冊資料：\n" + jo);
                    String outStr = jo.toString();

                    CommonTask signUpTask = new CommonTask(url, outStr);

                    boolean isSignUp = false;
                    try {
                        String jsonIn = signUpTask.execute().get();
                        isSignUp = gson.fromJson(jsonIn, Boolean.class);
                        Log.e(TAG, "isSignUp = " + isSignUp);

                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                    if (isSignUp) {
                        Log.e(TAG, "會員註冊成功。");
                        Common.toastShow(activity, "會員註冊成功！");
                        // signUpAnimator();
                    } else{
                        Log.e(TAG, "會員註冊失敗。");
                        Common.toastShow(activity, "會員註冊失敗");
                    }
                }else {
                    Common.toastShow(activity, R.string.textNoNetwork);
                }

                /* 註冊成功跳頁前動畫 */
                signUpAnimator();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        /* 結束註冊 Activity，回到上一層開啟的 Activity */
                        activity.finish();

                        /*
                        Intent settingIntent = new Intent(activity, SettingMainFragment.class);
                        Intent runIntent = new Intent(activity, MainActivity.class);
                        activity.startActivity(settingIntent);
                         */

                    }
                },8 * 1000);// 延後 8 秒
            }
        });

    }

    /* 連線 Servlet 檢查 ID 是否已被使用
     * 並改變輸入文字顏色狀態 */
    private boolean checkIdonSQL(String try_id){

        String url = Common.URL_SERVER + "SettingServlet";

        JsonObject jo = new JsonObject();
        jo.addProperty("action","checkId");
        jo.addProperty("try_id", try_id);

        CommonTask CheckIdTask = new CommonTask(url, jo.toString());

        boolean isIdUsed = false;
        try {

            String strIn = CheckIdTask.execute().get();
            isIdUsed = gson.fromJson(strIn, Boolean.class);

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        } finally {
            if(!isIdUsed){
                // ID 已經被註冊了
                etId.setTextColor(Color.RED);
                tvIdBtn.setText("＊重複使用的帳號");
            } else {
                // ID 尚未被註冊
                etId.setTextColor(Color.BLACK);
                tvIdBtn.setText("＊必填");
            }
        }
        return isIdUsed;
    }

    /**
     * 用正規表達式比對文字格式，回傳布林值
     * @param regExp    需要利用的正規表達式
     * @param checkText 任意字串
     * @return boolean
     */
    private boolean matches(String regExp, String checkText) {
        boolean result = false;
        switch (regExp){
            case "ID_REGEXP":
                result = checkText.matches("^\\w{4,16}$");
                break;
            case "NAME_REGEXP":
                result = true;
                break;
            case "PASSWORD_REGEXP":
                result = checkText.matches("^[A-Za-z0-9]+.{3,}$");
                break;
            case "EMAIL_REGEXP":
                result = checkText.matches("^\\w+(\\w+)@\\w+([-.]\\w+).\\w+([-.]\\w+)*$");
                break;
            default:
                break;
        }

        // 長度4-16，由數字、英文字母、_ 組成 --  ^\\w{4,16}$
        // 長度4以上的密碼 --                  ^[A-Za-z0-9]+.{3,}$
        // Email --                         ^\w+(\w+)@\w+([-.]\w+).\w+([-.]\w+)*$
        // 由數字或英文字母組成 --              ^[A-Za-z0-9]+$

        return result;
    }

    /* 按照輸入帳號改變打勾圖片 */
    private void setImageforId(String try_id, ImageView iv_true, ImageView iv_false, boolean isIdUsed){
        boolean isIdValid = false;

        if(try_id.length() >= 4 && try_id.length() <= 16){
            isIdValid = matches("ID_REGEXP", try_id);
        }

        /* 如果 ID 符合條件，而且尚未被註冊過 */
        if(isIdValid && isIdUsed){
            iv_true.setVisibility(View.VISIBLE);
            iv_false.setVisibility(View.GONE);
            btSignup.setEnabled(true);
        } else {
            iv_false.setVisibility(View.VISIBLE);
            iv_true.setVisibility(View.GONE);
            btSignup.setEnabled(false);
        }
    }

    /* 按照輸入文字改變打勾圖片 */
    private void setImage(boolean isValid, ImageView iv_true, ImageView iv_false){

        if(isValid){
            iv_true.setVisibility(View.VISIBLE);
            iv_false.setVisibility(View.GONE);
        } else {
            iv_false.setVisibility(View.VISIBLE);
            iv_true.setVisibility(View.GONE);
        }

    }

    /**
     * 隱藏傳入的任何 View
     * @param views     不定數量的 View
     */
    private void hideViews(View... views){
        for(View iv: views){
            iv.setVisibility(View.GONE);
        }
    }

    /* 註冊成功的動畫 */
    private void signUpAnimator(){
        // 隱藏全部頁面元件
        hideViews(etId, etName, etPW, etEmail, tvIdBtn, tvNameBtn, tvPWBtn, tvEmailBtn,
                ivIdCheck, ivIdCross, ivNameCheck, ivNameCross, ivPWCheck, ivPWCross,
                ivEmailCheck, ivEmailCross, btSignup);
        ivCheckBig.setVisibility(View.VISIBLE);

        if (animator != null) {
            animator.cancel();
        }
        animator = getScaleAnim();
        animator.start();
    }

    /**
     * 建立縮放動畫並套用當前特效
     */
    private ObjectAnimator getScaleAnim() {
        PropertyValuesHolder holderX =
                PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0f, 0.5f, 1.5f, 1.0f);
        PropertyValuesHolder holderY =
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0f, 0.5f, 1.5f, 1.0f);

        ObjectAnimator objectAnimator =
                ObjectAnimator.ofPropertyValuesHolder(ivCheckBig, holderX, holderY);
        objectAnimator.setDuration(2500);
        objectAnimator.setRepeatCount(2);
        return objectAnimator;
    }

    /* 隱藏鍵盤方法 */
    private void hideKeys(Activity activity){
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}