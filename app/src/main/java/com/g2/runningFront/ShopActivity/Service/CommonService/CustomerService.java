package com.g2.runningFront.ShopActivity.Server.CommonServer;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.g2.runningFront.Common.Common;
import com.g2.runningFront.R;
import com.g2.runningFront.RunActivity.MainActivity;
import com.g2.runningFront.ShopActivity.Server.Message;
import com.g2.runningFront.ShopActivity.ShopActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class CustomerService extends Service {
    private static final String TAG = "TAG_CsService";
    private PowerManager.WakeLock wakeLock;

    private final static int NOTIFICATION_ID = 0;
    private final static String NOTIFICATION_CHANNEL_ID = "Channel01";
    private LocalBroadcastManager broadcastManager;

    private int user_no;
    private String socket_no;
    private Context context;
    private Gson gson;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        acquireWakeLock();
        context = getApplicationContext();
        user_no = Common.getUserNo(context);


        broadcastManager = LocalBroadcastManager.getInstance(context);
        registerChatReceiver();

        socket_no = String.valueOf(user_no);
        ServiceCommon.connectServer(context,socket_no);
    }

    private void acquireWakeLock(){
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (powerManager != null && wakeLock == null){

            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"Service:WakeLock");
            wakeLock.acquire();
            Log.d(TAG,"acquireWakeLock");
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseWakeLock();
    }




    private void registerChatReceiver() {
        IntentFilter chatFilter = new IntentFilter("new Message");
        // 攔截chat廣播
        broadcastManager.registerReceiver(chatReceiver, chatFilter);
        // 如果有我們就來做CHATRECEIVER
    }

    public static BroadcastReceiver chatReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Gson gson = Common.getTimeStampGson();

            String jsStr = intent.getStringExtra("message");

            JsonObject jsonObject = gson.fromJson(jsStr,JsonObject.class);

            String message = jsonObject.get("message").getAsString();
            Message chatMessage = gson.fromJson(message, Message.class);

            NotificationManager notificationManager;
            notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                NotificationChannel notificationChannel = new NotificationChannel(
                        NOTIFICATION_CHANNEL_ID,
                        "MyNotificationChannel",
                        NotificationManager.IMPORTANCE_HIGH);

                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.enableVibration(true);
                notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
                notificationManager.createNotificationChannel(notificationChannel);
                // 設定通知訊息
            }

            Intent intentService = new Intent(context, ShopActivity.class);
            intentService.putExtra("action","toService");

            //包裝要發生的ITENT
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    //直接就要發生ITENT，被抑制住才要發生就是PENDINGINTENT
                    context, NOTIFICATION_ID, intentService, PendingIntent.FLAG_UPDATE_CURRENT);
            // 被執行的頁面，要執行的代碼，執行事項，
            //FLAG_UPDATE_CURRENT 就視窗的部分把

            Notification notification = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_server)
                    //顯示出來的圖片
                    .setContentTitle("Running 客服人員")
                    //內容標題
                    .setContentText("訊息："+chatMessage.getMsg_text())
                    .setAutoCancel(true)
                    // 點完是不是直接刪除，如果不響貝USER按掉就不見，可以改為FALSE
                    .setContentIntent(pendingIntent) // 若無開啟頁面可不寫
                    //要去哪裡，並把東西待過去
                    .build();
            notificationManager.notify(NOTIFICATION_ID, notification);
            //要給ID才能SWITCH出誰發出訊息
            //不同ID就會視為不同的通知訊息

        }

    };


    private void releaseWakeLock(){
        if (wakeLock != null){
            try {
                wakeLock.release();
                wakeLock = null;
                Log.d(TAG,"realeaseWakeLock");
            }catch (Exception e){

            }

        }
    }


}
