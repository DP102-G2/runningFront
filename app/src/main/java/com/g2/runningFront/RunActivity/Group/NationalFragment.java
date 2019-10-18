//package com.g2.runningFront.RunActivity.Group;
//
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.navigation.Navigation;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.PopupMenu;
//import android.widget.TextView;
//
//import com.g2.runningFront.Common.Common;
//import com.g2.runningFront.Common.CommonTask;
//import com.g2.runningFront.R;
//import com.g2.runningFront.RunActivity.Group.Common.ImageTask;
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import com.google.gson.JsonObject;
//import com.google.gson.reflect.TypeToken;
//
//import java.lang.reflect.Type;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//import static android.content.Context.MODE_PRIVATE;
//
//
//public class NationalFragment extends Fragment {
//
//
//    private Activity activity;
//    private RecyclerView recyclerView;
//    private static String TAG = "TAG_GroupFragment";
//    private Button gp_btFriend, gp_btAll;
//
//    /* 用於 RecyclerView 查詢、承接資料 */
//    private int no;
//    private RecyclerView gp_rv;
//    private CommonTask GetFollowsTask;
//    private ImageTask FollowImageTask;
//
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        activity = getActivity();
//
//        /* 從偏好設定讀取登入狀態與否，並得到會員編號（用來查詢該會員的追蹤名單） */
//        SharedPreferences pref = activity.getSharedPreferences(Common.PREF, MODE_PRIVATE);
//
//        boolean isSignIn = pref.getBoolean("isSignIn", false);
//        if (isSignIn) {
//            /* 顯示使用者追蹤名單 */
//            no = pref.getInt("user_no",0);
//        } else {
//            Log.d(TAG,"檢查未登入，不顯示追蹤名單。");
//        }
//
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        activity.setTitle("社群排行榜");
//        return inflater.inflate(R.layout.fragment_run_group, container, false);
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        /* 使用 RecyclerView */
//        gp_rv = view.findViewById(R.id.gp_rv);
//        gp_rv.setLayoutManager(new LinearLayoutManager(activity));
//        List<Follow> follows = getFollows();
//        showFollowList(follows);
//
//        gp_btFriend = view.findViewById(R.id.gp_btFriend);
//        gp_btAll = view.findViewById(R.id.gp_btAll);
//
//        gp_btFriend.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                /* 列出好友排行 */
//            }
//        });
//
//    }
//
//    private void showFollowList(List<Follow> follows) {
//        if (follows == null || follows.isEmpty()){
//            return;
//        }
//        GroupFragment.FollowAdapter followAdapter = (GroupFragment.FollowAdapter) gp_rv.getAdapter();
//        // 如果followAdapter不存在就建立新的，否則續用舊有的
//        if (followAdapter == null) {
//            gp_rv.setAdapter(new GroupFragment.FollowAdapter(activity, follows));
//        } else {
//            followAdapter.setFollows(follows);
//            followAdapter.notifyDataSetChanged();
//        }
//    }
//
//    private class FollowAdapter extends RecyclerView.Adapter<GroupFragment.FollowAdapter.MyViewHolder> {
//        private LayoutInflater layoutInflater;
//        private List<Follow> follows;
//        private final int IMAGE_SIZE = 80;
//
//        FollowAdapter(Context context, List<Follow> follows) {
//            layoutInflater = LayoutInflater.from(context);
//            this.follows = follows;
//
//            /* 螢幕寬度除以4當作將圖的尺寸 */
//            //imageSize = getResources().getDisplayMetrics().widthPixels / 4;
//
//        }
//
//        void setFollows(List<Follow> follows) {
//            this.follows = follows;
//        }
//
//        class MyViewHolder extends RecyclerView.ViewHolder {
//            ImageView gp_ivFriend, gp_ivHeart;
//            TextView gp_tvRank ,gp_tvFriend, gp_tvKm;
//
//            MyViewHolder(View itemView) {
//                super(itemView);
//                gp_ivFriend = itemView.findViewById(R.id.gp_ivFriend);
//                gp_tvRank = itemView.findViewById(R.id.gp_tvRank);
//                gp_tvFriend = itemView.findViewById(R.id.gp_tvFriend);
//                gp_tvKm = itemView.findViewById(R.id.gp_tvKm);
//                gp_ivHeart = itemView.findViewById(R.id.gp_ivHeart);
//            }
//        }
//
//        @Override
//        public int getItemCount() {
//            return follows.size();
//        }
//
//        @NonNull
//        @Override
//        public GroupFragment.FollowAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//
//            View itemView = layoutInflater.inflate(R.layout.itern_view_group2, parent, false);
//
//            return new GroupFragment.FollowAdapter.MyViewHolder(itemView);
//        }
//
//        @Override
//        public void onBindViewHolder(@NonNull final GroupFragment.FollowAdapter.MyViewHolder myViewHolder, int position) {
//
//            final Follow follow = follows.get(position);
//            String url = Common.URL_SERVER + "GroupServlet";
//
//            /* 索取追蹤會員大頭貼 */
//            int no = follow.getNo();
//            FollowImageTask = new ImageTask(url, no, IMAGE_SIZE, myViewHolder.gp_ivFriend);
//            FollowImageTask.execute();
//
//            myViewHolder.gp_tvRank.setText(String.valueOf(position));
//            myViewHolder.gp_tvFriend.setText(follow.getName());
//            myViewHolder.gp_tvKm.setText((follow.getDistance()) + " 公里");
//
//            /* 根據 follow 裡的 isLove(布林值)
//             * 轉換成常數
//             * 用來決定愛心圖示的顏色 */
//            boolean isLove = follow.getIsLove();
//            int loveInt = (isLove)? LOVE : NOLOVE;
//
//            switch (loveInt){
//                case LOVE:
//                    myViewHolder.gp_ivHeart.setImageResource(R.drawable.ic_lovered);
//                    break;
//                case NOLOVE:
//                    myViewHolder.gp_ivHeart.setImageResource(R.drawable.ic_loveblack);
//                    break;
//            }
//
//            /* 切換愛心狀態，也更改資料庫中會員在愛心列表的資料 */
//            myViewHolder.gp_ivHeart.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                    /* ==================== ⬇️正在施工區域⬇️ ==================== */
//                    //follow.setIsLove(!follow.getIsLove());
//
//                    boolean isLove_onClick = follow.getIsLove();
//                    int loveInt_onClick = (isLove_onClick)? LOVE : NOLOVE;
//
//                    switch (loveInt_onClick){
//                        case LOVE:
//                            myViewHolder.gp_ivHeart.setImageResource(R.drawable.ic_loveblack);
//                            /* 點按愛心後卡片狀態改變，更新適配器 */
//                            GroupFragment.FollowAdapter.this.notifyDataSetChanged();
//                            isLove_onClick = false;
//
//                            break;
//                        case NOLOVE:
//                            myViewHolder.gp_ivHeart.setImageResource(R.drawable.ic_lovered);
//                            /* 點按愛心後卡片狀態改變，更新適配器 */
//                            GroupFragment.FollowAdapter.this.notifyDataSetChanged();
//                            isLove_onClick = true;
//
//                            break;
//                    }
//
//                }
//            });
//
//            myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                    /* 從偏好設定存取使用者編號，再放入 bundle 中
//                     * 預設值為整數 0 */
//                    Bundle bundle = new Bundle();
////                    bundle.putSerializable("user_no", activity.getSharedPreferences(Common.PREF, MODE_PRIVATE).getInt("user_no",0));
//                    bundle.putInt("user_no",follow.getNo());
//                    Navigation.findNavController(view)
//                            .navigate(R.id.action_runGroupFragment_to_FriendFragment2, bundle);
//                }
//            });
//
//            /* 長按追蹤卡片，可以停止追蹤（刪除該筆追蹤資料） */
//            myViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View view) {
//
//                    PopupMenu popup = new PopupMenu(activity, view);
//                    popup.inflate(R.menu.group_follow_menu);
//                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                        @Override
//                        public boolean onMenuItemClick(MenuItem item) {
//                            switch (item.getItemId()){
//
//                                /* ==================== ⬇️正在施工區域⬇️ ==================== */
//
//                                case R.id.deleteFollow:
//                                    return true;
//                                default:
//                                    return false;
//                            }
//                        }
//                    });
//                    popup.show();
//                    return false;
//                }
//            });
//        }
//
//    }
//
//    /* 取得追蹤資料集合 */
//    private List<Follow> getFollows() {
//        List<Follow> followList = null;
//        if (Common.networkConnected(activity)) {
//            String url = Common.URL_SERVER + "GroupServlet";
//            JsonObject jo = new JsonObject();
//            jo.addProperty("action", "getAll");
//
//            jo.addProperty("user_no", no);
//
//            /* 查詢當前月份，設為跑步期間的條件 */
//            Gson gson = new GsonBuilder()
//                    .setDateFormat("yyMM")// 1910 即：年年月月
//                    .create();
//            /* 將 new Date() 轉為 Json，並且要符合以上的日期表示法
//             * 但是在 Servlet 端會變成字串型態 "1910"，需要再切開字串 */
//            String date = gson.toJson(new Date());
//            jo.addProperty("month", date);
//
//            String jsonOut = jo.toString();
//            GetFollowsTask = new CommonTask(url, jsonOut);
//
//            try {
//                String jsonIn = GetFollowsTask.execute().get();
//
//                Log.d(TAG, "傳回的 List<Follow> = \n" + jsonIn);
//                Type listType = new TypeToken<List<Follow>>() {
//                }.getType();
//                followList = new Gson().fromJson(jsonIn, listType);
//            } catch (Exception e) {
//                Log.e(TAG, e.toString());
//            }
//        } else {
//            Common.toastShow(activity, R.string.textNoNetwork);
//        }
//        return followList;
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        if (GetFollowsTask != null) {
//            GetFollowsTask.cancel(true);
//            GetFollowsTask = null;
//        }
//        if(FollowImageTask != null){
//            FollowImageTask.cancel(true);
//            FollowImageTask = null;
//        }
//    }
//
//}
//
//
//
//
//
