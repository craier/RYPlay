package player.rongyun.com.playsdk.Http;


import com.google.gson.JsonObject;

import java.util.ArrayList;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;


/**
 * 服务端请求的封装
 */
public interface HttpRequestUtils {

    String LOGIN_HOST_API_DEV = "http://play2.cloud.dayang.com.cn/playreport/";
    String LOGIN_HOST_API = LOGIN_HOST_API_DEV;

    @POST("sdkreport?")
    Observable<JsonObject> reportSdk(@Query("sign") String sign,
                                        @Query("tm") long tm,
                                        @Query("appid") String appid,
                                        @Body RequestBody requestBody);

}