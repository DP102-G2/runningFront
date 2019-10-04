package com.g2.runningFront.ShopActivity.ShopCart;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.g2.runningFront.Common.Common;
import com.g2.runningFront.Common.CommonTask;
import com.g2.runningFront.Common.ImageTask;
import com.g2.runningFront.R;
import com.g2.runningFront.ShopActivity.Order;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class ShopCartAcpayFragment extends Fragment {

    int sumToatal;
    OrderReceiver or;

    View view;
    RelativeLayout btGPay;
    Button btConfirm;
    TextView tvReceiver, tvAddress, tvPhone, tvPayment, tvSumTotal;
    RecyclerView rvList;
    List<ShopCart> shopCarts = new ArrayList<>();
    private SharedPreferences pref;
    Activity activity;

    ImageTask shopCartImageTask;
    private static final String url = Common.URL_SERVER + "ShopCartServlet";

    private final static String DEFAULT_ERROR = "null";
    private final static String PREFERENCES_NAME = "preferences";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shop_cart_acpay, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pref = activity.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        shopCarts = getShopCarts();
        this.view = view;
        holdView();
        setReceiver();
    }

    public void holdView() {

        btGPay = view.findViewById(R.id.AcPay_btGPay);
        tvReceiver = view.findViewById(R.id.AcPay_tvReceiver);
        tvAddress = view.findViewById(R.id.AcPay_tvAddress);
        tvPhone = view.findViewById(R.id.AcPay_tvPhone);
        tvPayment = view.findViewById(R.id.AcPay_tvPayment);
        tvSumTotal = view.findViewById(R.id.AcPay_tvSumTotal);
        rvList = view.findViewById(R.id.AcPay_rv);
        btConfirm = view.findViewById(R.id.AcPay_btConfirm);

        rvList.setLayoutManager(new LinearLayoutManager(activity));
        rvList.setAdapter(new myCartAdapter(activity, shopCarts));

    }

    public void setReceiver(){

        String listStr = pref.getString("OrderReceiver", "Error");
        sumToatal = pref.getInt("SumTotal",-1);

        if (!listStr.equals("Error")&&sumToatal!=-1) {
            or = new Gson().fromJson(listStr, OrderReceiver.class);
            tvReceiver.setText(or.getName());
            tvPayment.setText(or.getPaymentText());
            tvAddress.setText(or.getAddress());
            tvPhone.setText(or.getPhone());
            tvSumTotal.setText(String.valueOf(sumToatal));
        }
    }

    public List<ShopCart> getShopCarts() {

        List<ShopCart> shopCartList = new ArrayList<>();
        String listStr = pref.getString("ShopCartList", "Error");

        Type listTpye = new TypeToken<List<ShopCart>>() {
        }.getType();

        if (!listStr.equals("Error")) {
            shopCartList = new Gson().fromJson(listStr, listTpye);
        }

        return shopCartList;
    }

    private class myCartAdapter extends RecyclerView.Adapter<myCartAdapter.myCartViewHolder> {
        List<ShopCart> shopCartList;
        Context context;
        LayoutInflater layoutInflater;
        int imageSize=0;


        public myCartAdapter(Context context, List<ShopCart> shopCartList) {
            this.shopCartList = shopCartList;
            this.context = context;
            layoutInflater=LayoutInflater.from(context);
            imageSize= getResources().getDisplayMetrics().widthPixels /5;
        }

        @NonNull
        @Override
        public myCartAdapter.myCartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView =layoutInflater.inflate(R.layout.item_view_cartconfirm,parent,false);

            return new myCartViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull myCartAdapter.myCartViewHolder holder, int position) {
            ShopCart shopCart = shopCartList.get(position);
            holder.pro_name.setText(shopCart.getName());
            holder.pro_qty.setText(String.valueOf(shopCart.getQty()));
            holder.pro_sumTotal.setText(String.valueOf(shopCart.getTotal()));

            if (holder.pro_image != null) {
                shopCartImageTask = new ImageTask(url, shopCart.getNo(), imageSize, holder.pro_image);
                shopCartImageTask.execute();
            }

        }

        @Override
        public int getItemCount() {
            return shopCartList.size();
        }

        private class myCartViewHolder extends RecyclerView.ViewHolder {
            TextView pro_name,pro_qty,pro_sumTotal;
            ImageView pro_image;


            public myCartViewHolder(View itemView) {
                super(itemView);
                pro_image = itemView.findViewById(R.id.cartConfirm_Image);
                pro_name = itemView.findViewById(R.id.cartConfirm_tvName);
                pro_sumTotal = itemView.findViewById(R.id.cartConfirm_tvSumPrice);
                pro_qty = itemView.findViewById(R.id.cartConfirm_tvQty);
            }
        }
    }
}
