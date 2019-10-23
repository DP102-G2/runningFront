package com.g2.runningFront.ShopActivity;

import java.io.Serializable;

public class Adimage implements Serializable {
    private int pro_price;
    private String pro_name;
    private String pro_desc;
    private int pro_image;
    private String pro_no;
    private String pro_info;

    public Adimage(int pro_price, String pro_name, String pro_desc, int pro_image, String pro_no, String pro_info) {
        this.pro_price = pro_price;
        this.pro_name = pro_name;
        this.pro_desc = pro_desc;
        this.pro_image = pro_image;
        this.pro_no = pro_no;
        this.pro_info = pro_info;
    }

    public int getPro_price() {
        return pro_price;
    }

    public void setPro_price(int pro_price) {
        this.pro_price = pro_price;
    }

    public String getPro_name() {
        return pro_name;
    }

    public void setPro_name(String pro_name) {
        this.pro_name = pro_name;
    }

    public String getPro_desc() {
        return pro_desc;
    }

    public void setPro_desc(String pro_desc) {
        this.pro_desc = pro_desc;
    }

    public int getPro_image() {
        return pro_image;
    }

    public void setPro_image(int pro_image) {
        this.pro_image = pro_image;
    }

    public String getPro_no() {
        return pro_no;
    }

    public void setPro_no(String pro_no) {
        this.pro_no = pro_no;
    }

    public String getPro_info() {
        return pro_info;
    }

    public void setPro_info(String pro_info) {
        this.pro_info = pro_info;
    }
}