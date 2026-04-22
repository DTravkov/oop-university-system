package application;

import java.util.Scanner;

import model.enumeration.LanguagePreference;
import model.enumeration.UIMessages;
import services.LanguageService;
import utils.UIFields;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        askLanguage(scanner);

        while (true) {
            printMenu();
            String choice = UIFields.readChoice(scanner, UIMessages.CHOOSE, 1, 5);

            switch (choice) {
                case "1":
                    UserApp.startApp(scanner);
                    break;
                case "2":
                    CourseApp.startApp(scanner);
                    break;
                case "3":
                    EnrollmentApp.startApp(scanner);
                    break;
                case "4":
                    MessageApp.startApp(scanner);
                    break;
                case "5":
                    System.out.println(LanguageService.translate(UIMessages.GOODBYE));
                    scanner.close();
                    return;
                default:
                    System.out.println(LanguageService.translate(UIMessages.INVALID_CHOICE));
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n=== University System ===");
        System.out.println("1. User App");
        System.out.println("2. Course App");
        System.out.println("3. Enrollment App");
        System.out.println("4. Message App");
        System.out.println("5. " + LanguageService.translate(UIMessages.EXIT));
        System.out.print(LanguageService.translate(UIMessages.CHOOSE));
    }

    private static void askLanguage(Scanner scanner){
        System.out.println("\n=== Choose preferable language ===");
        System.out.println("1. English language");
        System.out.println("2. Қазақ тілі");
        System.out.println("3. Русский язык");
        while(true){
            String choice = UIFields.readChoice(scanner, UIMessages.CHANGE_LANG, 1, 3);
            switch (choice) {
                case "1":
                    LanguageService.updateLanguage(LanguagePreference.EN);
                    return;
                case "2":
                    LanguageService.updateLanguage(LanguagePreference.KK);
                    return;
                case "3":
                    LanguageService.updateLanguage(LanguagePreference.RU);
                    return;
                default:
                    System.out.println(LanguageService.translate(UIMessages.INVALID_CHOICE));
                    break;
            }
        }
    }
}
