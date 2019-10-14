package com.g2.runningFront.RunActivity.Group;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;// 取得偏好設定
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.g2.runningFront.R;
import com.g2.runningFront.Common.Common;
import com.g2.runningFront.Common.CommonTask;
/* 屬於 Group Package 底下的 ImageTask */
import com.g2.runningFront.RunActivity.Group.Common.ImageTask;

/* 有關使用 Gson 解析資料 */
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;

import java.util.Date;
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
    private CommonTask GetFollowsTask;
    private ImageTask FollowImageTask;

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
        List<Follow> follows = getFollows();
        showFollowList(follows);

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
        // 如果followAdapter不存在就建立新的，否則續用舊有的
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
        private final int IMAGE_SIZE = 80;

        FollowAdapter(Context context, List<Follow> follows) {
            layoutInflater = LayoutInflater.from(context);
            this.follows = follows;

            /* 螢幕寬度除以4當作將圖的尺寸 */
            //imageSize = getResources().getDisplayMetrics().widthPixels / 4;

        }

        void setFollows(List<Follow> follows) {
            this.follows = follows;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView gp_ivFriend, gp_ivHeart;
            TextView gp_tvRank ,gp_tvFriend, gp_tvKm;

            MyViewHolder(View itemView) {
                super(itemView);
                gp_ivFriend = itemView.findViewById(R.id.gp_ivFriend);
                gp_tvRank = itemView.findViewById(R.id.gp_tvRank);
                gp_tvFriend = itemView.findViewById(R.id.gp_tvFriend);
                gp_tvKm = itemView.findViewById(R.id.gp_tvKm);
                gp_ivHeart = itemView.findViewById(R.id.gp_ivHeart);
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

            /* ==================== ⬇️正在施工區域⬇️ ==================== */
            final Follow follow = follows.get(position);
            String url = Common.URL_SERVER + "GroupServlet";

            /* 索取追蹤會員大頭貼 */
            int no = follow.getNo();
            FollowImageTask = new ImageTask(url, no, IMAGE_SIZE, myViewHolder.gp_ivFriend);
            FollowImageTask.execute();

            myViewHolder.gp_tvRank.setText(String.valueOf(position));
            myViewHolder.gp_tvFriend.setText(follow.getName());
            myViewHolder.gp_tvKm.setText((follow.getDistance()) + " 公里");

            /* 根據 follow 裡的 isLove(布林值)來改變愛心圖示 */
            boolean isLove = follow.getIsLove();
            if (isLove){
                myViewHolder.gp_ivHeart.setImageResource(R.drawable.ic_lovered);
            } else {
                myViewHolder.gp_ivHeart.setImageResource(R.drawable.ic_loveblack);
            }

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
    private List<Follow> getFollows() {
        List<Follow> followList = null;
        if (Common.networkConnected(activity)) {
            String url = Common.URL_SERVER + "GroupServlet";
            JsonObject jo = new JsonObject();
            jo.addProperty("action", "getAll");

            jo.addProperty("user_no", no);

            /* 查詢當前月份，設為跑步期間的條件 */
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyMM")// 1910 即：年年月月
                    .create();
            /* 將 new Date() 轉為 Json，並且要符合以上的日期表示法
             * 但是在 Servlet 端會變成字串型態 "1910"，需要再切開字串 */
            String date = gson.toJson(new Date());
            jo.addProperty("month", date);

            String jsonOut = jo.toString();
            GetFollowsTask = new CommonTask(url, jsonOut);

            try {
                String jsonIn = GetFollowsTask.execute().get();
                
                Log.d(TAG, "傳回的 List<Follow> = \n" + jsonIn);
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

    @Override
    public void onStop() {
        super.onStop();
        if (GetFollowsTask != null) {
            GetFollowsTask.cancel(true);
            GetFollowsTask = null;
        }
        if(FollowImageTask != null){
            FollowImageTask.cancel(true);
            FollowImageTask = null;
        }
    }

}







