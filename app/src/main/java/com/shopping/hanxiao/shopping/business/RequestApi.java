package com.shopping.hanxiao.shopping.business;

import com.alibaba.fastjson.JSON;
import com.shopping.hanxiao.shopping.rxretrofit.Api.BaseApi;
import com.shopping.hanxiao.shopping.utils.NetWorkUtils;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import rx.Observable;

/**
 * Created by wenzhi on 17/6/20.
 */

public abstract class RequestApi extends BaseApi {

    public RequestApi() {
        super.setBaseUrl(NetWorkUtils.BASE_URL);
        super.setShowProgress(showProgress());
    }

    @Override
    public Observable getObservable(Retrofit retrofit) {
        String json = JSON.toJSONString(requestParams());
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        return concreteObservable(retrofit, body);
    }

    @Override
    public void setRetryCount(int retryCount) {
        super.setRetryCount(5);
    }

    /**
     * 是否展示进度条
     *
     * @return
     */
    protected boolean showProgress() {
        return false;
    }

    /**
     * 由子类来返回要请求的参数对象
     *
     * @return
     */
    protected abstract Object requestParams();

    /**
     * 有子类来提供具体的Observable
     *
     * @param retrofit
     * @param body
     * @return
     */
    protected abstract Observable concreteObservable(Retrofit retrofit, RequestBody body);
}
