package com.shopping.hanxiao.shopping.common.search;

import com.shopping.hanxiao.shopping.business.RequestApi;
import com.shopping.hanxiao.shopping.business.request.RequestParams;

import okhttp3.RequestBody;
import retrofit2.Retrofit;
import rx.Observable;

/**
 * Created by wenzhi on 17/6/20.
 */

public class SearchApi extends RequestApi {
    private String mType;
    private int mBegin;
    private int mOffset;
    private String mKeyword;

    public SearchApi(String type, int begin, int offset, String keyword) {
        this.mType = type;
        this.mBegin = begin;
        this.mOffset = offset;
        this.mKeyword = keyword;
    }

    @Override
    protected Object requestParams() {
        RequestParams requestParams = new RequestParams();
        requestParams.type = mType;
        requestParams.begin = mBegin;
        requestParams.offset = mOffset;
        requestParams.keyword = mKeyword;
        return requestParams;
    }

    @Override
    protected Observable concreteObservable(Retrofit retrofit, RequestBody body) {
        SearchRequestService requestService = retrofit.create(SearchRequestService.class);
        return requestService.searchCommodity(body);
    }

    @Override
    protected boolean showProgress() {
        return true;
    }
}
