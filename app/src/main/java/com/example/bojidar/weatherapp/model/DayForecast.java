package com.example.bojidar.weatherapp.model;

/**
 * Created by Bojidar on 5/24/2017.
 */

public class DayForecast extends Forecast {

    private double minTemp;
    private double maxTemp;
    private double minTempF;
    private double maxTempF;
    private String sunrise;
    private String sunset;

    public DayForecast(double temp,double tempF, String sky, double windSpeed, int humidity, double pressure, String date,double vis,double precip,
                       double minTemp, double maxTemp,double minTempF,double maxTempF, String sunrise, String sunset) {
        super(temp,tempF, sky, windSpeed, humidity, pressure, date,vis,precip);

        this.minTemp=minTemp;
        this.maxTemp=maxTemp;
        this.minTempF=minTempF;
        this.maxTempF=maxTempF;
        this.sunrise=sunrise;
        this.sunset=sunset;
    }

    public double getMinTempF() {
        return minTempF;
    }

    public double getMaxTempF() {
        return maxTempF;
    }

    public double getMinTemp() {
        return minTemp;
    }

    public double getMaxTemp() {
        return maxTemp;
    }

    public String getSunrise() {
        return sunrise;
    }

    public String getSunset() {
        return sunset;
    }
}
