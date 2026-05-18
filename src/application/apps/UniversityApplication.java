package application.apps;

import exceptions.UserBannedOrDeleted;
import model.domain.Admin;
import model.domain.Dean;
import model.domain.Employee;
import model.domain.Manager;
import model.domain.Student;
import model.domain.Teacher;
import model.domain.TechSupportSpecialist;
import model.domain.User;
import model.enumeration.LanguagePreference;
import services.UserService;
import settings.AppSettings;
import utils.UIForms;
import utils.UIText;

/**
 * Main menu for our application, each user will see this(or part of it)
 * It adds custom menus for every user, 
 * e.g Teacher will additionally have teacher-specific menus
 */
public class UniversityApplication extends BaseApp {

    static final UserService userService = services.userService;
    /**
     * main CLI entry point, builds AND starts menu according to user class
     */
    public static void start() {

        User user = getActiveUser();

        MenuBuilder menu = new MenuBuilder(UIText.MENU_TITLE_UNIVERSITY)
        
        .addAction(UIText.MENU_MY_PROFILE, () -> println(user.asTable()))
        .addAction(UIText.MENU_RESEARCH, () -> CommonMenus.getResearcherMenu().start())
        .addAction(UIText.MENU_NEWS, () -> CommonMenus.getNewsMenu().start())
        .addAction(UIText.MENU_COURSES, () -> CommonMenus.getCourseMenu().start());

        if(user instanceof Employee){
            menu.addAction(UIText.MENU_MESSENGER, () -> EmployeeApp.getMessengerMenu().start());
            menu.addAction(UIText.MENU_TECH_REQUEST, () -> EmployeeApp.getTechRequestMenu().start());
        }
        if (user instanceof Student) {
            menu.addAction(UIText.MENU_STUDENT, () -> StudentApp.getMenu().start());
        }
        if (user instanceof Admin) {
            menu.addAction(UIText.MENU_ADMIN, () -> AdminApp.getMenu().start());
        }
        if (user instanceof TechSupportSpecialist) {
            menu.addAction(UIText.MENU_TECH_SPECIALIST, () -> TechSupportSpecialistApp.getMenu().start());
        }
        if (user instanceof Manager) {
            menu.addAction(UIText.MENU_MANAGER, () -> ManagerApp.getMenu().start());
        }
        if (user instanceof Dean) {
            menu.addAction(UIText.MENU_COMPLAINT, () -> DeanApp.getMenu().start());
        }
        if (user instanceof Teacher) {
            menu.addAction(UIText.MENU_TEACHER, () -> TeacherApp.getMenu().start());
        }

        menu.addAction(UIText.MENU_NOTIFICATIONS, () -> CommonMenus.getNotificationMenu().start());

        menu.addExit();

        menu.start();
    }


    /**
     * form that checks user credentials
     */
    public static void authenticate() {
        MenuBuilder menu = new MenuBuilder(UIText.MENU_TITLE_AUTH, false);
        menu.addAction(UIText.MENU_AUTH_LOGIN, () -> {

            String login = UIForms.readNonEmpty(scanner, UIText.INPUT_LOGIN);
            String password = UIForms.readNonEmpty(scanner, UIText.INPUT_PASSWORD);
            User user = userService.authenticate(login, password);
            printSuccess(UIText.AUTH_WELCOME, user.getName());

            menu.stop();
        });
        menu.addAction(UIText.MENU_AUTH_EXIT, () -> kill());
        menu.start();
    }
    /**
     * simple helper form to change language for current session.
     */
    public static void changeLanguage() {
        LanguagePreference choice = UIForms.readLanguagePreference(scanner);
        AppSettings.setLanguage(choice);
        printSuccess(UIText.AUTH_CHANGE_LANG);
    }
}
