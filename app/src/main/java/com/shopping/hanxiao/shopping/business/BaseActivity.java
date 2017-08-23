package com.shopping.hanxiao.shopping.business;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.shopping.hanxiao.shopping.R;

public abstract class BaseActivity extends AppCompatActivity {
    private FrameLayout mBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBody = (FrameLayout) findViewById(R.id.base_layout_body);
        fillContent();
        initViews();
        requestDataFromServer();
    }

    private void fillContent() {
        int layoutId = getContentLayout();
        if (layoutId != 0) {
            getLayoutInflater().inflate(layoutId, mBody, true);
        }
    }

    protected void requestDataFromServer() {
    }

    protected abstract int getContentLayout();

    protected abstract void initViews();
}
