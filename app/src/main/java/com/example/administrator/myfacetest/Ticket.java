package com.example.administrator.myfacetest;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/11/10.
 */

public class Ticket implements Serializable{
    private String  num;
    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Ticket(int type, String num) {
        this.type = type;
        this.num = num;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }
}
