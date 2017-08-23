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

    public RotateTransformation(Context context, float rotate) {
        super(context);
        this.mRotate = rotate;
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        Matrix matrix = new Matrix();
        matrix.postRotate(mRotate);
        return Bitmap.createBitmap(toTransform, 0, 0, toTransform.getWidth(), toTransform.getHeight(), matrix, true);
    }

    @Override
    public String getId() {
        return "rotate" + mRotate;
    }
}
