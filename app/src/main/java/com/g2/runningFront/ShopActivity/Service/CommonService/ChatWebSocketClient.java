package com.g2.runningFront.ShopActivity.Service.CommonService;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.g2.runningFront.Common.Common;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Locale;

public class ChatWebSocketClient extends WebSocketClient {
    private static final String TAG = "ChatWebSocketClient";
    private LocalBroadcastManager broadcastManager;
    private Gson gson;

    ChatWebSocketClient(URI serverURI, Context context) {
        // Draft_17是連接協議，就是標準的RFC 6455（JSR256）
        super(serverURI, new Draft_17());
        broadcastManager = LocalBroadcastManager.getInstance(context);
        gson = Common.getTimeStampGson();
    }

    @Override
    public void onOpen(ServerHandshake handshakeData) {
        String text = String.format(Locale.getDefault(),
                "onOpen: Http status code = %d; status message = %s",
                handshakeData.getHttpStatus(),
                handshakeData.getHttpStatusMessage());
        Log.d(TAG, "onOpen: " + text);
    }

    @Override
    public void onMessage(String message) {
        // type: 訊息種類，有open(有user連線), close(有user離線), chat(其他user傳送來的聊天訊息)
        // 確認文字訊息的種類，只負責轉發，這邊就不判斷要做什麼事情

        JsonObject jsonObject = gson.fromJson(message, JsonObject.class);
        String action = jsonObject.get("action").getAsString();
        if (action.trim().equals("new Message")) {

            sendMessageBroadcast("new Message", message);
            Log.d(TAG, "onMessage: " + message);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        String text = String.format(Locale.getDefault(),
                "code = %d, reason = %s, remote = %b",
                code, reason, remote);
        Log.d(TAG, "onClose: " + text);
    }

    @Override
    public void onError(Exception ex) {
        Log.d(TAG, "onError: exception = " + ex.toString());
    }

    private void sendMessageBroadcast(String messageType, String message) {
        // 將訊息的種類跟訊息拉出來
        Intent intent = new Intent(messageType);
        intent.putExtra("message", message);
        // 存放資料
        broadcastManager.sendBroadcast(intent);
        // 如果收到資料都會拿到廣播，將某人登入/離線的訊息發送給所有程式碼(區域，所以是此app內)
    }
}