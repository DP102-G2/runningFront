package com.g2.runningFront.ShopActivity.ShopCart;

public class ShopCart {

    String name;
    String desc;
    int num;
    int price;

    public ShopCart(String name, String desc, int num, int price) {
        this.name = name;
        this.desc = desc;
        this.num = num;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
