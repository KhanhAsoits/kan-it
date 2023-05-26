package com.example.kan_it.core;

import android.content.Context;
import android.content.SharedPreferences;

public class ShareDataManager {
    public static String AUTH_SHARED_KEY = "auth_token";

    Context mContext;
    private ShareDataManager I;

    public ShareDataManager gI() {
        if (I == null) {
            I = new ShareDataManager();
        }
        return I;
    }

    public void init(Context context) {
        mContext = context;
    }

    public SharedPreferences getRef(String ref, int mode) {
        return mContext.getSharedPreferences(ref, mode);
    }

    public SharedPreferences.Editor getEditor(String ref, int mode) {
        return getRef(ref, mode).edit();
    }


    public String getStringRef(String ref, int mode, String key, String def) {
        return getRef(ref, mode).getString(key, def);
    }

    public boolean getBooleanRef(String ref, int mode, String key, boolean def) {
        return getRef(ref, mode).getBoolean(key, def);
    }

    public int getIntRef(String ref, int mode, String key, int def) {
        return getRef(ref, mode).getInt(key, def);
    }

    public long getLongRef(String ref, int mode, String key, long def) {
        return getRef(ref, mode).getLong(key, def);
    }


    public void setStringRef(String ref, int mode, String key, String data) {
        SharedPreferences.Editor editor = getEditor(ref, mode);
        editor.putString(key, data).commit();
    }

    public void setBooleanRef(String ref, int mode, String key, boolean data) {
        SharedPreferences.Editor editor = getEditor(ref, mode);
        editor.putBoolean(key, data).commit();
    }

    public void setIntRef(String ref, int mode, String key, int data) {
        SharedPreferences.Editor editor = getEditor(ref, mode);
        editor.putInt(key, data).commit();
    }

    public void setLongRef(String ref, int mode, String key, long data) {
        SharedPreferences.Editor editor = getEditor(ref, mode);
        editor.putLong(key, data).commit();
    }
}
