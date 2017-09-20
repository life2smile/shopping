package com.shopping.hanxiao.shopping.business.promotion;


import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import com.shopping.hanxiao.shopping.banner.BannerConfig;
import com.shopping.hanxiao.shopping.banner.listener.OnBannerListener;
import com.shopping.hanxiao.shopping.business.BaseFragment;
import com.shopping.hanxiao.shopping.business.TopBannerData;
import com.shopping.hanxiao.shopping.common.dialog.CustomDialog;
import com.shopping.hanxiao.shopping.loader.GlideImageLoader;
import com.shopping.hanxiao.shopping.rxretrofit.exception.ApiException;
import com.shopping.hanxiao.shopping.rxretrofit.http.HttpManager;
import com.shopping.hanxiao.shopping.rxretrofit.listener.HttpOnNextListener;
import com.shopping.hanxiao.shopping.utils.Constants;
import com.shopping.hanxiao.shopping.utils.ErrorUtils;
import com.shopping.hanxiao.shopping.utils.FloatActionButtonStatus;
import com.shopping.hanxiao.shopping.utils.ImageDownLoadUtil;
import com.shopping.hanxiao.shopping.utils.NumberFormatUtil;
import com.shopping.hanxiao.shopping.utils.ScreenInfoUtil;
import com.shopping.hanxiao.shopping.utils.SharePreferenceUtil;
import com.shopping.hanxiao.shopping.utils.StringUtils;
import com.shopping.hanxiao.shopping.utils.TextViewUtils;
import com.shopping.hanxiao.shopping.utils.ToastUtil;
import com.shopping.hanxiao.shopping.utils.UriParse;
import com.shopping.hanxiao.shopping.wrapper.HeaderAndFooterWrapper;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by wenzhi on 17/6/17.
 */

public class PromotionFragment extends BaseFragment {

