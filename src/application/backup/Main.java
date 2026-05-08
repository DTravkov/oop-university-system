package application;

import model.enumeration.LanguagePreference;
import model.enumeration.UIMessage;
import settings.AppSettings;
import utils.Translator;
import utils.UIForms;

public class Main extends BaseApp {

    public static void main(String[] args) {
        startApp();
        scanner.close();
    }

    public static void startApp() {
        askLanguage();

        while (true) {
            printMenu();
            String choice = UIForms.readChoice(scanner, UIMessage.MENU_CHOOSE, 1, 13);

            switch (choice) {
                case "1":
                    UserApp.startApp();
                    break;
                case "2":
                    CourseApp.startApp();
                    break;
                case "3":
                    EnrollmentApp.startApp();
                    break;
                case "4":
                    MessageApp.startApp();
                    break;
                case "5":
                    TechRequestApp.startApp();
                    break;
                case "6":
                    ComplaintApp.startApp();
                    break;
                case "7":
                    StudentOrganizationApp.startApp();
                    break;
                case "8":
                    NewsApp.startApp();
                    break;
                case "9":
                    ResearchApp.startApp();
                    break;
                case "10":
                    AdminApp.startApp();
                    break;
                case "11":
                    TestApp.runAllTests();
                    break;
                case "12":
                    MainMenuApp.startApp();
                    break;
                case "13":
                    println(Translator.translate(UIMessage.AUTH_GOODBYE));
                    return;
                default:
                    println(Translator.translate(UIMessage.MSG_INVALID_CHOICE));
            }
        }
    }

    private static void printMenu() {
        print("\n Welcome, " + AppSettings.getActiveUser().getFullName() + "!"
        + "\n(" + services.userService.getDTO(AppSettings.getActiveUser()).toShortString() + ")");
        println("\n|||  University System |||");
        println("1. User App");
        println("2. Course App");
        println("3. Enrollment App");
        println("4. Message App");
        println("5. Tech Request App");
        println("6. Teacher Complaint App");
        println("7. Student Org. App");
        println("8. News App");
        println("9. Research App");
        println("10. " + Translator.translate(UIMessage.MENU_TITLE_ADMIN));
        println("11. Run tests");
        println("12. Main Menu App (auth + role-based)");
        println("13. " + Translator.translate(UIMessage.MENU_EXIT));
    }

    private static void askLanguage() {
        println("\n|||  Choose preferable language |||");
        println("1. English language");
        println("2. Қазақ тілі");
        println("3. Русский язык");

        while (true) {
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
                    println(Translator.translate(UIMessage.MSG_INVALID_CHOICE));
                    break;
            }
        }
    }
}
