package app.com.knowledge.power.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharePrefData {
    private static final SharePrefData instance = new SharePrefData();
    private SharedPreferences.Editor spEditor;


    public static synchronized SharePrefData getInstance() {
        return instance;
    }

    public void setContext(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
    }

    private SharePrefData() {
    }

    public void setPrefString(Context context, String key, String value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(key, value).apply();
    }

    public void setPrefInt(Context context, String key, int value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putInt(key, value).apply();
    }

    public void setPrefBoolean(Context context, String key, boolean value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putBoolean(key, value).apply();
    }

    public void setPrefLong(Context context, String key, Long value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putLong(key, value).apply();
    }

    public Long getPrefLong(Context context, String key) {
        return PreferenceManager.getDefaultSharedPreferences(context).getLong(key, 0L);
    }

    public String getPrefString(Context context, String key) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(key, "");
    }

    public int getPrefInt(Context context, String key) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(key, 0);
    }

    public boolean getPrefBoolean(Context context, String key) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, false);
    }

    public void deletePrefData(Context context, String key) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().remove(key).apply();
    }

    public boolean containsPrefData(Context context, String key) {
        return PreferenceManager.getDefaultSharedPreferences(context).contains(key);
    }

}