package com.tld.company.util;

import android.graphics.Bitmap;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class FileTools {

    public static String convertToBase64(Bitmap scaledBitmap) {
        if (scaledBitmap == null || scaledBitmap.isRecycled()) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
        byte[] bytes = out.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }
}
