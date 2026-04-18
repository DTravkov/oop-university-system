package model.enumeration;

public enum LanguagePreference {
    EN("en"),
    KK("kk"),
    RU("ru");

    private final String code;

    LanguagePreference(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return code;
    }
}