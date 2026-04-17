package application;

import java.util.Scanner;

import exceptions.ApplicationException;
import model.Admin;
import model.Manager;
import model.Student;
import model.Teacher;
import model.TeacherTypeEnum;
import model.TechSupportSpecialist;
import model.UIMessages;
import services.LanguageService;
import services.UserService;
import utils.UIFields;

public class UserApp {

    private static final UserService authService = new UserService();

    public static void startApp(Scanner scanner) {
        while (true) {
            printMenu();

            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1":
                        registerAdmin(scanner);
                        break;
                    case "2":
                        registerStudent(scanner);
                        break;
                    case "3":
                        registerTeacher(scanner);
                        break;
                    case "4":
                        registerManager(scanner);
                        break;
                    case "5":
                        registerTechSupportSpecialist(scanner);
                        break;
                    case "6":
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
        System.out.println("\n--- " + LanguageService.translate(UIMessages.TITLE_USER) + " ---");
        System.out.println("1. " + LanguageService.translate(UIMessages.CREATE_ADMIN));
        System.out.println("2. " + LanguageService.translate(UIMessages.CREATE_STUDENT));
        System.out.println("3. " + LanguageService.translate(UIMessages.CREATE_TEACHER));
        System.out.println("4. " + LanguageService.translate(UIMessages.CREATE_MANAGER));
        System.out.println("5. " + LanguageService.translate(UIMessages.CREATE_SUPPORT));
        System.out.println("6. " + LanguageService.translate(UIMessages.EXIT));
        System.out.println("--------------------");
        System.out.print(LanguageService.translate(UIMessages.CHOOSE));
    }

    private static void registerAdmin(Scanner scanner) {
        authService.createUser(new Admin(
            readLogin(scanner),
            readPassword(scanner),
            readName(scanner),
            readSurname(scanner)
        ));
        printSuccess();
    }

    private static void registerStudent(Scanner scanner) {
        authService.createUser(new Student(
            readLogin(scanner),
            readPassword(scanner),
            readName(scanner),
            readSurname(scanner)
        ));
        printSuccess();
    }

    private static void registerTeacher(Scanner scanner) {
        authService.createUser(new Teacher(
            readLogin(scanner),
            readPassword(scanner),
            readName(scanner),
            readSurname(scanner),
            askTeacherType(scanner)
        ));
        printSuccess();
    }

    private static void registerManager(Scanner scanner) {
        authService.createUser(new Manager(
            readLogin(scanner),
            readPassword(scanner),
            readName(scanner),
            readSurname(scanner)
        ));
        printSuccess();
    }
    
    private static TeacherTypeEnum askTeacherType(Scanner scanner) {
    	return UIFields.askTeacherType(scanner);
    }

    private static void registerTechSupportSpecialist(Scanner scanner) {
        authService.createUser(new TechSupportSpecialist(
            readLogin(scanner),
            readPassword(scanner),
            readName(scanner),
            readSurname(scanner)
        ));
        printSuccess();
    }

    private static String readLogin(Scanner scanner) {
        return UIFields.readNonEmpty(scanner, "REGISTER", UIMessages.LOGIN);
    }

    private static String readPassword(Scanner scanner) {
        return UIFields.readNonEmpty(scanner, "REGISTER", UIMessages.PASSWORD);
    }

    private static String readName(Scanner scanner) {
        return UIFields.readNonEmpty(scanner, "REGISTER", UIMessages.NAME);
    }

    private static String readSurname(Scanner scanner) {
        return UIFields.readNonEmpty(scanner, "REGISTER", UIMessages.SURNAME);
    }

    private static void printSuccess() {
        System.out.println(LanguageService.translate(UIMessages.REGISTERED));
    }
}
