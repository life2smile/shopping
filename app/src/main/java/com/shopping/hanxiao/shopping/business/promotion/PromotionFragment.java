package com.shopping.hanxiao.shopping.business.promotion;


import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.shopping.hanxiao.shopping.R;
import com.shopping.hanxiao.shopping.banner.Banner;
import com.shopping.hanxiao.shopping.banner.listener.OnBannerListener;
import com.shopping.hanxiao.shopping.business.BaseFragment;
import com.shopping.hanxiao.shopping.business.TopBannerData;
import com.shopping.hanxiao.shopping.loader.GlideImageLoader;
import com.shopping.hanxiao.shopping.rxretrofit.exception.ApiException;
import com.shopping.hanxiao.shopping.rxretrofit.http.HttpManager;
import com.shopping.hanxiao.shopping.rxretrofit.listener.HttpOnNextListener;
import com.shopping.hanxiao.shopping.utils.Constants;
import com.shopping.hanxiao.shopping.utils.ErrorUtils;
import com.shopping.hanxiao.shopping.utils.ImageDownLoadUtil;
import com.shopping.hanxiao.shopping.utils.ScreenInfoUtil;
import com.shopping.hanxiao.shopping.utils.ToastUtil;
import com.shopping.hanxiao.shopping.utils.UriParse;
import com.shopping.hanxiao.shopping.wrapper.HeaderAndFooterWrapper;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by wenzhi on 17/6/17.
 */

public class PromotionFragment extends BaseFragment {

    private final static int customItems = 4;
    private final static int spanNum = 1;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mRefreshLayout;
    private int mBegin = 0;
    private int mOffset = 20;
    private PromotionAdapter mPromotionAdapter;
    private HeaderAndFooterWrapper mHeaderAndFooterWrapper;
    private View mFooter;
    private boolean mLoadMore;
    private boolean mRefreshing;
    private TextView mRefreshTv;
    private Banner mTopBanner;
    private TextView mRecommendTv;
    private TextView mHotCommodityTv;
    private TextView mPromotionTv;
    private RecyclerView mHeightRecyclerView;
    private HeightHorizontalAdapter mHeightHorizontalAdapter;
    private ImageView[] mCustomImageViews = new ImageView[customItems];
    private TextView[] mCustomTextViews = new TextView[customItems];


    public static PromotionFragment newInstance() {
        return new PromotionFragment();
    }

    @Override
    protected int getContentLayout() {
        return R.layout.promotion_fragment_main;
    }

