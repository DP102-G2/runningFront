package com.g2.runningFront.ShopActivity;


import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.g2.runningFront.Common.CommonTask;
import com.g2.runningFront.R;

import java.util.ArrayList;
import java.util.List;


public class ShopMainFragment extends Fragment {

    private Activity activity;
    private AnimationDrawable animationDrawable;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private List<Product> products;
    private CommonTask retrieveFriendTask;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle(R.string.shopping);
        return inflater.inflate(R.layout.fragment_shop_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchView = view.findViewById(R.id.searchView);
        recyclerView = view.findViewById(R.id.recyclerView);
        products = getProducts();
//        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(2,
                        StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(new ProductAdapter(activity, products));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {


            @Override
            public boolean onQueryTextChange(String newText) {
                ProductAdapter adapter = (ProductAdapter) recyclerView.getAdapter();
                if (adapter != null) {
                    // 如果搜尋條件為空字串，就顯示原始資料；否則就顯示搜尋後結果
                    if (newText.isEmpty()) {
                        adapter.setProducts(products);
                    } else {
                        List<Product> searchProducts = new ArrayList<>();
                        // 搜尋原始資料內有無包含關鍵字(不區別大小寫)
                        for (Product product : products) {
                            if (product.getname().toUpperCase().contains(newText.toUpperCase())) {
                                searchProducts.add(product);
                            }
                        }
                        adapter.setProducts(searchProducts);
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


        List<Drawable> drawables = new ArrayList<>();
        Resources res = getResources();
        /* API 21開始以Resources.getDrawable(int, Resources.Theme)取代
           getDrawable(int)；建議先檢查系統版本，再決定要呼叫新/舊版的方法 */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            /* Resources.getDrawable()並指定圖片ID可取得對應圖片。
               圖片資料類型必須為Drawable方可用於動畫 */
            drawables.add(res.getDrawable(R.drawable.ic_achieve, null));
            drawables.add(res.getDrawable(R.drawable.ic_battle, null));
            drawables.add(res.getDrawable(R.drawable.ic_clothes, null));

        } else {
            drawables.add(res.getDrawable(R.drawable.ic_achieve));
            drawables.add(res.getDrawable(R.drawable.ic_battle));
            drawables.add(res.getDrawable(R.drawable.ic_battle));

        }
        animationDrawable = new AnimationDrawable();
        animationDrawable.setOneShot(false);
        int duration = 1000;
        for (Drawable drawable : drawables) {
            animationDrawable.addFrame(drawable, duration);
        }

        ImageView ivPicture = view.findViewById(R.id.ivPicture);
        /* 呼叫View.setBackground()，該ImageView即可套用動畫設定 */
        ivPicture.setBackground(animationDrawable);
        /* AnimationDrawable.setOneShot()設定動畫是否只播放一次，
           true代表只播放一次，false代表連續播放。
           AnimationDrawable.addFrame()加入播放的圖片，
           並設定該圖片持續顯示的時間

        當ImageView被點擊，先呼叫isRunning()判斷是否在播放動畫；
       如果正在播放動畫，就呼叫stop()停止播放；否則就呼叫start()播放 */
        animationDrawable.start();
    }


    private class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {
        Context context;
        List<Product> products;
        public ProductAdapter(Context context, List<Product> products) {
            this.context = context;
            this.products = products;
        }

        public void setProducts(List<Product> products) {
            this.products = products;
        }

        @Override
        public int getItemCount() {
            return products.size();
        }

        private class MyViewHolder extends RecyclerView.ViewHolder {
            TextView pro_desc, pro_name,pro_price;
            ImageView pro_picture;
            public MyViewHolder(View itemView) {
                super(itemView);
                pro_name = itemView.findViewById(R.id.pro_name);
                pro_price = itemView.findViewById(R.id.pro_price);
                pro_desc = itemView.findViewById(R.id.pro_desc);
                pro_picture = itemView.findViewById(R.id.pro_picture);
            }
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.item_view_product, viewGroup, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder viewHolder, int index) {
            final Product product = products.get(index);
            viewHolder.pro_desc.setText(product.getdesc());
            viewHolder.pro_price.setText(String.valueOf(product.getprice()));
            viewHolder.pro_name.setText(product.getname());
            viewHolder.pro_picture.setImageResource(product.getImage());

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("product", product);
                    Navigation.findNavController(searchView)
                            .navigate(R.id.action_shopMainFragment_to_productFragment, bundle);
                }
            });
        }
    }

    private List<Product> getProducts() {
        List<Product> products = new ArrayList<>();
//        if (Common.networkConnected(activity)) {
//            String url = Common.URL + "ProductsServlet";
//            JsonObject jsonObject = new JsonObject();
//            jsonObject.addProperty("action", "getAll");
//            retrieveFriendTask = new CommonTask(url, jsonObject.toString());
//            try {
//                String jsonIn = retrieveFriendTask.execute().get();
//                Type listType = new TypeToken<List<Product>>() {
//                }.getType();
//                products = new Gson().fromJson(jsonIn, listType);
//            } catch (Exception e) {
//                Log.e(TAG, e.toString());
//            }
//            if (products == null || products.isEmpty()) {
//                Log.e(TAG, getString(R.string.textProductNotFound));
//            }
//        } else {
//            Log.e(TAG, getString(R.string.textNoNetwork));
//        }
//        return products;
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        if (retrieveFriendTask != null) {
//            retrieveFriendTask.cancel(true);
//            retrieveFriendTask = null;
//        }
//    }

        products.add(new Product(111,"aaa","a",R.drawable.ic_achieve));
        products.add(new Product(222,"bbb","b",R.drawable.ic_clothes));
        products.add(new Product(333,"ccc","c",R.drawable.ic_run));
        products.add(new Product(444,"ddd","d",R.drawable.ic_hat));
        return products;
    }


}


