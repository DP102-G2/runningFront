package com.g2.runningFront.ShopActivity.ShopCart;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
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

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;


public class ShopCartFragment extends Fragment  {
    Activity activity;

    private static final String url = Common.URL_SERVER + "ShopCartServlet";
    private static final String TAG = "TAG_SHOPCART";
    private final static String PREFERENCES_NAME = "preferences";
    // 偏好設定的名稱

    // VIEW
    View view;
    RecyclerView rv;
    Button btConfirm;
    TextView tvSumTotal;

    List<ShopCart> shopCartList = new ArrayList<>();
    // 存放一開始載入的清單

    List<ShopCart> mList = new ArrayList<>();
    // 存放實際被勾選的清單

    List<Boolean> isCheckedList = new ArrayList<>();
    // 存放每個項目是否被勾選

    int sumTotal = 0;
    // 購物車的總值

    CommonTask shopCartGetAllTask;
    ImageTask shopCartImageTask;
    boolean Confirl = false;
    // 判斷是否為唐突離開


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
        // 一開始取伺服器的購物車資料
        if (shopCartList != null) {
            holdView();
            setSumTotal(shopCartList);
        }else {
            Common.toastShow(activity,"無法連線");
        }
        // 根據取購物車結果，計算總計的數值
    }

    private void holdView() {
        btConfirm = view.findViewById(R.id.cart_btConfirm);
        tvSumTotal = view.findViewById(R.id.cart_tvSumTotal);

        rv = view.findViewById(R.id.cart_rv);
        rv.setLayoutManager(new LinearLayoutManager(activity));
        final CartViewAdapter cartViewAdapter = new CartViewAdapter(activity, shopCartList);
        rv.setAdapter(cartViewAdapter);

        // 確認結帳的按鈕
        btConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sumTotal == 0) {
                    Common.toastShow(activity, "您無選擇商品，請選擇商品");
                    return;
                }
                Confirl = true;

                // 將資料存入到偏好設定
                SharedPreferences pref = activity.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
                try {
                    updateShopCart();
                    // 同時將購物車篩選結果存入網路
                    pref.edit().putString("ShopCartList", new Gson().toJson(mList)).apply();
                    // 檔案到底去哪惹？

                    Bundle bundle = new Bundle();
                    bundle.putInt("SumTotal", sumTotal);
                    Navigation.findNavController(view).navigate(R.id.action_shopCartFragment_to_shopCartFillFragment, bundle);
                    // 將計算成果帶到下一頁，才能儲存入訂單

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
            holder.tvStock.setText("庫存： " + shopCart.getStock());

            //監測文字改變
            holder.etProNum.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    String b = s.toString().trim();
                    int nQty = 0;

                    if (b.equals("") | b.equals("0")) {
                        holder.cbSelect.setChecked(false);
                        nQty = 0;
                        shopCart.setQty(nQty);
                        shopCarts.set(index, shopCart);
                        // 上面只是修改顯示的本筆資料，現在要修改購物車list內的實際資料
                        holder.tvTotal.setText("小計： " + shopCart.getTotal());
                        setSumTotal(shopCarts);
                    } else {
                        nQty = Integer.parseInt(b);
                        shopCart.setQty(nQty);
                        // 根據加減結果顯示數量
                        shopCarts.set(index, shopCart);
                        // 上面只是修改顯示的本筆資料，現在要修改購物車list內的實際資料
                        holder.cbSelect.setChecked(true);

                        holder.tvTotal.setText("小計： " + shopCart.getTotal());
                        setSumTotal(shopCarts);
                        // 重新計算總價
                    }
                }
            });

            // 按加減時的指令碼
            View.OnClickListener chageQty = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int nQty = shopCart.getQty();
                    if (v.getId() == R.id.cart_ivPlus && nQty < shopCart.getStock()) {
                        nQty += 1;
                    } else if (v.getId() == R.id.cart_ivMinus && nQty >= 1) {
                        nQty -= 1;
                    } else {
                        Common.toastShow(activity, "無法調整數量。");
                    }
                    shopCart.setQty(nQty);
                    // 根據加減結果顯示數量
                    shopCarts.set(index, shopCart);
                    // 上面只是修改顯示的本筆資料，現在要修改購物車list內的實際資料

                    holder.etProNum.setText(String.valueOf(nQty));
                    holder.tvTotal.setText("小計： " + shopCart.getTotal());
                    setSumTotal(shopCarts);
                    // 重新計算總價
                }
            };

            holder.ivPlus.setOnClickListener(chageQty);
            holder.ivMinus.setOnClickListener(chageQty);

            holder.btDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shopCarts.remove(index);
                    isCheckedList.remove(index);
                    // 刪除兩個LIST

                    setSumTotal(shopCarts);
                    notifyDataSetChanged();
                    // 重新計算總價＋重製整個RV
                }
            });

            // 點選CHECKBOX時，更改clecklist裡的資料(計算時不要計算到)

            holder.cbSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked == false) {
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
            TextView tvTotal, tvStock;

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
                tvStock = itemView.findViewById(R.id.cart_tvStock);
            }
        }
    }


    // 一開始抓取伺服器的資料
    private List<ShopCart> getShopCartList() {

        List<ShopCart> shopCarts = new ArrayList<>();
        if (Common.networkConnected(activity)) {
            try {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("action", "getAll");
                jsonObject.addProperty("user_no", 1);
                // 屆時要更換成抓取偏好設定裡的使用者編號

                String jsonOut = jsonObject.toString();
                shopCartGetAllTask = new CommonTask(url, jsonOut);

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

        // 這邊要根據抓下來的list數量，創建同樣數量的LIST稱載是否被勾選
        isCheckedList.clear();


        if (shopCarts != null && !shopCarts.isEmpty()) {
            for (ShopCart a : shopCarts) {
                isCheckedList.add(true);
            }
            return shopCarts;

        }
        // 這樣寫是否洽當
        return null;
    }

    private void setSumTotal(List<ShopCart> shopCarts) {
        //計算總價
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


    // 離開始顯示提醒事項
    @Override
    public void onPause() {
        super.onPause();

        if (!Confirl&&shopCartList!=null) {


            PopupMenu popupMenu= new PopupMenu(activity,view);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    return false;
                }
            });

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

    public void updateShopCart() {
        //更新購物車

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
