package com.example.xushuoWeatherJingZhe.gson;

public class Update {


    //"update":{"loc":"2019-03-16 15:55","utc":"2019-03-16 07:55"}

    public String loc; //当地时间，24小时制，格式yyyy-MM-dd HH:mm
    public String utc; //UTC时间，24小时制，格式yyyy-MM-dd HH:mm

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    public String getUtc() {
        return utc;
    }

    public void setUtc(String utc) {
        this.utc = utc;
    }
}
