package com.example.bojidar.weatherapp;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Button;

import com.example.bojidar.weatherapp.model.HistoryListAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

public class LocationsHistoryActivity extends AppCompatActivity {

    private Button clearHistoryButton;
    private RecyclerView historyRV;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations_history);

        getSupportActionBar().hide();

        clearHistoryButton= (Button) findViewById(R.id.clear_history_button);
        historyRV = (RecyclerView) findViewById(R.id.location_history_lv);
        backButton= (Button) findViewById(R.id.back_button_history);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final SharedPreferences share= getSharedPreferences("locations_history",MODE_PRIVATE);
        SharedPreferences.Editor editor =share.edit();

        if(share.getString("loc_arr", null)==null){
            Gson gson = new Gson();
            ArrayList<String> tempArrayList=new ArrayList<>();
            String json = gson.toJson(tempArrayList);
            editor.putString("loc_arr", json);
            editor.commit();
        }

        String json = share.getString("loc_arr", null);
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        ArrayList<String> locArrayList = gson.fromJson(json, type);

        final ArrayList<String> locations=new ArrayList<>(locArrayList);
        Collections.reverse(locations);
        //ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(this,R.layout.location_row,R.id.history_city,locations);
        final HistoryListAdapter adapter=new HistoryListAdapter(this,locations);
        historyRV.setAdapter(adapter);
        historyRV.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false) );

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT ) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int position = viewHolder.getAdapterPosition();
                SharedPreferences share= getSharedPreferences("locations_history",MODE_PRIVATE);
                SharedPreferences.Editor editor =share.edit();

                Gson gson = new Gson();
                ArrayList<String> locArrayList = new ArrayList<>();

                locations.remove(position);
                locArrayList.addAll(locations);
                Collections.reverse(locArrayList);

                String json = "";
                json = gson.toJson(locArrayList);
                editor.putString("loc_arr", json);
                editor.commit();

                adapter.notifyDataSetChanged();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(historyRV);

        clearHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSharedPreferences("locations_history", 0).edit().clear().commit();
                locations.clear();
                historyRV.getAdapter().notifyDataSetChanged();
            }
        });

    }
}
