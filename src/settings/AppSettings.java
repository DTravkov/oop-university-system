package settings;

import java.util.List;
import java.util.Locale;

import model.domain.*;
import model.enumeration.LanguagePreference;

public class AppSettings {

    public static final LanguagePreference DEFAULT_LANGUAGE = LanguagePreference.RU;

    // used as a placeholder for unauthourized account OR while testing our application layer.
    public static final int ANONYMOUS_USER_ID = -1;
    public static final User ANONYMOUS_USER = new AnonymousUser();

    // used as a placeholder in case user is deleted, but his data must be saved (examlpe : messages/news).
    public static final int DELETED_USER_ID = -2;
    public static final User DELETED_USER = new DeletedUser();

    public static final User DEFAULT_ADMIN = new Admin("admin", "admin", "Admin", "Superuserovich");


    
    public static final List<User> DEFAULT_SYSTEM_USERS = List.of(
        ANONYMOUS_USER,
        DELETED_USER,
        DEFAULT_ADMIN
    );


    public static final int RECENT_LOG_HOURS = 12;

    public static final String DEFAULT_DATA_DIRECTORY = "data/";


    /** User subclasses that cannot be registered as normal accounts via the UI. */
    public static final List<Class<? extends User>> SYSTEM_CLASSES = List.of(
        AnonymousUser.class,
        DeletedUser.class,
        Employee.class,
        User.class
    );

    public static final List<Class<? extends User>> ALL_USER_CLASSES = List.of(
        AnonymousUser.class,
        DeletedUser.class,
        User.class,
        Student.class,
        GraduateStudent.class,
        Employee.class,
        Teacher.class,
        Dean.class,
        Manager.class,
        TechSupportSpecialist.class
    );



    public static final List<Class<? extends User>> REGISTRABLE_USER_CLASSES = ALL_USER_CLASSES.stream()
                                                                                               .filter(cls -> !SYSTEM_CLASSES.contains(cls))
                                                                                               .toList();


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
        if(user == null) user = ANONYMOUS_USER;
        SessionData.getInstance().setUser(user);
    }

    public static User getActiveUser(){
        User activeUser = SessionData.getInstance().getUser();
        return  activeUser == null ? ANONYMOUS_USER : activeUser;
    }

    public static void clearActiveUser(){
        SessionData.getInstance().setUser(ANONYMOUS_USER);
    }

}