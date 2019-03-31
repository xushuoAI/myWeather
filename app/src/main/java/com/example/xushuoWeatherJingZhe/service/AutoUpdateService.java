package com.example.xushuoWeatherJingZhe.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.example.xushuoWeatherJingZhe.gson.Suggestion;
import com.example.xushuoWeatherJingZhe.gson.Weather;
import com.example.xushuoWeatherJingZhe.gson.WeatherForeCast;
import com.example.xushuoWeatherJingZhe.util.HttpUtil;
import com.example.xushuoWeatherJingZhe.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {


    public AutoUpdateService() {


    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //服务启动时调用


        updateBingPic();
        updateWeather();
        AlarmManager manager=(AlarmManager)getSystemService(ALARM_SERVICE);
        //获取AlarmManager实例，设置定义任务触发时间 8小时
        int anHour=8*60*60*1000;    //  八小时的毫秒数
        long triggerAtTime=SystemClock.elapsedRealtime()+anHour;
        //triggerAtTime  =elapsedRealtime（从开机时间到当前时间)+触发时间 后触发定义任务
        //若为ELAPSED_RTC_WAKEUP即从1970/1/1日算起
        Intent i=new Intent(this,AutoUpdateService.class);
        PendingIntent pi=PendingIntent.getService(this,0,i,0); //指定处理定时任务的服务
        manager.cancel(pi);//该方法为解除与此PendingIntent相同的Alarm全部取消
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi); //完成设定
        return super.onStartCommand(intent,flags,startId);





    }

    private void updateWeather(){

        SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString=prefs.getString("weather",null);


        if (weatherString!=null){
            Weather weather=Utility.handleWeatherResponse(weatherString);
            String weatherId=weather.basic.cid;
            String weatherUrl="https://free-api.heweather.net/s6/weather/now?location="+weatherId+"&key=657004efd4694b4fb8ec38d4366f58e1";
            String weatherForecastUrl = "https://free-api.heweather.net/s6/weather/forecast?location="+weatherId+"&key=657004efd4694b4fb8ec38d4366f58e1";
            String weatherLifeStyleUrl = "https://free-api.heweather.net/s6/weather/lifestyle?location="+weatherId+"&key=657004efd4694b4fb8ec38d4366f58e1";
            HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    String responseText=response.body().string();
                    Weather weather=Utility.handleWeatherResponse(responseText);

                    if (weather!=null&&"ok".equals(weather.status)){
                        SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        editor.putString("weather",responseText);
                        editor.apply();

                    }
                }
            });

            HttpUtil.sendOkHttpRequest(weatherForecastUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    String responseText=response.body().string();
                    WeatherForeCast weatherForeCast = Utility.handleWeatherForecastResponse(responseText);

                    if (weatherForeCast!=null&&"ok".equals(weatherForeCast.status)){
                        SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        editor.putString("weatherForeCast",responseText);
                        editor.apply();

                    }

                }
            });



            HttpUtil.sendOkHttpRequest(weatherLifeStyleUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    String responseText=response.body().string();
                    Suggestion suggestion = Utility.handleSuggestionResponse(responseText);

                    if (suggestion!=null&&"ok".equals(suggestion.status)){
                        SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        editor.putString("LifeStyle",responseText);
                        editor.apply();

                    }

                }
            });

        }




    }

    private void updateBingPic(){
        String requestBingPic="http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String bingPic=response.body().string();
                SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();



            }
        });


    }





}
