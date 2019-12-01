package com.g2.runningFront.ShopActivity;


import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.graphics.Bitmap;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.g2.runningFront.Common.CommonTask;
import com.g2.runningFront.Common.Common;
import com.g2.runningFront.Common.ImageTask;
import com.g2.runningFront.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.g2.runningFront.Common.Common.showToast;


public class ShopMainFragment extends Fragment {

    private static final String TAG = "TAG_SpotListFragment";
    private Activity activity;
    private SearchView searchView;
    private RecyclerView recyclerView, rvadimage;
    private List<Product> products;
    private CommonTask productGetAllTask, adGetAllTask;
    private ImageTask productImageTask, adimageTask;
    private List<Adimage> adimages;
    private byte[] image;
    private Bitmap picture;
    private Timer mTimer;
    int adimagetime = 0;
    private CardView shoe, clothes, battle, hat, sock;

    ShopActivity shopActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        shopActivity = (ShopActivity) getActivity();

        // 廣告輪播init timer
        mTimer = new Timer();
        // start timer task
        setTimerTask();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle(R.string.shopping);
        return inflater.inflate(R.layout.fragment_shop_main, container, false);
    }


    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        shopActivity.btbShop.setVisibility(View.VISIBLE);

        shoe = view.findViewById((R.id.shoe));
        clothes = view.findViewById((R.id.clothes));
        battle = view.findViewById((R.id.battle));
        hat = view.findViewById((R.id.hat));
        sock = view.findViewById((R.id.sock));
        searchView = view.findViewById(R.id.searchview);

        //每日推薦的recycleview
        recyclerView = view.findViewById(R.id.recyclerView);
        products = getProducts();
        showProducts(products);
