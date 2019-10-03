package com.g2.runningFront.ShopActivity;

public class Order {
    private String order_no;
    private String order_date;
    private String payment_methon;
    private String order_money;
    private String order_status;
    public Order(String order_no,String order_date,String payment_methon,String order_money,String order_status){
        this.order_no=order_no;
        this.order_date=order_date;
        this.payment_methon=payment_methon;
        this.order_money=order_money;
        this.order_status=order_status;


    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public String getOrder_date() {
        return order_date;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }

    public String getPayment_methon() {
        return payment_methon;
    }

    public void setPayment_methon(String payment_methon) {
        this.payment_methon = payment_methon;
    }

    public String getOrder_money() {
        return order_money;
    }

    public void setOrder_money(String order_money) {
        this.order_money = order_money;
    }

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }
}
