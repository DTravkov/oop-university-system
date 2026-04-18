package application;

import model.domain.SessionData;
import model.domain.User;
import model.enumeration.LanguagePreference;
import model.enumeration.UIMessages;

import services.UserService;
import utils.UIFields;
import services.LanguageService;

import java.util.Scanner;

import exceptions.ApplicationException;
import exceptions.DoesNotExist;
import exceptions.InvalidCredentials;

class AuthApp {

    private static final UserService userService = new UserService();

    public static void startApp(Scanner scanner) {
        while (true) {
            printMenu();

            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "0":
                        changeLanguage(scanner);
                        break;

                    case "1":
                        register(scanner);
                        break;

                    case "2":
                        login(scanner);
                        afterLogin(scanner);
                        return;
                    case "3":
                        System.out.println(LanguageService.translate(UIMessages.GOODBYE));
                        return;

                    default:
                        System.out.println(LanguageService.translate(UIMessages.INVALID_CHOICE));
                }
            } catch (ApplicationException e) {
                System.out.println(LanguageService.translate(e.getMessageKey(), e.getArgs()));
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n--- " + LanguageService.translate(UIMessages.TITLE_AUTH) + " ---");
        System.out.println("0. " + LanguageService.translate(UIMessages.CHANGE_LANG));
        System.out.println("1. " + LanguageService.translate(UIMessages.SIGN_UP));
        System.out.println("2. " + LanguageService.translate(UIMessages.LOG_IN));
        System.out.println("3. " + LanguageService.translate(UIMessages.EXIT));
        System.out.println("--------------------");
        System.out.print(LanguageService.translate(UIMessages.CHOOSE));
    }

    private static void changeLanguage(Scanner scanner) {
        while (true) {
            System.out.println("1. English");
            System.out.println("2. Русский");
            System.out.println("3. Қазақша");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    LanguageService.updateLanguage(LanguagePreference.EN);
                    return;
                case "2":
                    LanguageService.updateLanguage(LanguagePreference.RU);
                    return;
                case "3":
                    LanguageService.updateLanguage(LanguagePreference.KK);
                    return;
                default:
                    System.out.println(LanguageService.translate(UIMessages.INVALID_CHOICE));
            }
        }
    }

    private static void register(Scanner scanner) {
        String login = UIFields.readNonEmpty(scanner, "AUTH REGISTER", UIMessages.LOGIN);
        String pass = UIFields.readNonEmpty(scanner, "AUTH REGISTER", UIMessages.PASSWORD);
        String name = UIFields.readNonEmpty(scanner, "AUTH REGISTER", UIMessages.NAME);
        String surname = UIFields.readNonEmpty(scanner, "AUTH REGISTER", UIMessages.SURNAME);
        userService.createUser(new User(login, pass, name, surname));

        System.out.println(LanguageService.translate(UIMessages.REGISTERED));
    }

    private static void login(Scanner scanner) throws InvalidCredentials, DoesNotExist {
        String login = UIFields.readNonEmpty(scanner, "AUTH LOGIN", UIMessages.LOGIN);
        String pass = UIFields.readNonEmpty(scanner, "AUTH LOGIN", UIMessages.PASSWORD);

        User user = userService.authenticate(login, pass);

        System.out.println(LanguageService.translate(UIMessages.WELCOME.getKey(), user.getName()));
        
        SessionData.getInstance().setUser(user);
    }

    private static void afterLogin(Scanner scanner) {
        MessageApp.startApp(scanner);
    }

    private static void openMessageSystem(Scanner scanner) {
        if (SessionData.getInstance().getUser() == null) {
            throw new DoesNotExist("Current session user");
        }
        MessageApp.startApp(scanner);
    }
}
