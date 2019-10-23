package com.g2.runningFront.SignInActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.g2.runningFront.R;
import com.g2.runningFront.Common.Common;
import com.g2.runningFront.Common.CommonTask;
import com.g2.runningFront.Common.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

/* Google 登入 imports */

public class SignInMainFragment extends Fragment {
    private static final String TAG = "TAG_SignIn_Main";
    private Activity activity;
    private EditText etId, etPassword;

    /* Google 登入宣告 */
    private GoogleSignInClient googleSignInClient;
    private static final int GSIGN_CODE = 101;

    private Gson gson;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();

        gson = new Gson();

        /* 有關 Google 登入，以及登入後讀取 Google 帳戶資訊 */
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(activity, gso);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity.setTitle("登入");
        return inflater.inflate(R.layout.fragment_signin_main, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etId = view.findViewById(R.id.etId);
        etPassword = view.findViewById(R.id.etPassword);

        /* 會員登入按鈕 */
        view.findViewById(R.id.btSignIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /* 隱藏鍵盤方法 */
                hideKeyboard(activity);

                String id = etId.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (id.length() <= 0 || password.length() <= 0) {
                    Common.toastShow(activity, "請輸入有效的帳號密碼");
                } else {

                    User user = new User(0, id, "NONE", password);

                    if (Common.networkConnected(activity)) {
                        String url = Common.URL_SERVER + "SettingServlet";

                        JsonObject jo = new JsonObject();
                        jo.addProperty("action", "signin");
                        jo.addProperty("user", new Gson().toJson(user));
                        String outStr = jo.toString();

                        CommonTask signInTask = new CommonTask(url, outStr);

                        try {

                            String strIn = signInTask.execute().get();
                            user = gson.fromJson(strIn, User.class);

                            if (user == null) {
                                Common.toastShow(activity, "輸入之帳號或密碼不正確");

                            } else {

                                Common.toastShow(activity, "一般登入成功");

                                /* 將登入的會員資料存入偏好設定 */
                                SharedPreferences pref = activity.getSharedPreferences("preference",
                                        MODE_PRIVATE);
                                pref.edit()
                                        .putInt("user_no", user.getNo())
                                        .putString("user_id", user.getId())
                                        .putString("user_name", user.getName())
                                        .putBoolean("isSignIn", true)
                                        .apply();

                                /* 回應成功代碼（傳回去發出 Intent 的頁面） */
                                /*setResult(RESULT_OK); */

                                /* 延時執行 */
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        Log.d(TAG, "登入成功，此頁消失");
                                        activity.finish();// finish() 讓視窗（Activity）消失

                                    }
                                }, 2 * 1000);// 延後2秒
                            }

                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage());
                        }
                    } else {
                        Common.toastShow(activity, R.string.textNoNetwork);
                    }
                }

            }
        });

        /* ==================== Google 登入程式碼 ==================== */

        /* Google 登入按鈕 */
        SignInButton signInButton = view.findViewById(R.id.btGSignIn);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent googleSignIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(googleSignIntent, GSIGN_CODE);

            }
        });

        /* 註冊會員按鈕 */
        view.findViewById(R.id.btSignUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view)
                        .navigate(R.id.action_signinMainFragment_to_signupFragment);
            }
        });

    }

    /* 承接 Google 登入結果 */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GSIGN_CODE){
            // The Task returned from this call is always completed,
            // no need to attach a listener.
            Task<GoogleSignInAccount> gTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(gTask);
        }

    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask){

        try{

            GoogleSignInAccount acct = completedTask.getResult(ApiException.class);

            /* 取得使用者的資料，取得後再看你想要對這些資料做什麼用途 */
            // getId 21位 Google 帳戶編號
            //Log.e(TAG, "handleSignInResult getId: " + acct.getId());
            // getDisplayName Google 名稱
            //Log.e(TAG, "handleSignInResult getName: " + acct.getDisplayName());
            // getEmail Google 信箱
            //Log.e(TAG, "handleSignInResult getEmail: " + acct.getEmail());
            // getPhotoUrl Google 大頭照圖片連結
            //Log.e(TAG, "handleSignInResult getPhotoUrl: " + acct.getPhotoUrl());

            String id = acct.getId();
            String name = acct.getDisplayName();
            String email = acct.getEmail();
            String googleImageUrl = String.valueOf(acct.getPhotoUrl());// Google 大頭貼網址

            String url = Common.URL_SERVER + "SettingServlet";

            JsonObject jo = new JsonObject();
            jo.addProperty("action", "googleSignIn");
            jo.addProperty("user_id", id);
            jo.addProperty("user_pw", id);// Google 登入的話，帳號同密碼
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

            boolean googleSignIn = false;

            try {
                String jsonIn = signUpTask.execute().get();
                jo = gson.fromJson(jsonIn, JsonObject.class);

                googleSignIn = jo.get("googleSignIn").getAsBoolean();

                Log.e(TAG, "googleSignIn = " + googleSignIn);

            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }

            if (googleSignIn) {

                Log.d(TAG, "Google 登入成功。");
                Common.toastShow(activity, "Google 登入成功");

                /* 將登入的會員資料存入偏好設定 */
                SharedPreferences pref = activity.getSharedPreferences("preference",
                        MODE_PRIVATE);
                pref.edit()
                        .putInt("user_no", jo.get("user_no").getAsInt())
                        .putString("user_id", id)
                        .putString("user_name", name)
                        .putBoolean("isSignIn", true)
                        .putBoolean("GoogleSignIn",true)
                        .putString("GoogleUserImage", googleImageUrl)
                        .apply();

                /* 延時執行 */
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Log.d(TAG, "登入成功，此頁消失");
                        activity.finish();
                        // finish() 讓視窗（Activity）消失

                    }
                }, 2 * 1000);// 延後2秒

            } else{
                Log.e(TAG, "Google 登入失敗。");
                Common.toastShow(activity, "Google 登入失敗");
            }


        } catch (ApiException e){
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "SignInResult: failed code = " + e.getStatusCode());
        }

    }

    /* 隱藏鍵盤方法 */
    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(),0);
    }

}