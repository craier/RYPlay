package player.rongyun.com.playsdk.Http;

public interface HttpListener<T> {

    void onSuccess(DataRequestType type, T t);

    void onFailure(DataRequestType type, String errorMsg, int errorCode);
}
