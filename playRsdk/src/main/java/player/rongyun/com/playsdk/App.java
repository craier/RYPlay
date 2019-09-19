package player.rongyun.com.playsdk;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.rongyun.lib_screen.VApplication;

import java.util.Timer;

import player.rongyun.com.playsdk.Http.HttpRequestClient;
import player.rongyun.com.playsdk.Http.HttpRequestUtils;

/**
 * 描述：
 *
 * @author Yanbo
 * @date 2018/11/6
 */
public class App extends Application {

    protected static Context context;
    private final static int MES_TIME_TICK = 0x2001;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MES_TIME_TICK:
                    HttpRequestClient.getInstance().reportSdk(4,"");
                    mHandler.sendEmptyMessageDelayed(MES_TIME_TICK,10*1000);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        VApplication.init(this);
        context = this;
      //  mHandler.sendEmptyMessageDelayed(MES_TIME_TICK,10*1000);
    }

    public static Context getContext() {
        return context;
    }


}
