package com.tld.company.iflowercamera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

class RotateTransformation extends BitmapTransformation {
    private static final String ID = RotateTransformation.class.getName();
    private static final byte[] ID_BYTES = ID.getBytes();

    private float rotateRotationAngle = 0f;

    RotateTransformation(Context context, float rotateRotationAngle) {
        super(context);

        this.rotateRotationAngle = rotateRotationAngle;
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        Matrix matrix = new Matrix();

        matrix.postRotate(rotateRotationAngle);

        return Bitmap.createBitmap(toTransform, 0, 0, toTransform.getWidth(), toTransform.getHeight(), matrix, true);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof RotateTransformation;
    }

    @Override
    public int hashCode() {
        return ID_BYTES.hashCode();
    }


    public String getId() {
        return "onRotationDegreeChanged" + this.rotateRotationAngle;
    }
}