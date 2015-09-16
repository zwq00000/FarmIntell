package com.lingya.farmintell;

import android.test.AndroidTestCase;

import retrofit.RestAdapter;
import retrofit.http.Body;
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
        //"http://192.168.0.101:5512/ASCP/Test/"
        String serverAddress = "http://192.168.133.107/cchp.web";
        RestAdapter adapter = new RestAdapter.Builder().setEndpoint(serverAddress)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        FarmIntellWebApi api = adapter.create(FarmIntellWebApi.class);
        String json = "{\"key\":\"lao5mcKxNG\",\"hostId\":\"0001\",\"hostName\":\"1# 大棚\",\"type\":\"SensorStatus\",\"time\":\"\\/Date(1442391384834)\\/\",\"statuses\":[{\"id\":\"1-0\",\"name\":\"temp\",\"value\":\"5.8\"},{\"id\":\"1-1\",\"name\":\"hum\",\"value\":\"81.4\"},{\"id\":\"1-2\",\"name\":\"co2\",\"value\":\"4071\"},{\"id\":\"1-3\",\"name\":\"light\",\"value\":\"172890\"},{\"id\":\"2-0\",\"name\":\"soilWater\",\"value\":\"42.1\"},{\"id\":\"2-1\",\"name\":\"soilTemp\",\"value\":\"28.5\"}]}";

        String value = api.getValue();
        assertNotNull(value);

        api.postSensorStatus(json);

    }

    /**
     * WebApi 访问接口
     */
    interface FarmIntellWebApi {
        @POST("/api/Sensors")
        String postSensorStatus(@Body String json);

        @GET("/api/Sensors")
        String getValue();
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