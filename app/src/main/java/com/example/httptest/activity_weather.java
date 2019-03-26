package com.example.httptest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.httptest.adapter.ForecastAdapter;
import com.example.httptest.gson.Forecast;
import com.example.httptest.gson.Weather;
import com.example.httptest.gson.WeatherForeCast;
import com.example.httptest.service.AutoUpdateService;
import com.example.httptest.util.HttpUtil;
import com.example.httptest.util.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.internal.Util;

public class activity_weather extends AppCompatActivity {

    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;

    private ImageView bingPicImg;

    public SwipeRefreshLayout swipeRefreshLayout; //下拉刷新

    public DrawerLayout drawerLayout;//滑动菜单
    private Button navButton; //打开滑动菜单


    public RecyclerView recyclerView; //天气预报显示
    private List<Forecast> weatherForeCasts=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT>=21){
            View decorView=getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            );//活动布局显示状态栏上
            getWindow().setStatusBarColor(Color.TRANSPARENT);//状态栏设置为透明色
        }



        setContentView(R.layout.activity_weather);

        weatherLayout=findViewById(R.id.weather_layout);
        titleCity=findViewById(R.id.title_city);
        titleUpdateTime=findViewById(R.id.title_update_time);

        degreeText=findViewById(R.id.degree_text);
        weatherInfoText=findViewById(R.id.weather_info_text);
        forecastLayout=findViewById(R.id.forecast_layout);

        aqiText=findViewById(R.id.aqi_text);
        pm25Text=findViewById(R.id.pm25_text);
        comfortText=findViewById(R.id.comfort_text);
        carWashText=findViewById(R.id.car_wash_text);
        sportText=findViewById(R.id.sport_text);

        bingPicImg=findViewById(R.id.bing_pic_img);
        swipeRefreshLayout=findViewById(R.id.swipe_refresh);

        drawerLayout=findViewById(R.id.drawer_layout);
        navButton=findViewById(R.id.nav_button);
        recyclerView=findViewById(R.id.recycle_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);//下拉刷新 进度条颜色


        SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(this);
        //已当前应用的包名作为前缀来命名SharedPreference文件

        String bingPic=prefs.getString("bing_pic",null);
        if (bingPic!=null){
            Glide.with(this).load(bingPic).into(bingPicImg); //加载背景图片

        }else{
            loadBingPic();

        }



        String weatherString=prefs.getString("weather",null);
        String weatherForecastString=prefs.getString("weatherForeCast",null);
        final String weatherId;

        if (weatherString!=null){

            Weather weather=Utility.handleWeatherResponse(weatherString);
            weatherId=weather.basic.cid;
            showWeatherInfo(weather);

            if(weatherForecastString!=null){
            WeatherForeCast weatherForeCast=Utility.handleWeatherForecastResponse(weatherForecastString);
                showWeatherForecastInfo(weatherForeCast);
            }else{
                requestWeatherForecast(weatherId);
            }





        }else{

            weatherId=getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
            requestWeatherForecast(weatherId);
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(weatherId);
                requestWeatherForecast(weatherId);
                //当下拉时，开始请求天气情况
            }
        });


        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });





    }

    public void requestWeather(final String weatherId){
        //https://free-api.heweather.net/s6/weather/now?location=CN101190401&key=657004efd4694b4fb8ec38d4366f58e1
        String weatherUrl = "https://free-api.heweather.net/s6/weather/now?location="+weatherId+"&key=657004efd4694b4fb8ec38d4366f58e1";

        HttpUtil.sendOkHttpRequest(weatherUrl,new Callback(){

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    final String responseText = response.body().string();
                    final Weather weather = Utility.handleWeatherResponse(responseText);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (weather!=null&&"ok".equals(weather.status)){
                                SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(activity_weather.this).edit();
                                editor.putString("weather",responseText);
                                editor.apply();
                                showWeatherInfo(weather);


                            }else{
                                Toast.makeText(activity_weather.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();

                            }
                            swipeRefreshLayout.setRefreshing(false);//表示刷新事件结束，隐藏进度条
                        }
                    });

                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity_weather.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });

        loadBingPic();
    }
    public void requestWeatherForecast(final String weatherId){
        //https://free-api.heweather.net/s6/weather/now?location=CN101190401&key=657004efd4694b4fb8ec38d4366f58e1
        String weatherUrl = "https://free-api.heweather.net/s6/weather/forecast?location="+weatherId+"&key=657004efd4694b4fb8ec38d4366f58e1";
        //获得未来天气预报
        HttpUtil.sendOkHttpRequest(weatherUrl,new Callback(){

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    final String responseText = response.body().string();
                    final WeatherForeCast weatherForeCast = Utility.handleWeatherForecastResponse(responseText);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (weatherForeCast!=null&&"ok".equals(weatherForeCast.status)){
                                SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(activity_weather.this).edit();
                                editor.putString("weatherForeCast",responseText);
                                editor.apply();
                                showWeatherForecastInfo(weatherForeCast);


                            }else{
                                Toast.makeText(activity_weather.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();

                            }
                            swipeRefreshLayout.setRefreshing(false);//表示刷新事件结束，隐藏进度条
                        }
                    });

                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity_weather.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });

        loadBingPic();
    }

    private void showWeatherForecastInfo(WeatherForeCast weatherForeCast){


        if (weatherForeCast!=null&&"ok".equals(weatherForeCast.status)) {

            weatherForeCasts = weatherForeCast.daily_forecast; //获得预报数组
            ForecastAdapter adapter = new ForecastAdapter(weatherForeCasts);

            recyclerView.setAdapter(adapter);
        }else {
            Toast.makeText(this,"recycleview天气预报获取失败",Toast.LENGTH_SHORT).show();
        }

    }

    private void showWeatherInfo(Weather weather){
        if (weather!=null&&"ok".equals(weather.status)) {
            String cityName = weather.basic.location;
           /* String updateTime = weather.update.loc;*/
            String degree = weather.now.tmp + "℃";
            /*String weatherInfo=weather.now.more.info;*/

            titleCity.setText(cityName);
            /*titleUpdateTime.setText(updateTime);*/
            degreeText.setText(degree);
            /* weatherInfoText.setText(weatherInfo);*/
            forecastLayout.removeAllViews();

            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
         /*   TextView dataText = view.findViewById(R.id.data_text);*/
            TextView infoText = view.findViewById(R.id.info_text);
            TextView maxText = view.findViewById(R.id.max_text);
            TextView minText = view.findViewById(R.id.min_text);


            /*dataText.setText(weather.update.loc);*/
            infoText.setText(weather.now.cond_txt);
            maxText.setText("体感温度："+weather.now.fl+"℃");
            minText.setText("实际温度："+weather.now.tmp+"℃");

            forecastLayout.addView(view);





            /*aqiText.setText(weather.now.hum);
            pm25Text.setText(weather.now.pcpn);*/


      /*  String comfort="舒适度:"+weather.suggestion.comfort.info;
        String carWash="洗车指数:"+weather.suggestion.carWash.info;
        String sport="运动建议:"+weather.suggestion.sport.info;*/

        /*comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);*/

            weatherLayout.setVisibility(View.VISIBLE);

            Intent intent=new Intent(this,AutoUpdateService.class);
            startService(intent); //开起服务，后台更新天气数据
        }else{
            Toast.makeText(activity_weather.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();

        }





    }



    private void loadBingPic(){
        String requestBingPic="http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String bingPic=response.body().string();
                SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(activity_weather.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(activity_weather.this).load(bingPic).into(bingPicImg);

                    }
                });

            }
        });


    }







}
