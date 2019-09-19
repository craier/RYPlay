package com.rongyun.lib_screen.callback;


import com.rongyun.lib_screen.entity.RenderingControlInfo;
import com.rongyun.lib_screen.utils.LogUtils;

import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.support.lastchange.EventedValue;
import org.fourthline.cling.support.lastchange.LastChangeParser;
import org.fourthline.cling.support.model.Channel;
import org.fourthline.cling.support.renderingcontrol.lastchange.ChannelMute;
import org.fourthline.cling.support.renderingcontrol.lastchange.ChannelVolume;
import org.fourthline.cling.support.renderingcontrol.lastchange.RenderingControlLastChangeParser;

import java.util.List;

public abstract class RenderingControlCallback extends BaseSubscriptionCallback {
    private final String TAG = this.getClass().getSimpleName();

    protected RenderingControlCallback(Service service) {
        super(service);
    }

    @Override
    protected LastChangeParser getLastChangeParser() {
        return new RenderingControlLastChangeParser();
    }

    @Override
    protected void onReceived(List<EventedValue> values) {
        RenderingControlInfo info = new RenderingControlInfo();
        for (EventedValue entry : values) {
            if ("Mute".equals(entry.getName())) {
                Object obj = entry.getValue();
                if (obj instanceof ChannelMute) {
                    ChannelMute cm = (ChannelMute) obj;
                    if (Channel.Master.equals(cm.getChannel())) {
                        info.setMute(cm.getMute());
                    }
                }
            }
            if ("Volume".equals(entry.getName())) {
                Object obj = entry.getValue();
                if (obj instanceof ChannelVolume) {
                    ChannelVolume cv = (ChannelVolume) obj;
                    if (Channel.Master.equals(cv.getChannel())) {
                        info.setVolume(cv.getVolume());
                    }
                }
            }
            if ("PresetNameList".equals(entry.getName())) {
                Object obj = entry.getValue();
                info.setPresetNameList(obj.toString());
            }
        }
        LogUtils.d("RenderingControlCallback onReceived:", "  info.isMute===  "+info.isMute() +"  info.getVolume===  "+info.getVolume());
        received(info);
    }

    protected abstract void received(RenderingControlInfo info);

}
