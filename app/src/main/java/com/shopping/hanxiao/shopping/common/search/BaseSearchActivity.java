package com.shopping.hanxiao.shopping.common.search;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.shopping.hanxiao.shopping.R;
import com.shopping.hanxiao.shopping.wrapper.HeaderAndFooterWrapper;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

/**
 * Created by liuyunming on 2016/11/24.
 */

public abstract class BaseSearchActivity extends RxAppCompatActivity {
    private TextView mCancelTv;
    private EditText mSearchEdt;
    private boolean mSearch;
    private int mBegin = 0;
    private int mOffset = 20;
    private RecyclerView mRecyclerView;
    private TextView mRefreshTv;
    private ImageView mCloseImg;

    protected String mOldKeyword;
    protected String mCurKeyword;
    protected View mFooter;
    protected boolean mLoadMore;
    protected HeaderAndFooterWrapper mHeaderAndFooterWrapper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_layout);
        mHeaderAndFooterWrapper = getHeaderAndFooterWrapper();
        initViews();
        setListeners();
    }

    public void initViews() {
        mCancelTv = (TextView) findViewById(R.id.cancel_tv);
        mSearchEdt = (EditText) findViewById(R.id.search_edt);
        mCloseImg = (ImageView) findViewById(R.id.close_img);
        mRefreshTv = (TextView) findViewById(R.id.refresh_tv);
        initFooterView(mHeaderAndFooterWrapper);//添加recycleview中的footer
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(spanNum(), StaggeredGridLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(getHeaderAndFooterWrapper());
    }


    private void setListeners() {
        mCancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mSearch) {
                    doCancel();
                    return;
                }
                //开始搜索商品
                if (!mCurKeyword.equals(mOldKeyword)) {
                    hiddenInputMethod();
                    doSearch(type(), mBegin, mOffset, mCurKeyword);
                }
            }
        });

        mSearchEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    mSearch = false;
                    mCancelTv.setText("取消");
                    mOldKeyword = mCurKeyword = null;
                    invalidateCurView();
                } else {
                    mSearch = true;
                    mCancelTv.setText("搜索");
                    mOldKeyword = mCurKeyword;
                    mCurKeyword = s.toString();
                    mCloseImg.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mRefreshTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRefreshTv.setVisibility(View.GONE);
                doSearch(type(), mBegin, mOffset, mCurKeyword);
            }
        });

        mCloseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCloseImg.setVisibility(View.GONE);
                mSearchEdt.getText().clear();
            }
        });
    }

    private void invalidateCurView() {
        mCloseImg.setVisibility(View.GONE);
        clearData();
        mHeaderAndFooterWrapper.notifyDataSetChanged();
        mFooter.setVisibility(View.GONE);
    }

    private void doCancel() {
        hiddenInputMethod();
        finish();
    }

    private void hiddenInputMethod() {
        InputMethodManager methodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        methodManager.hideSoftInputFromWindow(mSearchEdt.getWindowToken(), 0);
    }

    private void initFooterView(HeaderAndFooterWrapper headerAndFooterWrapper) {
        mFooter = LayoutInflater.from(this).inflate(R.layout.footer, null);
        headerAndFooterWrapper.addFootView(mFooter);
        initLoadMore();//下拉加载更多
    }

    private void initLoadMore() {
        mFooter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadMore = true;
                mFooter.setVisibility(View.GONE);
                mBegin = mHeaderAndFooterWrapper.getRealItemCount();//获取当前所有的数据数，作为起点查询
                doSearch(type(), mBegin, mOffset, mCurKeyword);
            }
        });
    }

    public abstract void doSearch(String type, int begin, int offset, String curKeyword);

    public abstract HeaderAndFooterWrapper getHeaderAndFooterWrapper();

    public abstract void clearData();

    public abstract String type();

    public abstract int spanNum();

}
