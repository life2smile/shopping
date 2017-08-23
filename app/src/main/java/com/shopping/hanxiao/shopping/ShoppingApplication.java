package com.shopping.hanxiao.shopping;

import android.app.Application;
import android.content.Context;
import android.telephony.TelephonyManager;

import com.shopping.hanxiao.shopping.rxretrofit.RxRetrofitApp;
import com.shopping.hanxiao.shopping.utils.MacUtil;

/**
 * Created by wenzhi on 17/6/20.
 */

public class ShoppingApplication extends Application {
    public static String deviceId;

    @Override
    public void onCreate() {
        super.onCreate();
        RxRetrofitApp.init(this);
        deviceId = getDeviceId();
    }

    private String getDeviceId() {
        String deviceId;
        try {
            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            deviceId = tm.getDeviceId();
        } catch (Exception e) {
            deviceId = MacUtil.getMac();
        }
        return deviceId;
    }
}
