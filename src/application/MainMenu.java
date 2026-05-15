package application;

import model.domain.Admin;
import model.domain.Dean;
import model.domain.Employee;
import model.domain.Manager;
import model.domain.Student;
import model.domain.Teacher;
import model.domain.TechSupportSpecialist;
import model.domain.User;
import model.enumeration.LanguagePreference;
import model.enumeration.UIMessage;
import services.UserService;
import settings.AppSettings;
import utils.Translator;
import utils.UIForms;

public class MainMenu extends BaseApp{

    static final UserService userService = services.userService;


    public static MenuBuilder getMainMenu(){

        User activeUser = getActiveUser();

        MenuBuilder menu = new MenuBuilder("University System v0.0001");
        menu.addAction("My Profile", () -> getProfileMenu().start());
        if(activeUser instanceof Student){
            menu.addAction("My Transcript", () -> StudentMenus.printStudentTranscript());
            menu.addAction("Student Organization Menu", () -> StudentMenus.getStudentOrgMenu().start());
        }
        menu.addAction("Research Menu", () -> CommonMenus.getResearcherMenu().start());
        menu.addAction("News Menu", () -> CommonMenus.getNewsMenu().start());
        menu.addAction("Course Menu", () -> CommonMenus.getCourseMenu().start());

        if(activeUser instanceof Employee){
            menu.addAction("Messenger Menu", () -> EmployeeMenus.getMessengerMenu().start());
            menu.addAction("Technical Request Menu", () -> EmployeeMenus.getTechRequestMenu().start());
        }
        if(activeUser instanceof Teacher
          || activeUser instanceof Dean){
            menu.addAction("Complaint Menu", () -> EmployeeMenus.getComplaintMenu().start());
        }
        if(activeUser instanceof Teacher){
            menu.addAction("Teacher Menu", () -> TeacherMenus.getTeacherMenu().start());
        }
        if(activeUser instanceof Admin){
            menu.addAction("Admin Menu", () -> AdminMenus.getAdminMenu().start());
        }
        if(activeUser instanceof Manager){
            menu.addAction("Manager Menu", () -> ManagerMenus.getManagerMenu().start());
        }
        if(activeUser instanceof TechSupportSpecialist){
            menu.addAction("Technical Specialist Menu", () -> TechSupportMenus.getTechSupportSpecMenu().start());
        }
        menu.addAction("Exit", () -> menu.stop());
        return menu;
    }


    public static void changeLanguage() {
        LanguagePreference choice = UIForms.readLanguagePreference(scanner);
        AppSettings.setLanguage(choice);
        printSuccess(Translator.translate(UIMessage.AUTH_CHANGE_LANG));
    }

    public static MenuBuilder getAuthMenu(){
        MenuBuilder menu = new MenuBuilder("Authentication");
        menu.addAction("Login", () -> {
            login();
            menu.stop();
        });
        menu.addAction("Exit", () -> shutdown());
        return menu;
    }

    public static MenuBuilder getProfileMenu() {
        MenuBuilder menu = new MenuBuilder("My profile");
        menu.addAction("View profile", () -> println("\n" + getActiveUser().asTable()));
        menu.addAction("Back", () -> menu.stop());
        return menu;
    }

    private static void login(){
        String login = UIForms.readNonEmpty(scanner, UIMessage.INPUT_LOGIN);
        String password = UIForms.readNonEmpty(scanner, UIMessage.INPUT_PASSWORD);
        User user = userService.authenticate(login, password);
        printSuccess(Translator.translate(UIMessage.AUTH_WELCOME, user.getName()));
    }
}
