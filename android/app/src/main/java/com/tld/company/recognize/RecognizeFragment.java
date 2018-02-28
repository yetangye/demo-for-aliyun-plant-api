package com.tld.company.recognize;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import com.tld.company.R;
import com.tld.company.bean.HblFlowerInfo;
import com.tld.company.iflowercamera.SimpleCamera;
import com.tld.company.util.FileUtils;
import com.tld.company.util.ToastUtils;

import java.io.File;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RecognizeFragment extends Fragment implements RecognizeContract.View {

    private RecognizeContract.Presenter mPresenter;

    SimpleCamera mSimpleCamera;
    @BindView(R.id.cameraStub)
    ViewStub cameraStub;
    @BindView(R.id.take_pic)
    View takePic;
    @BindView(R.id.fd_backView)
    View backView;
    @BindView(R.id.fd_loadingView)
    View loadingView;
    @BindView(R.id.msgView)
    View msgView;
    @BindView(R.id.showStep)
    TextView mStepView;

    @BindView(R.id.fd_bottomCenterContainer)
    View mTakeContainer;
    @BindView(R.id.back_btn)
    View mExit;
    @BindView(R.id.responseInfoContainer)
    View responseContainer;
    @BindView(R.id.responseImgView)
    ImageView responseImageView;
    @BindView(R.id.responseHtmlTextView)
    TextView responseHtmlTextView;
    @BindView(R.id.ldl_recyclerView)
    RecyclerView mRecyclerView;
    RecognizeAdapter mAdapter;
    @BindView(R.id.discernTitleTextView)
    TextView mdiscernTitleTextView;

    private String mCropPicPath;
    private Bitmap newestCropBitmap;
    BlurTransformation mBlurTransformation;

    private String type;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recognize, container, false);
        ButterKnife.bind(this, view);
        if (savedInstanceState != null) {
            if (mPresenter == null) {
                mPresenter = new RecognizePresenter(this,type);
            }
            mPresenter.onRestoreInstanceState(savedInstanceState);
        }
        if("Weeds".equals(type)){
            mdiscernTitleTextView.setText(R.string.take_photo_of_weeds);
        }
        mBlurTransformation = new BlurTransformation(getActivity(), 10);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new RecognizeAdapter();
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
        if (mSimpleCamera != null) {
            return;
        }
                if (cameraStub != null && mSimpleCamera == null) {
                    mSimpleCamera = (SimpleCamera) cameraStub.inflate();
                }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBlurTransformation != null) {
            mBlurTransformation.destroy();
            mBlurTransformation = null;
        }

        if (mSimpleCamera != null) {
            mSimpleCamera.releaseCamera();
        }
    }

    @OnClick(R.id.fd_backView)
    void onResponseCloseClick() {
        startCameraPreview();
    }

    @OnClick(R.id.take_pic)
    void onTackPicClick() {
        if (mSimpleCamera == null || mSimpleCamera.getAlpha() < 0.99f) {
            return;
        }
        showDiscernStep("正在取景...", null);

        try {
            mSimpleCamera.takePicture(new SimpleCamera.BitmapCallback<Bitmap>() {
                @Override
                public void onResult(Bitmap bitmap) {
                    newestCropBitmap = bitmap;
                    mPresenter.requestImageInfo(bitmap);
                }
            }, FileUtils.getFlowerSrcDir() + File.separator + UUID.randomUUID() + ".jpg");
        } catch (Exception e) {
            showErrorToast("取景失败，请重新拍摄");
            startCameraPreview();
        }
    }


    @Override
    public void setCropPath(String path) {
        mCropPicPath = path;
    }



    @OnClick(R.id.back_btn)
    void onInfoClick() {
        getActivity().finish();
    }
    @Override
    public void showResponseInfo(List<HblFlowerInfo> list) {
        if (!isVisible() || takePic.getVisibility() == View.VISIBLE) {
            return;
        }
        msgView.setVisibility(View.GONE);

        loadingView.setVisibility(View.GONE);
        takePic.setVisibility(View.GONE);
        mStepView.setVisibility(View.GONE);

        mAdapter.replaceData(list);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showUnknownUI(String msg) {
        if (!isVisible()) {
            return;
        }
        msgView.setVisibility(View.VISIBLE);
        backView.setVisibility(View.VISIBLE);
        mStepView.setVisibility(View.VISIBLE);
        mStepView.setText(msg);

        loadingView.setVisibility(View.GONE);
        takePic.setVisibility(View.GONE);
        responseContainer.setVisibility(View.GONE);
    }

    @Override
    public void showDiscernStep(String stepMsg, String leftChoiceString) {
        if (!isVisible()) {
            return;
        }
        if (stepMsg.equals("正在取景...")) {
            loadingView.setVisibility(View.VISIBLE);
            backView.setVisibility(View.GONE);
        } else {
            loadingView.setVisibility(View.GONE);
            backView.setVisibility(View.VISIBLE);
        }

        mStepView.setText(stepMsg);
        msgView.setVisibility(View.VISIBLE);
        mStepView.setVisibility(View.VISIBLE);

        takePic.setVisibility(View.GONE);
        responseContainer.setVisibility(View.GONE);

    }

    @Override
    public void showFrameView(String path) {
        if (VERSION.SDK_INT < 17) {
            mSimpleCamera.setMaskImage(path, null);
        } else {
            mSimpleCamera.setMaskImage(path, mBlurTransformation);
        }
    }


    @Override
    public void showTakePicUI() {
        if (!isVisible()) {
            return;
        }
        takePic.setVisibility(View.VISIBLE);
        mSimpleCamera.setVisibility(View.VISIBLE);

        backView.setVisibility(View.GONE);
        loadingView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);
        msgView.setVisibility(View.GONE);
        mStepView.setVisibility(View.GONE);
        responseContainer.setVisibility(View.GONE);
    }

    @Override
    public void setPresenter(RecognizeContract.Presenter presenter) {
        mPresenter = presenter;
    }

    
    private void startCameraPreview() {
        mPresenter.cancelLoading();
        if (mSimpleCamera != null) {
            mSimpleCamera.startPreview();
        }
        if (newestCropBitmap != null && !newestCropBitmap.isRecycled()) {
            newestCropBitmap.recycle();
            newestCropBitmap = null;
        }
        showTakePicUI();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("PLANT_TYPE",type);
        mPresenter.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.result(requestCode, resultCode, data);
    }

    @Override
    public void showErrorToast(String msg) {
        ToastUtils.showShortToast(msg);
    }

    public static RecognizeFragment newInstance() {
        return new RecognizeFragment();
    }

    public void setType(String type) {
        this.type = type;
    }




}
