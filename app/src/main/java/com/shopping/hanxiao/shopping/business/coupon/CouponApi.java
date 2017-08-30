package com.shopping.hanxiao.shopping.business.coupon;

import com.shopping.hanxiao.shopping.business.RequestApi;
import com.shopping.hanxiao.shopping.business.request.RequestParams;

import okhttp3.RequestBody;
import retrofit2.Retrofit;
import rx.Observable;

/**
 * Created by wenzhi on 17/6/20.
 */

public class CouponApi extends RequestApi {
    private String mType;
    private int mBegin;
    private int mOffset;

    public CouponApi(String type, int begin, int offset) {
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
        CouponRequestService requestService = retrofit.create(CouponRequestService.class);
        return requestService.getCommodity(body);
    }
}
