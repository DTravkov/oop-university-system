package application;

import exceptions.ApplicationException;
import model.domain.*;
import model.enumeration.NewsUrgencyLevel;
import model.enumeration.UIMessage;
import services.CommentService;
import services.NewsService;
import services.UserService;
import utils.Translator;
import utils.UIForms;

public final class NewsApp extends BaseApp {

    private static final NewsService newsService = services.newsService;
    private static final CommentService commentService = services.commentService;
    private static final UserService userService = services.userService;

    private NewsApp() {
    }

    public static void startApp() {
        while (true) {
            printMenu();
            String choice = readChoice(UIMessage.MENU_CHOOSE, 1, 8);

            try {
                switch (choice) {
                    case "1":
                        addNews();
                        break;
                    case "2":
                        deleteNews();
                        break;
                    case "3":
                        addComment();
                        break;
                    case "4":
                        deleteComment();
                        break;
                    case "5":
                        printAllNews();
                        break;
                    case "6":
                        printAllNewsByUrgency();
                        break;
                    case "7":
                        printNewsComments();
                        break;
                    case "8":
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
        println("\n|||  News |||");
        println("1. Add news");
        println("2. Delete news");
        println("3. Add comment");
        println("4. Delete comment");
        println("5. " + Translator.translate(UIMessage.MENU_VIEW_ALL));
        println("6. Get news by urgency level");
        println("7. Get news comments");
        println("8. " + Translator.translate(UIMessage.MENU_EXIT));
    }

    private static void addNews() {
        printManagers();
        int publisherId = UIForms.readInt(scanner, UIMessage.INPUT_SENDER_ID);
        String title = UIForms.readNonEmpty(scanner, UIMessage.INPUT_NAME);
        String content = UIForms.readNonEmpty(scanner, UIMessage.INPUT_MESSAGE_CONTENT);
        NewsUrgencyLevel urgencyLevel = askUrgencyLevel();

        News news = new News(publisherId, title, content, urgencyLevel);
        newsService.postNews(news);
        println(Translator.translate(UIMessage.MSG_CREATED));
        println(newsService.getDTO(news.getId()));
    }

    private static void deleteNews() {
        printAllNews();
        int newsId = UIForms.readInt(scanner, UIMessage.INPUT_MESSAGE_ID);
        newsService.delete(newsId);
        println(Translator.translate(UIMessage.MSG_DELETED));
    }

    private static void addComment() {
        printAllNews();
        int newsId = UIForms.readInt(scanner, UIMessage.INPUT_MESSAGE_ID);
        int senderId = UIForms.readInt(scanner, UIMessage.INPUT_SENDER_ID);
        String content = UIForms.readNonEmpty(scanner, UIMessage.INPUT_MESSAGE_CONTENT);

        Comment comment = commentService.create(new Comment(senderId, content));
        newsService.assignComment(newsId, comment.getId());

        println(Translator.translate(UIMessage.MSG_CREATED));
        println(newsService.getDTO(newsId));
    }

    private static void deleteComment() {
        printAllCommentIds();
        int commentId = UIForms.readInt(scanner, UIMessage.INPUT_MESSAGE_ID);
        commentService.delete(commentId);
        println(Translator.translate(UIMessage.MSG_DELETED));
    }

    private static void printAllNews() {
        for (News news : newsService.getAll()) {
            println(newsService.getDTO(news).toShortString());
        }
    }

    private static void printAllNewsByUrgency() {
        NewsUrgencyLevel urgencyLevel = askUrgencyLevel();
        for (News news : newsService.getAllByUrgency(urgencyLevel)) {
            println(newsService.getDTO(news));
        }
    }

    private static void printNewsComments() {
        printAllNews();
        int newsId = UIForms.readInt(scanner, UIMessage.INPUT_MESSAGE_ID);
        News news = newsService.get(newsId);
        println("|||  Comments |||");
        for (Integer commentId : news.getComments()) {
            println(commentService.getDTO(commentId));
        }
    }

    private static NewsUrgencyLevel askUrgencyLevel() {
        while (true) {
            println("Choose urgency level:");
            println("1. RESEARCH");
            println("2. HIGH");
            println("3. AVERAGE");
            println("4. LOW");
            String choice = readChoice(UIMessage.MENU_CHOOSE, 1, 4);
            switch (choice) {
                case "1":
                    return NewsUrgencyLevel.RESEARCH;
                case "2":
                    return NewsUrgencyLevel.HIGH;
                case "3":
                    return NewsUrgencyLevel.AVERAGE;
                case "4":
                    return NewsUrgencyLevel.LOW;
                default:
                    printInvalidChoice();
            }
        }
    }

    private static void printManagers() {
        println("|||  Managers |||");
        for (User user : userService.getAllByClass(Manager.class)) {
            println(userService.getDTO(user).toShortString());
        }
    }

    private static void printAllCommentIds() {
        println("|||  Comment IDs by News |||");
        for (News news : newsService.getAll()) {
            println("ID: " + news.getId() + " | Comment ids: " + news.getComments());
            for (Integer commentId : news.getComments()) {
                println(commentService.getDTO(commentId));
            }
        }
    }
}
