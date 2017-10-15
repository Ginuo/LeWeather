package com.ginuo.leweather.gson;



/**
  * aqi 表示aqi指数 
  * pm25 表示pm2.5指数
  * qlty 表示空气质量
  */
public class Aqi {
    public AqiCity city;

  public class AqiCity {
        public String aqi;
        public String pm25;
        public String qlty;
    }
}
