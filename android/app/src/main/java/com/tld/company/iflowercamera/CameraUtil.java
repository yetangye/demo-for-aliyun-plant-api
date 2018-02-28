package com.tld.company.iflowercamera;

import android.graphics.Rect;
import android.hardware.Camera.Size;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class CameraUtil {
    private static final String TAG = "CameraUtil";

    /**
     * 获取最佳匹配分辨率数值
     * 策略：（未特殊说明，宽度和高度指的是屏幕的高度和宽度）
     * 如果有正好匹配的值，则返回S {@link #fitExactly(List, int, int)}。
     * 有比例相等的情况，返回sizes中正好比S大的值，如果没有比S大的值，按没有比例相等情况计算 {@link #fitRatio(List, float, float)}
     * 没有比例相等的情况，返回宽度正好等于相机高度的值，高度不做适配{@link #fitJustWidth(List, int)}
     * 返回宽高正好都大于S的值，若没找到合适的，返回最大值 {@link #fitNothing(List, int, int)}
     *
     * @param sizes  相机支持的分辨率列表
     * @param with   待适配分辨率的宽度
     * @param height 待适配分辨率的调试
     * @return 适配好的分辨率
     */
    static Size getMatcherSize(List<Size> sizes, int with, int height) {

        final int cameraWith = Math.max(with, height);
        final int cameraHeight = Math.min(with, height);

        List<Size> sortedSizes = sortAscByWidth(sizes);

        Size size = fitExactly(sortedSizes, cameraWith, cameraHeight);
        if (size != null) {
            return size;
        }
        Log.d(TAG, "未匹配到正好合适到分辨率");
        size = fitRatio(sortedSizes, cameraWith, cameraHeight);
        if (size != null) {
            return size;
        }
        Log.d(TAG, "未匹配到比率合适到分辨率");
        size = fitJustWidth(sortedSizes, cameraHeight);
        if (size != null) {
            return size;
        }
        Log.d(TAG, "未匹配到高度正好合适到分辨率");
        size = fitNothing(sortedSizes, cameraWith, cameraHeight);
        return size;
    }

    private static Size fitExactly(List<Size> sizes, int w, int h) {
        for (Size size : sizes) {
            if (size.width == w && size.height == h) {
                return size;
            }
        }
        return null;
    }

    private static Size fitRatio(List<Size> sizes, float w, float h) {
        float ratio = w / h;
        final int ss = sizes.size();

        for (int i = 0; i < ss; i++) {
            Size size = sizes.get(i);
            float r = (float) size.width / size.height;
            if (Math.abs(ratio - r) < 0.0000001f && size.width >= w) {
                return size;
            }
        }
        return null;
    }

    private static Size fitJustWidth(List<Size> sizes, int h) {
        for (Size size : sizes) {
            if (size.height == h) {
                return size;
            }
        }
        return null;
    }

    private static Size fitNothing(List<Size> sizes, int w, int h) {
        final int ss = sizes.size();
        for (int i = 0; i < ss; i++) {
            Size size = sizes.get(i);
            if (size.width >= w && size.height >= h) {
                return size;
            }
        }
        return sizes.get(ss - 1);
    }

    private static List<Size> sortAscByWidth(List<Size> sizes) {
        Size[] ss = new Size[sizes.size()];
        sizes.toArray(ss);
        for (int i = 0; i < sizes.size(); i++) {
            boolean isSwap = false;
            for (int j = sizes.size() - 1; j > i; j--) {
                if (ss[j].width < ss[j - 1].width ||
                        (ss[j].width == ss[j - 1].width && ss[j].height < ss[j - 1].height)) {
                    Size swap = ss[j];
                    ss[j] = ss[j - 1];
                    ss[j - 1] = swap;
                    isSwap = true;
                }
            }
            if (!isSwap) {
                break;
            }
        }
        return Arrays.asList(ss);
    }


    static Rect calculateTapArea(float x, float y, float coefficient, Size previewSize) {
        // 转换坐标系
        int centerX = (int) (y / previewSize.height * 2000 - 1000);
        int centerY = (int) (1000 - x / previewSize.height * 2000);

        float focusAreaSize = 300;
        int areaSize = Float.valueOf(focusAreaSize * coefficient).intValue();

        int left = clamp(centerX - areaSize / 2, -1000, 1000 - areaSize);
        int top = clamp(centerY - areaSize / 2, -1000, 1000 - areaSize);

        return new Rect(left, top, left + areaSize, top + areaSize);
    }

    private static int clamp(int x, int min, int max) {
        if (x > max) {
            return max;
        }
        if (x < min) {
            return min;
        }
        return x;
    }

    static Size getAspectRatioSize(List<Size> sizes, int width) {
        List<Size> standardSizes = getStandardRatioSize(sortAscByWidth(sizes));
        final int n = standardSizes.size();
        Size result = standardSizes.get(n - 1);
        for (int i = n - 2; i >= 0; i--) {
            Size size = standardSizes.get(i);
            if (size.height >= width) {
                result = size;
            }
        }
        return result;
    }

    private static List<Size> getStandardRatioSize(List<Size> sizes) {
        List<Size> result = new ArrayList<>();
        for (Size size : sizes) {
            if (Math.abs((4 / 3f) - ((float) Math.max(size.width, size.height) / Math.min(size.width, size.height))) < 0.001) {
                result.add(size);
            }
        }
        return result;
    }


}
