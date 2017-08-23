package com.shopping.hanxiao.shopping.business.coupon.api;

import com.shopping.hanxiao.shopping.business.RequestApi;
import com.shopping.hanxiao.shopping.business.coupon.request.RequestParams;
import com.shopping.hanxiao.shopping.business.coupon.services.CommodityRequestService;

import okhttp3.RequestBody;
import retrofit2.Retrofit;
import rx.Observable;

/**
 * Created by wenzhi on 17/6/20.
 */

public class CommodityApi extends RequestApi {
    private String mType;
    private int mBegin;
    private int mOffset;

    public CommodityApi(String type, int begin, int offset) {
        this.mType = type;
        this.mBegin = begin;
        this.mOffset = offset;
    }

    @Override
    protected Object requestParams() {
        RequestParams requestParams = new RequestParams();
        requestParams.type = mType;
        requestParams.begin = mBegin;
        requestParams.offset = mOffset;
        return requestParams;
    }

    @Override
    protected Observable concreteObservable(Retrofit retrofit, RequestBody body) {
        CommodityRequestService requestService = retrofit.create(CommodityRequestService.class);
        return requestService.getCommodity(body);
    }
}
