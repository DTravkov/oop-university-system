package utils;

import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.Locale;

import model.enumeration.UIMessage;
import settings.SessionData;

public class Translator {
    private static final String BUNDLE_NAME = "messages";

    private static ResourceBundle getBundle() {
        Locale locale = SessionData.getInstance().getLanguage();
        return ResourceBundle.getBundle(BUNDLE_NAME, locale);
    }

    public static String translate(UIMessage msg, Object... args) { return translate(msg.getKey(), args); }

    public static String translate(String key, Object... args) {
        String pattern = getBundle().getString(key);

        if(args == null || args.length == 0)
            return pattern;
        
        return MessageFormat.format(pattern, args);
    }

}
