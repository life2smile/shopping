package com.shopping.hanxiao.shopping.rxretrofit.listener;


import com.shopping.hanxiao.shopping.rxretrofit.exception.ApiException;

/**
 * 成功回调处理
 */
public interface  HttpOnNextListener {
    /**
     * 成功后回调方法
     * @param result
     * @param method
     */
   void onNext(String result, String method);

    /**
     * 失败
     * 失败或者错误方法
     * 自定义异常处理
     * @param e
     */
    void onError(ApiException e);
}
