package com.example.bojidar.weatherapp;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.bojidar.weatherapp.model.DayForecast;
import com.example.bojidar.weatherapp.model.Forecast;
import com.example.bojidar.weatherapp.model.Location;
import com.example.bojidar.weatherapp.model.SerialBitmap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import io.ghyeok.stickyswitch.widget.StickySwitch;

public class MainActivity extends AppCompatActivity {

    private AutoCompleteTextView enteredCity;
    private TextView cityTV;
    private TextView countryTV;
    private TextView dateTV;
    private TextView tempTV;
    private TextView humidityTV;
    private TextView windSpeedTV;
    private TextView pressureTV;
    private TextView conditionTV;
    private Button openHoursButton;
    private Button openDaysButton;
    private ImageView currentWeatherIV;
    private ImageView iconGPS;
    private Button clearTextButton;
    private ProgressBar progBar;
    private ImageView windDirIV;
    private TextView windDirTV;
    private StickySwitch switchUnits;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView visTV;
    private TextView precipTV;

    private Location location;
    public static boolean isfahrenheitEnabled=false;
    private boolean hasInternetAccess;
    private boolean isAsyncByGPSActive;
    private boolean isSearchClicked;
    private boolean isRefreshActivated;
    private List<String> suggestions;
    private ArrayAdapter adapter;
    private boolean isSearchStartedFromVoice;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.activity_main2);

        hasInternetAccess=false;
        isSearchClicked=false;
        isRefreshActivated=false;
        isAsyncByGPSActive=false;
        enteredCity = (AutoCompleteTextView) findViewById(R.id.input_city);
        cityTV = (TextView) findViewById(R.id.city_tv);
        countryTV = (TextView) findViewById(R.id.country_tv);
        dateTV = (TextView) findViewById(R.id.date_tv);
        tempTV = (TextView) findViewById(R.id.temp_tv);
        humidityTV = (TextView) findViewById(R.id.humidity_tv);
        windSpeedTV = (TextView) findViewById(R.id.wind_speed_tv);
        openDaysButton = (Button) findViewById(R.id.open_days_activity);
        openHoursButton = (Button) findViewById(R.id.open_hours_activity);
        currentWeatherIV = (ImageView) findViewById(R.id.current_weather_iv);
        progBar = (ProgressBar) findViewById(R.id.prog_bar);
        pressureTV = (TextView) findViewById(R.id.pressure_tv);
        conditionTV = (TextView) findViewById(R.id.condition_tv);
        iconGPS= (ImageView) findViewById(R.id.icon_gps_m_activity);
        clearTextButton= (Button) findViewById(R.id.clear_text_button);
        windDirIV= (ImageView) findViewById(R.id.wind_dir_icon_iv);
        windDirTV= (TextView) findViewById(R.id.wind_dir_tv);
        switchUnits= (StickySwitch) findViewById(R.id.sticky_switch);
        swipeRefreshLayout= (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        visTV= (TextView) findViewById(R.id.vis_tv);
        precipTV= (TextView) findViewById(R.id.precip_tv);


        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {

                        if(location!=null){
                            isSearchClicked=true;
                            isRefreshActivated=true;
                            checkStatusAndRun();
                            return;
                        }

                        swipeRefreshLayout.setRefreshing(false);

                    }
                }
        );

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.argb(0, 0, 0, 0)));
        getSupportActionBar().setTitle("");

        suggestions=new ArrayList<>();

        adapter = new
                ArrayAdapter(this,android.R.layout.simple_list_item_1,suggestions);
        enteredCity.setAdapter(adapter);
        enteredCity.setThreshold(3);



        enteredCity.addTextChangedListener(new TextWatcher() {

            Handler handler = new Handler(Looper.getMainLooper() /*UI thread*/);
            Runnable workRunnable;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {

                if(isSearchStartedFromVoice){
                    isSearchStartedFromVoice=false;
                    return;
                }

                handler.removeCallbacks(workRunnable);
                if (s.length() >= 3) {

                    workRunnable = new Runnable() {
                        @Override
                        public void run() {
                            startThreadForSuggestions(s.toString());
                        }
                    };
                    handler.postDelayed(workRunnable, 20 /*delay*/);
                }

            }


            private void startThreadForSuggestions(String s){

//                    MainActivity.DownloadSuggestionsAsync asl=new MainActivity.DownloadSuggestionsAsync();
//                    asl.execute("" +
//                            "https://maps.googleapis.com/maps/api/place/autocomplete/json?input="+s+"&type=(cities)&language=en&key=AIzaSyA5R_lMcTS5E9c8lI46EQEnvPg9XG0Wfao");
                      DownloadSuggestionsAsync downloadSuggestionsAsync =new DownloadSuggestionsAsync(s);
                      downloadSuggestionsAsync.execute("https://api.teleport.org/api/cities/?search=+"+s+"&limit=5");


            }

            @Override
            public void afterTextChanged(final Editable s) {

//                suggestions.clear();
//                adapter.notifyDataSetChanged();

                String input = s.toString();
                if (TextUtils.isEmpty(input)) {
//                    clearTextButton.setVisibility(View.GONE);
                      clearTextButton.setBackgroundResource(R.drawable.voice_search_icon);
                } else {
                      clearTextButton.setVisibility(View.VISIBLE);
                      clearTextButton.setBackgroundResource(android.R.drawable.ic_menu_close_clear_cancel);
                }
            }
        });



        enteredCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String location=(String)parent.getItemAtPosition(position);
                location=location.trim().replace(" ","%20");
                new DownloadAndParseTask().execute(location);
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

            }
        });



        switchUnits.setOnSelectedChangeListener(new StickySwitch.OnSelectedChangeListener() {
            @Override
            public void onSelectedChange(@NotNull StickySwitch.Direction direction, @NotNull String s) {
                isfahrenheitEnabled=direction.equals(StickySwitch.Direction.RIGHT)?true:false;
                if(location==null){
                    return;
                }
                if(isfahrenheitEnabled){
                    tempTV.setText(location.getCurrentWeather().getTempF()+" F");
                    windSpeedTV.setText("Wind Speed: "+location.getCurrentWeather().getWindSpeedMph()+" | mph");
                    visTV.setText("Visibility:"+location.getCurrentWeather().getVissibilityMiles()+" | miles");
                    pressureTV.setText("Pressure: "+location.getCurrentWeather().getPressureInch()+" | inch");
                    precipTV.setText("Precipitation:"+location.getCurrentWeather().getPrecipInch()+" | inch");
                }else{
                    tempTV.setText(location.getCurrentWeather().getTemp()+" C");
                    visTV.setText("Visibility:"+location.getCurrentWeather().getVisibility()+" | km");
                    windSpeedTV.setText("Wind Speed: "+location.getCurrentWeather().getWindSpeed()+" | kph");
                    pressureTV.setText("Pressure: "+location.getCurrentWeather().getPressure()+" | mb");
                    precipTV.setText("Precipitation:"+location.getCurrentWeather().getPrecip()+" | mm");
                }
            }
        });


        iconGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkStatusAndRun();
            }
        });

        enteredCity.setFilters(new InputFilter[] {
                new InputFilter() {
                    public CharSequence filter(CharSequence src, int start,
                                               int end, Spanned dst, int dstart, int dend) {
                        if(src.equals("")){ // for backspace
                            return src;
                        }
                        if(src.toString().matches("[a-zA-Z ']+")){
                            return src;
                        }
                        return "";
                    }
                },
                new InputFilter.LengthFilter(24)
        });

        openHoursButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, HoursActivity.class);
                if (location != null) {
                    intent.putExtra("loc", location);
                    startActivity(intent);
                } else {
                    showErrorOnView("Please enter city");
                }
            }
        });

        openDaysButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DaysActivity.class);
                if (location != null) {
                    intent.putExtra("loc", location);
                    startActivity(intent);
                } else {
                    showErrorOnView("Please enter city");
                }

            }
        });

        enteredCity.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if(TextUtils.isEmpty(enteredCity.getText().toString())){
                        showErrorOnView("Please enter a city");
                        return false;
                    }
                    isSearchClicked=true;
                    checkStatusAndRun();
                    View view = getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    enteredCity.dismissDropDown();
                    return true;
                }
                return false;
            }
        });

        clearTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isEmpty=TextUtils.isEmpty(enteredCity.getText().toString());
                if(isEmpty){
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    // Start the activity, the intent will be populated with the speech text
                    startActivityForResult(intent, 4);
                }else{
                    suggestions.clear();
                    adapter.notifyDataSetChanged();
                    enteredCity.setText("");
                    enteredCity.setError(null);
                    enteredCity.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(enteredCity, InputMethodManager.SHOW_IMPLICIT);
                }

            }
        });

        if (savedInstanceState != null) {
            location = (Location) savedInstanceState.getSerializable("loc");
        }

        if (location != null) return;

        checkStatusAndRun();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_items, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.option_history:
                Intent intent=new Intent(MainActivity.this,LocationsHistoryActivity.class);
                startActivityForResult(intent,2);
                return true;
            case R.id.option_feedback:
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto","sirakow23@gmail.com", null));
                startActivity(Intent.createChooser(emailIntent, "Send email to developer with.."));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void callAsyncTaskGPS() {
        isAsyncByGPSActive=true;
        GPSTracker gps;
        gps = new GPSTracker(MainActivity.this);
        double latitude = gps.getLatitude();
        double longitude = gps.getLongitude();
        String lati = String.valueOf(latitude);
        String longi = String.valueOf(longitude);

        DownloadAndParseTask okk=new DownloadAndParseTask();
        okk.execute(lati+","+longi);
    }

    public static void showNoConnectionDialog(Context ctx1)
    {
        final Context ctx = ctx1;
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setCancelable(true);
        builder.setMessage("Please turn on your network connection.");
        builder.setTitle("No network connection");
        builder.setNegativeButton("Mobile", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which)
            {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings$DataUsageSummaryActivity"));
                ctx.startActivity(intent);
            }
        });
        builder.setPositiveButton("Wifi", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ctx.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            }
        });

        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                return;
            }
        });

        builder.show();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    private  void checkStatusAndRun() {

        new AsyncTask<Void,Void,Void>() {
            @Override
            protected Void doInBackground(Void[] params) {
                if (isNetworkAvailable()) {
                    try {
                        HttpURLConnection urlc = (HttpURLConnection)
                                (new URL("http://clients3.google.com/generate_204")
                                        .openConnection());
                        urlc.setRequestProperty("User-Agent", "Android");
                        urlc.setRequestProperty("Connection", "close");
                        urlc.setConnectTimeout(10000);
                        urlc.connect();
                        hasInternetAccess= (urlc.getResponseCode() == 204 &&
                                urlc.getContentLength() == 0);
                    } catch (IOException e) {
                        Log.e("Network", "Error checking internet connection", e);
                    }
                } else {
                    hasInternetAccess=false;
                    Log.d("Network", "No network available!");
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                LocationManager manager = (LocationManager) MainActivity.this.getSystemService(Context.LOCATION_SERVICE);

                if(hasInternetAccess && isSearchClicked){
                    DownloadAndParseTask asyncTask = new DownloadAndParseTask();
                    isSearchClicked=false;

                    if(isRefreshActivated){
                        asyncTask.execute(location.getCityName());
                        isSearchClicked=false;
                        isRefreshActivated=false;
                        return;
                    }
                    String cityStr = enteredCity.getText().toString();
                    cityStr=cityStr.trim();
                    cityStr=cityStr.replace(" ","%20");
                    asyncTask.execute(cityStr);

                    return;
                }

                if ( manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) && hasInternetAccess ) {
                    callAsyncTaskGPS();
                }else{

                    if(!hasInternetAccess){
                        showNoConnectionDialog(MainActivity.this);
                    }
                    if(isRefreshActivated){
                        isRefreshActivated=false;
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    if(isSearchClicked){
                        isSearchClicked=false;
                        return ;
                    }

                    if(!manager.isProviderEnabled( LocationManager.GPS_PROVIDER )){
                        new GPSTracker(MainActivity.this).showSettingsAlert();
                    }
                }

            }
        }.execute();

    }

    private void showErrorOnView(final String errMsg){

        Handler handler = new Handler(Looper.getMainLooper() /*UI thread*/);
        Runnable hideErrorRunnable;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                enteredCity.setError(errMsg);
            }
        });

        hideErrorRunnable=new Runnable() {
            @Override
            public void run() {
                enteredCity.setError(null);
            }
        };

        handler.postDelayed(hideErrorRunnable,2500);

    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putSerializable("loc",location);
    }

    private String extractJSONfromURL(String urlStr){
        urlStr=urlStr.replace(" ","%20");
        URL url = null;
        try {
            url = new URL(urlStr);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection httpURLConnection=null;
        try {
            httpURLConnection= (HttpURLConnection) url.openConnection();

        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedReader in = null;
        StringBuilder strJSON=new StringBuilder("");
        try {
            in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                strJSON.append(inputLine);
            }
            if(in!=null) {
                in.close();
            }
            httpURLConnection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
            //showErrorOnView("Connection error");
            return null;
        }

        if(strJSON.toString().contains("error")){
            return null;
        }

        return strJSON.toString();
    }

    public class DownloadSuggestionsAsync extends AsyncTask<String,Void,List<String>>{

        private String text;
        DownloadSuggestionsAsync(String text){
            this.text=text;
        }

        @Override
        protected List<String> doInBackground(String... urlStrings) {
            String strJSON=extractJSONfromURL(urlStrings[0]);
            if(TextUtils.isEmpty(strJSON)){
                return null;
            }
            List<String> cities=new ArrayList<>();
            JSONObject rootJSON=null;
            try {
                rootJSON=new JSONObject(strJSON);
                JSONArray sugList=rootJSON.getJSONObject("_embedded").getJSONArray("city:search-results");

                for(int position=0;position<sugList.length();position++){
                    String sugCity=sugList.getJSONObject(position).getString("matching_full_name");
                    if(TextUtils.isEmpty(sugCity)){
                        continue;
                    }
                    if(sugCity.split(",").length>=3){
                        sugCity=sugCity.split(",")[0]+"," +
                                ""+sugCity.split(",")[2];
                    }
                    StringBuilder sb=new StringBuilder(sugCity);
                    int indexFirstSkoba = -1;
                    int indexSecondSkoba= -1;
                    for(int i=0;i<sugCity.length();i++){
                        if(sugCity.charAt(i)=='('){
                            indexFirstSkoba=i;
                        }
                        if(sugCity.charAt(i)==')'){
                            indexSecondSkoba=i;
                        }
                    }
                    if(indexFirstSkoba!=-1&&indexSecondSkoba!=-1){
                        sb.replace(indexFirstSkoba,indexSecondSkoba+1,"");
                        sugCity=sb.toString();
                    }

                    int count=0;

                    for(int i=0;i<sugCity.split(",")[0].length();i++){
                        for(int a=0;a<text.length();a++){
                            if(sugCity.split(",")[0].charAt(i)==text.charAt(a)){
                                count++;
                            }
                        }
                    }

                    if(count<2){
                        continue;
                    }

                    cities.add(sugCity);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            LinkedHashSet<String> hs = new LinkedHashSet<>();
            hs.addAll(cities);
            cities.clear();
            cities.addAll(hs);
            return cities;
        }
        @Override
        protected void onPostExecute(List<String> strings) {

            if(strings==null){
                swipeRefreshLayout.setRefreshing(false);
                return;
            }



            suggestions.clear();
            suggestions.addAll(strings);
            adapter = new
                    ArrayAdapter(MainActivity.this,android.R.layout.simple_list_item_1,suggestions);
            enteredCity.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

//    public class DownloadSuggestionsAsync extends  AsyncTask<String,Void,List<String> >{
//
//        @Override
//        protected List<String> doInBackground(String... urlStrings) {
//            String strJSON=extractJSONfromURL(urlStrings[0]);
//            if(TextUtils.isEmpty(strJSON)){
//                return null;
//            }
//            List<String> cities=new ArrayList<>();
//            JSONObject rootJSON=null;
//            try {
//                rootJSON=new JSONObject(strJSON);
//                JSONArray sugList=rootJSON.getJSONArray("predictions");
//
//                for(int position=0;position<sugList.length();position++){
//                    String sugCity=sugList.getJSONObject(position).getJSONArray("terms").getJSONObject(0).getString("value");
//                    String validSugCity=extractJSONfromURL("http://api.apixu.com/v1/current.json?key=3a625b0cf4ec4d6dbab00955170703&q="+sugCity);
//                    if(validSugCity==null){
//                        continue;
//                    }
//                    cities.add(sugCity);
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            LinkedHashSet<String> hs = new LinkedHashSet<>();
//            hs.addAll(cities);
//            cities.clear();
//            cities.addAll(hs);
//            return cities;
//        }
//
//        @Override
//        protected void onPostExecute(List<String> strings) {
//            if(strings==null){
//                swipeRefreshLayout.setRefreshing(false);
//                return;
//            }
//
//            suggestions.clear();
//            suggestions.addAll(strings);
//            adapter = new
//                    ArrayAdapter(MainActivity.this,android.R.layout.simple_list_item_1,suggestions);
//            enteredCity.setAdapter(adapter);
//            adapter.notifyDataSetChanged();
//        }
//    }

    public class DownloadAndParseTask extends AsyncTask<String,Void,Location>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MainActivity.this.findViewById(R.id.data_current_layout).setVisibility(View.INVISIBLE);
            currentWeatherIV.setVisibility(View.INVISIBLE);
            progBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Location doInBackground(String... params) {

            String strJSON=extractJSONfromURL("http://api.apixu.com/v1/forecast.json?key=fea90c1fe17f4d5c99e74340171909&q="+params[0]+"&days=10");

            if(TextUtils.isEmpty(strJSON)){
                return null;
            }

            String jcity="";
            String country="";
            String localTime="";
            String lastUpdated="";
            double temp=0;
            double tempF=0;
            String sky="";
            double windSpeed=0;
            double windSpeedMph=0;
            String windDir="";
            int humidity=0 ;
            double pressure=0;
            double pressureInch=0;
            String date="";
            String imgPath="l";
            double visibility=0;
            double visMiles=0;
            double precip=0;
            double precipInch=0;

            JSONObject obj= null;
            Location location=null;
            try {
                obj = new JSONObject(strJSON);

                jcity=obj.getJSONObject("location").getString("name");
                if(jcity.toLowerCase().contains("airport") ){
                    Log.e("API mistake", "API return false data ");
                    return null;
                }
                country=obj.getJSONObject("location").getString("country");
                localTime=obj.getJSONObject("location").getString("localtime");
                lastUpdated=obj.getJSONObject("current").getString("last_updated");
                temp = obj.getJSONObject("current").getDouble("temp_c");
                tempF= obj.getJSONObject("current").getDouble("temp_f");
                sky= obj.getJSONObject("current").getJSONObject("condition").getString("text");
                imgPath=obj.getJSONObject("current").getJSONObject("condition").getString("icon");
                windSpeed = obj.getJSONObject("current").getDouble("wind_kph");
                windSpeedMph=obj.getJSONObject("current").getDouble("wind_mph");
                windDir=obj.getJSONObject("current").getString("wind_dir");
                humidity = obj.getJSONObject("current").getInt("humidity");
                pressure = obj.getJSONObject("current").getInt("pressure_mb");
                pressureInch = obj.getJSONObject("current").getInt("pressure_in");
                visibility=obj.getJSONObject("current").getDouble("vis_km");
                visMiles=obj.getJSONObject("current").getDouble("vis_miles");
                precip=obj.getJSONObject("current").getDouble("precip_mm");
                precipInch=obj.getJSONObject("current").getDouble("precip_in");


                int windIconId=getResources().getIdentifier(windDir.toLowerCase(), "drawable", getPackageName());
                Bitmap icon = BitmapFactory.decodeResource(MainActivity.this.getResources(),
                        windIconId);

                SerialBitmap iconWind=new SerialBitmap(icon);

                location= new Location(jcity,country);
                location.setCurrentWeather(temp,tempF, sky, windSpeed,windDir,iconWind, humidity, pressure, lastUpdated,visibility,precip);
                location.getCurrentWeather().setWindSpeedMph(windSpeedMph);
                location.getCurrentWeather().setPressureInch(pressureInch);
                location.getCurrentWeather().setVissibilityMiles(visMiles);
                location.getCurrentWeather().setPrecipInch(precipInch);

            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }

            final Bitmap bitmap =Bitmap.createScaledBitmap(extractIcon(imgPath).bitmap,300,300,true);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    currentWeatherIV.setImageBitmap(bitmap);
                }
            });

            List<Forecast> forecastsHours=new ArrayList<Forecast>();
            List<Forecast> forecastsDays=new ArrayList<Forecast>();

            JSONArray jsonArrDays = null;
            try {
                jsonArrDays = obj.getJSONObject("forecast").getJSONArray("forecastday");
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }

            for(int a=0;a<jsonArrDays.length();a++) {

                JSONArray jsonArrHours = null;
                double maxTemp=0;
                double minTemp=0;
                double maxTempF=0;
                double minTempF=0;
                String sunrise="";
                String sunset="";
                try {

                    JSONObject day=jsonArrDays.getJSONObject(a).getJSONObject("day");

                    temp=day.getDouble("avgtemp_c");
                    tempF=day.getDouble("avgtemp_f");
                    sky=day.getJSONObject("condition").getString("text");
                    windSpeed=day.getDouble("maxwind_kph");
                    windSpeedMph=day.getDouble("maxwind_mph");
                    humidity= (int) day.getDouble("avghumidity");
                    date=jsonArrDays.getJSONObject(a).getString("date");
                    imgPath=day.getJSONObject("condition").getString("icon");
                    maxTemp=day.getDouble("maxtemp_c");
                    minTemp=day.getDouble("mintemp_c");
                    maxTempF=day.getDouble("maxtemp_f");
                    minTempF=day.getDouble("mintemp_f");
                    sunrise=jsonArrDays.getJSONObject(a).getJSONObject("astro").getString("sunrise");
                    sunset=jsonArrDays.getJSONObject(a).getJSONObject("astro").getString("sunset");
                    precip=day.getDouble("totalprecip_mm");
                    precipInch=day.getDouble("totalprecip_in");
                    visibility=day.getDouble("avgvis_km");
                    visMiles=day.getDouble("avgvis_miles");

//                    jsonArrHours = jsonArrDays.getJSONObject(a).getJSONArray("hour");

                    DayForecast forecastDay = new DayForecast(temp,tempF, sky, windSpeed, humidity, 0, date,visibility,precip,
                            minTemp,maxTemp,minTempF,maxTempF,sunrise,sunset);
                    forecastDay.setWindSpeedMph(windSpeedMph);
                    forecastDay.setPressureInch(pressureInch);
                    forecastDay.setPrecipInch(precipInch);
                    forecastDay.setVissibilityMiles(visMiles);
                    forecastDay.setIcon(extractIcon(imgPath));
                    forecastsDays.add(forecastDay);

                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                }
//                if(a<2) {
//                    for (int i = 0; i < jsonArrHours.length(); i++) {
//
//                        try {
//                            temp = jsonArrHours.getJSONObject(i).getDouble("temp_c");
//                            tempF=jsonArrHours.getJSONObject(i).getDouble("temp_f");
//                            sky = jsonArrHours.getJSONObject(i).getJSONObject("condition").getString("text");
//                            windSpeed = jsonArrHours.getJSONObject(i).getDouble("wind_kph");
//                            windSpeedMph = jsonArrHours.getJSONObject(i).getDouble("wind_mph");
//                            humidity = jsonArrHours.getJSONObject(i).getInt("humidity");
//                            pressure = jsonArrHours.getJSONObject(i).getInt("pressure_mb");
//                            pressureInch = jsonArrHours.getJSONObject(i).getInt("pressure_in");
//                            date = jsonArrHours.getJSONObject(i).getString("time");
//                            imgPath=jsonArrHours.getJSONObject(i).getJSONObject("condition").getString("icon");
//                            visibility=jsonArrHours.getJSONObject(i).getDouble("vis_km");
//                            visMiles=jsonArrHours.getJSONObject(i).getDouble("vis_miles");
//                            precip=jsonArrHours.getJSONObject(i).getDouble("precip_mm");
//                            precipInch=jsonArrHours.getJSONObject(i).getDouble("precip_in");
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                            return null;
//                        }
//
//                        Forecast forecastHour = new Forecast(temp,tempF, sky, windSpeed, humidity, pressure, date,visibility,precip);
//
//                        forecastHour.setWindSpeedMph(windSpeedMph);
//                        forecastHour.setPressureInch(pressureInch);
//                        forecastHour.setPrecipInch(precipInch);
//                        forecastHour.setVissibilityMiles(visMiles);
//
//                        forecastHour.setIcon(extractIcon(imgPath));
//
//                        forecastsHours.add(forecastHour);
//                    }
//                }

            }

//            SimpleDateFormat sdf = new SimpleDateFormat("dd HH");
//            String[] dateSplit=location.getCurrentWeather().getDate().split(" ");
//            String currDate = dateSplit[0].charAt(dateSplit[0].length()-2)+""+dateSplit[0].charAt(dateSplit[0].length()-1)
//                    +" "+dateSplit[1].charAt(0)+dateSplit[1].charAt(1);
//
//            int indexOfNextHour=0;
//            for(int i=0;i<forecastsHours.size();i++){
//                if(forecastsHours.get(i).getDate().contains(currDate )){
//                    indexOfNextHour=i;
//                    break;
//                }
//
//            }
//            while(indexOfNextHour>=0){
//                forecastsHours.remove(0);
//                --indexOfNextHour;
//                System.out.print("["+indexOfNextHour+"] ");
//            }
//            while(forecastsHours.size()>24){
//                forecastsHours.remove(forecastsHours.size()-1);
//            }

            location.setForecastsDays(forecastsDays);
//            location.setforecastsHours(forecastsHours);
            MainActivity.this.location=location;

            return location;
        }

        @Override
        protected void onPostExecute(Location location) {

            progBar.setVisibility(View.GONE);
            MainActivity.this.findViewById(R.id.data_current_layout).setVisibility(View.VISIBLE);
            currentWeatherIV.setVisibility(View.VISIBLE);

            if(location==null){
                swipeRefreshLayout.setRefreshing(false);
                if(isAsyncByGPSActive){
                    showErrorOnView("GPS can't find accurate coordinates");
                    isAsyncByGPSActive=false;
                    return;
                }
                showErrorOnView("Invalid City, Please try again");
                return;
            }

            cityTV.setText(location.getCityName());
            countryTV.setText(location.getCountry());
            dateTV.setText(location.getCurrentWeather().getDate());
            humidityTV.setText("Humidity: "+location.getCurrentWeather().getHumidity()+" %");
            conditionTV.setText("Condition: "+location.getCurrentWeather().getSky());
            windDirTV.setText("Wind Dir:  "+location.getCurrentWeather().getWinDir());
            windDirIV.setImageBitmap(location.getCurrentWeather().getWindIcon().bitmap);

            if(isfahrenheitEnabled){
                tempTV.setText(location.getCurrentWeather().getTempF()+ " F");
                windSpeedTV.setText("Wind Speed: "+location.getCurrentWeather().getWindSpeedMph()+" | mph");
                pressureTV.setText("Pressure: "+location.getCurrentWeather().getPressureInch()+" | inch");
                visTV.setText("Visibility:"+location.getCurrentWeather().getVissibilityMiles()+" | miles");
                precipTV.setText("Precipitation:"+location.getCurrentWeather().getPrecipInch()+" | inch");
            }else{
                tempTV.setText(location.getCurrentWeather().getTemp()+ " C");
                windSpeedTV.setText("Wind Speed: "+location.getCurrentWeather().getWindSpeed()+" | kph");
                pressureTV.setText("Pressure: "+location.getCurrentWeather().getPressure()+" | mb");
                visTV.setText("Visibility:"+location.getCurrentWeather().getVisibility()+" | km");
                precipTV.setText("Precipitation:"+location.getCurrentWeather().getPrecip()+" | mm");
            }

            swipeRefreshLayout.setRefreshing(false);
            isAsyncByGPSActive=false;

            SharedPreferences share= getSharedPreferences("locations_history",MODE_PRIVATE);
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

            if(locArrayList.contains(location.getCityName())){
                locArrayList.remove(location.getCityName());
            }
            locArrayList.add(location.getCityName());

            json = gson.toJson(locArrayList);
            editor.putString("loc_arr", json);
            editor.commit();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==3){
            if(data!=null) {
                DownloadAndParseTask async = new DownloadAndParseTask();
                async.execute(data.getStringExtra("loc_his"));
            }
        }
        if(requestCode==4&&resultCode==RESULT_OK){
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            isSearchStartedFromVoice=true;
            isSearchClicked=true;
            enteredCity.setText(spokenText);
            checkStatusAndRun();
            // Do something with spokenText
        }
    }

    public SerialBitmap extractIcon(String imgPath){
        String uri = "";
        String[] strArrIcon = imgPath.split("/");

        uri=strArrIcon[strArrIcon.length-2]+"/"+strArrIcon[strArrIcon.length-1];

        InputStream is = null;
        try {
            is = MainActivity.this.getResources().getAssets().open(uri);
        } catch (IOException e) {

        }

        Drawable imgdraw = Drawable.createFromStream(is, null);
        Bitmap bitmapImg = Bitmap.createScaledBitmap(((BitmapDrawable) imgdraw).getBitmap(), 90, 90, true);
        SerialBitmap serialBitmap = new SerialBitmap(bitmapImg);
        return serialBitmap;
    }
}
