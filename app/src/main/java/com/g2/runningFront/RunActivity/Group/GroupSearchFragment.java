package com.g2.runningFront.RunActivity.Group;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;// Debug 列印錯誤使用
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.g2.runningFront.Common.Common;
import com.g2.runningFront.R;// Resource 資源

/* 連線資料庫取回資料使用 */
import com.g2.runningFront.Common.CommonTask;
/* 屬於 Group Package 底下的 ImageTask */
import com.g2.runningFront.RunActivity.Group.Common.ImageTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

/* 處理 List<JsonObject> 這個特殊型別 */
import java.util.List;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;


public class GroupSearchFragment extends Fragment {
    private static String TAG = "TAG_Serach";
    private Activity activity;
    private SearchView searchView;

    /* 用於 RecyclerView 查詢、承接資料 */
    private RecyclerView recyclerView;
    private List<Follow> searchedList;
    private CommonTask SearchTask;
    private ImageTask SearchImageTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle("搜尋會員");
        return inflater.inflate(R.layout.fragment_group_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /* 使用 RecyclerView */
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));

        /* 使用 SearchView */
        searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String searchText) {

                // 如果搜尋條件為空字串，就顯示原始資料；否則就顯示搜尋後結果
                if(searchText.length() > 1){
                    searchedList = getSearched(searchText.toUpperCase());
                    showSearched(searchedList);
                } else {
                    showSearched(null);
                }
                // 不知為何要寫這個
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

        });

        view.findViewById(R.id.bt_qrCode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view)
                        .navigate(R.id.action_groupSearchFragment_to_groupQRcodeFragment);
            }
        });
    }

    private void showSearched(List<Follow> searchedList) {
        if (searchedList == null || searchedList.isEmpty()){
            return;
        }

        SearchAdapter searchAdapter = (SearchAdapter) recyclerView.getAdapter();

        // 如果 searchAdapter 不存在就建立新的，否則續用舊有的
        if (searchAdapter == null) {
            recyclerView.setAdapter(new SearchAdapter(activity, searchedList));

        } else {
            searchAdapter.setFollows(searchedList);
            searchAdapter.notifyDataSetChanged();
        }
    }

    private class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater;
        private List<Follow> follows;
        private final int IMAGE_SIZE = 85;

        SearchAdapter(Context context, List<Follow> follows) {
            layoutInflater = LayoutInflater.from(context);
            this.follows = follows;
        }

        void setFollows(List<Follow> follows) {
            this.follows = follows;
        }

        @Override
        public int getItemCount() {
            return follows.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            TextView tvName, tvId;

            MyViewHolder(View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.imageView);
                tvName = itemView.findViewById(R.id.tvName);
                tvId = itemView.findViewById(R.id.tvId);
            }
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.item_view_search, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int position) {

            final Follow follow = follows.get(position);
            String url = Common.URL_SERVER + "GroupServlet";

            myViewHolder.tvName.setText(follow.getName());
            myViewHolder.tvId.setText("ID: " + follow.getId());

            /* 索取追蹤會員大頭貼 */
            int no = follow.getNo();
            SearchImageTask = new ImageTask(url, no, IMAGE_SIZE, myViewHolder.imageView);
            SearchImageTask.execute();

            myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("user_no", follow.getNo());
                    Navigation.findNavController(view)
                            .navigate(R.id.action_groupSearchFragment_to_FriendFragment, bundle);
                }
            });

        }
    }

    /* 取得查詢會員資料集合 */
    private List<Follow> getSearched(String searchText) {

        List<Follow> searchedList = null;

        if (Common.networkConnected(activity)) {
            String url = Common.URL_SERVER + "GroupServlet";
            JsonObject jo = new JsonObject();
            jo.addProperty("action", "search");
            jo.addProperty("searchText", searchText);

            SearchTask = new CommonTask(url, jo.toString());

            try {
                String jsonIn = SearchTask.execute().get();

                Log.d(TAG, "傳回的 List<Follow> = \n" + jsonIn);
                Type listType = new TypeToken<List<Follow>>() {
                }.getType();
                searchedList = new Gson().fromJson(jsonIn, listType);

            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.toastShow(activity, R.string.textNoNetwork);
        }
        return searchedList;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (SearchTask != null) {
            SearchTask.cancel(true);
            SearchTask = null;
        }
        if(SearchImageTask != null){
            SearchImageTask.cancel(true);
            SearchImageTask = null;
        }
    }
}