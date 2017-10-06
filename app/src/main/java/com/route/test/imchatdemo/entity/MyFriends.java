package com.route.test.imchatdemo.entity;

import android.widget.TextView;

/**
 * Created by my301s on 2017/9/25.
 */

public class MyFriends {
    private TextView  tv_name;

    public MyFriends(TextView tv_name) {
        this.tv_name = tv_name;
    }

    public TextView getTv_name() {
        return tv_name;
    }

    public void setTv_name(TextView tv_name) {
        this.tv_name = tv_name;
    }

    @Override
    public String toString() {
        return "MyFriends{" +
                "tv_name=" + tv_name +
                '}';
    }
}
