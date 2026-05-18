package settings;


import model.domain.User;
import model.enumeration.LanguagePreference;

import java.util.Locale;

/**
 * SessionData is a singleton that stores data about user language and who the user is.
 * data is stored only in memory, each restart erases it.
 */
public class SessionData {

    private static SessionData instance = new SessionData();

    private Locale language = Locale.of(AppSettings.DEFAULT_LANGUAGE.getCode());
    private User user = AppSettings.ANONYMOUS_USER;

    private SessionData() {}

    static SessionData getInstance() {
        if (instance == null) instance = new SessionData();
        return instance;
    }

    void setUser(User user) {
        this.user = user;
    }

    User getUser() {
        return user;
    }

    void setLanguage(LanguagePreference languagePreference) {
        this.language = Locale.of(languagePreference.getCode());
    }

    Locale getLanguage() {
        return language;
    }
}