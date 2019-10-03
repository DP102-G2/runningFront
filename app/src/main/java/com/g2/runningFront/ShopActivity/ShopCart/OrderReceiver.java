package com.g2.runningFront.ShopActivity.ShopCart;

public class OrderReceiver {

    String Name;
    String Address;
    String Phone;

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

    public OrderReceiver(String name, String address, String phone) {
        Name = name;
        Address = address;
        Phone = phone;
    }



}
