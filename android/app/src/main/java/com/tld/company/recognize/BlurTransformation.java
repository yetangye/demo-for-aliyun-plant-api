package com.tld.company.recognize;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Build.VERSION_CODES;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.FloatRange;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.lang.ref.WeakReference;

class BlurTransformation extends BitmapTransformation {

    private static final String ID = "com.tld.company.recognize.BlurTransformation";

    private static final float DEFAULT_RADIUS = 25.0f;
    private static final float MAX_RADIUS = 25.0f;
    private static final float DEFAULT_SAMPLING = 1.0f;

    private WeakReference<Context> mContext;
    private float mSampling = DEFAULT_SAMPLING;
    private float mRadius;
    private int mColor;

    public BlurTransformation(Context context, @FloatRange(from = 0.0f) float radius, int color) {
        super(context);
        mContext = new WeakReference<>(context);
        if (radius > MAX_RADIUS) {
            mSampling = radius / 25.0f;
            mRadius = MAX_RADIUS;
        } else {
            mRadius = radius;
        }
        mColor = color;
    }

    public BlurTransformation(Context context, @FloatRange(from = 0.0f) float radius) {
        this(context, radius, Color.TRANSPARENT);
    }

    public BlurTransformation(Context context) {
        this(context, DEFAULT_RADIUS);
    }

    @TargetApi(VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        boolean needScaled = mSampling == DEFAULT_SAMPLING;
        int originWidth = toTransform.getWidth();
        int originHeight = toTransform.getHeight();
        int width, height;
        if (needScaled) {
            width = originWidth;
            height = originHeight;
        } else {
            width = (int) (originWidth / mSampling);
            height = (int) (originHeight / mSampling);
        }

        Bitmap bitmap = pool.get(width, height, Bitmap.Config.ARGB_8888);
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        if (mSampling != DEFAULT_SAMPLING) {
            canvas.scale(1 / mSampling, 1 / mSampling);
        }
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG | Paint.ANTI_ALIAS_FLAG);
        PorterDuffColorFilter filter =
                new PorterDuffColorFilter(mColor, PorterDuff.Mode.SRC_ATOP);
        paint.setColorFilter(filter);
        canvas.drawBitmap(toTransform, 0, 0, paint);

        if (mContext.get() == null) {
            return toTransform;
        }

        RenderScript rs = RenderScript.create(mContext.get());
        Allocation input = Allocation.createFromBitmap(rs, bitmap, Allocation.MipmapControl.MIPMAP_NONE,
                Allocation.USAGE_SCRIPT);
        Allocation output = Allocation.createTyped(rs, input.getType());
        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        blur.setInput(input);
        blur.setRadius(mRadius);
        blur.forEach(output);
        output.copyTo(bitmap);

        rs.destroy();

        if (needScaled) {
            return bitmap;
        } else {
            Bitmap scaled = Bitmap.createScaledBitmap(bitmap, originWidth, originHeight, true);
            bitmap.recycle();
            return scaled;
        }
    }

    @Override
    public String getId() {
        return ID + '-' + mRadius + '-' + mColor;
    }

    public void destroy() {
        mContext = null;
    }
}