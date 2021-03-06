package com.gat.common.util;

import android.util.Log;

/**
 * Created by mozaa on 30/03/2017.
 */

public class MZDebug {

    public static boolean DEBUG = true;
    private static String TAG = "GATDebug";

    // verbose
    public static void v(String msg) {
        if (DEBUG)
            Log.v(TAG, msg);
    }
    public static void v(String tag, String msg) {
        if (DEBUG)
            Log.v(tag, msg);
    }
    // debug
    public static void d(String msg) {
        if (DEBUG)
            Log.d(TAG, msg);
    }
    public static void d(String tag, String msg) {
        if (DEBUG)
            Log.d(tag, msg);
    }
    // info
    public static void i(String msg) {
        if (DEBUG)
            Log.i(TAG, msg);
    }
    public static void i(String tag, String msg) {
        if (DEBUG)
            Log.i(tag, msg);
    }
    // warn
    public static void w(String msg) {
        if (DEBUG)
            Log.w(TAG, msg);
    }
    public static void w(String tag, String msg) {
        if (DEBUG)
            Log.w(tag, msg);
    }
    // error
    public static void e(String msg) {
        if (DEBUG)
            Log.e(TAG, msg);
    }
    public static void e(String tag, String msg) {
        if (DEBUG)
            Log.e(tag, msg);
    }
}
