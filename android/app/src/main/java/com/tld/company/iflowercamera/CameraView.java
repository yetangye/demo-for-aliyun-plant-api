package com.tld.company.iflowercamera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.view.MotionEventCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.tld.company.iflowercamera.SimpleCamera.SELECTOR_CROP_BITMAP;
import static com.tld.company.iflowercamera.SimpleCamera.SELECTOR_SCALED_BITMAP;
import static com.tld.company.iflowercamera.SimpleCamera.SELECTOR_SRC_BITMAP;

class CameraView extends SurfaceView {
    private static final String TAG = "CameraView";
    private static final int DEFAULT_DES_SIZE = 500;

    private Camera camera;
    private int mZoomValue;
    private int mActionPointId1;
    private int mActionPointId2;
    private Point mPoint1 = new Point();
    private Point mPoint2 = new Point();
    private double lastHypotenuseLength;
    private float mDensity;

    private int dstWidth;
    private int dstHeight;
    private OrientationEventAdapter mCatcher;

    private boolean handFocusable = true;
    private boolean isCameraIdle = true;

    private int mAspectRatio = 1;

    private int mMaxZoomValue;

    private SimpleCamera.OnCameraZoomingListener mZoomingListener;
    private String mFlashMode = Parameters.FLASH_MODE_AUTO ;

    private int mScreenHeight;
    private int mScreenWidth;

    private SimpleCamera.OnCameraSizeListener mSizeListener;

    private static Context mContext;

    public CameraView(Context context) {
        this(context, null);
        init();
        mContext = context;
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        mContext = context;
    }

    private void init() {
        mDensity = getResources().getDisplayMetrics().density;
        mScreenWidth = getResources().getDisplayMetrics().widthPixels;
        mScreenHeight = getResources().getDisplayMetrics().heightPixels;
        if (TextUtils.equals(Build.MODEL.toLowerCase(), "SM-T321".toLowerCase())) {
            mScreenWidth = 1080;
            mScreenHeight = 1920;
        } else if (mScreenHeight == 2560 && mScreenWidth == 1600) {
            mScreenWidth = 1080;
            mScreenHeight = 1920;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = MotionEventCompat.getActionMasked(event);
        int pointerIndex1 = -1;
        int pointerIndex2 = -1;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                Parameters params = getCameraParameters(camera);
                if (params != null && isSupportAutoFocus(params)) {
                    focusOnTouch(event.getX(), event.getY());
                }
                mActionPointId1 = MotionEventCompat.getPointerId(event, 0);
                mPoint1.x = (int) MotionEventCompat.getX(event, 0);
                mPoint1.y = (int) MotionEventCompat.getY(event, 0);
                break;
            case MotionEvent.ACTION_MOVE:
                pointerIndex1 = MotionEventCompat.findPointerIndex(event, mActionPointId1);
                pointerIndex2 = MotionEventCompat.findPointerIndex(event, mActionPointId2);
                if (pointerIndex1 < 0 || pointerIndex2 < 0) {
                    return false;
                }
                mPoint1.x = (int) MotionEventCompat.getX(event, pointerIndex1);
                mPoint1.y = (int) MotionEventCompat.getY(event, pointerIndex1);
                mPoint2.x = (int) MotionEventCompat.getX(event, pointerIndex2);
                mPoint2.y = (int) MotionEventCompat.getY(event, pointerIndex2);

                double hypotenuseLength = hypotenuseLength(mPoint1, mPoint2);
                setCameraZoomWithLastState((int) ((hypotenuseLength - lastHypotenuseLength) / (mDensity * 3)));
                lastHypotenuseLength = hypotenuseLength;
                break;
            case MotionEventCompat.ACTION_POINTER_DOWN:
                if (MotionEventCompat.getPointerCount(event) > 2) {
                    break;
                }
                pointerIndex2 = MotionEventCompat.getActionIndex(event);
                if (pointerIndex2 == -1) {
                    return false;
                }
                mActionPointId2 = MotionEventCompat.getPointerId(event, pointerIndex2);
                mPoint2.x = (int) MotionEventCompat.getX(event, pointerIndex2);
                mPoint2.y = (int) MotionEventCompat.getY(event, pointerIndex2);
                lastHypotenuseLength = hypotenuseLength(mPoint1, mPoint2);
                break;
            case MotionEvent.ACTION_UP:
                ((FrameLayout) getParent()).onTouchEvent(event);
                return false;
        }
        return true;
    }

