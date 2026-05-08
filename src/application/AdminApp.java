package application;

import java.util.Date;
import java.util.List;

import exceptions.OperationNotAllowed;
import model.domain.IEnrollable;
import model.domain.Teacher;
import model.domain.User;
import model.enumeration.TeacherType;
import model.enumeration.UIMessage;
import services.UserService;
import settings.AppSettings;
import utils.LogEntry;
import utils.Logger;
import utils.Translator;
import utils.UIForms;

public final class AdminApp extends BaseApp {

    private static final UserService userService = services.userService;

    private AdminApp() {
    }

    public static void startApp() {
        ActionMenu menu = new ActionMenu("Admin Menu");
        menu.addAction("Logs", AdminApp::startLogsMenu);
        menu.addAction("Users", AdminApp::startUsersMenu);
        menu.addAction("Exit", menu::stop);
        menu.start();
    }

    private static void startLogsMenu() {
        ActionMenu menu = new ActionMenu("Logs");
        menu.addAction("Show all logs", () -> handleExceptions(AdminApp::printAllLogs));
        menu.addAction("Show logs by user id", () -> handleExceptions(AdminApp::printLogsForUser));
        menu.addAction("Show recent logs", () -> handleExceptions(AdminApp::printRecentLogs));
        menu.addAction("Exit", menu::stop);
        menu.start();
    }

    private static void startUsersMenu() {
        ActionMenu menu = new ActionMenu("Users");
        menu.addAction("Get all users", () -> handleExceptions(AdminApp::printAllUsers));
        menu.addAction("Get all users by role", () -> handleExceptions(AdminApp::printUsersByRole));
        menu.addAction("Create user", () -> handleExceptions(AdminApp::createUser));
        menu.addAction("Change user password", () -> handleExceptions(AdminApp::changeUserPassword));
        menu.addAction("Delete user", () -> handleExceptions(AdminApp::deleteUser));
        menu.addAction("Ban user", () -> handleExceptions(AdminApp::setBanState));
        menu.addAction("Exit", menu::stop);
        menu.start();
    }

    private static void printAllLogs() {
        List<LogEntry> logs = Logger.getAllLogs();
        if (logs.isEmpty()) {
            println("No logs found.");
            return;
        }
        logs.forEach(BaseApp::println);
    }

    private static void printLogsForUser() {
        int userId = UIForms.readInt(scanner, UIMessage.INPUT_USER_ID);
        List<LogEntry> logs = Logger.getUserLogs(userId);
        if (logs.isEmpty()) {
            println("No logs found.");
            return;
        }
        logs.forEach(BaseApp::println);
    }

    private static void printRecentLogs() {
        List<LogEntry> logs = Logger.getRecentLogs();
        if (logs.isEmpty()) {
            println("No recent logs found.");
            return;
        }
        println("Recent logs for last " + AppSettings.RECENT_LOG_HOURS + " hours:");
        logs.forEach(BaseApp::println);
    }

    private static void printAllUsers() {
        for (User user : userService.getAll()) {
            println(userService.getDTO(user).toShortString());
        }
    }

    private static void printUsersByRole() {
        Class<? extends User> userClass = UIForms.readUserClass(scanner);
        List<User> users = userService.getAllByClassOrSubclass(userClass);
        if (users.isEmpty()) {
            println("No users found for role: " + userClass.getSimpleName());
            return;
        }
        users.forEach(user -> println(userService.getDTO(user).toShortString()));
    }

    private static void createUser() {
        Class<? extends User> userClass = UIForms.readUserClass(scanner);
        String login = UIForms.readNonEmpty(scanner, UIMessage.INPUT_LOGIN);
        String password = UIForms.readNonEmpty(scanner, UIMessage.INPUT_PASSWORD);
        String name = UIForms.readNonEmpty(scanner, UIMessage.INPUT_NAME);
        String surname = UIForms.readNonEmpty(scanner, UIMessage.INPUT_SURNAME);

        Date admissionDate = null;
        TeacherType teacherType = null;

        if (IEnrollable.class.isAssignableFrom(userClass)) {
            admissionDate = new Date();
        } else if (Teacher.class.isAssignableFrom(userClass)) {
            teacherType = UIForms.askTeacherType(scanner);
        }

        User createdUser = userService.registerUser(userClass, login, password, name, surname, admissionDate, teacherType);
        printSuccess("User created.");
        println(userService.getDTO(createdUser));
    }

    private static void deleteUser() {
        printAllUsers();
        int userId = UIForms.readInt(scanner, UIMessage.INPUT_USER_ID);
        if (userId <= 0) {
            throw new OperationNotAllowed("System users cannot be deleted");
        }
        userService.delete(userId);
        printSuccess("User deleted.");
    }

    private static void changeUserPassword() {
        printAllUsers();
        int id = UIForms.readInt(scanner, UIMessage.INPUT_USER_ID);
        String password = UIForms.readNonEmpty(scanner, UIMessage.INPUT_NEW_PASSWORD);
        User user = userService.get(id);
        user.setPassword(password);
        userService.update(user);
        printSuccess(Translator.translate(UIMessage.MSG_CREATED));
    }

    private static void setBanState() {
        printAllUsers();
        int userId = UIForms.readInt(scanner, UIMessage.INPUT_USER_ID);
        User user = userService.get(userId);
        boolean shouldBan = UIForms.readYesNo(scanner, UIMessage.INPUT_BAN);
        user.setBanned(shouldBan);
        userService.update(user);
        printSuccess("User " + user.getId() + (shouldBan ? " banned." : " unbanned."));
    }
}
