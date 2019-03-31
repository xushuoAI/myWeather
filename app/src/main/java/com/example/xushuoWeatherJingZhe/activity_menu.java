package com.example.httptest;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.httptest.db.County;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class activity_menu extends Fragment {
    private List<County> countyListTab=new ArrayList<County>();
    private ListView TablistView;
    private List<String> TabCity=new ArrayList<>(); //城市名称
    private List<String> TabWeatherId=new ArrayList<>(); //天气ID
    public  ArrayAdapter<String> adapter;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view=inflater.inflate(R.layout.activity_menu,container,false);
        TablistView=view.findViewById(R.id.TabCity);
        adapter=new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,TabCity);
        TablistView.setAdapter(adapter);
        initTabCity();



        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        TablistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String weatherId=TabWeatherId.get(position);
                activity_weather activity=(activity_weather)getActivity();
                activity.drawerLayout.closeDrawers();
                activity.swipeRefreshLayout.setRefreshing(true);
                activity.requestWeather(weatherId);
                activity.requestWeatherForecast(weatherId);
                activity.requestLifeStyle(weatherId);


            }
        });



    }



    public void initTabCity(){
        SharedPreferences preferences=getActivity().getSharedPreferences("TabList",Activity.MODE_MULTI_PROCESS);
        //获得已选择的城市列表
        String jsonCity=preferences.getString("TabList",null);
        //获得城市列表的json数据
        if(jsonCity!=null){
            Gson gson=new Gson();
            Type type=new TypeToken<List<County>>(){}.getType();
            //解析为county list
            countyListTab=gson.fromJson(jsonCity,type);
            TabCity.clear();
            TabWeatherId.clear();
            for (int i=0;i<countyListTab.size();i++){
                TabCity.add(countyListTab.get(i).getCountyName());
                TabWeatherId.add(countyListTab.get(i).getWeatherId());
            }
        }
        adapter.notifyDataSetChanged();
        TablistView.setSelection(0);
    }


   /* @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (hidden){
            initTabCity();
            //界面隐藏 不显示
            return;
        }else{
            //界面显示，数据刷新
            initTabCity();
        }



    }*/
}
