package com.ginuo.leweather.gson;

import com.google.gson.annotations.SerializedName;



public class Basic {
    @SerializedName("city") //城市名
    public String cityName;
    @SerializedName("id") //城市对应的天气ID
    public String weatherId;
    public Update update;

    //天气更新时间
    private class Update {
        @SerializedName("loc") //loc 表示天气的更新时间
        public String updateTime;
    }
}
