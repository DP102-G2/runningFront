package com.g2.runningFront.ShopActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Common {
    // 實機或模擬器
//	public static String URL = "http://192.168.196.185:8080/TextToJson_Web/";
    // 模擬器
    public final static String URL = "http://10.0.2.2:8080/FriendsDemo_Web/";

    // 檢查是否有網路連線
    public static boolean networkConnected(Context context) {
        ConnectivityManager conManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conManager != null ? conManager.getActiveNetworkInfo() : null;
        return networkInfo != null && networkInfo.isConnected();
    }

}
