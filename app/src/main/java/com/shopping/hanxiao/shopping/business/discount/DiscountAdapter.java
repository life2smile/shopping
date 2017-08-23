package com.shopping.hanxiao.shopping.business.discount;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shopping.hanxiao.shopping.R;
import com.shopping.hanxiao.shopping.utils.ImageDownLoadUtil;
import com.shopping.hanxiao.shopping.utils.NumberFormatUtil;
import com.shopping.hanxiao.shopping.utils.TextViewUtils;
import com.shopping.hanxiao.shopping.utils.UriParse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wenzhi on 17/6/17.
 */

public class DiscountAdapter extends RecyclerView.Adapter<DiscountAdapter.CustomViewHolder> {

    private List<DiscountItemData> mData;
    private Context mContext;


    public DiscountAdapter(Context context) {
        mContext = context;
        mData = new ArrayList<>();
    }

    public DiscountAdapter(Context context, ArrayList<DiscountItemData> data) {
        mContext = context;
        mData = data;
    }

    public void addData(List<DiscountItemData> data) {
        mData.addAll(data);
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_discount, parent, false);
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
        final DiscountItemData data = mData.get(position);
        customViewHolder.mItemView.setTag(position);
        TextViewUtils.showTextView(customViewHolder.mContentTv, data.description);
        TextViewUtils.showTextView(customViewHolder.mTitleTv, data.title);
        TextViewUtils.showTextView(customViewHolder.mPlatformDesc, data.platformDesc);
        TextViewUtils.setTextViewVisibility(customViewHolder.mCouponPriceTv, NumberFormatUtil.formatToRMB(data.couponPrice), data.couponPrice > 0 ? View.VISIBLE : View.GONE);
        TextViewUtils.setTextViewVisibility(customViewHolder.mOriginPriceTv, NumberFormatUtil.formatToRMB(data.originPrice), data.originPrice > 0 ? View.VISIBLE : View.GONE);
        TextViewUtils.setTextViewVisibility(customViewHolder.mCouponValueTv, NumberFormatUtil.formatToRMB(data.discount), data.discount > 0 ? View.VISIBLE : View.GONE);
        TextViewUtils.setTextViewVisibility(customViewHolder.mNoteTv, null, data.discount > 0 ? View.VISIBLE : View.GONE);
        int paintFlag = data.couponPrice > 0 ? Paint.STRIKE_THRU_TEXT_FLAG : Paint.ANTI_ALIAS_FLAG;
        customViewHolder.mOriginPriceTv.getPaint().setFlags(paintFlag);
        ImageDownLoadUtil.downLoadImage(mContext, mData.get(position).imageUrl, customViewHolder.mCommodityImg);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void clearData() {
        if (mData != null) {
            mData.clear();
        }
    }

    /**
     * 提供自定义的Viewholder
     */
    public static class CustomViewHolder extends RecyclerView.ViewHolder {

        private View mItemView;
        private TextView mContentTv;
        private TextView mOriginPriceTv;
        private TextView mCouponPriceTv;
        private TextView mCouponValueTv;
        private TextView mPlatformDesc;
        private TextView mTitleTv;
        private TextView mNoteTv;
        private ImageView mCommodityImg;


        public CustomViewHolder(View itemView) {
            super(itemView);
            mItemView = itemView;
            mContentTv = (TextView) itemView.findViewById(R.id.item_content_tv);
            mTitleTv = (TextView) itemView.findViewById(R.id.item_title_tv);
            mCouponPriceTv = (TextView) itemView.findViewById(R.id.item_coupon_price_tv);
            mOriginPriceTv = (TextView) itemView.findViewById(R.id.item_origin_price_tv);
            mCouponValueTv = (TextView) itemView.findViewById(R.id.item_coupon_value_tv);

            mCommodityImg = (ImageView) itemView.findViewById(R.id.item_discount_img);
            mPlatformDesc = (TextView) itemView.findViewById(R.id.item_platform_img);
            mNoteTv = (TextView) itemView.findViewById(R.id.item_coupon_note);
        }
    }
}
