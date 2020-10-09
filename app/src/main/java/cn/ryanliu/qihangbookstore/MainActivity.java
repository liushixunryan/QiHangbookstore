package cn.ryanliu.qihangbookstore;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    //页面跳转封装方法
    public static void statr(Context context){
        Intent intent = new Intent(context,MainActivity.class);
        context.startActivity(intent);
    }
}
