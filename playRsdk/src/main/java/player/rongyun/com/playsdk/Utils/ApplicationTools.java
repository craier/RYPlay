package player.rongyun.com.playsdk.Utils;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import player.rongyun.com.playsdk.BuildConfig;

/**
 * 整个应用相关的工具类
 */
public class ApplicationTools {

    public static final String NETWORK_CLASS_UNKNOWN = "unknown";
    public static final String NETWORK_CLASS_WIFI = "wifi";
    public static final String NETWORK_CLASS_2_G = "2g";
    public static final String NETWORK_CLASS_3_G = "3g";
    public static final String NETWORK_CLASS_4_G = "4g";
    private static final String TAG = ApplicationTools.class.getSimpleName();
    //	private static final String LOCAL_APP_FILES_DIR = HuaAoApplication.getInstance()
    // .getFilesDir().getAbsolutePath() + File.separator;
    private static String mVersionName = "";
    private static int mVersionCode = 0;
    private static String mFormatVersion = "";


    public static String getFormatVersion(Context context) {
        if (!TextUtils.isEmpty(mFormatVersion)) {
            return mFormatVersion;
        }
        String verName = ApplicationTools.getVersionName(context);
        int verCode = ApplicationTools.getVersionCode(context);
        if (BuildConfig.DEBUG && verCode > 0) {
            mFormatVersion = verName + "." + verCode;
        } else {
            mFormatVersion = verName;
        }
        return mFormatVersion;
    }

    public static String getVersionName(Context context) {
        if (!TextUtils.isEmpty(mVersionName)) {
            return mVersionName;
        }

        String packageName = context.getPackageName();
        try {
            PackageInfo pm = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_CONFIGURATIONS);
            mVersionName = pm.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            LogUtils.e(TAG, "getVersionName error :", e);
        }

