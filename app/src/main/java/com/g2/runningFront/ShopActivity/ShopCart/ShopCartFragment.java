package com.g2.runningFront.ShopActivity.ShopCart;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.g2.runningFront.R;

import java.util.ArrayList;
import java.util.List;

import static android.media.CamcorderProfile.get;


public class ShopCartFragment extends Fragment {
    Activity activity;
    View view;
    RecyclerView rv;
    Button btConfirm;
    TextView tvSumTotal;
    List<ShopCart> shopCartList;


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
    }

    private void holdView() {
        btConfirm = view.findViewById(R.id.cart_btConfirm);
        tvSumTotal = view.findViewById(R.id.cart_tvSumTotal);
        rv = view.findViewById(R.id.cart_rv);
        rv.setLayoutManager(new LinearLayoutManager(activity));
        rv.setAdapter(new cartViewAdapter(activity, shopCartList));
    }

    private class cartViewAdapter extends RecyclerView.Adapter<cartViewAdapter.myViewHolder> {
        private LayoutInflater layoutInflater;
        List<ShopCart> shopCarts;

        public cartViewAdapter(Context context, List<ShopCart> shopCartList) {
            layoutInflater = LayoutInflater.from(context);
            shopCarts = shopCartList;

        }

        @Override
        public int getItemCount() {
            return shopCarts.size();
        }

        @NonNull
        @Override
        public cartViewAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.item_view_cart, parent, false);

            return new myViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull cartViewAdapter.myViewHolder holder, int index) {

            ShopCart shopCart =  shopCarts.get(index);
            holder.tvProName.setText(shopCart.getName());
            holder.tvProDesc.setText(shopCart.getDesc());




        }

        private class myViewHolder extends RecyclerView.ViewHolder {
            RadioButton rbSelect;

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
                rbSelect = itemView.findViewById(R.id.cart_rbSelect);
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
        shopCarts.add(new ShopCart("神奇寶貝球", "史上最厲害", 20, 200));
        shopCarts.add(new ShopCart("神奇寶貝球", "史上最厲害", 20, 200));
        shopCarts.add(new ShopCart("神奇寶貝球", "史上最厲害", 20, 200));
        shopCarts.add(new ShopCart("神奇寶貝球", "史上最厲害", 20, 200));
        shopCarts.add(new ShopCart("神奇寶貝球", "史上最厲害", 20, 200));

        return shopCarts;

    }

}
