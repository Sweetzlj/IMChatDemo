package com.route.test.imchatdemo.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMContactListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.ui.EaseConversationListFragment;
import com.route.test.imchatdemo.R;
import com.route.test.imchatdemo.app.DemoApplication;
import com.route.test.imchatdemo.base.BaseActivity;
import com.route.test.imchatdemo.domain.EaseUser;
import com.route.test.imchatdemo.fragment.FriendsFragment;
import com.route.test.imchatdemo.fragment.SetFragment;
import com.route.test.imchatdemo.view.InviteMessage;
import com.route.test.imchatdemo.view.InviteMessgeDao;
import com.route.test.imchatdemo.view.UserDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends BaseActivity implements View.OnClickListener {

    FrameLayout MainFrameLayout;
    TextView rbChat;
    TextView rbFriends;
    TextView rbSet;
    private UserDao userDao;
    private TextView tv_red;
    private EaseConversationListFragment easeConversationListFragment;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    initRed();
                    break;
            }
        }
    };
    private InviteMessgeDao inviteMessgeDao;

    @Override
    protected void initData() {
        initRed();

        rbChat.setOnClickListener(this);
        rbFriends.setOnClickListener(this);
        rbSet.setOnClickListener(this);
    }

    @Override
    protected void initId() {
        inviteMessgeDao = new InviteMessgeDao(HomeActivity.this);
        userDao = new UserDao(HomeActivity.this);
        //注册联系人变动监听
        EMClient.getInstance().contactManager().setContactListener(new MyContactListener());

        tv_red = findViewById(R.id.tv_red);
        MainFrameLayout = findViewById(R.id.MainFrameLayout);
        rbChat = findViewById(R.id.rb_chat);
        rbFriends = findViewById(R.id.rb_friends);
        rbSet = findViewById(R.id.rb_set);

        easeConversationListFragment = new EaseConversationListFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.MainFrameLayout, easeConversationListFragment).commit();
        easeConversationListFragment.setConversationListItemClickListener(new EaseConversationListFragment.EaseConversationListItemClickListener() {
            @Override
            public void onListItemClicked(EMConversation conversation) {
                String s = conversation.conversationId();
                startActivity(new Intent(HomeActivity.this, ChatActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, s));
            }
        });
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_home;
    }


    @Override
    protected void onStart() {
        super.onStart();
        EMClient.getInstance().chatManager().addMessageListener(DemoApplication.xiaoxi(null, this));
        initRed();
    }

    public void initRed() {
        int a = EMClient.getInstance().chatManager().getUnreadMessageCount();
        if (a > 0) {
            tv_red.setVisibility(View.VISIBLE);
            tv_red.setText(a + "");
        } else {
            tv_red.setVisibility(View.GONE);
        }
        handler.sendEmptyMessage(1);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rb_chat:
                getSupportFragmentManager().beginTransaction().hide(new SetFragment()).commit();
                getSupportFragmentManager().beginTransaction().replace(R.id.MainFrameLayout, new EaseConversationListFragment()).commit();
                break;
            case R.id.rb_friends:
                getSupportFragmentManager().beginTransaction().hide(new EaseConversationListFragment()).commit();
                getSupportFragmentManager().beginTransaction().replace(R.id.MainFrameLayout, new FriendsFragment()).commit();
                break;
            case R.id.rb_set:
                getSupportFragmentManager().beginTransaction().hide(new EaseConversationListFragment()).commit();
                getSupportFragmentManager().beginTransaction().replace(R.id.MainFrameLayout, new SetFragment()).commit();
                break;
        }
    }

    /***
     * 好友变化listener
     *
     */
    public class MyContactListener implements EMContactListener {

        @Override
        public void onContactAdded(final String username) {
            // 保存增加的联系人
            Map<String, EaseUser> localUsers = DemoApplication.getInstance().getContactList();
            Map<String, EaseUser> toAddUsers = new HashMap<String, EaseUser>();
            EaseUser user = new EaseUser(username);
            // 添加好友时可能会回调added方法两次
            if (!localUsers.containsKey(username)) {
                userDao.saveContact(user);
            }
            toAddUsers.put(username, user);
            localUsers.putAll(toAddUsers);
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "增加联系人：+" + username, Toast.LENGTH_SHORT).show();
                }


            });
        }

        @Override
        public void onContactDeleted(final String username) {
            // 被删除
            Map<String, EaseUser> localUsers = DemoApplication.getInstance().getContactList();
            localUsers.remove(username);
            userDao.deleteContact(username);
            inviteMessgeDao.deleteMessage(username);

            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "删除联系人：+" + username, Toast.LENGTH_SHORT).show();
                }


            });

        }

        @Override
        public void onContactInvited(final String username, String reason) {
            // 接到邀请的消息，如果不处理(同意或拒绝)，掉线后，服务器会自动再发过来，所以客户端不需要重复提醒
            List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();

            for (InviteMessage inviteMessage : msgs) {
                if (inviteMessage.getGroupId() == null && inviteMessage.getFrom().equals(username)) {
                    inviteMessgeDao.deleteMessage(username);
                }
            }
            // 自己封装的javabean
            InviteMessage msg = new InviteMessage();
            msg.setFrom(username);
            msg.setTime(System.currentTimeMillis());
            msg.setReason(reason);

            // 设置相应status
            msg.setStatus(InviteMessage.InviteMesageStatus.BEINVITEED);
            notifyNewIviteMessage(msg);
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "收到好友申请：+" + username, Toast.LENGTH_SHORT).show();
                }
            });

        }

        @Override
        public void onFriendRequestAccepted(String s) {

        }

        @Override
        public void onFriendRequestDeclined(String s) {

        }


        public void onContactAgreed(final String username) {
            List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();
            for (InviteMessage inviteMessage : msgs) {
                if (inviteMessage.getFrom().equals(username)) {
                    return;
                }
            }
            // 自己封装的javabean
            InviteMessage msg = new InviteMessage();
            msg.setFrom(username);
            msg.setTime(System.currentTimeMillis());

            msg.setStatus(InviteMessage.InviteMesageStatus.BEAGREED);
            notifyNewIviteMessage(msg);
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "好友申请同意：+" + username, Toast.LENGTH_SHORT).show();
                }


            });

        }

        public void onContactRefused(String username) {
            // 参考同意，被邀请实现此功能,demo未实现
            Log.d(username, username + "拒绝了你的好友请求");
        }
    }

    /**
     * 保存并提示消息的邀请消息
     *
     * @param msg
     */
    private void notifyNewIviteMessage(InviteMessage msg) {
        if (inviteMessgeDao == null) {
            inviteMessgeDao = new InviteMessgeDao(HomeActivity.this);
        }
        inviteMessgeDao.saveMessage(msg);
        //保存未读数，这里没有精确计算
        inviteMessgeDao.saveUnreadMessageCount(1);
        // 提示有新消息
        //响铃或其他操作
    }


    private void logout() {
        final ProgressDialog pd = new ProgressDialog(HomeActivity.this);
        String st = getResources().getString(R.string.Are_logged_out);
        pd.setMessage(st);
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        DemoApplication.getInstance().logout(false, new EMCallBack() {

            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        pd.dismiss();
                        // 重新显示登陆页面
                        finish();
                        startActivity(new Intent(HomeActivity.this, LoginActivity.class));

                    }
                });
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        pd.dismiss();
                        Toast.makeText(HomeActivity.this, "unbind devicetokens failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

}
