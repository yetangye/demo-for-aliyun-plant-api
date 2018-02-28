package com.tld.company.recognize;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import com.tld.company.MainApplication;
import com.tld.company.bean.HblFlowerInfo;
import com.tld.company.http.ErrorType;
import com.tld.company.http.InvocationError;
import com.tld.company.upload.OnRecognizeListener;
import com.tld.company.upload.Recognize2AsyncTask;
import com.tld.company.upload.RecognizeAsyncTask;
import com.tld.company.upload.RecognizeManager;
import com.tld.company.upload.WeedRecognizeAsyncTask;
import com.tld.company.util.FileTools;
import com.tld.company.util.FileUtils;
import com.tld.company.util.SystemTools;

import java.io.File;
import java.util.List;

public class RecognizePresenter implements RecognizeContract.Presenter {


    private static final int FIX_SIDE = 299;

    private final RecognizeContract.View mRecognizeView;

    private RecognizeAsyncTask mRecognizeAsyncTask;
    private Recognize2AsyncTask mRecognize2AsyncTask;
    private WeedRecognizeAsyncTask mWeedRecognizeAsyncTask;

    private String type;

    public RecognizePresenter(@NonNull RecognizeContract.View view, String type) {
        mRecognizeView = view;
        mRecognizeView.setPresenter(this);
        this.type=type;
    }

    @Override
    public void cancelLoading() {
        if (mRecognizeAsyncTask != null) {
            mRecognizeAsyncTask.cancel();
        }
        if (mRecognize2AsyncTask != null) {
            mRecognize2AsyncTask.cancel();
        }
        if (mWeedRecognizeAsyncTask != null) {
            mWeedRecognizeAsyncTask.cancel();
        }
    }

    private List<HblFlowerInfo> mHblFlowerInfos;

    @Override
    public void requestImageInfo(@NonNull Bitmap bitmap) {
        mRecognizeView.showDiscernStep("正在识别...", null);
        String s = FileTools.convertToBase64(Bitmap.createScaledBitmap(bitmap, 500, 500, false));
        if("Weeds".equals(type)) {
            mWeedRecognizeAsyncTask = RecognizeManager.weedRecognize(s, new OnRecognizeListener<List<HblFlowerInfo>>() {
                @Override
                public void onSuccess(List<HblFlowerInfo> recognizeResults) {
                    mHblFlowerInfos = recognizeResults;
                    mRecognizeView.showResponseInfo(recognizeResults);
                }

                @Override
                public void onFail(InvocationError error) {
                    if (error.getErrorType() == ErrorType.USER_CANCELED) {
                        mRecognizeView.showTakePicUI();
                    } else if (!SystemTools.isNetworkAvailable(MainApplication.getInstance())) {
                        mRecognizeView.showUnknownUI("请检查网络");
                    } else {
                        mRecognizeView.showUnknownUI("没有识别出来...");
                    }
                }


            });
            mWeedRecognizeAsyncTask.execute();
        }else if("Flower".equals(type)){
            mRecognizeAsyncTask = RecognizeManager.recognize(s, new OnRecognizeListener<List<HblFlowerInfo>>() {
                @Override
                public void onSuccess(List<HblFlowerInfo> recognizeResults) {
                    mHblFlowerInfos = recognizeResults;
                    mRecognizeView.showResponseInfo(recognizeResults);
                }

                @Override
                public void onFail(InvocationError error) {
                    if (error.getErrorType() == ErrorType.USER_CANCELED) {
                        mRecognizeView.showTakePicUI();
                    } else if (!SystemTools.isNetworkAvailable(MainApplication.getInstance())) {
                        mRecognizeView.showUnknownUI("请检查网络");
                    } else {
                        mRecognizeView.showUnknownUI("没有识别出来...");
                    }
                }
            });
            mRecognizeAsyncTask.execute();
        }else{
            mRecognize2AsyncTask = RecognizeManager.recognize2(s, new OnRecognizeListener<List<HblFlowerInfo>>() {
                @Override
                public void onSuccess(List<HblFlowerInfo> recognizeResults) {
                    mHblFlowerInfos = recognizeResults;
                    mRecognizeView.showResponseInfo(recognizeResults);
                }

                @Override
                public void onFail(InvocationError error) {
                    if (error.getErrorType() == ErrorType.USER_CANCELED) {
                        mRecognizeView.showTakePicUI();
                    } else if (!SystemTools.isNetworkAvailable(MainApplication.getInstance())) {
                        mRecognizeView.showUnknownUI("请检查网络");
                    } else {
                        mRecognizeView.showUnknownUI("没有识别出来...");
                    }
                }
            });
            mRecognize2AsyncTask.execute();
        }
    }

    @Override
    public void start() {
    }

    @Override
    public void startCrop(Uri uri) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);
        intent.putExtra("scaleUpIfNeeded", true);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", FIX_SIDE);
        intent.putExtra("outputY", FIX_SIDE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoCropUri());
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
    }

    @Override
    public void result(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case RecognizeActivity.REQUEST_CODE_CROP:
                mRecognizeView.setCropPath(photoCropUri().getPath());
                new AsyncTask<String, Integer, Bitmap>() {
                    @Override
                    protected Bitmap doInBackground(String... params) {
                        return BitmapFactory.decodeFile(params[0]);
                    }

                    @Override
                    protected void onPostExecute(Bitmap bitmap) {
                        mRecognizeView.showFrameView(photoCropUri().getPath());
                        if (bitmap != null) {
                            requestImageInfo(bitmap);
                        } else {
                            mRecognizeView.showErrorToast("图片解析错误");
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, photoCropUri().getPath());
                break;
        }
    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
    }

    private Uri photoCropUri() {
        File file = new File(FileUtils.getFlowerCropDir(), "select_crop.jpg");
        return Uri.fromFile(file);
    }
}
