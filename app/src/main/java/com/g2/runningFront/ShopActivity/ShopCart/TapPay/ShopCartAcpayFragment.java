package com.g2.runningFront.ShopActivity.ShopCart.TapPay;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
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
import com.g2.runningFront.ShopActivity.ShopCart.CartOrder;
import com.g2.runningFront.ShopActivity.ShopCart.ShopCart;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.TransactionInfo;
import com.google.android.gms.wallet.WalletConstants;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import tech.cherri.tpdirect.api.TPDCard;
import tech.cherri.tpdirect.api.TPDCardInfo;
import tech.cherri.tpdirect.api.TPDConsumer;
import tech.cherri.tpdirect.api.TPDGooglePay;
import tech.cherri.tpdirect.api.TPDMerchant;
import tech.cherri.tpdirect.api.TPDServerType;
import tech.cherri.tpdirect.api.TPDSetup;
import tech.cherri.tpdirect.callback.TPDGooglePayListener;
import tech.cherri.tpdirect.callback.TPDTokenFailureCallback;
import tech.cherri.tpdirect.callback.TPDTokenSuccessCallback;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;
import static com.g2.runningFront.Common.Common.CARD_TYPES;


public class ShopCartAcpayFragment extends Fragment {

    // GPAY
    TPDGooglePay tpdGooglepay;
    public static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 101;
    // 判斷gpay的請求代碼，會轉換到MAINACTIVITY
    public static PaymentData paymentData = null;

    private static final String url = Common.URL_SERVER + "ShopCartServlet";
    private final static String PREFERENCES_NAME = "preferences";


    String receiverName, receiverPayment, receiverAddress, receiverPhone;
    int sumToatal;
    int orderNo;

    private SharedPreferences pref;
    // 載入偏好設定檔案及購物車清單

    List<ShopCart> shopCartList = new ArrayList<>();
    //訂單明細

    CartOrder cartOrder;
    //訂單


    // VIEW
    Activity activity;
    StringBuilder orderDetail = new StringBuilder();
    View view;
    RelativeLayout btGPay;
    public static Button btConfirm;
    TextView tvReceiver, tvAddress, tvPhone, tvPayment, tvSumTotal;
    RecyclerView rvList;

    ImageTask shopCartImageTask;
    CommonTask commonTask;

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
        this.view = view;

        pref = activity.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        // 抓取偏好設定

        shopCartList = getShopCartList();
        // 第一次抓取購物清單

