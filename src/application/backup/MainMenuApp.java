package application;

import java.util.ArrayList;
import java.util.List;

import exceptions.ApplicationException;
import model.domain.Employee;
import model.domain.User;
import model.enumeration.UIMessage;
import services.ResearchService;
import services.UserService;
import settings.AppSettings;
import utils.Translator;
import utils.UIForms;

public final class MainMenuApp extends BaseApp {

    private static final UserService userService = services.userService;
    private static final ResearchService researchService = services.researchService;

    private MainMenuApp() {
    }

    public static void startApp() {
        while (true) {
            if (isAnonymousSession()) {
                if (!handleAuthentication()) {
                    return;
                }
                continue;
            }

            List<MenuAction> actions = buildMainMenuActions(AppSettings.getActiveUser());
            showMainMenu(actions);
            String choice = readChoice(UIMessage.MENU_CHOOSE, 1, actions.size());
            MenuAction selectedAction = actions.get(Integer.parseInt(choice) - 1);
            selectedAction.execute();
        }
    }

    private static boolean isAnonymousSession() {
        return AppSettings.getActiveUser().getId() == AppSettings.ANONYMOUS_USER_ID;
    }

    private static boolean handleAuthentication() {
        println("\n|||  Authentication |||");
        println("1. " + Translator.translate(UIMessage.AUTH_LOG_IN));
        println("2. " + Translator.translate(UIMessage.MENU_EXIT));

        String choice = readChoice(UIMessage.MENU_CHOOSE, 1, 2);

        if (choice.equals("2")) {
            println(Translator.translate(UIMessage.AUTH_GOODBYE));
            return false;
        }

        String login = UIForms.readNonEmpty(scanner, UIMessage.INPUT_LOGIN);
        String password = UIForms.readNonEmpty(scanner, UIMessage.INPUT_PASSWORD);

        try {
            User authenticated = userService.authenticate(login, password);
            if (authenticated.isBanned()) {
                printFail("Account is banned. Contact administrator.");
                logout();
                return true;
            }
            printSuccess(authenticated.getFullName());
            return true;
        } catch (ApplicationException e) {
            printExceptionDetails(e);
            return true;
        }
    }

    private static List<MenuAction> buildMainMenuActions(User activeUser) {
        List<MenuAction> actions = new ArrayList<>();

        actions.add(new MenuAction("My profile", MainMenuApp::showProfile));
        actions.add(new MenuAction("Messages", MessageApp::startApp));
        actions.add(new MenuAction("News", NewsApp::startApp));
        actions.add(new MenuAction("Student organizations", StudentOrganizationApp::startApp));
        if (activeUser instanceof Employee) {
            actions.add(new MenuAction("Tech requests", TechRequestApp::startApp));
        }
        actions.add(new MenuAction("Researcher menu", ResearchApp::startApp));

        actions.add(new MenuAction("Log out", MainMenuApp::logout));
        actions.add(new MenuAction(Translator.translate(UIMessage.MENU_EXIT), MainMenuApp::exitSystem));
        return actions;
    }

    private static void showMainMenu(List<MenuAction> actions) {
        User activeUser = AppSettings.getActiveUser();
        println("\n|||  Main Menu |||");
        println("User: " + activeUser.getFullName() + " (" + activeUser.getClass().getSimpleName() + ")");
        for (int i = 0; i < actions.size(); i++) {
            println((i + 1) + ". " + actions.get(i).getTitle());
        }
    }

    private static void logout() {
        AppSettings.clearActiveUser();
        println(Translator.translate(UIMessage.SUCCESS) + " Logged out");
    }

    private static void showProfile() {
        User activeUser = AppSettings.getActiveUser();
        println(userService.getDTO(activeUser));
        if (researchService.isResearcher(activeUser.getId())) {
            println("|||  Researcher info |||");
            println("H-index: " + researchService.calculateHIndex(activeUser.getId()));
            println("Projects count: " + researchService.getResearcherProjects(activeUser.getId()).size());
            println("Papers count: " + researchService.getResearcherPapers(activeUser.getId()).size());
        }
    }

    private static void exitSystem() {
        println(Translator.translate(UIMessage.AUTH_GOODBYE));
        scanner.close();
        System.exit(0);
    }

}
