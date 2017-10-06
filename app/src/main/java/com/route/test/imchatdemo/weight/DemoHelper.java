package com.route.test.imchatdemo.weight;

import com.hyphenate.chat.EMClient;

/**
 * Created by Administrator on 2017/9/7.
 */

public class DemoHelper {
    private static DemoHelper  demoHelper;
    private DemoHelper(){

    }
    public static DemoHelper getInstance(){
        if (demoHelper==null){
            synchronized (DemoHelper.class){
                if (demoHelper == null){
                    demoHelper = new DemoHelper();
                }
            }
        }
        return demoHelper;
    }
    public boolean isLoggedIn() {
        return EMClient.getInstance().isLoggedInBefore();
    }
}
