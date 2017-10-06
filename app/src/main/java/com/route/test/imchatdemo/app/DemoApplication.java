package com.route.test.imchatdemo.app;

import android.app.ActivityManager;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.text.TextUtils;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.widget.EaseConversationList;
import com.hyphenate.exceptions.HyphenateException;
import com.route.test.imchatdemo.R;
import com.route.test.imchatdemo.activity.HomeActivity;
import com.route.test.imchatdemo.domain.EaseUser;
import com.route.test.imchatdemo.view.Constant;
import com.route.test.imchatdemo.view.Myinfo;
import com.route.test.imchatdemo.view.UserDao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DemoApplication extends Application {
    public static Context applicationContext;
    private static DemoApplication instance;
    private static NotificationManager notificationManager;
    private String username = "";
    private Map<String, EaseUser> contactList;
    public static String currentUserNick = "";

    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = this;
        notificationManager = (NotificationManager) applicationContext.getSystemService(Context.NOTIFICATION_SERVICE);
        instance = this;
        // 初始化环信sdk
        init(applicationContext);

        EMClient.getInstance().contactManager().setContactListener(new EMContactListener() {

            @Override
            public void onContactInvited(String username, String reason) {
                //收到好友邀请
                try {
                    EMClient.getInstance().contactManager().acceptInvitation(username);
                } catch (HyphenateException e) {

                }
            }

            @Override
            public void onFriendRequestAccepted(String s) {

            }

            @Override
            public void onFriendRequestDeclined(String s) {

            }

            @Override
            public void onContactDeleted(String username) {
                //被删除时回调此方法
            }


            @Override
            public void onContactAdded(String username) {
                //增加了联系人时回调此方法
                // hao();
                // contactListLayout.refresh();
            }
        });

    }

    public static DemoApplication getInstance() {
        return instance;
    }

	/*
     * 第一步：sdk的一些参数配置 EMOptions 第二步：将配置参数封装类 传入SDK初始化
	 */

    public void init(Context context) {
        // 第一步
        EMOptions options = initChatOptions();
        // 第二步
        boolean success = initSDK(context, options);
        if (success) {
            // 设为调试模式，打成正式包时，最好设为false，以免消耗额外的资源
            EMClient.getInstance().setDebugMode(true);
            // 初始化数据库
            initDbDao(context);
        }
    }

    private void initDbDao(Context context) {
        userDao = new UserDao(context);
    }

    public void setCurrentUserName(String username) {
        this.username = username;
        Myinfo.getInstance(instance).setUserInfo(Constant.KEY_USERNAME, username);
    }

    public String getCurrentUserName() {
        if (TextUtils.isEmpty(username)) {
            username = Myinfo.getInstance(instance).getUserInfo(Constant.KEY_USERNAME);
        }
        return username;

    }

    private UserDao userDao;

    private EMOptions initChatOptions() {

        // 获取到EMChatOptions对象
        EMOptions options = new EMOptions();
        // 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);
        // 设置是否需要已读回执
        options.setRequireAck(true);
        // 设置是否需要已送达回执
        options.setRequireDeliveryAck(false);
        return options;
    }

    private boolean sdkInited = false;

    public synchronized boolean initSDK(Context context, EMOptions options) {
        if (sdkInited) {
            return true;
        }

        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);

        // 如果app启用了远程的service，此application:onCreate会被调用2次
        // 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
        // 默认的app会在以包名为默认的process name下运行，如果查到的process name不是app的process
        // name就立即返回
        if (processAppName == null || !processAppName.equalsIgnoreCase(applicationContext.getPackageName())) {

            // 则此application::onCreate 是被service 调用的，直接返回
            return false;
        }
        if (options == null) {
            EMClient.getInstance().init(context, initChatOptions());
        } else {
            EMClient.getInstance().init(context, options);
        }
        sdkInited = true;
        return true;
    }

    /**
     * check the application process name if process name is not qualified, then
     * we think it is a service process and we will not init SDK
     *
     * @param pID
     * @return
     */
    @SuppressWarnings("rawtypes")
    private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) applicationContext.getSystemService(Context.ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {

                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
            }
        }
        return processName;
    }

    public void setContactList(Map<String, EaseUser> contactList) {

        this.contactList = contactList;

        userDao.saveContactList(new ArrayList<EaseUser>(contactList.values()));

    }

    public Map<String, EaseUser> getContactList() {

        if (contactList == null) {

            contactList = userDao.getContactList();

        }
        return contactList;

    }

    /**
     * 退出登录
     *·
     * @param unbindDeviceToken 是否解绑设备token(使用GCM才有)
     * @param callback          callback
     */
    public void logout(boolean unbindDeviceToken, final EMCallBack callback) {

        EMClient.getInstance().logout(unbindDeviceToken, new EMCallBack() {

            @Override
            public void onSuccess() {

                if (callback != null) {
                    callback.onSuccess();
                }
            }

            @Override
            public void onProgress(int progress, String status) {
                if (callback != null) {
                    callback.onProgress(progress, status);
                }
            }

            @Override
            public void onError(int code, String error) {

                if (callback != null) {
                    callback.onError(code, error);
                }
            }
        });
    }


    public static EMMessageListener xiaoxi(final EaseConversationList list, final Context Context) {
        //消息监听
        EMMessageListener msgListener = new EMMessageListener() {

            @Override
            public void onMessageReceived(List<EMMessage> messages) {
                Notification.Builder builder = new Notification.Builder(applicationContext);
                Intent intent = new Intent(applicationContext, HomeActivity.class);  //需要跳转指定的页面
                PendingIntent pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(pendingIntent);
                builder.setSmallIcon(R.mipmap.ic_launcher);// 设置图标
                builder.setContentTitle(messages.get(0).getFrom());// 设置通知的标题
                builder.setContentText(messages.get(0).getUserName());// 设置通知的内容
                builder.setWhen(System.currentTimeMillis());// 设置通知来到的时间
                builder.setAutoCancel(true); //自己维护通知的消失
                builder.setTicker("new message");// 第一次提示消失的时候显示在通知栏上的
                builder.setOngoing(false);
                Notification notification1 = builder.build();
                notificationManager.notify(0, notification1);


                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone r = RingtoneManager.getRingtone(Context, notification);
                r.play();
                //收到消息
                list.refresh();
            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> messages) {
                //收到透传消息
            }

            @Override
            public void onMessageRead(List<EMMessage> messages) {
                //收到已读回执
            }

            @Override
            public void onMessageDelivered(List<EMMessage> message) {
                //收到已送达回执
            }

            @Override
            public void onMessageChanged(EMMessage message, Object change) {
                //消息状态变动

                list.refresh();
            }
        };
        return msgListener;
    }

}
