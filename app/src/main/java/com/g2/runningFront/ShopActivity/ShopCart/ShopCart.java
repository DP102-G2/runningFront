package com.g2.runningFront.ShopActivity.ShopCart;

import java.io.Serializable;
import java.util.List;

public class ShopCart implements Serializable {

    String no;
    String name;
    String desc;
    int qty;
    int price;
    int stock;
    int total;

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
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

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public ShopCart(String no, String name, String desc, int qty, int price, int stock) {
        this.no = no;
        this.name = name;
        this.desc = desc;
        this.qty = qty;
        this.price = price;
        this.stock = stock;
    }

    public int getTotal() {
        total = qty * price;

        return total;
    }

    public String getDetail() {
        String detail = name + " x " + qty + " , ";

        return detail;
    }


}