//        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(2,
                        StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(new ProductAdapter(activity, products));

        //廣告輪播的recycleview
        rvadimage = view.findViewById(R.id.rvadimage);
        rvadimage.setLayoutManager(new StaggeredGridLayoutManager(1,
                StaggeredGridLayoutManager.HORIZONTAL));
        rvadimage.setAdapter(new ShopMainFragment.AdimageAdapter(activity, adimages));
        adimages = getAdimages();
        showAdimages(adimages);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                ProductAdapter adapter = (ProductAdapter) recyclerView.getAdapter();
                if (adapter != null) {
                    // 如果搜尋條件為空字串，就顯示原始資料；否則就顯示搜尋後結果
                    if (newText.isEmpty()) {
                        adapter.setProducts(products);
                    }
                }

                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {


                String product_name = searchView.getQuery().toString();

                Bundle bundle = new Bundle();
                bundle.putSerializable("product_name", product_name);

                Navigation.findNavController(view)
                        .navigate(R.id.action_shopMainFragment_to_productsearchFragment, bundle);

                return false;
            }
        });

        //磁片區 鞋子
        shoe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = "shoe";
                Bundle bundle = new Bundle();
                bundle.putSerializable("name", name);
                Navigation.findNavController(v)
                        .navigate(R.id.action_shopMainFragment_to_searchresultsFragment, bundle);
            }
        });

        //磁片區 衣服
        clothes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = "clothes";
                Bundle bundle = new Bundle();
                bundle.putSerializable("name", name);
                Navigation.findNavController(v)
                        .navigate(R.id.action_shopMainFragment_to_searchresultsFragment, bundle);
            }
        });

        //磁片區 水壺
        battle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = "battle";
                Bundle bundle = new Bundle();
                bundle.putSerializable("name", name);
                Navigation.findNavController(v)
                        .navigate(R.id.action_shopMainFragment_to_searchresultsFragment, bundle);
            }
        });

        //磁片區 帽子
        hat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = "hat";
                Bundle bundle = new Bundle();
                bundle.putSerializable("name", name);
                Navigation.findNavController(v)
                        .navigate(R.id.action_shopMainFragment_to_searchresultsFragment, bundle);
            }
        });

        //磁片區 襪子
        sock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = "sock";
                Bundle bundle = new Bundle();
                bundle.putSerializable("name", name);
                Navigation.findNavController(v)
                        .navigate(R.id.action_shopMainFragment_to_searchresultsFragment, bundle);
            }
        });


    }


    private class AdimageAdapter extends RecyclerView.Adapter<AdimageAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater;
        //        private Context context;
        private List<Adimage> adimages;
        private int imageSize;


        AdimageAdapter(Context context, List<Adimage> adimages) {
            layoutInflater = LayoutInflater.from(context);
            this.adimages = adimages;

            /* 螢幕寬度除以4當作將圖的尺寸 */
            imageSize = getResources().getDisplayMetrics().widthPixels / 4;

        }

        void setAdimages(List<Adimage> adimages) {
            this.adimages = adimages;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView adimage;

            MyViewHolder(View itemView) {
                super(itemView);
                adimage = itemView.findViewById(R.id.adimage);
            }
        }

        @Override
        public int getItemCount() {
            return adimages.size();
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View itemView = layoutInflater.inflate(R.layout.item_view_adimage, viewGroup, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder viewHolder, int position) {
            final Adimage adimage = adimages.get(position);
            int ad_no = (position < 3) ? 0 + (position + 1) : (position + 1);
            String url = Common.URL_SERVER + "adproductServlet";
            adimageTask = new ImageTask(url, ad_no, imageSize, viewHolder.adimage);
            adimageTask.execute();

            viewHolder.adimage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("adimage", adimage);
                    Navigation.findNavController(view)
                            .navigate(R.id.action_shopMainFragment_to_productFragment, bundle);
                }
            });


        }
    }

    public List<Adimage> getAdimages() {
        List<Adimage> adimages = null;
        if (Common.networkConnected(activity)) {
            String url = Common.URL_SERVER + "/adproductServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAll");
            String jsonOut = jsonObject.toString();
            adGetAllTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = adGetAllTask.execute().get();
                Type listType = new TypeToken<List<Adimage>>() {
                }.getType();
                adimages = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.showToast(activity, R.string.textNoNetwork);
        }
        return adimages;
    }

    private void showAdimages(List<Adimage> adimages) {
        if (adimages == null || adimages.isEmpty()) {
            Common.showToast(activity, R.string.textNoProductsFound);
            return;
        }
        AdimageAdapter adimageAdapter = (AdimageAdapter) rvadimage.getAdapter();
        // 如果spotAdapter不存在就建立新的，否則續用舊有的
        if (adimageAdapter == null) {
            rvadimage.setAdapter(new AdimageAdapter(activity, adimages));
        } else {
            adimageAdapter.setAdimages(adimages);
            adimageAdapter.notifyDataSetChanged();
        }
    }


    private class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater;
        //Context context;
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
            View itemView = layoutInflater.inflate(R.layout.item_view_product, viewGroup, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder viewHolder, final int postion) {
            final Product product = products.get(postion);
            String url = Common.URL_SERVER + "adproductshowServlet";
            String pro_no = product.getPro_no();
            productImageTask = new ImageTask(url, pro_no, imageSize, viewHolder.pro_image);
            productImageTask.execute();
            viewHolder.pro_desc.setText(product.getPro_desc());
            viewHolder.pro_price.setText(String.valueOf(product.getPro_price()));
            viewHolder.pro_name.setText(product.getPro_name());

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

    //timetask 廣告輪播
    @Override
    public void onDestroy() {
        super.onDestroy();
        // cancel timer
        mTimer.cancel();
    }

    private void setTimerTask() {
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 1;
                doActionHandler.sendMessage(message);
            }
        }, 1000, 2000/* 表示1000毫秒之後，每隔1000毫秒執行一次 */);
    }

    private Handler doActionHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (rvadimage==null){
                return;
            }
            int msgId = msg.what;
            switch (msgId) {
                case 1:
                    rvadimage.smoothScrollToPosition(adimagetime);
                    adimagetime++;
                    if (adimagetime >= 3) {
                        adimagetime = 0;
                    }
                    break;
                default:
                    break;
            }
        }
    };

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
        if (adGetAllTask != null) {
            adGetAllTask.cancel(true);
            adGetAllTask = null;
        }
        if (adimageTask != null) {
            adimageTask.cancel(true);
            adimageTask = null;
        }
    }


}