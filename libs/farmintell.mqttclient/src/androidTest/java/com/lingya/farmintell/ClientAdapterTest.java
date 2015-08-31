package com.lingya.farmintell;

import android.test.AndroidTestCase;

import retrofit.RestAdapter;
import retrofit.http.Field;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by zwq00000 on 15-8-22.
 */
public class ClientAdapterTest extends AndroidTestCase {

    public void setUp() throws Exception {
        super.setUp();

    }

    public void tearDown() throws Exception {

    }

    public void testRestInvok() throws Exception {
        final RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://api.openweathermap.org/data/2.5")
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();

        final WeatherApi weatherApi = restAdapter.create(WeatherApi.class);
        final WeatherData weatherData = weatherApi.getWeather("Tianjin,CN", "metric", "zh_cn");
        assertNotNull(weatherData);

        System.out.println("WeatherData:" + weatherData.toString());
    }

    public void testPostJson() throws Exception {
        RestAdapter adapter = new RestAdapter.Builder().setEndpoint("http://192.168.0.101:5512/ASCP/Test/")
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        FarmIntellWebApi api = adapter.create(FarmIntellWebApi.class);
        api.postSensorStatus("{status:\"121\"}");

    }

    interface FarmIntellWebApi {
        @POST("/Sensors")
        void postSensorStatus(@Field("value") String json);
    }

    /**
     * 天气API
     */
    private interface WeatherApi {
        @GET("/weather")
        WeatherData getWeather(@Query("q") String place, @Query("units") String units, @Query("lang") String lang);

        @GET("/weather")
        WeatherData getWeather(@Query("q") String place, @Query("units") String units);
    }

}