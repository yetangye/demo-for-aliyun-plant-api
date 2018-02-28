package com.tld.company.iflowercamera;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.tld.company.R;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class SimpleCamera extends FrameLayout {

    public static final int SELECTOR_SRC_BITMAP = 1;
    public static final int SELECTOR_CROP_BITMAP = 2;
    public static final int SELECTOR_SCALED_BITMAP = 4;

    public static final int PICTURE_QUALITY_LOW = 1;
    public static final int PICTURE_QUALITY_MIDDLE = 2;
    public static final int PICTURE_QUALITY_HEIGHT = 4;

    CameraView mCameraView;
    CameraMaskView mMaskView;
    private int mMaskForegroundColor;
    private Drawable mMaskFrameDrawable;
    ImageView mImageView;
    ByteArrayOutputStream tmpCropImage = new ByteArrayOutputStream();

    private RawImage tmpRawImage;
    private BitmapTransformation tmpTransformation;

    private SurfaceHolder currentHolder;
    private boolean autoInitCamera = true;
    private boolean showMaskView = true;

    OrientationEventAdapter mOrientationEventAdapter;
    private OnTouchListener mTouchEvent;
    private String mCurrentPicPath;
    private String mMaskPicPath;
    private int mAspectRatio;

    private int mPictureQuality = PICTURE_QUALITY_MIDDLE;

    private RotateDetector.OrientationChangedListener mOrientationChangedListener;
    private OnCameraSizeListener mCameraSizeListener;

    public SimpleCamera(Context context, boolean autoInitCamera) {
        super(context);
        this.autoInitCamera = autoInitCamera;
        init(context);
    }

    public SimpleCamera(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.SimpleCamera);
        this.autoInitCamera = t.getBoolean(R.styleable.SimpleCamera_autoInitCamera, true);
        showMaskView = t.getBoolean(R.styleable.SimpleCamera_showMaskView, true);
        mAspectRatio = t.getInt(R.styleable.SimpleCamera_aspectRatio, 1);
        mMaskFrameDrawable = t.getDrawable(R.styleable.SimpleCamera_focusFrameDrawable);
        mMaskForegroundColor = t.getColor(R.styleable.SimpleCamera_foregroundColor, 000000);
        t.recycle();
        init(context);
    }

    private void init(Context context) {
        mCameraView = new CameraView(context);
        mCameraView.setSizeListener(new OnCameraSizeListener() {
            @Override
            public void onPreview(Camera.Size previewSize) {
                if (mCameraSizeListener != null) {
                    mCameraSizeListener.onPreview(previewSize);
                }
            }

            @Override
            public void onPicture(Camera.Size pictureSize) {
                if (mCameraSizeListener != null) {
                    mCameraSizeListener.onPicture(pictureSize);
                }
            }
        });
        mCameraView.setAspectRatio(mAspectRatio);
        mCameraView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mCameraView.getHolder().addCallback(surfaceCallback);
        addView(mCameraView);

        mImageView = new ImageView(context);
        mImageView.setVisibility(View.GONE);
        mImageView.setScaleType(ScaleType.CENTER_CROP);
        mImageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addView(mImageView);

        mMaskView = new CameraMaskView(context);
        mMaskView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        if (mMaskFrameDrawable != null) {
            mMaskView.setFrameFill(mMaskFrameDrawable);
        }
        mMaskView.setMaskColor(mMaskForegroundColor);
        mMaskView.setFrameFilledListener(new CameraMaskView.MaskFrameFilledListener() {
            @Override
            public void onFilled(Rect rect) {
                LayoutParams params = new LayoutParams(rect.width(), rect.height());
                params.leftMargin = rect.left;
                params.topMargin = rect.top;
            }
        });
        if (!showMaskView) {
            mMaskView.setVisibility(View.GONE);
        }
        addView(mMaskView);

        mOrientationEventAdapter = new OrientationEventAdapter(context);
        mOrientationEventAdapter.setRotationChangedListener(new RotateDetector.OrientationChangedListener() {
            @Override
            public void onOrientationChanged(int rotation) {
                if (mOrientationChangedListener != null) {
                    mOrientationChangedListener.onOrientationChanged(rotation);
                }
            }
        });
        mCameraView.setOrientationCatcher(mOrientationEventAdapter);
    }

    /**
     * 准备拍照
     */
    public void startPreview() {
        mCurrentPicPath = null;
        mMaskPicPath = null;
        tmpRawImage = null;
        tmpCropImage.reset();
        if (showMaskView) {
            mMaskView.reset();
        }
        mCameraView.setHandFocusable(true);
        mCameraView.setBackgroundColor(0x00000000);

        if (currentHolder != null && !mCameraView.isInitCamera()) {
            mCameraView.initCamera(currentHolder, getPictureRate());
        }
        if (mImageView.getVisibility() == View.VISIBLE) {
            mImageView.setVisibility(View.GONE);
            if (showMaskView) {
                mMaskView.setBackgroundColor(0xff000000);
                ValueAnimator fadeAnim = ValueAnimator.ofInt(0xff, 0xff, 0x00);
                fadeAnim.addUpdateListener(new AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int color = (int) animation.getAnimatedValue();
                        mMaskView.setBackgroundColor(color << 24);
                    }
                });
                fadeAnim.setInterpolator(new AccelerateInterpolator(3));
                fadeAnim.setDuration(300);
                fadeAnim.start();
            }
        }
        try {
            mCameraView.startPreview();
        } catch (RuntimeException e) {
            mCameraView.initCamera(currentHolder, getPictureRate());
        }
    }

    /**
     * 获取调整好角度的图片
     *
     * @param callback     根据传入的图片质量参数，返回的Bitmap数组, size = 3。
     *                     bitmap[0]代表原图，
     *                     bitmap[1]代表根据选区{@link CameraMaskView#SIZE_BASE} 剪切过的图，
     *                     bitmap[2]代表剪切并scaled过的图
     * @param selectorMode {@link #SELECTOR_SRC_BITMAP}
     *                     {@link #SELECTOR_CROP_BITMAP}
     *                     {@link #SELECTOR_SCALED_BITMAP} 及其组合，
     *                     比如 (SELECTOR_SRC_BITMAP｜SELECTOR_CROP_BITMAP)代表返回原图和剪切图,
     *                     (SELECTOR_CROP_BITMAP|SELECTOR_SCALE_BITMAP)代表剪切图和差品质图
     */
    @Deprecated
    public void takePicture(final int selectorMode, final BitmapCallback<Bitmap[]> callback) {
        mCameraView.setHandFocusable(false);
        mCameraView.takeRawPicture(new BitmapCallback<RawImage>() {
            @Override
            public void onResult(RawImage image) {
                if (image == null) {
                    mCameraView.initCamera(currentHolder, getPictureRate());
                    callback.onResult(null);
                    return;
                }
                tmpRawImage = image;
                mCameraView.decodeBitmapArray(selectorMode, image, callback, true);
            }
        });
    }

    /**
     * 拍照，获取原始图片数据
     */
    public void takeRawPicture(final BitmapCallback<RawImage> callback) {
        mCameraView.setHandFocusable(false);
        mCameraView.takeRawPicture(new BitmapCallback<RawImage>() {
            @Override
            public void onResult(RawImage image) {
                callback.onResult(image);
            }
        });
    }

    /**
     * 获取处理过的图片
     *
     * @param callback 处理过的图片的数组，bitmap[0]代表旋转过的原图，bitmap[1]代表旋转并裁剪过的图片。
     */
    public void takePicture(final BitmapCallback<Bitmap[]> callback) {
        mCameraView.setHandFocusable(false);
        mCameraView.takeRawPicture(new BitmapCallback<RawImage>() {
            @Override
            public void onResult(RawImage image) {
                if (image == null) {
                    mCameraView.initCamera(currentHolder, getPictureRate());
                    callback.onResult(null);
                    return;
                }
                mCameraView.decodeBitmapArray(image, new BitmapCallback<Bitmap[]>() {
                    @Override
                    public void onResult(Bitmap[] bitmaps) {
                        callback.onResult(bitmaps);
                    }
                });
            }
        });
    }

    /**
     * 获取旋转并裁剪的图片，并且把大图保存到指定目录。
     *
     * @param callback 返回裁剪过的图片
     * @param srcPath  将处理过的原图保存到指定目录
     */
    public void takePicture(final BitmapCallback<Bitmap> callback, final String srcPath) {
        mCameraView.setHandFocusable(false);
        mCurrentPicPath = null;
        mCameraView.takeRawPicture(new BitmapCallback<RawImage>() {
            @Override
            public void onResult(RawImage image) {
                if (image == null) {
                    mCameraView.initCamera(currentHolder, getPictureRate());
                    callback.onResult(null);
                    return;
                }
                mCameraView.decodeBitmapArray(image, new BitmapCallback<Bitmap[]>() {
                    @Override
                    public void onResult(Bitmap[] bitmaps) {
                        if (bitmaps == null) {
                            callback.onResult(null);
                        } else {
                            saveBitmap(bitmaps[0], srcPath, null);
                            callback.onResult(bitmaps[1]);
                        }
                    }
                });
            }
        });
    }

    public void takePicture(final String path, final BitmapCallback<Boolean> cb) {
        mCameraView.setHandFocusable(false);
        mCurrentPicPath = path;
        mCameraView.takeRawPicture(new BitmapCallback<RawImage>() {
            @Override
            public void onResult(RawImage rawImage) {
                if (rawImage == null) {
                    mCameraView.initCamera(currentHolder, getPictureRate());
                    cb.onResult(false);
                    return;
                }
                mCameraView.decodeBitmapArray(rawImage, new BitmapCallback<Bitmap[]>() {
                    @Override
                    public void onResult(Bitmap[] bitmaps) {
                        if (bitmaps != null) {
                            saveBitmap(bitmaps[0], path, cb);
                        }
                    }
                });
            }
        });
    }

    private void saveBitmap(final Bitmap bitmap, final String path, final BitmapCallback<Boolean> cb) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                boolean result = true;
                try {
                    FileOutputStream fos = new FileOutputStream(path);
                    bitmap.compress(CompressFormat.JPEG, 100, fos);
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    result = false;
                }
                bitmap.recycle();
                mCurrentPicPath = path;
                return result;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (cb != null) {
                    cb.onResult(result);
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * 设置遮罩框的图像, 遮罩背景为黑色
     *
     * @param imagePath 图像地址
     */
    public void setMaskImage(String imagePath) {
        setMaskImage(imagePath, null);
    }

    /**
     * 设置遮罩框图像，根据transformation参数设置遮罩背景
     *
     * @param imagePath      图像地址
     * @param transformation 图像转换算法
     */
    public void setMaskImage(String imagePath, BitmapTransformation transformation) {
        mCurrentPicPath = null;
        tmpRawImage = null;
        mMaskPicPath = imagePath;
        tmpTransformation = transformation;
        mCameraView.releaseCamera();
        if (showMaskView) {
            mMaskView.setCenterFill(imagePath);
        }
        mImageView.setVisibility(View.VISIBLE);
        if (transformation == null) {
            Glide.with(getContext()).load(R.drawable.ifc_black)
                    .into(mImageView);
        } else {
            Glide.with(getContext()).load(imagePath)
                    .placeholder(R.drawable.ifc_black)
                    .error(R.drawable.ifc_black)
                    .override(100, 100)
                    .transform(transformation)
                    .into(mImageView);
        }
    }

    /**
     * 清除图片缓存
     */
    public void clear() {
        Glide.clear(mImageView);
        if (showMaskView) {
            mMaskView.reset();
        }
    }

    private void showTookPicture(String path) {
        mImageView.setVisibility(View.VISIBLE);
        Glide.with(getContext()).load(path)
                .placeholder(R.drawable.ifc_black)
                .error(R.drawable.ifc_black)
                .into(mImageView);
    }

    private void showTookPicture(byte[] data) {
        mImageView.setVisibility(View.VISIBLE);
        float rotate = 90f;
        if (CameraView.isNexus5X()) {
            rotate = 270f;
        }
        Glide.with(getContext()).load(data)
                .placeholder(R.drawable.ifc_black)
                .error(R.drawable.ifc_black)
                .override(getHeight() / 2, getWidth() / 2)
                .transform(new RotateTransformation(getContext(), rotate))
                .into(mImageView);
    }

    Callback surfaceCallback = new Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            mOrientationEventAdapter.enable();
            currentHolder = holder;
            if (!TextUtils.isEmpty(mCurrentPicPath)) {
                showTookPicture(mCurrentPicPath);
                mCameraView.setBackgroundColor(0xff000000);
                return;
            } else if (tmpRawImage != null && tmpRawImage.imageData.length > 0) {
                showTookPicture(tmpRawImage.imageData);
                mCameraView.setBackgroundColor(0xff000000);
                return;
            }

            if (!TextUtils.isEmpty(mMaskPicPath)) {
                setMaskImage(mMaskPicPath, tmpTransformation);
                return;
            }

            if (autoInitCamera) {
                mCameraView.initCamera(holder, getPictureRate());
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            mOrientationEventAdapter.disable();
            mCameraView.releaseCamera();
            mImageView.setVisibility(View.GONE);
        }
    };

    public void releaseCamera() {
        if (mCameraView.isInitCamera()) {
            mCameraView.releaseCamera();
        }
    }

    public interface BitmapCallback<T> {
        void onResult(@Nullable T t);
    }

    /**
     * 指定缩放裁剪图的尺寸
     */
    @Deprecated
    public void setDstSize(int width, int height) {
        mCameraView.setDstSize(width, height);
    }

    @Override
    public void setOnTouchListener(OnTouchListener l) {
        mTouchEvent = l;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mTouchEvent != null) {
            mTouchEvent.onTouch(this, event);
        }
        return super.onTouchEvent(event);
    }

    public String getCurrentPicPath() {
        return mCurrentPicPath;
    }

    /**
     * 设置拍照图片质量
     *
     * @param quality {@link #PICTURE_QUALITY_LOW} 、{@link #PICTURE_QUALITY_MIDDLE}、{@link #PICTURE_QUALITY_HEIGHT}
     */
    public void setPictureQuality(int quality) {
        mPictureQuality = quality;
    }

    private float getPictureRate() {
        return mPictureQuality / PICTURE_QUALITY_MIDDLE;
    }

    public void setMaskViewVisibility(int visibility) {
        mMaskView.setVisibility(visibility);
        showMaskView = visibility == View.VISIBLE;
    }

    public void setOrientationChangedListener(RotateDetector.OrientationChangedListener orientationChangedListener) {
        mOrientationChangedListener = orientationChangedListener;
    }

    public void setMaskViewColor(@ColorInt int color) {
        if (mMaskView != null) {
            mMaskView.setMaskColor(color);
        }
    }

    public void setZoomingListener(OnCameraZoomingListener listener) {
        mCameraView.setZoomingListener(listener);
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        mCameraView.setVisibility(visibility);
    }

    /**
     * 设置相机分辨率缩放值
     *
     * @param value 0, 1, 2, ... , 100. 0代表不放大，100代表放大到最大
     */
    public void setZoom(int value) {
        if (value < 0) {
            value = 0;
        }
        if (value > 100) {
            value = 100;
        }
        int mapValue = (int) (value / 100f * mCameraView.getMaxZoomValue());
        mCameraView.setZoom(mapValue);
    }

    public void showMaskView() {
        mMaskView.setVisibility(View.VISIBLE);
    }

    public void hideMaskView() {
        mMaskView.setVisibility(View.GONE);
    }

    /**
     * @return 0, 1, 2, ... , 100. 0代表不放大，100代表放大到最大
     */
    public int getCurrentZoom() {
        return mCameraView.getCurrentZoom();
    }

    /**
     * 闪光灯类型
     *
     * @param flashMode {@link Camera.Parameters#FLASH_MODE_AUTO}
     *                  {@link Camera.Parameters#FLASH_MODE_OFF}
     *                  {@link Camera.Parameters#FLASH_MODE_ON}
     */
    public void setFlashMode(String flashMode) {
        mCameraView.setFlashMode(flashMode);
    }

    public interface OnCameraZoomingListener {
        void onZooming(int value);
    }

    public void setCameraSizeListener(OnCameraSizeListener listener) {
        mCameraSizeListener = listener;
    }

    public interface OnCameraSizeListener {
        void onPreview(Camera.Size previewSize);

        void onPicture(Camera.Size pictureSize);
    }
}
