package com.route.test.imchatdemo.base;


import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.route.test.imchatdemo.app.DemoApplication;

import org.xutils.x;


/**
 * 需要初始化的在这里application初始化
 */
public class MyApp extends DemoApplication {
	public static BaseActivity mBaseActivity;
	public static BaseFragment mBaseLastFragment;
	@Override
	public void onCreate() {
		x.Ext.init(this);
		super.onCreate();

		//响铃配置
		Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
		r.play();

		EMOptions options = new EMOptions();
// 默认添加好友时，是不需要验证的，改成需要验证
		options.setAcceptInvitationAlways(false);
		options.setAutoLogin(true);

//初始化
		EMClient.getInstance().init(this, options);
//在做打包混淆时，关闭debug模式，避免消耗不必要的资源
		EMClient.getInstance().setDebugMode(true);
	}
}
