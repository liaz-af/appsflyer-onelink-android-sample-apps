package com.appsflyer.onelink.appsflyeronelinkbasicapp;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;
import com.appsflyer.deeplink.DeepLink;
import com.appsflyer.deeplink.DeepLinkListener;
import com.appsflyer.deeplink.DeepLinkResult;
import com.appsflyer.onelink.appsflyeronelinkbasicapp.AppsFlyerConstants;
import com.google.gson.Gson;

import org.json.JSONException;

import java.util.HashMap;
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
        appsflyer.init(afDevKey, null, this);
        appsflyer.start(this, afDevKey);
        appsflyer.setDebugLog(true);

//        appsflyer.registerConversionListener(this, new AppsFlyerConversionListener() {
//            @Override
//            public void onConversionDataSuccess(Map<String, Object> conversionData) {
//                for (String attrName : conversionData.keySet())
//                    Log.d(LOG_TAG, "Conversion attribute: " + attrName + " = " + conversionData.get(attrName));
//                String status = Objects.requireNonNull(conversionData.get("af_status")).toString();
//                if(status.equals("Non-organic")){
//                    if( Objects.requireNonNull(conversionData.get("is_first_launch")).toString().equals("true")){
//                        Log.d(LOG_TAG,"Conversion: First Launch");
//                    } else {
//                        Log.d(LOG_TAG,"Conversion: Not First Launch");
//                    }
//                } else {
//                    Log.d(LOG_TAG,"Conversion: This is an organic install.");
//                }
//            }
//
//            @Override
//            public void onConversionDataFail(String errorMessage) {
//                Log.d(LOG_TAG, "error getting conversion data: " + errorMessage);
//            }
//
//            @Override
//            public void onAppOpenAttribution(Map<String, String> attributionData) {
//                if (!attributionData.containsKey("is_first_launch"))
//                    Log.d(LOG_TAG, "onAppOpenAttribution: This is NOT deferred deep linking");
//                for (String attrName : attributionData.keySet()) {
//                    String deepLinkAttrStr = attrName + " = " + attributionData.get(attrName);
//                    Log.d(LOG_TAG, "Deeplink attribute: " + deepLinkAttrStr);
//                }
////                Log.d(LOG_TAG, "onAppOpenAttribution: Deep linking into " + attributionData.get("fruit_name"));
////                goToFruit(attributionData.get("fruit_name"), attributionData);
//            }
//
//            @Override
//            public void onAttributionFailure(String errorMessage) {
//                Log.d(LOG_TAG, "error onAttributionFailure : " + errorMessage);
//            }
//        });

        appsflyer.subscribeForDeepLink(new DeepLinkListener(){
            @Override
            public void onDeepLinking(@NonNull DeepLinkResult deepLinkResult) {
                DeepLinkResult.Error dlError = deepLinkResult.getError();
                DeepLink deepLinkObj = deepLinkResult.getDeepLink();
                try {
                    Log.d(LOG_TAG, "The DeepLink data is: " + deepLinkObj.toString());
                } catch (Exception e) {
                    Log.d(LOG_TAG, "DeepLink data came back null");
                    return;
                }
                // TODO - Add example for is_deferred
                // TODO - Add example for generic getter
                String fruitName = deepLinkObj.getAfSub5();
                goToFruit(fruitName, deepLinkObj);
            }
        });
    }

    private void goToFruit(String fruitName, DeepLink dlData) {
        String fruitClassName = fruitName.concat("Activity");
        try {
            Class fruitClass = Class.forName(this.getPackageName().concat(".").concat(fruitClassName));
            Log.d(LOG_TAG, "Looking for class " + fruitClass);
            Intent intent = new Intent(getApplicationContext(), fruitClass);
            if (dlData != null) {
                // TODO - make DeepLink Parcelable
                String objToStr = new Gson().toJson(dlData);
                intent.putExtra(DL_ATTRS, objToStr);
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (ClassNotFoundException e) {
            Log.d(LOG_TAG, "Deep linking failed looking for " + fruitName);
            e.printStackTrace();
        }
    }

}