        return mVersionName;
    }

    public static int getVersionCode(Context context) {
        if (mVersionCode > 0) {
            return mVersionCode;
        }
        String packageName = context.getPackageName();
        try {
            PackageInfo pm = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_CONFIGURATIONS);
            mVersionCode = pm.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            LogUtils.e(TAG, "mVersionCode error :", e);
        }
        return mVersionCode;
    }

    //应用安装
    public static void installAPK(Context context, File file) {
        // 通过Intent安装APK文件
        Intent intents = new Intent();
        intents.setAction("android.intent.action.VIEW");
        intents.addCategory("android.intent.category.DEFAULT");
        intents.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // 如果不加上这句的话在apk安装完成之后点击单开会崩溃
//        android.os.Process.killProcess(android.os.Process.myPid());
        context.startActivity(intents);

    }

    /**
     * 网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context
                .CONNECTIVITY_SERVICE);
        if (connectivity == null) {

        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    /**
     * 把字节单位转换为M,K单位 小于1M，表示为xxxK 小于10M，表示为x.xK 大于10M， 表示为xM
     *
     * @param byteSize
     * @return
     */
    public static String transFileByteSize(long byteSize) {
        double fileSize = byteSize / 1024.0 / 1024.0;
        String strSize = String.valueOf(fileSize);
        int deltaNum1 = 3;// 有效数字
        // int deltaNum2 = 4;// 有效数字
        if (fileSize < 1) {
            // 小于1M
            int dotIndex = strSize.indexOf(".");
            deltaNum1 = Math.min(deltaNum1, strSize.substring(dotIndex + 1).length());
            strSize = strSize.substring(dotIndex + 1, dotIndex + 1 + deltaNum1);
            while (strSize.startsWith("0") && (strSize.length() > 1)) {
                strSize = strSize.substring(1, strSize.length());
            }
            strSize += "K";
        } else {
            // 如果不足10M，包括小数点，总共3位
            // strSize = strSize.substring(0, deltaNum2);
            // strSize += "M";
            // fyf修改，原有格式会出现242.M的显示
            int endIndex = strSize.indexOf(".") + 2;
            strSize = strSize.substring(0, endIndex);
            strSize += "M";
        }
        return strSize;
    }


    /**
     * 测量view大小
     *
     * @param child
     */
    @SuppressWarnings("deprecation")
    public static void measureView(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup
                    .LayoutParams.WRAP_CONTENT);
        }

        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = View.MeasureSpec.makeMeasureSpec(lpHeight, View.MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    /**
     * 获取mobile type 类型的具体值，参考 MbgRequetUtils中wiki
     *
     * @param context
     * @return
     */
    public static final int getMobileNetType(Context context) {
        int type = 2; // 2 means GRPS 2G

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context
                .CONNECTIVITY_SERVICE);
        NetworkInfo mobileNetInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        String subType = mobileNetInfo.getSubtypeName();
        if (subType.equalsIgnoreCase("EDGE")) {
            type = 3;
        } else if (subType.equalsIgnoreCase("UMTS")) {
            type = 4;
        } else if (subType.equalsIgnoreCase("HSDPA")) {
            type = 5;
        } else if (subType.equalsIgnoreCase("HSUPA")) {
            type = 6;
        } else if (subType.equalsIgnoreCase("HSPA")) {
            type = 7;
        } else if (subType.equalsIgnoreCase("CDMA")) {
            type = 8;
        } else if (subType.equalsIgnoreCase("EVDO_0")) {
            type = 9;
        } else if (subType.equalsIgnoreCase("EVDO_A")) {
            type = 10;
        } else if (subType.equalsIgnoreCase("1xRTT")) {
            type = 11;
        } else if (subType.equalsIgnoreCase("HSPAP")) {
            type = 12;
        } else if (subType.contains("LTE")) {
            type = 14;
        }

        return type;
    }


    /**
     * 获取网络类型
     *
     * @param context
     * @return
     */
    public static String getNetworkType(Context context) {

        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context
                .CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();

        if (info != null) {
            switch (info.getType()) {
                case ConnectivityManager.TYPE_WIFI:
                    return NETWORK_CLASS_WIFI;
                case ConnectivityManager.TYPE_MOBILE:
                    switch (info.getSubtype()) {
                        case TelephonyManager.NETWORK_TYPE_GPRS:
                        case TelephonyManager.NETWORK_TYPE_EDGE:
                        case TelephonyManager.NETWORK_TYPE_CDMA:
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                        case TelephonyManager.NETWORK_TYPE_IDEN:
                            return NETWORK_CLASS_2_G;
                        case TelephonyManager.NETWORK_TYPE_UMTS:
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        case TelephonyManager.NETWORK_TYPE_EVDO_A:
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                        case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        case TelephonyManager.NETWORK_TYPE_EHRPD:
                        case TelephonyManager.NETWORK_TYPE_HSPAP:
                            return NETWORK_CLASS_3_G;
                        case TelephonyManager.NETWORK_TYPE_LTE:
                            return NETWORK_CLASS_4_G;
                        default:
                            return NETWORK_CLASS_UNKNOWN;
                    }
                default:
                    return NETWORK_CLASS_UNKNOWN;
            }
        }

        return NETWORK_CLASS_UNKNOWN;
    }

    /**
     * 序列化一个对象
     *
     * @param object
     * @return
     */
    public static String getSeriString(Object object) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        String productBase64 = null;
        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            productBase64 = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
        } catch (IOException e) {
            LogUtils.e(TAG, e.toString());
        } catch (OutOfMemoryError e) {
            LogUtils.e(TAG, e.toString());
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    LogUtils.e(TAG, e.toString());
                }
            }
        }
        if (productBase64 == null) {
            productBase64 = "";
        }
        return productBase64;
    }

    /**
     * 反序列化一个对象
     *
     * @param data
     * @return
     * @throws Exception
     */
    public static Object getObjectFromSerString(String data) throws Exception {
        byte[] objBytes = Base64.decode(data.getBytes(), Base64.DEFAULT);
        if (objBytes == null || objBytes.length == 0) {
            return null;
        }
        ByteArrayInputStream bi = null;
        ObjectInputStream oi = null;
        Object object = null;
        try {
            bi = new ByteArrayInputStream(objBytes);
            oi = new ObjectInputStream(bi);
            object = oi.readObject();
        } finally {
            if (oi != null) {
                oi.close();
            }
            if (bi != null) {
                bi.close();
            }
        }
        return object;
    }


    /**
     * @param durationInSecond - 以秒为单位的时间段
     */
    public static String getDurationTimeFormatString(int durationInSecond) {
//        int minute = durationInSecond / 60;
//        int hour = 0;
//        hour = minute / 60;
//        minute = minute % 60;
//        int second = durationInSecond % 60;
//        String dura = null;
//        if (hour == 0) {
//            dura = String.format("%02d:%02d", minute, second);
//        } else {
//            dura = String.format("%02d:%02d:%02d", hour, minute, second);
//        }

        int minute = durationInSecond / 60;
        int second = durationInSecond % 60;
        String dura = String.format("%02d:%02d", minute, second);
        return dura;
    }


    /**
     * 根据生日获取毫秒数
     * 例如：“1970-11-11” ->   Date 对象
     *
     * @param birthday
     * @return
     */
    @SuppressWarnings("deprecation")
    public static Date getDateFromBirthday(String birthday) {
        Date birthDate = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            birthDate = dateFormat.parse(birthday);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        LogUtils.d(TAG, "year= " + birthDate.getYear() + ",month ==" + birthDate.getMonth());
        return birthDate;
    }

    /**
     * 产生blur 图
     *
     * @param context
     * @param bitmap
     * @return
     */
    public static Bitmap getBlurBitmap(Context context, Bitmap bitmap) {

        final float BITMAP_SCALE = 0.4f;
        final float BLUR_RADIUS = 5f;

        if (bitmap == null) {
            return null;
        }

        int width = Math.round(bitmap.getWidth() * BITMAP_SCALE);
        int height = Math.round(bitmap.getHeight() * BITMAP_SCALE);

        Bitmap inputBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);

        Bitmap blurBitmap = Bitmap.createBitmap(inputBitmap);


        /*RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        Allocation inAlloc = Allocation.createFromBitmap(rs,inputBitmap);
        Allocation blurAlloc = Allocation.createFromBitmap(rs,blurBitmap);

        blur.setInput(inAlloc);
        blur.setRadius(BLUR_RADIUS);
        blur.forEach(blurAlloc);
        blurAlloc.copyTo(blurBitmap);

        rs.destroy();*/


        return doBlur(blurBitmap, (int) BLUR_RADIUS, true);
    }


    private static Bitmap doBlur(Bitmap sentBitmap, int radius, boolean canReuseInBitmap) {

        Bitmap bitmap;
        if (canReuseInBitmap) {
            bitmap = sentBitmap;
        } else {
            bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
        }

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        bitmap.setPixels(pix, 0, w, 0, 0, w, h);

        return (bitmap);
    }
}
