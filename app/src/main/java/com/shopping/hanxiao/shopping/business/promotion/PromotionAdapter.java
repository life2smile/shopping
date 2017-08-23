package com.shopping.hanxiao.shopping.business.promotion;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shopping.hanxiao.shopping.R;
import com.shopping.hanxiao.shopping.utils.ImageDownLoadUtil;
import com.shopping.hanxiao.shopping.utils.ScreenInfoUtil;
import com.shopping.hanxiao.shopping.utils.TextViewUtils;
import com.shopping.hanxiao.shopping.utils.UriParse;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by wenzhi on 17/6/17.
 */

public class PromotionAdapter extends RecyclerView.Adapter<PromotionAdapter.CustomViewHolder> {

    private List<PromotionItemData> mData;
    private Context mContext;


    public PromotionAdapter(Context context) {
        mContext = context;
        mData = new ArrayList<>();
    }

    public PromotionAdapter(Context context, List<PromotionItemData> data) {
        mContext = context;
        mData = data;
    }

    public void addData(List<PromotionItemData> data) {
        mData.addAll(data);
    }

    public void clearData() {
        mData.clear();
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_promotion, parent, false);
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
        final PromotionItemData data = mData.get(position);
        customViewHolder.mView.setTag(position);
        TextViewUtils.showTextView(customViewHolder.mDescTv, data.description);
        TextViewUtils.showTextView(customViewHolder.mSubDescTv, data.subDescription);
        RoundedCornersTransformation transformation = new RoundedCornersTransformation(mContext, ScreenInfoUtil.dpToPx(2), 0, RoundedCornersTransformation.CornerType.ALL);
        ImageDownLoadUtil.downLoadImageWithoutCompress(mContext, data.imgUrl, customViewHolder.mCommodityImg, transformation);
        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 1);
        layoutManager.setOrientation(GridLayoutManager.HORIZONTAL);
        customViewHolder.mRecyclerView.setLayoutManager(layoutManager);
        HorizontalAdapter adapter = new HorizontalAdapter(mContext);
        adapter.addData(data.banners);
        customViewHolder.mRecyclerView.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    /**
     * 提供自定义的Viewholder
     */
    public static class CustomViewHolder extends RecyclerView.ViewHolder {


        private View mView;
        private TextView mDescTv;
        private ImageView mCommodityImg;
        private RecyclerView mRecyclerView;
        private TextView mSubDescTv;


        public CustomViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mDescTv = (TextView) itemView.findViewById(R.id.item_cover_img_desc);
            mSubDescTv = (TextView) itemView.findViewById(R.id.item_cover_img_sub_desc);

            mCommodityImg = (ImageView) itemView.findViewById(R.id.item_promotion_img);
            mRecyclerView = (RecyclerView) itemView.findViewById(R.id.recycler_view);
        }
    }
}
