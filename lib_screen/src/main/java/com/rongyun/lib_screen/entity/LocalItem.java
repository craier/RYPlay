package com.rongyun.lib_screen.entity;

import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.DescMeta;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.WriteStatus;
import org.fourthline.cling.support.model.container.Container;
import org.fourthline.cling.support.model.item.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xdg on 2019/9/19.
 */

public class LocalItem extends Item {

    public LocalItem() {
    }

    public LocalItem(LocalItem other) {
        super(other);
        setRefID(other.getRefID());
    }

    public LocalItem(String id, String parentID, String title, String creator, boolean restricted, WriteStatus writeStatus, Class clazz, List<Res> resources, List<Property> properties, List<DescMeta> descMetadata) {
        super(id, parentID, title, creator, restricted, writeStatus, clazz, resources, properties, descMetadata);
    }

    public LocalItem(String id, String parentID, String title, String creator, boolean restricted, WriteStatus writeStatus, Class clazz, List<Res> resources, List<Property> properties, List<DescMeta> descMetadata, String refID) {
        super(id, parentID, title, creator, restricted, writeStatus, clazz, resources, properties, descMetadata);
        this.refID = refID;
    }

    public LocalItem(String id, Container parent, String title, String creator, DIDLObject.Class clazz) {
        this(id, parent.getId(), title, creator, false, null, clazz, new ArrayList(), new ArrayList(), new ArrayList());
    }

    public LocalItem(String id, Container parent, String title, String creator, DIDLObject.Class clazz, String refID) {
        this(id, parent.getId(), title, creator, false, null, clazz, new ArrayList(), new ArrayList(), new ArrayList(), refID);
    }

    public LocalItem(String id, String parentID, String title, String creator, DIDLObject.Class clazz) {
        this(id, parentID, title, creator, false, null, clazz, new ArrayList(), new ArrayList(), new ArrayList());
    }

    public LocalItem(String id, String parentID, String title, String creator, DIDLObject.Class clazz, String refID) {
        this(id, parentID, title, creator, false, null, clazz, new ArrayList(), new ArrayList(), new ArrayList(), refID);
    }

    public String getRefID() {
        return refID;
    }

    public void setRefID(String refID) {
        this.refID = refID;
    }
}
