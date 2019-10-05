package com.g2.runningFront.Common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.g2.runningFront.SignInActivity.SignInActivity;

import tech.cherri.tpdirect.api.TPDCard;

import static android.content.Context.MODE_PRIVATE;


public class Common {

    public static String URL_SERVER = "http://10.0.2.2:8080/RunningWeb/";

    /**
     * 確認連網
     * @param activity 目前使用的 Activity
     */
    public static boolean networkConnected(Activity activity) {
        ConnectivityManager conManager =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conManager != null ? conManager.getActiveNetworkInfo() : null;
        return networkInfo != null && networkInfo.isConnected();
    }

    /**
     * Toast 提示訊息
     * @param activity 目前使用的 Activity
     * @param string 想要提示的文字內容
     */
    public static void toastShow(Activity activity ,String string){
        Toast.makeText(activity, string, Toast.LENGTH_LONG).show();
    }

    public static void toastShow(Activity activity ,int optSt){
        Toast.makeText(activity, optSt, Toast.LENGTH_LONG).show();
    }

    public final static String PREF = "preference";

    /**
     *
     * @param activity 目前使用的 Activity
     */
    public static void signIn(Activity activity) {
        boolean isSignIn = false;
        /* 取得當前偏好設定                  .getSharedPreferences */
        SharedPreferences pref  = activity.getSharedPreferences(Common.PREF, MODE_PRIVATE);
        try {
            isSignIn = pref.getBoolean("isSignIn", false);
        } catch (ClassCastException c) {
            c.printStackTrace();
        } finally {
            if (!isSignIn) {
                /* 檢查到未登入，將切換至登入頁 */
                Intent signInIntent = new Intent(activity, SignInActivity.class);

                /* 請求登入頁面的結果（登入成功/登入失敗）
                 * startActivityForResult(Intent, 請求代碼); */
                activity.startActivity(signInIntent);
                //activity.startActivityForResult(signInIntent, Common.REQ_SIGNIN);
                return;
            }
        }
    }

    public static void signIn(Activity activity, int requestCode) {
        boolean isSignIn = false;
        SharedPreferences pref  = activity.getSharedPreferences(Common.PREF, MODE_PRIVATE);
        try {
            isSignIn = pref.getBoolean("isSignIn", false);
        } catch (ClassCastException c) {
            c.printStackTrace();
        } finally {
            if (!isSignIn) {
                Intent signInIntent = new Intent(activity, SignInActivity.class);
                activity.startActivityForResult(signInIntent, requestCode);
                return;
            }
        }
    }

    public static final TPDCard.CardType[] CARD_TYPES = new TPDCard.CardType[]{
            TPDCard.CardType.Visa
            , TPDCard.CardType.MasterCard
            , TPDCard.CardType.JCB
            , TPDCard.CardType.AmericanExpress
    };
}
