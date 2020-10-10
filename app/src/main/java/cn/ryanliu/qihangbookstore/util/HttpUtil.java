package cn.ryanliu.qihangbookstore.util;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpUtil {

    public static void SendOkHttpRequest() throws IOException {
        final OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://api.myjson.com/bins/ir8oa")
                .build();

        //开启一个异步请求
        client.newCall(request).enqueue(new Callback() {

            //请求失败
            @Override public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            //有响应
            @Override public void onResponse(Call call, Response response) throws IOException {
                {
                    if (!response.isSuccessful()) {
                        //请求失败
                    }

                    //获取到数据
                    System.out.println(response.body().string());
//                  Log.i("it520",response.body().string());
//                    String date=response.body().string();

                }
            }
        });

    }
}
