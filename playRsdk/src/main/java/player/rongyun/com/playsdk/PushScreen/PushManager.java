package player.rongyun.com.playsdk.PushScreen;

import android.text.TextUtils;

import com.rongyun.lib_screen.callback.ControlCallback;
import com.rongyun.lib_screen.entity.AVTransportInfo;
import com.rongyun.lib_screen.entity.ClingDevice;
import com.rongyun.lib_screen.entity.LocalItem;
import com.rongyun.lib_screen.entity.RemoteItem;
import com.rongyun.lib_screen.event.ControlEvent;
import com.rongyun.lib_screen.event.DeviceEvent;
import com.rongyun.lib_screen.manager.ClingManager;
import com.rongyun.lib_screen.manager.ControlManager;
import com.rongyun.lib_screen.manager.DeviceManager;
import com.rongyun.lib_screen.utils.VMDate;


import java.util.ArrayList;
import java.util.List;

import player.rongyun.com.playsdk.R;

import static com.rongyun.lib_screen.VError.NO_ERROR;
import static com.rongyun.lib_screen.VError.STATE_ERROR;

/**
 * Created by xdg on 2019/9/18.
 */

public class PushManager {
    private static PushManager instance;

    /**
     * 唯一获取单例对象实例方法
     */
    public static PushManager getInstance() {
        if (instance == null) {
            instance = new PushManager();
        }
        return instance;
    }

    private PlayCallback mCallback = null;
    private ArrayList<DeviceCallback> mDeviceCallbackList = null;
    private ArrayList<RemoteControlCallback> mControlCallbackList = null;

    public void startService() {
        ClingManager.getInstance().startClingService();
        ControlManager.getInstance().setRenderControlEvent(new ControlManager.RenderControlEvent() {
            @Override
            public void onRenderControlEvent(ControlEvent event) {
                for (int i = 0; mControlCallbackList != null && i < mControlCallbackList.size();
                     i++) {
                    mControlCallbackList.get(i).onControlEvent(event);
                }
            }
        });
        DeviceManager.getInstance().setDeviceManageEvent(new DeviceManager.DeviceManageEvent() {
            @Override
            public void onDeviceManageEvent(DeviceEvent event) {
                for (int i = 0; mDeviceCallbackList != null && i < mDeviceCallbackList.size();
                     i++) {
                    mDeviceCallbackList.get(i).onDeviceEvent(event);
                }
            }
        });
    }

    public void stopService() {
        ClingManager.getInstance().stopClingService();
        ControlManager.getInstance().setRenderControlEvent(null);
        DeviceManager.getInstance().setDeviceManageEvent(null);
    }

    //选择你要投屏的设备；
    public void selectClingDevice(ClingDevice device) {
        DeviceManager.getInstance().setCurrClingDevice(device);
    }

    //获取可以投屏的设备列表；
    public List<ClingDevice> getClingDevices() {
        return DeviceManager.getInstance().getClingDeviceList();
    }

    public void setRemoteItem(RemoteItem item) {
        ClingManager.getInstance().setRemoteItem(item);
    }

    public void setLocalItem(LocalItem item) {
        ClingManager.getInstance().setLocalItem(item);
    }

    public RemoteItem getRemoteItem() {
        return ClingManager.getInstance().getRemoteItem();
    }

    public LocalItem getLocalItem() {
        return ClingManager.getInstance().getLocalItem();
    }

    public void setPlayCallback(PlayCallback callback) {
        mCallback = callback;
    }

    public void setDeviceCallback(DeviceCallback deviceCallback) {
        if (deviceCallback == null) {
            return;
        }
        if (mDeviceCallbackList == null) {
            mDeviceCallbackList = new ArrayList<DeviceCallback>();
        }
        if (!mDeviceCallbackList.contains(deviceCallback)) {
            mDeviceCallbackList.add(deviceCallback);
        }
    }

    public void removeDeviceCallback(DeviceCallback deviceCallback) {
        if (deviceCallback == null || mDeviceCallbackList == null) {
            return;
        }
        if (mDeviceCallbackList.contains(deviceCallback)) {
            mDeviceCallbackList.remove(deviceCallback);
        }
    }