    @Override
    protected void initViews(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), spanNum));
        mPromotionAdapter = new PromotionAdapter(getActivity());
        mHeaderAndFooterWrapper = new HeaderAndFooterWrapper(mPromotionAdapter);
        mRefreshTv = (TextView) view.findViewById(R.id.refresh_tv);
        setListeners();
        initRefreshLayout(view);
        initHeaderView(mHeaderAndFooterWrapper);
        initFooterView(mHeaderAndFooterWrapper);
        mRecyclerView.setAdapter(mHeaderAndFooterWrapper);
    }

    @Override
    protected int titleColor() {
        return R.color.colorRed;
    }

    @Override
    protected void requestDataFromServer() {
        HttpManager manager = new HttpManager(new HttpOnNextListener() {
            @Override
            public void onNext(String result, String method) {
                PromotionData data = JSON.parseObject(result, PromotionData.class);
                initTopBanner(data.topBanner);
                updateTwoItems(data.twoItems);
                updateVerticalHeightItems(data.heightItems);
                mFooter.setVisibility(data.hasMore ? View.VISIBLE : View.GONE);
                if (mLoadMore && (data.list == null || data.list.size() == 0)) {
                    ToastUtil.toast(getActivity(), ErrorUtils.NO_DATA);
                    recoveryViewStatus();
                    return;
                }
                mPromotionAdapter.addData(data.list);
                mHeaderAndFooterWrapper.notifyDataSetChanged();
                recoveryViewStatus();
            }

            @Override
            public void onError(ApiException e) {
                ToastUtil.toast(getActivity(), ErrorUtils.ERROR_MSG);
                recoveryViewStatus();
                if (!mLoadMore) {
                    mRefreshTv.setVisibility(View.VISIBLE);
                }
            }
        }, (RxAppCompatActivity) getActivity());
        PromotionApi promotionApi = new PromotionApi(null, mBegin, mOffset);
        promotionApi.setShowProgress(!mRefreshing);
        manager.doHttpDeal(promotionApi);
    }

    private void initLoadMore() {
        mFooter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadMore = true;
                mFooter.setVisibility(View.GONE);
                mBegin = mPromotionAdapter.getItemCount();//累计数据，作为下次要查询的起点
                requestDataFromServer();
            }
        });
    }


    private void initRefreshLayout(View view) {
        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                resetRequestStatus();
                mRefreshing = true;
                requestDataFromServer();
            }
        });
    }

    private void initHeaderView(HeaderAndFooterWrapper headerAndFooterWrapper) {
        //顶部轮播图
        View banner = LayoutInflater.from(getActivity()).inflate(R.layout.header, null);
        mTopBanner = (Banner) banner.findViewById(R.id.banner);
        mTopBanner.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Constants.bannerHeight));
        headerAndFooterWrapper.addHeaderView(banner);

        //底部轮播图下面的两个图片item区域
        View towImagesView = LayoutInflater.from(getActivity()).inflate(R.layout.item_two_images_horizontal, null);
        initTowImages(towImagesView);
        headerAndFooterWrapper.addHeaderView(towImagesView);

        //两个图片item下面的长高图片区域
        View itemView = LayoutInflater.from(getActivity()).inflate(R.layout.item_vertical_height_promotion, null);
        initVerticalHeightView(itemView);
        headerAndFooterWrapper.addHeaderView(itemView);

        //活动商品上面的文案信息
        View promotionView = LayoutInflater.from(getActivity()).inflate(R.layout.header_second, null);
        initPromotionView(promotionView);
        headerAndFooterWrapper.addHeaderView(promotionView);
    }

    private void initPromotionView(View promotionView) {
        int defaultPadding = ScreenInfoUtil.dpToPx(5);
        mPromotionTv = (TextView) promotionView.findViewById(R.id.header_text);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        mPromotionTv.setVisibility(View.VISIBLE);
        mPromotionTv.setLayoutParams(params);
        mPromotionTv.setPadding(defaultPadding, ScreenInfoUtil.dpToPx(10), 0, ScreenInfoUtil.dpToPx(8));
        mPromotionTv.setText("|良品精选");
        mPromotionTv.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        mPromotionTv.setTextColor(getResources().getColor(R.color.base_main_text_color3));
    }

    private void initTowImages(View towImages) {
        mHotCommodityTv = (TextView) towImages.findViewById(R.id.header_text);
        int[] itemImages = new int[]{R.id.item_img1, R.id.item_img2, R.id.item_img3, R.id.item_img4};
        int[] itemTexts = new int[]{R.id.item_cover_img_desc1, R.id.item_cover_img_desc2, R.id.item_cover_img_desc3, R.id.item_cover_img_desc4};
        for (int i = 0; i < customItems; i++) {
            mCustomImageViews[i] = (ImageView) towImages.findViewById(itemImages[i]);
            mCustomTextViews[i] = (TextView) towImages.findViewById(itemTexts[i]);
        }
    }

    private void initVerticalHeightView(View view) {
        mRecommendTv = (TextView) view.findViewById(R.id.header_text);
        mHeightRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
        layoutManager.setOrientation(GridLayoutManager.HORIZONTAL);
        mHeightRecyclerView.setLayoutManager(layoutManager);
        mHeightHorizontalAdapter = new HeightHorizontalAdapter(getActivity());
        mHeightRecyclerView.setAdapter(mHeightHorizontalAdapter);
    }

    private void updateTwoItems(List<CustomItemData> customItems) {
        RoundedCornersTransformation transformation = new RoundedCornersTransformation(getActivity(), ScreenInfoUtil.dpToPx(3), 0, RoundedCornersTransformation.CornerType.ALL);
        for (int i = 0; i < customItems.size(); i++) {
            final CustomItemData data = customItems.get(i);
            mCustomTextViews[i].setText(data.description);
            ImageDownLoadUtil.downLoadImage(getActivity(), data.imgUrl, mCustomImageViews[i], transformation);
            mCustomImageViews[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UriParse.startByWebView(getActivity(), data.actionUrl);
                }
            });
        }
    }

    private void updateVerticalHeightItems(List<CustomItemData> customItems) {
        if (!mLoadMore) {
            mHeightHorizontalAdapter.addData(customItems);
            mHeightHorizontalAdapter.notifyDataSetChanged();
        }
    }

    private void initFooterView(HeaderAndFooterWrapper headerAndFooterWrapper) {
        mFooter = LayoutInflater.from(getActivity()).inflate(R.layout.footer, null);
        headerAndFooterWrapper.addFootView(mFooter);
        initLoadMore();
    }

    private void setListeners() {
        mRefreshTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRefreshTv.setVisibility(View.GONE);
                resetRequestStatus();
                requestDataFromServer();
            }
        });
    }

    private void recoveryViewStatus() {
        mLoadMore = false;
        mRefreshing = false;
        mRefreshLayout.setRefreshing(false);//停止上拉刷新
        int visibility = mPromotionAdapter.getItemCount() > 0 ? View.VISIBLE : View.GONE;
        mRecommendTv.setVisibility(visibility);//推荐secondheader
        mHotCommodityTv.setVisibility(visibility);
        mPromotionTv.setVisibility(visibility);
    }

    private void resetRequestStatus() {
        this.mBegin = 0;
        this.mOffset = 20;
        this.mLoadMore = false;
        this.mRefreshing = false;
        this.mPromotionAdapter.clearData();
        this.mHeaderAndFooterWrapper.notifyDataSetChanged();
        this.mFooter.setVisibility(View.GONE);
        this.mHeightHorizontalAdapter.clearData();
    }

    private void initTopBanner(List<TopBannerData> data) {
        ArrayList<String> imagUrls = new ArrayList<>();
        final ArrayList<String> actionUrls = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {//取前四个数据作为topBanner
            imagUrls.add(data.get(i).imgUrl);
            actionUrls.add(data.get(i).actionUrl);
        }

        mTopBanner.setImages(imagUrls)
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
