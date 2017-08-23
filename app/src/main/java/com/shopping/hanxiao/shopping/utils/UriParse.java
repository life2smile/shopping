package com.shopping.hanxiao.shopping.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.shopping.hanxiao.shopping.common.WebViewActivity;

/**
 * Created by wenzhi on 17/6/24.
 */

public class UriParse {
    public static void startByBrower(Context context, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }

    public static void startByWebView(Context context, String uri) {
        Intent intent = new Intent();
        intent.putExtra("shopping_uri", uri);
        intent.setClass(context, WebViewActivity.class);
        context.startActivity(intent);
    }
}
