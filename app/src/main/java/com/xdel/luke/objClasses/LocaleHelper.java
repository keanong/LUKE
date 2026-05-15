package com.xdel.luke.objClasses;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Locale;

public class LocaleHelper {
    public static final String ENGLISH = "en";
    public static final String CHINESE = "zh";

    public static void initialize(Context context, @LocaleDef String defaultLanguage) {
        setLocale(context, defaultLanguage);
    }

    public static boolean setLocale(Context context, @LocaleDef String language) {
        return updateResources(context, language);
    }

    private static boolean updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        context.createConfigurationContext(configuration);
        configuration.locale = locale;
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        return true;
    }

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({ENGLISH, CHINESE})
    public @interface LocaleDef {
        String[] SUPPORTED_LOCALES = {ENGLISH, CHINESE};
    }

    public static void setSelectedLanguageId(String id){
        final SharedPreferences prefs = objGlobalMethods.getGcmPreferences();
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("app_language_id", id);
        editor.apply();
    }

    public static String getSelectedLanguageId(){
        return objGlobalMethods.getGcmPreferences().getString("language", "en");
    }
}
