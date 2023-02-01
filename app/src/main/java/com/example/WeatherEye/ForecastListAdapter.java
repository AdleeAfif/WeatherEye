package com.example.WeatherEye;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import pl.droidsonroids.gif.GifImageView;

public class ForecastListAdapter extends ArrayAdapter<ForecastList> {

    private Context mContext;
    int mResource;

    public ForecastListAdapter(Context context, int resource, ArrayList<ForecastList> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Get the forecast information
        String forecastTime = getItem(position).getfTime();
        String forecastRain = getItem(position).getfRain();

        //Create the data objects with the information
        ForecastList forecastList = new ForecastList(forecastTime, forecastRain);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView tvTime = (TextView) convertView.findViewById(R.id.forecastTime);
        TextView tvRain = (TextView) convertView.findViewById(R.id.forecastRain);
        GifImageView gifWeather = (GifImageView) convertView.findViewById(R.id.forecastWeatherSymbol);

//        if(Integer.parseInt(forecastTime) >= 12) {
//            tvTime.setText(Integer.parseInt(forecastTime) - 12 + " PM");
//            if (Integer.parseInt(forecastTime) - 12 == 0)
//                tvTime.setText(Integer.parseInt(forecastTime) + " PM");
//        }
//        else {
//            tvTime.setText(forecastTime + " AM");
//            if (Integer.parseInt(forecastTime) == 0)
//                tvTime.setText("12 AM");
//        }

        if ((int) Double.parseDouble(forecastRain) > 95){
            gifWeather.setImageResource(R.drawable.sun_icon);
        }else{
            gifWeather.setImageResource(R.drawable.rain_icon);
        }

        tvTime.setText(Integer.parseInt(forecastTime) + 1 + " Hour");
        tvRain.setText("Raindrop: " + (int) Double.parseDouble(forecastRain));

        return convertView;
    }
}
