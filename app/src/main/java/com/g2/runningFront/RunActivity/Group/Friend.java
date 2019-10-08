package com.g2.runningFront.RunActivity.Group;

import java.io.Serializable;

public class Friend implements Serializable {
    private int imageId;
    private String name, phone, total_time, total_distance;
    private String speed, calories, love, join_time;

    public Friend(int imageId, String name, String phone, String total_time, String total_distance
            , String speed, String calories, String love, String join_time) {
        this.imageId = imageId;
        this.name = name;
        this.phone = phone;
        this.total_time = total_time;
        this.total_distance = total_distance;
        this.speed = speed;
        this.calories = calories;
        this.love = love;
        this.join_time = join_time;
    }

    public int getImageId() {
        return imageId;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getTotal_time() {
        return total_time;
    }

    public String getTotal_distance() {
        return total_distance;
    }

    public String getSpeed() {
        return speed;
    }

    public String getCalories() {
        return calories;
    }

    public String getLove() {
        return love;
    }

    public String getJoin_time() {
        return join_time;
    }

}