package application.apps;

import java.util.Date;
import java.util.List;

import model.domain.Admin;
import model.domain.GraduateStudent;
import model.domain.Student;
import model.domain.Teacher;
import model.domain.User;
import model.factories.UserBuilder;
import services.UserService;
import settings.AppSettings;
import utils.LogEntry;
import utils.Logger;
import utils.UIForms;
import utils.UIText;

public final class AdminApp extends BaseApp {

    static final UserService userService = services.userService;

    private AdminApp() {
    }

    public static MenuBuilder getMenu() {
        return new MenuBuilder("Admin Menu")
                .addAction("Get all logs", () -> printAllLogs())
                .addAction("Get recent logs", () -> printRecentLogs())
                .addAction("Get logs by user id", () -> printAllLogsByUserId())
                .addAction("Get all users", () -> printAllUsersByClass(User.class))
                .addAction("Create User", () -> createUser())
                .addAction("Delete User", () -> deleteUser())
                .addAction("Ban User", () -> banUser())
                .addExit();
    }

    private static void banUser() {
        Admin admin = (Admin) getActiveUser();
        List<User> users = userService.getUsersByClass(User.class);
        if (users.isEmpty()) {
            println("No users found.");
            return;
        }
        printHeader("User");
        users.stream().filter(u -> !u.equals(getActiveUser())).forEach(u -> println(u.asLine()));
        User user = UIForms.readIdFromList(scanner, UIText.INPUT_USER_ID, users);
        userService.ban(user, admin);
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
        User user = UIForms.readIdFromList(scanner, UIText.INPUT_USER_ID, users);
        userService.delete(user);
        printSuccess("User " + user.asLine() + " is deleted");
    }

    private static void createUser() {
        Class<? extends User> className = UIForms.readUserClass(scanner);

        String login = UIForms.readNonEmpty(scanner, UIText.INPUT_LOGIN);
        String password = UIForms.readNonEmpty(scanner, UIText.INPUT_PASSWORD);
        String name = UIForms.readNonEmpty(scanner, UIText.INPUT_NAME);
        String surname = UIForms.readNonEmpty(scanner, UIText.INPUT_SURNAME);

        UserBuilder builder = new UserBuilder()
                .userClass(className)
                .login(login)
                .password(password)
                .name(name)
                .surname(surname);

        if (className == Student.class || className == GraduateStudent.class) {
            builder.admissionDate(new Date());
        }
        if (className == Teacher.class) {
            builder.teacherType(UIForms.askTeacherType(scanner));
        }

        User user = builder.build();
        userService.create(user);
        printSuccess(UIText.MSG_CREATED.localized());
    }

    private static <U extends User> void printAllUsersByClass(Class<U> className) {
        List<U> users = userService.getUsersByClass(className);
        if (users == null || users.isEmpty()) {
            println("No users found.");
            return;
        }
        printHeader(className.getSimpleName());
        users.forEach(u -> println(u.asLine()));
    }

    private static void printAllLogsByUserId() {
        int userId = UIForms.readInt(scanner, UIText.INPUT_USER_ID);
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
