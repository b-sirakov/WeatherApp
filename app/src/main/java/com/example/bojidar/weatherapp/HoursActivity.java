package com.example.bojidar.weatherapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.bojidar.weatherapp.model.ForecastAdapter;
import com.example.bojidar.weatherapp.model.Location;

public class HoursActivity extends AppCompatActivity {

    private Location location;
    private RecyclerView hoursRV;
    private TextView cityTV;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hours);

        getSupportActionBar().hide();
        location= (Location) getIntent().getSerializableExtra("loc");
        //Toast.makeText(this, location.getForecastsHours().get(0).getDate(), Toast.LENGTH_SHORT).show();

        cityTV= (TextView) findViewById(R.id.hours_city_tv);
        cityTV.setText(location.getCityName());
        hoursRV = (RecyclerView) findViewById(R.id.hours_rv);
        backButton = (Button) findViewById(R.id.back_button_hours);

        ForecastAdapter forecastAdapter =new ForecastAdapter(this,location.getForecastsHours());
        hoursRV.setAdapter(forecastAdapter);
        hoursRV.setLayoutManager(new LinearLayoutManager(this));

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
