package com.nova.simple;

import android.app.Application;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        UiTheme.applyTheme(this);
    }
}
