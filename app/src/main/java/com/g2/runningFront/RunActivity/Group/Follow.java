package com.g2.runningFront.RunActivity.Group;

import java.io.Serializable;

public class Follow implements Serializable {
    private int no;
    private String name;
    private double distance;

    public Follow(){
        super();
    }

    public Follow(int no, String name, double distance) {
        this.no = no;
        this.name = name;
        this.distance = distance;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
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

}
