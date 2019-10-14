package com.g2.runningFront.RunActivity.Group;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;// 取得偏好設定
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.g2.runningFront.Common.*;
import com.g2.runningFront.R;

/* 有關使用 Gson 解析資料 */
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class GroupFragment extends Fragment {
    private static String TAG = "TAG_GroupFragment";
    private Activity activity;
    private Button gp_btFriend, gp_btAll;

    /* 用於 RecyclerView 查詢、承接資料 */
    private int no;
    private RecyclerView gp_rv;
    private Gson gson;
    private CommonTask followListGetAllTask;
    private ImageTask FriendImageTask;

    int flag = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();

        /* 從偏好設定讀取登入狀態與否，並得到會員編號（用來查詢該會員的追蹤名單） */
        SharedPreferences pref = activity.getSharedPreferences(Common.PREF, MODE_PRIVATE);

        boolean isSignIn = pref.getBoolean("isSignIn", false);
        if (isSignIn) {
            /* 顯示使用者追蹤名單 */
            no = pref.getInt("user_no",0);
        } else {
            Log.d(TAG,"檢查未登入，不顯示追蹤名單。");
        }

        /* 使用 Gson 的前置宣告 */
        gson = new Gson();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle("社群排行榜");
        return inflater.inflate(R.layout.fragment_run_group, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /* 使用 RecyclerView */
        gp_rv = view.findViewById(R.id.gp_rv);
        gp_rv.setLayoutManager(new LinearLayoutManager(activity));
        List<Follow> followList = getfollowList();
        showFollowList(followList);

        gp_btFriend = view.findViewById(R.id.gp_btFriend);
        gp_btAll = view.findViewById(R.id.gp_btAll);

        gp_btFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* 列出好友排行 */
            }
        });
    }

    private void showFollowList(List<Follow> follows) {
        if (follows == null || follows.isEmpty()){
            return;
        }
        FollowAdapter followAdapter = (FollowAdapter) gp_rv.getAdapter();
        // 如果spotAdapter不存在就建立新的，否則續用舊有的
        if (followAdapter == null) {
            gp_rv.setAdapter(new FollowAdapter(activity, follows));
        } else {
            followAdapter.setFollows(follows);
            followAdapter.notifyDataSetChanged();
        }
    }

    private class FollowAdapter extends RecyclerView.Adapter<FollowAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater;
        private List<Follow> follows;
        private int imageSize;

        FollowAdapter(Context context, List<Follow> follows) {
            layoutInflater = LayoutInflater.from(context);
            this.follows = follows;

            /* 螢幕寬度除以4當作將圖的尺寸 */
            imageSize = getResources().getDisplayMetrics().widthPixels / 4;

        }

        void setFollows(List<Follow> follows) {
            this.follows = follows;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            TextView gp_tvFriend, gp_tvKm;

            MyViewHolder(View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.gp_ivFriend);
                gp_tvFriend = itemView.findViewById(R.id.gp_tvFriend);
                gp_tvKm = itemView.findViewById(R.id.gp_tvKm);
            }
        }

        @Override
        public int getItemCount() {
            return follows.size();
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View itemView = layoutInflater.inflate(R.layout.itern_view_group2, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
            final Follow follow = follows.get(position);
            String url = Common.URL_SERVER + "GroupServlet";

            /* 索取追蹤會員大頭貼 */
            int no = follow.getNo();
            FriendImageTask = new ImageTask(url, no, imageSize, myViewHolder.imageView);
            FriendImageTask.execute();

            myViewHolder.gp_tvFriend.setText(follow.getName());
            myViewHolder.gp_tvKm.setText(String.valueOf(follow.getDistance()) + " 公里");

            myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("follow", follow);
                    //Navigation.findNavController(view)
                            //.navigate(R.id.action_GroupFragment_to_FriendFragment, bundle);
                }
            });
        }

    }

    /* 取得追蹤資料集合 */
    private List<Follow> getfollowList() {
        List<Follow> followList = null;
        if (Common.networkConnected(activity)) {
            String url = Common.URL_SERVER + "GroupServlet";
            JsonObject jo = new JsonObject();
            jo.addProperty("action", "getAll");


            jo.addProperty("user_no", 1);


            String jsonOut = jo.toString();
            followListGetAllTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = followListGetAllTask.execute().get();
                
                Log.e(TAG, "傳回的 List<Follow> = \n" + jsonIn);
                
                Type listType = new TypeToken<List<Follow>>() {
                }.getType();
                followList = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.toastShow(activity, R.string.textNoNetwork);
        }
        return followList;
    }

    /* 一些假資料 */ /*
    private List<Follow> getfollowList_() {
        List<Follow> followList = new ArrayList<>();
        followList.add(new Follow(1,"追蹤A",10));
        followList.add(new Follow(2,"追蹤B",20));
        followList.add(new Follow(3,"追蹤C",30));
        return followList;
    }*/

    @Override
    public void onStop() {
        super.onStop();
        if (followListGetAllTask != null) {
            followListGetAllTask.cancel(true);
            followListGetAllTask = null;
        }
    }

}







