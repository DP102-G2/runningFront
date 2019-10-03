package com.g2.runningFront.ShopActivity;


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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.g2.runningFront.R;

import java.util.ArrayList;
import java.util.List;

public class ShopOrderFragment extends Fragment {
    private RecyclerView recyclerView;
    private Activity activity;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    activity=getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shop_order, container, false);



    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView=view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        List<Order> orders = getorders();
        recyclerView.setAdapter(new FriendAdapter(activity, orders));
    }

    private class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.MyViewHolder> {
        Context context;
        List<Order> orders;
        public FriendAdapter(Context context, List<Order> orders) {
            this.context = context;
            this.orders = orders;
        }

        @Override
        public int getItemCount() {
            return orders.size();
        }

        private class MyViewHolder extends RecyclerView.ViewHolder {

            TextView tvorderno, tvorderdate,tvpaymentmathon,tvordermoney,tvorderstatus;
            public MyViewHolder(View itemView) {
                super(itemView);
               tvorderno = itemView.findViewById(R.id.tvorderno);
                tvorderdate= itemView.findViewById(R.id.tvorderdate);
               tvpaymentmathon = itemView.findViewById(R.id.tvpaymentmathon);
               tvordermoney = itemView.findViewById(R.id.tvordermoney);
              tvorderstatus = itemView.findViewById(R.id.tvorderstatus);
            }
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.item_view_order, viewGroup, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder viewHolder, int index) {
            final Order order = orders.get(index);
            viewHolder.tvorderno.setText("訂單標號:"+order.getOrder_no());
            viewHolder.tvorderdate.setText("訂單日期:"+order.getOrder_date());
            viewHolder.tvpaymentmathon.setText("付款方式:"+order.getPayment_methon());
            viewHolder.tvordermoney.setText("金額:"+order.getOrder_money());
            viewHolder.tvorderstatus.setText("訂單狀態:"+order.getOrder_status());

                }
            }
    private List<Order> getorders() {
        List<Order> orders = new ArrayList<>();
        orders.add(new Order("11","111","111","Ivy", "091234342"));
        orders.add(new Order("11","111","111","Ivy", "091234342"));
        orders.add(new Order("11","111","111","Ivy", "091234342"));
        orders.add(new Order("11","111","111","Ivy", "091234342"));

        return orders;
    }
        }



