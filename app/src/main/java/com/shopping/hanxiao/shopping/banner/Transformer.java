package com.shopping.hanxiao.shopping.banner;

import android.support.v4.view.ViewPager.PageTransformer;

import com.shopping.hanxiao.shopping.banner.transformer.AccordionTransformer;
import com.shopping.hanxiao.shopping.banner.transformer.BackgroundToForegroundTransformer;
import com.shopping.hanxiao.shopping.banner.transformer.CubeInTransformer;
import com.shopping.hanxiao.shopping.banner.transformer.CubeOutTransformer;
import com.shopping.hanxiao.shopping.banner.transformer.DefaultTransformer;
import com.shopping.hanxiao.shopping.banner.transformer.DepthPageTransformer;
import com.shopping.hanxiao.shopping.banner.transformer.FlipHorizontalTransformer;
import com.shopping.hanxiao.shopping.banner.transformer.FlipVerticalTransformer;
import com.shopping.hanxiao.shopping.banner.transformer.ForegroundToBackgroundTransformer;
import com.shopping.hanxiao.shopping.banner.transformer.RotateDownTransformer;
import com.shopping.hanxiao.shopping.banner.transformer.RotateUpTransformer;
import com.shopping.hanxiao.shopping.banner.transformer.ScaleInOutTransformer;
import com.shopping.hanxiao.shopping.banner.transformer.StackTransformer;
import com.shopping.hanxiao.shopping.banner.transformer.TabletTransformer;
import com.shopping.hanxiao.shopping.banner.transformer.ZoomInTransformer;
import com.shopping.hanxiao.shopping.banner.transformer.ZoomOutSlideTransformer;
import com.shopping.hanxiao.shopping.banner.transformer.ZoomOutTranformer;


public class Transformer {
    public static Class<? extends PageTransformer> Default = DefaultTransformer.class;
    public static Class<? extends PageTransformer> Accordion = AccordionTransformer.class;
    public static Class<? extends PageTransformer> BackgroundToForeground = BackgroundToForegroundTransformer.class;
    public static Class<? extends PageTransformer> ForegroundToBackground = ForegroundToBackgroundTransformer.class;
    public static Class<? extends PageTransformer> CubeIn = CubeInTransformer.class;
    public static Class<? extends PageTransformer> CubeOut = CubeOutTransformer.class;
    public static Class<? extends PageTransformer> DepthPage = DepthPageTransformer.class;
    public static Class<? extends PageTransformer> FlipHorizontal = FlipHorizontalTransformer.class;
    public static Class<? extends PageTransformer> FlipVertical = FlipVerticalTransformer.class;
    public static Class<? extends PageTransformer> RotateDown = RotateDownTransformer.class;
    public static Class<? extends PageTransformer> RotateUp = RotateUpTransformer.class;
    public static Class<? extends PageTransformer> ScaleInOut = ScaleInOutTransformer.class;
    public static Class<? extends PageTransformer> Stack = StackTransformer.class;
    public static Class<? extends PageTransformer> Tablet = TabletTransformer.class;
    public static Class<? extends PageTransformer> ZoomIn = ZoomInTransformer.class;
    public static Class<? extends PageTransformer> ZoomOut = ZoomOutTranformer.class;
    public static Class<? extends PageTransformer> ZoomOutSlide = ZoomOutSlideTransformer.class;
}
