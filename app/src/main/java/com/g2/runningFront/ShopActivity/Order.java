package com.g2.runningFront.ShopActivity;

import com.google.gson.JsonObject;

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
    private  int user_no;
//    private  Order_Detail order_detail;
//
//    public String getOrder_detail() {
//        String name = order_detail.getName;
//
//
//
//        return text;
//    }

    public Order(int order_no, Timestamp order_date, int payment_methon, int order_money, String order_status, String product_no, int qty, int order_price, int user_no) {
        this.order_no = order_no;
        this.order_date = order_date;
        this.payment_methon = payment_methon;
        this.order_money = order_money;
        this.order_status = order_status;
        this.product_no = product_no;
        this.qty = qty;
        this.order_price = order_price;
        this.user_no=user_no;


    }

    public String getPaymentText(){
        String paymentText=null;
        switch (payment_methon){
            case 1:
                paymentText = "貨到付款";
                break;
            case 2:
                paymentText ="信用卡";
                break;
            case 3:
                paymentText = "行動支付";
                break;
        }

        return paymentText;
    }
    public String getorder_statustText(){
        String orderText=null;
        switch (order_status){
            case "1":
                orderText = "未出貨";
                break;
            case "2":
                orderText ="已出貨";
                break;
            case "3":
                orderText = "未送達";
                break;
            case "4":
                orderText = "已送達";
                break;
        }

        return orderText;
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

    public int getUser_no() {
        return user_no;
    }

    public void setUser_no(int user_no) {
        this.user_no = user_no;
    }

    public int getOrder_price() {
        return order_price;
    }

    public void setOrder_price(int order_price) {
        this.order_price = order_price;
    }
}

