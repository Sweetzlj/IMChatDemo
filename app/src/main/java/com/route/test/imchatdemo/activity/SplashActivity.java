package com.route.test.imchatdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.hyphenate.chat.EMClient;
import com.route.test.imchatdemo.R;
import com.route.test.imchatdemo.weight.DemoHelper;

public class SplashActivity extends AppCompatActivity {
    private static final int sleepTime = 2000;
    private ImageView iv_splash_logo;
    private TextView tv_version;
    private RelativeLayout splash_root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initView();
    }

    private void initView() {
        iv_splash_logo = (ImageView) findViewById(R.id.iv_splash_logo);
        tv_version = (TextView) findViewById(R.id.tv_version);
        splash_root = (RelativeLayout) findViewById(R.id.splash_root);
    }

    @Override
    protected void onStart() {
        super.onStart();
        new Thread(new Runnable() {
            public void run() {
                if (DemoHelper.getInstance().isLoggedIn()) {
                    // auto login mode, make sure all group and conversation is loaed before enter the main screen
                    long start = System.currentTimeMillis();
                    EMClient.getInstance().chatManager().loadAllConversations();
                    EMClient.getInstance().groupManager().loadAllGroups();
                    long costTime = System.currentTimeMillis() - start;
                    //wait
                    if (sleepTime - costTime > 0) {
                        try {
                            Thread.sleep(sleepTime - costTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                        startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                    finish();
                }else {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                    }
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }
            }
        }).start();
    }
}
