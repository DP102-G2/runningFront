package com.g2.runningFront.SignInActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import static android.view.View.GONE;

/* Google 登入 imports */

public class SignInMainFragment extends Fragment {
    private static final String TAG = "TAG_SETmain";
    private Activity activity;
    /* 宣告 GoogleSignInClient */
    public GoogleSignInClient gooSignClient;
    public static final int GSIGN_CODE = 10;

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
        gooSignClient = GoogleSignIn.getClient(activity, gso);
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
                String id = etId.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                User user = new User(0, id , password);

                if(id.length()==0 || password.length()==0){
                    textView.setText("帳號密碼不能為空");
                    return;
                }

                if (Common.networkConnected(activity)) {
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
                            Toast.makeText(activity, "輸入之帳號或密碼不正確", Toast.LENGTH_LONG);
                        } else {
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("user", user);
                            Navigation.findNavController(textView)
                                    .navigate(R.id.action_settingMainFragment_to_settingFragment, bundle);
                        }

                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }
        });

        /* Google 登入按鈕 */
        view.findViewById(R.id.btGSignIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent googleSignIntent = gooSignClient.getSignInIntent();
                startActivityForResult(googleSignIntent, GSIGN_CODE);

            }
        });

        view.findViewById(R.id.btgooSignIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent googleSignIntent = gooSignClient.getSignInIntent();
                startActivityForResult(googleSignIntent, GSIGN_CODE);

            }
        });

        SignInButton signInButton = view.findViewById(R.id.btGSignIn);
        signInButton.setSize(SignInButton.SIZE_WIDE);

        view.findViewById(R.id.btSignOut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gooSignClient.signOut();
                textView.setText("Google 已登出");
                view.setVisibility(GONE);
            }
        });
    }

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

            // getDisplayName Google 名稱
            Log.d(TAG, "handleSignInResult getName: " + acct.getDisplayName());
            // getEmail Google 信箱
            Log.d(TAG, "handleSignInResult getEmail: " + acct.getEmail());
            // getPhotoUrl Google 大頭照圖片連結
            Log.d(TAG, "handleSignInResult getPhotoUrl: " + acct.getPhotoUrl());

            Bundle bundle = new Bundle();
            bundle.putString("image" ,String.valueOf(acct.getPhotoUrl()));
            Navigation.findNavController(textView)
                    .navigate(R.id.action_settingMainFragment_to_settingFragment, bundle);

        } catch (ApiException e){
            Log.w(TAG, "SignInResult: failed code = " + e.getStatusCode());
        }
    }
}