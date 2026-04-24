package application;

import java.util.Date;
import java.util.Scanner;

import exceptions.ApplicationException;
import exceptions.OperationNotAllowed;
import model.domain.User;
import model.enumeration.TeacherType;
import model.enumeration.UIMessages;
import model.enumeration.UserRole;
import services.LanguageService;
import services.UserService;
import utils.UIFields;

public class UserApp {

    private static final UserService userService = new UserService();

    public static void startApp(Scanner scanner) {
        while (true) {
            printMenu();
            String choice = UIFields.readChoice(scanner, UIMessages.CHOOSE, 1, 7);

            try {
                switch (choice) {
                    case "1":
                        createUser(scanner);
                        break;
                    case "2":
                        getUserById(scanner);
                        break;
                    case "3":
                        getAllUsersByRole(scanner);
                        break;
                    case "4":
                        getAllUsers();
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
                        System.out.println(LanguageService.translate(UIMessages.INVALID_CHOICE));
                }
            } catch (ApplicationException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n--- " + LanguageService.translate(UIMessages.TITLE_USER) + " ---");
        System.out.println("1. " + LanguageService.translate(UIMessages.SIGN_UP));
        System.out.println("2. Get user by id");
        System.out.println("3. List all users by role");
        System.out.println("4. " + LanguageService.translate(UIMessages.VIEW_ALL));
        System.out.println("5. " + LanguageService.translate(UIMessages.DELETE_USER));
        System.out.println("6. " + LanguageService.translate(UIMessages.LOG_IN));
        System.out.println("7. " + LanguageService.translate(UIMessages.EXIT));
    }

    private static void getAllUsersByRole(Scanner scanner) {
        String role = UIFields.readNonEmpty(scanner, UIMessages.USER_ROLE);
        role = role.toUpperCase();
        try{
            UserRole.valueOf(role);
        }
        catch (IllegalArgumentException e){
            throw new OperationNotAllowed(" find role : " + role);
        }
        System.out.println(userService.getAllByRole(UserRole.valueOf(role)));
    }

    private static void createUser(Scanner scanner) {
        UserRole role = UIFields.readUserRole(scanner);
        String login = UIFields.readNonEmpty(scanner, UIMessages.LOGIN);
        String password = UIFields.readNonEmpty(scanner, UIMessages.PASSWORD);
        String name = UIFields.readNonEmpty(scanner, UIMessages.NAME);
        String surname = UIFields.readNonEmpty(scanner, UIMessages.SURNAME);

        Date admissionDate = null;
        TeacherType teacherType = null;

        if (role == UserRole.STUDENT) {
            admissionDate = new Date();
        } else if (role == UserRole.TEACHER) {
            teacherType = UIFields.askTeacherType(scanner);
        }

        User user = userService.create(role, login, password, name, surname, admissionDate, teacherType);

        System.out.println(LanguageService.translate(UIMessages.CREATED));
        System.out.println(user);
        System.out.println(userService.getAll());
    }

    private static void getUserById(Scanner scanner) {
        int id = UIFields.readInt(scanner, UIMessages.STUDENT_ID);
        System.out.println(userService.get(id));
    }

    private static void getAllUsers() {
        System.out.println(userService.getAll());
    }

    private static void deleteUser(Scanner scanner) {
        int id = UIFields.readInt(scanner, UIMessages.STUDENT_ID);
        userService.delete(id);
        System.out.println(LanguageService.translate(UIMessages.DELETED));
        System.out.println(userService.getAll());
    }

    private static void authenticate(Scanner scanner) {
        String login = UIFields.readNonEmpty(scanner, UIMessages.LOGIN);
        String password = UIFields.readNonEmpty(scanner, UIMessages.PASSWORD);
        User user = userService.authenticate(login, password);
        System.out.println("Authenticated: " + user);
    }
}
