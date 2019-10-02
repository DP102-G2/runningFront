package com.g2.runningFront.Common;

import java.io.Serializable;

public class User implements Serializable {
    private int no;
    private String id, password;

    public User() {
        super();
    }

    public User(int no, String id, String password) {
        this.no = no;
        this.id = id;
        this.password = password;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
