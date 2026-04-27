package application;

import java.util.Scanner;

import model.enumeration.LanguagePreference;
import model.enumeration.UIMessages;
import services.*;
import utils.UIFields;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        askLanguage(scanner);

        while (true) {
            printMenu();
            String choice = UIFields.readChoice(scanner, UIMessages.MENU_CHOOSE, 1, 10);

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
                    ComplaintApp.startApp(scanner);
                    break;
                case "6":
                    StudentOrganizationApp.startApp(scanner);
                    break;
                case "7":
                    NewsApp.startApp(scanner);
                    break;
                case "8":
                    TestApp.runScenario();
                    break;
                case "9":
                    TestApp.printAllData();
                    break;
                case "10":
                    System.out.println(LanguageService.translate(UIMessages.AUTH_GOODBYE));
                    scanner.close();
                    return;
                default:
                    System.out.println(LanguageService.translate(UIMessages.MSG_INVALID_CHOICE));
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n=== University System ===");
        System.out.println("1. User App");
        System.out.println("2. Course App");
        System.out.println("3. Enrollment App");
        System.out.println("4. Message App");
        System.out.println("5. Teacher Complaint App");
        System.out.println("6. Student Org. App");
        System.out.println("7. News App");
        System.out.println("8. Run tests");
        System.out.println("9. Print all data");
        System.out.println("10. " + LanguageService.translate(UIMessages.MENU_EXIT));
    }

    private static void askLanguage(Scanner scanner){
        System.out.println("\n=== Choose preferable language ===");
        System.out.println("1. English language");
        System.out.println("2. Қазақ тілі");
        System.out.println("3. Русский язык");
        while(true){
            String choice = UIFields.readChoice(scanner, UIMessages.AUTH_CHANGE_LANG, 1, 3);
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
                    System.out.println(LanguageService.translate(UIMessages.MSG_INVALID_CHOICE));
                    break;
            }
        }
    }
}
