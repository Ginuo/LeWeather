package com.ginuo.leweather.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;


public class HttpUtil {

    //没有返回值，但是传入了回调对象，相应结果在回调方法中处理
    public static void sendOkHttpRequest(String address,okhttp3.Callback callback){
        OkHttpClient client =new OkHttpClient();
        //构造请求
        Request request = new Request.Builder().url(address).build();
        //加入发送队列
        client.newCall(request).enqueue(callback);
    }
}
