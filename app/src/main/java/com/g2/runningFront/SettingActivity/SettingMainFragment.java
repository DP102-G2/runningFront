package com.g2.runningFront.SettingActivity;

import android.app.Activity;
import android.content.Intent;// Intent
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;

import android.util.Log;// 除錯
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.g2.runningFront.R;// res 目錄
import static android.view.View.GONE;/// UI
/* Google 登入 imports */
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class SettingMainFragment extends Fragment {
    private static final String TAG = "TAG_SETmain";
    private Activity activity;
    /* 宣告 GoogleSignInClient */
    public GoogleSignInClient gooSignClient;
    public static final int GSIGN_CODE = 10;

    private TextView textView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        gooSignClient = GoogleSignIn.getClient(activity, gso);

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity.setTitle("登入頁面");
        return inflater.inflate(R.layout.fragment_setting_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
            Log.d(TAG, "handleSignInResult getEmail: " + acct.getEmail());
            Log.d(TAG, "handleSignInResult getPhotoUrl: " + acct.getPhotoUrl());

        } catch (ApiException e){
            Log.w(TAG, "SignInResult: failed code = " + e.getStatusCode());
        }
    }
}