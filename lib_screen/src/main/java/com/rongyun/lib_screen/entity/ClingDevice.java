package com.rongyun.lib_screen.entity;

import org.fourthline.cling.model.meta.Device;

public class ClingDevice {
    private Device device;
    private boolean isSelected = false;

    public ClingDevice(Device device) {
        this.device = device;
    }

    public Device getDevice() {
        return device;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

}
