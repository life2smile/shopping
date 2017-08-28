package com.shopping.hanxiao.shopping.business;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shopping.hanxiao.shopping.R;

import static com.shopping.hanxiao.shopping.R.id.title_center_title;

public abstract class BaseFragment extends Fragment {
    private TextView mTitleTv;
    private RelativeLayout mLayout;
    private View mView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (mView != null) {
            ViewGroup parent = (ViewGroup) mView.getParent();
            if (parent != null) {
                parent.removeView(parent);
            }
            return mView;
        }
        mView = fillContent(inflater, container);
        initCommonViews();
        initViews(mView);
        requestDataFromServer();
        return mView;
    }

    private void initCommonViews() {
        mTitleTv = (TextView) mView.findViewById(title_center_title);
        mLayout = (RelativeLayout) mView.findViewById(R.id.base_layout_title);
        if (mTitleTv != null && !TextUtils.isEmpty(pagerTitle())) {
            mTitleTv.setText(pagerTitle());
            showPagerTitle();
        }

        if (titleBackgroundColor() != 0) {
            mLayout.setBackgroundColor(getResources().getColor(titleBackgroundColor()));
        }

        if (titleColor() != 0) {
            TextView title = (TextView) mView.findViewById(title_center_title);
            title.setTextColor(getResources().getColor(titleColor()));
        }

        TextView rightView = (TextView) mView.findViewById(R.id.title_right_btn);
        customRightView(rightView);
    }

    protected int titleBackgroundColor() {
        return 0;
    }

    protected int titleColor() {
        return 0;
    }

    private View fillContent(LayoutInflater inflater, ViewGroup container) {
        int layoutId = getContentLayout();
        if (layoutId != 0) {
            return inflater.inflate(layoutId, container, false);
        }
        return null;
    }

    protected void showPagerTitle() {
        if (mLayout != null) {
            mLayout.setVisibility(View.VISIBLE);
        }
    }

    protected void requestDataFromServer() {
    }

    protected void customRightView(TextView rightView) {

    }

    protected String pagerTitle() {
        return null;
    }

    protected abstract int getContentLayout();

    protected abstract void initViews(View view);
}
