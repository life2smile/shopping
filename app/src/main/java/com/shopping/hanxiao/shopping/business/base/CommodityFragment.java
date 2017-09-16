package com.shopping.hanxiao.shopping.business.base;


import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.shopping.hanxiao.shopping.R;
import com.shopping.hanxiao.shopping.banner.Banner;
import com.shopping.hanxiao.shopping.banner.BannerConfig;
import com.shopping.hanxiao.shopping.banner.listener.OnBannerListener;
import com.shopping.hanxiao.shopping.business.BaseFragment;
import com.shopping.hanxiao.shopping.business.TopBannerData;
import com.shopping.hanxiao.shopping.business.coupon.CouponFragment;
import com.shopping.hanxiao.shopping.business.coupon.CouponItemTitle;
import com.shopping.hanxiao.shopping.business.titles.TitleApi;
import com.shopping.hanxiao.shopping.common.WebViewActivity;
import com.shopping.hanxiao.shopping.common.WebViewContext;
import com.shopping.hanxiao.shopping.common.search.SearchActivity;
import com.shopping.hanxiao.shopping.loader.GlideImageLoader;
import com.shopping.hanxiao.shopping.rxretrofit.exception.ApiException;
import com.shopping.hanxiao.shopping.rxretrofit.http.HttpManager;
import com.shopping.hanxiao.shopping.rxretrofit.listener.HttpOnNextListener;
import com.shopping.hanxiao.shopping.tablayout.toptab.ViewPagerAdapter;
import com.shopping.hanxiao.shopping.utils.Constants;
import com.shopping.hanxiao.shopping.utils.ErrorUtils;
import com.shopping.hanxiao.shopping.utils.NumberFormatUtil;
import com.shopping.hanxiao.shopping.utils.ScreenInfoUtil;
import com.shopping.hanxiao.shopping.utils.StringUtils;
import com.shopping.hanxiao.shopping.utils.ToastUtil;
import com.shopping.hanxiao.shopping.utils.UriParse;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wenzhi on 17/6/17.
 */

public class CommodityFragment extends BaseFragment {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private TextView mRefreshTv;
    private Banner mTopBanner;
    private AppBarLayout mAppBarLayout;
    private Toolbar mToolbar;
    private View mToobarBackground;
    private ImageView mHelpImg;

    public static BaseFragment newInstance() {
        return new CommodityFragment();
    }

    @Override
    protected int getContentLayout() {
        return R.layout.tab_top_indicator;
    }

