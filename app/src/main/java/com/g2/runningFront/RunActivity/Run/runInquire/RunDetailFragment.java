package com.g2.runningFront.RunActivity.Run.runInquire;


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

import com.g2.runningFront.Common.Common;
import com.g2.runningFront.Common.ImageTask;
import com.g2.runningFront.R;
import com.g2.runningFront.RunActivity.Run.Run;


/**
 * A simple {@link Fragment} subclass.
 */
public class RunDetailFragment extends Fragment {

    Activity activity;
    Bundle bundle;
    ImageView ivRoute;
    TextView tvDate,tvDistance,tvTime,tvSpeed,tvCalorie;
    View view;

    ImageTask routeImageTask;
    private static final String url = Common.URL_SERVER + "RunServlet";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle=getArguments();
        activity=getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_run_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        holdView();

    }

    private void holdView(){

        ivRoute = view.findViewById(R.id.rd_ivImage);
        tvDate = view.findViewById(R.id.rd_tvDate);
        tvCalorie = view.findViewById(R.id.rd_tvCalroie);
        tvDistance = view.findViewById(R.id.rd_tvDistance);
        tvTime = view.findViewById(R.id.rd_tvTime);
        tvSpeed = view.findViewById(R.id.rd_tvSpeed);

        Run run = (Run) bundle.getSerializable("Run");

        tvDate.setText("運動日期： " + String.format("%1$tm 月 %1$td 日", run.getRun_date()));
        tvCalorie.setText("消耗卡路里： " + String.valueOf(run.getCalorie()));
        tvDistance.setText("跑步距離： " + String.valueOf(run.getDistance()));
        tvTime.setText("累計時間： " + String.valueOf(run.getTime()));
        tvSpeed.setText("跑步時速： " + String.valueOf(run.getSpeed()));
        getImage(ivRoute,run);

    }


    private void getImage(ImageView imageView,Run run){

        routeImageTask = new ImageTask(url, run.getUserNo(),run.getRunNo() , imageView);
        routeImageTask.execute();


    }
}
