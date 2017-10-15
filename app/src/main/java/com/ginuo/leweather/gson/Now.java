package com.ginuo.leweather.gson;

import com.google.gson.annotations.SerializedName;



/**
 *当前天气温度和情况
 */
public class Now {
    @SerializedName("tmp")
    public String temperature;
    @SerializedName("cond")
    public More more;

    public class More {
        @SerializedName("code")
        public int code;
        @SerializedName("txt")
        public String info;
    }
}
