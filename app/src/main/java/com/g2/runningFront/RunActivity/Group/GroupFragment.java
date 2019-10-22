package com.g2.runningFront.RunActivity.Group;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;// 取得偏好設定
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SearchView;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
/* 設定 View 的可見狀態 */
import static android.view.View.GONE;
import static android.view.View.VISIBLE;


public class GroupFragment extends Fragment {
    private static String TAG = "TAG_GroupFragment";
    private Activity activity;
    private Button gp_btFriend, gp_btAll, bt_addFollow;
    private SearchView gp_sv;

    /* 用於 RecyclerView 查詢、承接資料 */
    private int no;
    private RecyclerView gp_rv;
    private List<Follow> follows;
    private CommonTask GetFollowsTask;
    private ImageTask FollowImageTask;

    /* 用於表現會員愛心狀態 */
    private final int LOVE = 0;
    private final int NOLOVE = 1;

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

        /* 使用 SearchView */
        gp_sv = view.findViewById(R.id.searchView);
        gp_sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {

                // 如果搜尋條件為空字串，就顯示原始資料；否則就顯示搜尋後結果
                if (newText.isEmpty()) {
                    showFollowList(follows);
                } else {
                    List<Follow> searchFollows = new ArrayList<>();
                    // 搜尋原始資料內有無包含關鍵字(不區別大小寫)
                    for (Follow follow : follows) {
                        if (follow.getName().toUpperCase().contains(newText.toUpperCase())) {
                            searchFollows.add(follow);
                        }
                    }
                    showFollowList(searchFollows);
                }
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
        });

        /* 使用 RecyclerView */
        gp_rv = view.findViewById(R.id.recyclerView);
        gp_rv.setLayoutManager(new LinearLayoutManager(activity));
        follows = getFollows();
        showFollowList(follows);

        gp_btFriend = view.findViewById(R.id.gp_btFriend);
        gp_btAll = view.findViewById(R.id.gp_btAll);

        gp_btFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gp_btFriend.setTextColor(Color.parseColor("#36C2CF"));
                gp_btAll.setTextColor(Color.parseColor("#000000"));
                gp_rv.setVisibility(VISIBLE);

                showFollowList(follows);
            }
        });

        gp_btAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gp_btAll.setTextColor(Color.parseColor("#36C2CF"));
                gp_btFriend.setTextColor(Color.parseColor("#000000"));

                gp_rv.setVisibility(GONE);

                // 顯示 App 全體排行榜
            }
        });

        /* 新增追蹤會員按鈕 */
        bt_addFollow = view.findViewById(R.id.addFollow);
        bt_addFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view)
                        .navigate(R.id.action_runGroupFragment_to_groupSearchFragment);
            }
        });
    }

    private void showFollowList(List<Follow> follows) {
        if (follows == null || follows.isEmpty()){
            return;
        }

        FollowAdapter followAdapter = (FollowAdapter) gp_rv.getAdapter();

        // 如果 followAdapter 不存在就建立新的，否則續用舊有的
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
        private final int IMAGE_SIZE = 90;

        FollowAdapter(Context context, List<Follow> follows) {
            layoutInflater = LayoutInflater.from(context);
            this.follows = follows;
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
        public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int position) {

            final Follow follow = follows.get(position);
            String url = Common.URL_SERVER + "GroupServlet";

            /* 索取追蹤會員大頭貼 */
            int no = follow.getNo();
            FollowImageTask = new ImageTask(url, no, IMAGE_SIZE, myViewHolder.gp_ivFriend);
            FollowImageTask.execute();

            /* 名次用 position+1 來顯示 */
            String rank = (position < 10)? "0"+(position+1) : String.valueOf(position+1);
            myViewHolder.gp_tvRank.setText(rank);

            myViewHolder.gp_tvFriend.setText(follow.getName());
            String length = follow.getDistance() + " 公里";
            myViewHolder.gp_tvKm.setText(length);

            /* 根據 follow 裡的 isLove(布林值)
             * 轉換成常數
             * 用來決定愛心圖示的顏色 */
            boolean isLove = follow.getIsLove();
            int loveInt = (isLove)? LOVE : NOLOVE;

            switch (loveInt){
                case LOVE:
                    myViewHolder.gp_ivHeart.setImageResource(R.drawable.ic_lovered);
                    break;
                case NOLOVE:
                    myViewHolder.gp_ivHeart.setImageResource(R.drawable.ic_loveblack);
                    break;
            }

            /* 切換愛心狀態，也更改資料庫中會員在愛心列表的資料 */
            myViewHolder.gp_ivHeart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    /* 改變愛心圖示，也改變 follow 的 isLove 布林值 */
                    boolean isLove_new = changeHeart(follow, myViewHolder.gp_ivHeart);

                    /* 連線 Servlet 修改愛心列表 */
                    changeLove(follow.getNo(), isLove_new);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            /* 重設適配器內指定的 follow 物件
                             * 並提醒適配器即時更新
                             * 最後更新此頁面屬性之一的 follows 物件集合 */
                            follows.set(position, follow);
                            FollowAdapter.this.notifyDataSetChanged();
                            GroupFragment.this.follows.set(position, follow);
                        }
                    },3 * 1000);// 延後3秒

                }
            });

            myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Bundle bundle = new Bundle();
                    bundle.putInt("user_no", follow.getNo());
                    Navigation.findNavController(view)
                            .navigate(R.id.action_runGroupFragment_to_FriendFragment, bundle);
                }
            });

            /* 長按追蹤卡片，可以取消追蹤（刪除該筆追蹤資料） */
            myViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    PopupMenu popup = new PopupMenu(activity, view, Gravity.CENTER);
                    popup.inflate(R.menu.group_follow_menu);
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()){

                                /* 刪除追蹤會員 */
                                case R.id.deleteFollow:
                                    if (Common.networkConnected(activity)) {
                                        String url = Common.URL_SERVER + "GroupServlet";
                                        JsonObject jo = new JsonObject();
                                        jo.addProperty("action", "unfollow");
                                        jo.addProperty("user_no",
                                                activity.getSharedPreferences(Common.PREF, MODE_PRIVATE)
                                                        .getInt("user_no",0));
                                        jo.addProperty("follow_no", follow.getNo());

                                        int count = 0;

                                        try {
                                            CommonTask deleteTask = new CommonTask(url, jo.toString());
                                            String result = deleteTask.execute().get();
                                            count = Integer.valueOf(result);
                                        } catch (Exception e) {
                                            Log.e(TAG, e.toString());
                                        }
                                        if (count == 0) {
                                            Common.toastShow(activity,"取消追蹤失敗");
                                        } else {

                                            /* 移除適配器內指定的 follow 物件
                                             * 並提醒適配器即時更新
                                             * 最後更新此頁面屬性之一的 follows 物件集合 */
                                            follows.remove(follow);
                                            FollowAdapter.this.notifyDataSetChanged();
                                            GroupFragment.this.follows.remove(follow);
                                            Common.toastShow(activity,"已取消追蹤");

                                        }
                                    } else {
                                        Common.toastShow(activity,"取消追蹤失敗");
                                    }
                            }
                            return true;
                        }

                    });
                    popup.show();
                    return true;
                }
            });
        }

    }

    /* 取得追蹤會員資料集合 */
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

    /* 改變愛心圖示，也改變 follow 的 isLove 布林值 */
    private boolean changeHeart(Follow follow, ImageView imageView) {

        follow.setIsLove(!follow.getIsLove());

        int loveInt = (follow.getIsLove())? LOVE : NOLOVE;
        switch (loveInt){
            case LOVE:
                imageView.setImageResource(R.drawable.ic_lovered);
                break;
            case NOLOVE:
                imageView.setImageResource(R.drawable.ic_loveblack);
                break;
        }
        return follow.getIsLove();
    }

    /* 連線資料庫更改使用者的愛心列表 */
    private void changeLove(int follow_no ,boolean isLove){
        if (Common.networkConnected(activity)) {

            String url = Common.URL_SERVER + "GroupServlet";

            JsonObject jo = new JsonObject();
            jo.addProperty("action", "changeLove");
            jo.addProperty("user_no", no);
            jo.addProperty("follow_no", follow_no);
            jo.addProperty("isLove", isLove);

            String outStr = jo.toString();
            CommonTask updateTask = new CommonTask(url, outStr);

            try {
                /* 執行就好，不計結果 */
                updateTask.execute();
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }

        }else {
            Common.toastShow(activity, "暫時無法更改狀態");
        }

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