    public void focusOnTouch(float touchX, float touchY) {
        Parameters params = getCameraParameters(camera);
        if (params == null || !isCameraIdle) {
            return;
        }
        Size previewSize = params.getPreviewSize();

        if (params.getMaxNumFocusAreas() > 0) {
            Rect focusRect = CameraUtil.calculateTapArea(touchX, touchY, 1f, previewSize);
            List<Camera.Area> focusAreas = new ArrayList<>();
            focusAreas.add(new Camera.Area(focusRect, 800));
            params.setFocusAreas(focusAreas);
        } else {
            Log.i(TAG, "focus areas not supported");
        }

        if (params.getMaxNumMeteringAreas() > 0) {
            Rect meterRect = CameraUtil.calculateTapArea(touchX, touchY, 1.5f, previewSize);
            List<Camera.Area> meterAreas = new ArrayList<>();
            meterAreas.add(new Camera.Area(meterRect, 500));
            params.setMeteringAreas(meterAreas);
        } else {
            Log.i(TAG, " meter areas not supported");
        }

        final String currentFocusMode = params.getFocusMode();
        if (isSupportAutoFocus(params)) {
            camera.cancelAutoFocus();
            params.setFocusMode(Parameters.FOCUS_MODE_MACRO);
        }

        try {
            camera.setParameters(params);
            camera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    Parameters params = getCameraParameters(camera);
                    if (params == null) {
                        return;
                    }
                    if (isSupportAutoFocus(params)) {
                        params.setFocusMode(currentFocusMode);
                    }
                    camera.setParameters(params);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "focusOnTouch: ", e);
        }

    }

    private boolean isSupportAutoFocus(Parameters params) {
        return params.getSupportedFocusModes() != null &&
                params.getSupportedFocusModes().contains(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
    }

    private double hypotenuseLength(@NonNull Point a, @NonNull Point b) {
        double deltaX = a.x - b.x;
        double deltaY = a.y - b.y;
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }

    public void setAspectRatio(int aspect) {
        mAspectRatio = aspect;
    }

    public void startPreview() throws RuntimeException {
        if (camera != null) {
            try {
                camera.startPreview();
                isCameraIdle = true;
            } catch (Exception e) {
                camera = null;
                isCameraIdle = false;
                if (e.getMessage().contains("release")) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    void initCamera(SurfaceHolder holder, float pictureRate) {
        try {
            camera = Camera.open(0);
        } catch (Throwable e) {
            Log.i(TAG, "打开相机失败", e);
            return;
        }

        if (camera == null) {
            Log.i(TAG, "打开相机失败");
            return;
        }

        Parameters parameters = getCameraParameters(camera);
        if (parameters == null) {
            return;
        }

        // 设置picture分辨率
        List<Size> supportedPictureSizes = parameters.getSupportedPictureSizes();

        Size pictureSize;
        if (mAspectRatio == 1) {
            pictureSize = CameraUtil.getAspectRatioSize(supportedPictureSizes, getWidth());
        } else {
            pictureSize = CameraUtil.getMatcherSize(supportedPictureSizes, (int) (mScreenWidth * pictureRate), (int) (mScreenHeight * pictureRate));
        }
        parameters.setPictureSize(pictureSize.width, pictureSize.height);
        Log.d(TAG, String.format(Locale.CHINA, "set picture size (%d, %d)", pictureSize.width, pictureSize.height));
        if (mSizeListener != null) {
            mSizeListener.onPicture(pictureSize);
        }

        // 设置preview分辨率
        List<Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
        if (mAspectRatio == 1) {
            // 4:3比率的分辨率
            Size fitPreviewSize = CameraUtil.getAspectRatioSize(supportedPreviewSizes, getWidth());
            parameters.setPreviewSize(fitPreviewSize.width, fitPreviewSize.height);
            Log.d(TAG, String.format(Locale.CHINA, "set preview size (%d, %d)", fitPreviewSize.width, fitPreviewSize.height));
            if (mSizeListener != null) {
                mSizeListener.onPreview(fitPreviewSize);
            }
        } else {
            // 16:9比率的分辨率
            Size previewSize;
            Size fitPreviewSize = CameraUtil.getMatcherSize(supportedPreviewSizes, pictureSize.width, pictureSize.height);
            if (fitPreviewSize.width == pictureSize.width && fitPreviewSize.height == pictureSize.height) {
                previewSize = fitPreviewSize;
            } else {
                previewSize = CameraUtil.getMatcherSize(supportedPreviewSizes, mScreenWidth, mScreenHeight);
            }
            parameters.setPreviewSize(previewSize.width, previewSize.height);
            Log.d(TAG, String.format(Locale.CHINA, "set preview size (%d, %d)", previewSize.width, previewSize.height));
            if (mSizeListener != null) {
                mSizeListener.onPreview(previewSize);
            }
        }


        // 旋转相机preview展示方向
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(0, info);
        int displayOrientation = info.orientation;
        if (isNexus5X()) {
            displayOrientation = 270;
        }
        camera.setDisplayOrientation(displayOrientation);

        // 设置自动对焦
        if (isSupportAutoFocus(parameters)) {
            parameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        }

        // 设置自动闪光灯
        if (parameters.getSupportedFlashModes() != null
                && parameters.getSupportedFlashModes().contains(mFlashMode)) {
            parameters.setFlashMode(mFlashMode);
            camera.setParameters(parameters);
        }

        // 设置焦距
        if (parameters.isZoomSupported()) {
            parameters.setZoom(mZoomValue);
        }

        mMaxZoomValue = parameters.getMaxZoom();

        try {
            camera.setParameters(parameters);
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    void setOrientationCatcher(OrientationEventAdapter catcher) {
        mCatcher = catcher;
    }

    void releaseCamera() {
        if (camera != null) {
            try {
                camera.cancelAutoFocus();
                camera.stopPreview();
                camera.release();
            } catch (Exception e) {
                Log.e(TAG, "releaseCamera: ", e);
            } finally {
                camera = null;
            }
        }
    }

    void setZoomingListener(SimpleCamera.OnCameraZoomingListener listener) {
        mZoomingListener = listener;
    }

    void setCameraZoomWithLastState(int value) {
        if (!handFocusable) {
            return;
        }

        mZoomValue = mZoomValue + value;
        if (mZoomValue < 0) {
            mZoomValue = 0;
        } else if (mZoomValue > mMaxZoomValue) {
            mZoomValue = mMaxZoomValue;
        }
        setZoom(mZoomValue);
    }

    void setZoom(int value) {
        mZoomValue = value;
        Parameters params = getCameraParameters(camera);
        if (params == null) {
            return;
        }
        params.setZoom(value);
        camera.setParameters(params);
        if (mZoomingListener != null) {
            mZoomingListener.onZooming(getCurrentZoom());
        }
    }

    int getMaxZoomValue() {
        return mMaxZoomValue;
    }

    int getCurrentZoom() {
        return (int) ((float) mZoomValue / mMaxZoomValue * 100);
    }

    private Parameters getCameraParameters(Camera camera) {
        if (camera == null) {
            return null;
        }
        try {
            return camera.getParameters();
        } catch (RuntimeException e) {
            Log.e(TAG, "getCameraParameters: ", e);
            return null;
        }
    }

    void decodeBitmapArray(final int selectorMode, RawImage rawImage, final SimpleCamera.BitmapCallback<Bitmap[]> callback, final boolean rotate) {
        new AsyncTask<RawImage, Integer, Bitmap[]>() {
            @Override
            protected Bitmap[] doInBackground(RawImage... params) {
                byte[] bytes = params[0].imageData;
                Bitmap bitmap;
                try {
                    bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    if (bitmap == null) {
                        return null;
                    }
                } catch (Exception e) {
                    return null;
                }
                Bitmap srcBitmap;
                if (rotate && mCatcher != null) {
                    srcBitmap = rotateBitmap(bitmap, mCatcher.getRotation());
                    if (srcBitmap != bitmap) {
                        bitmap.recycle();
                    }
                } else {
                    srcBitmap = bitmap;
                }
                Bitmap[] bitmaps = new Bitmap[3];
                if ((selectorMode & SELECTOR_SRC_BITMAP) > 0) {
                    bitmaps[0] = srcBitmap;
                }
                if ((selectorMode & SELECTOR_CROP_BITMAP) > 0) {
                    bitmaps[1] = cropBitmap(srcBitmap, params[0].orientation);
                }

                if ((selectorMode & SELECTOR_SCALED_BITMAP) > 0) {
                    int width = dstWidth == 0 ? DEFAULT_DES_SIZE : dstWidth;
                    int height = dstHeight == 0 ? DEFAULT_DES_SIZE : dstHeight;
                    bitmaps[2] = Bitmap.createScaledBitmap(scaleBitmap(srcBitmap, params[0].orientation), width, height, false);
                }

                if ((selectorMode & SELECTOR_SRC_BITMAP) == 0 && bitmaps[0] != null) {
                    bitmaps[0].recycle();
                    bitmaps[0] = null;
                }

                if ((selectorMode & SELECTOR_CROP_BITMAP) == 0 && bitmaps[1] != null) {
                    bitmaps[1].recycle();
                    bitmaps[1] = null;
                }

                if ((selectorMode & SELECTOR_SCALED_BITMAP) == 0 && bitmaps[2] != null) {
                    bitmaps[2].recycle();
                    bitmaps[2] = null;
                }
                return bitmaps;
            }

            @Override
            protected void onPostExecute(Bitmap[] bitmap) {
                callback.onResult(bitmap);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, rawImage);
    }

    void decodeBitmapArray(RawImage rawImage, final SimpleCamera.BitmapCallback<Bitmap[]> callback) {
        new AsyncTask<RawImage, Integer, Bitmap[]>() {
            @Override
            protected Bitmap[] doInBackground(RawImage... params) {
                Bitmap srcBitmap = decodeBitmap(params[0]);
                if (srcBitmap == null) {
                    return null;
                }
                Bitmap[] bitmaps = new Bitmap[2];
                bitmaps[0] = srcBitmap;
                bitmaps[1] = cropBitmap(srcBitmap, params[0].orientation);
                return bitmaps;
            }

            @Override
            protected void onPostExecute(Bitmap[] bitmap) {
                callback.onResult(bitmap);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, rawImage);
    }

    void takeRawPicture(final SimpleCamera.BitmapCallback<RawImage> bitmapCallback) {
        isCameraIdle = false;
        try {
            final int rotation = mCatcher == null ? -1 : mCatcher.getRotation();
            camera.takePicture(null, null, new PictureCallback() {
                @Override
                public void onPictureTaken(byte[] bytes, Camera camera) {
                    try {
                        camera.stopPreview();
                    }catch (Exception e){
                        //camera may be released.
                    }
                    bitmapCallback.onResult(new RawImage(bytes, rotation));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            bitmapCallback.onResult(null);
        }
    }

    private static Bitmap decodeBitmap(RawImage rawImage) {
        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeByteArray(rawImage.imageData, 0, rawImage.imageData.length);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        Bitmap srcBitmap = rotateBitmap(bitmap, rawImage.orientation);
        if (srcBitmap != bitmap) {
            bitmap.recycle();
        }
        return srcBitmap;
    }

    private static Bitmap cropBitmap(@NonNull final Bitmap bitmap, int rotation) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int x  = 0;
        int y = 0;
        int size = (int) (Math.min(width, height) * CameraMaskView.SIZE_RATIO);
        int widthEdge = width / CameraMaskView.SIZE_BASE;
        int heightEdge = height / CameraMaskView.SIZE_BASE;
        int newHeight = heightEdge+size+heightEdge;
        int newWidth = widthEdge+size+widthEdge;

        switch (rotation) {
            case Surface.ROTATION_180:
                y = height - newHeight;
                break;
            case Surface.ROTATION_270:
                x =  width - newWidth;
                break;
            default:

                break;
        }
        newHeight = heightEdge+size+heightEdge;
        newWidth = widthEdge+size+widthEdge;

        Log.i(TAG, "x:" + widthEdge + ",y:" + heightEdge + ",size:" + size);
        Log.i(TAG, "newWidth:" + newWidth + ",newHeight:" + newHeight);
        return Bitmap.createBitmap(bitmap, x, y, newWidth, newHeight);
    }

    private static Bitmap scaleBitmap(@NonNull final Bitmap bitmap, int rotation) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int x = 0, y = 0;
        int size = (int) (Math.min(width, height) * CameraMaskView.SIZE_RATIO);
        switch (rotation) {
            case Surface.ROTATION_180:
                x = width / CameraMaskView.SIZE_BASE;
                y = height - size - height / CameraMaskView.SIZE_BASE;
                break;
            case Surface.ROTATION_270:
                x = width - size - width / CameraMaskView.SIZE_BASE;
                y = height / CameraMaskView.SIZE_BASE;
                break;
            default:
                x = width / CameraMaskView.SIZE_BASE;
                y = height / CameraMaskView.SIZE_BASE;
                break;
        }
        return Bitmap.createBitmap(bitmap, x, y, size, size);
    }

    private static Bitmap rotateBitmap(Bitmap bitmap, int rotation) {
        if (bitmap == null)
            return null;
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix mtx = new Matrix();
        if (isNexus5X()) {
            rotation = (rotation + 2) % 4;
        }
        switch (rotation) {
            case Surface.ROTATION_0:
                mtx.postRotate(90);
                break;
            case Surface.ROTATION_90:
                mtx.postRotate(0);
                break;
            case Surface.ROTATION_180:
                mtx.postRotate(270);
                break;
            case Surface.ROTATION_270:
                mtx.postRotate(180);
                break;
        }
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }

    static boolean isNexus5X() {
        return TextUtils.equals(Build.MODEL.toLowerCase(), "nexus 5x".toLowerCase());
    }

    boolean isInitCamera() {
        return camera != null;
    }

    void setDstSize(int with, int height) {
        dstWidth = with;
        dstHeight = height;
    }

    void setHandFocusable(boolean enable) {
        handFocusable = enable;
    }

    void setFlashMode(String flashMode) {
        mFlashMode = flashMode;
        Parameters params = getCameraParameters(camera);
        if (params == null) {
            return;
        }
        if (params.getSupportedFlashModes() != null
                && params.getSupportedFlashModes().contains(mFlashMode)) {
            params.setFlashMode(mFlashMode);
            camera.setParameters(params);
        }
    }

    void setSizeListener(SimpleCamera.OnCameraSizeListener listener) {
        mSizeListener = listener;
    }
}