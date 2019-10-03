package com.g2.runningFront.ShopActivity.ShopCart;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.g2.runningFront.R;
import com.g2.runningFront.ShopActivity.Order;
import com.google.gson.Gson;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShopCartFillFragment extends Fragment {

    Activity activity;

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

    private final static String DEFAULT_ERROR = "null";
    private final static String PREFERENCES_NAME = "preferences";

    private SharedPreferences pref;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
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

        btConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                receiverName = etReceiver.getText().toString();
                receiverAddress = etAddress.getText().toString();
                receiverPhone = etPhone.getText().toString();

                OrderReceiver or = new OrderReceiver(receiverName, receiverAddress, receiverPhone);

                pref = activity.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
                pref.edit().putString("OrderReceiver", new Gson().toJson(or)).apply();


            }
        });


        cbReview.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {

                    String orderReceiver = pref.getString("OrderReceiver", DEFAULT_ERROR);

                    if (!orderReceiver.equals(DEFAULT_ERROR)) {
                        OrderReceiver or = new Gson().fromJson(orderReceiver, OrderReceiver.class);
                        etReceiver.setText(or.getName());
                        etAddress.setText(or.getAddress());
                        etPhone.setText(or.getPhone());
                    }

                } else if (!buttonView.isChecked()) {

                    etReceiver.setText("");
                    etAddress.setText("");
                    etPhone.setText("");
                }


            }
        });
    }
}


