package com.example.bojidar.weatherapp.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bojidar.weatherapp.MainActivity;
import com.example.bojidar.weatherapp.R;

import java.util.List;



public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.HoursViewHolder> {

    class HoursViewHolder  extends RecyclerView.ViewHolder{

        TextView tempTV;
        TextView hourTV;
        ImageView iconIV;

        public HoursViewHolder(View row) {
            super(row);
            this.tempTV= (TextView) row.findViewById(R.id.temp_tv);
            this.hourTV= (TextView) row.findViewById(R.id.hour_tv);
            this.iconIV= (ImageView) row.findViewById(R.id.icon_iv);
        }
    }

    private Context context;
    private List<Forecast> forecastList;

    public ForecastAdapter(Context context, List hours) {
        this.context=context;
        this.forecastList =hours;

    }

    @Override
    public HoursViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View row = inflater.inflate(R.layout.activity_row, parent, false);
        HoursViewHolder vh=new HoursViewHolder(row);
        return vh;
    }

    @Override
    public void onBindViewHolder(final HoursViewHolder holder, final int position) {

        final Forecast forecast=forecastList.get(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
                View mView = LayoutInflater.from(context).inflate(R.layout.days_alert_dialog, null);

                TextView dateTV= (TextView) mView.findViewById(R.id.date_alert_dialog_tv);
                TextView tempTV= (TextView) mView.findViewById(R.id.temp_alert_dialog_tv);
                TextView windSpeedTV= (TextView) mView.findViewById(R.id.wind_speed_alert_dialog_tv);
                TextView humidityTV= (TextView) mView.findViewById(R.id.humidity_alert_dialog_tv);
                TextView conditionTV= (TextView) mView.findViewById(R.id.condition_alert_dialog_tv);
                TextView visTV= (TextView) mView.findViewById(R.id.vis_alert_dialog_tv);
                TextView precipTV= (TextView) mView.findViewById(R.id.precip_alert_dialog_tv);
                ImageView iconIV= (ImageView) mView.findViewById(R.id.icon_alert_dialog_tv);

                dateTV.setText("Date: "+ forecast.getDate().substring(5));
                if(MainActivity.isfahrenheitEnabled){
                    tempTV.setText("Temp: "+ forecast.getTemp()+"| F");
                    windSpeedTV.setText("Wind speed: "+ forecast.getWindSpeedMph()+" | mph");
                    visTV.setText("Visibility: "+forecast.getVissibilityMiles()+" | miles");
                    precipTV.setText("Precipitation: "+forecast.getPrecipInch()+" | inch");
                }else{
                    tempTV.setText("Temp: "+ forecast.getTemp()+" C");
                    windSpeedTV.setText("Wind speed: "+ forecast.getWindSpeed()+" | kph");
                    visTV.setText("Visibility: "+forecast.getVisibility()+" | km");
                    precipTV.setText("Precipitation: "+forecast.getPrecip()+" | mm");
                }

                humidityTV.setText("Humidity: "+ forecast.getHumidity()+" %");
                conditionTV.setText("Condition: "+ forecast.getSky());

                iconIV.setImageBitmap(Bitmap.createScaledBitmap(forecast.getIcon().bitmap,150,150,true));

                if(forecastList.get(position) instanceof DayForecast){

                    DayForecast dayForecast = (DayForecast) forecast;

                    TextView maxTempTV= (TextView) mView.findViewById(R.id.max_temp_alert_dialog_tv);
                    TextView minTempTV= (TextView) mView.findViewById(R.id.min_temp_alert_dialog_tv);
                    TextView sunriseTV= (TextView) mView.findViewById(R.id.sunrise_alert_dialog_tv);
                    TextView sunsetTV= (TextView) mView.findViewById(R.id.sunset_alert_dialog_tv);

                    if(MainActivity.isfahrenheitEnabled){
                        maxTempTV.setText("Max temp: "+dayForecast.getMaxTempF()+" | F");
                        minTempTV.setText("Min temp: "+dayForecast.getMinTempF()+" | F");
                    }else{
                        maxTempTV.setText("Max temp: "+dayForecast.getMaxTemp()+" | C");
                        minTempTV.setText("Min temp: "+dayForecast.getMinTemp()+" | C");
                    }
                    sunriseTV.setText("Sunrise: "+dayForecast.getSunrise());
                    sunsetTV.setText("Sunset: "+dayForecast.getSunset());

                    minTempTV.setVisibility(View.VISIBLE);
                    maxTempTV.setVisibility(View.VISIBLE);
                    sunriseTV.setVisibility(View.VISIBLE);
                    sunsetTV.setVisibility(View.VISIBLE);

                    if(MainActivity.isfahrenheitEnabled){
                        tempTV.setText("Avg Temp: "+ forecast.getTempF()+" | F");
                    }else{
                        tempTV.setText("Avg Temp: "+ forecast.getTemp()+" | C");
                    }

                }
                mBuilder.setView(mView);
                AlertDialog dialog = mBuilder.create();
                dialog.show();
            }
        });

        if(forecast instanceof DayForecast){
            DayForecast dayForecast = (DayForecast) forecast;
            int minT= 0;
            int maxT= 0;
            if(MainActivity.isfahrenheitEnabled){
                 minT= (int) Math.round(dayForecast.getMinTempF());
                 maxT= (int) Math.round(dayForecast.getMaxTempF());
                holder.tempTV.setText(minT+" | "+ maxT+" F");
            }else{
                minT= (int) Math.round(dayForecast.getMinTemp());
                maxT= (int) Math.round(dayForecast.getMaxTemp());
                 holder.tempTV.setText(minT+" | "+ maxT+" C");
            }
        }else{
            if(MainActivity.isfahrenheitEnabled){
                holder.tempTV.setText(forecast.getTempF()+" F");
            }else{
                holder.tempTV.setText(forecast.getTemp()+" C");
            }

        }
        holder.hourTV.setText(forecast.getDate().substring(5));
        if(forecast.getIcon()!=null) {
            holder.iconIV.setImageBitmap(forecast.getIcon().bitmap);
        }
    }

    @Override
    public int getItemCount() {
        return forecastList.size();
    }
}
