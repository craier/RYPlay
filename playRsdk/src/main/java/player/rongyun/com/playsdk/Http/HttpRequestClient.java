package player.rongyun.com.playsdk.Http;

import android.content.Context;
import android.net.ParseException;
import android.os.Build;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import player.rongyun.com.playsdk.App;
import player.rongyun.com.playsdk.R;
import player.rongyun.com.playsdk.Utils.EncryptionUtil;
import player.rongyun.com.playsdk.Utils.GsonUtils;
import player.rongyun.com.playsdk.Utils.LogUtils;
import player.rongyun.com.playsdk.Utils.ToastUtils;
import retrofit2.HttpException;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import static player.rongyun.com.playsdk.Utils.FileUtils.TAG;


public class HttpRequestClient implements HttpListener<JsonObject> {

    public static final int USER_TYPE = 0;
    private static HttpRequestClient instance;
    private HttpRequestUtils httpRequestUserUtils;
    private Retrofit retrofitUser;//用于用户登录
    private HttpRequestUtils customRequestUtils;
    private Retrofit customRetrofit;
    private Observable<JsonObject> mObservable;

    public static HttpRequestClient getInstance() {
        if (instance == null) {
            synchronized (HttpRequestClient.class) {
                if (instance == null) {
                    instance = new HttpRequestClient();
                }
            }
        }
        return instance;
    }

    public HttpRequestUtils getHttpRequestUtils(int type) {
        if (type == USER_TYPE) {
            if (null == httpRequestUserUtils) {
                httpRequestUserUtils = getRetrofit(type).create(HttpRequestUtils.class);
            }
            return httpRequestUserUtils;
        }
        return null;
    }

    private Retrofit getRetrofit(int type) {
        if (type == USER_TYPE) {
            if (null == retrofitUser) {
                retrofitUser = new Retrofit.Builder()
                        .baseUrl(HttpRequestUtils.LOGIN_HOST_API)
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // 添加Rx适配器
                        .addConverterFactory(CustomGsonConverterFactory.create(GsonUtils.getInstance
                                ())) // 添加自定义Gson转换器 修改null字符串的处理
                        .client(getOkHttpClient())
                        .build();
            }
            return retrofitUser;
        }
        return null;
    }

    /**
     * 获取OkhttpClient,并进行配置
     *
     * @return
     */
    private OkHttpClient getOkHttpClient() {
        LoggingInterceptor logging = new LoggingInterceptor();
        logging.setLevel(LoggingInterceptor.Level.BODY);

        OkHttpClient.Builder builder = new OkHttpClient.Builder().connectTimeout(10, TimeUnit
                .SECONDS)
                .connectTimeout(10 * 1000, TimeUnit.MILLISECONDS)//连接超时时间
                .readTimeout(10 * 1000, TimeUnit.MILLISECONDS)
                .writeTimeout(10 * 1000, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(true) // 失败重发
                .addInterceptor(new HeaderInterceptor())
                .addInterceptor(logging);
        return builder.build();
    }

    /**
     * 发起订阅请求
     *
     * @param observable
     * @param type
     * @param mHttpListener
     * @param <T>
     */
    public <T> void toSubscribe(Observable<T> observable, final DataRequestType type, final
    HttpListener<T> mHttpListener) {
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<T>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(T value) {
                        if (null != mHttpListener) {
                            mHttpListener.onSuccess(type, value);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        String errorMsg = catchException(e);
                        int errorCode = -1;
                        if (e instanceof ResultException) {
                            ResultException resultException = (ResultException) e;
                            errorCode = resultException.getErrorCode();
                        }
                        if (null != mHttpListener) {
                            mHttpListener.onFailure(type, errorMsg, errorCode);
                        }

                        try {
                            if (errorCode == HttpErrorCode.TOKEN_INVALID) {

                            }
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    /**
     * 取消订阅
     *
     * @param observable 需要取消的订阅信息
     * @param <T>
     * @return
     */
    public <T> boolean unSubscribe(Observable<T> observable) {
        if (observable != null) {
            try {
                observable.unsubscribeOn(Schedulers.io());
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 捕获异常
     *
     * @param e
     */
    private String catchException(Throwable e) {
        Context context = App.getContext();
        //请求异常提示
        String errorMsg = "";
        if (e instanceof UnknownHostException) {
            errorMsg = context.getString(R.string.network_connection_faile);
        } else if (e instanceof SocketTimeoutException) {
            errorMsg = context.getString(R.string.network_connection_time_out);
        } else if (e instanceof ConnectException) {
            errorMsg = context.getString(R.string.network_connection_time_out);
        } else if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            int responseCode = httpException.code();
            if (responseCode >= 400 && responseCode <= 417) {
                errorMsg = context.getString(R.string.network_url_error);
            } else if (responseCode >= 500 && responseCode <= 505) {
                errorMsg = context.getString(R.string.network_server_busy);
            } else {
                errorMsg = context.getString(R.string.network_connection_exception);
            }
        } else if (e instanceof ResultException) {
            ResultException resultException = (ResultException) e;
            errorMsg = resultException.getErrorMsg();
        } else if (e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof ParseException
                || e instanceof NullPointerException) {
            errorMsg = context.getString(R.string.network_data_exception);
        } else {
            errorMsg = context.getString(R.string.network_unknown_error);
        }
        return errorMsg;
    }

    public void reportSdk(int type, String playUrl) {
        LogUtils.e(TAG, "reportSdk");
        long time = System.currentTimeMillis();
        String appid = "905d2617764c4bababa8940309d3fbff";
        String appSecret = "4a51d1ff-49dc-4312-abd4-07fe68ff6814";
        String sign = EncryptionUtil.encodeMD5(appid + appSecret +
                String.valueOf(time));
        String version = android.os.Build.VERSION.RELEASE;
        String SerialNumber = android.os.Build.SERIAL;
        String deviceType = Build.MANUFACTURER + Build.DEVICE;
        try {
            ReportSdkBean reportSdkBean = new ReportSdkBean();
            reportSdkBean.setType(type);
            reportSdkBean.setPlayUrl(playUrl);
            reportSdkBean.setDeviceId(SerialNumber);
            reportSdkBean.setDeviceSysVersion(version);
            reportSdkBean.setDeviceMachineType(deviceType);
            reportSdkBean.setSdkVersion("1.0");
            Gson gson = new Gson();
            //将map转成json
            String resultdata = gson.toJson(reportSdkBean);
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),
                    resultdata);
            mObservable = HttpRequestClient.getInstance().getHttpRequestUtils
                    (HttpRequestClient.USER_TYPE)
                    .reportSdk(sign, time, appid, requestBody);
            HttpRequestClient.getInstance().toSubscribe(mObservable, DataRequestType
                    .DATA_REQUEST_TYPE_REPORT, this);
        } catch (Exception e) {
            LogUtils.d("UploadVideoService", e.getMessage());
        }

    }

    @Override
    public void onSuccess(DataRequestType type, JsonObject jsonObject) {
        ToastUtils.ToastShort("上报成功");
    }

    @Override
    public void onFailure(DataRequestType type, String errorMsg, int errorCode) {
        ToastUtils.ToastShort(errorMsg);
    }
}