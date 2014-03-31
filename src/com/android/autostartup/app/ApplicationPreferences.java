package com.android.autostartup.app;

import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;

public class ApplicationPreferences implements SharedPreferences {

    private static final String SHARED_PREFERENCE_NAME = "com.android.autostartup.preferences";

    private static final String SERIAL_PATH = "path";
    private static final String SERIAL_BAUNDRATE = "baundrate";

    private final SharedPreferences mSharedPreferences;

    public ApplicationPreferences(Context context) {
        mSharedPreferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME,
                Context.MODE_PRIVATE);
    }

    public String getSerialPath() {
        return get(SERIAL_PATH, "/dev/ttyS5");
    }

    public void setSerialPath(String path) {
        set(SERIAL_PATH, path);
    }

    public int getSerialBaundrate() {
        return get(SERIAL_BAUNDRATE, 9600);
    }

    public void setSerialBaundrate(int baundrate) {
        set(SERIAL_BAUNDRATE, baundrate);
    }

    private String get(String key, String defValue) {
        return getString(key, defValue);
    }

    private boolean set(String key, String value) {
        return edit().putString(key, value).commit();
    }

    private int get(String key, int defValue) {
        return getInt(key, defValue);
    }

    private boolean set(String key, int value) {
        return edit().putInt(key, value).commit();
    }

    public boolean clearAll() {
        return edit().clear().commit();
    }

    @Override
    public boolean contains(String key) {
        return mSharedPreferences.contains(key);
    }

    @Override
    public Editor edit() {
        return mSharedPreferences.edit();
    }

    @Override
    public Map<String, ?> getAll() {
        return mSharedPreferences.getAll();
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        return mSharedPreferences.getBoolean(key, defValue);
    }

    @Override
    public float getFloat(String key, float defValue) {
        return mSharedPreferences.getFloat(key, defValue);
    }

    @Override
    public int getInt(String key, int defValue) {
        return mSharedPreferences.getInt(key, defValue);
    }

    @Override
    public long getLong(String key, long defValue) {
        return mSharedPreferences.getLong(key, defValue);
    }

    @Override
    public String getString(String key, String defValue) {
        return mSharedPreferences.getString(key, defValue);
    }

    @Override
    public Set<String> getStringSet(String arg0, Set<String> arg1) {
        return null;
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        mSharedPreferences.registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(listener);
    }
}
