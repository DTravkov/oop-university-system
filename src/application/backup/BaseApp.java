package application;

import java.util.Scanner;

import exceptions.ApplicationException;
import model.enumeration.UIMessage;
import model.factories.ServiceRegistry;
import utils.Translator;
import utils.UIForms;

public abstract class BaseApp {

    protected static final Scanner scanner = new Scanner(System.in);
    protected static final ServiceRegistry services = ServiceRegistry.getInstance();

    protected BaseApp() {
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

    protected static void printInvalidChoice() {
        println(Translator.translate(UIMessage.MSG_INVALID_CHOICE));
    }

    protected static String readChoice(UIMessage prompt, int min, int max) {
        return UIForms.readChoice(scanner, prompt, min, max);
    }

    protected static class MenuAction {
        private final String title;
        private final Runnable action;
        private final boolean exitAfterRun;

        protected MenuAction(String title, Runnable action) {
            this(title, action, false);
        }

        protected MenuAction(String title, Runnable action, boolean exitAfterRun) {
            this.title = title;
            this.action = action;
            this.exitAfterRun = exitAfterRun;
        }

        protected static MenuAction exit(String title) {
            return new MenuAction(title, () -> {
            }, true);
        }

        protected String getTitle() {
            return title;
        }

        protected void execute() {
            action.run();
        }

        protected boolean shouldExitAfterRun() {
            return exitAfterRun;
        }
    }
}
