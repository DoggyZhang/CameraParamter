package com.cameraparamter.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.cameraparamter.configuration.PreferenceConfiguration;


/**
 * Created by Administrator on 2016/11/29.
 */

public class PreferenceUtils {

    public static String get(Context context, String key, String defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PreferenceConfiguration.CAMERA_PREFERENCE, Context.MODE_PRIVATE);
        String value = sharedPreferences.getString(key, defaultValue);
        return value;
    }

    public static void put(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PreferenceConfiguration.CAMERA_PREFERENCE, Context.MODE_PRIVATE);
        sharedPreferences
                .edit()
                .putString(key, value)
                .apply();
    }
}
