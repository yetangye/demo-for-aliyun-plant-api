package com.tld.company.util;

import android.widget.Toast;

import com.tld.company.MainApplication;

public class ToastUtils {
    private static Toast mToastShort = null;

    public static void showShortToast(CharSequence charSequence) {
        if (mToastShort == null) {
            mToastShort = Toast.makeText(MainApplication.getInstance(), charSequence, Toast.LENGTH_SHORT);
        } else {
            mToastShort.setText(charSequence);
        }
        mToastShort.show();
    }

}
