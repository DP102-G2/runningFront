package com.g2.runningFront.ShopActivity.ShopCart;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.g2.runningFront.R;
import com.google.gson.Gson;

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


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
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

                SharedPreferences pref = activity.getSharedPreferences("example", MODE_PRIVATE);
                try {
                    pref.edit().putString("ShopCartList", new Gson().toJson(mList)).putInt("SumTotal",sumTotal).apply();
                    // 檔案到底去哪惹？

                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d("TAG",String.valueOf(sumTotal));
                Log.d("TAG", new Gson().toJson(mList));
            }
        });

    }

    private class CartViewAdapter extends RecyclerView.Adapter<CartViewAdapter.myViewHolder> {
        private LayoutInflater layoutInflater;
        List<ShopCart> shopCarts;

        public CartViewAdapter(Context context, List<ShopCart> shopCartList) {
            layoutInflater = LayoutInflater.from(context);
            shopCarts = shopCartList;

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
                    } else if (v.getId() == R.id.cart_ivMinus) {
                        nQty -= 1;
                    }
                    shopCart.setQty(nQty);
                    shopCarts.set(index, shopCart);
                    notifyDataSetChanged();
                    // 提醒改變資料
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
                    notifyDataSetChanged();
                }
            });

            setSumTotal(shopCarts);
        }

        public void setShopCarts(List<ShopCart> shopCarts) {
            this.shopCarts = shopCarts;
        }

        public List<ShopCart> getShopCarts() {
            return shopCarts;
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
        shopCarts.add(new ShopCart("神奇寶貝球", "史上最厲害", 20, 100, 100));
        shopCarts.add(new ShopCart("神奇寶貝球", "史上最厲害", 10, 120, 200));
        shopCarts.add(new ShopCart("神奇寶貝球", "史上最厲害", 15, 200, 100));
        shopCarts.add(new ShopCart("神奇寶貝球", "史上最厲害", 5, 90, 100));
        shopCarts.add(new ShopCart("神奇寶貝球", "史上最厲害", 3, 200, 120));

        for (ShopCart a : shopCarts) {
            isCheckedList.add(true);
        }
        // 這樣寫是否洽當

        return shopCarts;
    }

    private void setSumTotal(List<ShopCart> shopCarts) {
        sumTotal = 0;
        mList.clear();

        for (int i = 0; i <= isCheckedList.size()-1; i++) {

            if (isCheckedList.get(i)) {
                sumTotal += shopCarts.get(i).getTotal();
                mList.add(shopCarts.get(i));
            }
        }

        tvSumTotal.setText(String.valueOf(sumTotal));
    }

}