        holdView();
        setTPDCard();
        // 設定TPAY的初始偏好設定
        setReceiver();
        // 根據得到的資料顯示訂購資料
        getOrderNo();
        // 第一次上傳至伺服器建構訂單資料，得到訂單編號(流水號)
        orderDetail = getOrderDetail();
        // 將資料存入訂單明細，才能訂單明細資料給TPAY存入
    }

    private StringBuilder getOrderDetail() {

        orderDetail.append("訂單編號：" + orderNo + ",");

        for (ShopCart shopCart : shopCartList) {
            orderDetail.append(shopCart.getDetail());
        }

        return orderDetail;
    }

    private void holdView() {

        btGPay = view.findViewById(R.id.AcPay_btGPay);
        tvReceiver = view.findViewById(R.id.AcPay_tvReceiver);
        tvAddress = view.findViewById(R.id.AcPay_tvAddress);
        tvPhone = view.findViewById(R.id.AcPay_tvPhone);
        tvPayment = view.findViewById(R.id.AcPay_tvPayment);
        tvSumTotal = view.findViewById(R.id.AcPay_tvSumTotal);
        rvList = view.findViewById(R.id.AcPay_rv);
        btConfirm = view.findViewById(R.id.AcPay_btConfirm);

        rvList.setLayoutManager(new LinearLayoutManager(activity));
        rvList.setAdapter(new myCartAdapter(activity, shopCartList));

        /**
         *
         * 這邊抓不到
         * 1. 找專門針對FRAGMENT的api
         * 2. 抓FRAGMENT的實體在存入的資料
         * 3. 存入偏好設定
         * 4. STATIC耗費記憶體資源，離開時要記得清除
         *
         */


        btGPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳出user資訊視窗讓user確認，確認後會呼叫onActivityResult()
                tpdGooglepay.requestPayment(TransactionInfo.newBuilder()
                        //這裡是用到GOOGLE WALLET的api之常數，因此要新增GRADLE
                        .setTotalPriceStatus(WalletConstants.TOTAL_PRICE_STATUS_FINAL)
                        // 消費總金額
                        .setTotalPrice(String.valueOf(sumToatal))
                        // 設定幣別
                        .setCurrencyCode("TWD")
                        .build(), LOAD_PAYMENT_DATA_REQUEST_CODE);
                //我們預設的值，代碼就是101

            }
        });

        btConfirm.setVisibility(View.GONE);
        btConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendTapPay();

            }
        });

    }

    // 拿偏好設定檔裡的訂單資料
    public void setReceiver() {

        String listStr = pref.getString("CartOrder", "Error");

        if (!listStr.equals("Error") && sumToatal != -1) {

            cartOrder = new Gson().fromJson(listStr, CartOrder.class);

            receiverName = cartOrder.getName();
            receiverPayment = cartOrder.getPaymentText();
            receiverAddress = cartOrder.getAddress();
            receiverPhone = cartOrder.getPhone();
            sumToatal = cartOrder.getSumTotal();

            tvReceiver.setText("收件人： " + receiverName);
            tvPayment.setText("付款方式： " + receiverPayment);
            tvAddress.setText("收件地址： " + receiverAddress);
            tvPhone.setText("聯絡電話： " + cartOrder.getPhone());
            tvSumTotal.setText("訂單總金額： " + sumToatal);
        }
    }

    // 到偏好設定檔裡拿購物車清單
    public List<ShopCart> getShopCartList() {

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
        int imageSize = 0;

        public myCartAdapter(Context context, List<ShopCart> shopCartList) {
            this.shopCartList = shopCartList;
            this.context = context;
            layoutInflater = LayoutInflater.from(context);
            imageSize = getResources().getDisplayMetrics().widthPixels / 5;
        }

        @NonNull
        @Override
        public myCartAdapter.myCartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.item_view_cartconfirm, parent, false);

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
            TextView pro_name, pro_qty, pro_sumTotal;
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

    // TAPPAY的api
    private void setTPDCard() {

        TPDSetup.initInstance(activity.getApplicationContext(),//CONTEXT物件都可以擺放，如果是MAINACTIVITY的話放THIS也可以
                Integer.parseInt(getString(R.string.TapPay_AppID)),
                // 抓取APPID
                getString(R.string.TapPay_AppKey),
                // 放APPKEY
                TPDServerType.Sandbox);
        // PRODUTION是正式區，SANDBOX是測試區的意思
        // 正式區的開通方式可以到tapPay了解

        tech.cherri.tpdirect.api.TPDCard.CardType[] allowedNetworks
                = new TPDCard.CardType[]{tech.cherri.tpdirect.api.TPDCard.CardType.Visa, tech.cherri.tpdirect.api.TPDCard.CardType.MasterCard};
        //設定卡片類型

        //TPD商家
        TPDMerchant tpdMerchant = new TPDMerchant();
        //允許連線工作
        tpdMerchant.setSupportedNetworks(CARD_TYPES);
        tpdMerchant.setSupportedNetworks(allowedNetworks);
        tpdMerchant.setMerchantName(getString(R.string.TapPay_MerchantID));

        //下面這些全部都要設定
        TPDConsumer tpdConsumer = new TPDConsumer();

        //真正要做的事情，把商家跟顧客都安排好
        tpdGooglepay = new TPDGooglePay(activity, tpdMerchant, tpdConsumer);
        tpdGooglepay.isGooglePayAvailable(new TPDGooglePayListener() {
            @Override
            public void onReadyToPayChecked(boolean isReadyToPay, String s) {
                Common.toastShow(activity, "Success Ready to Pay!!");

            }
        });

    }

    //按下結帳就會送至TAPAY伺服器
    private void sendTapPay() {
        tpdGooglepay.getPrime(paymentData, new TPDTokenSuccessCallback() {
            @Override
            public void onSuccess(String prime, TPDCardInfo tpdCardInfo) {
                //PRIME類似通行證
                TapPayApiUtil tapPayApiUtil
                        = new TapPayApiUtil(sumToatal, orderNo, orderDetail.toString(), receiverName, receiverPhone);
                tapPayApiUtil.generatePayByPrimeCURLForSanBox
                        (prime, getString(R.string.TapPay_PartnerKey), getString(R.string.TapPay_MerchantID));
                orderComplete();
            }
        }, new TPDTokenFailureCallback() {
            @Override
            public void onFailure(int i, String s) {
                Log.d(TAG, s);

                Common.toastShow(activity, "請先點選'Buy With GPay'輸入付款資料");
            }
        });

    }


    // 完成訂單時要做的事情，並把訂單狀態改為已完成付款狀態
    private void orderComplete() {
        cartOrder.setOrdStatus(1);
        cartOrder.setOrderNo(orderNo);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "OrderComplete");
        jsonObject.addProperty("CartOrder", new Gson().toJson(cartOrder));
        commonTask = new CommonTask(url, jsonObject.toString());

        Log.d(TAG, jsonObject.toString());

        if (Common.networkConnected(activity)) {
            try {
                String Complete = commonTask.execute().get();
                Common.toastShow(activity, "訂購成功");
                Navigation.findNavController(view).navigate(R.id.action_shopCartAcpayFragment_to_shopMainFragment);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    // 將目前資料上傳新增訂單，但預設訂單狀態為尚未完成付款
    private void getOrderNo() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "insertOrder");
        jsonObject.addProperty("CartOrder", new Gson().toJson(cartOrder));
        jsonObject.addProperty("ShopCart", new Gson().toJson(shopCartList));
        commonTask = new CommonTask(url, jsonObject.toString());

        if (Common.networkConnected(activity)) {
            Log.d(TAG, jsonObject.toString());
            if (Common.networkConnected(activity)) {

                try {
                    String getOrderNo = commonTask.execute().get();
                    Log.d(TAG, getOrderNo);

                    orderNo = Integer.parseInt(getOrderNo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

