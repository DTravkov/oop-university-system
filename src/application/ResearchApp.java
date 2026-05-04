package application;

import exceptions.ApplicationException;
import model.domain.ResearcherProfile;
import model.domain.User;
import model.enumeration.UIMessage;
import services.ResearchService;
import services.UserService;
import utils.Translator;
import utils.UIForms;

public final class ResearchApp extends BaseApp {

    private static final ResearchService researchService = services.researchService;
    private static final UserService userService = services.userService;

    private ResearchApp() {
    }

    public static void startApp() {
        while (true) {
            printMenu();
            String choice = readChoice(UIMessage.MENU_CHOOSE, 1, 6);

            try {
                switch (choice) {
                    case "1":
                        printAllResearchersBasicAccounts();
                        break;
                    case "2":
                        printAllResearcherProfiles();
                        break;
                    case "3":
                        checkIsResearcher();
                        break;
                    case "4":
                        makeResearcher();
                        break;
                    case "5":
                        deleteResearcherProfile();
                        break;
                    case "6":
                        return;
                    default:
                        printInvalidChoice();
                }
            } catch (ApplicationException e) {
                printExceptionDetails(e);
            }
        }
    }

    private static void printMenu() {
        println("\n--- Research App ---");
        println("1. Get all researchers basic accounts");
        println("2. Get all researcher profiles");
        println("3. Check if user is researcher by id");
        println("4. Make person a researcher");
        println("5. Delete researcher profile");
        println("6. " + Translator.translate(UIMessage.MENU_EXIT));
    }

    private static void printAllResearchersBasicAccounts() {
        for (User user : researchService.getAllResearchersBasicAccounts()) {
            println(userService.getDTO(user).toShortString());
        }
    }

    private static void printAllResearcherProfiles() {
        for (ResearcherProfile profile : researchService.getAllResearcherProfiles()) {
            println(researchService.getDTO(profile));
        }
    }

    private static void checkIsResearcher() {
        int userId = UIForms.readInt(scanner, UIMessage.INPUT_STUDENT_ID);
        println(researchService.isResearcher(userId));
    }

    private static void makeResearcher() {
        println("Available user IDs: " +
                userService.getAll().stream().map(user -> user.getId()).toList());
        int userId = UIForms.readInt(scanner, UIMessage.INPUT_STUDENT_ID);
        ResearcherProfile profile = researchService.makeResearcher(userId);
        println(Translator.translate(UIMessage.MSG_CREATED));
        println(userService.getDTO(userId));
        println(researchService.getDTO(profile));
    }

    private static void deleteResearcherProfile() {
        int userId = UIForms.readInt(scanner, UIMessage.INPUT_STUDENT_ID);
        researchService.deleteResearcherProfile(userId);
        println(Translator.translate(UIMessage.MSG_DELETED));
    }
}
