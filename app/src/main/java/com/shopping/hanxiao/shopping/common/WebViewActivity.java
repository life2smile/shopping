package com.shopping.hanxiao.shopping.common;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.shopping.hanxiao.shopping.R;
import com.shopping.hanxiao.shopping.business.BaseActivity;

/**
 * Created by wenzhi on 17/6/26.
 */

public class WebViewActivity extends BaseActivity {
    private WebView mWebView;
    private SlowlyProgressBar mSlowlyProgressBar;

    @Override
    protected int getContentLayout() {
        return R.layout.web_main;
    }

    @Override
    protected void initViews() {
        final String uri = getIntent().getStringExtra("shopping_uri");

        mSlowlyProgressBar = new SlowlyProgressBar((ProgressBar) findViewById(R.id.progressBar));
        mWebView = (WebView) findViewById(R.id.web_content);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setBlockNetworkImage(false);
        mWebView.getSettings().setBlockNetworkLoads(false);
        mWebView.getSettings().setLoadsImagesAutomatically(true);
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
        mWebView.loadUrl(uri);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSlowlyProgressBar != null) {
            mSlowlyProgressBar.destroy();
        }
    }
}
