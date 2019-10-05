package com.g2.runningFront.ShopActivity.ShopCart;

public class CartOrder {

    int orderNo;
    int userNo;
    String Name;
    String Address;
    String Phone;
    int Payment;
    int ordStatus;
    int sumTotal;



    public CartOrder(int orderNo, int userNo, String name, String address, String phone, int payment, int ordStatus, int sumTotal) {
        this.orderNo = orderNo;
        this.userNo = userNo;
        Name = name;
        Address = address;
        Phone = phone;
        Payment = payment;
        this.ordStatus = ordStatus;
        this.sumTotal = sumTotal;
    }

    public CartOrder(int userNo, String name, String address, String phone, int payment, int ordStatus, int sumTotal) {
        this.userNo = userNo;
        Name = name;
        Address = address;
        Phone = phone;
        Payment = payment;
        this.ordStatus = ordStatus;
        this.sumTotal = sumTotal;
    }

    public int getSumTotal() {
        return sumTotal;
    }

    public void setSumTotal(int sumTotal) {
        this.sumTotal = sumTotal;
    }


    public int getUserNo() {
        return userNo;
    }

    public void setUserNo(int userNo) {
        this.userNo = userNo;
    }

    public int getOrdStatus() {
        return ordStatus;
    }

    public void setOrdStatus(int ordStatus) {
        this.ordStatus = ordStatus;
    }

    public String getPaymentText(){
        String paymentText=null;

        switch (Payment){
            case -1:
                paymentText = "未選擇";
                break;
            case 0:
                paymentText ="信用卡支付";
                break;
            case 1:
                paymentText = "行動支付";
                break;
        }

        return paymentText;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public int getPayment() {
        return Payment;
    }

    public void setPayment(int payment) {
        Payment = payment;
    }

    public int getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(int orderNo) {
        this.orderNo = orderNo;
    }
}