    public void setControlCallback(RemoteControlCallback controlCallback) {
        if (controlCallback == null) {
            return;
        }
        if (mControlCallbackList == null) {
            mControlCallbackList = new ArrayList<RemoteControlCallback>();
        }
        if (!mControlCallbackList.contains(controlCallback)) {
            mControlCallbackList.add(controlCallback);
        }
    }

    public void removeControlCallback(RemoteControlCallback controlCallback) {
        if (controlCallback == null || mControlCallbackList == null) {
            return;
        }
        if (mControlCallbackList.contains(controlCallback)) {
            mControlCallbackList.remove(controlCallback);
        }
    }

    public void play() {
        if (ControlManager.getInstance().getState() == ControlManager.CastState.STOPED) {
            if (getLocalItem() != null) {
                newPlayCastLocalContent(getLocalItem(), "1.0");
            } else if (getRemoteItem() != null) {
                newPlayCastRemoteContent(getRemoteItem(), "1.0");
            }
        } else if (ControlManager.getInstance().getState() == ControlManager.CastState.PAUSED) {
            playCast("1.0");
        }
    }

    public void play(String speed) {
        if (ControlManager.getInstance().getState() == ControlManager.CastState.STOPED) {
            if (getLocalItem() != null) {
                newPlayCastLocalContent(getLocalItem(), speed);
            } else if (getRemoteItem() != null) {
                newPlayCastRemoteContent(getRemoteItem(), speed);
            }
        } else if (ControlManager.getInstance().getState() == ControlManager.CastState.PAUSED) {
            playCast(speed);
        } else if (ControlManager.getInstance().getState() == ControlManager.CastState.PLAYING) {
            if (mCallback != null) {
                mCallback.onError(PERFORM_TYPE.onPlayCast, STATE_ERROR, "正在播放中");
            }

        } else {
            if (mCallback != null) {
                mCallback.onError(PERFORM_TYPE.onPlayCast, STATE_ERROR, "正在连接设备，请稍后操作");
            }
        }
    }

    private void newPlayCastLocalContent(final LocalItem localItem, final String speed) {
        ControlManager.getInstance().setState(ControlManager.CastState.TRANSITIONING);
        ControlManager.getInstance().newPlayCast(localItem, new ControlCallback() {
            @Override
            public void onSuccess() {
                ControlManager.getInstance().setState(ControlManager.CastState.PLAYING);
                ControlManager.getInstance().initScreenCastCallback();
                if (mCallback != null) {
                    mCallback.onSuccess(PERFORM_TYPE.NewPlayCastLocal);
                }
            }

            @Override
            public void onError(int code, String msg) {
                ControlManager.getInstance().setState(ControlManager.CastState.STOPED);
                if (mCallback != null) {
                    mCallback.onError(PERFORM_TYPE.NewPlayCastLocal, code, msg);
                }
            }
        }, speed);
    }

    /**
     * 网络投屏
     */
    private void newPlayCastRemoteContent(final RemoteItem remoteItem, final String speed) {

        ControlManager.getInstance().setState(ControlManager.CastState.TRANSITIONING);
        ControlManager.getInstance().newPlayCast(remoteItem, new ControlCallback() {
            @Override
            public void onSuccess() {
                ControlManager.getInstance().setState(ControlManager.CastState.PLAYING);
                ControlManager.getInstance().initScreenCastCallback();
                if (mCallback != null) {
                    mCallback.onSuccess(PERFORM_TYPE.onNewPlayCastRemote);
                }
            }

            @Override
            public void onError(int code, String msg) {
                ControlManager.getInstance().setState(ControlManager.CastState.STOPED);
                if (mCallback != null) {
                    mCallback.onError(PERFORM_TYPE.onNewPlayCastRemote, code, msg);
                }
            }
        }, speed);
    }

    /**
     * 播放
     */
    private void playCast(String speed) {
        ControlManager.getInstance().playCast(new ControlCallback() {
            @Override
            public void onSuccess() {
                ControlManager.getInstance().setState(ControlManager.CastState.PLAYING);
                if (mCallback != null) {
                    mCallback.onSuccess(PERFORM_TYPE.onPlayCast);
                }
            }

            @Override
            public void onError(int code, String msg) {
                if (mCallback != null) {
                    mCallback.onError(PERFORM_TYPE.onPlayCast, code, msg);
                }
            }
        }, speed);
    }

