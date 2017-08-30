package com.shopping.hanxiao.shopping.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.shopping.hanxiao.shopping.common.WebViewActivity;
import com.shopping.hanxiao.shopping.common.WebViewContext;

/**
 * Created by wenzhi on 17/6/24.
 */

public class UriParse {
    public static void startByBrowser(Context context, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }

    public static void startByWebView(Context context, String uri) {
        Intent intent = new Intent();
        intent.putExtra(WebViewContext.URI, uri);
        intent.setClass(context, WebViewActivity.class);
        context.startActivity(intent);
    }

    public static void startByWebView(Context context, String uri, String title, int leftResId, int rightResId) {
        Intent intent = new Intent();
        intent.putExtra(WebViewContext.URI, uri);
        intent.putExtra(WebViewContext.TITLE, title);
        intent.putExtra(WebViewContext.LEFT_IMAGE_RES_ID, leftResId);
        intent.putExtra(WebViewContext.RIGHT_IMAGE_RES_ID, rightResId);
        intent.setClass(context, WebViewActivity.class);
        context.startActivity(intent);
    }

    public static void startByWebView(Context context, Intent intent) {
        intent.setClass(context, WebViewActivity.class);
        context.startActivity(intent);
    }

    public static void startByBrowser(Context context, Intent intent) {
        context.startActivity(intent);
    }
}
