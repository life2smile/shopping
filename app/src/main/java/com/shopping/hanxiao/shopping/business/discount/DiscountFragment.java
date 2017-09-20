package com.shopping.hanxiao.shopping.business.discount;


import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import com.shopping.hanxiao.shopping.common.search.BaseSearchActivity;
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
import com.shopping.hanxiao.shopping.utils.ToastUtil;
import com.shopping.hanxiao.shopping.utils.UriParse;
import com.shopping.hanxiao.shopping.wrapper.HeaderAndFooterWrapper;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wenzhi on 17/6/17.
 */

public class DiscountFragment extends BaseFragment {

    private final static int spanNum = 1;
    private final static int fixItemNum = 5;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mRefreshLayout;
    private DiscountAdapter mDiscountAdapter;
    private int mBegin = 0;
    private int mOffset = 20;
    private Banner mTopBanner;
    private HeaderAndFooterWrapper mHeaderAndFooterWrapper;
    private View mFooter;
    private TextView mRightView;
    private String mHelpLink;
    private boolean mLoadMore;
    private boolean mRefreshing;
    private TextView mRefreshTv;
    private TextView mFindTv;
    private FloatingActionButton mActionBtn;
    private ImageView[] mFixImages = new ImageView[fixItemNum];
    private TextView[] mFixTextView = new TextView[fixItemNum];


    public static DiscountFragment newInstance() {
        return new DiscountFragment();
    }

    @Override
    protected int getContentLayout() {
        return R.layout.discount_fragment_main;
    }

    @Override
    protected void initViews(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), spanNum));
        mDiscountAdapter = new DiscountAdapter(getActivity());
        mHeaderAndFooterWrapper = new HeaderAndFooterWrapper(mDiscountAdapter);
        mRefreshTv = (TextView) view.findViewById(R.id.refresh_tv);
        mActionBtn = (FloatingActionButton) view.findViewById(R.id.action_button);
        mActionBtn.setVisibility(SharePreferenceUtil.getData(getContext(), "removeAlways", false) ?
                View.GONE : View.VISIBLE);

        initRefreshLayout(view);

        initHeaderView(mHeaderAndFooterWrapper);

        initFooterView(mHeaderAndFooterWrapper);

        setListeners();

        mRecyclerView.setAdapter(mHeaderAndFooterWrapper);
    }

    @Override
    protected void requestDataFromServer() {
        HttpManager manager = new HttpManager(new HttpOnNextListener() {
            @Override
            public void onNext(String result, String method) {
                DiscountData data = JSON.parseObject(result, DiscountData.class);
                initTopBanner(data.topBanner);
                updateFixItemView(data.fixItems);
                mFooter.setVisibility(data.hasMore ? View.VISIBLE : View.GONE);
                mHelpLink = data.helpLink;
                mRightView.setVisibility(mHelpLink != null ? View.VISIBLE : View.GONE);
                if (mLoadMore && (data.list == null || data.list.size() == 0)) {
                    ToastUtil.toast(getActivity(), ErrorUtils.NO_DATA);
                    recoveryViewStatus();
                    return;
                }
                mDiscountAdapter.addData(data.list);
                mHeaderAndFooterWrapper.notifyDataSetChanged();
                recoveryViewStatus();
            }

            @Override
            public void onError(ApiException e) {
                System.out.println(e.getDisplayMessage());
                ToastUtil.toast(getActivity(), ErrorUtils.ERROR_MSG);
                recoveryViewStatus();
                if (!mLoadMore) {
                    mRefreshTv.setVisibility(View.VISIBLE);
                }
            }
        }, (RxAppCompatActivity) getActivity());
        DiscountApi discountApi = new DiscountApi(null, mBegin, mOffset);
        discountApi.showProgress(!mRefreshing);
        manager.doHttpDeal(discountApi);
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

    private void initLoadMore() {
        mFooter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadMore = true;
                mFooter.setVisibility(View.GONE);
                mBegin = mDiscountAdapter.getItemCount();//累计数据，作为下次要查询的起点
                requestDataFromServer();
            }
        });
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
                startActivity(new Intent(getActivity(), DiscountSearchActivity.class));
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

    private void resetRequestStatus() {
        this.mBegin = 0;
        this.mOffset = 20;
        this.mLoadMore = false;
        this.mRefreshing = false;
        this.mDiscountAdapter.clearData();
        this.mHeaderAndFooterWrapper.notifyDataSetChanged();
        this.mFooter.setVisibility(View.GONE);
    }

    private void updateFixItemView(List<DiscountItemData> list) {
        int i = 0;
        for (final DiscountItemData itemData : list) {
            ImageDownLoadUtil.downLoadImage(getActivity(), itemData.imageUrl, mFixImages[i]);
            mFixImages[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UriParse.startByWebView(getActivity(), itemData.actionUrl);
                }
            });

            //设置text后自增
            mFixTextView[i++].setText(itemData.description);
        }
    }

    private void initHeaderView(HeaderAndFooterWrapper headerAndFooterWrapper) {
        //顶部轮播图
        View header = LayoutInflater.from(getActivity()).inflate(R.layout.header, null);
        mTopBanner = (Banner) header.findViewById(R.id.banner);
        mTopBanner.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Constants.BANNER_HEIGHT));
        headerAndFooterWrapper.addHeaderView(header);

        //轮播图下面的5图片区域
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_fix_five_item, null);
        initFixItemView(view);
        headerAndFooterWrapper.addHeaderView(view);

        //发现专区
        //活动商品上面的文案信息
        View findView = LayoutInflater.from(getActivity()).inflate(R.layout.header_second, null);
        initFindView(findView);
