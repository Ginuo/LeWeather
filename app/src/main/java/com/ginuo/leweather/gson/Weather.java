package com.ginuo.leweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 *建立Weather类 实现对Aqi Basic Now Suggestion 类的引用(由于hourly_forecast
 *daily_forecast 包含数组 所以用List集合来引用HourForecast和Forecast类的引用 )
 */
public class Weather {
    public String status;
    public Aqi aqi;
    public Basic basic;
    public Now now;
    public Suggestion suggestion;
    @SerializedName("hourly_forecast")        //指定该字段在Json中对应的字段名称
    public List<HourForecast> hourForecastList;
    @SerializedName("daily_forecast")
    public List<Forecast> forecastsList;
}
