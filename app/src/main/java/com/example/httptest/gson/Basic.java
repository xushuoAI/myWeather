package com.example.httptest.gson;

import com.google.gson.annotations.SerializedName;

public class Basic {
    //@SerializedName用于将json中的属性与java中的属性对应起来。
    //　我们有一段json数据如下：
    //           {
    //
    //    "id":"1"
    //    "n":"kyoya"
    //    "p":"123456"
    //    "s":"0"
    //}
    //定义一个User类解析json
    //public class User{
    //         private String id;
    //         private String n;
    //         private String p;
    //         private String s;
    //     }
    //User类要这样写才能直接使用Gson直接解析出来，但是这样的话User类的属性命名就不太友好了，那我们要怎么做呢？Gson提供注解的方法来解决这个问题@SerializedName，使用方法如下：
    //public class User{
    //
    //    private String id;
    //
    //    @SerializedName("n")
    //    private String userName;
    //
    //    @SerializedName("p")
    //    private String password;
    //
    //    @SerializedName("s")
    //    private String sex;
    //}


    //{"basic":{"
    // cid":"CN101190401", 地区／城市ID
    // "location":"苏州", 地区／城市名称
    // "parent_city":"苏州", 该地区／城市的上级城市
    // "admin_area":"江苏", 该地区／城市所属行政区域
    // "cnty":"中国", 该地区／城市所属国家名称
    // "lat":"31.29937935", 地区／城市纬度
    // "lon":"120.61958313", 地区／城市经度
    // "tz":"+8.00" 该地区／城市所在时区
    // },

    public String cid;  //地区／城市ID
    public String location; //地区／城市名称
    public String parent_city;//该地区／城市的上级城市
    public String admin_area;//该地区／城市所属行政区域
    public String cnty;//该地区／城市所属国家名称
    public String lat;//地区／城市纬度
    public String lon;//地区／城市经度
    public String tz;//城市所在时区

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getParent_city() {
        return parent_city;
    }

    public void setParent_city(String parent_city) {
        this.parent_city = parent_city;
    }

    public String getAdmin_area() {
        return admin_area;
    }

    public void setAdmin_area(String admin_area) {
        this.admin_area = admin_area;
    }

    public String getCnty() {
        return cnty;
    }

    public void setCnty(String cnty) {
        this.cnty = cnty;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getTz() {
        return tz;
    }

    public void setTz(String tz) {
        this.tz = tz;
    }
}
