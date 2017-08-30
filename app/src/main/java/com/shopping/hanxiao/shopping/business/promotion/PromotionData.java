package com.shopping.hanxiao.shopping.business.promotion;

import com.shopping.hanxiao.shopping.business.TopBannerData;

import java.util.List;

/**
 * Created by wenzhi on 17/6/28.
 */

public class PromotionData {
    public List<PromotionItemData> list;
    public List<TopBannerData> topBanner;
    public List<CustomItemData> twoItems;
    public List<CustomItemData> heightItems;
    public List<CustomItemData> nextHeightItems;
    public boolean hasMore;
}
