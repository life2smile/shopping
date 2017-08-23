package com.shopping.hanxiao.shopping.business.coupon;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shopping.hanxiao.shopping.R;
import com.shopping.hanxiao.shopping.business.coupon.data.CouponItemData;
import com.shopping.hanxiao.shopping.utils.ImageDownLoadUtil;
import com.shopping.hanxiao.shopping.utils.NetWorkUtils;
import com.shopping.hanxiao.shopping.utils.NumberFormatUtil;
import com.shopping.hanxiao.shopping.utils.ScreenInfoUtil;
import com.shopping.hanxiao.shopping.utils.UriParse;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by wenzhi on 17/6/17.
 */

public class CouponAdapter extends RecyclerView.Adapter<CouponAdapter.CustomViewHolder> {

    private List<CouponItemData> mData;
    private Context mContext;


    public CouponAdapter(Context context) {
        mContext = context;
        mData = new ArrayList<>();
    }

    public CouponAdapter(Context context, List<CouponItemData> data) {
        mContext = context;
        mData = data;
    }

    public void addData(List<CouponItemData> data) {
        mData.addAll(data);
    }

    public void clearData() {
        mData.clear();
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_coupon, parent, false);
        CustomViewHolder customViewHolder = new CustomViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UriParse.startByWebView(mContext, mData.get((int) v.getTag()).actionUrl);
            }
        });
        return customViewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        final CustomViewHolder customViewHolder = holder;
        final CouponItemData data = mData.get(position);
        customViewHolder.mItemView.setTag(position);
        customViewHolder.mDescTv.setText(data.description);
        customViewHolder.mCouponValueTv.setText(NumberFormatUtil.formatToRMB(data.couponValue));
        customViewHolder.mCouponPriceTv.setText(NumberFormatUtil.formatToRMB(data.couponPrice));
        customViewHolder.mOriginPriceTv.setText(NumberFormatUtil.formatToRMB(data.originPrice));
        customViewHolder.mOriginPriceTv.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        ImageDownLoadUtil.downLoadImage(mContext, NetWorkUtils.PLATFORM_ICON_PREFIX + mData.get(position).platformImg, customViewHolder.mPlatformImg);
        RoundedCornersTransformation transformation = new RoundedCornersTransformation(mContext, ScreenInfoUtil.dpToPx(4), 0, RoundedCornersTransformation.CornerType.TOP);
        ImageDownLoadUtil.downLoadImage(mContext, mData.get(position).imageUrl, customViewHolder.mCommodityImg, transformation);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    /**
     * 提供自定义的Viewholder
     */
    public static class CustomViewHolder extends RecyclerView.ViewHolder {
        private TextView mDescTv;
        private TextView mOriginPriceTv;
        private TextView mCouponPriceTv;
        private TextView mCouponValueTv;
        private ImageView mPlatformImg;
        private ImageView mCommodityImg;
        private View mItemView;

        public CustomViewHolder(View itemView) {
            super(itemView);
            mItemView = itemView;
            mDescTv = (TextView) itemView.findViewById(R.id.item_desc_tv);
            mCouponPriceTv = (TextView) itemView.findViewById(R.id.item_coupon_price_tv);
            mOriginPriceTv = (TextView) itemView.findViewById(R.id.item_origin_price_tv);
            mCouponValueTv = (TextView) itemView.findViewById(R.id.item_coupon_value_tv);

            mCommodityImg = (ImageView) itemView.findViewById(R.id.item_commodity_img);
            mPlatformImg = (ImageView) itemView.findViewById(R.id.item_platform_img);
        }
    }
}
