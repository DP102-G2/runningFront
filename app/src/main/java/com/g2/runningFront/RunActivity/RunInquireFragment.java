package com.g2.runningFront.RunActivity;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.g2.runningFront.Common.Common;
import com.g2.runningFront.R;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;


/**
 * A simple {@link Fragment} subclass.
 */
public class RunInquireFragment extends Fragment
        implements DatePickerDialog.OnDateSetListener {

    Activity activity;
    EditText etInputTime;
    View view;
    private static int year, month, day;

    Date mDate;
    Timestamp timestamp;


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
        holdView();

    }

    @SuppressLint("ClickableViewAccessibility")
    private void holdView() {
        etInputTime = view.findViewById(R.id.inquire_etInputTime);
        etInputTime.setShowSoftInputOnFocus(false);


        etInputTime.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    DatePickerDialog datePicker = new DatePickerDialog(
                            activity, RunInquireFragment.this, // 間聽器
                            RunInquireFragment.year, RunInquireFragment.month, RunInquireFragment.day);
                    //預選日期，抓取時間;

                    Date date = new Date();
                    Long DateLong = date.getTime();
                    datePicker.getDatePicker().setMaxDate(DateLong);
                    datePicker.getDatePicker().setMinDate(DateLong - (1000 * 3600 * 24 * 7));
                    datePicker.show();
                }
                return false;

            }
    });

}


    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        RunInquireFragment.year = year; //存入預設的時間
        RunInquireFragment.month = month;
        RunInquireFragment.day = day;
        updateDisplay();

        Calendar calendar = new GregorianCalendar(year,month,day);
        timestamp = new Timestamp(calendar.getTimeInMillis());
        Log.d()


//        mDate = new Date(year+month+day);
//        Common.toastShow(activity,mDate.toString());
    }

    private void updateDisplay() {
        etInputTime.setText(new StringBuilder().append(year).append("-")
                .append(pad(month + 1)).append("-").append(pad(day)));
    }

    private String pad(int number) {
        if (number >= 10) {
            return String.valueOf(number);
        } else {
            return "0" + number;
        }
    }
}
