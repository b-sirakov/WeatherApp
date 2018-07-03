package com.example.bojidar.weatherapp.model;

import java.io.Serializable;

/**
 * Created by Bojidar on 3/27/2017.
 */

public class Forecast implements Serializable{
    private double temp ;
    private double tempF;
    private String sky;
    private double windSpeed;
    private double windSpeedMph;
    private String winDir;
    private int humidity ;
    private double pressure;
    private double pressureInch;
    private String date;
    private SerialBitmap icon;
    private SerialBitmap windIcon;
    private double visibility;
    private double vissibilityMiles;
    private double precip;
    private double precipInch;


    public Forecast(double temp,double tempF, String sky, double windSpeed, int humidity,double pressure,
                    String date,double visibility,double precip) {

        this.temp = temp;
        this.tempF=tempF;
        this.sky = sky;
        this.windSpeed = windSpeed;
        this.humidity = humidity;
        this.pressure = pressure;
        this.date=date;
        this.visibility=visibility;
        this.precip=precip;

    }

    public double getWindSpeedMph() {
        return windSpeedMph;
    }

    public double getPressureInch() {
        return pressureInch;
    }

    public double getVissibilityMiles() {
        return vissibilityMiles;
    }

    public double getPrecipInch() {
        return precipInch;
    }

    public void setWindSpeedMph(double windSpeedMph) {
        this.windSpeedMph = windSpeedMph;
    }

    public void setPressureInch(double pressureInch) {
        this.pressureInch = pressureInch;
    }

    public void setVissibilityMiles(double vissibilityMiles) {
        this.vissibilityMiles = vissibilityMiles;
    }

    public void setPrecipInch(double precipInch) {
        this.precipInch = precipInch;
    }

    public SerialBitmap getIcon() {
        return icon;
    }

    public void setIcon(SerialBitmap icon) {
        this.icon = icon;
    }

    public SerialBitmap getWindIcon() {
        return windIcon;
    }

    public void setWindIcon(SerialBitmap windIcon) {
        this.windIcon = windIcon;
    }

    public double getTemp() {
        return temp;
    }

    public String getSky() {
        return sky;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public String getWinDir() {
        return winDir;
    }

    public void setWinDir(String winDir) {
        this.winDir = winDir;
    }

    public int getHumidity() {
        return humidity;
    }

    public double getPressure() {
        return pressure;
    }

    public String getDate() {
        return date;
    }

    public double getTempF() {
        return tempF;
    }

    public double getVisibility() {
        return visibility;
    }

    public double getPrecip() {
        return precip;
    }

    public void print(){
        System.out.println("Date: "+this.date);
        System.out.println("Temperature: "+this.temp);
        System.out.println("Sky: "+this.sky);
        System.out.println("Wind Speed: "+this.windSpeed );
        System.out.println("Humidity: "+this.humidity );
        System.out.println("Pressure: "+this.pressure);

    }
}
