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

    /* Google 登入宣告 */
    private GoogleSignInClient googleSignInClient;
    private static final int GSIGN_CODE = 101;
    private Button button;

    public GoogleSignInClient gooSignClient;

    private EditText etId, etPassword;
    private TextView tvAt, textView;

    private Gson gson;

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences pref = getSharedPreferences("preference", MODE_PRIVATE);

        boolean isSignIn = pref.getBoolean("isSignIn", false);
        if (isSignIn) {

            setResult(RESULT_OK);
            Log.d(TAG, "onStart 檢查已經登入，此頁不會出現。");
            finish();

        } else {
            pref.edit().putBoolean("isSignIn", false)
                    .apply();
            Log.d(TAG, "onStart 檢查未登入");
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        setTitle("登入 Activity");

        tvAt = findViewById(R.id.tvAt);
        tvAt.setText("這裡是 SigninActivity.java");

        /*============================== 登入程式碼 ==============================*/

        etId = findViewById(R.id.etId);
        etPassword = findViewById(R.id.etPassword);
        textView = findViewById(R.id.textView);

        /* 供下方使用 Gson 方法 */
        gson = new Gson();

        findViewById(R.id.btSignIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /* 隱藏 OR 顯示輸入鍵盤 */
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

                String id = etId.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                User user = new User(0, id , password);

                if(id.length()==0 || password.length()==0){
                    textView.setText("帳號密碼不能為空");
                    return;
                }

                if (Common.networkConnected(SignInActivity.this)) {
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
                            Common.toastShow(SignInActivity.this, "輸入之帳號或密碼不正確");
                        } else {

                            textView.setText("一般登入成功\n"+"User_ID:\t\t"+user.getId()
                                    +"\nUser_PW:\t\t"+user.getPassword()
                                    +"\nUser_No:\t\t"+user.getNo());
                            Common.toastShow(SignInActivity.this, "一般登入成功");

                            /* 將登入資料存入偏好設定 */
                            SharedPreferences pref = getSharedPreferences("preference",
                                    MODE_PRIVATE);
                            pref.edit()
                                    .putInt("user_no", user.getNo())
                                    .putString("user_id", user.getId())
                                    .putBoolean("isSignIn", true)
                                    .apply();

                            /* 回應成功代碼（傳回去發出 Intent 的頁面） */
                            setResult(RESULT_OK);

                            /* 延時執行 */
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    Log.d(TAG, "登入成功，此頁消失");
                                    SignInActivity.this.finish();// finish() 讓視窗（頁面）消失
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

        googleSignInClient = GoogleSignIn.getClient(SignInActivity.this, gso);

        ViewHolder();

        findViewById(R.id.btSignOut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gooSignClient.signOut();
                textView.setText("Google 已登出");
                // 頁面跳換，以下無法顯示
                Common.toastShow(SignInActivity.this, "Google 已登出");
            }
        });

    }


    /* ViewHolder 自訂方法 */
    void ViewHolder() {
        textView = findViewById(R.id.textView);
        findViewById(R.id.btGSignIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, GSIGN_CODE);

            }
        });
    }

    /* 承接 Google 登入結果 */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GSIGN_CODE) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);

        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            textView.setText(account.getEmail());

            // 取得使用者的資料，取得後再看你想要對這些資料做什麼用途
            Log.d(TAG, "handleSignInResult getName:\n" + account.getDisplayName());
            Log.d(TAG, "handleSignInResult getEmail:\n" + account.getEmail());
            Log.d(TAG, "handleSignInResult getId:\n" + account.getId()); // 21位數字
            Common.toastShow(SignInActivity.this, "Google 登入成功");
        } catch (ApiException e) {
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }

    /*
    @Override
    public void onStop() {
        super.onStop();
        if (signInTask != null) {
            signInTask.cancel(true);
        }
    }*/

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