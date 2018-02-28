package com.tld.company;

import android.app.Application;

public class MainApplication extends Application {
    private static MainApplication mainApplication;

    public static MainApplication getInstance() {
        return mainApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mainApplication = this;
    }


}
