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
import utils.Logger;
import utils.UIForms;
import utils.UIText;
import utils.Logger.LogEntry;

public final class AdminApp extends BaseApp {

    static final UserService userService = services.userService;

    private AdminApp() {
    }

    public static MenuBuilder getMenu() {
        return new MenuBuilder(UIText.ADMIN_MENU_TITLE)
                .addAction(UIText.ADMIN_LOGS_ALL, () -> printAllLogs())
                .addAction(UIText.ADMIN_LOGS_RECENT, () -> printRecentLogs())
                .addAction(UIText.ADMIN_LOGS_BY_USER, () -> printAllLogsByUserId())
                .addAction(UIText.ADMIN_GET_ALL_USERS, () -> printAllUsersByClass(User.class))
                .addAction(UIText.ADMIN_CREATE_USER, () -> createUser())
                .addAction(UIText.ADMIN_DELETE_USER, () -> deleteUser())
                .addAction(UIText.ADMIN_BAN_USER, () -> banUser())
                .addAction(UIText.ADMIN_UNBAN_USER, () -> unbanUser())
                .addExit();
    }

    private static void banUser() {
        Admin admin = (Admin) getActiveUser();
        List<User> users = userService.getUsersByClass(User.class);
        if (users.isEmpty()) {
            println(UIText.ADMIN_NO_USERS);
            return;
        }
        printHeader(UIText.ADMIN_HEADER_USER);
        users.stream().filter(u -> !u.equals(getActiveUser())).forEach(u -> println(u.asLine()));
        User user = UIForms.readIdFromList(scanner, UIText.INPUT_USER_ID, users);
        userService.ban(user, admin);
        printSuccess(UIText.ADMIN_USER_BANNED, user.asLine());
    }

    private static void unbanUser() {
        Admin admin = (Admin) getActiveUser();
        List<User> bannedUsers = userService.getAll(u -> u.isBanned());
        if (bannedUsers.isEmpty()) {
            println(UIText.ADMIN_NO_USERS);
            return;
        }
        printHeader(UIText.ADMIN_HEADER_USER);
        bannedUsers.forEach(u -> println(u.asLine()));
        User user = UIForms.readIdFromList(scanner, UIText.INPUT_USER_ID, bannedUsers);
        userService.unban(user);
        printSuccess(UIText.ADMIN_USER_UNBANNED, user.asLine());
    }

    private static void deleteUser() {
        List<User> users = userService.getUsersByClass(User.class);
        if (users.isEmpty()) {
            println(UIText.ADMIN_NO_USERS);
            return;
        }
        printHeader(UIText.ADMIN_HEADER_USER);
        users.forEach(u -> println(u.asLine()));
        User user = UIForms.readIdFromList(scanner, UIText.INPUT_USER_ID, users);
        userService.delete(user);
        printSuccess(UIText.ADMIN_USER_DELETED, user.asLine());
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
        printSuccess(UIText.MSG_CREATED);
    }

    private static <U extends User> void printAllUsersByClass(Class<U> className) {
        List<U> users = userService.getUsersByClass(className);
        if (users == null || users.isEmpty()) {
            println(UIText.ADMIN_NO_USERS);
            return;
        }
        printHeader(className.getSimpleName());
        users.forEach(u -> println(u.asLine()));
    }

    private static void printAllLogsByUserId() {
        int userId = UIForms.readInt(scanner, UIText.INPUT_USER_ID);
        List<LogEntry> logs = Logger.getUserLogs(userId);
        if (logs.isEmpty()) {
            println(UIText.ADMIN_NO_LOGS);
            return;
        }
        logs.forEach(l -> println(l.asLine()));
    }

    private static void printRecentLogs() {
        List<LogEntry> logs = Logger.getRecentLogs();
        if (logs.isEmpty()) {
            println(UIText.ADMIN_NO_LOGS);
            return;
        }
        println(UIText.ADMIN_RECENT_LOGS_PREFIX, AppSettings.RECENT_LOG_HOURS);
        logs.forEach(l -> println(l.asLine()));
    }

    private static void printAllLogs() {
        List<LogEntry> logs = Logger.getAllLogs();
        if (logs.isEmpty()) {
            println(UIText.ADMIN_NO_LOGS);
            return;
        }
        logs.forEach(l -> println(l.asLine()));
    }
}
