package com.g2.runningFront.RunActivity.Group;



public class National {
    private int user_no;
    private String name;
    private double distance;
    private boolean isFollow;
    private String month;
    public National(int user_no, String name, double distance, boolean isFollow,String month) {
        super();
        this.user_no = user_no;
        this.name = name;
        this.distance = distance;
        this.isFollow = isFollow;
        this.month=month;
    }
    public int getUser_no() {
        return user_no;
    }
    public void setUser_no(int user_no) {
        this.user_no = user_no;
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
    public boolean isFollow() {
        return isFollow;
    }
    public void setFollow(boolean isFollow) {
        this.isFollow = isFollow;
    }
    public String getmonth() {
        return month;
    }
    public void setmonth(String month) {
        this.month = month;
    }


}
