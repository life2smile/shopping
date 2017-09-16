package com.shopping.hanxiao.shopping.common;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.http.SslError;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shopping.hanxiao.shopping.R;
import com.shopping.hanxiao.shopping.business.BaseActivity;
import com.shopping.hanxiao.shopping.utils.ScreenInfoUtil;

/**
 * Created by wenzhi on 17/6/26.
 */

public class WebViewActivity extends BaseActivity {
    private WebView mWebView;
    private boolean mShowTitleBar;
    private SlowlyProgressBar mSlowlyProgressBar;
    private String mUri;
    private String mTitle;
    private int mLeftImageResId;
    private int mRightImageResId;

    @Override
    protected int getContentLayout() {
        return R.layout.web_main;
    }

    @Override
    protected void initViews() {
        initTitleBar();
        initWebView();
    }

    private void initTitleBar() {
        if (mShowTitleBar) {
            RelativeLayout mTitleLayout = (RelativeLayout) findViewById(R.id.base_layout_title);
            mTitleLayout.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(mTitle)) {
                TextView title = (TextView) findViewById(R.id.title_center_title);
                title.setText(mTitle);
            }

            if (mLeftImageResId != 0) {
                ImageView mLeftImg = (ImageView) findViewById(R.id.title_left_btn);
                mLeftImg.setImageResource(mLeftImageResId);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        ScreenInfoUtil.dpToPx(50), ScreenInfoUtil.dpToPx(50)
                );
                params.addRule(RelativeLayout.CENTER_VERTICAL);
                mLeftImg.setLayoutParams(params);
                mLeftImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
            }

            if (mRightImageResId != 0) {
                TextView mRightImg = (TextView) findViewById(R.id.title_right_btn);
                Drawable rightDrawable = getResources().getDrawable(mRightImageResId);
                mRightImg.setCompoundDrawablesWithIntrinsicBounds(rightDrawable, null, null, null);
                mRightImg.setPadding(ScreenInfoUtil.dpToPx(14), 0, ScreenInfoUtil.dpToPx(14), 0);
                mRightImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //do nothing
                    }
                });
            }
        }
    }

    private void initWebView() {
        mSlowlyProgressBar = new SlowlyProgressBar((ProgressBar) findViewById(R.id.progressBar));
        mWebView = (WebView) findViewById(R.id.web_content);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setBlockNetworkImage(false);
        mWebView.getSettings().setBlockNetworkLoads(false);
        mWebView.getSettings().setLoadsImagesAutomatically(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                mSlowlyProgressBar.onProgressChange(newProgress);
            }
        });

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                mSlowlyProgressBar.onProgressStart();
            }


            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });
        mWebView.loadUrl(mUri);
    }

    @Override
    protected void initDataFromIntent(Intent intent) {
        this.mUri = intent.getStringExtra(WebViewContext.URI);
        this.mTitle = intent.getStringExtra(WebViewContext.TITLE);
        this.mLeftImageResId = intent.getIntExtra(WebViewContext.LEFT_IMAGE_RES_ID, 0);
        this.mRightImageResId = intent.getIntExtra(WebViewContext.RIGHT_IMAGE_RES_ID, 0);
        this.mShowTitleBar = !TextUtils.isEmpty(mTitle)
                || mLeftImageResId != 0
                || mRightImageResId != 0;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSlowlyProgressBar != null) {
            mSlowlyProgressBar.destroy();
        }
    }
}
