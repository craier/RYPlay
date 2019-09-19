package player.rongyun.com.playsdk.Http;

/**
 * Created by xdg on 2019/4/12.
 */

public class ReportSdkBean extends BaseBean {
    public static final Creator<ReportSdkBean> CREATOR = new Creator<>(ReportSdkBean.class);
    public int type;
    public String playUrl;
    public long viewTime;
    public long eventStartSpan;
    public long eventEndSpan;
    public String deviceId;
    public String deviceSysVersion;
    public String deviceMachineType;
    public String sdkVersion;
    public String errorCode;
    public String eventOccurSpan;

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }

    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
    }

    public String getPlayUrl() {
        return this.playUrl;
    }

    public void setViewTime(long viewTime){
        this.viewTime = viewTime;
    }

    public long getViewTime(){
        return this.viewTime;
    }

    public void setEventStartSpan(long eventStartSpan){
        this.eventStartSpan = eventStartSpan;
    }

    public long getEventStartSpan(){
        return this.eventStartSpan;
    }

    public void setEventEndSpan(long eventEndSpan){
        this.eventEndSpan = eventEndSpan;
    }

    public long getEventEndSpan(){
        return this.eventEndSpan;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceId() {
        return this.deviceId;
    }

    public void setDeviceSysVersion(String deviceSysVersion){
        this.deviceSysVersion = deviceSysVersion;
    }

    public String getDeviceSysVersion() {
        return this.deviceSysVersion;
    }

    public void setDeviceMachineType(String deviceMachineType){
        this.deviceMachineType = deviceMachineType;
    }

    public String getDeviceMachineType() {
        return this.deviceMachineType;
    }

    public void setSdkVersion(String sdkVersion){
        this.sdkVersion = sdkVersion;
    }

    public String getSdkVersion() {
        return this.sdkVersion;
    }

    public void setErrorCode(String errorCode){
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public void setEventOccurSpan(String eventOccurSpan){
        this.eventOccurSpan = eventOccurSpan;
    }

    public String getEventOccurSpan() {
        return this.eventOccurSpan;
    }
}
