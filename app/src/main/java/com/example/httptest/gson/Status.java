package com.example.httptest.gson;

public class Status {

    //"status":"ok"

    public String status; //接口状态，具体含义请参考接口状态码及错误码

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
