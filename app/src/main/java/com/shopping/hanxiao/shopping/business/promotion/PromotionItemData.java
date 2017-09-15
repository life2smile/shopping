package com.shopping.hanxiao.shopping.business.promotion;

import java.util.List;

/**
 * Created by wenzhi on 17/6/17.
 */

public class PromotionItemData {
    public long id;
    public double originPrice;
    public double promotionPrice;
    public String description;
    public String subDescription;
    public String imgUrl;
    public String actionUrl;
    public String type;
    public List<ItemBannerData> banners;

}
