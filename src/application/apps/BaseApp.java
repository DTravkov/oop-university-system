package application.apps;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import exceptions.ApplicationException;
import model.domain.User;
import model.factories.ServiceRegistry;
import settings.AppSettings;
import utils.Translator;
import utils.UIForms;
import utils.UIText;

public abstract class BaseApp {

    protected static final Scanner scanner = new Scanner(System.in);
    protected static final ServiceRegistry services = ServiceRegistry.getInstance();

    public static void kill() {
        System.exit(0);
    }

    public static User getActiveUser() {
        return AppSettings.getActiveUser();
    }

    public static boolean isAuthenticated() {
        return AppSettings.getActiveUser().getId() != AppSettings.ANONYMOUS_USER_ID;
    }

    public static void print(String data) {
        System.out.print(data);
    }

    public static void print(Object data) {
        System.out.print(data);
    }

    public static void println(String data) {
        System.out.println(data);
    }

    public static void println(Object data) {
        System.out.println(data);
    }

    public static void printHeader(String data) {
        System.out.println("||| " + data + " |||");
    }

    public static void printHeader(Object data) {
        System.out.println("||| " + data + " |||");
    }

    public static void printSuccess(String data) {
        System.out.println("[" + UIText.SUCCESS.localized() + "] " + data);
    }

    public static void printFail(String data) {
        System.out.println("[" + UIText.FAIL.localized() + "] " + data);
    }

    public static void printExceptionDetails(ApplicationException exc) {
        printFail(exc.getMessage());
    }

    protected static void handleExceptions(Runnable action) {
        try {
            action.run();
        } catch (ApplicationException exc) {
            printExceptionDetails(exc);
        }
    }

    public static void printInvalidChoice() {
        println(Translator.translate(UIText.MSG_INVALID_CHOICE));
    }

    protected static class MenuBuilder {

        private final List<Action> actions = new ArrayList<>();
        private String menuTitle;
        private boolean isRunning;

        protected MenuBuilder(String menuTitle) {
            if (menuTitle.isBlank()) {
                this.menuTitle = "";
                return;
            }
            this.menuTitle = "\n||| " + menuTitle + " |||";
        }

        protected MenuBuilder(String menuTitle, boolean withExit) {
            this(menuTitle);
        }

        public void start() {
            isRunning = true;
            int actionCount = actions.size();

            while (isRunning) {
                println(menuTitle);
                for (int i = 0; i < actionCount; i++) {
                    String prefix = String.valueOf(i+1) + ". ";
                    String actionTitle = actions.get(i).title;
                    println(prefix + " " + actionTitle);
                }
                int choice = UIForms.readChoice(scanner, UIText.MENU_CHOOSE, 1, actionCount);
                actions.get(choice - 1).run();
            }
        }

        protected void stop() {
            isRunning = false;
        }

        protected MenuBuilder addLabel(String label) {
            this.menuTitle += "\n" + label;
            return this;
        }

        protected MenuBuilder addAction(String actionTitle, Runnable callback) {
            actions.add(new Action(actionTitle, callback));
            return this;
        }

        protected MenuBuilder addExit() {
            actions.add(new Action(UIText.MENU_EXIT.localized(), this::stop));
            return this;
        }

        private final class Action {

            public final String title;
            public final Runnable callback;

            public Action(String title, Runnable callback) {
                this.title = title;
                this.callback = callback;
            }

            public void run() {
                BaseApp.handleExceptions(callback);
            }
        }
    }
}
