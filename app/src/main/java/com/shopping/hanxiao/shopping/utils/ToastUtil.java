package com.shopping.hanxiao.shopping.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by wenzhi on 17/6/21.
 */

public class ToastUtil {
    public static void toast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
