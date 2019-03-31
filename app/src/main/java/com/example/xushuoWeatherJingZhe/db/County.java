package com.example.httptest.db;

import org.litepal.crud.DataSupport;

public class County extends DataSupport {
    private int id;
    private String countyName; //县名字
    private String weatherId;//天气ID
    private int cityId;//县所属市的ID值

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    @Override
    public boolean equals( Object obj) {
        if (obj == null){
            return false;
        }
        if (obj instanceof County){
        County other = (County) obj;
        if (!this.countyName.equals(other.getCountyName())){
            //城市名称不一样 返回假
            //equals 相同返回true
            return false;
             }
        }
            return true;


    }
}
