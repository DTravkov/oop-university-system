package settings;

import java.util.List;
import java.util.Locale;

import model.domain.*;
import model.enumeration.LanguagePreference;

public class AppSettings {


    // used as a placeholder for unauthourized account OR while testing our application layer.
    public static final int SYSTEM_USER_ID = -54;
    public static final User SYSTEM_USER = new SystemUser();

    // used as a placeholder in case user is deleted, but his data must be saved (examlpe : messages/news).
    public static final int DELETED_USER_ID = -27;
    public static final User DELETED_USER = new DeletedUser();

    public static final String DEFAULT_REPOSITORY_ROOT = "data/";

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
        if(user == null) user = SYSTEM_USER;
        SessionData.getInstance().setUser(user);
    }

    public static User getActiveUser(){
        User activeUser = SessionData.getInstance().getUser();
        return  activeUser == null ? SYSTEM_USER : activeUser;
    }

    public static void clearActiveUser(){
        SessionData.getInstance().setUser(SYSTEM_USER);
    }

}