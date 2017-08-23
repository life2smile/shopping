package com.shopping.hanxiao.shopping.utils;

/**
 * Created by wenzhi on 17/6/21.
 */

public class NetWorkUtils {
    public final static String CONTENT_TYPE = "Content-Type: application/json";
    public final static String ACCEPT_TYPE = "Accept: application/json";

    public final static String BASE_URL = "http://www.life2smile.com:80/";
    public final static String PREFIX_PATH = "/shopping";

//    public final static String BASE_URL = "http://localhost:8080/";
//    public final static String PREFIX_PATH = "/shopping";

    public final static String PLATFORM_ICON_PREFIX = BASE_URL + PREFIX_PATH + "/img/";
    public final static String CHECK_VERSION = PREFIX_PATH + "/v1/update.action";
    public final static String GET_TITLES = PREFIX_PATH + "/v1/titles.action";
    public final static String GET_DISCOUNT = PREFIX_PATH + "/v1/getDiscountData.action";
    public final static String GET_COMMODITY = PREFIX_PATH + "/v1/getCommodity.action";
    public final static String GET_PROMOTION = PREFIX_PATH + "/v1/getPromotionData.action";
    public final static String SEARCH_COMMODITY = PREFIX_PATH + "/v1/searchCommodity.action";
}
