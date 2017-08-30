package com.shopping.hanxiao.shopping.utils;

import android.text.TextUtils;

/**
 * Created by wenzhi on 17/6/27.
 */

public class StringUtils {
    public static String truncateString(String str, int maxNum) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        if (str.length() <= maxNum) {
            return str;
        }
        return str.substring(0, maxNum);
    }

    public static String truncateStringWithEllipsis(String str, int maxNum) {
        String ellipsis = "...";
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        if (str.length() <= maxNum) {
            return str + ellipsis;
        }
        return str.substring(0, maxNum) + ellipsis;
    }
}
