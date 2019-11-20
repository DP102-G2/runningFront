package com.g2.runningFront.ShopActivity;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.g2.runningFront.Common.AdimageaTask;
import com.g2.runningFront.Common.Common;
import com.g2.runningFront.Common.CommonTask;
import com.g2.runningFront.Common.ImageTask;
import com.g2.runningFront.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.security.PublicKey;
import java.util.List;

import static com.g2.runningFront.Common.Common.downSize;
import static com.g2.runningFront.Common.Common.round;
import static com.g2.runningFront.Common.Common.showToast;




public class ProductFragment extends Fragment {
    private final static String TAG = "TAG_ProductFragment";
    private Activity activity;
    private TextView pro_desc, pro_name, pro_price, productdescription;
    private ImageView imageView, imageView1, imageView2, imageView3;
    private ImageTask productGetimageTask;
    private AdimageaTask adGetimageTask;
    private Bitmap bitmap1, bitmap2, bitmap3;
    private int imageSize;
    private Button btproductcart;
    private CommonTask getadImageTask;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
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
        imageView = view.findViewById(R.id.pro_image);
        pro_desc = view.findViewById(R.id.pro_desc);
        pro_name = view.findViewById(R.id.pro_name);
        pro_price = view.findViewById(R.id.pro_price);
        imageView1 = view.findViewById(R.id.pro_image1);
        imageView2 = view.findViewById(R.id.pro_image2);
        imageView3 = view.findViewById(R.id.pro_image3);
        productdescription = view.findViewById(R.id.tvproductdescription);
        btproductcart = view.findViewById(R.id.btproductcart);

        Bundle bundle = getArguments();
        if (bundle != null) {
            Product product = (Product) bundle.getSerializable("product");
            Adimage adimage = (Adimage) bundle.getSerializable("adimage");
            Commodity commodity = (Commodity) bundle.getSerializable("commodity");


            if (product != null) {
                pro_price.setText(String.valueOf(product.getPro_price()));
                pro_name.setText(product.getPro_name());
                pro_desc.setText(product.getPro_desc());
                productdescription.setText(product.getPro_info());

                if (Common.networkConnected(activity)) {
                    String url = Common.URL_SERVER + "adproductshowServlet";
                    String pro_no = product.getPro_no();
                    productGetimageTask = new ImageTask(url, pro_no, imageSize, imageView);
                    productGetimageTask.execute();

                }
            } else if (adimage != null) {
                pro_price.setText(String.valueOf(adimage.getPro_price()));
                pro_name.setText(adimage.getPro_name());
                pro_desc.setText(adimage.getPro_desc());
                productdescription.setText(adimage.getPro_info());

                if (Common.networkConnected(activity)) {
                    String url = Common.URL_SERVER + "adproductServlet";
                    String pro_no = adimage.getPro_no();
                    adGetimageTask = new AdimageaTask(url, pro_no, imageSize, imageView);
                    adGetimageTask.execute();
                }
            }else if (commodity != null) {
                pro_price.setText(String.valueOf(commodity.getPro_price()));
                pro_name.setText(commodity.getPro_name());
                pro_desc.setText(commodity.getPro_desc());
                productdescription.setText(commodity.getPro_info());

                if (Common.networkConnected(activity)) {
                    String url = Common.URL_SERVER + "adproductServlet";
                    String pro_no = commodity.getPro_no();
                    adGetimageTask = new AdimageaTask(url, pro_no, imageSize, imageView);
                    adGetimageTask.execute();
                }
            }


        }

