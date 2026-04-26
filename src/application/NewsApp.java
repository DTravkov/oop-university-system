package application;

import java.util.Scanner;

import exceptions.ApplicationException;
import model.domain.*;
import model.enumeration.NewsUrgencyLevel;
import model.enumeration.UIMessages;
import model.factories.ServiceFactory;
import services.*;
import utils.UIFields;

public class NewsApp {

    private static final ServiceFactory serviceFactory = ServiceFactory.getInstance();
    private static final NewsService newsService = serviceFactory.getService(NewsService.class);
    private static final CommentService commentService = serviceFactory.getService(CommentService.class);
    private static final UserService userService = serviceFactory.getService(UserService.class);

    public static void startApp(Scanner scanner) {
        while (true) {
            printMenu();
            String choice = UIFields.readChoice(scanner, UIMessages.MENU_CHOOSE, 1, 8);

            try {
                switch (choice) {
                    case "1":
                        addNews(scanner);
                        break;
                    case "2":
                        deleteNews(scanner);
                        break;
                    case "3":
                        addComment(scanner);
                        break;
                    case "4":
                        deleteComment(scanner);
                        break;
                    case "5":
                        getAllNews();
                        break;
                    case "6":
                        getAllNewsByUrgency(scanner);
                        break;
                    case "7":
                        getNewsComments(scanner);
                        break;
                    case "8":
                        return;
                    default:
                        System.out.println(LanguageService.translate(UIMessages.MSG_INVALID_CHOICE));
                }
            } catch (ApplicationException e) {
                System.out.println(LanguageService.translate(e.getMessageKey(), e.getArgs()));
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n--- News ---");
        System.out.println("1. Add news");
        System.out.println("2. Delete news");
        System.out.println("3. Add comment");
        System.out.println("4. Delete comment");
        System.out.println("5. " + LanguageService.translate(UIMessages.MENU_VIEW_ALL));
        System.out.println("6. Get news by urgency level");
        System.out.println("7. Get news comments");
        System.out.println("8. " + LanguageService.translate(UIMessages.MENU_EXIT));
    }

    private static void addNews(Scanner scanner) {
        printManagers();
        int publisherId = UIFields.readInt(scanner, UIMessages.INPUT_SENDER_ID);
        String title = UIFields.readNonEmpty(scanner, UIMessages.INPUT_NAME);
        String content = UIFields.readNonEmpty(scanner, UIMessages.INPUT_MESSAGE_CONTENT);
        NewsUrgencyLevel urgencyLevel = askUrgencyLevel(scanner);

        News news = new News(publisherId, title, content, urgencyLevel);
        newsService.postNews(news);
        System.out.println(LanguageService.translate(UIMessages.MSG_CREATED));
        System.out.println(news);
    }

    private static void deleteNews(Scanner scanner) {
        getAllNews();
        int newsId = UIFields.readInt(scanner, UIMessages.INPUT_MESSAGE_ID);
        newsService.delete(newsId);
        System.out.println(LanguageService.translate(UIMessages.MSG_DELETED));
    }

    private static void addComment(Scanner scanner) {
        getAllNews();
        int newsId = UIFields.readInt(scanner, UIMessages.INPUT_MESSAGE_ID);
        int senderId = UIFields.readInt(scanner, UIMessages.INPUT_SENDER_ID);
        String content = UIFields.readNonEmpty(scanner, UIMessages.INPUT_MESSAGE_CONTENT);

        Comment comment = commentService.create(new Comment(senderId, content));
        newsService.assignComment(newsId, comment.getId());

        System.out.println(LanguageService.translate(UIMessages.MSG_CREATED));
        System.out.println(newsService.get(newsId));
    }

    private static void deleteComment(Scanner scanner) {
        printAllCommentIds();
        int commentId = UIFields.readInt(scanner, UIMessages.INPUT_MESSAGE_ID);
        commentService.delete(commentId);
        System.out.println(LanguageService.translate(UIMessages.MSG_DELETED));
    }

    private static void getAllNews() {
        System.out.println(newsService.getAll());
    }

    private static void getAllNewsByUrgency(Scanner scanner) {
        NewsUrgencyLevel urgencyLevel = askUrgencyLevel(scanner);
        System.out.println(newsService.getAllByUrgency(urgencyLevel));
    }

    private static void getNewsComments(Scanner scanner) {
        getAllNews();
        int newsId = UIFields.readInt(scanner, UIMessages.INPUT_MESSAGE_ID);
        News news = newsService.get(newsId);
        System.out.println("--- Comments ---");
        for (Integer commentId : news.getComments()) {
            System.out.println(commentService.get(commentId));
        }
    }

    private static NewsUrgencyLevel askUrgencyLevel(Scanner scanner) {
        while (true) {
            System.out.println("Choose urgency level:");
            System.out.println("1. RESEARCH");
            System.out.println("2. HIGH");
            System.out.println("3. AVERAGE");
            System.out.println("4. LOW");
            String choice = UIFields.readChoice(scanner, UIMessages.MENU_CHOOSE, 1, 4);
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
                    System.out.println(LanguageService.translate(UIMessages.MSG_INVALID_CHOICE));
            }
        }
    }

    private static void printManagers() {
        System.out.println("--- Managers ---");
        for (User user : userService.getAllByClass(Manager.class)) {
            System.out.println("ID: " + user.getId() + ", Name: " + user.getName() + ", Surname: " + user.getSurname());
        }
    }

    private static void printAllCommentIds() {
        System.out.println("--- Comment IDs by News ---");
        for (News news : newsService.getAll()) {
            System.out.println("News ID: " + news.getId() + ", comments: " + news.getComments());
        }
    }

}
