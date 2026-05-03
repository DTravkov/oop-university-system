package application;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

import exceptions.ApplicationException;
import model.domain.User;
import model.enumeration.UIMessage;
import model.factories.ServiceFactory;
import services.UserService;
import settings.SessionData;
import utils.LogEntry;
import utils.Logger;
import utils.Translator;
import utils.UIForms;

public class AdminApp {

    private static final UserService userService = ServiceFactory.getInstance().getService(UserService.class);

    public static void startApp(Scanner scanner) {
        while (true) {
            printMenu();
            String choice = UIForms.readChoice(scanner, UIMessage.MENU_CHOOSE, 1, 4);

            try {
                switch (choice) {
                    case "1":
                        printAllLogs();
                        break;
                    case "2":
                        printLogsForUser(scanner);
                        break;
                    case "3":
                        printSessionUserLogs();
                        break;
                    case "4":
                        return;
                    default:
                        System.out.println(Translator.translate(UIMessage.MSG_INVALID_CHOICE));
                }
            } catch (ApplicationException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n--- " + Translator.translate(UIMessage.MENU_TITLE_ADMIN) + " ---");
        System.out.println("1. " + Translator.translate(UIMessage.ADMIN_LOGS_ALL));
        System.out.println("2. " + Translator.translate(UIMessage.ADMIN_LOGS_BY_USER));
        System.out.println("3. " + Translator.translate(UIMessage.ADMIN_LOGS_SESSION));
        System.out.println("4. " + Translator.translate(UIMessage.MENU_EXIT));
    }

    private static void printAllLogs() {
        Map<Integer, List<LogEntry>> all = Logger.getAllLogs();
        if (all.isEmpty()) {
            System.out.println(Translator.translate(UIMessage.ADMIN_EMPTY_LOGS));
            return;
        }
        for (Map.Entry<Integer, List<LogEntry>> e : all.entrySet()) {
            User currentUser = userService.get(e.getKey());
            System.out.println("--- " + currentUser.getClass().getSimpleName() + ", " + currentUser.getFullName() + ", id=" + e.getKey() + " ---");
            for (LogEntry entry : e.getValue()) {
                System.out.print(entry);
            }
        }
    }

    private static void printLogsForUser(Scanner scanner) {
        int userId = UIForms.readInt(scanner, UIMessage.INPUT_USER_ID);
        List<LogEntry> logs = Logger.getUserLogs(userId);
        if (logs.isEmpty()) {
            System.out.println(Translator.translate(UIMessage.ADMIN_EMPTY_LOGS));
            return;
        }
        for (LogEntry entry : logs) {
            System.out.print(entry);
        }
    }

    private static void printSessionUserLogs() {
        User user = SessionData.getInstance().getUser();
        if (user == null) {
            System.out.println(Translator.translate(UIMessage.ADMIN_NO_ACTIVE_USER));
            return;
        }
        List<LogEntry> logs = Logger.getUserLogs(user.getId());
        if (logs.isEmpty()) {
            System.out.println(Translator.translate(UIMessage.ADMIN_EMPTY_LOGS));
            return;
        }
        for (LogEntry entry : logs) {
            System.out.print(entry);
        }
    }
}
