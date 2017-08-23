package com.shopping.hanxiao.shopping.utils;

import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * Created by wenzhi on 17/6/17.
 */

public class ScreenInfoUtil {
    private static DisplayMetrics sDM = Resources.getSystem().getDisplayMetrics();

    public static int getScreenWidth() {
        return sDM.widthPixels;
    }

    public static int getScreenHeight() {
        return sDM.heightPixels;
    }

    public static float getDensity() {
        return sDM.density;
    }

    public static int dpToPx(int dp) {
        return dpToPx((float) dp);
    }

    public static int dpToPx(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, sDM);
    }

    public static int spToPx(float sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, sDM);
    }

    public static int pxToDp(int px) {
        return Math.round(px / getDensity());
    }

}
