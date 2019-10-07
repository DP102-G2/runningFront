package com.g2.runningFront.ShopActivity;

import java.io.Serializable;
import java.sql.Timestamp;

public class Order implements Serializable {
    private int order_no;
    private  Timestamp order_date;
    private int payment_methon;
    private int order_money;
    private String order_status;
    private String product_no;
    private int qty;
    private int order_price;


    public Order(int order_no,  Timestamp order_date, int payment_methon, int order_money, String order_status, String product_no, int qty, int order_price) {
        this.order_no = order_no;
        this.order_date = order_date;
        this.payment_methon = payment_methon;
        this.order_money = order_money;
        this.order_status = order_status;
        this.product_no = product_no;
        this.qty = qty;
        this.order_price = order_price;


    }

    public int getOrder_no() {
        return order_no;
    }

    public void setOrder_no(int order_no) {
        this.order_no = order_no;
    }

    public  Timestamp getOrder_date() {
        return order_date;
    }

    public void setOrder_date( Timestamp order_date) {
        this.order_date = order_date;
    }

    public int getPayment_methon() {
        return payment_methon;
    }

    public void setPayment_methon(int payment_methon) {
        this.payment_methon = payment_methon;
    }

    public int getOrder_money() {
        return order_money;
    }

    public void setOrder_money(int order_money) {
        this.order_money = order_money;
    }

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }

    public String getProduct_no() {
        return product_no;
    }

    public void setProduct_no(String product_no) {
        this.product_no = product_no;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public int getOrder_price() {
        return order_price;
    }

    public void setOrder_price(int order_price) {
        this.order_price = order_price;
    }
}

