package com.rongyun.lib_screen.listener;

public abstract class ItemClickListener implements ICListener {

    @Override
    public abstract void onItemAction(int action, Object object);

    @Override
    public void onItemLongAction(int action, Object object) {
        
    }
}
