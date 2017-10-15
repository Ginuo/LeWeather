package com.ginuo.leweather.gson;

import com.google.gson.annotations.SerializedName;



    /**
     * 未来的天气信息
     */

public class Forecast {
    public String date;      //日期
    @SerializedName("tmp")   // 温度
    public Temperature temperature;
    @SerializedName("cond")  //天气情况
    public More more;


    //温度温度最大值和最小值
    public class Temperature {
        public String max;
        public String min;
    }

    public class More {
        @SerializedName("code_d")
        public int foreCode;
        @SerializedName("txt_d")
        public String info;
    }
}
