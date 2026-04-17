package application;

import model.*;
import services.*;

import java.util.Scanner;

import exceptions.DoesNotExist;
import exceptions.FieldValidationError;

public class AdminApp {

    private static final UserService userService = new UserService();
    private static final AuthService authService = new AuthService();
    private static final CourseService courseService = new CourseService();

    public static void startApp(Scanner scanner) {
        while (true) {
            printMenu();

            String choice = scanner.nextLine().trim();

            if (!choice.matches("[1-6]")) {
                System.out.println(LanguageService.translate(UIMessages.ERROR_APP_INTERNAL));
                continue;
            }

            try {
                switch (choice) {
                    case "1":
                        updateUser(scanner);
                        break;

                    case "2":
                        deleteUser(scanner);
                        break;

                    case "3":
                        register(scanner, true);
                        break;

                    case "4":
                        register(scanner, false);
                        break;

                    case "5":
                        createCourse(scanner);
                        break;

                    case "6":
                        return;
                }
            } catch (DoesNotExist e) {
                System.out.println(e.getMessage());
            } catch (FieldValidationError e) {
                System.out.println(LanguageService.translate(UIMessages.ERROR_FIELD_VALIDATION));
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n--- " + LanguageService.translate(UIMessages.ADMIN_TITLE) + " ---");
        System.out.println("1. " + LanguageService.translate(UIMessages.ADMIN_UPDATE));
        System.out.println("2. " + LanguageService.translate(UIMessages.ADMIN_DELETE));
        System.out.println("3. " + LanguageService.translate(UIMessages.ADMIN_REG_ADMIN));
        System.out.println("4. " + LanguageService.translate(UIMessages.ADMIN_REG_STUDENT));
        System.out.println("5. " + LanguageService.translate(UIMessages.ADMIN_REG_COURSE));
        System.out.println("6. " + LanguageService.translate(UIMessages.ADMIN_EXIT));
        System.out.println("--------------------");
        System.out.print(LanguageService.translate(UIMessages.ADMIN_PICK_OPTION));
    }

    private static void updateUser(Scanner scanner) throws DoesNotExist {
        String login = InputService.readNonEmpty(scanner, "ADMIN UPDATE", UIMessages.ADMIN_PROMPT_TARGET_LOGIN);

        User user = userService.findOrThrow(login);
        User updated = new User(user);

        String name = InputService.readOptional(scanner, UIMessages.ADMIN_NEW_NAME);
        if (!name.isBlank()) updated.setName(name);

        String surname = InputService.readOptional(scanner, UIMessages.ADMIN_NEW_SURNAME);
        if (!surname.isBlank()) updated.setSurname(surname);

        String pass = InputService.readOptional(scanner, UIMessages.ADMIN_NEW_PASS);
        if (!pass.isBlank()) updated.setPassword(pass);

        boolean banned = InputService.readYesNo(scanner, "ADMIN UPDATE", UIMessages.ADMIN_BAN_CHOICE);
        updated.setBanned(banned);

        userService.updateUser(user, updated);
    }

    private static void deleteUser(Scanner scanner) {
        String login = InputService.readNonEmpty(scanner, "ADMIN DELETE", UIMessages.ADMIN_PROMPT_DELETE);
        userService.deleteUser(login);
    }

    private static void createCourse(Scanner scanner) {
        String name = InputService.readNonEmpty(scanner, "COURSE CREATE", UIMessages.ADMIN_COURSE_NAME_PROMPT);
        String description = InputService.readNonEmpty(scanner, "COURSE CREATE", UIMessages.ADMIN_COURSE_DESCRIPTION_PROMPT);
        int credits = InputService.readInt(scanner, "COURSE CREATE", UIMessages.ADMIN_COURSE_CREDITS_PROMPT);

        CourseType type = askCourseType(scanner);

        courseService.createCourse(new Course(name, description, credits, type));

        System.out.println(LanguageService.translate(UIMessages.ADMIN_REG_SUCCESS));
    }

    private static CourseType askCourseType(Scanner scanner) {
        while (true) {
            System.out.print(LanguageService.translate(UIMessages.ADMIN_COURSE_TYPE_SELECTION));
            String t = scanner.nextLine().trim();

            switch (t) {
                case "1": return CourseType.MAJOR;
                case "2": return CourseType.MINOR;
                case "3": return CourseType.ELECTIVE;
                default:
                    System.out.println(LanguageService.translate(UIMessages.ERROR_APP_INTERNAL));
            }
        }
    }

    private static void register(Scanner scanner, boolean isAdmin) {
        String login = InputService.readNonEmpty(scanner, "REGISTER", UIMessages.AUTH_PROMPT_LOGIN);
        String pass = InputService.readNonEmpty(scanner, "REGISTER", UIMessages.AUTH_PROMPT_PASS);
        String name = InputService.readNonEmpty(scanner, "REGISTER", UIMessages.AUTH_PROMPT_NAME);
        String surname = InputService.readNonEmpty(scanner, "REGISTER", UIMessages.AUTH_PROMPT_SURNAME);

        if (isAdmin)
            authService.registerUser(new Admin(login, pass, name, surname));
        else
            authService.registerUser(new Student(login, pass, name, surname));

        System.out.println(LanguageService.translate(UIMessages.ADMIN_REG_SUCCESS));
    }
}