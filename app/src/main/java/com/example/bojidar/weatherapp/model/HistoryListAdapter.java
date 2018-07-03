package com.example.bojidar.weatherapp.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bojidar.weatherapp.R;

import java.util.List;

/**
 * Created by Bozhidar.Sirakov on 8/24/2017.
 */

public class HistoryListAdapter extends RecyclerView.Adapter {

    public class HistoryListVH extends RecyclerView.ViewHolder{

        TextView historyTV;

        public HistoryListVH(View row) {
            super(row);
            historyTV= (TextView) row.findViewById(R.id.history_city);
        }
    }

    private Context context;
    private List<String> locationsList;

    public HistoryListAdapter(Context context, List<String> locationsList) {
        this.context = context;
        this.locationsList = locationsList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View row = inflater.inflate(R.layout.location_row, parent, false);
        HistoryListVH vh=new HistoryListVH(row);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ((HistoryListVH)holder).historyTV.setText(locationsList.get(position));
        ((HistoryListVH)holder).historyTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("loc_his",locationsList.get(position));
                ((Activity)context).setResult(3,intent);
                ((Activity)context).finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return locationsList.size();
    }
}
