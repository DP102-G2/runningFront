package com.g2.runningFront.ShopActivity.Service;

import java.sql.Timestamp;

public class Message {

    int msg_no;
    int user_no;
    Timestamp msg_time;
    int msg_by;
    String msg_text;
    int msg_read;

    public Message(int user_no, int msg_read) {
        this.user_no = user_no;
        this.msg_read = msg_read;
    }

    public Message(int user_no, int msg_by, String msg_text, int msg_read,Timestamp msg_time) {
        this.user_no = user_no;
        this.msg_by = msg_by;
        this.msg_time = msg_time;
        this.msg_text = msg_text;
        this.msg_read = msg_read;
    }

    public Message(int user_no, int msg_by, String msg_text) {
        this.user_no = user_no;
        this.msg_by = msg_by;
        this.msg_text = msg_text;
    }

    public Message(int msg_no, int user_no, Timestamp msg_time, int msg_by, String msg_text, int msg_read) {
        this.msg_no = msg_no;
        this.user_no = user_no;
        this.msg_time = msg_time;
        this.msg_by = msg_by;
        this.msg_text = msg_text;
        this.msg_read = msg_read;
    }

    public int getMsg_no() {
        return msg_no;
    }

    public void setMsg_no(int msg_no) {
        this.msg_no = msg_no;
    }

    public int getUser_no() {
        return user_no;
    }

    public void setUser_no(int user_no) {
        this.user_no = user_no;
    }

    public Timestamp getMsg_time() {
        return msg_time;
    }

    public void setMsg_time(Timestamp msg_time) {
        this.msg_time = msg_time;
    }

    public int getMsg_by() {
        return msg_by;
    }

    public void setMsg_by(int msg_by) {
        this.msg_by = msg_by;
    }

    public String getMsg_text() {
        return msg_text;
    }

    public void setMsg_text(String msg_text) {
        this.msg_text = msg_text;
    }

    public int getMsg_read() {
        return msg_read;
    }

    public void setMsg_read(int msg_read) {
        this.msg_read = msg_read;
    }
}
