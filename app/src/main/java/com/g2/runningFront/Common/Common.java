package com.g2.runningFront.Common;

import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
/* 有關隱藏鍵盤 */
import android.view.View;
import android.view.inputmethod.InputMethodManager;
/* 有關大頭貼改成圓形 */
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;

import android.net.NetworkInfo;
import android.net.ConnectivityManager;

import android.widget.Toast;

import com.g2.runningFront.SignInActivity.SignInActivity;

import java.sql.Timestamp;
import java.util.Calendar;

import tech.cherri.tpdirect.api.TPDCard;

import static android.content.Context.MODE_PRIVATE;


public class Common {

    public static String URL_SERVER = "http://10.0.2.2:8080/RunningWeb/";
    // 底下為安裝至手機時改用的位址（必須在連線同一個區域網路）
    // public static String URL_SERVER = "http://192.168.196.207/RunningWeb/";

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
    // 偏好設定的變數名稱
    public final static String PREF = "preference";

    /**
     * 登入（檢查登入情形）
     * @param activity  目前使用的 Activity
     */
    public static void signIn(Activity activity) {
        boolean isSignIn = false;
        /* 取得當前偏好設定                  .getSharedPreferences */
        SharedPreferences pref  = activity.getSharedPreferences(PREF, MODE_PRIVATE);
        try {
            isSignIn = pref.getBoolean("isSignIn", false);
        } catch (ClassCastException c) {
            c.printStackTrace();
        } finally {
            if (!isSignIn) {
                /* 檢查到未登入，發起意圖：切換至登入頁 */
                Intent signInIntent = new Intent(activity, SignInActivity.class);
                activity.startActivity(signInIntent);
                return;
            }
        }
    }

    public static void signIn(Activity activity, int requestCode) {
        boolean isSignIn = false;
        SharedPreferences pref  = activity.getSharedPreferences(PREF, MODE_PRIVATE);
        try {
            isSignIn = pref.getBoolean("isSignIn", false);
        } catch (ClassCastException c) {
            c.printStackTrace();
        } finally {
            if (!isSignIn) {
                Intent signInIntent = new Intent(activity, SignInActivity.class);
                /* 請求登入頁面的結果（登入成功OR登入失敗）*/
                activity.startActivityForResult(signInIntent, requestCode);
                return;
            }
        }
    }

    /**
     * 查詢登入中的會員編號
     * @param activity  目前使用的 Activity
     * @return          返回使用者編號，預設回傳 0
     */
    public static int getUserNo(Activity activity) {
        int no = 0;
        try {
            no = activity.getSharedPreferences(PREF, MODE_PRIVATE).getInt("user_no", 0);
        } catch (ClassCastException c) {
            c.printStackTrace();
        }
        return no;
    }

    public static final TPDCard.CardType[] CARD_TYPES = new TPDCard.CardType[]{
            TPDCard.CardType.Visa
            , TPDCard.CardType.MasterCard
            , TPDCard.CardType.JCB
            , TPDCard.CardType.AmericanExpress
    };

    /**
     * 呼叫隱藏鍵盤的指令
     * @param activity  目前使用的 Activity
     */
    public static void hideKeys(Activity activity){
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 把圖片改成同樣大小的圓形圖片（用於大頭貼）
     * @param bitmap 原始圖片資源
     */
    public static Bitmap round(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int r = 0;
        //取照片的寬跟高，並將較短的一邊當做基準邊
        if (width > height) {
            r = height;
        } else {
            r = width;
        }
        //建構一個bitmap
        Bitmap backgroundBmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        //new一个Canvas，並在backgroundBmp上畫圈
        Canvas canvas = new Canvas(backgroundBmp);
        Paint paint = new Paint();
        //設置邊緣光滑，去掉鋸齒狀
        paint.setAntiAlias(true);
        //要抓寬高相等，就是取正方形，再畫成圓形
        RectF rect = new RectF(0, 0, r, r);
        //透過制定的rect畫一個圓角矩形，當圓角X軸方向的半徑等於Y軸方向的半徑，
        //且都等于r/2时，畫出來的圓角矩形就是圓形
        canvas.drawRoundRect(rect, r / 2, r / 2, paint);
        //設置當兩個圖形相交時的模式，SRC_IN為取SRC圖形相交的部分，多餘的將被去掉
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        //canvas將bitmap畫在backgroundBmp上
        canvas.drawBitmap(bitmap, null, rect, paint);
        //回傳製作完成的backgroundBmp
        return backgroundBmp;
    }

    public static String getWeekDay(Timestamp timestamp){

        Calendar cal = Calendar.getInstance();
        cal.setTime(timestamp);
        cal.add(Calendar.DAY_OF_MONTH, -1);

        cal.setFirstDayOfWeek(Calendar.MONDAY);
        int day = cal.get(Calendar.DAY_OF_WEEK);
        String dayStr = null;

        switch (day){
            case 1:
                dayStr = "星期一";
                break;
            case 2:
                dayStr = "星期二";
                break;
            case 3:
                dayStr = "星期三";
                break;
            case 4:
                dayStr = "星期四";
                break;
            case 5:
                dayStr = "星期五";
                break;
            case 6:
                dayStr = "星期六";
                break;
            case 7:
                dayStr = "星期日";
                break;
        }

        return dayStr;
    }

    /**
     * 用正規表達式檢查字串，回傳布林值
     * @param aString    任意字串
     * @return boolean
     */
    public static boolean matches(String aString){
        return aString.matches("正規表達式");
        // 規範                              正規表達式
        // 長度4-16，由數字、英文字母、_ 組成 --  ^\\w{4,16}$
        // 長度4以上的密碼 --                  ^[A-Za-z0-9]+.{3,}$
        // Email --                         ^\w+(\w+)@\w+([-.]\w+).\w+([-.]\w+)*$
        // 由數字或英文字母組成 --              ^[A-Za-z0-9]+$
    }

}