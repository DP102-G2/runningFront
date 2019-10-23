package com.g2.runningFront.RunActivity.Group;

import java.io.Serializable;
import java.text.DecimalFormat;

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
        // 因為是資料庫的數據是公尺，需要 ÷ 1000
        // 除以 1000 以後的小數點，改成只顯示小數點後2位
        DecimalFormat df = new DecimalFormat("##.00");
        String distanceStr = df.format(distance/1000);
        return Double.parseDouble(distanceStr);
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
