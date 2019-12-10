package com.g2.runningFront.RunActivity.Group;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
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

import com.g2.runningFront.Common.Common;
import com.g2.runningFront.Common.CommonTask;
import com.g2.runningFront.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class NationalFragment extends Fragment {


    private Activity activity;
    private static String TAG = "TAG_National";
    private Button na_btFriend, na_btAll,btyes;

    /* 用於 RecyclerView 查詢、承接資料 */
    private int no;
    private RecyclerView na_rv;
    private CommonTask GetFollowsTask;
    private ImageTask_Rk NationalImageTask;
    private int j;
    private  List<National> nationals ;


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
        return inflater.inflate(R.layout.fragment_national, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /* 使用 RecyclerView */
        na_rv = view.findViewById(R.id.na_rv);
        na_rv.setLayoutManager(new LinearLayoutManager(activity));
//        List<National> nationals = getNational();
        nationals = getNational();
        showNationalList(nationals);

        na_btFriend = view.findViewById(R.id.na_btFriend);
        na_btAll = view.findViewById(R.id.na_btAll);


        na_btFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* 列出好友排行 */

                Navigation.findNavController(view)
                        .navigate(R.id.action_NationalFragment_to_runGroupFragment);
            }
        });

    }

    private void showNationalList(List<National> nationals) {
        if (nationals == null || nationals.isEmpty()){
            return;
        }
        NationalAdapter nationalAdapter = (NationalAdapter) na_rv.getAdapter();
        // 如果followAdapter不存在就建立新的，否則續用舊有的
        if (nationalAdapter == null) {
            na_rv.setAdapter(new NationalAdapter(activity, nationals));
        } else {
            nationalAdapter.setNational(nationals);

        }
    }

    private class NationalAdapter extends RecyclerView.Adapter<NationalAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater;
        private List<National> nationals;
        private final int IMAGE_SIZE = 80;

        NationalAdapter(Context context, List<National> nationals) {
            layoutInflater = LayoutInflater.from(context);
            this.nationals=nationals;

            /* 螢幕寬度除以4當作將圖的尺寸 */
            //imageSize = getResources().getDisplayMetrics().widthPixels / 4;

        }

        void setNational(List<National> nationals) {
            this.nationals = nationals;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView gp_ivFriend;
            TextView gp_tvRank ,gp_tvFriend, gp_tvKm;
            Button btyes;

            MyViewHolder(View itemView) {
                super(itemView);
                gp_ivFriend = itemView.findViewById(R.id.gp_ivFriend);
                gp_tvRank = itemView.findViewById(R.id.gp_tvRank);
                gp_tvFriend = itemView.findViewById(R.id.gp_tvFriend);
                gp_tvKm = itemView.findViewById(R.id.gp_tvKm);
                btyes=itemView.findViewById(R.id.btyes);




//                btyes.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        j++;
//                        if (j % 2 > 0) {
//
//                            btyes.setBackgroundDrawable(getResources().getDrawable(R.drawable.nobt_style));
//                            btyes.setText("已追蹤");
//
//                        } else {
//                            btyes.setBackgroundDrawable(getResources().getDrawable(R.drawable.bt_style));
//                            btyes.setText("追蹤");
//                        }
//                    }
//
//                });


            }
        }

        @Override
        public int getItemCount() {
            return nationals.size();
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View itemView = layoutInflater.inflate(R.layout.itern_view_group, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, int position) {
            final int index = position;
            final National national = nationals.get(position);
            String url = Common.URL_SERVER + "NationalServlet";

            /* 索取追蹤會員大頭貼 */
            int rkuser_no = national.getRkuser_no();
            Log.e(TAG, "rkuser_no = " + rkuser_no);
            NationalImageTask = new ImageTask_Rk(url, rkuser_no, IMAGE_SIZE, myViewHolder.gp_ivFriend);
            NationalImageTask.execute();

            myViewHolder.gp_tvRank.setText(String.valueOf(position+1));
            myViewHolder.gp_tvFriend.setText(national.getName());
            myViewHolder.gp_tvKm.setText((national.getDistance()) + " 公里");



//            boolean isFollow = national.isFollow();
            int follow = national.getFollow_no();
            if(follow > 0){
                myViewHolder.btyes.setBackgroundDrawable(getResources().getDrawable(R.drawable.nobt_style));
                myViewHolder.btyes.setText("已追蹤");
            } else if(follow == 0){
                myViewHolder.btyes.setBackgroundDrawable(getResources().getDrawable(R.drawable.bt_style));
                myViewHolder.btyes.setText("追蹤");
            }


            // 追蹤按鈕
            myViewHolder.btyes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int follow_inner = national.getFollow_no();
                    if(follow_inner > 0){

                        myViewHolder.btyes.setBackgroundDrawable(getResources().getDrawable(R.drawable.bt_style));
                        myViewHolder.btyes.setText("追蹤");

                        // 取消追蹤 Task
                        if (Common.networkConnected(activity)) {
                            String url = Common.URL_SERVER + "NationalServlet";
                            JsonObject jo = new JsonObject();
                            jo.addProperty("action", "unfollow");
                            jo.addProperty("user_no",
                                    activity.getSharedPreferences(Common.PREF, MODE_PRIVATE)
                                            .getInt("user_no",0));
                            jo.addProperty("follow_no", national.getFollow_no());

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
                                Common.toastShow(activity,"追蹤成功");
                                national.setfollow_no(0);

//                            if(follow > 0){
//                                myViewHolder.btyes.setBackgroundDrawable(getResources().getDrawable(R.drawable.nobt_style));
//                                myViewHolder.btyes.setText("已追蹤");
//                            } else if(follow == 0){
//                                myViewHolder.btyes.setBackgroundDrawable(getResources().getDrawable(R.drawable.bt_style));
//                                myViewHolder.btyes.setText("追蹤");
//                            }


//                            nationals.set(position, national);
//                            NationalFragment.this.nationals.set(position, national);

                                NationalAdapter.this.notifyDataSetChanged();
                                Common.toastShow(activity,"已取消追蹤");
                            }
                        } else {
                            Common.toastShow(activity,"取消追蹤失敗");
                        }

                    } else if(follow_inner == 0){
                        national.setfollow_no(national.getRkuser_no());

                        myViewHolder.btyes.setBackgroundDrawable(getResources().getDrawable(R.drawable.nobt_style));
                        myViewHolder.btyes.setText("已追蹤");

                        //  追蹤 Task
                        if (Common.networkConnected(activity)) {
                            String url = Common.URL_SERVER + "NationalServlet";
                            JsonObject jo = new JsonObject();
                            jo.addProperty("action", "follow");
                            jo.addProperty("user_no", Common.getUserNo(activity));
//                            jo.addProperty("follow_no", national.getNo());
                            jo.addProperty("follow_no", national.getRkuser_no());

                            int count = 0;

                            try {
                                CommonTask deleteTask = new CommonTask(url, jo.toString());
                                String result = deleteTask.execute().get();
                                count = Integer.valueOf(result);
                            } catch (Exception e) {
                                Log.e(TAG, e.toString());
                            }
                            if (count == 0) {
                                Common.toastShow(activity,"追蹤失敗");
                            } else {

                                /* 移除適配器內指定的 follow 物件
                                 * 並提醒適配器即時更新
                                 * 最後更新此頁面屬性之一的 follows 物件集合 */
//                                follows.remove(follow);
                                NationalFragment. NationalAdapter.this.notifyDataSetChanged();
//                                NationalFragment.this.nationals.remove(national);
                                Common.toastShow(activity,"已追蹤");

                            }
                        } else {
                            Common.toastShow(activity,"連不到");
                        }
                    }
                    notifyDataSetChanged();

                }
            });










            myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    /* 從偏好設定存取使用者編號，再放入 bundle 中
                     * 預設值為整數 0 */
                    Bundle bundle = new Bundle();
