package cn.ryanliu.qihangbookstore.util;

import android.content.Intent;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpUtilPost {
    public static void sendOkhttpRequest(String address, RequestBody requestBody, okhttp3.Callback callback){
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder()
                .url(address)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

}
