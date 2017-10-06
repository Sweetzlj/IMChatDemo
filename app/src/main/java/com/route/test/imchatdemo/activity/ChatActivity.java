package com.route.test.imchatdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.ui.EaseChatFragment;
import com.route.test.imchatdemo.R;
import com.route.test.imchatdemo.base.BaseActivity;

import butterknife.BindView;

public class ChatActivity extends BaseActivity {


    @BindView(R.id.frameLayout)
    FrameLayout frameLayout;
    @Override
    protected void initData() {


    }

    @Override
    protected void initId() {
        Intent intent = getIntent();
        String userId = intent.getStringExtra(EaseConstant.EXTRA_USER_ID);
        EaseChatFragment chatFragment = new EaseChatFragment();
        //传入参数
        Bundle args = new Bundle();
        args.putString(EaseConstant.EXTRA_USER_ID, userId);
        chatFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, chatFragment).commit();
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_chat;
    }
}
