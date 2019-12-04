package com.g2.runningFront.ShopActivity.Service;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.g2.runningFront.Common.Common;
import com.g2.runningFront.Common.CommonTask;
import com.g2.runningFront.R;
import com.g2.runningFront.ShopActivity.Service.CommonService.CustomerService;
import com.g2.runningFront.ShopActivity.Service.CommonService.ServiceCommon;
import com.g2.runningFront.ShopActivity.ShopActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShopServiceFragment extends Fragment {

    private static final String TAG = "TAG_ChatFragment";
    private LocalBroadcastManager broadcastManager;
    private String socket_no ;

    ShopActivity shopActivity;
    Activity activity;

    List<Message> messageList;

    mesAdapter adapter;
    RecyclerView rvList;
    EditText etMessage;
    ImageView btSubmit;
    View view;

    int user_no;

    CommonTask commonTask;
    private static final String url = Common.URL_SERVER + "ServiceServlet";

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

        /* 解除舊版的廣播監聽 */
        broadcastManager.unregisterReceiver(CustomerService.chatReceiver);

        /* 加載現在版本的廣播監聽 */
        registerChatReceiver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shop_service, container, false);
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
            View itemView = layoutInflater.inflate(R.layout.item_view_service, parent, false);

            return new messageViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull mesAdapter.messageViewHolder holder, int position) {
            final Message message = messages.get(position);
            holder.tvText.setText(message.getMsg_text());
            if (message.getMsg_by() == 0) {
                holder.tvWho.setVisibility(View.VISIBLE);
                holder.tvWho.setText("客服人員");
                holder.cardView.setCardBackgroundColor(activity.getColor(R.color.colorBrown));
                holder.csLayout.setTranslationX(0);

            }else {
                holder.tvWho.setVisibility(View.GONE);
                holder.cardView.setCardBackgroundColor(activity.getColor(R.color.colorPrimary));
                holder.csLayout.setTranslationX(imageSize-800);
            }


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String formatTS = new SimpleDateFormat("訊息時間： \n yyyy年 MM月 dd日 , HH點 mm分 ss秒").format(message.getMsg_time());

                    Common.showToast(activity, formatTS);
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
            TextView tvWho;
            ConstraintLayout csLayout;

            public messageViewHolder(View itemView) {
                super(itemView);
                cardView = itemView.findViewById(R.id.server_item_cardView);
                tvText = itemView.findViewById(R.id.server_item_tvText);
                tvWho = itemView.findViewById(R.id.server_item_tvWho);
                csLayout = itemView.findViewById(R.id.server_item_layout);
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

                    Date date = new Date();
                    Timestamp msg_time = new Timestamp(date.getTime());

                    String text = etMessage.getText().toString();
                    Message message = new Message(user_no, 1, text, 1,msg_time);
                    insertMessage(message);
                    pushToSocket(message);
                }
            }
        });

    }

    /* 拿到完整的訊息資料 */
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

            String jsStr = intent.getStringExtra("message");

            JsonObject jsonObject = gson.fromJson(jsStr,JsonObject.class);

            String message = jsonObject.get("message").getAsString();
            Message chatMessage = gson.fromJson(message, Message.class);
            messageList.add(chatMessage);
            adapter.setMessages(messageList);
            adapter.notifyDataSetChanged();
            rvList.smoothScrollToPosition(adapter.getItemCount());
            // 解析成需要顯示的字串
            Log.d(TAG, message);
        }
    };


    private void insertMessage(Message message) {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "insertMessage");
        jsonObject.addProperty("message", gson.toJson(message));

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

    private void pushToSocket(Message message){


        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action","new Message");
        jsonObject.addProperty("message",gson.toJson(message));
        ServiceCommon.chatWebSocketClient.send(jsonObject.toString());
    }


    @Override
    public void onPause() {
        super.onPause();
        broadcastManager.unregisterReceiver(chatReceiver);
//        ServiceCommon.disconnectServer();
        // 恢復原本的廣播監聽
        IntentFilter chatFilter = new IntentFilter("new Message");
        // 攔截chat廣播
        broadcastManager.registerReceiver(CustomerService.chatReceiver, chatFilter);
        // 如果有我們就來做CHATRECEIVER
    }


}
