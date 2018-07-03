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

public class DaysActivity extends AppCompatActivity {

    private Location location;
    private RecyclerView daysRV;
    private TextView cityTV;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_days);

        getSupportActionBar().hide();

        if(getIntent()!=null) {
                location = (Location) getIntent().getSerializableExtra("loc");
        }

        cityTV= (TextView) findViewById(R.id.days_city_tv);
        cityTV.setText(location.getCityName());
        daysRV = (RecyclerView) findViewById(R.id.days_rv);
        backButton = (Button) findViewById(R.id.back_button_days);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ForecastAdapter forecastAdapter =new ForecastAdapter(this,location.getForecastsDays());
        daysRV.setAdapter(forecastAdapter);
        daysRV.setLayoutManager(new LinearLayoutManager(this) );
    }

}
