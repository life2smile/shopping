package com.shopping.hanxiao.shopping.business.discount;

import com.shopping.hanxiao.shopping.business.coupon.api.CommodityApi;

import okhttp3.RequestBody;
import retrofit2.Retrofit;
import rx.Observable;

/**
 * Created by wenzhi on 17/6/20.
 */

public class DiscountApi extends CommodityApi {

    protected DiscountApi(String type, int begin, int offset) {
        super(type, begin, offset);
    }

    @Override
    protected Observable concreteObservable(Retrofit retrofit, RequestBody body) {
        DiscountRequestService requestService = retrofit.create(DiscountRequestService.class);
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
