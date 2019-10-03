package com.g2.runningFront.SignInActivity;

/* 有關於隱藏/顯示輸入鍵盤 */
import android.content.Context;
import android.content.Intent;
import android.view.inputmethod.InputMethodManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import static android.view.View.GONE;// 有關隱藏按鈕

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.g2.runningFront.Common.*;
import com.g2.runningFront.R;

/* Gson */
import com.google.gson.Gson;
import com.google.gson.JsonObject;

/* Google 登入 */
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        setTitle("登入 Activity");

        tvAt = findViewById(R.id.tvAt);
        tvAt.setText("這裡是 SigninActivity.java");

        /*============================== Google 登入程式碼 ==============================*/

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(SignInActivity.this, gso);

        ViewHolder();

        /*============================== 登入程式碼 ==============================*/

        etId = findViewById(R.id.etId);
        etPassword = findViewById(R.id.etPassword);
        textView = findViewById(R.id.textView);

        /* 供下方使用 Gson 方法 */
        gson = new Gson();

        findViewById(R.id.btSignIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /* 隱藏/顯示輸入鍵盤 */
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

                    CommonTask loginTask = new CommonTask(url, outStr);

                    try {

                        String strIn = loginTask.execute().get();
                        user = gson.fromJson(strIn, User.class);

                        if(user == null){
                            textView.setText("輸入之帳號或密碼不正確");
                            Common.toast(SignInActivity.this, "輸入之帳號或密碼不正確");
                        } else {
                            /*
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("user", user);
                             */
                            textView.setText("一般登入成功\n"+"User_ID:\t\t"+user.getId()
                                    +"\nUser_PW:\t\t"+user.getPassword());
                            Common.toast(SignInActivity.this, "一般登入成功");
                        }

                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }
        });

        findViewById(R.id.btSignOut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gooSignClient.signOut();
                textView.setText("Google 已登出");
            }
        });

    }


    /* ViewHolder 自訂方法 */
    void ViewHolder() {
        textView = findViewById(R.id.tvTest);
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
            Log.d(TAG, "handleSignInResult getEmail\n:" + account.getEmail());
        } catch (ApiException e) {
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }

}