package com.example.WeatherEye;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HistoryListAdapter extends RecyclerView.Adapter<HistoryListAdapter.ViewHolder> {

    private ArrayList<HistoryList> arrayList;
    public HistoryListAdapter(ArrayList<HistoryList> arrayList){
        this.arrayList = arrayList;
    }
    int selectedPosition=-1;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View inflate = layoutInflater.inflate(R.layout.recorditem_layout, null);

        ViewHolder viewHolder = new ViewHolder(inflate);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        HistoryList historyList = arrayList.get(position);
        holder.temperatureData.setText(historyList.getTemperature() + "°C");
        holder.humidityData.setText(historyList.getHumidity() + "%");
        holder.raindropData.setText(historyList.getRaindrop());
        holder.dateData.setText(historyList.getDate().substring(0,10));
        holder.timeData.setText(historyList.getTime().substring(11,19));
        holder.idData.setText(historyList.getID());

        if(selectedPosition==position)
            holder.itemView.setBackgroundColor(Color.parseColor("#272643"));
        else
            holder.itemView.setBackgroundColor(Color.parseColor("#2e3b52"));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPosition=position;
                notifyDataSetChanged();

            }
        });

        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        // Set the height by params
        params.height=450;
        // Set height of RecyclerView
        holder.itemView.setLayoutParams(params);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView temperatureData;
        TextView humidityData;
        TextView raindropData;
        TextView dateData;
        TextView timeData;
        TextView idData;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            temperatureData = itemView.findViewById(R.id.itemTemp);
            humidityData = itemView.findViewById(R.id.itemHumid);
            raindropData = itemView.findViewById(R.id.itemRain);
            dateData = itemView.findViewById(R.id.itemDate);
            timeData = itemView.findViewById(R.id.itemTime);
            idData = itemView.findViewById(R.id.historyID);
        }
    }
}
