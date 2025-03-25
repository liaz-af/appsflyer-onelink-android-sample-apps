package com.kuku.muku.kukumukubasicapp;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.google.gson.Gson;
import org.json.JSONObject;
import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import io.branch.referral.Branch;

import com.appsflyer.AppsFlyerLib;


public class AppsflyerBasicApp extends Application {
    public static final String LOG_TAG = "AppsFlyerOneLinkSimApp";
    public static final String DL_ATTRS = "dl_attrs";
    Map<String, Object> conversionData = null;


    @Override
    public void onCreate() {
        super.onCreate();

        // Branch logging for debugging
        Branch.enableLogging();

        // Branch object initialization
        Branch.getAutoInstance(this);

        String afDevKey = AppsFlyerConstants.afDevKey;
        AppsFlyerLib.getInstance().setDebugLog(true);
        AppsFlyerLib.getInstance().init(afDevKey, null, this);
    }
}
