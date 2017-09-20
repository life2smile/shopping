package com.shopping.hanxiao.shopping.business.coupon;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.shopping.hanxiao.shopping.R;
import com.shopping.hanxiao.shopping.business.BaseFragment;
import com.shopping.hanxiao.shopping.rxretrofit.exception.ApiException;
import com.shopping.hanxiao.shopping.rxretrofit.http.HttpManager;
import com.shopping.hanxiao.shopping.rxretrofit.listener.HttpOnNextListener;
import com.shopping.hanxiao.shopping.utils.ErrorUtils;
import com.shopping.hanxiao.shopping.utils.ToastUtil;
import com.shopping.hanxiao.shopping.wrapper.HeaderAndFooterWrapper;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

/**
 * Created by wenzhi on 17/6/17.
 */

public class CouponFragment extends BaseFragment {

    private final static int spanNum = 2;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mRefreshLayout;
    private CouponAdapter mCouponAdapter;
    private String mType = "0";
    private int mBegin = 0;
    private int mOffset = 20;
    private HeaderAndFooterWrapper mHeaderAndFooterWrapper;
    private View mFooter;
    private boolean mLoadMore;
    private TextView mRefreshTv;

    public static CouponFragment newInstance(String type) {
        CouponFragment fragment = new CouponFragment();
        Bundle args = new Bundle();
        args.putString("type", type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mType = getArguments() != null ? getArguments().getString("type") : "0";
    }

    @Override
    protected int getContentLayout() {
        return R.layout.coupon_fragment_main;
    }

    @Override
    protected void initViews(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), spanNum));
        mCouponAdapter = new CouponAdapter(getActivity());
        mHeaderAndFooterWrapper = new HeaderAndFooterWrapper(mCouponAdapter);
        mRefreshTv = (TextView) view.findViewById(R.id.refresh_tv);

        initRefreshLayout(view);//上拉刷新

        initFooterView(mHeaderAndFooterWrapper);//添加recycleview中的footer

        setListeners(mRefreshTv);

        mRecyclerView.setAdapter(mHeaderAndFooterWrapper);
    }

    private void initFooterView(HeaderAndFooterWrapper headerAndFooterWrapper) {
        mFooter = LayoutInflater.from(getActivity()).inflate(R.layout.footer, null);
        headerAndFooterWrapper.addFootView(mFooter);
        initLoadMore();//下拉加载更多
    }

    private void initRefreshLayout(View view) {
        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                resetRequestStatus();
                requestDataFromServer();
            }
        });
    }

    private void setListeners(TextView tv) {
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRefreshTv.setVisibility(View.GONE);
                resetRequestStatus();
                requestDataFromServer();
            }
        });
    }


    @Override
    protected void requestDataFromServer() {
        CouponApi commodityApi = new CouponApi(mType, mBegin, mOffset);
        if (mLoadMore) {
            commodityApi.setShowProgress(true);
        }
        HttpManager manager = new HttpManager(new HttpOnNextListener() {
            @Override
            public void onNext(String result, String method) {
                CouponData data = JSON.parseObject(result, CouponData.class);
                mFooter.setVisibility(data.hasMore ? View.VISIBLE : View.GONE);
                if ((data.list == null || data.list.size() == 0) && mLoadMore) {
                    ToastUtil.toast(getActivity(), ErrorUtils.NO_DATA);
                    recoveryViewStatus();
                    return;
                }
                mCouponAdapter.addData(data.list);
                mHeaderAndFooterWrapper.notifyDataSetChanged();
                recoveryViewStatus();
            }

            @Override
            public void onError(ApiException e) {
                if ("0".equals(mType)) {
                    ToastUtil.toast(getActivity(), ErrorUtils.ERROR_MSG);
                }
                recoveryViewStatus();
                if (!mLoadMore) {
                    mRefreshTv.setVisibility(View.VISIBLE);
                }
            }
        }, (RxAppCompatActivity) getActivity());
        manager.doHttpDeal(commodityApi);
    }

    private void recoveryViewStatus() {
        mLoadMore = false;
        mRefreshLayout.setRefreshing(false);//停止上拉刷新
    }

    private void initLoadMore() {
        mFooter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadMore = true;
                mFooter.setVisibility(View.GONE);
                mBegin = mCouponAdapter.getItemCount();//获取当前所有的数据数，作为起点查询
                requestDataFromServer();
            }
        });
    }

    private void resetRequestStatus() {
        this.mBegin = 0;
        this.mOffset = 20;
        this.mCouponAdapter.clearData();
        this.mHeaderAndFooterWrapper.notifyDataSetChanged();//clear data之后一定要通知数据变化
        this.mLoadMore = false;
        this.mFooter.setVisibility(View.GONE);
    }
}