        //加入購物車
        btproductcart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.signIn(activity);
                if (Common.networkConnected(activity)) {

                    Bundle bundle = getArguments();
                    Product product = (Product) bundle.getSerializable("product");
                    Adimage adimage = (Adimage) bundle.getSerializable("adimage");
                    Commodity commodity = (Commodity) bundle.getSerializable("commodity");

                    String url = Common.URL_SERVER + "adproductshowServlet";
                    int user_no;
                    user_no = Common.getUserNo(activity);

                    String pro_no = null;
                    if (product != null) {
                        pro_no = product.getPro_no();
                    } else if (adimage != null) {
                        pro_no = adimage.getPro_no();
                    }else if (commodity != null) {
                        pro_no = commodity.getPro_no();
                    }

                    Product productcart = new Product(user_no, pro_no, 1);
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "adproductshowInsert");
                    jsonObject.addProperty("adproductshow", new Gson().toJson(productcart));
                    int count = 0;
                    try {
                        String result = new CommonTask(url, jsonObject.toString()).execute().get();
                        count = Integer.valueOf(result);
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                    if (count == 0) {
                        Common.showToast(activity, R.string.textUpdateFail);
                    } else {
                        Common.showToast(activity, R.string.textUpdateSuccess);
                    }
                } else {
                    Common.showToast(activity, R.string.textNoNetwork);
                }
            }
        });

        //抓取3張小圖
        Product product = (Product) bundle.getSerializable("product");
        Adimage adimage = (Adimage) bundle.getSerializable("adimage");
        Commodity commodity = (Commodity) bundle.getSerializable("commodity");


        String pro_no = null;
        if (product != null) {
            pro_no = product.getPro_no();
        } else if (adimage != null) {
            pro_no = adimage.getPro_no();
        }else if (commodity != null) {
            pro_no = commodity.getPro_no();
        }

        if (Common.networkConnected(activity)) {
            String url = Common.URL_SERVER + "adproductshowServlet";


            JsonObject ad_image1 = new JsonObject();
            ad_image1.addProperty("action", "getadImage1");
            ad_image1.addProperty("pro_no", pro_no);
            CommonTask imageTask1 = new CommonTask(url, ad_image1.toString());
            bitmap1 = null;
            try {
                /* 用 Base64 解碼 Servlet 端 編碼而成的文字變成 byte[]
                 * 再用 BitmapFactory 把 byte[] 換成 bitmap 以供 UI 元件貼圖 */
                byte[] image = Base64.decode(imageTask1.execute().get(), Base64.DEFAULT);
                bitmap1 = BitmapFactory.decodeByteArray(image, 0, image.length);

            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (bitmap1 != null) {
                imageView1.setImageBitmap(downSize(bitmap1, 100));
            } else {
                bitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.pro_image);
                imageView1.setImageBitmap(downSize(bitmap1, 100));
            }

            imageView1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imageView.setImageBitmap(downSize(bitmap1, 300));
                }
            });

            JsonObject ad_image2 = new JsonObject();
            ad_image2.addProperty("action", "getadImage2");
            ad_image2.addProperty("pro_no", pro_no);
            CommonTask imageTask2 = new CommonTask(url, ad_image2.toString());
            bitmap2 = null;
            try {
                /* 用 Base64 解碼 Servlet 端 編碼而成的文字變成 byte[]
                 * 再用 BitmapFactory 把 byte[] 換成 bitmap 以供 UI 元件貼圖 */
                byte[] image = Base64.decode(imageTask2.execute().get(), Base64.DEFAULT);
                bitmap2 = BitmapFactory.decodeByteArray(image, 0, image.length);

            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (bitmap2 != null) {
                imageView2.setImageBitmap(downSize(bitmap2, 100));
            } else {
                bitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.pro_image);
                imageView2.setImageBitmap(downSize(bitmap2, 100));
            }

            imageView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imageView.setImageBitmap(downSize(bitmap2, 300));
                }
            });

            JsonObject ad_image3 = new JsonObject();
            ad_image3.addProperty("action", "getadImage3");
            ad_image3.addProperty("pro_no", pro_no);
            CommonTask imageTask3 = new CommonTask(url, ad_image3.toString());
            bitmap3 = null;
            try {
                /* 用 Base64 解碼 Servlet 端 編碼而成的文字變成 byte[]
                 * 再用 BitmapFactory 把 byte[] 換成 bitmap 以供 UI 元件貼圖 */
                byte[] image = Base64.decode(imageTask3.execute().get(), Base64.DEFAULT);
                bitmap3 = BitmapFactory.decodeByteArray(image, 0, image.length);

            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (bitmap3 != null) {
                imageView3.setImageBitmap(downSize(bitmap3, 100));
            } else {
                bitmap3 = BitmapFactory.decodeResource(getResources(), R.drawable.pro_image);
                imageView3.setImageBitmap(downSize(bitmap3, 100));
            }
            imageView3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imageView.setImageBitmap(downSize(bitmap3, 300));
                }
            });
        }


    }


    @Override
    public void onStop() {
        super.onStop();
        if (productGetimageTask != null) {
            productGetimageTask.cancel(true);
            productGetimageTask = null;
        }
        if (adGetimageTask != null) {
            adGetimageTask.cancel(true);
            adGetimageTask = null;
        }

    }

}




