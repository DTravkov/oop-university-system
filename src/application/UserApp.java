package application;

import java.util.Date;
import java.util.Scanner;

import exceptions.ApplicationException;
import model.domain.*;
import model.enumeration.TeacherType;
import model.enumeration.UIMessage;
import model.factories.ServiceFactory;
import services.*;
import utils.Translator;
import utils.UIForms;

public class UserApp {

    private static final ServiceFactory serviceFactory = ServiceFactory.getInstance();
    private static final UserService userService = serviceFactory.getService(UserService.class);

    public static void startApp(Scanner scanner) {
        while (true) {
            printMenu();
            String choice = UIForms.readChoice(scanner, UIMessage.MENU_CHOOSE, 1, 7);

            try {
                switch (choice) {
                    case "1":
                        registerUser(scanner);
                        break;
                    case "2":
                        printUserById(scanner);
                        break;
                    case "3":
                        getAllUsersByRole(scanner);
                        break;
                    case "4":
                        printAllUsers();
                        break;
                    case "5":
                        deleteUser(scanner);
                        break;
                    case "6":
                        authenticate(scanner);
                        break;
                    case "7":
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
        System.out.println("\n--- " + Translator.translate(UIMessage.MENU_TITLE_USER) + " ---");
        System.out.println("1. " + Translator.translate(UIMessage.AUTH_SIGN_UP));
        System.out.println("2. Get user by id");
        System.out.println("3. List all users by role");
        System.out.println("4. " + Translator.translate(UIMessage.MENU_VIEW_ALL));
        System.out.println("5. " + Translator.translate(UIMessage.USER_DELETE));
        System.out.println("6. " + Translator.translate(UIMessage.AUTH_LOG_IN));
        System.out.println("7. " + Translator.translate(UIMessage.MENU_EXIT));
    }

    private static void getAllUsersByRole(Scanner scanner) {
        Class<? extends User> inputClass = UIForms.readUserClass(scanner);
        System.out.println(userService.getAllByClass(inputClass));
    }

    private static void registerUser(Scanner scanner) {
        Class<? extends User> inputClass = UIForms.readUserClass(scanner);
        String login = UIForms.readNonEmpty(scanner, UIMessage.INPUT_LOGIN);
        String password = UIForms.readNonEmpty(scanner, UIMessage.INPUT_PASSWORD);
        String name = UIForms.readNonEmpty(scanner, UIMessage.INPUT_NAME);
        String surname = UIForms.readNonEmpty(scanner, UIMessage.INPUT_SURNAME);

        Date admissionDate = null;
        TeacherType teacherType = null;


        // this thingy checks that inputClass is a subclass of IEnrollable or Teacher, to provide additional input options
        if (IEnrollable.class.isAssignableFrom(inputClass)) {
            admissionDate = new Date();
        } else if (Teacher.class.isAssignableFrom(inputClass)) {
            teacherType = UIForms.askTeacherType(scanner);
        }

        
        User user = userService.registerUser(inputClass, login, password, name, surname, admissionDate, teacherType);

        System.out.println(Translator.translate(UIMessage.MSG_CREATED));
        System.out.println(user);
    }

    private static void printUserById(Scanner scanner) {
        printAllUsers();
        int id = UIForms.readInt(scanner, UIMessage.INPUT_STUDENT_ID);
        System.out.println(userService.get(id));
    }

    private static void printAllUsers() {
        System.out.println(userService.getAll());
    }

    private static void deleteUser(Scanner scanner) {
        printAllUsers();
        int id = UIForms.readInt(scanner, UIMessage.INPUT_STUDENT_ID);
        userService.delete(id);
        System.out.println(Translator.translate(UIMessage.MSG_DELETED));
        System.out.println(userService.getAll());
    }

    private static void authenticate(Scanner scanner) {
        String login = UIForms.readNonEmpty(scanner, UIMessage.INPUT_LOGIN);
        String password = UIForms.readNonEmpty(scanner, UIMessage.INPUT_PASSWORD);
        User user = userService.authenticate(login, password);
        System.out.println("Authenticated: " + user);
    }
}
