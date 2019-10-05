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
    TextView textView;

    int sumTotal;



    private final static String DEFAULT_ERROR = "null";
    private final static String PREFERENCES_NAME = "preferences";

    private SharedPreferences pref;
    String orderReceiver;
    CartOrder co;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        bundle = getArguments();
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

        getPref();


        btConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sumTotal = bundle.getInt("SumTotal");

                receiverName = etReceiver.getText().toString();
                receiverAddress = etAddress.getText().toString();
                receiverPhone = etPhone.getText().toString();
                CartOrder or = new CartOrder(1,receiverName, receiverAddress, receiverPhone, receiverPayment,0,sumTotal);
                // 未來要補上USERNO

                pref.edit().putString("CartOrder", new Gson().toJson(or)).apply();

                if (receiverName.equals("") || receiverAddress.equals("") || receiverPhone.equals("")) {
                    Common.toastShow(activity, "Please Set Your Receiver ? ");
                } else {
                    switch (receiverPayment) {
                        case -1:
                            Common.toastShow(activity, "Please Choice How to Pay ? ");
                            break;
                        case 0:
                            Navigation.findNavController(view).navigate(R.id.action_shopCartFillFragment_to_shopCartCeditPayFragment);
                            break;
                        case 1:
                            Navigation.findNavController(view).navigate(R.id.action_shopCartFillFragment_to_shopCartAcpayFragment);
                            break;
                    }

                }
            }
        });

        checkToInt();

        rgPayment.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                checkToInt();
            }
        });

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
        orderReceiver = pref.getString("CartOrder", DEFAULT_ERROR);

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

    private void checkToInt(){
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


