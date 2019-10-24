package com.g2.runningFront.ShopActivity;


import android.app.Activity;
import android.content.Context;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.g2.runningFront.Common.Common;
import com.g2.runningFront.Common.CommonTask;
import com.g2.runningFront.Common.ImageTask;
import com.g2.runningFront.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import static com.g2.runningFront.Common.Common.showToast;


public class SearchresultsFragment extends Fragment {
    private Activity activity;
    private RecyclerView recyclerView;
    private List<Product> products;
    private CommonTask productGetAllTask;
    private ImageTask productImageTask;
    private static final String TAG = "TAG_SpotListFragment";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle((R.string.searchresults));
        return inflater.inflate(R.layout.fragment_searchresults, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.rvsearchresult);
        products = getProducts();
        showProducts(products);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setAdapter(new ProductAdapter(activity, products));

    }

    private class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater;
        List<Product> products;
        private int imageSize;


        ProductAdapter(Context context, List<Product> products) {
            layoutInflater = LayoutInflater.from(context);
            //  this.context = context;
            this.products = products;

            /* 螢幕寬度除以4當作將圖的尺寸 */
            imageSize = getResources().getDisplayMetrics().widthPixels / 4;

        }

        void setProducts(List<Product> products) {
            this.products = products;
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
            return products.size();
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View itemView = layoutInflater.inflate(R.layout.item_view_searchresults, viewGroup, false);
            return new ProductAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final ProductAdapter.MyViewHolder viewHolder, final int postion) {
            final Product product = products.get(postion);
            String url = Common.URL_SERVER + "adproductshowServlet";
            String pro_no = product.getPro_no();
            productImageTask = new ImageTask(url, pro_no, imageSize, viewHolder.pro_image);
            productImageTask.execute();
            viewHolder.pro_desc.setText(product.getPro_desc());
            viewHolder.pro_price.setText(String.valueOf(product.getPro_price()));
            viewHolder.pro_name.setText(product.getPro_name());
        }
    }

    //連線SQL抓取資料
    private List<Product> getProducts() {
        List<Product> products = null;
        if (Common.networkConnected(activity)) {
            String url = Common.URL_SERVER + "/adproductshowServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAll");
            String jsonOut = jsonObject.toString();
            productGetAllTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = productGetAllTask.execute().get();
                Type listType = new TypeToken<List<Product>>() {
                }.getType();
                products = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            showToast(activity, R.string.textNoNetwork);
        }
        return products;
    }

    // check if the device connect to the network
    private void showProducts(List<Product> products) {
        if (products == null || products.isEmpty()) {
            showToast(activity, R.string.textNoProductsFound);
            return;
        }
        ProductAdapter productAdapter = (ProductAdapter) recyclerView.getAdapter();
        // 如果spotAdapter不存在就建立新的，否則續用舊有的
        if (productAdapter == null) {
            recyclerView.setAdapter(new ProductAdapter(activity, products));
        } else {
            productAdapter.setProducts(products);
            productAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (productGetAllTask != null) {
            productGetAllTask.cancel(true);
            productGetAllTask = null;
        }
        if (productImageTask != null) {
            productImageTask.cancel(true);
            productImageTask = null;
        }

    }


}
