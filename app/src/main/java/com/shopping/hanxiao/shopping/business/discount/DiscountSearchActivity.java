package com.shopping.hanxiao.shopping.business.discount;

import android.view.View;

import com.alibaba.fastjson.JSON;
import com.shopping.hanxiao.shopping.common.search.BaseSearchActivity;
import com.shopping.hanxiao.shopping.common.search.SearchApi;
import com.shopping.hanxiao.shopping.rxretrofit.exception.ApiException;
import com.shopping.hanxiao.shopping.rxretrofit.http.HttpManager;
import com.shopping.hanxiao.shopping.rxretrofit.listener.HttpOnNextListener;
import com.shopping.hanxiao.shopping.utils.ErrorUtils;
import com.shopping.hanxiao.shopping.utils.ToastUtil;
import com.shopping.hanxiao.shopping.wrapper.HeaderAndFooterWrapper;

/**
 * Created by wenzhi on 17/9/18.
 */

public class DiscountSearchActivity extends BaseSearchActivity {
    private DiscountAdapter mDiscountAdapter;

    @Override
    public void doSearch(String type, int begin, int offset, String curKeyword) {
        SearchApi searchApi = new SearchApi(type, begin, offset, curKeyword);
        HttpManager manager = new HttpManager(new HttpOnNextListener() {
            @Override
            public void onNext(String result, String method) {
                mOldKeyword = mCurKeyword;
                DiscountData data = JSON.parseObject(result, DiscountData.class);
                mFooter.setVisibility(data.hasMore ? View.VISIBLE : View.GONE);
                if (data.list == null || data.list.size() == 0) {
                    ToastUtil.toast(DiscountSearchActivity.this, mLoadMore ? ErrorUtils.NO_DATA : ErrorUtils.NO_SEARCH_DATA);
                    return;
                }
                mDiscountAdapter.addData(data.list);
                mHeaderAndFooterWrapper.notifyDataSetChanged();
            }

            @Override
            public void onError(ApiException e) {
                ToastUtil.toast(DiscountSearchActivity.this, ErrorUtils.ERROR_MSG);
            }
        }, DiscountSearchActivity.this);
        manager.doHttpDeal(searchApi);
    }

    @Override
    public HeaderAndFooterWrapper getHeaderAndFooterWrapper() {
        if (mHeaderAndFooterWrapper == null) {
            if (mDiscountAdapter == null) {
                mDiscountAdapter = new DiscountAdapter(this);
            }
            mHeaderAndFooterWrapper = new HeaderAndFooterWrapper(mDiscountAdapter);
        }
        return mHeaderAndFooterWrapper;
    }

    @Override
    public void clearData() {
        if (mDiscountAdapter != null) {
            mDiscountAdapter.clearData();
        }
    }

    @Override
    public String type() {
        return "1";
    }

    @Override
    public int spanNum() {
        return 1;
    }
}