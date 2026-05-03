package application;

import java.util.Scanner;

import model.enumeration.LanguagePreference;
import model.enumeration.UIMessage;
import settings.AppSettings;
import utils.Translator;
import utils.UIForms;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        askLanguage(scanner);

        while (true) {
            printMenu();
            String choice = UIForms.readChoice(scanner, UIMessage.MENU_CHOOSE, 1, 12);

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
                    TechSupportApp.startApp(scanner);
                    break;
                case "6":
                    ComplaintApp.startApp(scanner);
                    break;
                case "7":
                    StudentOrganizationApp.startApp(scanner);
                    break;
                case "8":
                    NewsApp.startApp(scanner);
                    break;
                case "9":
                    ResearchApp.startApp(scanner);
                    break;
                case "10":
                    AdminApp.startApp(scanner);
                    break;
                case "11":
                    TestApp.runAllTests();
                    break;
                case "12":
                    System.out.println(Translator.translate(UIMessage.AUTH_GOODBYE));
                    scanner.close();
                    return;
                default:
                    System.out.println(Translator.translate(UIMessage.MSG_INVALID_CHOICE));
            }
        }
    }

    private static void printMenu() {
        System.out.print("\nCurrent active user : "  + AppSettings.getActiveUser());
        System.out.println("\n=== University System ===");
        System.out.println("1. User App");
        System.out.println("2. Course App");
        System.out.println("3. Enrollment App");
        System.out.println("4. Message App");
        System.out.println("5. Tech Support App");
        System.out.println("6. Teacher Complaint App");
        System.out.println("7. Student Org. App");
        System.out.println("8. News App");
        System.out.println("9. Research App");
        System.out.println("10. " + Translator.translate(UIMessage.MENU_TITLE_ADMIN));
        System.out.println("11. Run tests");
        System.out.println("12. " + Translator.translate(UIMessage.MENU_EXIT));
    }

    private static void askLanguage(Scanner scanner){

        System.out.println("\n=== Choose preferable language ===");
        System.out.println("1. English language");
        System.out.println("2. Қазақ тілі");
        System.out.println("3. Русский язык");

        while(true){
            String choice = UIForms.readChoice(scanner, UIMessage.AUTH_CHANGE_LANG, 1, 3);
            switch (choice) {
                case "1":
                    AppSettings.setLanguage(LanguagePreference.EN);
                    return;
                case "2":
                    AppSettings.setLanguage(LanguagePreference.KK);
                    return;
                case "3":
                    AppSettings.setLanguage(LanguagePreference.RU);
                    return;
                default:
                    System.out.println(Translator.translate(UIMessage.MSG_INVALID_CHOICE));
                    break;
            }
        }
    }
}
