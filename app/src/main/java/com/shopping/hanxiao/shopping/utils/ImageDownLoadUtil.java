package com.shopping.hanxiao.shopping.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.shopping.hanxiao.shopping.R;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by wenzhi on 17/6/27.
 */

public class ImageDownLoadUtil {
    public static void downLoadImage(Context context, String imgUrl, ImageView target) {
        Glide.with(context)
                .load(imgUrl)
                .asBitmap()
                .error(R.drawable.img_empty)
                .into(target);
    }

    public static void downLoadImage(Context context, String imgUrl, ImageView target, Transformation... transformation) {
        Glide.with(context)
                .load(imgUrl)
                .asBitmap()
                .error(R.drawable.img_empty)
                .transform(transformation)
                .into(target);
    }

    public static void downLoadImageWithoutCompress(Context context, String imgUrl, ImageView target, Transformation... transformation) {
        Glide.with(context)
                .load(imgUrl)
                .asBitmap()
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .error(R.drawable.img_empty)
                .transform(transformation)
                .into(target);
    }

    public static void downLoadImageWithPalette(Context context, String imgUrl, ImageView target, final View paletteView) {
        Glide.with(context)
                .load(imgUrl)
                .asBitmap()
                .error(R.drawable.img_empty)
                .transform(new RoundedCornersTransformation(context, ScreenInfoUtil.dpToPx(4), 0, RoundedCornersTransformation.CornerType.TOP))
                .into(new BitmapImageViewTarget(target) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        super.onResourceReady(resource, glideAnimation);
                        Palette.from(resource).generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(Palette palette) {
                                int color = palette.getLightVibrantColor(0x000000);
                                paletteView.setBackgroundColor(color);
                            }
                        });
                    }
                });
    }
}
