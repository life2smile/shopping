package com.shopping.hanxiao.shopping.utils;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

/**
 * Created by wenzhi on 17/8/6.
 */

public class TextViewUtils {

    public static void setViewVisibility(View view, int visibility) {
        view.setVisibility(visibility);
    }

    public static void setTextViewVisibility(TextView view, String text, int visibility) {
        view.setVisibility(visibility);
        if (!TextUtils.isEmpty(text)) {
            view.setText(text);
        }
    }

    public static void showView(View view) {
        view.setVisibility(View.VISIBLE);
    }

    public static void hiddenView(View view) {
        view.setVisibility(View.GONE);
    }

    public static void showTextView(TextView view, String text) {
        view.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(text)) {
            view.setText(text);
            view.setVisibility(View.VISIBLE);
        }
    }
}
