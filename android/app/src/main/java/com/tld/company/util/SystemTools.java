package com.tld.company.util;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Function: 系统服务工具类，主要方便getSystemService方法的调用
 */
public class SystemTools {
    private static final String TAG = SystemTools.class.getName();

    /**
     * 简单判断当前是否有网络
     */
    public static boolean isNetworkAvailable(Context context) {
        boolean result = false;
        if (context != null) {
            // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm == null) {
                return result;
            }
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = cm.getAllNetworkInfo();
            if (networkInfo == null) {
                return result;
            }
            for (NetworkInfo aNetworkInfo : networkInfo) {
                // 判断当前网络状态是否为连接状态
                if (aNetworkInfo.getState() == State.CONNECTED) {
                    result = true;
                }
            }
        }
        return result;
    }

    /**
     * 强制隐藏软键盘
     */
    public static void hideSoftInputFromWindow(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
