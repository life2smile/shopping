package com.shopping.hanxiao.shopping;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.shopping.hanxiao.shopping.business.coupon.CouponBaseFragment;
import com.shopping.hanxiao.shopping.business.discount.DiscountFragment;
import com.shopping.hanxiao.shopping.business.promotion.PromotionFragment;
import com.shopping.hanxiao.shopping.rxretrofit.exception.ApiException;
import com.shopping.hanxiao.shopping.rxretrofit.http.HttpManager;
import com.shopping.hanxiao.shopping.rxretrofit.listener.HttpOnNextListener;
import com.shopping.hanxiao.shopping.tablayout.TabFragmentPagerAdaper;
import com.shopping.hanxiao.shopping.utils.UriParse;
import com.shopping.hanxiao.shopping.version.VersionApi;
import com.shopping.hanxiao.shopping.version.VersionData;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends RxAppCompatActivity {

    private TabLayout mTabLayout;
    private int[] tabNames = {R.string.main_tab_name_coupon, R.string.main_tab_name_discount, R.string.main_tab_name_promotion};
    private int[] tabIcons = {R.drawable.radio_homepage, R.drawable.radio_discount, R.drawable.radio_promotion};

    private FrameLayout container;
    private TabFragmentPagerAdaper adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = View.inflate(this, R.layout.activity_tablayout, null);
        setContentView(view);
        initTabLayout();
        initFragment();
        registerListeners();
        checkNewVersion();
    }

    private void initFragment() {
        List<Fragment> list = new ArrayList<>();
        list.add(CouponBaseFragment.newInstance());
        list.add(DiscountFragment.newInstance());
        list.add(PromotionFragment.newInstance());
        container = (FrameLayout) findViewById(R.id.fl_contains);
        adapter = new TabFragmentPagerAdaper(getSupportFragmentManager(), list);

        // 初始化默认显示的fragment
        Fragment fragment = (Fragment) adapter.instantiateItem(container, 0);
        adapter.setPrimaryItem(container, 0, fragment);
        adapter.finishUpdate(container);
        adapter.destroyItem(container, 0, fragment);
    }

    private void initTabLayout() {
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        for (int i = 0; i < tabNames.length; i++) {
            View tabView = View.inflate(this, R.layout.tab_indicator, null);
            TextView textView = (TextView) tabView.findViewById(R.id.tab_title);
            textView.setText(tabNames[i]);
            // 利用这种办法设置图标是为了解决默认设置图标和文字出现的距离较大问题
            textView.setCompoundDrawablesWithIntrinsicBounds(0, tabIcons[i], 0, 0);
            mTabLayout.addTab(mTabLayout.newTab().setCustomView(textView));
        }
    }

    private void registerListeners() {
        // Tablayout选择tab监听
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = mTabLayout.getSelectedTabPosition();//tab.getPosition();
                Fragment fragment = (Fragment) adapter.instantiateItem(container, position);
                adapter.setPrimaryItem(container, position, fragment);
                adapter.finishUpdate(container);
                adapter.destroyItem(container, position, fragment);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    //此处检查版本，用于更新app，并传递deviceId
    private void checkNewVersion() {
        HttpManager manager = new HttpManager(new HttpOnNextListener() {
            @Override
            public void onNext(String result, String method) {
                final VersionData versionData = JSON.parseObject(result, VersionData.class);
                if (versionData.newVersion) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("有新版本啦!")
                            .setMessage("发现新版本啦，点击下载进行更新!")
                            .setPositiveButton("下载", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    UriParse.startByBrower(MainActivity.this, Uri.parse(versionData.url));
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).create();
                    builder.show();
                }
            }

            @Override
            public void onError(ApiException e) {
                System.out.println(e.getMessage());
            }
        }, MainActivity.this);
        manager.doHttpDeal(new VersionApi());
    }
}
