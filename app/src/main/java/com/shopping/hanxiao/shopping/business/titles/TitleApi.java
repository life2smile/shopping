package com.shopping.hanxiao.shopping.business.titles;

import com.shopping.hanxiao.shopping.business.RequestApi;
import com.shopping.hanxiao.shopping.business.request.RequestParams;

import okhttp3.RequestBody;
import retrofit2.Retrofit;
import rx.Observable;

/**
 * Created by wenzhi on 17/6/20.
 */

public class TitleApi extends RequestApi {
    @Override
    protected Object requestParams() {
        RequestParams params = new RequestParams();
        return params;
    }

    @Override
    protected Observable concreteObservable(Retrofit retrofit, RequestBody body) {
        TitlesRequestService requestService = retrofit.create(TitlesRequestService.class);
        return requestService.getTitles(body);
    }

    @Override
    protected boolean showProgress() {
        return true;
    }
}
