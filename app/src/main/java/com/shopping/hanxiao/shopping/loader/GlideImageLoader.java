package com.shopping.hanxiao.shopping.loader;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.shopping.hanxiao.shopping.R;
import com.shopping.hanxiao.shopping.banner.loader.ImageLoader;
import com.shopping.hanxiao.shopping.common.transformation.RotateTransformation;


public class GlideImageLoader extends ImageLoader {
    private float mRotate = 0;
    private boolean mRotateOnlyHGreaterThanW;

    public GlideImageLoader() {
    }

    public GlideImageLoader(float rotate) {
        this.mRotate = rotate;
    }

    public GlideImageLoader(float rotate, boolean rotateWGreaterThanH) {
        this.mRotate = rotate;
        this.mRotateOnlyHGreaterThanW = rotateWGreaterThanH;
    }

    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        //具体方法内容自己去选择，次方法是为了减少banner过多的依赖第三方包，所以将这个权限开放给使用者去选择
        Glide.with(context.getApplicationContext())
                .load(path)
                .crossFade()
                .fitCenter()
                .transform(new RotateTransformation(context, mRotate, mRotateOnlyHGreaterThanW))
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .error(R.drawable.img_empty)
                .into(imageView);
    }
}
