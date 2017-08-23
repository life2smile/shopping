package com.shopping.hanxiao.shopping.utils;

import java.text.NumberFormat;

/**
 * Created by wenzhi on 17/8/6.
 */

public class NumberFormatUtil {

    public static String formatNumber(double number) {
        return formatNumber(number, 2, 2);
    }

    public static String formatNumber(double number, int minFractionDigits, int maxFractionDigits) {
        NumberFormat format = NumberFormat.getNumberInstance();
        format.setMinimumFractionDigits(minFractionDigits);
        format.setMaximumFractionDigits(maxFractionDigits);
        return format.format(number);
    }

    public static String formatToRMB(double price) {
        String formatPrice = formatNumber(price);
        return "￥" + formatPrice;
    }

    public static String formatToDiscount(double price) {
        return price + "折";
    }

}
