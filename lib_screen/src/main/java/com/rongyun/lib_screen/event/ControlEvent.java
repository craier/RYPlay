package com.rongyun.lib_screen.event;


import com.rongyun.lib_screen.entity.AVTransportInfo;
import com.rongyun.lib_screen.entity.RenderingControlInfo;

public class ControlEvent {
    private AVTransportInfo avtInfo;
    private RenderingControlInfo rcInfo;

    public AVTransportInfo getAvtInfo() {
        return avtInfo;
    }

    public void setAvtInfo(AVTransportInfo avtInfo) {
        this.avtInfo = avtInfo;
    }

    public RenderingControlInfo getRcInfo() {
        return rcInfo;
    }

    public void setRcInfo(RenderingControlInfo rcInfo) {
        this.rcInfo = rcInfo;
    }
}
