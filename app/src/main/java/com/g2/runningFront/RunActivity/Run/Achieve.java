package com.g2.runningFront.RunActivity.Run;


import android.widget.TextView;

import java.io.Serializable;

public class Achieve implements Serializable {
    private String achieve_value;
    private int achieve_no;
    private int user_no;
    private  int achieve_count;


    String tvachieve_value1,tvachieve_value2,tvachieve_value3,tvachieve_value4,
            tvachieve_value5,tvachieve_value6,tvachieve_value7,tvachieve_value8,tvachieve_value9;

    public Achieve(int user_no) {
        this.user_no = user_no;
    }


    public Achieve(String achieve_value,int achieve_no,int user_no) {
        this.achieve_value = achieve_value;
        this.achieve_no = achieve_no;
        this.user_no = user_no;
    }

    public int getAchieve_count() {
        return achieve_count;
    }

    public void setAchieve_count(int achieve_count) {
        this.achieve_count = achieve_count;
    }

    public String getAchieve_value() {
        return achieve_value;
    }

    public void setAchieve_value(String achieve_value) {
        this.achieve_value = achieve_value;
    }

    public int getAchieve_no() {
        return achieve_no;
    }

    public void setAchieve_no(int achieve_no) {
        this.achieve_no = achieve_no;
    }

    public int getUser_no() {
        return user_no;
    }

    public void setUser_no(int user_no) {
        this.user_no = user_no;
    }

    public void setTvachieve_value1(String tvachieve_value1) {
        this.tvachieve_value1 = tvachieve_value1;
    }

    public void setTvachieve_value2(String tvachieve_value2) {
        this.tvachieve_value2 = tvachieve_value2;
    }

    public void setTvachieve_value3(String tvachieve_value3) {
        this.tvachieve_value3 = tvachieve_value3;
    }

    public void setTvachieve_value4(String tvachieve_value4) {
        this.tvachieve_value4 = tvachieve_value4;
    }

    public void setTvachieve_value5(String tvachieve_value5) {
        this.tvachieve_value5 = tvachieve_value5;
    }

    public void setTvachieve_value6(String tvachieve_value6) {
        this.tvachieve_value6 = tvachieve_value6;
    }

    public void setTvachieve_value7(String tvachieve_value7) {
        this.tvachieve_value7 = tvachieve_value7;
    }

    public void setTvachieve_value8(String tvachieve_value8) {
        this.tvachieve_value8 = tvachieve_value8;
    }

    public void setTvachieve_value9(String tvachieve_value9) {
        this.tvachieve_value9 = tvachieve_value9;
    }

    public String getTvachieve_value1() {
        return tvachieve_value1;
    }

    public String getTvachieve_value2() {
        return tvachieve_value2;
    }

    public String getTvachieve_value3() {
        return tvachieve_value3;
    }

    public String getTvachieve_value4() {
        return tvachieve_value4;
    }

    public String getTvachieve_value5() {
        return tvachieve_value5;
    }

    public String getTvachieve_value6() {
        return tvachieve_value6;
    }

    public String getTvachieve_value7() {
        return tvachieve_value7;
    }

    public String getTvachieve_value8() {
        return tvachieve_value8;
    }

    public String getTvachieve_value9() {
        return tvachieve_value9;
    }

    public Achieve(String tvachieve_value1, String tvachieve_value2, String tvachieve_value3, String tvachieve_value4, String tvachieve_value5,
                   String tvachieve_value6, String tvachieve_value7, String tvachieve_value8, String tvachieve_value9) {
        this.tvachieve_value1 = tvachieve_value1;
        this.tvachieve_value2 = tvachieve_value2;
        this.tvachieve_value3 = tvachieve_value3;
        this.tvachieve_value4 = tvachieve_value4;
        this.tvachieve_value5 = tvachieve_value5;
        this.tvachieve_value6 = tvachieve_value6;
        this.tvachieve_value7 = tvachieve_value7;
        this.tvachieve_value8 = tvachieve_value8;
        this.tvachieve_value9 = tvachieve_value9;
    }







}
