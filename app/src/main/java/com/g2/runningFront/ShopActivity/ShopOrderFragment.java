package com.g2.runningFront.ShopActivity;


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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;


import com.g2.runningFront.Common.Common;
import com.g2.runningFront.Common.CommonTask;
import com.g2.runningFront.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class ShopOrderFragment extends Fragment {
    private RecyclerView recyclerView;
    private Activity activity;
    private int j;
    private  int no;
    Button btoOrder_detail;
    List<Order> orders = new ArrayList<>();
    List<Order> orders_spinner = new ArrayList<>();
    List<Order> orders_change = new ArrayList<>();

    private CommonTask orderGetAllTask;
    private CommonTask spotDeleteTask;
    private static final String TAG = "TAG_OrdertListFragment";
    private Spinner spOrder;

    TextView tvorderstatus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();

        Common.signIn(activity);
        no=Common.getUserNo(activity);

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
        recyclerView = view.findViewById(R.id.reachieve);
        spOrder = view.findViewById(R.id.spOrder);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        /* 從偏好設定讀取登入狀態與否，並得到會員編號（用來查詢該會員的追蹤名單） */
        SharedPreferences pref = activity.getSharedPreferences(Common.PREF, MODE_PRIVATE);

        boolean isSignIn = pref.getBoolean("isSignIn", false);
        if (isSignIn) {
            /* 顯示使用者追蹤名單 */
            no = pref.getInt("user_no",0);
        } else {
            Log.d(TAG,"檢查未登入，不顯示追蹤名單。");
        }
        showAll();
        spcon();


    }


    private void showAll() {
        if (activity != null) {
            if (Common.networkConnected(activity)) {
                Timestamp t = new Timestamp(System.currentTimeMillis());
                String url = Common.URL_SERVER + "OrderServlet";
                try {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "getAll");
                    jsonObject.addProperty("user_no", no);
                    String jsonOut = jsonObject.toString();
                    orderGetAllTask = new CommonTask(url, jsonOut);
                    String jsonIn = orderGetAllTask.execute().get();
                    Log.d(TAG, jsonIn);
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    gsonBuilder.setDateFormat("yyyyMMddHHmmss");
                    gsonBuilder.registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter());
                    Gson gson = gsonBuilder.create();

                    Type listType = new TypeToken<List<Order>>() {
                    }.getType();
                    orders = gson.fromJson(jsonIn, listType);

                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
                if (orders == null || orders.isEmpty()) {
                    Common.toastShow(activity, R.string.textNoNewsFound);
                } else {
                    recyclerView.setAdapter(new OrderAdapter(activity, orders));
                }
            } else {
                Common.toastShow(activity, R.string.textNoNetwork);
            }
        }
    }
 //
    public void spcon() {
        spOrder = getView().findViewById(R.id.spOrder);

        spOrder.setSelection(0, true);
        spOrder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                orders_spinner.clear();
                switch (position) {
                    case 0:
                        orders_spinner.clear();
                        for (Order order : orders) {
                            {
                                orders_spinner.add(order);
                            }
                        }
 
                        OrderAdapter adapter = (OrderAdapter) recyclerView.getAdapter();
                        adapter.setOrders(orders_spinner);
                        adapter.notifyDataSetChanged();


                        break;
                    case 1:
                        orders_spinner.clear();
                        for (Order order : orders) {
                            if (order.getOrder_status().equals("1")) {
                                orders_spinner.add(order);
                            }
                        }
                        adapter = (OrderAdapter) recyclerView.getAdapter();
                        adapter.setOrders(orders_spinner);
                        adapter.notifyDataSetChanged();

                        break;
                    case 2:
                        orders_spinner.clear();
                        for (Order order : orders) {
                            if (order.getOrder_status().equals("2")) {
                                orders_spinner.add(order);

                            }
                        }
                        adapter = (OrderAdapter) recyclerView.getAdapter();
                        adapter.setOrders(orders_spinner);
                        adapter.notifyDataSetChanged();


                        break;
                    case 3:
                        orders_spinner.clear();
                        for (Order order : orders) {
                            if (order.getOrder_status().equals("3")) {
                                orders_spinner.add(order);

                            }
                        }
                        adapter = (OrderAdapter) recyclerView.getAdapter();
                        adapter.setOrders(orders_spinner);
                        adapter.notifyDataSetChanged();

                        break;
                    case 4:
                        orders_spinner.clear();
                        for (Order order : orders) {
                            if (order.getOrder_status().equals("0")) {
                                orders_spinner.add(order);

                            }
                        }
                        adapter = (OrderAdapter) recyclerView.getAdapter();
                        adapter.setOrders(orders_spinner);
                        adapter.notifyDataSetChanged();

                        break;


                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }


    private class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
        private LayoutInflater layoutInflater;
        //  Context context;
        private List<Order> orders;
      //  private boolean[] orderExpanded;

        public OrderAdapter(Context context, List<Order> orders) {
            layoutInflater = LayoutInflater.from(context);
            //  this.context = context;
            this.orders = orders;
        }

        public void setOrders(List<Order> orders) {
            this.orders = orders;

        }

        @Override
        public int getItemCount() {
            return orders.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
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
            viewHolder.tvpaymentmathon.setText("付款方式:" + order.getPaymentText());
            viewHolder.tvordermoney.setText("金額:" + order.getOrder_money() + " ");
            viewHolder.tvorderstatus.setText("訂單狀態:" + order.getorder_statustText());
            viewHolder.tvProduct.setText("商品編號:" +order.getProduct_no());
            viewHolder.tvShop_quantity.setText("商品數量:" +order.getQty() + " ");
            viewHolder.tvProduct_price.setText("訂單金額:" +order.getOrder_price() + " ");

        }


    }


}