    @Override
    protected void initViews(View view) {
        mTabLayout = (TabLayout) view.findViewById(R.id.tablayout);
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        mRefreshTv = (TextView) view.findViewById(R.id.refresh_tv);
        mTopBanner = (Banner) view.findViewById(R.id.banner);
        mTopBanner.setLayoutParams(new CollapsingToolbarLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Constants.FIRST_BANNER_HEIGHT));
        mAppBarLayout = (AppBarLayout) view.findViewById(R.id.app_bar_layout);
        mToolbar = (Toolbar) view.findViewById(R.id.tool_bar);
        mToobarBackground = view.findViewById(R.id.toolbar_background);
        mHelpImg = (ImageView) view.findViewById(R.id.img_help);
        setListeners();
    }

    private void setListeners() {
        mRefreshTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRefreshTv.setVisibility(View.GONE);
                requestDataFromServer();
            }
        });

        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                float bannerHeight = mTopBanner.getMeasuredHeight() - mToobarBackground.getMeasuredHeight() - ScreenInfoUtil.dpToPx(5);
                verticalOffset = Math.abs(verticalOffset);
                mHelpImg.setVisibility(verticalOffset > bannerHeight ? View.VISIBLE : View.GONE);
                if (verticalOffset == 0) {
                    mToobarBackground.setAlpha(0);
                    mToolbar.setAlpha(0);
                    mToolbar.setVisibility(View.GONE);
                    mToobarBackground.setVisibility(View.GONE);
                    return;
                }

                if (verticalOffset > bannerHeight) {
                    mToobarBackground.setAlpha(1);
                    mToolbar.setAlpha(1);
                    mToolbar.setVisibility(View.VISIBLE);
                    mToobarBackground.setVisibility(View.VISIBLE);
                    return;
                }

                if (verticalOffset < bannerHeight) {
                    float alpha = verticalOffset / bannerHeight;
                    mToobarBackground.setAlpha(alpha);
                    mToolbar.setAlpha(alpha);
                    mToolbar.setVisibility(View.GONE);
                    mToobarBackground.setVisibility(View.GONE);
                }
            }
        });

        mToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SearchActivity.class));
            }
        });

        mToobarBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do nothing
            }
        });
    }

    @Override
    protected void requestDataFromServer() {
        requestTitles();
    }

    private void requestTitles() {
        HttpManager manager = new HttpManager(new HttpOnNextListener() {
            @Override
            public void onNext(String result, String method) {
                CommodityBaseData baseData = JSON.parseObject(result, CommodityBaseData.class);
                updateViews(baseData);
            }

            @Override
            public void onError(ApiException e) {
                Log.d("error:", e.getMessage());
                ToastUtil.toast(getActivity(), ErrorUtils.ERROR_MSG);
                mRefreshTv.setVisibility(View.VISIBLE);
            }
        }, (RxAppCompatActivity) getActivity());
        manager.doHttpDeal(new TitleApi());
    }

    private void updateViews(CommodityBaseData data) {
        ArrayList<Fragment> fragments = new ArrayList<>();
        ArrayList<String> titles = new ArrayList<>();
        for (CouponItemTitle itemTitle : data.titles) {
            titles.add(itemTitle.title);
            fragments.add(CouponFragment.newInstance(itemTitle.type));
        }
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager(), getActivity(),
                fragments, titles);
        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(titles.size());
        mTabLayout.setupWithViewPager(mViewPager);
        initTopBanner(data.topBanners);
        registerHelpListener(data.helpLink);
    }

    private void registerHelpListener(final String helpLink) {
        mHelpImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(WebViewContext.URI, helpLink);
                intent.putExtra(WebViewContext.TITLE, "券立减答疑");
                intent.putExtra(WebViewContext.LEFT_IMAGE_RES_ID, R.drawable.back_arrow);
                intent.setClass(getActivity(), WebViewActivity.class);
                UriParse.startByWebView(getContext(), intent);
            }
        });
    }

    private void initTopBanner(List<TopBannerData> datas) {
        ArrayList<String> imgUrls = new ArrayList<>();
        Map<String, String> descMap = new HashMap<>();
        final ArrayList<String> actionUrls = new ArrayList<>();
        for (int i = 0; i < datas.size(); i++) {//取前四个数据作为topBanner
            TopBannerData data = datas.get(i);
            imgUrls.add(data.imgUrl);
            actionUrls.add(data.actionUrl);
            if (!TextUtils.isEmpty(data.description) || data.price != 0) {
                String bottomDesc = TextUtils.isEmpty(data.description) ?
                        NumberFormatUtil.formatToRMB(data.price)
                        : (data.price == 0 ? null
                        : StringUtils.truncateStringWithEllipsis(data.description, Constants.ELLIPSIS_NUMBER)
                        + NumberFormatUtil.formatToRMB(data.price));
                descMap.put(data.imgUrl, bottomDesc);
            }
            mTopBanner.setImages(imgUrls)
                    .setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE)
                    .setDescMap(descMap)
                    .setImageLoader(new GlideImageLoader(90, true))
                    .setOnBannerListener(new OnBannerListener() {
                    @Override
                    public void OnBannerClick(int position) {
                        UriParse.startByWebView(getActivity(), actionUrls.get(position));
                    }
                    })
                    .start();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mTopBanner != null) {
            mTopBanner.releaseBanner();
        }
    }

}
