package cn.ryanliu.qihangbookstore;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.ryanliu.qihangbookstore.readbookpage.BookPageBezierHelper;
import cn.ryanliu.qihangbookstore.readbookpage.BookPageView;

public class BookActivity extends AppCompatActivity {
    public static final String FILE_PATH = "file_path";
    private BookPageView mBookPageView;
    private TextView mProgressTextView;
    private RelativeLayout mSettingView;
    private RecyclerView mSettingRecyclerView;
    private int mCurrentLength;
    private String mBookmark;
    private BookPageBezierHelper mHelper;
    private Bitmap mCurrentPageBitmap;
    private Bitmap mNextPageBitmap;
    private String mFilePath;
    private int mWidth;
    private int mHeight;

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
        // 判断是否存在
        if (getIntent() != null){
            mFilePath = getIntent().getStringExtra(FILE_PATH);
        }else {
            Toast.makeText(this, "未找到这本书", Toast.LENGTH_SHORT).show();
        }
        //实例化
        initView();
        //设置贝塞尔曲线翻页效果
        setBookHelper();

    }

    private void setBookHelper() {
        //获取页面高度和宽度
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mWidth = displayMetrics.widthPixels;
        mHeight = displayMetrics.heightPixels;
        openBookByProgress(0);
    }

    private void openBookByProgress(int progress) {
        //设置贝塞尔曲线翻页
        mHelper = new BookPageBezierHelper(mWidth, mHeight,progress);
        mBookPageView.setBookPageBezierHelper(mHelper);

        //当前页,下一页
        mCurrentPageBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        mNextPageBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        mBookPageView.setBitmaps(mCurrentPageBitmap, mNextPageBitmap);


        if (!TextUtils.isEmpty(mFilePath)){
            try {
                //打开这本书
                mHelper.openBook(mFilePath);
                //画这本书
                mHelper.draw(new Canvas(mCurrentPageBitmap));
                mBookPageView.invalidate();
                //设置背景
                mHelper.setBackground(this,R.drawable.background);
                //设置进度
                mHelper.setOnProgressChangedListener(new BookPageBezierHelper.OnProgressChangedListener() {
                    @Override
                    public void setProgress(int currentLength, int totalLength) {
                        mCurrentLength = currentLength;
                        float progress = mCurrentLength * 100 / totalLength;
                        mProgressTextView.setText(progress + "%");
                    }
                });
                //设置界面
                mBookPageView.setOnUserNeedSettingListener(new BookPageView.OnUserNeedSettingListener() {
                    @Override
                    public void onUserNeedSetting() {
                        //如果显示设置界面就隐藏,如果隐藏就显示
                        mSettingView.setVisibility(mSettingView.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                    }
                });

                //设置页面界面数据
                List<String> settingList = new ArrayList<>();
                settingList.add("添加书签");
                settingList.add("读取书签");
                settingList.add("设置背景");
                settingList.add("语音朗读");
                settingList.add("跳转进度");

                LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
                mSettingRecyclerView.setLayoutManager(layoutManager);
                mSettingRecyclerView.setAdapter(new HorizontalAdapter(this,settingList));

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "未找到这本书", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this, "未找到这本书", Toast.LENGTH_SHORT).show();
        }
    }

    private void initView() {
        mSettingRecyclerView = findViewById(R.id.setting_recycler_view);
        mBookPageView = findViewById(R.id.book_page_view);
        mProgressTextView = findViewById(R.id.progress_text_view);
        mSettingView = findViewById(R.id.setting_view);
    }

    //页面跳转封装方法
    public static void statr(Context context , String filePath) {
        Intent intent = new Intent(context, BookActivity.class);
        intent.putExtra(FILE_PATH, filePath);
        context.startActivity(intent);
    }

    private class HorizontalAdapter extends RecyclerView.Adapter {
        private Context mContext;
        private List<String> mList = new ArrayList<>();
        public HorizontalAdapter(Context context, List<String> list) {
            mContext = context;
            mList = list;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            //用代码的方式写一个TextView
            TextView itemView = new TextView(mContext);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, @SuppressLint("RecyclerView") final int position) {
            TextView textView = (TextView) viewHolder.itemView;
            textView.setWidth(240);
            textView.setHeight(180);
            textView.setTextSize(16);
            textView.setTextColor(Color.WHITE);
            textView.setGravity(Gravity.CENTER);
            textView.setText(mList.get(position));
            final SharedPreferences sharedPreferences = mContext.getSharedPreferences("ryan_book_prefers",MODE_PRIVATE);
            final SharedPreferences.Editor editor = sharedPreferences.edit();
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (position){
                        case 0:
                            //添加书签
                            /**
                             * 1.获取进度
                             * 2.存取进度
                             */
                            mBookmark = "bookmark";
                            editor.putInt(mBookmark, mCurrentLength);
                            editor.apply();
                            break;
                        case 1:
                            //读取书签
                            /**
                             * 1.读取进度
                             * 2.跳转到进度
                             */
                            int lastLength = sharedPreferences.getInt(mBookmark,0);
                            openBookByProgress(lastLength);
                            break;
                        case 2:
                            break;
                        case 3:
                            break;
                        case 4:
                            break;
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
        public class ViewHolder extends RecyclerView.ViewHolder{
            private TextView mTextView;
            public ViewHolder(@NonNull TextView itemView) {
                super(itemView);
                mTextView = itemView;
            }
        }
    }
}