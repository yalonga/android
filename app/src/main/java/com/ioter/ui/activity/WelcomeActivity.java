package com.ioter.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import com.ioter.R;
import com.ioter.common.util.UIConstant;

import butterknife.ButterKnife;

/**
 * 欢迎页
 *
 * @author Administrator
 */
public class WelcomeActivity extends AppCompatActivity
{

    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case UIConstant.MSG_WELCOME_Delay:
                    //startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                    startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                    WelcomeActivity.this.finish();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);

        initData();
    }

    private void initData()
    {
        Message message = new Message();
        message.what = UIConstant.MSG_WELCOME_Delay;
        handler.sendMessageDelayed(message, 1000);
    }

}
