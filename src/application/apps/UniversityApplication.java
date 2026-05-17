package application.apps;

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

public class UniversityApplication extends BaseApp {

    static final UserService userService = services.userService;

    public static void start() {

        User user = getActiveUser();

        MenuBuilder menu = new MenuBuilder("University System v0.0001")
        
        .addAction("My Profile", () -> println(getActiveUser().asTable()))
        .addAction("Research Menu", () -> CommonMenus.getResearcherMenu().start())
        .addAction("News Menu", () -> CommonMenus.getNewsMenu().start())
        .addAction("Course Menu", () -> CommonMenus.getCourseMenu().start());

        if(user instanceof Employee){
            menu.addAction("Messenger Menu", () -> EmployeeApp.getMessengerMenu().start());
            menu.addAction("Technical Request Menu", () -> EmployeeApp.getTechRequestMenu().start());
        }
        if (user instanceof Student) {
            menu.addAction("Student Menu", () -> StudentApp.getMenu().start());
        }
        if (user instanceof Admin) {
            menu.addAction("Admin Menu", () -> AdminApp.getMenu().start());
        }
        if (user instanceof TechSupportSpecialist) {
            menu.addAction("Technical Specialist Menu", () -> TechSupportSpecialistApp.getMenu().start());
        }
        if (user instanceof Manager) {
            menu.addAction("Manager Menu", () -> ManagerApp.getMenu().start());
        }
        if (user instanceof Dean) {
            menu.addAction("Complaint Menu", () -> DeanApp.getMenu().start());
        }
        if (user instanceof Teacher) {
            menu.addAction("Teacher Menu", () -> TeacherApp.getMenu().start());
        }

        menu.addExit();

        menu.start();
    }



    public static void authenticate() {
        MenuBuilder menu = new MenuBuilder("Authentication", false);
        menu.addAction("Login", () -> {

            String login = UIForms.readNonEmpty(scanner, UIText.INPUT_LOGIN);
            String password = UIForms.readNonEmpty(scanner, UIText.INPUT_PASSWORD);
            User user = userService.authenticate(login, password);
            printSuccess(UIText.AUTH_WELCOME.localized(user.getName()));

            menu.stop();
        });
        menu.addAction("Exit", () -> kill());
        menu.start();
    }

    public static void changeLanguage() {
        LanguagePreference choice = UIForms.readLanguagePreference(scanner);
        AppSettings.setLanguage(choice);
        printSuccess(UIText.AUTH_CHANGE_LANG.localized());
    }
}
