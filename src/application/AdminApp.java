package application;

import java.util.List;

import exceptions.ApplicationException;
import model.domain.User;
import model.enumeration.UIMessage;
import settings.AppSettings;
import settings.SessionData;
import utils.LogEntry;
import utils.Logger;
import utils.Translator;
import utils.UIForms;

public final class AdminApp extends BaseApp {

    private AdminApp() {
    }

    public static void startApp() {
        while (true) {
            printMenu();
            String choice = readChoice(UIMessage.MENU_CHOOSE, 1, 5);

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
        println("5. " + Translator.translate(UIMessage.MENU_EXIT));
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
}
