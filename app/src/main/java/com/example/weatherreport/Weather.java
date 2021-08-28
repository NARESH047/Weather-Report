package com.example.weatherreport;


import android.graphics.Bitmap;

public class Weather {

    private String mTempMin, mTempMax;

    private String mPressure;

    private String mHumidity;

    private String mSunRise, mSunSet;

    private String mWindSpeed;

    private String mLatitude, mLongitude;

    private String mPlaceName, mDescription, mCountry;

    private Bitmap mImage;


    public Weather(String placeName, String country, String description, String latitude, String longitude, String tempMin, String tempMax, String pressure, String humidity, String sunRise, String sunSet, String windSpeed, Bitmap image) {
        mPlaceName = placeName;
        mCountry = country;
        mDescription = description;
        mLongitude = longitude;
        mLatitude = latitude;
        mTempMin = tempMin;
        mTempMax = tempMax;
        mHumidity = humidity;
        mPressure = pressure;
        mSunRise =sunRise;
        mSunSet = sunSet;
        mImage = image;
        mWindSpeed = windSpeed;
    }

    public String getmTempMin() {
        return mTempMin;
    }

    public String getmTempMax() {
        return mTempMax;
    }

    public String getmPressure() {
        return mPressure;
    }

    public String getmHumidity() {
        return mHumidity;
    }

    public String getmPlaceName() {
        return mPlaceName;
    }

    public String getmSunRise() {
        return mSunRise;
    }

    public String getmSunSet() {
        return mSunSet;
    }

    public String getmLongitude() {
        return mLongitude;
    }

    public String getmLatitude() {
        return mLatitude;
    }

    public String getmDescription() {
        return mDescription;
    }

    public Bitmap getmImage() {
        return mImage;
    }

    public String getmCountry(){
        return mCountry;
    }

    public String getmWindSpeed(){
        return mWindSpeed;
    }
}
