package com.g2.runningFront.RunActivity.Run;


import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.g2.runningFront.Common.Common;
import com.g2.runningFront.Common.CommonTask;
import com.g2.runningFront.Common.TimestampTypeAdapter;
import com.g2.runningFront.R;
import com.g2.runningFront.RunActivity.MainActivity;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

// 取偏好設定，如果沒有就跳到最前面

public class RunMainFragment extends Fragment {
    private Activity activity;
    private Button btStartRun;
    TextView tvTime, tvCalorie, tvBMI;
    ImageView ivEditBMI;
    View view;

    UserBasic userBasic;

    List<Run> runList = new ArrayList<>();
    String formatTime;
    double wDistance = 0, wTime = 0, wCalorie = 0;

    CommonTask runTask;
    CommonTask uDataTask;
    private static final String url = Common.URL_SERVER + "RunServlet";

    SharedPreferences pref;
    private final static String PREFERENCES_NAME = "preference";

    PieChart pieChart;
    List<PieEntry> pieEntries = new ArrayList<>();

    DecimalFormat format = new DecimalFormat(("0.00"));

    int user_no;
    Gson gson;

    MainActivity mainActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        mainActivity = (MainActivity) getActivity();
        Common.signIn(activity);
        user_no = Common.getUserNo(activity);
        gson = Common.getTimeStampGson();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle("運動");
        return inflater.inflate(R.layout.fragment_run_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        mainActivity.btbRun.setVisibility(View.VISIBLE);
        pref = activity.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        runList = getRun();
        getUserBasic();
        if (runList != null&&userBasic!=null) {
            getWeekData();
            holdView();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        getUserBasic();
    }

    private void holdView() {

        btStartRun = view.findViewById(R.id.rm_btStartRun);
        btStartRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view)
                        .navigate(R.id.action_runMain_to_runStart);
            }
        });

        tvTime = view.findViewById(R.id.rm_tvTime);
        tvCalorie = view.findViewById(R.id.rm_tvCalorie);
        tvBMI = view.findViewById(R.id.rm_tvBMI);

        tvTime.setText(formatTime);
        tvCalorie.setText(format.format(wCalorie) + " 卡 ");
        tvBMI.setText(userBasic.getBMISuggest());

        ivEditBMI = view.findViewById(R.id.rm_ivEditBMI);
        ivEditBMI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_runMain_to_runInput);
            }
        });

        pieChart = view.findViewById(R.id.rm_pieChart);


        String sDistance;
        pieChart.setRotationEnabled(true);
        if (wDistance == 0) {
            sDistance = "本週尚未\n開始跑步";
        } else {
            sDistance = "跑步距離 \n" + format.format(wDistance / 1000) + " km ";
        }
        pieChart.setCenterText(sDistance);
        pieChart.setCenterTextSize(25);

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Run WeekData");
        pieDataSet.setValueTextSize(0);
        pieDataSet.setSliceSpace(2);

        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();

        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, Highlight highlight) {
                PieEntry pieEntry = (PieEntry) entry;
                String text = pieEntry.getLabel() + "\n" + format.format(pieEntry.getValue()) + " m";
                Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected() {

            }
        });
    }

    private void getWeekData() {

        wCalorie = 0;
        wDistance = 0;
        wTime = 0;

        for (Run run : runList) {
            wCalorie += run.getCalorie();
            wDistance += run.getDistance();
            wTime += run.getTime();
            float iDistance = (float) run.getDistance();
            pieEntries.add(new PieEntry(iDistance, Common.getWeekDay(run.getRun_date())));
        }
        int seconds = ((int) wTime) % 60;
        int minutes = (((int) wTime) / 60) % 60;
        int hours = ((int) wTime) / 3600;

        formatTime = hours + " 小時 , " + minutes + " 分鐘  " + seconds + " 秒  ";
    }

    private List<Run> getRun() {


        List<Run> runs = new ArrayList<>();

        if(Common.networkConnected(activity)){

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getWeekRunList");
            jsonObject.addProperty("userNo", user_no);
            // 之後要補足資料

            try {

                runTask = new CommonTask(url, jsonObject.toString());
                String runListStr = runTask.execute().get();
                Type listType = new TypeToken<List<Run>>() {
                }.getType();
                runs = gson.fromJson(runListStr, listType);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return runs;
    }

    private void getUserBasic() {

        // 先上網抓並存到偏好設定，再偵測如果偏好設定內沒有資料，就會跳到初始頁面

        if (Common.networkConnected(activity)) {
            try {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("action", "getUserBasic");
                jsonObject.addProperty("user_no", user_no);
                uDataTask = new CommonTask(url, jsonObject.toString());
                String ubStr = uDataTask.execute().get();
                Log.d("ubStr", ubStr);
                userBasic = new Gson().fromJson(ubStr, UserBasic.class);
                userBasic.setUser_no(user_no);
                pref.edit().putString("UserBasic", new Gson().toJson(userBasic)).apply();

                if (userBasic.getHeight() == 0 | userBasic.getWeight() == 0 |  userBasic.getAge() == 0) {
                    Navigation.findNavController(view).navigate(R.id.action_runMain_to_runInput);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            String ubPrefStr = pref.getString("UserBasic", "noData");
            if (ubPrefStr.equals("noData")) {
                Navigation.findNavController(view).navigate(R.id.action_runMain_to_runInput);
            } else {
                userBasic = new Gson().fromJson(ubPrefStr, UserBasic.class);
            }
        }
    }

}
