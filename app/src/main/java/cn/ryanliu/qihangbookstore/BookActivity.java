package cn.ryanliu.qihangbookstore;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.TextView;

import cn.ryanliu.qihangbookstore.readbookpage.BookPageBezierHelper;
import cn.ryanliu.qihangbookstore.readbookpage.BookPageView;

public class BookActivity extends AppCompatActivity {
    public static final String FILE_PATH = "file_path";
    private BookPageView mBookPageView;
    private TextView mProgressTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        mBookPageView.setBookPageBezierHelper(new BookPageBezierHelper(width,height));
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