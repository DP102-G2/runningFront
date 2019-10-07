package com.g2.runningFront.ShopActivity;


import android.app.Activity;
import android.content.Context;
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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.g2.runningFront.Common.Common;
import com.g2.runningFront.Common.CommonTask;
import com.g2.runningFront.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class ShopOrderFragment extends Fragment {
    private RecyclerView recyclerView;
    private Activity activity;
    private int j;
    Button btoOrder_detail;
    List<Order> orders;
    private CommonTask orderGetAllTask;
    private CommonTask spotDeleteTask;
    private static final String TAG = "TAG_OrdertListFragment";
    private Spinner spOrder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
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
        recyclerView = view.findViewById(R.id.recyclerView);
        spOrder=view.findViewById(R.id.spOrder);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        showAll();

    }

    private void showAll() {
        if (activity != null) {
            if (Common.networkConnected(activity)) {
                Timestamp t = new Timestamp(System.currentTimeMillis());
                String url = Common.URL_SERVER + "OrderServlet";
                List<Order> order = null;
                try {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "getAll");
                    String jsonOut = jsonObject.toString();
                    orderGetAllTask = new CommonTask(url, jsonOut);
                    String jsonIn = orderGetAllTask.execute().get();
                    Common.toastShow(activity,jsonIn);
                    Log.d(TAG, jsonIn);
//                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd hh:mm:ss.S").create();
                    Gson gson = new GsonBuilder()
                            .registerTypeAdapter(Timestamp.class,new TimestampTypeAdapter()).setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                    // timestamp 解碼？

                    Type listType = new TypeToken<List<Order>>() {
                    }.getType();
                    order = gson.fromJson(jsonIn, listType);

                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
                if (order == null || order.isEmpty()) {
                    Common.toastShow(activity, R.string.textNoNewsFound);
                } else {
                    Common.toastShow(activity, "Complete");
                    recyclerView.setAdapter(new OrderAdapter(activity, order));
                }
            } else {
                Common.toastShow(activity, R.string.textNoNetwork);
            }
        }
    }


    private class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
        private LayoutInflater layoutInflater;
        //  Context context;
        private List<Order> orders;
        private boolean[] orderExpanded;

        public OrderAdapter(Context context, List<Order> orders) {
            layoutInflater = LayoutInflater.from(context);
            //  this.context = context;
            this.orders = orders;
        }

        @Override
        public int getItemCount() {
            return orders.size();
        }

        private class ViewHolder extends RecyclerView.ViewHolder {
            LinearLayout list_Detail;
            TextView tvorderno, tvorderdate, tvpaymentmathon, tvordermoney, tvorderstatus, tvProduct, tvShop_quantity, tvProduct_price;


            public ViewHolder(View itemView) {
                super(itemView);
                tvorderno = itemView.findViewById(R.id.tvorderno);
                tvorderdate = itemView.findViewById(R.id.tvorderdate);
                tvpaymentmathon = itemView.findViewById(R.id.tvpaymentmathon);
                tvordermoney = itemView.findViewById(R.id.tvordermoney);
                tvorderstatus = itemView.findViewById(R.id.tvorderstatus);
                btoOrder_detail = itemView.findViewById(R.id.btoOrder_detail);
                tvProduct = itemView.findViewById(R.id.tvProduct);
                tvShop_quantity = itemView.findViewById(R.id.tvShop_quantity);
                tvProduct_price = itemView.findViewById(R.id.tvProduct_price);
                list_Detail = itemView.findViewById(R.id.list_Detail);


                btoOrder_detail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        j++;
                        if (j % 2 > 0) {

                            list_Detail.setVisibility(View.VISIBLE);
                            tvProduct.setVisibility(View.VISIBLE);
                            tvShop_quantity.setVisibility(View.VISIBLE);
                            tvProduct_price.setVisibility(View.VISIBLE);


                        } else {
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
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View itemView = layoutInflater.inflate(R.layout.item_view_order, viewGroup, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {
            final Order order = orders.get(position);

            viewHolder.tvorderno.setText("訂單標號:" + order.getOrder_no());
            viewHolder.tvorderdate.setText("訂單日期:" + order.getOrder_date());
            viewHolder.tvpaymentmathon.setText("付款方式:" + order.getPayment_methon());
            viewHolder.tvordermoney.setText("金額:" + order.getOrder_money());
            viewHolder.tvorderstatus.setText("訂單狀態:" + order.getOrder_status());
            viewHolder.tvProduct.setText(order.getProduct_no());
            viewHolder.tvShop_quantity.setText(order.getQty());
            viewHolder.tvProduct_price.setText(order.getOrder_price());

        }



        }


    }









