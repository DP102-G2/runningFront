package com.g2.runningFront.ShopActivity.Server;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.g2.runningFront.Common.Common;
import com.g2.runningFront.Common.CommonTask;
import com.g2.runningFront.R;
import com.g2.runningFront.RunActivity.MainActivity;
import com.g2.runningFront.ShopActivity.Server.serviceCommon.ServiceCommon;
import com.g2.runningFront.ShopActivity.ShopActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShopServerFragment extends Fragment {

    private static final String TAG = "TAG_ChatFragment";
    private LocalBroadcastManager broadcastManager;
    private String socket_no ;

    ShopActivity shopActivity;
    Activity activity;

    List<Message> messageList;

    mesAdapter adapter;
    RecyclerView rvList;
    EditText etMessage;
    Button btSubmit;
    View view;

    int user_no;

    CommonTask commonTask;
    private static final String url = Common.URL_SERVER + "ServerServlet";

    Gson gson;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shopActivity = (ShopActivity) getActivity();
        shopActivity.btbShop.setVisibility(View.GONE);
        activity = getActivity();
        gson = Common.getTimeStampGson();
        user_no =Common.getUserNo(activity);

        socket_no = String.valueOf(user_no);

        broadcastManager = LocalBroadcastManager.getInstance(activity);
        registerChatReceiver();

        ServiceCommon.connectServer(activity,socket_no);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shop_server, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view =view;
        messageList = getMessageList();
        holdView();
        rvList.scrollToPosition(adapter.getItemCount()-1);
    }


    private class mesAdapter extends RecyclerView.Adapter<mesAdapter.messageViewHolder> {
        Activity activity;
        List<Message> messages;
        LayoutInflater layoutInflater;
        int imageSize;

        public void setMessages(List<Message> messages) {
            this.messages = messages;
        }

        public mesAdapter(Activity activity, List<Message> messageList) {
            this.activity = activity;
            this.messages = messageList;
            layoutInflater = LayoutInflater.from(activity);
            imageSize = getResources().getDisplayMetrics().widthPixels;

        }

        @NonNull
        @Override
        public mesAdapter.messageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.item_view_server, parent, false);

            return new messageViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull mesAdapter.messageViewHolder holder, int position) {
            final Message message = messages.get(position);
            holder.tvText.setText(message.getMsg_text());
            if (message.getMsg_by() == 0) {
                holder.cardView.setCardBackgroundColor(activity.getColor(R.color.colorBrown));
                holder.cardView.setTranslationX(0);

            }else {
                holder.cardView.setCardBackgroundColor(activity.getColor(R.color.colorPrimary));
                holder.cardView.setTranslationX(imageSize-800);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Common.toastShow(activity, message.msg_time.toString());
                    rvList.smoothScrollToPosition(adapter.getItemCount());

                }
            });



        }

        @Override
        public int getItemCount() {
            return messages.size();
        }

        private class messageViewHolder extends RecyclerView.ViewHolder {

            CardView cardView;
            TextView tvText;

            public messageViewHolder(View itemView) {
                super(itemView);
                cardView = itemView.findViewById(R.id.server_item_cardView);
                tvText = itemView.findViewById(R.id.server_item_tvText);
            }
        }
    }

    private void holdView() {

        etMessage = view.findViewById(R.id.server_etMessage);
        btSubmit = view.findViewById(R.id.server_btSubmit);
        rvList = view.findViewById(R.id.server_sv);

        rvList.setLayoutManager(new LinearLayoutManager(activity));
        rvList.setAdapter(new mesAdapter(activity, messageList));
        adapter = (mesAdapter) rvList.getAdapter();


        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (etMessage.getText().toString().equals("")) {
                    Common.toastShow(activity, "請輸入文字訊息");
                } else {
                    insertMessage();
                    pushToSocket();
                }
            }
        });

    }


    private void insertMessage() {
        String text = etMessage.getText().toString();

        Message message = new Message(user_no, 1, text, 1);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "insertMessage");
        jsonObject.addProperty("message", new Gson().toJson(message));

        commonTask = new CommonTask(url, jsonObject.toString());

        try {
            String messageListStr = commonTask.execute().get();
            Message nMessage = gson.fromJson(messageListStr, Message.class);
            messageList.add(nMessage);
            adapter.setMessages(messageList);
            adapter.notifyDataSetChanged();
            rvList.smoothScrollToPosition(adapter.getItemCount());


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private List<Message> getMessageList() {

        List<Message> messageList = new ArrayList<>();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "getMessageList");
        jsonObject.addProperty("user_no", user_no);

        commonTask = new CommonTask(url, jsonObject.toString());

        try {
            String messageListStr = commonTask.execute().get();
            Type typeList = new TypeToken<List<Message>>() {
            }.getType();

            messageList = gson.fromJson(messageListStr, typeList);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return messageList;
    }

    private void registerChatReceiver() {
        IntentFilter chatFilter = new IntentFilter("new Message");
        // 攔截chat廣播
        broadcastManager.registerReceiver(chatReceiver, chatFilter);
        // 如果有我們就來做CHATRECEIVER
    }

    private BroadcastReceiver chatReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            // 拿到完整的資料
            Message chatMessage = gson.fromJson(message, Message.class);
            messageList.add(chatMessage);
            adapter.setMessages(messageList);
            adapter.notifyDataSetChanged();
            rvList.smoothScrollToPosition(adapter.getItemCount());
            // 解析成需要顯示的字串
            Log.d(TAG, message);
        }
    };

    private void pushToSocket(){

        String text = etMessage.getText().toString();
        Message message = new Message(user_no, 1, text, 1);
        ServiceCommon.chatWebSocketClient.send(gson.toJson(message));
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        // Fragment頁面切換時解除註冊，但不需要關閉WebSocket，
        // 否則回到前頁好友列表，會因為斷線而無法顯示好友
        broadcastManager.unregisterReceiver(chatReceiver);
    }
}
