package com.g2.runningFront.ShopActivity;


import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.g2.runningFront.R;


public class ProductFragment extends Fragment {
    private Activity activity;
    private TextView pro_desc,pro_name,pro_price;
    private ImageView pro_picture;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity=getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle((R.string.ProductIntroduction));
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pro_picture = view.findViewById(R.id.pro_picture);
        pro_desc = view.findViewById(R.id.pro_desc);
        pro_name = view.findViewById(R.id.pro_name);
        pro_price = view.findViewById(R.id.pro_price);

        Bundle bundle = getArguments();
        if (bundle != null) {
            Product product = (Product) bundle.getSerializable("product");
            if (product != null) {
                pro_picture.setImageResource(product.getImage());
                pro_price.setText(String.valueOf(product.getprice()));
                pro_name.setText(product.getname());
                pro_desc.setText(product.getdesc());

            }
        }

    }

}
