package services;

import java.util.ResourceBundle;
import java.util.Locale;

import model.LanguagePreference;
import model.SessionData;
import model.UIMessages;

public class LanguageService {
    private static final String BUNDLE_NAME = "messages";

    private static ResourceBundle getBundle() {
        Locale locale = SessionData.getInstance().getLanguage();
        return ResourceBundle.getBundle(BUNDLE_NAME, locale);
    }

    public static String translate(UIMessages msg) {
        return getBundle().getString(msg.getKey());
    }

    public static String translate(String key) {
        return getBundle().getString(key);
    }

    public static void updateLanguage(LanguagePreference pref) {
        SessionData.getInstance().setLanguage(Locale.of(pref.getCode()));
    }
}