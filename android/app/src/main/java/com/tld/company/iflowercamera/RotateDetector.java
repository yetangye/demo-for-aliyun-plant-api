package com.tld.company.iflowercamera;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;

public class RotateDetector {
    private int lastRotation;
    private RotationDegreeListener mRotationDegreeListener;

    public RotateDetector(RotationDegreeListener listener) {
        mRotationDegreeListener = listener;
    }

    public void onRotated(int rotation) {
        if (rotation == 3 && lastRotation == 0) {
            rotate(0, -90);
        } else if (rotation == 0 && lastRotation == 3) {
            rotate(-90, 0);
        } else {
            rotate(90 * lastRotation, 90 * rotation);
        }
        lastRotation = rotation;
    }

    private void rotate(int from, int to) {
        ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.setIntValues(from, to);
        valueAnimator.setDuration(300);
        valueAnimator.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int degree = (int) animation.getAnimatedValue();
                mRotationDegreeListener.onRotationDegreeChanged(degree);
            }
        });
        valueAnimator.start();
    }

    public interface RotationDegreeListener {
        void onRotationDegreeChanged(int degree);
    }

    public interface OrientationChangedListener {
        void onOrientationChanged(int rotation);
    }
}
