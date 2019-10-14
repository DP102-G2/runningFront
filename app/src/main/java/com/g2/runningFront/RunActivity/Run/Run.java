package com.g2.runningFront.RunActivity.Run;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.concurrent.SynchronousQueue;

public class Run implements Serializable {
    int runNo;
    int userNo;
    byte[] route;
    Timestamp run_date;
    double time;
    double distance;
    double calorie;
    double speed;

    public Run(int userNo, byte[] route, Timestamp run_date, double distance, double calorie, double speed) {
        this.userNo = userNo;
        this.route = route;
        this.run_date = run_date;
        this.distance = distance;
        this.calorie = calorie;
        this.speed = speed;
    }


    public Run(int userNo,int runNo,  byte[] route, Timestamp run_date, double time, double distance, double calorie, double speed) {
        this.runNo = runNo;
        this.userNo = userNo;
        this.route = route;
        this.run_date = run_date;
        this.time = time;
        this.distance = distance;
        this.calorie = calorie;
        this.speed = speed;
    }

    public Run(int userNo, double time, double distance, double calorie, double speed) {
        this.userNo = userNo;
        this.time = time;
        this.distance = distance;
        this.calorie = calorie;
        this.speed = speed;
    }

    public Run(int run_no,Timestamp run_date, double time, double distance, double calorie, double speed) {
        this.runNo = run_no;
        this.run_date = run_date;
        this.time = time;
        this.distance = distance;
        this.calorie = calorie;
        this.speed = speed;
    }

    public Run(int userNo,int run_no,Timestamp run_date, double time, double distance, double calorie, double speed) {
        this.userNo = userNo;
        this.runNo = run_no;
        this.run_date = run_date;
        this.time = time;
        this.distance = distance;
        this.calorie = calorie;
        this.speed = speed;
    }

    public int getRunNo() {
        return runNo;
    }

    public void setRunNo(int runNo) {
        this.runNo = runNo;
    }

    public int getUserNo() {
        return userNo;
    }

    public void setUserNo(int userNo) {
        this.userNo = userNo;
    }

    public byte[] getRoute() {
        return route;
    }

    public void setRoute(byte[] route) {
        this.route = route;
    }

    public Timestamp getRun_date() {
        return run_date;
    }

    public void setRun_date(Timestamp run_date) {
        this.run_date = run_date;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getCalorie() {
        return calorie;
    }

    public void setCalorie(double calorie) {
        this.calorie = calorie;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

}
