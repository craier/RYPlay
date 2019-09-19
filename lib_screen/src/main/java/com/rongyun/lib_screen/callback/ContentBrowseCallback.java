package com.rongyun.lib_screen.callback;

import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.support.contentdirectory.callback.Browse;
import org.fourthline.cling.support.model.BrowseFlag;
import org.fourthline.cling.support.model.SortCriterion;

public abstract class ContentBrowseCallback extends Browse {

    public ContentBrowseCallback(Service service, String containerId) {
        super(service, containerId, BrowseFlag.DIRECT_CHILDREN, "*", 0, null,
                new SortCriterion(true, "dc:title"));
    }

    @Override
    public void updateStatus(Status status) {

    }
}
