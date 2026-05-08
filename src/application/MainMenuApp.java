package application;

import model.domain.Admin;
import model.domain.Employee;
import model.domain.Manager;
import model.domain.Student;
import model.domain.Teacher;
import model.enumeration.UIMessage;
import services.UserService;
import utils.UIForms;

public final class MainMenuApp extends BaseApp {

    private static final UserService userService = services.userService;

    private MainMenuApp() {
    }

    public static void main() {
        startAuthMenu();
    }

    public static void startAuthMenu(){
        ActionMenu menu = new ActionMenu("Login");
        menu.addAction("Login", () -> handleExceptions(() -> authenticate()));
        menu.addAction("Exit", () -> System.exit(0));
        menu.start();
    }

    public static void startMainMenu(){
        ActionMenu menu = new ActionMenu("University System v0.01");
        menu.addUserLabel(userService.getDTO(getActiveUser()));
        menu.addAction("My Profile", () -> authenticate());
        menu.addAction("Research Menu", null);
        menu.addAction("News", NewsApp::startApp);
        if(getActiveUser() instanceof Employee){
            menu.addAction("Messenger", MessageApp::startApp);
            menu.addAction("Technical Requests Menu", TechRequestApp::startApp);
        }
        if(getActiveUser() instanceof Student){
            menu.addAction("Student Menu", StudentApp::startApp);
        }
        if(getActiveUser() instanceof Manager){
            menu.addAction("Manager Menu", ManagerApp::startApp);
        }
        if(getActiveUser() instanceof Admin){
            menu.addAction("Admin Menu", AdminApp::startApp);
        }
        if(getActiveUser() instanceof Teacher){
            menu.addAction("Teacher Menu", TeacherApp::startApp);
        }

        menu.addAction("Log out", menu::stop);


        menu.start();
    }


    private static void authenticate() {
        while (true){
            String login = UIForms.readNonEmpty(scanner, UIMessage.INPUT_LOGIN);
            String password = UIForms.readNonEmpty(scanner, UIMessage.INPUT_PASSWORD);
            userService.authenticate(login, password);
            printSuccess("Logged in successfully.");
            startMainMenu();
            break;
        }
    }

}
