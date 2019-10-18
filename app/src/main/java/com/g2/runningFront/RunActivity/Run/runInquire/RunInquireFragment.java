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


    int dayTime = 3600*1000*24;


    Activity activity;
    EditText etInputTime;
    View view;
    private static int year, month, day;

    Timestamp startDate,endDate;
    List<Run> runList = new ArrayList<>();
    List<Run> dpRunList = new ArrayList<>();
    RecyclerView rv;


    CommonTask getRunListTask;
    ImageTask routeImageTask;
    private static final String url = Common.URL_SERVER + "RunServlet";


    PieChart pieChart;
    List<PieEntry> pieEntryList = new ArrayList<>();
    DecimalFormat format = new DecimalFormat(("0.00"));
    double wDistance = 0;
    String distanceStr ;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
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
        Date date = new Date();
        startDate = new Timestamp(date.getTime());
        endDate = startDate;
        runList = getRunList();
        if (runList != null) {
            holdView();
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    private void holdView() {
        etInputTime = view.findViewById(R.id.inquire_etInputTime);
        etInputTime.setText(String.format("%1$ty-%1$tm-%1$td",runList.get(0).getRun_date()));
        etInputTime.setShowSoftInputOnFocus(false);

        etInputTime.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    DatePickerDialog datePicker = new DatePickerDialog(
                            activity, RunInquireFragment.this, // 間聽器
                            RunInquireFragment.year, RunInquireFragment.month, RunInquireFragment.day);
                    //預選日期，抓取時間;

                    datePicker.getDatePicker().setMaxDate(runList.get(runList.size()-1).getRun_date().getTime());
                    // 限定最多查到當天

                    datePicker.getDatePicker().setMinDate(runList.get(0).getRun_date().getTime());
                    // 限定開始時間，這要查

                    datePicker.show();
                }
                return false;

            }
        });

        rv = view.findViewById(R.id.inquire_rv);

        rv.setLayoutManager(new LinearLayoutManager(activity));
        rv.setAdapter(new RunAdapter(activity, dpRunList));

        updateDisplay(runList.get(0).getRun_date(),runList.get(runList.size()-1).getRun_date());

        pieChart = view.findViewById(R.id.inquire_pieChart);
        pieChart.setRotationEnabled(true);
        pieChart.setCenterText("跑步距離 \n" + distanceStr + " km ");
        pieChart.setCenterTextSize(25);

        PieDataSet pieDataSet = new PieDataSet(pieEntryList, "Run WeekData");
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


    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        RunInquireFragment.year = year; //存入預設的時間
        RunInquireFragment.month = month;
        RunInquireFragment.day = day;

        Calendar calendar = new GregorianCalendar(year, month, day);
        startDate = new Timestamp(calendar.getTimeInMillis());
        endDate = new Timestamp(startDate.getTime()+dayTime*7);

        updateDisplay(startDate,endDate);
    }

    private void updateDisplay(Timestamp startDate,Timestamp endDate) {

        wDistance=0;
        dpRunList.clear();
        pieEntryList.clear();

        for(Run run :runList){
            if(startDate.getTime()<=run.getRun_date().getTime()&run.getRun_date().getTime()<endDate.getTime()){
                dpRunList.add(run);
                wDistance+=run.getDistance();
                float iDistance = (float) run.getDistance();
                pieEntryList.add(new PieEntry(iDistance, Common.getWeekDay(run.getRun_date())));
            }
        }

        distanceStr = format.format(wDistance / 1000);

        RunAdapter runAdapter = (RunAdapter) rv.getAdapter();
        runAdapter.setRuns(dpRunList);
        runAdapter.notifyDataSetChanged();

        if(pieChart!=null) {
            pieChart.notifyDataSetChanged();
            pieChart.invalidate();
            pieChart.setCenterText("跑步距離 \n" + distanceStr + " km ");
        }
        String startTime = String.format("20%1$ty-%1$tm-%1$td",startDate);
        String endTime = String.format("20%1$ty-%1$tm-%1$td",endDate);


        StringBuilder set = new StringBuilder();
        set.append(startTime+"   ~   "+endTime);

        etInputTime.setText(set);
    }

    /**
     * 從這邊開始寫
     */

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
            holder.tvCalorie.setText("消耗卡路里： " + String.valueOf(run.getCalorie()));
            holder.tvDistance.setText("跑步距離： " + String.valueOf(run.getDistance()));
            holder.tvTime.setText("累計時間： " + String.valueOf(run.getTime()));

            routeImageTask = new ImageTask(url, run.getUserNo(),run.getRunNo() , holder.ivImage);
            routeImageTask.execute();

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("Run",run);
                    Navigation.findNavController(view).navigate(R.id.action_runInquireFragment_to_FriendFragment,bundle);

                }
            });
        }

    }


    private List<Run> getRunList() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("yyyyMMddhhmmss");
        gsonBuilder.registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter());
        Gson gson = gsonBuilder.create();

        List<Run> runs = new ArrayList<>();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "getRunList");
        jsonObject.addProperty("userNo", 1);
        jsonObject.addProperty("startDate", gson.toJson(startDate));

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

        return runs;

    }

}
