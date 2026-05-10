package application;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import exceptions.ApplicationException;
import model.domain.User;
import model.enumeration.UIMessage;
import model.factories.ServiceRegistry;
import settings.AppSettings;
import utils.Translator;
import utils.UIForms;

public abstract class BaseApp {

    protected static final Scanner scanner = new Scanner(System.in);
    protected static final ServiceRegistry services = ServiceRegistry.getInstance();

    protected BaseApp() {
    }

    protected static void shutdown() {
        System.exit(0);
    }

    protected static void print(String data) {
        System.out.print(data);
    }

    protected static void print(Object data) {
        System.out.print(data);
    }

    protected static void println(String data) {
        System.out.println(data);
    }

    protected static void println(Object data) {
        System.out.println(data);
    }

    protected static void printSuccess(String data) {
        System.out.println( "[" + Translator.translate(UIMessage.SUCCESS) + "] " + data);
    }

    protected static void printFail(String data) {
        System.out.println("[" + Translator.translate(UIMessage.FAIL) + "] " + data);
    }

    protected static void printExceptionDetails(ApplicationException exc) {
        printFail(exc.getMessage());
    }

    protected static void handleExceptions(Runnable action) {
        try {
            action.run();
        } catch (ApplicationException exc) {
            printExceptionDetails(exc);
        }
    }

    protected static void retryOnException(Runnable action) {
        while (true) {
            try {
                action.run();
                return;
            } catch (ApplicationException exc) {
                printExceptionDetails(exc);
            }
        }
    }

    protected static void printInvalidChoice() {
        println(Translator.translate(UIMessage.MSG_INVALID_CHOICE));
    }

    protected static User getActiveUser(){
        return AppSettings.getActiveUser();
    }

    protected static boolean isAuthenticated() {
        return AppSettings.getActiveUser().getId() != AppSettings.ANONYMOUS_USER_ID;
    }

    public static void logout() {
        AppSettings.clearActiveUser();
    }

    protected static class MenuBuilder {

        private final List<Action> actions = new ArrayList<>();
        private String menuTitle;
        private boolean isRunning;

        protected MenuBuilder(String menuTitle) {
            this.menuTitle = "\n||| " + menuTitle + " |||";
        }

        protected MenuBuilder(UIMessage msg, Object... args) {
            this(Translator.translate(msg, args));
        }

        protected void start() {
            isRunning = true;
            while (isRunning) {
                println(menuTitle);
                for (int i = 0; i < actions.size(); i++) {
                    String actionNumber = i + 1 + ".";
                    String actionTitle = actions.get(i).title;
                    println(actionNumber + " " + actionTitle);
                }
                int choice = UIForms.readChoice(scanner, UIMessage.MENU_CHOOSE, 1, actions.size());
                actions.get(choice - 1).run();
            }
        }

        protected void stop() {
            isRunning = false;
        }

        protected MenuBuilder addLabel(String label){
            this.menuTitle += "\n" + label;
            return this;
        }

        protected MenuBuilder addAction(String actionTitle, Runnable callback){
            actions.add(new Action(actionTitle, callback));
            return this;
        }

        private final class Action{
            public final String title;
            public final Runnable callback;

            public Action(String title, Runnable callback){
                this.title = title;
                this.callback = callback;
            }

            public void run() {
                BaseApp.handleExceptions(callback);
            }

        }

    }
}