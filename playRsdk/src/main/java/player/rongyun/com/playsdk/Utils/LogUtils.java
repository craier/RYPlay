package player.rongyun.com.playsdk.Utils;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import java.io.File;

import player.rongyun.com.playsdk.App;


public class LogUtils {
    private static boolean ENABLE_LOG = true;
    private static final String APP_LOG_DIR = FileUtils.getTraceDir(App.getContext());

    public LogUtils() {
    }

    public static final void enableDebugMode(boolean enabled) {
        ENABLE_LOG = enabled;
    }

    public static final void d(String tag, String msg) {
        if (ENABLE_LOG) {
            Log.d(tag, msg);
        }

    }

    public static final void d(String tag, String msg, Throwable tr) {
        if (ENABLE_LOG) {
            Log.d(tag, msg, tr);
        }

    }

    public static final void i(String tag, String msg) {
        if (ENABLE_LOG) {
            Log.i(tag, msg);
        }

    }

    public static final void i(String tag, String msg, Throwable tr) {
        if (ENABLE_LOG) {
            Log.i(tag, msg, tr);
        }

    }

    public static final void w(String tag, String msg) {
        if (ENABLE_LOG) {
            Log.w(tag, msg);
        }

    }

    public static final void w(String tag, Throwable tr) {
        if (ENABLE_LOG) {
            Log.w(tag, tr);
        }

    }

    public static final void w(String tag, String msg, Throwable tr) {
        if (ENABLE_LOG) {
            Log.w(tag, msg, tr);
        }

    }

    public static final void e(String tag, String msg) {
        if (ENABLE_LOG) {
            Log.e(tag, msg);
        }

    }

    public static final void e(String tag, String msg, Throwable tr) {
        if (ENABLE_LOG) {
            Log.e(tag, msg, tr);
        }

    }

    public static final void p(Object obj) {
        if (ENABLE_LOG) {
            System.out.println(obj);
        }

    }

    public static final void file(String str) {
        String versionName = ApplicationTools.getVersionName(App.getContext());
        String today = DateUtils.formatYearMonthDay(System.currentTimeMillis());


        String fileName = null;
        if (versionName != null) {
            fileName = "V" + versionName + "_" + today + ".txt";
        } else {
            fileName = today + ".txt";
        }

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            String logPath = APP_LOG_DIR + fileName;
            String time = DateUtils.formatDate(System.currentTimeMillis());
            File dir = new File(APP_LOG_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File file = new File(logPath);
            String appendlog = "\n" + time + ":" + str;
            if (file.exists()) {
                // 增加一行，说明已经存在
                FileUtils.writeFile(logPath, appendlog, true);
            } else {
                //新建
                FileUtils.writeFile(logPath, appendlog, false);
            }
        }
    }

    public static String getTraceInfo() {
        if (!ENABLE_LOG) {
            return "";
        } else {
            StringBuffer sb = new StringBuffer();
            StackTraceElement[] stacks = (new Throwable()).getStackTrace();
            sb.append("[file:").append(stacks[1].getFileName()).append(",line:").append(stacks[1]
                    .getLineNumber()).append(",method:").append(stacks[1].getMethodName() + "];");
            return sb.toString();
        }
    }
}