    public void pauseCast() {
        if (ControlManager.getInstance().getState() == ControlManager.CastState.PLAYING) {
            ControlManager.getInstance().pauseCast(new ControlCallback() {
                @Override
                public void onSuccess() {
                    ControlManager.getInstance().setState(ControlManager.CastState.PAUSED);
                    if (mCallback != null) {
                        mCallback.onSuccess(PERFORM_TYPE.onPauseCast);
                    }
                }

                @Override
                public void onError(int code, String msg) {
                    if (mCallback != null) {
                        mCallback.onError(PERFORM_TYPE.onPauseCast, code, msg);
                    }
                }
            });
        } else {
            if (mCallback != null) {
                mCallback.onError(PERFORM_TYPE.onPauseCast, STATE_ERROR, "目前不在播放状态");
            }
        }
    }

    /**
     * 退出投屏
     */
    public void stopCast() {
        ControlManager.getInstance().stopCast(new ControlCallback() {
            @Override
            public void onSuccess() {
                ControlManager.getInstance().setState(ControlManager.CastState.STOPED);
                ControlManager.getInstance().unInitScreenCastCallback();
                if (mCallback != null) {
                    mCallback.onSuccess(PERFORM_TYPE.onStopCast);
                }
            }

            @Override
            public void onError(int code, String msg) {
                if (mCallback != null) {
                    mCallback.onError(PERFORM_TYPE.onStopCast, code, msg);
                }
            }
        });
    }

    /**
     * 改变投屏进度
     */
    public void seekCast(int progress) {

        String target = VMDate.toTimeString(progress);
        ControlManager.getInstance().seekCast(target, new ControlCallback() {
            @Override
            public void onSuccess() {
                if (mCallback != null) {
                    mCallback.onSuccess(PERFORM_TYPE.onSeekCast);
                }
            }

            @Override
            public void onError(int code, String msg) {
                if (mCallback != null) {
                    mCallback.onError(PERFORM_TYPE.onSeekCast, code, msg);
                }
            }
        });
    }

    /**
     * 设置音量大小
     */
    public void setVolume(int volume) {
        ControlManager.getInstance().setVolumeCast(volume, new ControlCallback() {

            @Override
            public void onSuccess() {
                if (mCallback != null) {
                    mCallback.onSuccess(PERFORM_TYPE.onSetVolume);
                }
            }

            @Override
            public void onError(int code, String msg) {
                if (mCallback != null) {
                    mCallback.onError(PERFORM_TYPE.onSetVolume, code, msg);
                }
            }
        });
    }


    public void setMute(final boolean isMute) {
        ControlManager.getInstance().muteCast(isMute, new ControlCallback() {
            @Override
            public void onSuccess() {
                ControlManager.getInstance().setMute(isMute);
                if (mCallback != null) {
                    mCallback.onSuccess(PERFORM_TYPE.onMute);
                }
            }

            @Override
            public void onError(int code, String msg) {
                if (mCallback != null) {
                    mCallback.onError(PERFORM_TYPE.onMute, code, msg);
                }
            }
        });
    }

    public boolean getMute() {
        return ControlManager.getInstance().isMute();
    }

    public void setState(ControlManager.CastState state) {
        ControlManager.getInstance().setState(state);
    }

    public ControlManager.CastState getState() {
        return ControlManager.getInstance().getState();
    }

    public interface DeviceCallback {
        void onDeviceEvent(DeviceEvent event);
    }

    public interface RemoteControlCallback {
        void onControlEvent(ControlEvent event);
    }

    public interface PlayCallback {
        void onSuccess(PERFORM_TYPE type);

        void onError(PERFORM_TYPE type, int code, String msg);
    }

    public enum PERFORM_TYPE {
        NewPlayCastLocal,
        onNewPlayCastRemote,
        onPlayCast,
        onPauseCast,
        onStopCast,
        onSeekCast,
        onSetVolume,
        onMute
    }
};