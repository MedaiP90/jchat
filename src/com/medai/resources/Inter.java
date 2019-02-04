package com.medai.resources;

import java.util.Locale;
import java.util.ResourceBundle;

public class Inter {

    public static String getLocale(String key) {
        Locale locale = Locale.getDefault();
        // System.out.println("Default locale: " + locale.toString());
        return ResourceBundle.getBundle("com/medai/resources/locale", locale).getString(key);
    }

}
