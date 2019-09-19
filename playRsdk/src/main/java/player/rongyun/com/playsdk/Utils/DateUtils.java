package player.rongyun.com.playsdk.Utils;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间工具
 */
@SuppressLint("SimpleDateFormat")
public class DateUtils {

    /**
     * 格式化服务器返回的时间
     *
     * @param time
     * @return
     */
    public static String formatDate(long time) {
        SimpleDateFormat myFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (String.valueOf(time).length() == 10) {
            return myFmt.format(time * 1000);
        } else {
            return myFmt.format(time);
        }
    }


    /**
     * 格式化成hh:mm:ss
     *
     * @param ms
     * @return
     */
    public static String formatPushDate(long ms) {
        Integer ss = 1000;
        Integer mi = ss * 60;
        Integer hh = mi * 60;
        Integer dd = hh * 24;

        Long day = ms / dd;
        Long hour = (ms - day * dd) / hh;
        Long minute = (ms - day * dd - hour * hh) / mi;
        Long second = (ms - day * dd - hour * hh - minute * mi) / ss;
        Long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;

        StringBuffer sb = new StringBuffer();
        if(day > 0) {
            //sb.append(day+"天");
        }
        if(hour > 0) {
            if(hour<10){
                sb.append("0"+hour+":");
            }else{
                sb.append(hour+":");
            }
        }else{
            sb.append("00:");
        }
        if(minute > 0) {
            if(minute<10){
                sb.append("0"+minute+":");
            }else{
                sb.append(minute+":");
            }
        }else{
            sb.append("00:");
        }
        if(second > 0) {
            if(second<10){
                sb.append("0"+second+"");
            }else{
                sb.append(second+"");
            }

        }else{
            sb.append("00");
        }
//        if(milliSecond > 0) {
//            sb.append(milliSecond+"毫秒");
//        }
        return sb.toString();
    }


    /**
     * 格式化为年月日
     *
     * @param time
     * @return
     */
    public static String formatYearMonthDay(long time) {
        SimpleDateFormat myFmt = new SimpleDateFormat("yyyy-MM-dd");
        if (String.valueOf(time).length() == 10) {
            return myFmt.format(time * 1000);
        } else {
            return myFmt.format(time);
        }
    }

    /**
     * 格式化为年月日
     *
     * @param time
     * @return
     */
    public static String formatYearMonth(long time) {
        SimpleDateFormat myFmt = new SimpleDateFormat("yyyy-MM");
        if (String.valueOf(time).length() == 10) {
            return myFmt.format(time * 1000);
        } else {
            return myFmt.format(time);
        }
    }

    /**
     * 格式化为时分秒
     *
     * @param time
     * @return
     */
    public static String formatHourMinuteSecond(long time) {
        SimpleDateFormat myFmt = new SimpleDateFormat("HH:mm:ss");
        if (String.valueOf(time).length() == 10) {
            return myFmt.format(time * 1000);
        } else {
            return myFmt.format(time);
        }
    }

    /**
     * 格式化为时分
     *
     * @param time
     * @return
     */
    public static String formatHourMinute(long time) {
        SimpleDateFormat myFmt = new SimpleDateFormat("HH:mm");
        if (String.valueOf(time).length() == 10) {
            return myFmt.format(time * 1000);
        } else {
            return myFmt.format(time);
        }
    }


    /**
     * 格式化服务器返回的时间
     *
     * @param time
     * @return
     */
    public static String toDate(long time) {
        SimpleDateFormat myFmt = new SimpleDateFormat("HH时mm分ss秒");
        if (String.valueOf(time).length() == 10) {
            return myFmt.format(time * 1000);
        } else {
            return myFmt.format(time);
        }
    }

    /**
     * 格式化为年月日
     *
     * @param time
     * @return
     */
    public static String toYearMonthDay(long time) {
        SimpleDateFormat myFmt = new SimpleDateFormat("yyyy年MM月dd日");
        if (String.valueOf(time).length() == 10) {
            return myFmt.format(time * 1000);
        } else {
            return myFmt.format(time);
        }
    }

    /**
     * 格式化为月日
     *
     * @param time
     * @return
     */
    public static String toMonthDay(long time) {
        SimpleDateFormat myFmt = new SimpleDateFormat("MM月dd日");
        if (String.valueOf(time).length() == 10) {
            return myFmt.format(time * 1000);
        } else {
            return myFmt.format(time);
        }
    }

    /**
     * 格式化为时分秒
     *
     * @param time
     * @return
     */
    public static String toHourMinuteSecond(long time) {
        SimpleDateFormat myFmt = new SimpleDateFormat("HH时mm分ss秒");
        if (String.valueOf(time).length() == 10) {
            return myFmt.format(time * 1000);
        } else {
            return myFmt.format(time);
        }
    }

    /**
     * 通过时间戳获取当天的0点时间戳
     *
     * @param time 时间戳
     * @return
     */
    public static long getZeroHourByTime(long time) {
        long longTime = 0L;
        if (String.valueOf(time).length() == 10) {
            longTime = time * 1000;
        } else {
            longTime = time;
        }
        Date date = new Date(longTime);
        Date zeroofday = new Date(date.getYear(), date.getMonth(), date.getDate());
        return zeroofday.getTime();
    }

    public static boolean isToady(long time) {
        return DateUtils.getZeroHourByTime(time) == DateUtils.getZeroHourToday();
    }

    /**
     * 获取今天0点时间戳
     *
     * @return
     */
    public static long getZeroHourToday() {
        Date date = new Date();
        Date today = new Date(date.getYear(), date.getMonth(), date.getDate());
        return today.getTime();
    }

    /**
     * 将日期时间转换成毫秒
     *
     * @param date
     * @return
     */
    public static long dateToLong(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //得到毫秒数
        long timeLong = 0;
        try {
            timeLong = sdf.parse(date).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeLong;
    }

    /**
     * 将日期时间转换成对应格式
     *
     * @param time
     * @return
     */
    public static String formatToString(long time, String format) {
        if (TextUtils.isEmpty(format)) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat myFmt = new SimpleDateFormat(format);
        if (String.valueOf(time).length() == 10) {
            return myFmt.format(time * 1000);
        } else {
            return myFmt.format(time);
        }
    }
}
