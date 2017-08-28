package com.shopping.hanxiao.shopping.common.transformation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

/**
 * Created by wenzhi on 17/8/8.
 */

public class RotateTransformation extends BitmapTransformation {

    private float mRotate;
    private boolean mRotateOnlyHGreaterThanW;//只有当Width > height的时候进行选择

    public RotateTransformation(Context context, float rotate) {
        super(context);
        this.mRotate = rotate;
    }

    public RotateTransformation(Context context, float rotate, boolean rotateWGreaterThanH) {
        super(context);
        this.mRotate = rotate;
        this.mRotateOnlyHGreaterThanW = rotateWGreaterThanH;
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        if (mRotateOnlyHGreaterThanW) {
            mRotate = toTransform.getHeight() > toTransform.getWidth() ? mRotate : 0;
        }

        System.out.println(toTransform.getHeight() + "------------------" + toTransform.getWidth());

        Matrix matrix = new Matrix();
        matrix.postRotate(mRotate);
        return Bitmap.createBitmap(toTransform, 0, 0, toTransform.getWidth(), toTransform.getHeight(), matrix, true);
    }

    @Override
    public String getId() {
        return "rotate" + mRotate;
    }
}
