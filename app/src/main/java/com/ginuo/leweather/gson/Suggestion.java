package com.ginuo.leweather.gson;

import com.google.gson.annotations.SerializedName;



/**
 *天气指数的生活建议包括(空气，舒适，感冒，穿衣)
 */

public class Suggestion {
    @SerializedName("air")
    public Air air;
    @SerializedName("comf")
    public Comfort comfort;
    @SerializedName("flu")
    public Influenza flu;
    public Dress drsg;
    public class Air {
        @SerializedName("brf")
        public String info;
        @SerializedName("txt")
        public String infotxt;
    }
    public class Comfort {
        @SerializedName("brf")
        public String info;
        @SerializedName("txt")
        public String infotxt;
    }
    public class Influenza {
        @SerializedName("brf")
        public String info;
        @SerializedName("txt")
        public String infotxt;
    }
    public class Dress {
        @SerializedName("brf")
        public String info;
        @SerializedName("txt")
        public String infotxt;
    }



}
