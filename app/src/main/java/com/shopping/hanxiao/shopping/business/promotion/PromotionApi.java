package com.shopping.hanxiao.shopping.business.promotion;

import com.shopping.hanxiao.shopping.business.coupon.api.CommodityApi;

import okhttp3.RequestBody;
import retrofit2.Retrofit;
import rx.Observable;

/**
 * Created by wenzhi on 17/6/20.
 */

public class PromotionApi extends CommodityApi {

    public PromotionApi(String type, int begin, int offset) {
        super(type, begin, offset);
    }

    @Override
    protected Observable concreteObservable(Retrofit retrofit, RequestBody body) {
        PromotionRequestService requestService = retrofit.create(PromotionRequestService.class);
        return requestService.getCommodity(body);
    }

    @Override
    protected boolean showProgress() {
        return true;
    }

    protected void showProgress(boolean show) {
        super.setShowProgress(show);
    }
}
