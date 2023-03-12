package com.nova.simple.model;

import com.nova.simple.model.HeaderItem;
import com.nova.simple.model.Items;

public class HeaderItem implements Items {

    private String title;

    public HeaderItem(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public int getViewType() {
        return HeaderItem.VIEW_HEADER;
    }
}
