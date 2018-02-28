package com.tld.company.recognize;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.tld.company.bean.HblFlowerInfo;

import java.util.List;

interface RecognizeContract {
    interface BaseView<T> {
        void setPresenter(T presenter);
    }

    interface BasePresenter {
        void start();
    }

    interface View extends BaseView<Presenter> {

        void showDiscernStep(String stepMsg, String nextStepMsg);

        void showUnknownUI(String msg);

        void showTakePicUI();

        void showFrameView(String path);

        void showResponseInfo(List<HblFlowerInfo> list);

        void setCropPath(String path);

        void showErrorToast(String msg);


    }

    interface Presenter extends BasePresenter {

        void requestImageInfo(Bitmap bitmap);

        void cancelLoading();

        void result(int requestCode, int resultCode, Intent data);

        void startCrop(Uri uri);

        void onRestoreInstanceState(@NonNull Bundle savedInstanceState);

        void onSaveInstanceState(Bundle outState);

    }
}
