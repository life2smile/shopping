package com.shopping.hanxiao.shopping.version;

import com.shopping.hanxiao.shopping.utils.NetWorkUtils;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by wenzhi on 17/6/20.
 */

public interface VersionRequestService {
    @Headers({NetWorkUtils.CONTENT_TYPE, NetWorkUtils.ACCEPT_TYPE})
    @POST(NetWorkUtils.CHECK_VERSION)
    Observable<String> checkVersion(@Body RequestBody body);
}
