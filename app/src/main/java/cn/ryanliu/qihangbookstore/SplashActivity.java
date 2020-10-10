package cn.ryanliu.qihangbookstore;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.ref.WeakReference;

public class SplashActivity extends AppCompatActivity {

    public static final int CODE = 1001;
    public static final int TOTAL_TIME = 3000;
    public static final int INTERVAL_TIME = 1000;
    private TextView mTextView;
    private LinearLayout mLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mTextView = findViewById(R.id.time_text_view);
        mLinearLayout = findViewById(R.id.time_linearlayout);
        final MyHandler handler = new MyHandler(this);

        Message message = Message.obtain();
        message.what = CODE;
        message.arg1 = TOTAL_TIME;
        handler.sendMessage(message);

        mLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 跳到下一个页面
                MainActivity.statr(SplashActivity.this);
                SplashActivity.this.finish();
                handler.removeMessages(CODE);
            }
        });
    }

    /**
     * 倒计时
     */
    //这样写handler可以防止内存泄露,因为使用handler会持有对象(activity引用) activity销毁时候被他持有,所以销毁不掉
    public static class MyHandler extends Handler {
        //弱引用
        public final WeakReference<SplashActivity> mWeakReference;

        public MyHandler(SplashActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SplashActivity activity = mWeakReference.get();
            if (msg.what == CODE) {
                if (activity != null) {
                    //设置TextView,更新UI
                    int time = msg.arg1;
                    activity.mTextView.setText(time / INTERVAL_TIME + "");
                    //发送倒计时
                    Message message = Message.obtain();
                    message.what = CODE;
                    message.arg1 = time - INTERVAL_TIME;

                    if (time > 0) {
                        sendMessageDelayed(message, INTERVAL_TIME);
                    } else {
                        //TODO 跳到下一个页面
                        MainActivity.statr(activity);
                        activity.finish();
                    }

                }
            }
        }
    }
}
