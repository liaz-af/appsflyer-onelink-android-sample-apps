package com.kuku.muku.kukumukubasicapp;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Taras Lozovyi on 20.03.2025
 */

public class PreferencesHelper {

    private static final String PREFS_NAME = "app-preferences";
    private static final String IS_LATD_COLLECTED_KEY = "pref-branch-latd-collected";

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static boolean isBranchLATDCollected(Context context) {
        return getSharedPreferences(context).getBoolean(IS_LATD_COLLECTED_KEY, false);
    }

    public static void setBranchLATDCollected(Context context, boolean isCollected) {
        getSharedPreferences(context).edit().putBoolean(IS_LATD_COLLECTED_KEY, isCollected).apply();
    }
}
