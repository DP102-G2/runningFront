package com.g2.runningFront.SettingActivity;

import android.app.Activity;
import android.content.Intent;// Intent
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;

import android.util.Log;// 除錯用
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.g2.runningFront.R;// res 目錄
import static android.view.View.GONE;/// UI

import com.google.android.gms.auth.api.signin.GoogleSignIn;// Google 登入
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class SettingMainFragment extends Fragment {
    private static final String TAG = "TAG_SETmain";
    private Activity activity;
    /* 宣告 GoogleSignInClient */
    public GoogleSignInClient mGoogleSignInClient;
    public static final int GOOGLE_SIGNIN = 5;

    private TextView textView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);

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
        Log.d(TAG, "onViewCreated");

        view.findViewById(R.id.btSignInGoogle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent googleSignInIntent = mGoogleSignInClient.getSignInIntent();
//                startActivity(googleSignInIntent, GOOGLE_SIGNIN);

            }
        });

        view.findViewById(R.id.btSignOut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setVisibility(GONE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GOOGLE_SIGNIN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }

    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask){
        try{
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            Log.d(TAG, "handleSignInResult getName: " + account.getDisplayName());
            Log.d(TAG, "handleSignInResult getEmail: " + account.getEmail());

        } catch (ApiException e){
            Log.w(TAG, "SignInResult: failed code = " + e.getStatusCode());
        }
    }
}