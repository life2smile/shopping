package com.shopping.hanxiao.shopping;

import android.app.Application;

import com.shopping.hanxiao.shopping.rxretrofit.RxRetrofitApp;

/**
 * Created by wenzhi on 17/6/20.
 */

public class ShoppingApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        RxRetrofitApp.init(this);
    }
}
