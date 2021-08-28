package com.example.weatherreport;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {
    private TextView humidity, placeName, maxTemp, minTemp, pressure, wind, sunrise, sunset, place, country, longitude, latitude, description;
    private ImageView weather;
    ArrayList<Weather> weatherForDisplay;
    FusedLocationProviderClient mFusedLocationClient;
    int PERMISSION_ID = 44;
    static String currentLatitude;
    static String currentLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(!(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)){
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);

        } else if(!(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))){
            Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        } else {
            getLastLocation();
        }

        humidity = findViewById(R.id.humidityTextView);
        maxTemp = findViewById(R.id.maxTempTextView);
        minTemp = findViewById(R.id.minTempTextView);
        pressure = findViewById(R.id.pressureTextView);
        wind = findViewById(R.id.windTextView);
        sunrise = findViewById(R.id.sunRiseTextView);
        sunset = findViewById(R.id.sunSetTextView);
        place = findViewById(R.id.placeTextView);
        placeName = findViewById(R.id.placeNameTextView);
        country = findViewById(R.id.countryTextView);
        longitude = findViewById(R.id.longitudeTextView);
        latitude = findViewById(R.id.latitudeTextView);
        description = findViewById(R.id.descriptionTextView);
        weather = findViewById(R.id.weatherImageView);
        wind = findViewById(R.id.windTextView);

//        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
//        boolean isConnected = activeNetwork != null &&
//                activeNetwork.isConnectedOrConnecting();
//        if (isConnected) {
//
//            Log.e(TAG, currentLatitude + " current latitude");
//            Log.e(TAG, currentLongitude + " current longitude");
//
//        } else {
//            TextView internet = (TextView) findViewById(R.id.noInternet);
//            internet.setTextSize(24);
//            internet.setTextColor(Color.parseColor("#00FFFF"));
//            internet.setVisibility(View.VISIBLE);
//        }
    }


    @SuppressLint("MissingPermission")
    private void getLastLocation() {

        mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (location == null) {
                    requestNewLocationData();
                } else {
                    currentLatitude = String.valueOf(location.getLatitude());
                    currentLongitude = String.valueOf(location.getLongitude());
                    WeatherAsyncTask task2 = new WeatherAsyncTask();
                    task2.execute();
                }
            }
        });

    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            currentLatitude = String.valueOf(mLastLocation.getLatitude());
            currentLongitude = String.valueOf(mLastLocation.getLongitude());
            WeatherAsyncTask task = new WeatherAsyncTask();
            task.execute();
        }
    };

    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    private class WeatherAsyncTask extends AsyncTask<URL, Void, List<Weather>> {

        @Override
        protected List<Weather> doInBackground(URL... urls) {

            List<Weather> weathers = QueryUtilis.extractWeather();
            return weathers;
        }

        @Override
        protected void onPostExecute(List<Weather> weathers) {
            View loading = findViewById(R.id.loading);
            loading.setVisibility(View.GONE);
            View weatherView = findViewById(R.id.weather);
            weatherView.setVisibility(View.VISIBLE);

            weatherForDisplay = (ArrayList<Weather>) weathers;

            if (weathers.size() > 0) {
                Weather currentWeather = weatherForDisplay.get(0);
                humidity.setText(currentWeather.getmHumidity() + " g/m");
                maxTemp.setText(currentWeather.getmTempMax()+ " C");
                minTemp.setText(currentWeather.getmTempMin() + " C");
                pressure.setText(currentWeather.getmPressure()+" Pa");
                String sunRise = "Not Available";
                String sunSet = "Not Available";
                java.util.Date time= new java.util.Date(Long.parseLong(currentWeather.getmSunRise())*1000);
                java.util.Date time1= new java.util.Date(Long.parseLong(currentWeather.getmSunSet())*1000);
                String sunRiseTime = String.valueOf(time);
                String sunSetTime = String.valueOf(time1);
                SimpleDateFormat formatter_from = new SimpleDateFormat("EEE MMM dd hh:mm:ss ZZ yyyy");
                SimpleDateFormat formatter_to = new SimpleDateFormat("hh:mm a");
                try {
                    java.util.Date d = formatter_from.parse(sunRiseTime);
                    sunRise = formatter_to.format(d);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                try {
                    java.util.Date d = formatter_from.parse(sunSetTime);
                    sunSet = formatter_to.format(d);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                sunrise.setText(sunRise);
                sunset.setText(sunSet);
                place.setText(currentWeather.getmPlaceName());
                placeName.setText(currentWeather.getmPlaceName());
                country.setText(currentWeather.getmCountry());
                latitude.setText(currentWeather.getmLatitude());
                longitude.setText(currentWeather.getmLongitude());
                description.setText(currentWeather.getmDescription());
                wind.setText(currentWeather.getmWindSpeed() + " m/s");
                if(currentWeather.getmImage()!=null){
                    weather.setImageBitmap(currentWeather.getmImage());
                }
            } else {
                Log.e(TAG, weathers + "Not found");
            }
        }
    }



}
