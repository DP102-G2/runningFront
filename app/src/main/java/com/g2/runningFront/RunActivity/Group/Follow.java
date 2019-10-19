package com.g2.runningFront.RunActivity.Group;

import java.io.Serializable;

public class Follow implements Serializable {
    private int no;
    private String id;
    private String name;
    private double distance;
    private boolean isLove;// 是否也被按了愛心

    public Follow(){
        super();
    }

    public Follow(int no, String id, String name, double distance, boolean isLove) {
        this.no = no;
        this.id = id;
        this.name = name;
        this.distance = distance;
        this.isLove = isLove;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public boolean getIsLove() {
        return isLove;
    }

    public void setIsLove(boolean isLove) {
        this.isLove = isLove;
    }
}
