package com.example.httptest.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.httptest.R;
import com.example.httptest.gson.Forecast;
import com.example.httptest.gson.WeatherForeCast;

import org.w3c.dom.Text;

import java.util.List;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ViewHolder> {

    private List<Forecast> weatherForeCastsList; //未来天气预报

    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView forecast_tmp;
        TextView cond_txt_d;
        TextView date;


        public ViewHolder(View view){
            super(view);

            forecast_tmp=view.findViewById(R.id.forecast_tmp);//预报天气温度
            cond_txt_d=view.findViewById(R.id.cond_txt_d);//预报天气信息 如：晴
            date=view.findViewById(R.id.date);//时间




        }
    }

    public ForecastAdapter(List<Forecast> weatherForeCasts){

        weatherForeCastsList=weatherForeCasts;
        notifyDataSetChanged();//刷新recycle界面

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_item,parent,false);
       ViewHolder holder=new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        Forecast weatherForeCast=weatherForeCastsList.get(position);

        String tmp_max=weatherForeCast.getTmp_max();
        String tmp_min=weatherForeCast.getTmp_min();

        String cond_txt=weatherForeCast.getCond_txt_d();
        String date=weatherForeCast.getDate();

        viewHolder.forecast_tmp.setText(tmp_min+"℃~"+tmp_max+"℃");
        viewHolder.cond_txt_d.setText(cond_txt);
        //2019-03-26
        viewHolder.date.setText(date.substring(5));





    }

    @Override
    public int getItemCount() {

        return weatherForeCastsList.size();
    }
}
