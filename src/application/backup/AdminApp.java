package application;

import java.util.Date;
import java.util.List;

import exceptions.ApplicationException;
import exceptions.OperationNotAllowed;
import model.domain.IEnrollable;
import model.domain.Teacher;
import model.domain.User;
import model.enumeration.TeacherType;
import model.enumeration.UIMessage;
import services.UserService;
import settings.AppSettings;
import settings.SessionData;
import utils.LogEntry;
import utils.Logger;
import utils.Translator;
import utils.UIForms;

public final class AdminApp extends BaseApp {

    private static final UserService userService = services.userService;

    private AdminApp() {
    }

    public static void startApp() {
        while (true) {
            printMenu();
            String choice = readChoice(UIMessage.MENU_CHOOSE, 1, 6);

            try {
                switch (choice) {
                    case "1":
                        printAllLogs();
                        break;
                    case "2":
                        printLogsForUser();
                        break;
                    case "3":
                        printSessionUserLogs();
                        break;
                    case "4":
                        printLastLogs();
                        break;
                    case "5":
                        manageUsers();
                        break;
                    case "6":
                        return;
                    default:
                        printInvalidChoice();
                }
            } catch (ApplicationException e) {
                printExceptionDetails(e);
            }
        }
    }

    private static void printMenu() {
        println("\n|||  " + Translator.translate(UIMessage.MENU_TITLE_ADMIN) + " |||");
        println("1. " + Translator.translate(UIMessage.ADMIN_LOGS_ALL));
        println("2. " + Translator.translate(UIMessage.ADMIN_LOGS_BY_USER));
        println("3. " + Translator.translate(UIMessage.ADMIN_LOGS_SESSION));
        println("4. " + "Get latest logs (<= " + AppSettings.RECENT_LOG_HOURS + "h)");
        println("5. Manage users");
        println("6. " + Translator.translate(UIMessage.MENU_EXIT));
    }

    private static void printAllLogs() {
        List<LogEntry> logList = Logger.getAllLogs();
        if (logList.isEmpty()) {
            println(Translator.translate(UIMessage.ADMIN_EMPTY_LOGS));
            return;
        }
        logList.forEach(log -> println(log));
    }

    private static void printLogsForUser() {
        int userId = UIForms.readInt(scanner, UIMessage.INPUT_USER_ID);
        List<LogEntry> logs = Logger.getUserLogs(userId);
        if (logs.isEmpty()) {
            println(Translator.translate(UIMessage.ADMIN_EMPTY_LOGS));
            return;
        }
        logs.forEach(log -> println(log));
    }

    private static void printSessionUserLogs() {
        User user = SessionData.getInstance().getUser();
        List<LogEntry> logs = Logger.getUserLogs(user.getId());
        if (logs.isEmpty()) {
            println(Translator.translate(UIMessage.ADMIN_EMPTY_LOGS));
            return;
        }
        logs.forEach(log -> println(log));
    }

    private static void printLastLogs() {
        List<LogEntry> logs = Logger.getRecentLogs();
        if (logs.isEmpty()) {
            println(Translator.translate(UIMessage.ADMIN_EMPTY_LOGS));
            return;
        }
        logs.forEach(log -> println(log));
    }

    private static void manageUsers() {
        while (true) {
            printUserManagementMenu();
            String choice = readChoice(UIMessage.MENU_CHOOSE, 1, 6);
            try {
                switch (choice) {
                    case "1":
                        printAllUsers();
                        break;
                    case "2":
                        printUserById();
                        break;
                    case "3":
                        registerUser();
                        break;
                    case "4":
                        setBanState();
                        break;
                    case "5":
                        deleteUser();
                        break;
                    case "6":
                        return;
                    default:
                        printInvalidChoice();
                }
            } catch (ApplicationException e) {
                printExceptionDetails(e);
            }
        }
    }

    private static void printUserManagementMenu() {
        println("\n|||  User Management |||");
        println("1. " + Translator.translate(UIMessage.MENU_VIEW_ALL));
        println("2. Get user by id");
        println("3. " + Translator.translate(UIMessage.AUTH_SIGN_UP));
        println("4. Ban / unban user");
        println("5. " + Translator.translate(UIMessage.USER_DELETE));
        println("6. " + Translator.translate(UIMessage.MENU_EXIT));
    }

    private static void printAllUsers() {
        for (User user : userService.getAll()) {
            println(userService.getDTO(user).toShortString());
        }
    }

    private static void printUserById() {
        int id = UIForms.readInt(scanner, UIMessage.INPUT_USER_ID);
        println(userService.getDTO(id));
    }

    private static void registerUser() {
        Class<? extends User> inputClass = UIForms.readUserClass(scanner);
        String login = UIForms.readNonEmpty(scanner, UIMessage.INPUT_LOGIN);
        String password = UIForms.readNonEmpty(scanner, UIMessage.INPUT_PASSWORD);
        String name = UIForms.readNonEmpty(scanner, UIMessage.INPUT_NAME);
        String surname = UIForms.readNonEmpty(scanner, UIMessage.INPUT_SURNAME);

        Date admissionDate = null;
        TeacherType teacherType = null;

        if (IEnrollable.class.isAssignableFrom(inputClass)) {
            admissionDate = new Date();
        } else if (Teacher.class.isAssignableFrom(inputClass)) {
            teacherType = UIForms.askTeacherType(scanner);
        }

        User createdUser = userService.registerUser(inputClass, login, password, name, surname, admissionDate, teacherType);
        printSuccess(Translator.translate(UIMessage.MSG_CREATED));
        println(userService.getDTO(createdUser));
    }

    private static void setBanState() {
        printAllUsers();
        int userId = UIForms.readInt(scanner, UIMessage.INPUT_USER_ID);
        User user = userService.get(userId);
        boolean shouldBan = UIForms.readYesNo(scanner, UIMessage.INPUT_BAN);
        user.setBanned(shouldBan);
        userService.update(user);
        printSuccess("User " + user.getId() + (shouldBan ? " banned" : " unbanned"));
    }

    private static void deleteUser() {
        printAllUsers();
        int userId = UIForms.readInt(scanner, UIMessage.INPUT_USER_ID);
        if (userId <= 0) {
            throw new OperationNotAllowed("System users can not be deleted");
        }
        userService.delete(userId);
        printSuccess(Translator.translate(UIMessage.MSG_DELETED));
    }
}
