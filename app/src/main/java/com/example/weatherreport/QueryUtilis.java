package com.example.weatherreport;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public final class QueryUtilis {
    static String  CURRENT_LATITUDE = MainActivity.currentLatitude;
    static String CURRENT_LONGITUDE = MainActivity.currentLongitude;

    public static final String WEATHER_REQUEST_URL =  "https://api.openweathermap.org/data/2.5/weather?lat="+CURRENT_LATITUDE+"&lon="+CURRENT_LONGITUDE+"&units=metric&appid=418df8e9b00c2c4be4b0be8e0d06f636";

    public static ArrayList<Weather> extractWeather() {

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(createUrl(WEATHER_REQUEST_URL));
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<Weather> weathers = new ArrayList<>();

        try {

            JSONObject baseJsonResponse = new JSONObject(jsonResponse);

            JSONObject coord = baseJsonResponse.optJSONObject("coord");
            String longitude = String.valueOf(coord.optInt("lon"));
            String latitude = String.valueOf(coord.optInt("lat"));

            JSONArray weatherConditionArray = baseJsonResponse.optJSONArray("weather");
            JSONObject weatherConditionObject = weatherConditionArray.optJSONObject(0);
            String  weatherDescription = weatherConditionObject.optString("description");
            String iconId = weatherConditionObject.optString("icon");

            JSONObject weatherData = baseJsonResponse.optJSONObject("main");
            String averageTemp = String.valueOf(weatherData.optInt("temp"));
            String tempMin = String.valueOf(weatherData.optInt("temp_min"));
            String tempMax = String.valueOf(weatherData.optInt("temp_max"));
            String pressure = String.valueOf(weatherData.optInt("pressure"));
            String humidity = String.valueOf(weatherData.optInt("humidity"));

            JSONObject windObject = baseJsonResponse.optJSONObject("wind");
            String speed = String.valueOf(windObject.optInt("speed"));

            JSONObject sys = baseJsonResponse.optJSONObject("sys");
            String country = sys.optString("country");
            String sunRise = String.valueOf(sys.optLong("sunrise"));
            String sunSet = String.valueOf(sys.optLong("sunset"));

            String placeName = baseJsonResponse.optString("name");

            URL imageUrl = createUrl("http://openweathermap.org/img/wn/"+iconId+"@2x.png");
            Bitmap icon = getImage(imageUrl);

            Weather weather = new Weather(placeName, country, weatherDescription, latitude, longitude, tempMin, tempMax, pressure, humidity, sunRise, sunSet, speed, icon);

            weathers.add(weather);

        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the JSON results", e);
        }

        return weathers;
    }

    static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            return null;
        }
        return url;
    }


    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        if(url==null){
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 );
            urlConnection.setConnectTimeout(15000 );
            urlConnection.connect();
            int code = urlConnection.getResponseCode();
            if(code==200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }

        } catch (IOException e) {
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    public static Bitmap getImage(URL imageUrl){
        Bitmap bmp = null;
        try {
            bmp = BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream());
            return bmp;
        } catch (IOException e) {
            e.printStackTrace();
        }
      return bmp;
    }

}

