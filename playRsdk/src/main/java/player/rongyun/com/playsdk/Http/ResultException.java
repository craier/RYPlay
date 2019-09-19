package player.rongyun.com.playsdk.Http;

public class ResultException extends RuntimeException {
    private int errorCode;
    private String errorMsg;

    public ResultException(int errorCode, String errorMsg) {
        super(errorMsg);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
