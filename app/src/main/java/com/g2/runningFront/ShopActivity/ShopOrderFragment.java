package com.g2.runningFront.ShopActivity;


import android.app.Activity;
import android.content.Context;
import android.media.tv.TvView;
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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.g2.runningFront.R;

import java.util.ArrayList;
import java.util.List;

public class ShopOrderFragment extends Fragment {
    private RecyclerView recyclerView;
    private Activity activity;
   private  int j;
    Button btoOrder_detail;
    List<Order> orderList = new ArrayList<>();

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
            LinearLayout list_Detail;


            TextView tvorderno, tvorderdate,tvpaymentmathon,tvordermoney,tvorderstatus,tvProduct,tvShop_quantity,tvProduct_price;
            public MyViewHolder(View itemView) {
                super(itemView);
               tvorderno = itemView.findViewById(R.id.tvorderno);
                tvorderdate= itemView.findViewById(R.id.tvorderdate);
               tvpaymentmathon = itemView.findViewById(R.id.tvpaymentmathon);
               tvordermoney = itemView.findViewById(R.id.tvordermoney);
              tvorderstatus = itemView.findViewById(R.id.tvorderstatus);
              btoOrder_detail=itemView.findViewById(R.id.btoOrder_detail);
              tvProduct=itemView.findViewById(R.id.tvProduct);
              tvShop_quantity=itemView.findViewById(R.id.tvShop_quantity);
              tvProduct_price=itemView.findViewById(R.id.tvProduct_price);
                list_Detail=itemView.findViewById(R.id.list_Detail);
              btoOrder_detail.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {
                      j++;
                      if(j%2>0){

                          list_Detail.setVisibility(View.VISIBLE);
                          tvProduct.setVisibility(View.VISIBLE);
                          tvShop_quantity.setVisibility(View.VISIBLE);
                         tvProduct_price.setVisibility(View.VISIBLE);


                      }
                      else {
                          list_Detail.setVisibility(View.VISIBLE);
                          tvProduct.setVisibility(View.GONE);
                        tvShop_quantity.setVisibility(View.GONE);
                          tvProduct_price.setVisibility(View.GONE);
                      }
                  }
              });




            }
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.item_view_order, viewGroup, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder viewHolder, int index) {
            final Order order = orders.get(index);

            viewHolder.tvorderno.setText("訂單標號:"+order.getOrder_no());
            viewHolder.tvorderdate.setText("訂單日期:"+order.getOrder_date());
            viewHolder.tvpaymentmathon.setText("付款方式:"+order.getPayment_methon());
            viewHolder.tvordermoney.setText("金額:"+order.getOrder_money());
            viewHolder.tvorderstatus.setText("訂單狀態:"+order.getOrder_status());
            viewHolder.tvProduct.setText(order.getP());
            viewHolder.tvShop_quantity.setText(order.getPp());
            viewHolder.tvProduct_price.setText(order.getPpp());

                   };
                }

    private List<Order> getorders() {
        List<Order> orders = new ArrayList<>();
        orders.add(new Order("11","111","111","Ivy", "091234342","1789","1123","090"));
        orders.add(new Order("11","111","111","Ivy", "091234342","1789","1123","090"));
        orders.add(new Order("11","111","111","Ivy", "091234342","1789","1123","090"));


        return orders;
    }
        }



