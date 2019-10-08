package com.g2.runningFront.ShopActivity;

import java.io.Serializable;

public class Product implements Serializable {
    private int price;
    private String name;
    private String desc;
    private int image;

    public Product() {
    }

    public Product(int price, String name, String desc, int image) {
        this.price = price;
        this.name = name;
        this.desc = desc;
        this.image= image;
    }

    public  int getprice() {
        return price;
    }

    public  String getname() {
        return name;
    }

    public  String getdesc() { return desc; }

    public int getImage() {
        return image;
    }


    public void setprice(int price) { this.price = price; }

    public void setname(String name) { this.name = name; }

    public void setdesc(String desc) { this.desc = desc; }

    public void setImage(int image) { this.image = image; }
}
