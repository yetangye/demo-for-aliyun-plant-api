package com.tld.company.iflowercamera;

public class RawImage {
    public final byte[] imageData;
    /* 拍摄时手机旋转角度 */
    public final int orientation;

    public RawImage(byte[] data, int orientation) {
        imageData = data;
        this.orientation = orientation;
    }
}
