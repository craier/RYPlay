package com.rongyun.lib_screen;

import android.content.Context;


public class VApplication {
    protected static Context mcontext;
    public static Context getContext() {
        return mcontext;
    }
    public static void init(Context context) {
        mcontext=context;
//        ClingManager.getInstance().startClingService();
    }
}
