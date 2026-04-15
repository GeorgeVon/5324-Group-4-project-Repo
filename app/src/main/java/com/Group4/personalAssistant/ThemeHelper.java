package com.Group4.personalAssistant;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class ThemeHelper {
    private static final String PREF_NAME = "theme_prefs";
    private static final String KEY_THEME = "current_theme";

    public static void applyTheme(Activity activity) {
        SharedPreferences prefs = activity.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        int themeId = prefs.getInt(KEY_THEME, R.style.Theme_Group4);
        activity.setTheme(themeId);
    }

    public static void setTheme(Activity activity, int themeId) {
        SharedPreferences.Editor editor = activity.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putInt(KEY_THEME, themeId);
        editor.apply();
        activity.recreate();
    }
}