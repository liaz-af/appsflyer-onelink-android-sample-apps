package com.appsflyer.onelink.appsflyeronelinkbasicapp;

import android.app.Application;
import android.util.Log;


import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;

import java.util.Map;
import java.util.Objects;

public class AppsflyerBasicApp extends Application {
    public static final String LOG_TAG = "AppsFlyerFeedMeApp";
    public static final String DL_ATTRS = "dl_attrs";
    @Override
    public void onCreate() {
        super.onCreate();
        //noinspection SpellCheckingInspection
        String afDevKey = AppsFlyerConstants.afDevKey;
        AppsFlyerLib appsflyer = AppsFlyerLib.getInstance();
        appsflyer.setMinTimeBetweenSessions(0);
        appsflyer.setDebugLog(true);

        // Put Unified DeepLink Listener here

        // Put the Legacy DeepLink Listener here

        // Do Not forget to replace the init function when you introduce the conversion listener
        appsflyer.init(afDevKey, null, this);
        appsflyer.start(this, afDevKey);
    }


    // Put here the goToFruit function


}