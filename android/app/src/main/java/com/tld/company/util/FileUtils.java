package com.tld.company.util;

import android.os.Environment;
import android.util.Log;

import java.io.File;

public class FileUtils {
    private static final String TAG = FileUtils.class.getSimpleName();
    public static final String HBZ_ROOT_DIR = "com.tld.company";

    private static String getFlowerDir() {
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + HBZ_ROOT_DIR + File.separator + "花卉识别");
        if (file.exists()) {
            return file.getPath();
        }
        if (file.mkdirs()) {
            return file.getPath();
        }
        Log.e(TAG, "获取花卉识别目录失败");
        return null;
    }

    public static String getFlowerSrcDir() {
        return getFlowerDir();
    }

    public static String getFlowerCropDir() {
        String flowerDir = getFlowerDir();
        if (flowerDir == null) {
            return null;
        }
        File file = new File(flowerDir + File.separator + "crop");
        if (file.exists()) {
            return file.getPath();
        }
        if (file.mkdirs()) {
            return file.getPath();
        }
        Log.e(TAG, "获取花卉识别裁剪目录失败");
        return null;
    }
}
