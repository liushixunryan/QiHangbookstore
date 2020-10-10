package cn.ryanliu.qihangbookstore;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cn.ryanliu.qihangbookstore.bean.BookListResult;
import cn.ryanliu.qihangbookstore.util.HttpUtilGet;
import cn.ryanliu.qihangbookstore.util.Url;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private ListView mListView;
    private Handler mHandler;
    private RequestBody requestBody;
    //初始化,防止使用时候空指针
    private List<BookListResult.BookData> mBookData = new ArrayList<>();
    private  MediaType JSON= MediaType.parse("application/json; charset=utf-8");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        NetworkConnections();
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1){
                    mListView.setAdapter(new BookListAdapter());
                }
            }
        };
    }

    private void initView() {
        mListView = findViewById(R.id.book_list_view);
    }

    //网络连接
    private void NetworkConnections() {
        HttpUtilGet.sendOkhttpRequest(Url.book_list_url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //异常处理
                Looper.prepare();
                Log.i("it520", e.toString());
                Toast.makeText(MainActivity.this, "网络未连接或网络不稳定", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String result = response.body().string();
                Gson gson = new Gson();
                BookListResult bookListResult = gson.fromJson(result,BookListResult.class);
                mBookData = bookListResult.getData();
                mHandler.sendEmptyMessage(1);
            }
        });
    }

    //页面跳转封装方法
    public static void statr(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    private class BookListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mBookData.size();
        }

        @Override
        public Object getItem(int position) {
            return mBookData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            BookListResult.BookData bookData = mBookData.get(position);
            ViewHolder viewHolder = new ViewHolder();
            if (convertView == null){
                //如果为空就新建
                convertView = getLayoutInflater().inflate(R.layout.item_book_list_view,null);
                viewHolder.mNameTextView = convertView.findViewById(R.id.name_text_view);
                viewHolder.mButton = convertView.findViewById(R.id.book_button);
                viewHolder.mImageView = convertView.findViewById(R.id.icon_image_view);
                convertView.setTag(viewHolder);
            }else {
                //如果不为空直接拿他缓存
                viewHolder = (ViewHolder) convertView.getTag();
            }
             viewHolder.mNameTextView.setText(bookData.getBookname());
            viewHolder.mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //下载的功能
                }
            });
            return convertView;
        }
        class ViewHolder{
            public TextView mNameTextView;
            public Button mButton;
            public ImageView mImageView;
        }
    }
}
