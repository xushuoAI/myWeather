package com.example.httptest;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.httptest.db.City;
import com.example.httptest.db.County;
import com.example.httptest.db.Province;
import com.example.httptest.util.HttpUtil;
import com.example.httptest.util.Utility;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChooseAreaFragment extends Fragment {
    public static final int LEVEL_PROVINCE=0;
    public static final int LEVEL_CITY=1;
    public static final int LEVEL_COUNTY=2;
    private ProgressDialog progressDialog;
    private TextView titleText;
    private Button backButton;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList=new ArrayList<>();

    private List<Province> provinceList;    //省列表
    private List<City> cityList;    //市列表
    private List<County> countyList;    //县列表
    private List<County> cityTabList=new ArrayList<County>();;//已选择城市

    private Province selectedProvince;//选中的省份
    private City selectedCity;  //选中的城市
    private int currentLevel;//当前选中的级别

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.choose_area,container,false);
      /*  resource：布局资源id
        root：resource生成view对象要填入的父布局。为null，则返回的view就是布局资源；否则，需要参照第三个参数
        attachToRoot：是否将resource生成view对象填入root，以root作为最终返回view的根布局。
        false，root不为null，则提供root的LayoutParams约束resource生成的view；
        true，root不为null，以root作为最终返回view的根布局
         */
          titleText=view.findViewById(R.id.title_text);
          backButton=view.findViewById(R.id.back_button);
          listView=view.findViewById(R.id.list_view);
          adapter=new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,dataList);
          listView.setAdapter(adapter);

          return view;


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel==LEVEL_PROVINCE){
                    selectedProvince=provinceList.get(position);
                    queryCities();

                }else if (currentLevel==LEVEL_CITY){
                    selectedCity=cityList.get(position);
                    queryConties();

                }else if (currentLevel==LEVEL_COUNTY){

                    String weatherId=countyList.get(position).getWeatherId();


                    if (getActivity() instanceof MainActivity){
                    //如果是该碎片在MainActivity中 处理不变
                        addCityTab(countyList.get(position));//保存已选城市
                        Intent intent=new Intent(getActivity(),activity_weather.class);
                        intent.putExtra("weather_id",weatherId);
                        startActivity(intent);
                        getActivity().finish();
                    }else if (getActivity() instanceof activity_weather){
                        addCityTab(countyList.get(position));//保存已选城市
                        //如果是该碎片在weather_activity中 关闭滑动菜单，显示刷新进度，请求城市信息
                        activity_weather activity=(activity_weather)getActivity();
                        activity.drawerLayout.closeDrawers();
                        activity.swipeRefreshLayout.setRefreshing(true);
                        activity.requestWeather(weatherId);
                        activity.requestWeatherForecast(weatherId);
                        activity.requestLifeStyle(weatherId);
                        activity.RefreshTabCity();

                    }
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (currentLevel==LEVEL_COUNTY){
                    queryCities();

                }else if (currentLevel==LEVEL_CITY){
                    queryProvinces();
                }
            }
        });
        queryProvinces();
    }

    private void queryFromServer(String address,final String type){
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            //连接网络，请求服务器数据
            @Override
            public void onFailure(Call call, IOException e) {
                //访问服务器失败。
                getActivity().runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        closeProgressDialog();

                        Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();

                    }
                });


            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //访问服务器成功，获得数据
                String responseText=response.body().string();//获得数据，转换为String
                boolean result=false;//设置结果值
                if ("province".equals(type)){
                    //从服务器查询省的数据信息
                    result=Utility.handleProvinceResponse(responseText);
                    //将数据信息添加到本地数据库。添加成功返回true，否则返回false
                }else if ("city".equals(type)){
                    //从服务器查询市的数据信息
                    result=Utility.handleCityResponse(responseText,selectedProvince.getId());
                }else if ("county".equals(type)){
                    //从服务器查询县的数据信息
                    result=Utility.handleCountyResponse(responseText,selectedCity.getId());

                }
                if (result){
                    //如果已成功添加到本地数据库
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)){
                                //查询省的信息
                                queryProvinces();
                            }else if ("city".equals(type)){
                                queryCities();

                            }else if ("county".equals(type)){
                                queryConties();

                            }
                        }
                    });

                }
            }
        });

    }


    private void queryProvinces(){
        //查询所有的省，优先从数据库中查询，如果没有查询到。再从服务器上查询。
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);
        //setVisibility(View.GONE);隐藏
        //setVisibility(View.visivle);可见
        //setVisibility(View.invisible);不可见
        //android:visibibility="Ggone"
        provinceList=DataSupport.findAll(Province.class);
        if (provinceList.size()>0){
             dataList.clear();//适配器中的数据 清空
             for (Province province:provinceList){
                 dataList.add(province.getProvinceName());
                 //适配器中的数据 添加
                 // adapter=new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,dataList);
                 //          listView.setAdapter(adapter);

             }
             adapter.notifyDataSetChanged();
             //调用adapter.notifyDataSetChanged与listView.setAdapter函数都会引起界面重绘，
            // 区别是前者会保留原有位置、数据信息，后者是回到初始状态。
             listView.setSelection(0);
             currentLevel=LEVEL_PROVINCE;

        }else{
            String address="http://guolin.tech/api/china";
            queryFromServer(address,"province");

        }

    }

    private void queryCities(){
        titleText.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        cityList=DataSupport.where("provinceid=?",String.valueOf(selectedProvince.getId())).find(City.class);
        //从City表中查询那些数据是属于selectedProvince这个省的
        //为了避免冗长的参数列表，LitePal采用了一种非常巧妙的解决方案，
        // 叫作连缀查询，这种查询很灵活，可以根据我们实际的查询需求来动态配置查询参数。
        // 那这里举个简单的例子，比如我们想查询news表中所有评论数大于零的新闻，
        // 就可以这样写：List<News> newsList = DataSupport.where("commentcount > ?", "0").find(News.class);
        //---------------------
        if (cityList.size()>0){
            dataList.clear();
            for (City city:cityList){
                dataList.add(city.getCityName());

            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel=LEVEL_CITY;
        }else{
            int provinceCode=selectedProvince.getProvinceCode();
            String address="http://guolin.tech/api/china/"+provinceCode;
            queryFromServer(address,"city");
        }

    }

    private void queryConties(){
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countyList=DataSupport.where("cityid=?",String.valueOf(selectedCity.getId())).find(County.class);

        if (countyList.size()>0){
            dataList.clear();

            for (County county:countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel=LEVEL_COUNTY;

        }else{
            int provinceCode=selectedProvince.getProvinceCode();
            int cityCode=selectedCity.getCityCode();
            String address="http://guolin.tech/api/china/"+provinceCode+"/"+cityCode;
            queryFromServer(address,"county");
        }



    }


    private void addCityTab(County county){
        SharedPreferences preferences=getActivity().getSharedPreferences("TabList",Activity.MODE_MULTI_PROCESS);
        //获得已选择的城市列表
        String jsonCity=preferences.getString("TabList",null);
        //获得城市列表的json数据
        if(jsonCity!=null){
            Gson gson=new Gson();
            Type type=new TypeToken<List<County>>(){}.getType();
            //解析为county list
            cityTabList=gson.fromJson(jsonCity,type);
        }

        if (!cityTabList.contains(county)){
            //如果不包含该城市  equals 不一样,返回假
            cityTabList.add(county);

        }
        SharedPreferences.Editor editor=getActivity().getSharedPreferences("TabList",Context.MODE_MULTI_PROCESS).edit();
        //将已选城市保存保sharedpreference,将List<county>转换为json 保存
        Gson gson=new Gson();
        jsonCity=gson.toJson(cityTabList);

        editor.putString("TabList",jsonCity).commit();







    }





    private void showProgressDialog(){
        if (progressDialog==null){
            progressDialog=new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);

        }
        progressDialog.show();
    }
    private void closeProgressDialog(){
        if (progressDialog!=null){
            progressDialog.dismiss();
        }

    }


}
