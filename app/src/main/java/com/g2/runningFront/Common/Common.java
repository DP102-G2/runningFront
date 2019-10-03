package com.g2.runningFront.Common;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class Common {

    public static String URL_SERVER = "http://10.0.2.2:8080/RunningWeb/";

    // 確認連網
    public static boolean networkConnected(Activity activity) {
        ConnectivityManager conManager =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conManager != null ? conManager.getActiveNetworkInfo() : null;
        return networkInfo != null && networkInfo.isConnected();
    }

    public static void toastShow(Activity activity ,String optSt){
        Toast.makeText(activity,optSt,Toast.LENGTH_LONG).show();
    }

    public static void toastShow(Activity activity ,int optSt){
        Toast.makeText(activity,optSt,Toast.LENGTH_LONG).show();
    }

}
