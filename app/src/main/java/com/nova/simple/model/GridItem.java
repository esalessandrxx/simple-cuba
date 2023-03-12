package com.nova.simple.model;

public class GridItem implements Items {

    private String title, subtitle;
    private int icon;

    public GridItem(String title, String subtitle, int icon) {
        this.title = title;
        this.subtitle = subtitle;
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public int getIcon() {
        return icon;
    }

    @Override
    public int getViewType() {
        return GridItem.VIEW_GRID;
    }
}
