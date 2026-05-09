package settings;

import java.util.List;
import java.util.Locale;

import model.domain.*;
import model.enumeration.LanguagePreference;

public class AppSettings {


    public static final int DELETED_USER_ID = -1;

    public static final String DEFAULT_DATA_DIRECTORY = "data/";

    public static final LanguagePreference DEFAULT_LANGUAGE = LanguagePreference.RU;

    public static final List<Class<? extends SerializableModel>> DEFAULT_RESEARCHER_CLASSES = List.of(
        GraduateStudent.class,
        Teacher.class
    );

    public static final List<Class<? extends SerializableModel>> ST_ORG_ALLOWED_PRESIDENT_CLASSES = List.of(
        Student.class,
        GraduateStudent.class
    );


    public static void setLanguage(LanguagePreference pref){
        SessionData.getInstance().setLanguage(pref);
    }

    public static Locale getLanguage(){
        return SessionData.getInstance().getLanguage();
    }
    
    public static void setActiveUser(User user){
        SessionData.getInstance().setUser(user);
    }

    public static User getActiveUser(){
        return SessionData.getInstance().getUser();
    }

}