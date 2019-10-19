package com.g2.runningFront.RunActivity.Group;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.g2.runningFront.R;
import com.g2.runningFront.Common.CommonTask;
import com.g2.runningFront.Common.Common;

/* 想使用 QR Code 必須先複製這兩個 Java 至專案內 */
import com.g2.runningFront.RunActivity.Group.Common.Contents;
import com.g2.runningFront.RunActivity.Group.Common.QRCodeEncoder;

/* QR Code API */
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
/* 解碼 QR Code API（當使用相機掃描條碼時） */
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

/* 為了使用 Gson */
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import static android.content.Context.MODE_PRIVATE;


public class GroupQRcodeFragment extends Fragment {
    private static final String TAG = "TAG_QRcode";
    private Activity activity;
    private Button button;
    private ImageView imageView;

    private boolean isSignIn = true;
    private int no;
    private String id;

    private Gson gson;

    @Override
    public void onStart() {
        super.onStart();
        no = Common.getUserNo(activity);
        Log.d(TAG, "user_no: " + no);

        if(no == 0){
            isSignIn = false;
            // 提示需要登入會員？
        }
    }

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
        activity.setTitle("會員條碼");
        return inflater.inflate(R.layout.fragment_group_qrcode, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        button = view.findViewById(R.id.button);
        imageView = view.findViewById(R.id.imageView);

        /* 取得使用者帳號 id */
        id = activity.getSharedPreferences(Common.PREF, MODE_PRIVATE).getString("user_id","ERROR: FIND NO user_id");

        /* 如果有有登入，依照片好設定裡的 user_id 來製作 QR Code 條碼圖片 */
        if(!isSignIn){

            imageView.setImageResource(R.drawable.user_no_image);
            // 不要顯示 QR Code ImageView

            /* 稍後一會，前往登入頁面 */
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Common.signIn(activity);
                }
            },4 * 1000);// 延後 4 秒

        } else {

            if(!id.equals("ERROR: FIND NO user_id")){
                Log.d(TAG, "將用以下帳號產生 QR Code：" + id);

                /* 產生 QR Code 圖像 */
                // 固定圖片大小是 180
                int dimension = 180;

                // 用 QR Code 編碼產生圖片
                QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(id, null,
                        Contents.Type.TEXT, BarcodeFormat.QR_CODE.toString(),
                        dimension);//條碼大小
                try {
                    Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
                    imageView.setImageBitmap(bitmap);

                    /* 改變 view 的圖層順序到最上位（為了蓋過"請先登入會員"的 TextView） */
                    imageView.bringToFront();

                } catch (WriterException e) {
                    Log.e(TAG, e.toString());
                }
            } else{
                imageView.setVisibility(View.GONE);
            }
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /* 若在Activity內需要呼叫IntentIntegrator(Activity)建構式建立IntentIntegrator物件；
                 * 而在Fragment內需要呼叫IntentIntegrator.forSupportFragment(Fragment)建立物件，
                 * 掃瞄完畢時， Fragment.onActivityResult() 才會被呼叫 */
                // IntentIntegrator integrator = new IntentIntegrator(this);
                IntentIntegrator integrator = IntentIntegrator.forSupportFragment(GroupQRcodeFragment.this);

                // Set to true to enable saving the barcode image and sending its path in the result Intent.
                integrator.setBarcodeImageEnabled(true);
                // Set to false to disable beep on scan.
                integrator.setBeepEnabled(true);//掃描發出嗶聲
                // 選擇使用
                // 後鏡頭 0 ； 前鏡頭 1
                integrator.setCameraId(0);
                // By default, the orientation is locked. Set to false to not lock.
                // 拍照畫面是否可以調成直立或橫置（目前無效）
                integrator.setOrientationLocked(false);
                // Set a prompt to display on the capture screen.
                // 設定掃描畫面上的提示文字
                integrator.setPrompt("請將相機對準 QR 條碼");
                // Initiates a scan
                // 開始掃描 QR 碼
                integrator.initiateScan();

            }
        });

    }

    @Override/* onActivityResult 當頁面取得結果時，此方法會被呼叫 */
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null && intentResult.getContents() != null) {

            /* 連線資料庫，將掃描到的 id 加入使用者的追蹤列表 */
            if(Common.networkConnected(activity)){

                String url = Common.URL_SERVER + "GroupServlet";

                // 掃描到的 id 帳號
                String follow_id = intentResult.getContents();

                JsonObject jo = new JsonObject();
                jo.addProperty("action","follow");
                jo.addProperty("user_id", id);
                jo.addProperty("follow_id", follow_id);

                CommonTask FollowTask = new CommonTask(url, jo.toString());

                boolean isFollow = false;
                try{

                    String jsonIn = FollowTask.execute().get();
                    isFollow = gson.fromJson(jsonIn, Boolean.class);
                    Log.d(TAG, "isFollow = " + isFollow);

                } catch (Exception e){
                    Log.e(TAG, e.getMessage());
                }
                if(isFollow){
                    Common.toastShow(activity,"加入追蹤成功");
                } else {
                    Common.toastShow(activity,"加入追蹤失敗\n請重新加入");
                }
            } else {
                Common.toastShow(activity, R.string.textNoNetwork);
            }
        } else {
            Common.toastShow(activity,"找不到 QR 掃描結果\n請使用其它方式");
        }
        /* ==================== ⬇️ ️正在施工區域 ⬇️ ==================== */
    }
}
