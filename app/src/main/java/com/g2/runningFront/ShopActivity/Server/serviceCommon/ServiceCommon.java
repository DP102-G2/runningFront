package com.g2.runningFront.ShopActivity.Server.serviceCommon;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.net.URI;
import java.net.URISyntaxException;

import static android.content.Context.MODE_PRIVATE;

public class ServiceCommon {


    private final static String TAG = "CommonTwo";
    public static final String SERVER_URI = "ws://10.0.2.2:8080/RunningWeb/ServiceServer/";
    public static ChatWebSocketClient chatWebSocketClient;
    //讓他變成全域型，以因應隨時都要使用的事情
    // 這個是一個我們自己所撰寫的類別，只要連線成功後，就會持續使用

    // 建立WebSocket連線
    public static void connectServer(Context context, String userName) {
        URI uri = null;
        try {
            uri = new URI(SERVER_URI + userName);
            //你是誰？MARY!!!
        } catch (URISyntaxException e) {
            Log.e(TAG, e.toString());
        }
        if (chatWebSocketClient == null) {
            //如果沒有連線，我們就來連線~
            chatWebSocketClient = new ChatWebSocketClient(uri, context);
            chatWebSocketClient.connect();
            // 將Server連線好
        }
    }

    // 中斷WebSocket連線
    public static void disconnectServer() {
        if (chatWebSocketClient != null) {
            chatWebSocketClient.close();
            chatWebSocketClient = null;
        }
    }

    public static void saveUserName(Context context, String userName) {

        // 將USER資料除存在系統偏好設定裡面，以避免BUNDLE辣值
        SharedPreferences preferences =
                context.getSharedPreferences("user", MODE_PRIVATE);
        preferences.edit().putString("userName", userName).apply();
        //放入放入
    }

    public static String loadUserName(Context context) {
        SharedPreferences preferences =
                context.getSharedPreferences("user", MODE_PRIVATE);
        String userName = preferences.getString("userName", "");
        Log.d(TAG, "userName = " + userName);
        return userName;
    }

}

