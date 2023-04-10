package com.appsflyer.onelink.appsflyeronelinkbasicapp;

import com.appsflyer.AppsFlyerLib;


import android.app.Application;
import android.content.Intent;
import android.util.Log;
import com.google.gson.Gson;
import org.json.JSONObject;
import androidx.annotation.NonNull;

import java.util.Map;
import java.util.Objects;

public class AppsflyerBasicApp extends Application {
    public static final String LOG_TAG = "AppsFlyerOneLinkSimApp";
    public static final String DL_ATTRS = "dl_attrs";
    Map<String, Object> conversionData = null;

    public void onCreate() {
        super.onCreate();

        AppsFlyerLib appsflyer = AppsFlyerLib.getInstance();
        // For debug - remove in production
        appsflyer.setDebugLog(true);

        appsflyer.init("sQ84wpdxRTR4RMCaE9YqS4", null, this);
        appsflyer.start(this);
    }
}
