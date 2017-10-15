package com.ginuo.leweather;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ginuo.leweather.gson.Forecast;
import com.ginuo.leweather.gson.HourForecast;
import com.ginuo.leweather.gson.Weather;
import com.ginuo.leweather.util.Common;
import com.ginuo.leweather.service.AutoUpdateService;
import com.ginuo.leweather.util.HttpUtil;
import com.ginuo.leweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class WeatherFragment extends Fragment {
    public SwipeRefreshLayout swipeRefresh;
    private ScrollView scrWeatherLayout;
    private TextView tempText;
    private ImageView imgWeather;
    private TextView weatherInfoText;
    private TextView airQlty;
    private TextView pm25Text;
    private TextView airBrf;
    private TextView comfBrf;
    private TextView fluBrf;
    private TextView drsgBrf;
    private TextView aqiText;
    private TextView airTxt;
    private TextView comfortTxt;
    private TextView influenzaTxt;
    private TextView dressTxt;
    private LinearLayout hourforecastLayout;
    private LinearLayout forecastLayout;
    private Toolbar mToolbar;
    private String weatherId;

    /**
     * Framgment生命周期的第一个回调
     * 在刚创建时Fragment时调用
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        //得到宿主Activity的ToolBar
        if (context instanceof MainActivity){
            MainActivity mainActivity = (MainActivity) context;
            mToolbar= (Toolbar) mainActivity.findViewById(R.id.toolbar);
        }
        Log.d("LifeCycle","WeatherFragment_onAttach");

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("LifeCycle","WeatherFragment_onCreate");
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.activity_weather,container,false);
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout);
        scrWeatherLayout = (ScrollView)view.findViewById(R.id.weather_scrollView);
        tempText = (TextView) view.findViewById(R.id.temp);      //显示温度
        imgWeather= (ImageView)view.findViewById(R.id.img_cond);   //显示天气图片
        weatherInfoText = (TextView)view.findViewById(R.id.weather_info);    //天气说明（阵雨、多云等）
        hourforecastLayout= (LinearLayout)view.findViewById(R.id.hour_layout);    //24小时预报布局
        forecastLayout = (LinearLayout)view.findViewById(R.id.forecast_layout);    //三天预报布局
        aqiText = (TextView)view.findViewById(R.id.aqi_text);  //AQI指数   不变
        airQlty= (TextView)view.findViewById(R.id.airQlty);    //AQI值
        pm25Text = (TextView)view.findViewById(R.id.pm2_5);   //PM2.5值
        airBrf = (TextView)view.findViewById(R.id.sug_air);  //item_suggestion中的“空气指数---”文本
        comfBrf= (TextView)view.findViewById(R.id.sug_comf);   //舒适指数---
        fluBrf= (TextView)view.findViewById(R.id.sug_flu);    //感冒指数
        drsgBrf= (TextView)view.findViewById(R.id.sug_drsg);
        airTxt = (TextView)view.findViewById(R.id.air_txt);
        comfortTxt = (TextView)view.findViewById(R.id.comf_txt);
        influenzaTxt = (TextView)view.findViewById(R.id.flu_txt);
        dressTxt = (TextView)view.findViewById(R.id.drsg_txt);

        //获取传入的weather_id
        weatherId = (String) getArguments().get("weather_id");
            scrWeatherLayout.setVisibility(View.INVISIBLE);
            if (weatherId !=null) {
                requestWeather(weatherId);

            }

        //刷新控件的实现
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if (weatherId!=null){
                        requestWeather(weatherId);
                        //Toast.makeText(getActivity(), "更新成功( •̀ .̫ •́ )✧", Toast.LENGTH_SHORT).show();
                    }else{
                        Log.d("LifeCycle","swipeWeatherId is null");
                    }

                }
            });
        Log.d("LifeCycle","WeatherFragment_onCreateView");
        return view;
    }

    //从宿主Activity所接收到的Intent中获取Weather_id，恢复天气
    //其实应该也可以从SharedPreferenced中获得上一次的响应数据，以恢复天气，避免重复请求
    @Override
    public void onResume() {
        super.onResume();
        String weatherId = getActivity().getIntent().getStringExtra("weather_id");
        if (weatherId!=null){
            requestWeather(weatherId);
        }else {
            Log.d("LifeCycle","WeatherId is null");
        }
        Log.d("LifeCycle","WeatherFragment_onResume");
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        weatherId=null;
        Log.d("LifeCycle","WeatherFragment_onDetach");

    }

    //负责刷新显示UI
    private void showWeatherInfo(Weather weather) {
        String cityName = weather.basic.cityName;
        String temp = weather.now.temperature + "℃";
        String weatherInfo = weather.now.more.info;
        int imgCode = weather.now.more.code;

        //设置通知栏所需要的天气信息存储
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("notification",Context.MODE_PRIVATE).edit();
        editor.putString("cityName", cityName);
        editor.putString("temperature",temp);
        editor.putString("weatherInfo",weatherInfo);
        editor.apply();
        tempText.setText(temp);
        mToolbar.setTitle(cityName);
        weatherInfoText.setText(weatherInfo);
        //设置天气信息的相应Icon

        if (imgCode == 100) {
            imgWeather.setImageResource(R.mipmap.sun);
        }
        if (imgCode >= 101 && imgCode <= 103){
            imgWeather.setImageResource(R.mipmap.cloudy);
        }
        if (imgCode==104){
            imgWeather.setImageResource(R.mipmap.overcast);
        }
        if (imgCode >= 200 && imgCode <= 213){
            imgWeather.setImageResource(R.mipmap.windy);
        }
        if (imgCode >= 300 && imgCode <= 313 ){
            imgWeather.setImageResource(R.mipmap.rain);
        }
        if (imgCode >= 400 && imgCode <= 407){
            imgWeather.setImageResource(R.mipmap.snow);
        }
        if (imgCode >= 500 && imgCode <= 501){
            imgWeather.setImageResource(R.mipmap.fog);
        }
        if (imgCode == 502){
            imgWeather.setImageResource(R.mipmap.haze);
        }
        if (imgCode >= 503 && imgCode <= 508){
            imgWeather.setImageResource(R.mipmap.sandstorm);
        }

        //先清空hourforecastLayout上的所有控件（24小时天气）
        hourforecastLayout.removeAllViews();
        //逐行动态加载未来小时的天气信息
        for (HourForecast hourForecast:weather.hourForecastList){
            //重新加载控件
            View viewHour =LayoutInflater.from(getActivity()).inflate(R.layout.item_hour,hourforecastLayout,false);
            TextView hourText = (TextView) viewHour.findViewById(R.id.hour_clock);
            TextView tmpText = (TextView) viewHour.findViewById(R.id.hour_temp);
            TextView humText = (TextView) viewHour.findViewById(R.id.hour_hum);
            TextView windText = (TextView) viewHour.findViewById(R.id.hour_wind);
            //去年月日保留时间
            String Hour = hourForecast.date;
            String hourWeather = Common.getHour(Hour);
            hourText.setText(hourWeather);                //时间
            tmpText.setText(hourForecast.tmp+"℃");       //温度
            humText.setText(hourForecast.hum+"%");        //湿度
            windText.setText(hourForecast.wind.spd+"Km/h");  //风速

            //※※记得addView()，将行布局添加到hourforecastLayout中
            hourforecastLayout.addView(viewHour);
        }

        //同样，清除forecastLayout(未来几天预报)的所有子控件，之后再动态加载
        forecastLayout.removeAllViews();
        //动态加载未来几天的天气信息
        for (Forecast forecast : weather.forecastsList){
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_forecast,forecastLayout,false);
            TextView dateText= (TextView) view.findViewById(R.id.date_Text);
            TextView infoText = (TextView) view.findViewById(R.id.info_Text);
            ImageView forecastImg = (ImageView) view.findViewById(R.id.foreDayWeather);
            TextView minText= (TextView) view.findViewById(R.id.min_Text);
            TextView maxText= (TextView) view.findViewById(R.id.max_Text);

            String WeatherDate=forecast.date;
            //返回JSON数据中的日期转换成星期
            String weekDate = Common.getWeek(WeatherDate);
            dateText.setText(weekDate);

            int foreCode  = forecast.more.foreCode;
            //设置天气信息的相应Icon

            if (foreCode >= 100 && foreCode <= 103  ){
                forecastImg.setImageResource(R.mipmap.foredaysun);
            }
            if ((foreCode >= 104 && foreCode <= 213)||(foreCode >= 500 && foreCode <= 502)){
                forecastImg.setImageResource(R.mipmap.foredaycloud);
            }
            if (foreCode >= 300 && foreCode <= 313 ){
                forecastImg.setImageResource(R.mipmap.foredayrain);
            }
            if (foreCode >= 400 && foreCode <=407 ){
                forecastImg.setImageResource(R.mipmap.foredaysnow);
            }
            if (foreCode >=503 && foreCode <= 508){
                forecastImg.setImageResource(R.mipmap.foredaysand);
            }
            infoText.setText(forecast.more.info);
            minText.setText(forecast.temperature.min+"℃");
            maxText.setText(forecast.temperature.max+"℃");
            forecastLayout.addView(view);
        }

        if (weather.aqi !=null){
            airQlty.setText("："+weather.aqi.city.qlty);
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }
        String airbrt = "空气指数---"+weather.suggestion.air.info;
        String comfbrf = "舒适指数---"+weather.suggestion.comfort.info;
        String flubrf = "感冒指数---"+weather.suggestion.flu.info;
        String drsgbrf = "穿衣指数---"+weather.suggestion.drsg.info;
        String airtxt = weather.suggestion.air.infotxt;
        String comforttxt =weather.suggestion.comfort.infotxt;
        String influenzatxt =weather.suggestion.flu.infotxt;
        String dresstxt=weather.suggestion.drsg.infotxt;
        airBrf.setText(airbrt);
        comfBrf.setText(comfbrf);
        fluBrf.setText(flubrf);
        drsgBrf.setText(drsgbrf);
        airTxt.setText(airtxt);
        comfortTxt.setText(comforttxt);
        influenzaTxt.setText(influenzatxt);
        dressTxt.setText(dresstxt);
        scrWeatherLayout.setVisibility(View.VISIBLE);
    }

    /**   核心方法
     * 在requestWeather()方法中调用了sendOkHttpRequest()方法向服务器请求数据
     * 相应的数据会回调到onResponse()方法中 
     */
    public void requestWeather( String weatherId) {
        //构造请求
        String weatherUrl = "https://free-api.heweather.com/v5/weather?city="+weatherId+"&key=d8bccdec73e8474584b269a49cd150a0";
        Log.i("天气详情", weatherUrl);
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "一不小心获取失败了Q_Q ", Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            /**在onResponse()方法中 调用Utility.handleWeatherResponse()进行数据解析和处理
             *请求成功后把数据储存
             */
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("status", "status");
                        if (weather != null && "ok".equals(weather.status)) {
                            Toast.makeText(getActivity(), "更新成功( •̀ .̫ •́ )✧", Toast.LENGTH_SHORT).show();

                            //直接存储responseText（响应字符串）到SharedPreferences中
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            showWeatherInfo(weather);        //显示到UI上

                            //启动启动更新天气服务
                            Intent intentAutoService = new Intent(getActivity(), AutoUpdateService.class);
                            getActivity().startService(intentAutoService);
                        } else {
                            Toast.makeText(getActivity(), "后台获取天气失败( >﹏< )", Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
    }

}
