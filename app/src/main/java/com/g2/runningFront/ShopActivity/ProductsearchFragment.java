package com.g2.runningFront.ShopActivity;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.g2.runningFront.Common.Common;
import com.g2.runningFront.Common.CommonTask;
import com.g2.runningFront.Common.ImageTask;
import com.g2.runningFront.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.g2.runningFront.Common.Common.showToast;


public class ProductsearchFragment extends Fragment {

    private Activity activity;
    private RecyclerView recyclerView;
    private List<Commodity> commoditys;
    private CommonTask commodityGetAllTask;
    private ImageTask commodityImageTask;
    private static final String TAG = "TAG_SpotListFragment";
    private String product_name;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_productsearch, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final SearchView searchView = view.findViewById(R.id.svproduct);

        recyclerView = view.findViewById(R.id.rvsearchproduct);
        commoditys = getCommoditys();
        showCommoditys(commoditys);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setAdapter(new CommodityAdapter(activity, commoditys));

        //搜尋
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {


                CommodityAdapter adapter = (CommodityAdapter) recyclerView.getAdapter();


                if (adapter != null) {
                    // 如果搜尋條件為空字串，就顯示原始資料；否則就顯示搜尋後結果
                    if (newText.isEmpty()) {
                        adapter.setCommoditys(commoditys);
                    } else {
                        List<Commodity> searchCommoditys = new ArrayList<>();
                        // 搜尋原始資料內有無包含關鍵字(不區別大小寫)
                        for (Commodity commodity : commoditys) {
                            if (commodity.getPro_name().toUpperCase().contains(newText.toUpperCase())) {
                                searchCommoditys.add(commodity);
                            }
                        }
                        adapter.setCommoditys(searchCommoditys);
                    }
                    adapter.notifyDataSetChanged();
                    return true;
                }
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }
        });

        Bundle bundle = getArguments();
        product_name = bundle.getString("product_name");
        searchView.setQuery(product_name, true);
    }


    private class CommodityAdapter extends RecyclerView.Adapter<CommodityAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater;
        List<Commodity> commoditys;
        private int imageSize;


        CommodityAdapter(Context context, List<Commodity> commoditys) {
            layoutInflater = LayoutInflater.from(context);
            //  this.context = context;
            this.commoditys = commoditys;

            /* 螢幕寬度除以4當作將圖的尺寸 */
            imageSize = getResources().getDisplayMetrics().widthPixels / 4;

        }

        void setCommoditys(List<Commodity> commoditys) {
            this.commoditys = commoditys;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView pro_desc, pro_name, pro_price;
            ImageView pro_image;

            public MyViewHolder(View itemView) {
                super(itemView);
                pro_name = itemView.findViewById(R.id.pro_name);
                pro_price = itemView.findViewById(R.id.pro_price);
                pro_desc = itemView.findViewById(R.id.pro_desc);
                pro_image = itemView.findViewById(R.id.pro_image);
            }
        }

        @Override
        public int getItemCount() {
            return commoditys.size();
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View itemView = layoutInflater.inflate(R.layout.item_view_searchresults, viewGroup, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder viewHolder, final int postion) {
            final Commodity commodity = commoditys.get(postion);
            String url = Common.URL_SERVER + "adproductshowServlet";
            String pro_no = commodity.getPro_no();
            commodityImageTask = new ImageTask(url, pro_no, imageSize, viewHolder.pro_image);
            commodityImageTask.execute();
            viewHolder.pro_desc.setText(commodity.getPro_desc());
            viewHolder.pro_price.setText(String.valueOf(commodity.getPro_price()));
            viewHolder.pro_name.setText(commodity.getPro_name());

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("commodity", commodity);
                    Navigation.findNavController(v)
                            .navigate(R.id.action_productsearchFragment_to_productFragment2, bundle);

                }
            });
        }
    }

    //連線SQL抓取資料
    private List<Commodity> getCommoditys() {
        List<Commodity> commoditys = null;
        if (Common.networkConnected(activity)) {
            String url = Common.URL_SERVER + "/adproductServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAllproduct");
            String jsonOut = jsonObject.toString();
            commodityGetAllTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = commodityGetAllTask.execute().get();
                Type listType = new TypeToken<List<Commodity>>() {
                }.getType();
                commoditys = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            showToast(activity, R.string.textNoNetwork);
        }
        return commoditys;
    }

    // check if the device connect to the network
    private void showCommoditys(List<Commodity> commoditys) {
        if (commoditys == null || commoditys.isEmpty()) {
            showToast(activity, R.string.textNoProductsFound);
            return;
        }
        CommodityAdapter commodityAdapter = (CommodityAdapter) recyclerView.getAdapter();
        // 如果spotAdapter不存在就建立新的，否則續用舊有的
        if (commodityAdapter == null) {
            recyclerView.setAdapter(new CommodityAdapter(activity, commoditys));
        } else {
            commodityAdapter.setCommoditys(commoditys);
            commodityAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (commodityGetAllTask != null) {
            commodityGetAllTask.cancel(true);
            commodityGetAllTask = null;
        }
        if (commodityImageTask != null) {
            commodityImageTask.cancel(true);
            commodityImageTask = null;
        }

    }


}

