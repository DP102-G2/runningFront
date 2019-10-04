package com.g2.runningFront.ShopActivity.ShopCart;

public class OrderReceiver {

    String Name;
    String Address;
    String Phone;
    int Payment;

    public OrderReceiver(String name, String address, String phone, int payment) {
        Name = name;
        Address = address;
        Phone = phone;
        Payment = payment;
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
}
