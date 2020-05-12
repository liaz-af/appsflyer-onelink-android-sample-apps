package com.appsflyer.onelink.appsflyeronelinkbasicapp;

import android.app.Application;

import com.appsflyer.AppsFlyerLib;

public class AppsflyerBasicApp extends Application {
    public static final String LOG_TAG = "AppsFlyerBasicApp";
    @Override
    public void onCreate() {
        super.onCreate();
        //noinspection SpellCheckingInspection
        // TODO - Feel you afDevKey - https://support.appsflyer.com/hc/en-us/articles/207032126-Android-SDK-integration-for-developers#integration-31-retrieving-your-dev-key
        String afDevKey = "XXXXXXXXXXXXXXX";
        AppsFlyerLib appsflyer = AppsFlyerLib.getInstance();
        appsflyer.setMinTimeBetweenSessions(0);
        appsflyer.init(afDevKey, null, this);
        appsflyer.startTracking(this, afDevKey);
        appsflyer.setDebugLog(true);
    }
}