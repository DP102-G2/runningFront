package com.g2.runningFront.RunActivity;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.g2.runningFront.Common.Common;
import com.g2.runningFront.Common.CommonTask;
import com.g2.runningFront.Common.ImageTask;
import com.g2.runningFront.R;
import com.g2.runningFront.RunActivity.Run.Achieve;
import com.g2.runningFront.RunActivity.Run.Achieve2;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;


public class RunAchieveFragment extends Fragment {

    private static final String TAG = "TAG_AchieveListFragment";
    private RecyclerView rvAchieve,rvAchieve2;
    private Activity activity;
    private CommonTask achieveGetAllTask,achieve2GetAllTask;
    private ImageTask achieveImageTask,achieve2ImageTask;
    private List<Achieve> achieves;
    private List<Achieve2> achieve2s;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }
                                                                                                    //初始化Fragment

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle(R.string.achieve);
        return inflater.inflate(R.layout.fragment_run_achieve, container, false);

    }
                                                                                                    //提供Fragment Ui
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //1-3
        rvAchieve = view.findViewById(R.id.reachieve);
        rvAchieve.setLayoutManager(
                new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL));
        achieves = getAchieves();
        showAchieves(achieves);
        rvAchieve.setAdapter(new AchieveAdapter(getActivity(), achieves));


        //4-9
        rvAchieve2 = view.findViewById(R.id.reachieve2);
        rvAchieve2.setLayoutManager(
                new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL));
        achieve2s = getAchieve2s();
        showAchieve2s(achieve2s);
        rvAchieve2.setAdapter(new Achieve2Adapter(getActivity(), achieve2s));


    }

    private class AchieveAdapter extends
            RecyclerView.Adapter<AchieveAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater;
        private List<Achieve> achieves;
        private int imageSize;

        AchieveAdapter(Context context, List<Achieve> achieves) {
            this.achieves = achieves;
            layoutInflater = LayoutInflater.from(context);
            imageSize = getResources().getDisplayMetrics().widthPixels;

        }
        void setAchieves(List<Achieve> achieves) {
            this.achieves = achieves;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView ivachieve;
            TextView tvachieve_no, tvachieve_value,tvunit;

            MyViewHolder(View itemView) {
                super(itemView);
                ivachieve = itemView.findViewById(R.id.ivachieve);
                tvachieve_no = itemView.findViewById(R.id.tvachieve_no);
                tvachieve_value = itemView.findViewById(R.id.tvachieve_value);
                tvunit = itemView.findViewById(R.id.tvunit);
            }
        }

        @Override
        public int getItemCount() {
            return achieves.size();
        }


        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            /* 載入item_view_friend.xml其實是提供顯示一筆好友資料所需的View元件 */
            View itemView = layoutInflater.inflate(R.layout.item_view_achieve, viewGroup, false);
            /* 透過呼叫MyViewHolder建構式將itemView傳給MyViewHolder */
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder viewHolder, int position) {
            /* 依照position所代表的資料索引取得對應Friend物件 */
            final Achieve achieve = achieves.get(position);
            String url = Common.URL_SERVER + "AchieveServlet";
            int achieve_no = achieve.getAchieve_no();
            achieveImageTask = new ImageTask(url, achieve_no, imageSize, viewHolder.ivachieve);
            achieveImageTask.execute();
            viewHolder.tvachieve_no.setText(String.valueOf(achieve.getAchieve_name()));
            viewHolder.tvachieve_value.setText(String.valueOf(achieve.getAchieve_value()));
            viewHolder.tvunit.setText(String.valueOf(achieve.getUnit()));

        }
    }
    // 建立測試用資料
    private List<Achieve> getAchieves() {
        List<Achieve> achieves = null;
        if (Common.networkConnected(activity)) {
            String url = Common.URL_SERVER + "/AchieveServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAll");
            String jsonOut = jsonObject.toString();
            achieveGetAllTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = achieveGetAllTask.execute().get();
                Log.e(TAG ,jsonIn);
                // {"achieve_name":"6","achieve_value":"一公里最快時間","unit":"10:12:12"}
                // (int user_no, String tvachieve_no, String tvachieve_value, int tvachieve_count, int ivachieve, String tvunit, int achieve_no)
                Type listType = new TypeToken<List<Achieve>>() {
                }.getType();
                achieves = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.toastShow(activity, R.string.textNoNetwork);
        }
        return achieves;
    }

    private void showAchieves(List<Achieve> achieves) {
        if (achieves == null || achieves.isEmpty()) {
            Common.toastShow(activity, R.string.textNoAchievesFound);
            return;
        }
        AchieveAdapter achieveAdapter = (AchieveAdapter) rvAchieve.getAdapter();
        // 如果achieveAdapter不存在就建立新的，否則續用舊有的
        if (achieveAdapter == null) {
            rvAchieve.setAdapter(new AchieveAdapter(activity, achieves));
        } else {
            achieveAdapter.setAchieves(achieves);
            achieveAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        if (achieveGetAllTask != null) {
            achieveGetAllTask.cancel(true);
            achieveGetAllTask = null;
        }

        if (achieveImageTask != null) {
            achieveImageTask.cancel(true);
            achieveImageTask = null;
        }

        if (achieve2GetAllTask != null) {
            achieve2GetAllTask.cancel(true);
            achieve2GetAllTask = null;
        }

        if (achieve2ImageTask != null) {
            achieve2ImageTask.cancel(true);
            achieve2ImageTask = null;
        }
    }







//////////////////////////////////////////////////////////
//4-9
    private class Achieve2Adapter extends
            RecyclerView.Adapter<Achieve2Adapter.MyViewHolder> {
        private LayoutInflater layoutInflater;
        private List<Achieve2> achieve2s;
        private int imageSize;

        Achieve2Adapter(Context context, List<Achieve2> achieve2s) {
            this.achieve2s = achieve2s;
            layoutInflater = LayoutInflater.from(context);
            imageSize = getResources().getDisplayMetrics().widthPixels;

        }
        void setAchieve2s(List<Achieve2> achieve2s) {
            this.achieve2s = achieve2s;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView ivachieve;
            TextView tvachieve_no, tvachieve_value,tvunit;

            MyViewHolder(View itemView) {
                super(itemView);
                ivachieve = itemView.findViewById(R.id.ivachieve);
                tvachieve_no = itemView.findViewById(R.id.tvachieve_no);
                tvachieve_value = itemView.findViewById(R.id.tvachieve_value);
                tvunit = itemView.findViewById(R.id.tvunit);
            }
        }

        @Override
        public int getItemCount() {
            return achieve2s.size();
        }


        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            /* 載入item_view_friend.xml其實是提供顯示一筆好友資料所需的View元件 */
            View itemView = layoutInflater.inflate(R.layout.item_view_achieve, viewGroup, false);
            /* 透過呼叫MyViewHolder建構式將itemView傳給MyViewHolder */
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder viewHolder, int position) {
            /* 依照position所代表的資料索引取得對應Friend物件 */
            final Achieve2 achieve2 = achieve2s.get(position);
            String url = Common.URL_SERVER + "AchieveServlet";
            int achieve_no = achieve2.getAchieve_no();
            achieve2ImageTask = new ImageTask(url, achieve_no, imageSize, viewHolder.ivachieve);
            achieve2ImageTask.execute();
            viewHolder.tvachieve_no.setText(String.valueOf(achieve2.getAchieve_name()));
            viewHolder.tvachieve_value.setText(String.valueOf(achieve2.getAchieve_value()));
            viewHolder.tvunit.setText(String.valueOf(achieve2.getUnit()));

        }
    }
    // 建立測試用資料4-9
    private List<Achieve2> getAchieve2s() {
        List<Achieve2> achieve2s = null;
        if (Common.networkConnected(activity)) {
            String url = Common.URL_SERVER + "/Achieve2Servlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAll");
            String jsonOut = jsonObject.toString();
            achieve2GetAllTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = achieve2GetAllTask.execute().get();
                Log.e(TAG ,jsonIn);
                // {"achieve_name":"6","achieve_value":"一公里最快時間","unit":"10:12:12"}
                // (int user_no, String tvachieve_no, String tvachieve_value, int tvachieve_count, int ivachieve, String tvunit, int achieve_no)
                Type listType = new TypeToken<List<Achieve2>>() {
                }.getType();
                achieve2s = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.toastShow(activity, R.string.textNoNetwork);
        }
        return achieve2s;
    }

    private void showAchieve2s(List<Achieve2> achieve2s) {
        if (achieve2s == null || achieve2s.isEmpty()) {
            Common.toastShow(activity, R.string.textNoAchievesFound);
            return;
        }
        Achieve2Adapter achieve2Adapter = (Achieve2Adapter) rvAchieve2.getAdapter();
        // 如果achieveAdapter不存在就建立新的，否則續用舊有的
        if (achieve2Adapter == null) {
            rvAchieve2.setAdapter(new Achieve2Adapter(activity, achieve2s));
        } else {
            achieve2Adapter.setAchieve2s(achieve2s);
            achieve2Adapter.notifyDataSetChanged();
        }
    }




}



