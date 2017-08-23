package com.shopping.hanxiao.shopping.version;

import com.shopping.hanxiao.shopping.ShoppingApplication;
import com.shopping.hanxiao.shopping.business.RequestApi;
import com.shopping.hanxiao.shopping.utils.VersionUtil;

import okhttp3.RequestBody;
import retrofit2.Retrofit;
import rx.Observable;

/**
 * Created by wenzhi on 17/6/20.
 */

public class VersionApi extends RequestApi {

    @Override
    public void setShowProgress(boolean showProgress) {
        super.setShowProgress(false);
    }

    @Override
    protected Object requestParams() {

        VersionRequestParams params = new VersionRequestParams();
        params.version = VersionUtil.version;
        params.deviceId = ShoppingApplication.deviceId;
        return params;
    }

    @Override
    protected Observable concreteObservable(Retrofit retrofit, RequestBody body) {
        VersionRequestService requestService = retrofit.create(VersionRequestService.class);
        return requestService.checkVersion(body);
    }
}
