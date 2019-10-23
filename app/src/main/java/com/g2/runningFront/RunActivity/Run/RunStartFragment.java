package com.g2.runningFront.RunActivity.Run;


import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.g2.runningFront.Common.Common;
import com.g2.runningFront.Common.CommonTask;
import com.g2.runningFront.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class RunStartFragment extends Fragment {


    private static final int PER_ACCESS_LOCATION = 0; //索引值

    private View view;
    private Activity activity;
    private Button btStart, btComplete;
    private TextView tvTime, tvDistance, tvCalories, tvSpeed;

    private Timer timer;
    private double calorie, speed, distance, time;
    Run run;
    boolean runStates;

    Bitmap routeBitmap;
    byte[] route;

    CommonTask commonTask;
    private static final String url = Common.URL_SERVER + "RunServlet";


    private GoogleMap map;
    // 實際執行元件

    private MapView mapView;
    // 承載畫面的元件

    private FusedLocationProviderClient fusedLocationClient;
    // 與GOOGLEMAP建立連線的元件

    private LocationCallback locationCallback;
    // 承載傳回資料的元件

    private Location lastLoaction;
    // 記錄連現實的最後一個位置

    private List<LatLng> lastLocationList  = new ArrayList<>();

    private Polyline polyline;
    private PolylineOptions polylineOptions;

    GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
        @Override
        public void onSnapshotReady(Bitmap bp) {
            if (bp != null) {
                routeBitmap = bp;
            }
        }

    };

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
        askAccessLoactionPermission();

        // 建立連線，並取得網端儲存的最後一個位置
        if (fusedLocationClient == null) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
            /* 如果使用者同意了取得定位的請求，則加上監聽器 */
            if (ActivityCompat.checkSelfPermission(activity,
                    Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) {
                /* 加上監聽器 */
                fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        /* 取得手機最後的位置 */
                        lastLoaction = location;
                        moveMap(new LatLng(lastLoaction.getLatitude(), lastLoaction.getLongitude()));
                    }
                });
            }
        }


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();


        locationCallback = new LocationCallback() {
            /* 一旦定位更新，就會呼叫 onLocationResult() */
            @Override
            public void onLocationResult(LocationResult locationResult) {
                lastLoaction = locationResult.getLastLocation();
                LatLng latLng = new LatLng(lastLoaction.getLatitude(), lastLoaction.getLongitude());

                if (runStates) {
                    lastLocationList.add(latLng);
                    drawMap(latLng);
                    getRunValue();
                }

                moveMap(latLng);
                map.snapshot(callback, routeBitmap);
            }
        };

        polylineOptions = new PolylineOptions().width(15).color(Color.BLUE);
        //設定線的顏色，屆時應該可根據時速變換顏色
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle("開始運動");
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_run_start, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;

        // 創建MAPVIEW
        mapView = view.findViewById(R.id.runstart_mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                showMyLocation();
            }
        });

        holdView();


    }

    @Override
    public void onResume() {
        super.onResume();

        if (ActivityCompat
                .checkSelfPermission
                        (activity, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            LocationRequest locationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(10000);
            // 偵測是否有改變位置
            // 不要這麼高頻率，調低，30秒或一分鐘

            fusedLocationClient
                    .requestLocationUpdates(locationRequest, locationCallback, null);
            // 如果獲取當下的位置發現改變位置很大，就執行locationCallback，來比對嚕
        }

    }

    @Override
    public void onPause() {
        super.onPause();

        if (fusedLocationClient != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    void holdView() {
        tvTime = view.findViewById(R.id.runstart_tvTime);
        tvCalories = view.findViewById(R.id.runstart_tvCalories);
        tvDistance = view.findViewById(R.id.runstart_tvDistance);
        tvSpeed = view.findViewById(R.id.runstart_tvSpeed);

        tvTime.setText("00:00:00");
        tvCalories.setText("0");
        tvDistance.setText("0.00");
        tvSpeed.setText("0.0");

        btStart = view.findViewById(R.id.runstart_btStart);
        btStart.setVisibility(View.VISIBLE);
        btStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runStart();
            }
        });

        btComplete = view.findViewById(R.id.runstart_btComplete);
        btComplete.setVisibility(View.GONE);
        btComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runComplete();
            }
        });

    }

    private void askAccessLoactionPermission() {

        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION
        };

        int result = ContextCompat.checkSelfPermission(activity, permissions[0]);
        if (result == PackageManager.PERMISSION_DENIED) {
            requestPermissions(permissions, PER_ACCESS_LOCATION);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        showMyLocation();
    }

    private void showMyLocation() {
        if (ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            // 如果同意資源的話，就讓別人擷取地點
            map.setMyLocationEnabled(true);
        }
    }

    // 計算距離
    private void getRunValue() {

        //當超過兩個點時，才開始計算距離
        if (lastLocationList.size() > 2) {
            float[] distanceResult = new float[1];
            int index = lastLocationList.size() - 1;
            LatLng latLng1 = lastLocationList.get(index);
            LatLng latLng2 = lastLocationList.get(index - 1);

            Location.distanceBetween(latLng1.longitude, latLng1.latitude, latLng2.longitude, latLng2.latitude, distanceResult);
            distance += (distanceResult[0]);

            DecimalFormat format = new DecimalFormat(("0.000"));
            String sDistance = format.format(distance/1000);

            tvDistance.setText(sDistance);

            speed = distance / time * 3600 / 1000;
            String sSpeed = format.format(speed);
            tvSpeed.setText(sSpeed);

            // 400 米 跑幾分鐘？
            // 跑步熱量（kcal）＝體重（kg）× (time/3600) × 指數 K
            // 指數 K＝30÷ 速度（分鐘 / 400 米）
            // K = 30 / ((time/60) / (distance/400))
            // 一公尺跑幾分＊400等於400公尺跑幾分
            calorie = 70 * (time / 3600) * 30 / ((time) / (distance) * 400 / 60);
            String sCalorie = format.format(calorie);
            tvCalories.setText(sCalorie);
        }
    }

    private void moveMap(LatLng latLng) {

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)
                .zoom(16)
                .build();
        CameraUpdate cameraUpdate =null;

        if (lastLocationList.size()<=5){
            cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        }
        else if (lastLocationList.size() > 5) {

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (LatLng marker : lastLocationList) {
                builder.include(new LatLng(marker.latitude,marker.longitude));
            }
            LatLngBounds bounds = builder.build();

            cameraUpdate = CameraUpdateFactory
                    .newLatLngBounds(bounds,100);
        }

        map.animateCamera(cameraUpdate);


    }

    private void drawMap(LatLng latLng) {

        if (polyline == null) {
            polyline = map.addPolyline(polylineOptions.add(latLng));
        }

        polyline.setPoints(lastLocationList);
    }

    private void runStart() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                time += 1;//累積的總秒數
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int seconds = ((int) time) % 60;
                        int minutes = (((int) time) / 60) % 60;
                        int hours = ((int) time) / 3600;

                        tvTime.setText(hours + ":" + minutes + ":" +seconds);
                    }
                });
            }
        }, 1000, 1000);

        runStates = true;
        btComplete.setVisibility(View.VISIBLE);
        btStart.setVisibility(View.GONE);
    }

    private void runComplete() {
        timer.cancel();
        timer = null;

        if(time<5){
            Common.toastShow(activity,"跑步時間過短，無存取資料");
        }else {
//
            runStates = false;
            run = new Run(1, time,
                    Double.valueOf(tvDistance.getText().toString())*1000,
                    Double.valueOf(tvCalories.getText().toString()),
                    Double.valueOf(tvSpeed.getText().toString()));

            if (routeBitmap != null) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                routeBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                route = out.toByteArray();
            }

            if (Common.networkConnected(activity)) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("action", "InsertRun");
                jsonObject.addProperty("Run", new Gson().toJson(run));

                if (route != null) {
                    jsonObject.addProperty("imageBase64", Base64.encodeToString(route, Base64.DEFAULT));
                }

                try {
                    commonTask = new CommonTask(url, jsonObject.toString());
                    String result = commonTask.execute().get();

                    if (result.equals("1"))
                        Common.toastShow(activity, "完成跑步");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }
        Navigation.findNavController(view).navigate(R.id.action_runStart_to_runMain);

    }
}
