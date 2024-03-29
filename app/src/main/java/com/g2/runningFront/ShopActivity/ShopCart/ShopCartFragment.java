package com.g2.runningFront.ShopActivity.ShopCart;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
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

import static android.content.Context.MODE_PRIVATE;


public class ShopCartFragment extends Fragment {

    Activity activity;
    SharedPreferences pref;
    int user_no;


    private static final String url = Common.URL_SERVER + "ShopCartServlet";
    private static final String TAG = "TAG_SHOPCART";
    private final static String PREFERENCES_NAME = "preference";
    // 偏好設定的名稱

    // VIEW
    View view;
    RecyclerView rvShopCart;
    Button btConfirm;
    TextView tvSumTotal,tvNoCart;

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
        Common.signIn(activity);
        user_no = Common.getUserNo(activity);
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
        pref = activity.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        shopCartList = getShopCartList();
        // 一開始取伺服器的購物車資料
        activity.setTitle("購物車");

            holdView();
            setSumTotal(shopCartList);
        // 根據取購物車結果，計算總計的數值
    }

    private void holdView() {
        btConfirm = view.findViewById(R.id.cart_btConfirm);
        tvSumTotal = view.findViewById(R.id.cart_tvSumTotal);
        rvShopCart = view.findViewById(R.id.cart_rv);
        tvNoCart = view.findViewById(R.id.cart_tvNoCart);

        if(shopCartList==null){
            rvShopCart.setVisibility(View.GONE);

        }else {
            tvNoCart.setVisibility(View.GONE);
        }
            rvShopCart.setLayoutManager(new LinearLayoutManager(activity));
            final CartViewAdapter cartViewAdapter = new CartViewAdapter(activity, shopCartList);
            rvShopCart.setAdapter(cartViewAdapter);

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
            if(shopCart.getStock() <1){
                holder.tvStock.setText("庫存不足");
                holder.etProNum.setText("0");
                holder.cardView.setCardBackgroundColor(activity.getColor(R.color.colorBrown));
                holder.cbSelect.setChecked(false);
                holder.cbSelect.setEnabled(false);
                holder.ivPlus.setEnabled(false);
                holder.ivMinus.setEnabled(false);
                holder.etProNum.setEnabled(false);
                shopCart.setQty(0);
                holder.tvTotal.setText("小計： " + shopCart.getTotal());
                holder.etProNum.setText(String.valueOf(0));
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Common.showToast(activity,"庫存不足");
                    }
                });
            }else {
                holder.tvStock.setText("庫存： " + shopCart.getStock());
            }

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
                    int nQty;
                    boolean checked ;
                    if (b.equals("")) {
                        nQty = 0;
                        holder.cbSelect.setChecked(false);
                        checked = false;
                    } else {
                        nQty = Integer.parseInt(b);
                        if (nQty > shopCart.getStock() | nQty == 0) {
                            checked = false;
                            holder.cbSelect.setChecked(false);
                            nQty = 0;
                            Common.toastShow(activity, "選擇數量已大於庫存");
                        } else {
                            holder.cbSelect.setChecked(true);
                            checked = true;
                            // 重新計算總價
                        }
                    }
                    isCheckedList.set(index, checked);
                    shopCart.setQty(nQty);
                    shopCarts.set(index, shopCart);
                    holder.tvTotal.setText("小計： " + shopCart.getTotal());
                    setSumTotal(shopCarts);
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
                    }
                    shopCart.setQty(nQty);
                    // 根據加減結果顯示數量
                    shopCarts.set(index, shopCart);
                    holder.tvTotal.setText("小計： " + shopCart.getTotal());
                    holder.etProNum.setText(String.valueOf(nQty));
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


            holder.cbSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    int nQty = 0;

                    if (!holder.etProNum.getText().toString().equals("")) {
                        nQty = Integer.valueOf(holder.etProNum.getText().toString().trim());
                    }

                    if (!isChecked | shopCart.getStock() < nQty  | nQty == 0) {
                        buttonView.setChecked(false);
                        shopCart.setQty(0);
                        holder.tvTotal.setText("小計： " + shopCart.getTotal());
                        holder.etProNum.setText(String.valueOf(0));
                        Common.toastShow(activity, "請再次確認數量");
                    } else if (isChecked) {
                        buttonView.setChecked(true);
                        isCheckedList.set(index, true);
                    }
                    shopCarts.set(index, shopCart);
                    setSumTotal(shopCarts);

                }
            });

            setSumTotal(shopCarts);

        }


        private class myViewHolder extends RecyclerView.ViewHolder {

            CardView cardView;

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
                cardView = itemView.findViewById(R.id.cart_CardView);
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
                jsonObject.addProperty("user_no", user_no);
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

        if (!Confirl && shopCartList != null) {

            updateShopCart();
            Common.toastShow(activity, "儲存購物車成功");

        }

    }

    public void updateShopCart() {
        //更新購物車

        /* 結束此Activity頁面 */
        if (Common.networkConnected(activity)) {

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "saveShopCart");
            jsonObject.addProperty("user_no", user_no);
            // 需更改USER

            jsonObject.addProperty("ShopCart", new Gson().toJson(mList));
            String jsonOut = jsonObject.toString();
            shopCartGetAllTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = shopCartGetAllTask.execute().get();
                Common.toastShow(activity, "儲存購物車成功");

            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }

        } else {

            Common.toastShow(activity, "no network connection available");
        }
    }
}