    private final static int sCustomItems = 2;
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
    private TextView mSelectTv;
    private TextView mHotCommodityTv;
    private TextView mPromotionTv;
    private RecyclerView mHeightRecyclerView;
    private RecyclerView mNextHeightRecyclerView;
    private FloatingActionButton mActionBtn;
    private HeightHorizontalAdapter mHeightHorizontalAdapter;
    private HeightHorizontalAdapter mNextHeightHorizontalAdapter;
    private ImageView[] mCustomImageViews = new ImageView[sCustomItems];
    private TextView[] mCustomTextViews = new TextView[sCustomItems];
    private TextView[] mPromotionPriceTextViews = new TextView[sCustomItems];


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
        mActionBtn = (FloatingActionButton) view.findViewById(R.id.action_button);
        mActionBtn.setVisibility(SharePreferenceUtil.getData(getContext(), "removeAlways", false) ?
                View.GONE : View.VISIBLE);
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
                updateNextVerticalHeightItems(data.nextHeightItems);
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
        mTopBanner.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Constants.BANNER_HEIGHT));
        headerAndFooterWrapper.addHeaderView(banner);

        //底部轮播图下面的两个图片item区域
        View towImagesView = LayoutInflater.from(getActivity()).inflate(R.layout.item_two_images_horizontal, null);
        initTowImages(towImagesView);
        headerAndFooterWrapper.addHeaderView(towImagesView);

        //两个图片item下面的长高图片区域
        View itemView = LayoutInflater.from(getActivity()).inflate(R.layout.item_vertical_height_promotion, null);
        initVerticalHeightView(itemView);
        headerAndFooterWrapper.addHeaderView(itemView);

        //长高图片区域的下一个长高图片区域
        View nextItemView = LayoutInflater.from(getActivity()).inflate(R.layout.item_vertical_height_promotion, null);
        initNextVerticalHeightView(nextItemView);
        headerAndFooterWrapper.addHeaderView(nextItemView);

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
        mPromotionTv.setText("|品牌精选");
        mPromotionTv.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        mPromotionTv.setTextColor(getResources().getColor(R.color.base_main_text_color3));
    }

    private void initTowImages(View towImages) {
        mHotCommodityTv = (TextView) towImages.findViewById(R.id.header_text);
        int[] itemImages = new int[]{R.id.item_img1, R.id.item_img2};
        int[] itemTexts = new int[]{R.id.item_cover_img_desc1, R.id.item_cover_img_desc2};
        int[] originPrice = new int[]{R.id.item_price_tv1, R.id.item_price_tv2};
        for (int i = 0; i < sCustomItems; i++) {
            mCustomImageViews[i] = (ImageView) towImages.findViewById(itemImages[i]);
            mCustomTextViews[i] = (TextView) towImages.findViewById(itemTexts[i]);
            mPromotionPriceTextViews[i] = (TextView) towImages.findViewById(originPrice[i]);
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

    private void initNextVerticalHeightView(View view) {
        mSelectTv = (TextView) view.findViewById(R.id.header_text);
        mSelectTv.setText("|优选清单");
        mNextHeightRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
        layoutManager.setOrientation(GridLayoutManager.HORIZONTAL);
        mNextHeightRecyclerView.setLayoutManager(layoutManager);
        mNextHeightHorizontalAdapter = new HeightHorizontalAdapter(getActivity());
        mNextHeightRecyclerView.setAdapter(mNextHeightHorizontalAdapter);
    }

    private void updateTwoItems(List<CustomItemData> customItems) {
        RoundedCornersTransformation transformation = new RoundedCornersTransformation(getActivity(), ScreenInfoUtil.dpToPx(3), 0, RoundedCornersTransformation.CornerType.ALL);
        for (int i = 0; i < customItems.size() && i < sCustomItems; i++) {
            final CustomItemData data = customItems.get(i);
            mCustomTextViews[i].setText(data.description);
            if (data.promotionPrice != 0) {
                TextViewUtils.showTextView(mPromotionPriceTextViews[i], NumberFormatUtil.formatToRMB(data.promotionPrice));
            }
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

    private void updateNextVerticalHeightItems(List<CustomItemData> customItems) {
        if (!mLoadMore) {
            int visibility = customItems == null || customItems.size() == 0 ?
                    View.GONE : View.VISIBLE;
            mSelectTv.setVisibility(visibility);
            mNextHeightRecyclerView.setVisibility(visibility);
            mNextHeightHorizontalAdapter.addData(customItems);
            mNextHeightHorizontalAdapter.notifyDataSetChanged();
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

        mActionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), PromotionSearchActivity.class));
            }
        });

        mActionBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                CustomDialog dialog = new CustomDialog.DialogBuilder(getContext())
                        .setTitle("温馨提示")
                        .setMsg("确定移除悬浮搜索按钮吗？点击屏幕其他区域可取消操作。")
                        .setCancelBtn("仅本次移除", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SharePreferenceUtil.saveData(getContext(), "removeAlways", false);
                                FloatActionButtonStatus.showWhenRunning = false;
                                mActionBtn.setVisibility(View.GONE);
                            }
                        })
                        .setOkBtn("永久移除", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SharePreferenceUtil.saveData(getContext(), "removeAlways", true);
                                FloatActionButtonStatus.showWhenRunning = false;
                                mActionBtn.setVisibility(View.GONE);
                            }
                        })
                        .build();
                dialog.show();
                return true;
            }
        });
    }

    private void recoveryViewStatus() {
        mLoadMore = false;
        mRefreshing = false;
        mRefreshLayout.setRefreshing(false);//停止上拉刷新
        int visibility = mPromotionAdapter.getItemCount() > 0 ? View.VISIBLE : View.GONE;
        mRecommendTv.setVisibility(mHeightHorizontalAdapter.getItemCount() > 0 ? View.VISIBLE : View.GONE);//推荐secondheader
        mSelectTv.setVisibility(mNextHeightHorizontalAdapter.getItemCount() > 0 ? View.VISIBLE : View.GONE);
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
        this.mNextHeightHorizontalAdapter.clearData();
    }

    private void initTopBanner(List<TopBannerData> datas) {
        ArrayList<String> imagUrls = new ArrayList<>();
        Map<String, String> descMap = new HashMap<>();
        final ArrayList<String> actionUrls = new ArrayList<>();
        for (int i = 0; i < datas.size(); i++) {//取前四个数据作为topBanner
            TopBannerData data = datas.get(i);
            imagUrls.add(data.imgUrl);
            actionUrls.add(data.actionUrl);
            if (!TextUtils.isEmpty(data.description) || data.price != 0) {
                String bottomDesc = TextUtils.isEmpty(data.description) ?
                        NumberFormatUtil.formatToRMB(data.price)
                        : (data.price == 0 ? null
                        : StringUtils.truncateStringWithEllipsis(data.description, Constants.ELLIPSIS_NUMBER)
                        + NumberFormatUtil.formatToRMB(data.price));
                descMap.put(data.imgUrl, bottomDesc);
            }
        }

        mTopBanner.setImages(imagUrls)
                .setDescMap(descMap)
                .setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE)
                .setImageLoader(new GlideImageLoader(90, true))
                .setOnBannerListener(new OnBannerListener() {
                    @Override
                    public void OnBannerClick(int position) {
                        UriParse.startByWebView(getActivity(), actionUrls.get(position));
                    }
                })
                .start();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mActionBtn != null) {
            mActionBtn.setVisibility(FloatActionButtonStatus.showWhenRunning ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(mTopBanner != null){
            mTopBanner.releaseBanner();
        }
    }
}
