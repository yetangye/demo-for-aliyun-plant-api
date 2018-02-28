package com.tld.company.recognize;

import android.Manifest;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.View;

import com.tld.company.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import butterknife.ButterKnife;

public class RecognizeActivity extends FragmentActivity {

    public static final int REQUEST_CODE_PERMISSIONS_SETTING = 0;
    public static final int REQUEST_CODE_PERMISSIONS = 1;
    public static final int REQUEST_CODE_CROP = 3;
    static boolean isNotDeal = true;
    private String type;

    private RecognizePresenter mPresenter;

    AlertDialog dialog;

    @SuppressWarnings("TryWithIdenticalCatches")
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 23) {
            AppOpsManager appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            try {
                Method checkOp = appOpsManager.getClass().getMethod("checkOp", int.class, int.class, String.class);
                Integer resultCamera = (Integer) checkOp.invoke(appOpsManager, 26, Process.myUid(), getPackageName());
                Integer resultWriteStorage = (Integer) checkOp.invoke(appOpsManager, 60, Process.myUid(), getPackageName());
                if (resultCamera != 0 || resultWriteStorage != 0) {
                    showPermissionDialog();
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognize);
        ButterKnife.bind(this);
        type=getIntent().getStringExtra("PLANT_TYPE");
        if (isLackPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_PERMISSIONS);
        } else if (isLackPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSIONS);
        } else if (isLackPermissions(this, Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_PERMISSIONS);
        } else {
            addFragment();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        if (intent.getType() != null && intent.getType().contains("image/") && isNotDeal) {
            isNotDeal = false;
            Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
            mPresenter.startCrop(imageUri);
        }
    }

    private void addFragment() {
        FragmentManager fm = getSupportFragmentManager();
        RecognizeFragment recognizeFragment = (RecognizeFragment) fm.findFragmentById(R.id.ad_contentContainer);
        if (recognizeFragment == null) {
            recognizeFragment = RecognizeFragment.newInstance();
            recognizeFragment.setType(type);
            fm.beginTransaction().add(R.id.ad_contentContainer, recognizeFragment).commitAllowingStateLoss();
            mPresenter = new RecognizePresenter(recognizeFragment,type);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        View decorView = this.getWindow().getDecorView();
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
            decorView.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isNotDeal = true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            boolean isGrantedPrimaryPermission = true;
            for (int i = 0; i < grantResults.length; i++) {
                if (!Manifest.permission.ACCESS_FINE_LOCATION.equals(permissions[i]) &&
                        grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    isGrantedPrimaryPermission = false;
                }
            }
            if (isGrantedPrimaryPermission) {
                addFragment();
            } else {
                showPermissionDialog();
            }
        }
    }

    private void showPermissionDialog() {
        if (dialog != null) {
            if (dialog.isShowing()) {
                return;
            } else {
                dialog.show();
            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.help);
        String args = "相机权限\n读写权限";
        String helpText = String.format(getString(R.string.string_help_text), args);
        builder.setMessage(helpText);

        // 拒绝, 退出应用
        builder.setNegativeButton(R.string.quit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        builder.setPositiveButton(R.string.permission_settings, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_CODE_PERMISSIONS_SETTING);
            }
        });

        builder.setCancelable(false);

        dialog = builder.create();

        dialog.show();
    }

    boolean isLackPermissions(Context ctx, String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(ctx, permission) != PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PERMISSIONS_SETTING) {
            if (isLackPermissions(this, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSIONS);
            } else {
                addFragment();
            }
        }

        if (mPresenter != null) {
            mPresenter.result(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void finish() {
        super.finish();

    }

    public static void launch(Context ctx,String type) {
        Intent intent=new Intent(ctx, RecognizeActivity.class);
        intent.putExtra("PLANT_TYPE",type);
        ctx.startActivity(intent);

    }
}
