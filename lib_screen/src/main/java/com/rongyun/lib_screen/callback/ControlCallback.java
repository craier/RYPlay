package com.rongyun.lib_screen.callback;

public interface ControlCallback {
    void onSuccess();

    void onError(int code, String msg);
}
