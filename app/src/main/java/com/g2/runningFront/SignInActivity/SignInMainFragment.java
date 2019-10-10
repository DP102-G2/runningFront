package com.g2.runningFront.SignInActivity;

import android.app.Activity;
import android.content.Context;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.g2.runningFront.Common.Common;
import com.g2.runningFront.Common.CommonTask;
import com.g2.runningFront.Common.User;
import com.g2.runningFront.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;

/* Google 登入 imports */

public class SignInMainFragment extends Fragment {
    private static final String TAG = "TAG_SignIn_Main";
    private Activity activity;

    /* Google 登入宣告 */
    private GoogleSignInClient googleSignInClient;
    private static final int GSIGN_CODE = 101;

    private EditText etId, etPassword;
    private TextView textView;

    private Gson gson;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();

        gson = new Gson();

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
        textView = view.findViewById(R.id.textView);

        view.findViewById(R.id.btSignIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /* 隱藏 OR 顯示輸入鍵盤 */
                /*InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);*/
                hideKeyboard(activity);

                String id = etId.getText().toString().trim();
                String password = etId.getText().toString().trim();
                User user = new User(0, id, password);

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

                        if(user == null){
                            textView.setText("輸入之帳號或密碼不正確");
                            Common.toastShow(activity, "輸入之帳號或密碼不正確");
                        } else {

                            textView.setText("一般登入成功\n"+"User_ID:\t\t"+user.getId()
                                    +"\nUser_PW:\t\t"+user.getPassword()
                                    +"\nUser_No:\t\t"+user.getNo());
                            Common.toastShow(activity,"一般登入成功");

                            /* 將登入資料存入偏好設定 */
                            SharedPreferences pref = activity.getSharedPreferences("preference",
                                    MODE_PRIVATE);
                            pref.edit()
                                    .putInt("user_no", user.getNo())
                                    .putString("user_id", user.getId())
                                    .putBoolean("isSignIn", true)
                                    .apply();

//                            /* 回應成功代碼（傳回去發出 Intent 的頁面） */
//                            setResult(RESULT_OK);

                            /* 延時執行 */
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    Log.d(TAG, "登入成功，此頁消失");
                                    activity.finish();// finish() 讓視窗（Activity）消失
                                }
                            },2000);// 延後2秒
                        }

                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                }

            }
        });

        /*============================== Google 登入程式碼 ==============================*/
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(activity, gso);

        /* Google 登入按鈕 */
        view.findViewById(R.id.btGSignIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent googleSignIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(googleSignIntent, GSIGN_CODE);

            }
        });

        SignInButton signInButton = view.findViewById(R.id.btGSignIn);
        signInButton.setSize(SignInButton.SIZE_WIDE);

        /* 登出按鈕 */
        view.findViewById(R.id.btSignOut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSignInClient.signOut();
                textView.setText("Google 已登出");
            }
        });
    }

    /* 承接 Google 登入結果 */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GSIGN_CODE){
            Task<GoogleSignInAccount> gTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(gTask);
        }

    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask){
        try{

            GoogleSignInAccount acct = completedTask.getResult(ApiException.class);

            // 取得使用者的資料，取得後再看你想要對這些資料做什麼用途
            // getId 21位 Google帳戶編號
            Log.d(TAG, "handleSignInResult getId:\n" + acct.getId());
            // getDisplayName Google 名稱
            Log.d(TAG, "handleSignInResult getName: " + acct.getDisplayName());
            // getEmail Google 信箱
            Log.d(TAG, "handleSignInResult getEmail: " + acct.getEmail());
            // getPhotoUrl Google 大頭照圖片連結
            Log.d(TAG, "handleSignInResult getPhotoUrl: " + acct.getPhotoUrl());

            Bundle bundle = new Bundle();
            bundle.putString("image" ,String.valueOf(acct.getPhotoUrl()));
            Navigation.findNavController(textView)
                    .navigate(R.id.action_settingMainFragment_to_settingUpadteFragment, bundle);

        } catch (ApiException e){
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