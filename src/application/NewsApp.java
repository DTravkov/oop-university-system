package application;

import java.util.List;

import model.domain.Comment;
import model.domain.News;
import model.dto.NewsDTO;
import model.enumeration.UIMessage;
import services.CommentService;
import services.NewsService;
import utils.UIForms;

public final class NewsApp extends BaseApp {

    private static final NewsService newsService = services.newsService;
    private static final CommentService commentService = services.commentService;

    private static Integer lastViewedNewsId = null;

    private NewsApp() {
    }

    public static void startApp() {
        ActionMenu menu = new ActionMenu("News Menu");
        menu.addAction("View all news", () -> handleExceptions(NewsApp::showAllNews));
        menu.addAction("View news details", () -> handleExceptions(NewsApp::showNewsDetails));
        menu.addAction("Leave comment", () -> handleExceptions(NewsApp::leaveComment));
        menu.addAction("Exit", menu::stop);
        menu.start();
    }

    private static void showAllNews() {
        List<News> allNews = newsService.getAll();
        if (allNews.isEmpty()) {
            println("No news found.");
            return;
        }
        println("\nAll news:");
        allNews.stream()
                .map(newsService::getDTO)
                .map(NewsDTO::toShortString)
                .forEach(BaseApp::println);
    }

    private static void showNewsDetails() {
        int newsId = UIForms.readInt(scanner, UIMessage.INPUT_NEWS_ID);
        NewsDTO news = newsService.getDTO(newsId);
        println(news);
        lastViewedNewsId = newsId;
    }

    private static void leaveComment() {
        if (lastViewedNewsId == null) {
            printFail("Open news details first, then leave a comment.");
            return;
        }
        String content = UIForms.readNonEmpty(scanner, UIMessage.INPUT_MESSAGE_CONTENT);
        Comment createdComment = commentService.create(new Comment(getActiveUser().getId(), content));
        newsService.assignComment(lastViewedNewsId, createdComment.getId());
        printSuccess("Comment added.");
    }
}
