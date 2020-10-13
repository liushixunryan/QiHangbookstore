package cn.ryanliu.qihangbookstore;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import cn.ryanliu.qihangbookstore.readbookpage.BookPageBezierHelper;
import cn.ryanliu.qihangbookstore.readbookpage.BookPageView;

public class BookActivity extends AppCompatActivity {
    public static final String FILE_PATH = "file_path";
    private BookPageView mBookPageView;
    private TextView mProgressTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全屏操作(沉浸式状态)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //如果>=4.4才支持沉浸式
            //显示上面的导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //显示下面的导航键
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setContentView(R.layout.activity_book);
        //实例化
        initView();
        //设置贝塞尔曲线翻页效果
        setBookHelper();

    }

    private void setBookHelper() {
        //获取页面高度和宽度
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        //设置贝塞尔曲线翻页
        final BookPageBezierHelper helper = new BookPageBezierHelper(width, height);
        mBookPageView.setBookPageBezierHelper(helper);

        //当前页,下一页
        Bitmap currentPageBitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        Bitmap nextPageBitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        mBookPageView.setBitmaps(currentPageBitmap,nextPageBitmap);

        // 判断是否存在
        if (getIntent() != null){
            String filePath = getIntent().getStringExtra(FILE_PATH);
            if (!TextUtils.isEmpty(filePath)){
                try {
                    //打开这本书
                    helper.openBook(filePath);
                    //画这本书
                    Canvas canvas = new Canvas(currentPageBitmap);
                    helper.draw(canvas);
                    //设置背景
                    helper.setBackground(this,R.drawable.background);
                    //设置进度
                    helper.setOnProgressChangedListener(new BookPageBezierHelper.OnProgressChangedListener() {
                        @Override
                        public void setProgress(int currentLength, int totalLength) {
                            mProgressTextView.setText("0.1");
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "未找到这本书", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(this, "未找到这本书", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this, "未找到这本书", Toast.LENGTH_SHORT).show();
        }
    }

    private void initView() {
        mBookPageView = findViewById(R.id.book_page_view);
        mProgressTextView = findViewById(R.id.progress_text_view);
    }

    //页面跳转封装方法
    public static void statr(Context context , String filePath) {
        Intent intent = new Intent(context, BookActivity.class);
        intent.putExtra(FILE_PATH, filePath);
        context.startActivity(intent);
    }
}