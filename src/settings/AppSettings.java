package settings;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Locale;

import model.domain.*;
import model.enumeration.LanguagePreference;

public class AppSettings {

    public static final LanguagePreference DEFAULT_LANGUAGE = LanguagePreference.RU;
    
    // default users
    public static final int ANONYMOUS_USER_ID = -1;
    public static final User ANONYMOUS_USER = new AnonymousUser();

    public static final User DEFAULT_ADMIN = new Admin("admin", "admin", "Admin", "Superuserovich");

    public static final List<User> DEFAULT_SYSTEM_USERS = List.of(
        ANONYMOUS_USER,
        DEFAULT_ADMIN
    );


    // class lists, for different purposes
    public static final List<Class<? extends User>> ALL_USER_CLASSES = List.of(
        AnonymousUser.class,
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
                                                                                               .filter(cls -> !Modifier.isAbstract(cls.getModifiers()))
                                                                                               .filter(cls -> cls != AnonymousUser.class)
                                                                                               .toList();


    public static final List<Class<? extends SerializableModel>> DEFAULT_RESEARCHER_CLASSES = List.of(
        GraduateStudent.class,
        Teacher.class
    );

    public static final List<Class<? extends SerializableModel>> ST_ORG_ALLOWED_PRESIDENT_CLASSES = List.of(
        Student.class,
        GraduateStudent.class
    );

    // general defautls
    public static final String DEFAULT_DATA_DIRECTORY = "data/";
    public static final int RECENT_LOG_HOURS = 12;
    public static final Class<?> NOTIFICATION_BROADCAST_CLASS = User.class;

    //global validation rules
    public static boolean REQUIRE_PASSWORD_VALIDATION = false;


    //helpers and etc.
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