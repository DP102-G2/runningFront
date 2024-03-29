package com.g2.runningFront.SettingActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.JsonObject;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;

import com.g2.runningFront.Common.*;
import com.g2.runningFront.R;

/* 有關從偏好設定找出使用者資訊 */
import android.content.SharedPreferences;
import static com.g2.runningFront.Common.Common.PREF;
import static android.content.Context.MODE_PRIVATE;

/* 有關 Google 登入登出 */
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

/* 有關圖片處理 */
import com.squareup.picasso.Picasso;// 圖片資源載入優化套件
import android.util.Base64;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import static com.g2.runningFront.Common.Common.round;

/* 有關延後執行程式碼 */
import android.os.Handler;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;

import com.g2.runningFront.SignInActivity.SignInActivity;

public class SettingMainFragment extends Fragment {
    private static String TAG = "TAG_SettingMain";
    private Activity activity;

    /* 偏好設定檔案 */
    private SharedPreferences pref;

    /* Google 登入資訊 */
    private GoogleSignInClient googleSignInClient;

    private ImageView imageView;
    private TextView tvName;

    /* 請求登入的代碼 */
    private static final int REQ_SIGNIN = 50;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = getActivity();

        /* Google 登入登出 */
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .build();
        googleSignInClient = GoogleSignIn.getClient(activity, gso);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity.setTitle("設定主頁面");
        return inflater.inflate(R.layout.fragment_setting_main, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        /* 在 onsStart 先檢查登入狀態 */
        Common.signIn(activity);

        /* 取得偏好設定中的會員資訊 */
        pref = activity.getSharedPreferences(PREF, MODE_PRIVATE);
        /* 顯示偏好設定中的會員暱稱 */
        String name = pref.getString("user_name","");
        tvName.setText(name);

        /* 取得會員大頭貼圖像 */
        // 如果是 Google 登入，則從 Google 網址抓取大頭貼
        if(pref.getBoolean("GoogleSignIn",false) == true) {
            String imgUrl = pref.getString("GoogleUserImage", "No URL");

            Picasso
                    .with(activity)
                    .load(imgUrl)
                    .error(R.drawable.user_no_image)
                    //.resize(140, 140)
                    .into(imageView);

        } else {// 如果是一般登入，去 Servlet 查詢會員圖片

            int no = pref.getInt("user_no", 0);
            Log.d(TAG, "onStart 檢查到會員編號：" + no);

            String url = Common.URL_SERVER + "SettingServlet";

            JsonObject jo = new JsonObject();
            jo.addProperty("action", "getImage");
            jo.addProperty("user_no", no);

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

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imageView = view.findViewById(R.id.imageView);
        tvName = view.findViewById(R.id.tvName);

        /* 修改會資料按鈕 */
        view.findViewById(R.id.btUpdate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int no = activity.getSharedPreferences(Common.PREF, MODE_PRIVATE).getInt("user_no", 0);
                Bundle bundle = new Bundle();
                bundle.putInt("user_no", no);

                Navigation.findNavController(imageView).
                        navigate(R.id.action_settingMainFragment_to_settingUpadteFragment, bundle);
            }

        });

        /* 登出按鈕 */
        view.findViewById(R.id.btSignOut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(activity)
                        .setTitle("登出會員")
                        .setIcon(R.drawable.ic_cross_red)
                        .setMessage("確定要登出嗎？")

                        .setPositiveButton("登出", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                pref.edit()
                                        /* 清除偏好設定 */
                                        //.putBoolean("isSignIn", false)
                                        .clear()
                                        .apply();

                                /* Google 登出帳號 */
                                signOut();

                                String signoutText = pref.getString("user_name","偏好設定已清空");
                                Log.e(TAG, signoutText);


                                /* 延時執行 */
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        // 請求轉往登入頁面
                                        Intent signInIntent = new Intent(activity, SignInActivity.class);
                                        startActivity(signInIntent);

                                        // 讓 Activity 結束
                                        // activity.finish();
                                    }
                                }, 2 * 1000);// 延後 2 秒

                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .show();
            }
        });
    }

    /* Google 登出方法 */
    private void signOut() {
        googleSignInClient.signOut()
                .addOnCompleteListener(activity, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Clear preferences balabala...
                    }
                });
    }

    /* 分析請求代碼，判斷如何處理 */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        SharedPreferences pref = activity.getSharedPreferences(Common.PREF,MODE_PRIVATE);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQ_SIGNIN:
                    break;
                default:
                    break;
            }
        }
    }

}