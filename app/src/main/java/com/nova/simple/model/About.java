package com.nova.simple.model;

public class About {

    private String name;
    private String description;

    public About(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }
    public String getDescription(){
        return description;
    }
}
