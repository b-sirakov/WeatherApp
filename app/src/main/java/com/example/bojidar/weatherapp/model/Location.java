package com.example.bojidar.weatherapp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bojidar on 3/27/2017.
 */

public class Location implements Serializable{
    private String cityName;
    private String country;
    private Forecast currentWeather;
    private List<Forecast> forecastsDays = new ArrayList<>();
    private List<Forecast> forecastsHours = new ArrayList<>();

    public Location(String cityName, String country) {
        this.cityName = cityName;
        this.country = country;
    }

    public void setCurrentWeather(double temp,double tempF, String sky, double windSpeed,String winDir,
                                  SerialBitmap iconWind,int humidity,double pressure,String date,double vis,double precip){
        this.currentWeather=new Forecast(temp,tempF,sky,windSpeed,humidity,pressure,date,vis,precip);
        this.currentWeather.setWinDir(winDir);
        this.currentWeather.setWindIcon(iconWind);
    }

    public void setForecastsDays(List<Forecast> forecastsDays) {
        this.forecastsDays = forecastsDays;
    }

    public void setforecastsHours(List<Forecast> forecastsHours){
        this.forecastsHours=forecastsHours;
    }

    public String getCityName() {
        return cityName;
    }

    public String getCountry() {
        return country;
    }

    public Forecast getCurrentWeather() {
        return currentWeather;
    }

    public List<Forecast> getForecastsHours() {
        return forecastsHours;
    }

    public List<Forecast> getForecastsDays() {
        return forecastsDays;
    }

    //    public void print(){
//        System.out.println("City: "+this.cityName);
//        System.out.println("County: "+this.country+"\n");
//        System.out.println("Current Weather: ");
//        currentWeather.print();
//    }
}
