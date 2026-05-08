package application;

import java.util.ArrayList;
import java.util.List;

import exceptions.ApplicationException;
import model.domain.*;
import model.enumeration.NewsUrgencyLevel;
import model.enumeration.UIMessage;
import services.CommentService;
import services.NewsService;
import services.UserService;
import settings.AppSettings;
import utils.Translator;
import utils.UIForms;

public final class NewsApp extends BaseApp {

    private static final NewsService newsService = services.newsService;
    private static final CommentService commentService = services.commentService;
    private static final UserService userService = services.userService;

    private NewsApp() {
    }

    public static void startApp() {
        User activeUser = AppSettings.getActiveUser();
        boolean isManager = activeUser instanceof Manager;
        while (true) {
            List<MenuAction> actions = buildNewsMenuActions(isManager);
            showMenu("News", actions);
            String choice = readChoice(UIMessage.MENU_CHOOSE, 1, actions.size());

            try {
                MenuAction selectedAction = actions.get(Integer.parseInt(choice) - 1);
                selectedAction.execute();
                if (selectedAction.shouldExitAfterRun()) {
                    return;
                }
            } catch (ApplicationException e) {
                printExceptionDetails(e);
            }
        }
    }

    private static List<MenuAction> buildNewsMenuActions(boolean isManager) {
        List<MenuAction> actions = new ArrayList<>();
        actions.add(new MenuAction("View all news", NewsApp::runViewNewsMenu));
        if (isManager) {
            actions.add(new MenuAction("Create news", NewsApp::createNews));
            actions.add(new MenuAction("Delete news", NewsApp::deleteNews));
        }
        actions.add(MenuAction.exit(Translator.translate(UIMessage.MENU_EXIT)));
        return actions;
    }

    private static void showMenu(String title, List<MenuAction> actions) {
        println("\n|||  " + title + " |||");
        for (int i = 0; i < actions.size(); i++) {
            println((i + 1) + ". " + actions.get(i).getTitle());
        }
    }

    private static void runViewNewsMenu() {
        while (true) {
            List<MenuAction> actions = buildViewNewsActions();
            showMenu("View News", actions);
            String choice = readChoice(UIMessage.MENU_CHOOSE, 1, actions.size());
            MenuAction selectedAction = actions.get(Integer.parseInt(choice) - 1);
            selectedAction.execute();
            if (selectedAction.shouldExitAfterRun()) {
                return;
            }
        }
    }

    private static List<MenuAction> buildViewNewsActions() {
        List<MenuAction> actions = new ArrayList<>();
        actions.add(new MenuAction("View all news", NewsApp::showAllNews));
        actions.add(new MenuAction("Open news by id", NewsApp::showNewsDetailsAndHandleComment));
        actions.add(MenuAction.exit("Back"));
        return actions;
    }

    private static void showNewsDetailsAndHandleComment() {
        showAllNews();
        int newsId = UIForms.readInt(scanner, UIMessage.INPUT_MESSAGE_ID);
        println(newsService.getDTO(newsId).toString());
        println("1. Leave comment");
        println("2. Back");
        String choice = readChoice(UIMessage.MENU_CHOOSE, 1, 2);
        if ("1".equals(choice)) {
            createComment(newsId);
        }
    }

    private static void createNews() {
        showManagers();
        int publisherId = UIForms.readInt(scanner, UIMessage.INPUT_SENDER_ID);
        String title = UIForms.readNonEmpty(scanner, UIMessage.INPUT_NAME);
        String content = UIForms.readNonEmpty(scanner, UIMessage.INPUT_MESSAGE_CONTENT);
        NewsUrgencyLevel urgencyLevel = UIForms.readNewsUrgencyLevel(scanner);

        News news = new News(publisherId, title, content, urgencyLevel);
        newsService.postNews(news);
        println(Translator.translate(UIMessage.MSG_CREATED));
        println(newsService.getDTO(news.getId()));
    }

    private static void deleteNews() {
        showAllNews();
        int newsId = UIForms.readInt(scanner, UIMessage.INPUT_MESSAGE_ID);
        newsService.delete(newsId);
        println(Translator.translate(UIMessage.MSG_DELETED));
    }

    private static void createComment(int newsId) {
        int senderId = AppSettings.getActiveUser().getId();
        String content = UIForms.readNonEmpty(scanner, UIMessage.INPUT_MESSAGE_CONTENT);

        Comment comment = commentService.create(new Comment(senderId, content));
        newsService.assignComment(newsId, comment.getId());

        println(Translator.translate(UIMessage.MSG_CREATED));
        println(newsService.getDTO(newsId));
    }

    private static void showAllNews() {
        for (News news : newsService.getAll()) {
            println(newsService.getDTO(news).toShortString());
        }
    }

    private static void showManagers() {
        println("|||  Managers |||");
        for (User user : userService.getAllByClass(Manager.class)) {
            println(userService.getDTO(user).toShortString());
        }
    }

}
