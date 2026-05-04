package application;

import java.util.Date;

import exceptions.ApplicationException;
import model.domain.*;
import model.enumeration.TeacherType;
import model.enumeration.UIMessage;
import services.UserService;
import utils.Translator;
import utils.UIForms;

public final class UserApp extends BaseApp {

    private static final UserService userService = services.userService;

    private UserApp() {
    }

    public static void startApp() {
        while (true) {
            printMenu();
            String choice = readChoice(UIMessage.MENU_CHOOSE, 1, 7);

            try {
                switch (choice) {
                    case "1":
                        registerUser();
                        break;
                    case "2":
                        printUserById();
                        break;
                    case "3":
                        printAllUsersByChoice();
                        break;
                    case "4":
                        printAllUsers();
                        break;
                    case "5":
                        deleteUser();
                        break;
                    case "6":
                        authenticate();
                        break;
                    case "7":
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
        println("\n--- " + Translator.translate(UIMessage.MENU_TITLE_USER) + " ---");
        println("1. " + Translator.translate(UIMessage.AUTH_SIGN_UP));
        println("2. Get user by id");
        println("3. List all users by role");
        println("4. " + Translator.translate(UIMessage.MENU_VIEW_ALL));
        println("5. " + Translator.translate(UIMessage.USER_DELETE));
        println("6. " + Translator.translate(UIMessage.AUTH_LOG_IN));
        println("7. " + Translator.translate(UIMessage.MENU_EXIT));
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

        User user = userService.registerUser(inputClass, login, password, name, surname, admissionDate, teacherType);

        println(Translator.translate(UIMessage.MSG_CREATED));
        println(userService.getDTO(user));
    }

    private static void deleteUser() {
        printAllUsers();
        int id = UIForms.readInt(scanner, UIMessage.INPUT_STUDENT_ID);
        userService.delete(id);
        println(Translator.translate(UIMessage.MSG_DELETED));
        printAllUsers();
    }

    private static void authenticate() {
        String login = UIForms.readNonEmpty(scanner, UIMessage.INPUT_LOGIN);
        String password = UIForms.readNonEmpty(scanner, UIMessage.INPUT_PASSWORD);
        User user = userService.authenticate(login, password);
        println(userService.getDTO(user));
    }

    private static void printUserById() {
        printAllUsers();
        int id = UIForms.readInt(scanner, UIMessage.INPUT_STUDENT_ID);
        println(userService.getDTO(id));
    }

    private static void printAllUsersByChoice() {
        Class<? extends User> inputClass = UIForms.readUserClass(scanner);
        for (User user : userService.getAllByClass(inputClass)) {
            println(userService.getDTO(user).toShortString());
        }
    }

    private static void printAllUsers() {
        for (User user : userService.getAll()) {
            println(userService.getDTO(user).toShortString());
        }
    }

}
