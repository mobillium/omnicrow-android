package com.mobillium.omnicrow.utils;

import android.util.Log;

import com.mobillium.omnicrow.OmniCrow;

/**
 * Created by oguzhandongul on 18/10/2016.
 */

public class OmniCrowAnalyticsLogger {
    private static String TAG = "TUBITAK_SDK";
    private static String TAG_PARAMS = "TUBITAK_SDK_PARAMS";

    public OmniCrowAnalyticsLogger() {
    }


    public static void writeErrorLog(String message) {
        if (OmniCrow.getInstance().isDebugEnabled()) {
            Log.e(TAG, message);
        }
    }

    public static void writeInfoLog(String message) {
        if (OmniCrow.getInstance().isDebugEnabled()) {
            Log.i(TAG, message);
        }
    }

    public static void writeResponseLog(String message) {
        if (OmniCrow.getInstance().isDebugEnabled()) {
            Log.i(TAG_PARAMS, message);
        }
    }

    public static void writeDebugLog(String message) {
        if (OmniCrow.getInstance().isDebugEnabled()) {
            Log.d(TAG, message);
        }
    }
}
