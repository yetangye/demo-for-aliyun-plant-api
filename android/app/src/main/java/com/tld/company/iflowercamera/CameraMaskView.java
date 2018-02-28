package com.tld.company.iflowercamera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.View;

class CameraMaskView extends View {
    // 决定方框的边长
    static final float SIZE_RATIO = 2 / 3f;
    // 决定方框的起始顶点
    static final int SIZE_BASE = 6;

    private int x = 100;
    private int y = 100;
    private int size = 200;

    Paint paint;

    private Bitmap centerFill;
    private Rect centerFillRect;
    private Bitmap frameFill;
    private MaskFrameFilledListener mListener;

    private int foregroundColor = 0xfff9f9f9;

    public CameraMaskView(Context context) {
        this(context, null);
    }

    public CameraMaskView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraMaskView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(foregroundColor);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(4);

        centerFillRect = new Rect();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        size = (int) (Math.min(getMeasuredHeight(), getMeasuredWidth()) * SIZE_RATIO);
        x = getMeasuredWidth() / SIZE_BASE;
        y = getMeasuredHeight() / SIZE_BASE;
        centerFillRect.set(x, y, x + size, y + size);
        if (centerFillRect.top != 0 && mListener != null) {
            mListener.onFilled(centerFillRect);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (centerFill != null) {
            canvas.drawBitmap(centerFill, null, centerFillRect, null);
        }

        if (frameFill != null) {
            canvas.drawBitmap(frameFill, null, centerFillRect, null);
        }

        canvas.drawRect(0, 0, getMeasuredWidth(), y, paint);
        canvas.drawRect(0, y, x, y + size, paint);
        canvas.drawRect(x + size, y, getMeasuredWidth(), y + size, paint);
        canvas.drawRect(0, y + size, getMeasuredWidth(), getMeasuredHeight(), paint);
    }

    public void reset() {
        if (centerFill != null) {
            centerFill.recycle();
            centerFill = null;
        }
        invalidate();
    }

    public void setCenterFill(String imgPath) {
        centerFill = BitmapFactory.decodeFile(imgPath);
        invalidate();
    }

    public void setFrameFill(Drawable drawable) {
        frameFill = ((BitmapDrawable) drawable).getBitmap();
        invalidate();
    }

    public void setFrameFilledListener(MaskFrameFilledListener listener) {
        mListener = listener;
    }

    public interface MaskFrameFilledListener {
        void onFilled(Rect rect);
    }

    public void setMaskColor(@ColorInt int color) {
        if (paint != null) {
            paint.setColor(color);
        }
        foregroundColor = color;
        invalidate();
    }
}
