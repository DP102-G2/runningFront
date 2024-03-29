package com.g2.runningFront.ShopActivity.ShopCart;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.g2.runningFront.Common.Common;
import com.g2.runningFront.R;
import com.google.gson.Gson;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShopCartFillFragment extends Fragment {

    Activity activity;
    Bundle bundle;

    // 控制VIEW
    View view;
    EditText etReceiver;
    EditText etAddress;
    EditText etPhone;
    RadioGroup rgPayment;
    Button btConfirm;
    CheckBox cbReview;

    String receiverName;
    String receiverAddress;
    String receiverPhone;
    int receiverPayment = -1;
    // 預設一開始的選擇是空值，0等於信用卡、1等於行動支付
    TextView textView;

    int sumTotal;
    int user_no;

    private final static String DEFAULT_ERROR = "null";
    private final static String PREFERENCES_NAME = "preference";

    private SharedPreferences pref;
    String orderReceiver;
    CartOrder co;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        bundle = getArguments();
        user_no = Common.getUserNo(activity);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shop_cart_fill, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity.setTitle("填寫購物資訊");

        pref = activity.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        this.view = view;
        holdView();
    }

    public void holdView() {
        etReceiver = view.findViewById(R.id.cartfill_etReceiver);
        etAddress = view.findViewById(R.id.cartfill_etAddress);
        etPhone = view.findViewById(R.id.cartfill_etPhone);
        btConfirm = view.findViewById(R.id.cartfill_btConfirm);
        rgPayment = view.findViewById(R.id.cartfill_rgPayment);
        cbReview = view.findViewById(R.id.cartfill_cb);
        textView = view.findViewById(R.id.cartfill_tvReceiver);

        orderReceiver = pref.getString("CartOrder", DEFAULT_ERROR);
        getPref();
        // 抓取偏好設定裡的購物車

        btConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sumTotal = bundle.getInt("SumTotal");
                // 抓取總值

                receiverName = etReceiver.getText().toString().trim();
                receiverAddress = etAddress.getText().toString().trim();
                receiverPhone = etPhone.getText().toString().trim();

                if (receiverName.equals("") || receiverAddress.equals("") || receiverPhone.equals("")) {
                    Common.toastShow(activity, "請填寫你的收件人資料");
                    return;
                }
                if (!Common.isCellPhoneNo(receiverPhone)) {
                    Common.toastShow(activity, "電話格式不正確");
                    return;
                }

                CartOrder or = new CartOrder(user_no, receiverName, receiverAddress, receiverPhone, receiverPayment, 0, sumTotal);
                // 訂單狀態先預設為0，代表尚未付款，等到下一頁要上傳取訂單編號用
                // 未來要補上USERNO
                pref.edit().putString("CartOrder", new Gson().toJson(or)).apply();
                // 將得到的訂單資料傳入下一頁

                switch (receiverPayment) {
                    case -1:
                        Common.toastShow(activity, "請選擇您的支付方式");
                        break;
                    case 0:
                        Navigation.findNavController(view).navigate(R.id.action_shopCartFillFragment_to_shopCartCeditPayFragment);
                        break;
                    case 1:
                        Navigation.findNavController(view).navigate(R.id.action_shopCartFillFragment_to_shopCartAcpayFragment);
                        break;


                }
            }
        });

        checkToInt();
        // 將選擇支付方式的結果，轉乘iNT才能放入物件

        rgPayment.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                checkToInt();
            }
        });

        // 是否選取上次偏好設定檔案
        cbReview.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    getPref();
                } else {
                    etReceiver.setText("");
                    etAddress.setText("");
                    etPhone.setText("");
                    rgPayment.clearCheck();
                }
            }
        });

    }

    private void getPref() {
        // 如果偏好設定檔案裡面有值，就根據內容顯示到頁面

        if (!orderReceiver.equals(DEFAULT_ERROR)) {
            co = new Gson().fromJson(orderReceiver, CartOrder.class);
            if (!co.Name.equals("")) {
                cbReview.setChecked(true);
                etReceiver.setText(co.getName());
                etAddress.setText(co.getAddress());
                etPhone.setText(co.getPhone());
                switch (co.getPayment()) {
                    case -1:
                        rgPayment.clearCheck();
                        break;
                    case 0:
                        rgPayment.check(R.id.cartfill_rbCredit);
                        break;
                    case 1:
                        rgPayment.check(R.id.cartfill_rbAcPay);
                        break;
                }
            }
        }
    }

    private void checkToInt() {
        //將被選擇的結果轉換成數字
        switch (rgPayment.getCheckedRadioButtonId()) {
            case (R.id.cartfill_rbCredit):
                receiverPayment = 0;
                break;
            case (R.id.cartfill_rbAcPay):
                receiverPayment = 1;
                break;
            default:
                receiverPayment = -1;
                break;
        }
    }

}


