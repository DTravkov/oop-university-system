package application;

import model.SessionData;
import model.User;
import model.LanguagePreference;
import model.UIMessages;

import services.AuthService;
import services.LanguageService;
import services.InputService;

import java.util.Scanner;

import exceptions.AlreadyExists;
import exceptions.DoesNotExist;
import exceptions.FieldValidationError;
import exceptions.InvalidCredentials;

class AuthApp {

    private static final AuthService authService = new AuthService();

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
                        return;

                    case "3":
                        System.out.println(LanguageService.translate(UIMessages.AUTH_GOODBYE));
                        return;

                    default:
                        System.out.println(LanguageService.translate(UIMessages.ERROR_APP_INTERNAL));
                }
            } catch (DoesNotExist | InvalidCredentials | AlreadyExists | FieldValidationError e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n--- " + LanguageService.translate(UIMessages.AUTH_TITLE) + " ---");
        System.out.println("0. " + LanguageService.translate(UIMessages.AUTH_CHANGE_LANG));
        System.out.println("1. " + LanguageService.translate(UIMessages.AUTH_SIGNUP));
        System.out.println("2. " + LanguageService.translate(UIMessages.AUTH_LOGIN));
        System.out.println("3. " + LanguageService.translate(UIMessages.AUTH_EXIT));
        System.out.println("--------------------");
        System.out.print(LanguageService.translate(UIMessages.ADMIN_PICK_OPTION));
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
                    System.out.println(LanguageService.translate(UIMessages.ERROR_APP_INTERNAL));
            }
        }
    }

    private static void register(Scanner scanner) {
        String login = InputService.readNonEmpty(scanner, "AUTH REGISTER", UIMessages.AUTH_PROMPT_LOGIN);
        String pass = InputService.readNonEmpty(scanner, "AUTH REGISTER", UIMessages.AUTH_PROMPT_PASS);
        String name = InputService.readNonEmpty(scanner, "AUTH REGISTER", UIMessages.AUTH_PROMPT_NAME);
        String surname = InputService.readNonEmpty(scanner, "AUTH REGISTER", UIMessages.AUTH_PROMPT_SURNAME);

        authService.registerUser(new User(login, pass, name, surname));

        System.out.println(LanguageService.translate(UIMessages.ADMIN_REG_SUCCESS));
    }

    private static void login(Scanner scanner) throws InvalidCredentials, DoesNotExist {
        String login = InputService.readNonEmpty(scanner, "AUTH LOGIN", UIMessages.AUTH_PROMPT_LOGIN);
        String pass = InputService.readNonEmpty(scanner, "AUTH LOGIN", UIMessages.AUTH_PROMPT_PASS);

        User user = authService.authenticate(login, pass);

        System.out.println(
            String.format(
                LanguageService.translate(UIMessages.AUTH_ACCESS_GRANTED),
                user.getName()
            )
        );

        SessionData.getInstance().setUser(user);
    }
}