//        headerAndFooterWrapper.addHeaderView(findView);

    }

    private void initFindView(View findView) {
        int defaultPadding = ScreenInfoUtil.dpToPx(10);
        mFindTv = (TextView) findView.findViewById(R.id.header_text);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        mFindTv.setVisibility(View.VISIBLE);
        mFindTv.setLayoutParams(params);
        mFindTv.setPadding(defaultPadding, defaultPadding, 0, ScreenInfoUtil.dpToPx(8));
        mFindTv.setText("|发现专区");
        mFindTv.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        mFindTv.setTextColor(getResources().getColor(R.color.base_main_text_color3));
    }

    private void initFixItemView(View view) {
        int[] imageIds = new int[]{R.id.img_left, R.id.img_right_top_left, R.id.img_right_top_right,
                R.id.img_right_bottom_left, R.id.img_right_bottom_right};
        int[] tvIds = new int[]{R.id.tv_left, R.id.tv_right_top_left, R.id.tv_right_top_right,
                R.id.tv_right_bottom_left, R.id.tv_right_bottom_right};

        for (int i = 0; i < fixItemNum; i++) {
            mFixImages[i] = (ImageView) view.findViewById(imageIds[i]);
            mFixTextView[i] = (TextView) view.findViewById(tvIds[i]);
        }
    }

    private void initFooterView(HeaderAndFooterWrapper headerAndFooterWrapper) {
        mFooter = LayoutInflater.from(getActivity()).inflate(R.layout.footer, null);
        headerAndFooterWrapper.addFootView(mFooter);
        initLoadMore();
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


    private void recoveryViewStatus() {
        mLoadMore = false;
        mRefreshing = false;
        mRefreshLayout.setRefreshing(false);//停止上拉刷新
        mFindTv.setVisibility(mDiscountAdapter.getItemCount() > 0 ? View.VISIBLE : View.GONE);
    }

    private void initHelpIcon(TextView rightView) {
        Drawable drawable = getResources().getDrawable(R.drawable.help_icon_origin);
        rightView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        rightView.setPadding(ScreenInfoUtil.dpToPx(14), 0, ScreenInfoUtil.dpToPx(14), 0);
        rightView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getActivity(), BaseSearchActivity.class));
            }
        });
        mRightView = rightView;
    }


    @Override
    protected int titleColor() {
        return R.color.colorRed;
    }

    @Override
    protected void customRightView(TextView rightView) {
        initHelpIcon(rightView);
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
