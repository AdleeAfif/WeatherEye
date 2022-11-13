package com.example.WeatherEye;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class HistoryListAdapter extends ArrayAdapter<HistoryList> {

    private Context mContext;
    int mResource;

    public HistoryListAdapter(Context context, int resource, ArrayList<HistoryList> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Get the data information
        String timeFetch = getItem(position).getTime();
        String dateFetch = getItem(position).getDate();
        String dataTemp = getItem(position).getTemperature();
        String dataHumid= getItem(position).getHumidity();
        String dataRain = getItem(position).getRaindrop();

        //Create the data objects with the information
        HistoryList historyList = new HistoryList(timeFetch, dateFetch, dataTemp, dataHumid, dataRain);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView tvTime = (TextView) convertView.findViewById(R.id.testList);
        TextView tvDate = (TextView) convertView.findViewById(R.id.testList2);
        TextView tvTemp = (TextView) convertView.findViewById(R.id.temperatureData);
        TextView tvHumid = (TextView) convertView.findViewById(R.id.humidityData);
        TextView tvRain = (TextView) convertView.findViewById(R.id.raindropData);

        tvTime.setText(timeFetch);
        tvDate.setText(dateFetch);
        tvTemp.setText("Temperature: " + dataTemp);
        tvHumid.setText("Humidity: " + dataHumid);
        tvRain.setText("Raindrop: " + dataRain);

        return convertView;
    }
}
