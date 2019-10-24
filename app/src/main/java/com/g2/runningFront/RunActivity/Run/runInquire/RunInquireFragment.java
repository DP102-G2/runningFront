package com.g2.runningFront.RunActivity.Run.runInquire;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.g2.runningFront.Common.Common;
import com.g2.runningFront.Common.CommonTask;
import com.g2.runningFront.Common.ImageTask;
import com.g2.runningFront.Common.TimestampTypeAdapter;
import com.g2.runningFront.R;
import com.g2.runningFront.RunActivity.Run.Run;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class RunInquireFragment extends Fragment
        implements DatePickerDialog.OnDateSetListener {


    int dayTime = 3600 * 1000 * 24;
    private Timestamp startDate, endDate;

    private List<Run> runList = new ArrayList<>();
    // 資料庫取出的List

    private List<Run> dpRunList = new ArrayList<>();
    // 實際顯示的List

    private Activity activity;
    private View view;
    private RecyclerView rvRunList;
    private EditText etInputTime;

    CommonTask getRunListTask;
    ImageTask routeImageTask;
    private static final String url = Common.URL_SERVER + "RunServlet";
    private static int year, month, day;


    PieChart pieChart;
    List<PieEntry> pieEntryList = new ArrayList<>();
    DecimalFormat valueFormat = new DecimalFormat(("0.00"));
    double sumDistance = 0;
    String distanceStr;

    int user_no;
    Gson gson;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        setHasOptionsMenu(true);
        Common.signIn(activity);
        user_no = Common.getUserNo(activity);
        gson = Common.getTimeStampGson();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_run_inquire, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        runList = getRunList();
        holdView();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void holdView() {

        etInputTime = view.findViewById(R.id.inquire_etInputTime);
        rvRunList = view.findViewById(R.id.inquire_rv);
        rvRunList.setLayoutManager(new LinearLayoutManager(activity));
        rvRunList.setAdapter(new RunAdapter(activity, dpRunList));

        /* 設定圓餅圖 */
        pieChart = view.findViewById(R.id.inquire_pieChart);
        pieChart.setRotationEnabled(true);
        // 是否選轉
        pieChart.getLegend().setEnabled(false);
        // 是否顯示下方資料來源

        String pieChartStr;
        if (pieEntryList.isEmpty()) {
            pieChartStr = "目前尚無跑步資訊";
        } else {
            pieChartStr = "跑步距離 \n" + distanceStr + " km ";

        }

        // 設定中間圓心的文字
        pieChart.setCenterText(pieChartStr);
        pieChart.setCenterTextSize(25);

        // 設定圓餅圖的文字
        PieDataSet pieDataSet = new PieDataSet(pieEntryList, "Run WeekData");
        pieDataSet.setValueTextSize(0);
        pieDataSet.setSliceSpace(2);
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        // 圓餅圖顯示的顏色

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
        pieChart.setDrawSliceText(false);


        if (runList.size()!=0) {

            etInputTime.setEnabled(true);

            /* 關閉鍵盤模式，並使用onTouch設定事件直接顯示選擇時間畫面 */
            etInputTime.setShowSoftInputOnFocus(false);
            etInputTime.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        DatePickerDialog datePicker = new DatePickerDialog(
                                activity, RunInquireFragment.this,
                                RunInquireFragment.year, RunInquireFragment.month, RunInquireFragment.day);

                        // 設定可以選擇的最大/最小時間
                        datePicker.getDatePicker().setMaxDate(runList.get(runList.size() - 1).getRun_date().getTime());
                        datePicker.getDatePicker().setMinDate(runList.get(0).getRun_date().getTime());
                        datePicker.show();
                    }
                    return false;
                }
            });


            /* 根據擷取到的列表， */
            updateDisplay(new Timestamp(runList.get(runList.size() - 1).getRun_date().getTime() - dayTime * 6), runList.get(runList.size() - 1).getRun_date());


            // 圓餅圖被點選時顯示"時間＋距離"
            pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                @Override
                public void onValueSelected(Entry entry, Highlight highlight) {
                    PieEntry pieEntry = (PieEntry) entry;
                    String text = pieEntry.getLabel() + "\n" + valueFormat.format(pieEntry.getValue()) + " m";
                    Common.toastShow(activity,text);
                }

                @Override
                public void onNothingSelected() {

                }
            });
        }
    }


    /* 當DatePicker被選擇時自動啟動 */
    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {

        //存入預設的時間，由於為static因此需使用fragment
        RunInquireFragment.year = year;
        RunInquireFragment.month = month;
        RunInquireFragment.day = day;

        // 針對目前的時間，可以算出起始與七天後的時間
        Calendar calendar = new GregorianCalendar(year, month, day);
        startDate = new Timestamp(calendar.getTimeInMillis());

        if (runList.get(runList.size() - 1).getRun_date().getTime() - startDate.getTime() < dayTime * 6) {

            endDate = new Timestamp(runList.get(runList.size() - 1).getRun_date().getTime());

        } else {
            endDate = new Timestamp(startDate.getTime() + dayTime * 6);
        }

        updateDisplay(startDate, endDate);
    }

    /* 根據輸入的起始與結束時間，計算出需顯示的資訊(距離、列表) */
    private void updateDisplay(Timestamp startDate, Timestamp endDate) {

        /* 重置所有數值 */
        sumDistance = 0;
        dpRunList.clear();
        pieEntryList.clear();

        /* 判斷輸入的開始/結束時間，抓出符合的列表 */
        for (Run run : runList) {
            if (startDate.getTime() <= run.getRun_date().getTime() & run.getRun_date().getTime() <= endDate.getTime()) {
                dpRunList.add(run);
                sumDistance += run.getDistance();
                float iDistance = (float) run.getDistance();
                pieEntryList.add(new PieEntry(iDistance, Common.getDay(run.getRun_date())));
                // 圓餅圖專用的List
            }
        }

        distanceStr = valueFormat.format(sumDistance / 1000);

        /* 更新RecyclerView */

        RunAdapter runAdapter = (RunAdapter) rvRunList.getAdapter();
        runAdapter.setRuns(dpRunList);
        runAdapter.notifyDataSetChanged();

        /* 更新圓餅圖 */

        if (pieChart != null) {
            pieChart.notifyDataSetChanged();
            pieChart.invalidate();
            pieChart.setCenterText("跑步距離 \n" + distanceStr + " km ");
        }


//      /* 判斷時間會不會超過 */



        /* 更新EditText */

        String startTime = String.format("20%1$ty-%1$tm-%1$td", startDate);
        String endTime = String.format("20%1$ty-%1$tm-%1$td", endDate);

        StringBuilder set = new StringBuilder();
        set.append(startTime + "   ~   " + endTime);

        etInputTime.setText(set);
    }


    private class RunAdapter extends RecyclerView.Adapter<RunAdapter.InquireHolder> {

        Context context;
        List<Run> runs;
        LayoutInflater layoutInflater;


        public RunAdapter(Context context, List<Run> runs) {
            this.runs = runs;
            this.context = context;
            layoutInflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public RunAdapter.InquireHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.item_view_runinquire, parent, false);

            return new InquireHolder(itemView);
        }

        @Override
        public int getItemCount() {
            return runs.size();
        }

        public void setRuns(List<Run> runs) {
            this.runs = runs;
        }


        private class InquireHolder extends RecyclerView.ViewHolder {
            ImageView ivImage;
            TextView tvDate, tvDistance, tvTime, tvCalorie;

            public InquireHolder(View itemView) {
                super(itemView);
                ivImage = itemView.findViewById(R.id.iqItem_image);
                tvDate = itemView.findViewById(R.id.iqItem_tvDate);
                tvDistance = itemView.findViewById(R.id.iqItem_tvDistance);
                tvTime = itemView.findViewById(R.id.iqItem_tvTime);
                tvCalorie = itemView.findViewById(R.id.iqItem_tvCalorie);
            }


        }

        @Override
        public void onBindViewHolder(@NonNull RunAdapter.InquireHolder holder, int position) {

            final Run run = runs.get(position);

            holder.tvDate.setText("運動日期： " + String.format("%1$tm 月 %1$td 日", run.getRun_date()));
            holder.tvCalorie.setText("消耗卡路里： " + run.getCalorie()+"卡");
            holder.tvDistance.setText("跑步距離： " + run.getDistance()+"公尺");
            holder.tvTime.setText("累計時間： " + Common.secondToString((int)run.getTime()));

            /* 根據UserNo跟RunNo擷取圖片 */
            routeImageTask = new ImageTask(url, holder.ivImage, run.getUserNo(), run.getRunNo());
            routeImageTask.execute();
//
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("Run", run);
                    Navigation.findNavController(view).navigate(R.id.action_runInquireFragment_to_runDetailFragment, bundle);
                }
            });
        }

    }


    /**
     * 根據UserNo抓取對應資料，之後要補抓取UserNo
     */
    private List<Run> getRunList() {
        List<Run> runs = new ArrayList<>();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "getRunList");
        jsonObject.addProperty("userNo", user_no);
        jsonObject.addProperty("startDate", gson.toJson(startDate));

        if (Common.networkConnected(activity)) {

            try {
                getRunListTask = new CommonTask(url, jsonObject.toString());
                String runListStr = getRunListTask.execute().get();
                Log.d("TAG", runListStr);

                Type listTpye = new TypeToken<List<Run>>() {
                }.getType();

                runs = gson.fromJson(runListStr, listTpye);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return runs;

    }

}
