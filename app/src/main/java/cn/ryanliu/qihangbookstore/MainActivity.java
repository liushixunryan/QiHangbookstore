package cn.ryanliu.qihangbookstore;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
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
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cn.ryanliu.qihangbookstore.bean.BookListResult;
import cn.ryanliu.qihangbookstore.util.HttpUtilGet;
import cn.ryanliu.qihangbookstore.util.Url;
import cz.msebera.android.httpclient.Header;
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
    private AsyncHttpClient mClient = new AsyncHttpClient();
    //初始化,防止使用时候空指针
    private List<BookListResult.BookData> mBookData = new ArrayList<>();
    private MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        NetworkConnections();
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
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
                Toast.makeText(MainActivity.this, "网络未连接或网络不稳定", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String result = response.body().string();
                Gson gson = new Gson();
                BookListResult bookListResult = gson.fromJson(result, BookListResult.class);
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
            final BookListResult.BookData bookData = mBookData.get(position);
            ViewHolder viewHolder = new ViewHolder();
            if (convertView == null) {
                //如果为空就新建
                convertView = getLayoutInflater().inflate(R.layout.item_book_list_view, null);
                viewHolder.mNameTextView = convertView.findViewById(R.id.name_text_view);
                viewHolder.mButton = convertView.findViewById(R.id.book_button);
                viewHolder.mImageView = convertView.findViewById(R.id.icon_image_view);
                convertView.setTag(viewHolder);
            } else {
                //如果不为空直接拿他缓存
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.mNameTextView.setText(bookData.getBookname());
            //路径
            final String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/QiHangBookStore/" + bookData.getBookfile() + ".txt";
            //文件
            final File file = new File(path);
            //如果文件存在就是点击打开,不是就是点击下载
            viewHolder.mButton.setText(file.exists()?"点击打开":"点击下载");
            final ViewHolder finalViewHolder = viewHolder;
            viewHolder.mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //下载的功能
                    //判断文件是否存在
                    if (file.exists()) {
                        //TODO:打开书籍
                        BookActivity.statr(MainActivity.this,path);

                    } else {
                        //不存在就创建
                        //默认使用baigzip压缩，导致无法提前获取下载文件的总du大小zhi，不让它压缩即可
                        mClient.addHeader("Accept-Encoding", "identity");
                        mClient.get(bookData.getBookfile(), new FileAsyncHttpResponseHandler(file) {
                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                                //下载失败
                                Log.i("it520", "onFailure: " + throwable);
                                finalViewHolder.mButton.setText("下载失败");
                            }

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, File file) {
                                //下载成功
                                finalViewHolder.mButton.setText("点击打开");
                            }

                            @Override
                            public void onProgress(long bytesWritten, long totalSize) {
                                //进度条
                                super.onProgress(bytesWritten, totalSize);
                                finalViewHolder.mButton.setText(bytesWritten * 100 / totalSize + "%");
                            }
                        });
                    }

                }
            });
            return convertView;
        }

        class ViewHolder {
            public TextView mNameTextView;
            public Button mButton;
            public ImageView mImageView;
        }
    }


}
