package com.tld.company.upload;

import com.tld.company.http.InvocationError;

public interface OnRecognizeListener<T> {
    void onSuccess(T recognizeResults);

    void onFail(InvocationError error);

}