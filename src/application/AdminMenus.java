package application;

import java.util.Date;
import java.util.List;

import model.domain.GraduateStudent;
import model.domain.Student;
import model.domain.Teacher;
import model.domain.User;
import model.enumeration.TeacherType;
import model.enumeration.UIMessage;
import model.factories.UserFactory;
import services.UserService;
import settings.AppSettings;
import utils.LogEntry;
import utils.Logger;
import utils.Translator;
import utils.UIForms;

public class AdminMenus extends BaseApp {

    static final UserService userService = services.userService;

    
    static MenuBuilder getAdminMenu() {
        MenuBuilder menu = new MenuBuilder("Admin Menu");
        menu.addAction("Get all logs", () -> printAllLogs());
        menu.addAction("Get recent logs", () -> printRecentLogs());
        menu.addAction("Get logs by user id", () -> printAllLogsByUserId());
        menu.addAction("Get all users", () -> printAllUsersByClass(User.class));
        menu.addAction("Create User", () -> createUser());
        menu.addAction("Delete User", () -> deleteUser());
        menu.addAction("Ban User", () -> banUser());
        menu.addAction("Back", () -> menu.stop());
        return menu;
    }

    private static void banUser() {
        List<User> users = userService.getUsersByClass(User.class);
        if (users.isEmpty()) {
            println("No users found.");
            return;
        }
        printHeader("User");
        users.stream().filter(u -> !u.equals(getActiveUser())).forEach(u -> println(u.asLine()));
        User user = UIForms.readIdFromList(scanner, UIMessage.INPUT_USER_ID, users);
        userService.ban(user);
        printSuccess("User " + user.asLine() + " is banned");
    }

    private static void deleteUser() {
        List<User> users = userService.getUsersByClass(User.class);
        if (users.isEmpty()) {
            println("No users found.");
            return;
        }
        printHeader("User");
        users.forEach(u -> println(u.asLine()));
        User user = UIForms.readIdFromList(scanner, UIMessage.INPUT_USER_ID, users);
        userService.delete(user);
        printSuccess("User " + user.asLine() + " is deleted");
    }

    private static void createUser() {
            Class<? extends User> className = UIForms.readUserClass(scanner);

            String login = UIForms.readNonEmpty(scanner, UIMessage.INPUT_LOGIN);
            String password = UIForms.readNonEmpty(scanner, UIMessage.INPUT_PASSWORD);
            String name = UIForms.readNonEmpty(scanner, UIMessage.INPUT_NAME);
            String surname = UIForms.readNonEmpty(scanner, UIMessage.INPUT_SURNAME);

            Date admissionDate = null;
            TeacherType teacherType = null;

            if (className == Student.class || className == GraduateStudent.class) {
                admissionDate = new Date();
            }
            if (className == Teacher.class) {
                teacherType = UIForms.askTeacherType(scanner);
            }

            User user = UserFactory.createFromClass(className, login, password, name, surname, admissionDate, teacherType);
            User saved = userService.create(user);
            printSuccess(Translator.translate(UIMessage.AUTH_WELCOME, saved.getName()));
    }

    private static <U extends User> void printAllUsersByClass(Class<U> className) {
        List<U> users = userService.getUsersByClass(className);
        if(users == null || users.isEmpty()){
            println("No users found.");
            return;
        }
        printHeader(className.getSimpleName());
        users.forEach(u -> println(u.asLine()));
    }

    private static void printAllLogsByUserId() {
        int userId = UIForms.readInt(scanner, UIMessage.INPUT_USER_ID);
        List<LogEntry> logs = Logger.getUserLogs(userId);
        if (logs.isEmpty()) {
            println("No logs found.");
            return;
        }
        logs.forEach(l -> println(l.asLine()));
    }

    private static void printRecentLogs() {
        List<LogEntry> logs = Logger.getRecentLogs();
        if (logs.isEmpty()) {
            println("No recent logs found.");
            return;
        }
        println("Recent logs for last " + AppSettings.RECENT_LOG_HOURS + " hours:");
        logs.forEach(l -> println(l.asLine()));
    }

    private static void printAllLogs() {
        List<LogEntry> logs = Logger.getAllLogs();
        if (logs.isEmpty()) {
            println("No logs found.");
            return;
        }
        logs.forEach(l -> println(l.asLine()));
    }
}
