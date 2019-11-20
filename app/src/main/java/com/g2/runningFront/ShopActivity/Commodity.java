package com.g2.runningFront.ShopActivity;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Commodity implements Serializable, CharSequence {
    private int pro_price;
    private String pro_name;
    private String pro_desc;
    private int pro_image;
    private String pro_no;
    private String pro_info;
    private int user_no;
    private int qty;

    public Commodity() {
    }

    public void setFields(String pro_no, String pro_name, int pro_stock) {
        this.pro_no = pro_no;
        this.pro_name = pro_name;
        //       this.pro_stock = pro_stock;
    }


    public Commodity(int pro_price, String pro_name, String pro_desc, int image, String pro_no, String pro_info) {
        this.pro_price = pro_price;
        this.pro_name = pro_name;
        this.pro_desc = pro_desc;
        this.pro_image = image;
        this.pro_no = pro_no;
        this.pro_info = pro_info;

    }


    public Commodity(int user_no, String pro_no, int qty) {
        this.user_no = user_no;
        this.pro_no = pro_no;
        this.qty = qty;

    }

    public int getPro_price() {
        return pro_price;
    }

    public String getPro_name() {
        return pro_name;
    }

    public String getPro_desc() {
        return pro_desc;
    }

    public int getPro_image() {
        return pro_image;
    }

    public String getPro_no() {
        return pro_no;
    }

    public String getPro_info() {
        return pro_info;
    }

    public int getUser_no() { return user_no; }

    public int getQty() { return qty; }


    public void setPro_price(int pro_price) {
        this.pro_price = pro_price;
    }

    public void setPro_name(String pro_name) {
        this.pro_name = pro_name;
    }

    public void setPro_desc(String pro_desc) {
        this.pro_desc = pro_desc;
    }

    public void setPro_image(int pro_image) {
        this.pro_image = pro_image;
    }

    public void setPro_no(String pro_no) {
        this.pro_no = pro_no;
    }

    public void setPro_info(String pro_info) {
        this.pro_no = pro_info;
    }

    public void setQty(int qty) { this.qty = qty; }

    public void setUser_no(int user_no) { this.user_no = user_no; }


    @Override
    public int length() {
        return 0;
    }

    @Override
    public char charAt(int index) {
        return 0;
    }

    @NonNull
    @Override
    public CharSequence subSequence(int start, int end) {
        return null;
    }
}
