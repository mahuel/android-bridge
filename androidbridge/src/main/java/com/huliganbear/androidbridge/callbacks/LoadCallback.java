package com.huliganbear.androidbridge.callbacks;

public interface LoadCallback {
    void onLoadFail();
    void onLoadSuccess(String data);
}
