package settings;


import model.domain.User;
import model.enumeration.LanguagePreference;

import java.util.Locale;

public class SessionData {

    private static SessionData instance = new SessionData();
    private Locale language = Locale.of(LanguagePreference.RU.getCode());
    private User user = null;

    private SessionData() {}

    public static SessionData getInstance() {
        if (instance == null) instance = new SessionData();
        return instance;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setLanguage(LanguagePreference languagePreference) {
        this.language = Locale.of(languagePreference.getCode());
    }

    public Locale getLanguage() {
        return language;
    }
}