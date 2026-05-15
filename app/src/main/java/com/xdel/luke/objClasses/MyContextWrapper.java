package com.xdel.luke.objClasses;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;

import java.util.Locale;

public class MyContextWrapper extends ContextWrapper{

    public MyContextWrapper(Context base) {
        super(base);
    }

    @SuppressWarnings("deprecation")
    public static ContextWrapper wrap(Context ctx){
        SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
        String langType = objGlobalMethods.getGCMPrefs(GcmPreferences, "language");
        String language = langType.equals("") ? "en" : langType;
/////////////////////////////////////////////
        Locale sysLocale;
        Resources rs = ctx.getResources();
        Configuration config = rs.getConfiguration();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            sysLocale = config.getLocales().get(0);
        } else {
            sysLocale = config.locale;
        }
        if (!language.equals("") && !sysLocale.getLanguage().equals(language)) {
            Locale locale = new Locale(language);
            Locale.setDefault(locale);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                locale = Locale.CHINA;
                config.setLocale(locale);
            } else {
                //locale = Locale.UK;
                config.locale = locale;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                ctx = ctx.createConfigurationContext(config);
            } else {
                ctx.getResources().updateConfiguration(config, ctx.getResources().getDisplayMetrics());
            }
        }

        return new ContextWrapper(ctx);
    }

    @SuppressWarnings("deprecation")
    public static Locale getSystemLocaleLegacy(Configuration config){
        return config.locale;
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static Locale getSystemLocale(Configuration config){
        return config.getLocales().get(0);
    }

    @SuppressWarnings("deprecation")
    public static void setSystemLocaleLegacy(Configuration config, Locale locale){
        config.locale = locale;
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static void setSystemLocale(Configuration config, Locale locale){
        config.setLocale(locale);
    }

}