//                    bundle.putSerializable("user_no", activity.getSharedPreferences(Common.PREF, MODE_PRIVATE).getInt("user_no",0));
                    bundle.putInt("user_no",national.getRkuser_no());
                    Navigation.findNavController(v)
                            .navigate(R.id.action_nationalFragment_to_FriendFragment, bundle);
                }
            });



        }

    }

    /* 取得追蹤資料集合 */
    private List<National> getNational() {
        List<National> nationalList = null;
        if (Common.networkConnected(activity)) {
            String url = Common.URL_SERVER + "NationalServlet";
            JsonObject jo = new JsonObject();

            jo.addProperty("action", "getAll");
            jo.addProperty("user_no", no);

            /* 查詢當前月份，設為跑步期間的條件 */
            Gson gson = new GsonBuilder().create();;
//                    .setDateFormat("yyMM")// 1910 即：年年月月
//                    .create();
            /* 將 new Date() 轉為 Json，並且要符合以上的日期表示法
             * 但是在 Servlet 端會變成字串型態 "1910"，需要再切開字串 */
//            String date = gson.toJson(new Date());
//            jo.addProperty("month", date);

            String jsonOut = jo.toString();
            GetFollowsTask = new CommonTask(url, jsonOut);

            try {
                String jsonIn = GetFollowsTask.execute().get();

                Log.d(TAG, "傳回的 List<Follow> = \n" + jsonIn);
                Type listType = new TypeToken<List<National>>() {
                }.getType();
                nationalList = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.toastShow(activity, R.string.textNoNetwork);
        }
        return nationalList;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (GetFollowsTask != null) {
            GetFollowsTask.cancel(true);
            GetFollowsTask = null;
        }
        if(NationalImageTask != null){
            NationalImageTask.cancel(true);
            NationalImageTask = null;
        }
    }

}



