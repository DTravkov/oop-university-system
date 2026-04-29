package application;

import java.util.Scanner;

import exceptions.ApplicationException;
import model.domain.ResearcherProfile;
import model.factories.ServiceFactory;
import model.enumeration.UIMessages;
import services.LanguageService;
import services.ResearchService;
import services.UserService;
import utils.UIFields;

public class ResearchApp {
    private static final ServiceFactory serviceFactory = ServiceFactory.getInstance();
    private static final ResearchService researchService = serviceFactory.getService(ResearchService.class);
    private static final UserService userService = serviceFactory.getService(UserService.class);

    public static void startApp(Scanner scanner) {
        while (true) {
            printMenu();
            String choice = UIFields.readChoice(scanner, UIMessages.MENU_CHOOSE, 1, 6);

            try {
                switch (choice) {
                    case "1":
                        printAllResearchersBasicAccounts();
                        break;
                    case "2":
                        printAllResearcherProfiles();
                        break;
                    case "3":
                        checkIsResearcher(scanner);
                        break;
                    case "4":
                        makeResearcher(scanner);
                        break;
                    case "5":
                        deleteResearcherProfile(scanner);
                        break;
                    case "6":
                        return;
                    default:
                        System.out.println(LanguageService.translate(UIMessages.MSG_INVALID_CHOICE));
                }
            } catch (ApplicationException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n--- Research App ---");
        System.out.println("1. Get all researchers basic accounts");
        System.out.println("2. Get all researcher profiles");
        System.out.println("3. Check if user is researcher by id");
        System.out.println("4. Make person a researcher");
        System.out.println("5. Delete researcher profile");
        System.out.println("6. " + LanguageService.translate(UIMessages.MENU_EXIT));
    }

    private static void printAllResearchersBasicAccounts() {
        System.out.println(researchService.getAllResearchersBasicAccounts());
    }

    private static void printAllResearcherProfiles() {
        System.out.println(researchService.getAllResearcherProfiles());
    }

    private static void checkIsResearcher(Scanner scanner) {
        int userId = UIFields.readInt(scanner, UIMessages.INPUT_STUDENT_ID);
        System.out.println(researchService.isResearcher(userId));
    }

    private static void makeResearcher(Scanner scanner) {
        System.out.println("Available user IDs: " +
                userService.getAll().stream().map(user -> user.getId()).toList());
        int userId = UIFields.readInt(scanner, UIMessages.INPUT_STUDENT_ID);
        ResearcherProfile profile = researchService.makeResearcher(userId);
        System.out.println(LanguageService.translate(UIMessages.MSG_CREATED));
        System.out.println("User account: " + userService.get(userId));
        System.out.println("Researcher profile: " + profile);
    }

    private static void deleteResearcherProfile(Scanner scanner) {
        int userId = UIFields.readInt(scanner, UIMessages.INPUT_STUDENT_ID);
        researchService.deleteResearcherProfile(userId);
        System.out.println(LanguageService.translate(UIMessages.MSG_DELETED));
    }
}
