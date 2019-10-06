package com.g2.runningFront.ShopActivity.ShopCart;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class ShopCartFragment extends Fragment {
    Activity activity;
    View view;
    RecyclerView rv;
    Button btConfirm;
    TextView tvSumTotal;
    List<ShopCart> shopCartList;
    int sumTotal = 0;
    List<ShopCart> mList = new ArrayList<>();
    List<Boolean> isCheckedList = new ArrayList<>();
    CommonTask shopCartGetAllTask;
    ImageTask shopCartImageTask;
    boolean Confirl = false;

    private static final String url = Common.URL_SERVER + "ShopCartServlet";
    private static final String TAG = "TAG_SHOPCART";
    private final static String PREFERENCES_NAME = "preferences";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        activity.setTitle("購物車");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shop_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        shopCartList = getShopCartList();
        holdView();
        setSumTotal(shopCartList);
    }

    private void holdView() {
        btConfirm = view.findViewById(R.id.cart_btConfirm);
        tvSumTotal = view.findViewById(R.id.cart_tvSumTotal);
        rv = view.findViewById(R.id.cart_rv);
        rv.setLayoutManager(new LinearLayoutManager(activity));
        final CartViewAdapter cartViewAdapter = new CartViewAdapter(activity, shopCartList);

        rv.setAdapter(cartViewAdapter);

        btConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(sumTotal==0){
                    Common.toastShow(activity,"您無選擇商品，請選擇商品");
                    return;
                }
                Confirl = true;

                SharedPreferences pref = activity.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
                try {
                    updateShopCart();
                    pref.edit().putString("ShopCartList", new Gson().toJson(mList)).apply();
                    // 檔案到底去哪惹？

                    Bundle bundle = new Bundle();
                    bundle.putInt("SumTotal",sumTotal);
                    Navigation.findNavController(view).navigate(R.id.action_shopCartFragment_to_shopCartFillFragment,bundle);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d("TAG", String.valueOf(sumTotal));
                Log.d("TAG", new Gson().toJson(mList));
            }
        });

    }

    private class CartViewAdapter extends RecyclerView.Adapter<CartViewAdapter.myViewHolder> {
        private LayoutInflater layoutInflater;
        List<ShopCart> shopCarts;
        private int imageSize;

        public CartViewAdapter(Context context, List<ShopCart> shopCartList) {
            layoutInflater = LayoutInflater.from(context);
            shopCarts = shopCartList;
            imageSize = getResources().getDisplayMetrics().widthPixels / 5;

        }

        @Override
        public int getItemCount() {
            return shopCarts.size();
        }

        @NonNull
        @Override
        public CartViewAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.item_view_cart, parent, false);

            return new myViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final CartViewAdapter.myViewHolder holder, final int index) {

            final ShopCart shopCart = shopCarts.get(index);

            int Qty = shopCart.getQty();

            shopCartImageTask = new ImageTask(url, shopCart.getNo(), imageSize, holder.ivProImage);
            shopCartImageTask.execute();

            holder.tvProName.setText(shopCart.getName());
            holder.tvProDesc.setText(shopCart.getDesc());

            holder.ivProImage.setImageResource(R.drawable.pro_image);
            holder.ivPlus.setImageResource(R.drawable.iv_cart_plus);
            holder.ivMinus.setImageResource(R.drawable.iv_cart_minus);

            holder.etProNum.setText(String.valueOf(Qty));
            holder.tvTotal.setText("小計： " + shopCart.getTotal());

            View.OnClickListener chageQty = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int nQty = shopCart.getQty();
                    if (v.getId() == R.id.cart_ivPlus) {
                        nQty += 1;
                    } else if (v.getId() == R.id.cart_ivMinus && nQty >= 1) {
                        nQty -= 1;
                    }else {
                        Common.toastShow(activity, "商品數量已經為零，無法在減少數量。");
                    }
                    shopCart.setQty(nQty);
                    shopCarts.set(index, shopCart);
                    holder.etProNum.setText(String.valueOf(nQty));
                    holder.tvTotal.setText("小計： " + shopCart.getTotal());
                    setSumTotal(shopCarts);
                }
            };

            holder.ivPlus.setOnClickListener(chageQty);
            holder.ivMinus.setOnClickListener(chageQty);

            holder.btDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shopCarts.remove(index);
                    isCheckedList.remove(index);
                    setSumTotal(shopCarts);
                    notifyDataSetChanged();
                }
            });

            holder.cbSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!holder.cbSelect.isChecked()) {
                        isCheckedList.set(index, false);
                    } else {
                        isCheckedList.set(index, true);
                    }
                    shopCarts.set(index, shopCart);
                    setSumTotal(shopCarts);
                }
            });

            setSumTotal(shopCarts);
        }


        private class myViewHolder extends RecyclerView.ViewHolder {
            CheckBox cbSelect;

            ImageView ivProImage;
            TextView tvProName;
            TextView tvProDesc;

            ImageView ivPlus;
            ImageView ivMinus;
            EditText etProNum;
            TextView tvTotal;

            Button btDelete;

            public myViewHolder(View itemView) {
                super(itemView);
                cbSelect = itemView.findViewById(R.id.cart_cbSelect);
                ivProImage = itemView.findViewById(R.id.cart_ProImage);
                tvProName = itemView.findViewById(R.id.cart_tvProName);
                tvProDesc = itemView.findViewById(R.id.cart_tvDesc);
                ivPlus = itemView.findViewById(R.id.cart_ivPlus);
                ivMinus = itemView.findViewById(R.id.cart_ivMinus);
                etProNum = itemView.findViewById(R.id.cart_etProNum);
                tvTotal = itemView.findViewById(R.id.cart_tvTotal);
                btDelete = itemView.findViewById(R.id.cart_btDelete);
            }
        }
    }


    private List<ShopCart> getShopCartList() {

        List<ShopCart> shopCarts = new ArrayList<>();
        if (Common.networkConnected(activity)) {

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAll");
            jsonObject.addProperty("user_no", 1);
            String jsonOut = jsonObject.toString();
            shopCartGetAllTask = new CommonTask(url, jsonOut);
            try {

                String jsonIn = shopCartGetAllTask.execute().get();
                Type listTpye = new TypeToken<List<ShopCart>>() {
                }.getType();
                shopCarts = new Gson().fromJson(jsonIn, listTpye);

            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {

            Common.toastShow(activity, "no network connection available");

        }

        isCheckedList.clear();

        for (ShopCart a : shopCarts) {
            isCheckedList.add(true);
        }
        // 這樣寫是否洽當

        return shopCarts;
    }

    private void setSumTotal(List<ShopCart> shopCarts) {
        sumTotal = 0;
        mList.clear();

        for (int i = 0; i <= isCheckedList.size() - 1; i++) {

            if (isCheckedList.get(i)) {
                sumTotal += shopCarts.get(i).getTotal();
                mList.add(shopCarts.get(i));
            }
        }

        tvSumTotal.setText(String.valueOf(sumTotal));
    }


    @Override
    public void onPause() {
        super.onPause();

        if (!Confirl) {

            new AlertDialog.Builder(activity)
                    /* 設定標題 */
                    .setTitle("是否要儲存購物車？")
                    /* 設定圖示 */
                    .setIcon(R.drawable.ic_shopcart)
                    /* 設定訊息文字 */
                    .setMessage("你好好考慮清楚")
                    /* 設定positive與negative按鈕上面的文字與點擊事件監聽器 */
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            updateShopCart();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            /* 關閉對話視窗 */
                            dialog.cancel();
                        }
                    })
                    .show();
        }
    }

    public void updateShopCart(){
        /* 結束此Activity頁面 */
        if (Common.networkConnected(activity)) {

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "saveShopCart");
            jsonObject.addProperty("user_no", 1);
            // 需更改USER

            jsonObject.addProperty("ShopCart", new Gson().toJson(mList));
            String jsonOut = jsonObject.toString();
            shopCartGetAllTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = shopCartGetAllTask.execute().get();
                Common.toastShow(activity, "成功");

            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }

        } else {

            Common.toastShow(activity, "no network connection available");
        }
    }
}
