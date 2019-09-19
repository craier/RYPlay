package player.rongyun.com.playsdk.Utils;

import android.text.TextUtils;
import android.widget.Toast;

import player.rongyun.com.playsdk.App;

public class ToastUtils {
    private static Toast instanceShort = null;
    private static Toast instanceLong = null;

    public ToastUtils() {
    }

    public static void ToastShort(String content) {
        if (TextUtils.isEmpty(content)) {
            return;
        }
        //20173.16 chenyu修改
//        if (instanceLong != null) {
//            instanceLong.cancel();
//        }

        // if (instanceShort == null) {
        instanceShort = Toast.makeText(App.getContext(),
                content, Toast.LENGTH_SHORT);
        // } else {
        //    instanceShort.setText(content);
        //}

        instanceShort.show();
    }

    public static void ToastLong(String content) {
        if (TextUtils.isEmpty(content)) {
            return;
        }
        //20173.16 chenyu修改
//        if (instanceShort != null) {
//            instanceShort.cancel();
//        }

        // if (instanceLong == null) {
        instanceLong = Toast.makeText(App.getContext(),
                content, Toast.LENGTH_LONG);
        //} else {
        //    instanceLong.setText(content);
        //}

        instanceLong.show();
    }

    public static void ToastShort(int resId) {
        ToastShort(App.getContext().getResources().getString
                (resId));
    }

    public static void ToastLong(int resId) {
        ToastLong(App.getContext().getResources().getString
                (resId));
    }

    public static void cancelToast() {
        if (instanceLong != null) {
            instanceLong.cancel();
            instanceLong = null;
        }
        if (instanceShort != null) {
            instanceShort.cancel();
            instanceShort = null;
        }
    }
}
