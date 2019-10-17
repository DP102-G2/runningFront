package com.g2.runningFront.RunActivity.Run;

import java.io.Serializable;

public class Achieve implements Serializable {

    private int achieve_no;
    private String achieve_name;
    private String achieve_value;
    private String unit;

    public Achieve(int achieve_no, String achieve_name, String achieve_value, String unit) {
        this.achieve_no = achieve_no;
        this.achieve_name = achieve_name;
        this.achieve_value = achieve_value;
        this.unit = unit;
    }



    public void setAchieve_no(int achieve_no) {
        this.achieve_no = achieve_no;
    }

    public void setAchieve_name(String achieve_name) {
        this.achieve_name = achieve_name;
    }

    public void setAchieve_value(String achieve_value) {
        this.achieve_value = achieve_value;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getAchieve_no() {
        return achieve_no;
    }

    public String getAchieve_name() {
        return achieve_name;
    }

    public String getAchieve_value() {
        return achieve_value;
    }

    public String getUnit() {
        return unit;
    }


}