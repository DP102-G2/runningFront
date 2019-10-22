package com.g2.runningFront.Common;

import java.io.Serializable;

public class User implements Serializable {
    private int no;
    private String id;
    private String name;
    private String password;

    public User() {
        super();
    }

    public User(int no, String id, String name, String password) {
        this.no = no;
        this.id = id;
        this.name = name;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
