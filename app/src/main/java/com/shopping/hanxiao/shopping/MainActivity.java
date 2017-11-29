package com.shopping.hanxiao.shopping;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.shopping.hanxiao.shopping.business.base.CommodityFragment;
import com.shopping.hanxiao.shopping.business.discount.DiscountFragment;
import com.shopping.hanxiao.shopping.business.promotion.PromotionFragment;
import com.shopping.hanxiao.shopping.permission.PermissionsActivity;
import com.shopping.hanxiao.shopping.permission.PermissionsChecker;
import com.shopping.hanxiao.shopping.rxretrofit.exception.ApiException;
import com.shopping.hanxiao.shopping.rxretrofit.http.HttpManager;
import com.shopping.hanxiao.shopping.rxretrofit.listener.HttpOnNextListener;
import com.shopping.hanxiao.shopping.tablayout.TabFragmentPagerAdaper;
import com.shopping.hanxiao.shopping.utils.MacUtil;
import com.shopping.hanxiao.shopping.utils.TokenUtil;
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
    private PermissionsChecker mPermissionsChecker;
    private final static String[] PERMISSIONS = new String[]{Manifest.permission.READ_PHONE_STATE};
    private final static int REQUEST_CODE = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = View.inflate(this, R.layout.activity_tablayout, null);
        setContentView(view);
        initTabLayout();
        initFragment();
        registerListeners();
        checkPermission();
    }

    private void initFragment() {
        List<Fragment> list = new ArrayList<>();
        list.add(CommodityFragment.newInstance());
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
                                    UriParse.startByBrowser(MainActivity.this, Uri.parse(versionData.url));
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
        manager.doHttpDeal(new VersionApi(getDeviceId()));
    }

    private String getDeviceId() {
        String deviceId;
        try {
            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                //在这之前已经主动申请过一次权限了，如果用户拒绝了，将不再申请任何权限。而是随机产生一个随机数作为用户标识。
                return TokenUtil.instance().generateToken();
            }
            deviceId = tm.getDeviceId();
        } catch (Exception e) {
            deviceId = MacUtil.getMac();
        }
        return deviceId;
    }

    private void checkPermission() {
        mPermissionsChecker = new PermissionsChecker(this);
        if (mPermissionsChecker.lackPermissions(PERMISSIONS)) {
            PermissionsActivity.startActivityForResult(this, REQUEST_CODE, PERMISSIONS);
        } else {
            checkNewVersion();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //这里处理权限允许或者被拒的case。一些必须权限如果没有得到这里可以直接finish
        super.onActivityResult(requestCode, resultCode, data);
        checkNewVersion();
    }
}
