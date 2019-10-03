package com.g2.runningFront.ShopActivity.ShopCart;

import java.io.Serializable;
import java.util.List;

public class ShopCart implements Serializable {

    String name;
    String desc;
    int qty;
    int price;
    int stock;
    int total;



    public ShopCart(String name, String desc, int qty, int price, int stock) {
        this.name = name;
        this.desc = desc;
        this.qty = qty;
        this.price = price;
        this.stock = stock;
    }

    public int getTotal(){
        total=qty*price;

        return total;
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

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }


    public void setTotal(int total) {
        this.total = total;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }




}
