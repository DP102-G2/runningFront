package com.g2.runningFront.RunActivity.Group;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.g2.runningFront.Common.Common;
import com.g2.runningFront.Common.CommonTask;
import com.g2.runningFront.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.g2.runningFront.Common.Common.round;


public class FriendFragment extends Fragment {

    private static String TAG = "TAG_GroupFragment";
    private Activity activity;
//    List<Personal> personals = new ArrayList<>();
    private CommonTask GetPersonalTask;
//    private int no;
    private Gson gson;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        gson = new Gson();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle(R.string.textFriend);
        return inflater.inflate(R.layout.fragment_friend, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView imageView = view.findViewById(R.id.imageView);
        TextView tvId = view.findViewById(R.id.tvId);
        TextView tvName = view.findViewById(R.id.tvName);
        TextView tvtotal_time = view.findViewById(R.id.tvtotal_time);
        TextView tvtotal_dis = view.findViewById(R.id.tvdis);
        TextView tvjoin = view.findViewById(R.id.tvjoin);
        TextView tvlove = view.findViewById(R.id.tvlove);

        final Bundle bundle = getArguments();


        if (bundle == null || bundle.getSerializable("user_no") == null) {
            Log.e(TAG, "讀入的 user_no 為空值");
            return;// 沒通過 bundle 檢驗,就跳出
        } else {
            int no = bundle.getInt("user_no");
            Log.d(TAG, "讀入的 user_no：" + no);

            if (Common.networkConnected(activity)) {

                /* 取得會員資料（純文字） */
                String url = Common.URL_SERVER + "PersonalServlet";

                JsonObject jo = new JsonObject();
                jo.addProperty("action", "showPersonalInfo");
                jo.addProperty("user_no", no);
                String outStr = jo.toString();

                CommonTask loginTask = new CommonTask(url, outStr);

                try {

                    String strIn = loginTask.execute().get();
                   jo = gson.fromJson(strIn, JsonObject.class);
                    //user = gson.fromJson(strIn, User.class);

                    if (jo  == null) {
                       // Common.toastShow(activity, "找不到會員資料");
                        Log.e(TAG, "ooooooooo：\n" + jo );

                    } else {

                        Log.e(TAG, "傳回的 JsonObject：\n" + jo );
                        int i=10;
                        /* 印出會員資料 */
                        tvName.setText(jo.get("user_name").getAsString());
                        tvtotal_time.setText(Common.secondToString((int)jo.get("user_runtime").getAsDouble()));
                        tvId.setText(String.valueOf(jo.get("user_id").getAsString()));

//                        double distance = jo.get("user_rundis").getAsDouble();
//                        DecimalFormat df = new DecimalFormat("##.00");
//                        String distanceStr = df.format(distance/1000);

                        tvtotal_dis.setText(String.valueOf(jo.get("user_rundis").getAsDouble()));
                        tvjoin.setText(jo.get("user_regtime").getAsString());
//                        tvjoin.setText("99999999");
                        tvlove.setText(jo.get("user_love").getAsInt()+"");


                    }

                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                    Log.e(TAG, "傳回的 JsonObjectxxxxxxxxxxxxxx：\n");
                }
                /* 取得會員大頭貼圖像 */
                JsonObject jo_image = new JsonObject();
                jo_image.addProperty("action","getImage");
                jo_image.addProperty("user_no", no);

                String url_image = Common.URL_SERVER + "SettingServlet";
                CommonTask imageTask = new CommonTask(url_image, jo_image.toString());

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
                    imageView.setImageBitmap(round(bitmap));
                } else {
                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.user_no_image);
                    imageView.setImageBitmap(round(bitmap));
                }


                //取得Bundle物件
//                Bundle bundle = getArguments();
//                //如果Bundle不為null，進一步取得Friend物件
//                if (bundle != null) {
//                    int no = bundle.getInt("user_no");
//                    //getPersonal();
////                    Personal personal = bundle == null ? null : (Personal) bundle.get("user_no");
//                    //如果Personal物件不為null，顯示各個屬性值，否則顯示錯誤訊息
//                    if (bundle != null) {
//
//                        tvId.setText(String.valueOf(no));
//                        tvId.setText(String.valueOf(personal.getUser_no()));

            }


        }
    }
}


