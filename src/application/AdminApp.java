package application;

import model.domain.Admin;
import model.domain.Course;
import model.domain.Student;
import model.domain.User;
import model.enumeration.CourseType;
import model.enumeration.UIMessages;
import services.*;
import utils.UIFields;

import java.util.Date;
import java.util.Scanner;

import exceptions.ApplicationException;
import exceptions.DoesNotExist;

public class AdminApp {

    private static final UserService userService = new UserService();
    private static final CourseService courseService = new CourseService();

    public static void startApp(Scanner scanner) {
        while (true) {
            printMenu();

            String choice = scanner.nextLine().trim();

            if (!choice.matches("[1-6]")) {
                System.out.println(LanguageService.translate(UIMessages.INVALID_CHOICE));
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
            } catch (ApplicationException e) {
                System.out.println(LanguageService.translate(e.getMessageKey(), e.getArgs()));
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n--- " + LanguageService.translate(UIMessages.TITLE_ADMIN) + " ---");
        System.out.println("1. " + LanguageService.translate(UIMessages.UPDATE_USER));
        System.out.println("2. " + LanguageService.translate(UIMessages.DELETE_USER));
        System.out.println("3. " + LanguageService.translate(UIMessages.CREATE_ADMIN));
        System.out.println("4. " + LanguageService.translate(UIMessages.CREATE_STUDENT));
        System.out.println("5. " + LanguageService.translate(UIMessages.CREATE_COURSE));
        System.out.println("6. " + LanguageService.translate(UIMessages.EXIT));
        System.out.println("--------------------");
        System.out.print(LanguageService.translate(UIMessages.CHOOSE));
    }

    private static void updateUser(Scanner scanner) throws DoesNotExist {
        String login = UIFields.readNonEmpty(scanner, "ADMIN UPDATE", UIMessages.TARGET_LOGIN);

        User user = userService.findOrThrow(login);
        User updated = new User(user);

        String name = UIFields.readOptional(scanner, UIMessages.NEW_NAME);
        if (!name.isBlank()) updated.setName(name);

        String surname = UIFields.readOptional(scanner, UIMessages.NEW_SURNAME);
        if (!surname.isBlank()) updated.setSurname(surname);

        String pass = UIFields.readOptional(scanner, UIMessages.NEW_PASSWORD);
        if (!pass.isBlank()) updated.setPassword(pass);

        boolean banned = UIFields.readYesNo(scanner, "ADMIN UPDATE", UIMessages.BAN);
        updated.setBanned(banned);

        userService.updateUser(user, updated);
    }

    private static void deleteUser(Scanner scanner) {
        String login = UIFields.readNonEmpty(scanner, "ADMIN DELETE", UIMessages.TARGET_LOGIN);
        userService.deleteUser(login);
    }

    private static void createCourse(Scanner scanner) {
        String name = UIFields.readNonEmpty(scanner, "COURSE CREATE", UIMessages.COURSE_NAME);
        String description = UIFields.readNonEmpty(scanner, "COURSE CREATE", UIMessages.COURSE_DESC);
        int credits = UIFields.readInt(scanner, "COURSE CREATE", UIMessages.COURSE_CREDITS);

        CourseType type = askCourseType(scanner);

        courseService.createCourse(new Course(name, description, credits, type));

        System.out.println(LanguageService.translate(UIMessages.CREATED));
    }

    private static CourseType askCourseType(Scanner scanner) {
        while (true) {
            System.out.print(LanguageService.translate(UIMessages.COURSE_TYPE));
            String t = scanner.nextLine().trim();

            switch (t) {
                case "1": return CourseType.MAJOR;
                case "2": return CourseType.MINOR;
                case "3": return CourseType.ELECTIVE;
                default:
                    System.out.println(LanguageService.translate(UIMessages.INVALID_CHOICE));
            }
        }
    }

    private static void register(Scanner scanner, boolean isAdmin) {
        String login = UIFields.readNonEmpty(scanner, "REGISTER", UIMessages.LOGIN);
        String pass = UIFields.readNonEmpty(scanner, "REGISTER", UIMessages.PASSWORD);
        String name = UIFields.readNonEmpty(scanner, "REGISTER", UIMessages.NAME);
        String surname = UIFields.readNonEmpty(scanner, "REGISTER", UIMessages.SURNAME);

        if (isAdmin)
            userService.createUser(new Admin(login, pass, name, surname));
        else
        	userService.createUser(new Student(login, pass, name, surname, new Date()));

        System.out.println(LanguageService.translate(UIMessages.REGISTERED));
    }
}
